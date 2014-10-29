package br.com.inf.nuggets.miner.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;

import com.google.gson.Gson;

public class IssueQueryExecuter
  extends Job
{
  private final List<IQueryIssueResultReadyHandler> handlers;
  private final CommonsHttpSolrServer server;
  private String queryStr;
  
  public IssueQueryExecuter(CommonsHttpSolrServer server, String query, IQueryIssueResultReadyHandler... handlers)
  {
    super("IssueQueryExecuter execution (Solr)");
    this.queryStr = query;
    this.handlers = new ArrayList<IQueryIssueResultReadyHandler>();
    Collections.addAll(this.handlers, handlers);
    this.server = server;
  }
  
  private void notifyResult(Map<String, IssueData> result)
  {
    for (IQueryIssueResultReadyHandler h : this.handlers) {
      h.onSuccess(result);
    }
  }
  
  private void notifyError(Exception e)
  {
    for (IQueryIssueResultReadyHandler h : this.handlers) {
      h.onError(e);
    }
  }
  
  private HashMap<String, IssueData> doQuery()
    throws IOException, SolrServerException
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    int resultSetSize = store.getInt(NuggetsMinerPreferencesConstant.SOLR_RESULTS_SIZE);
    
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(this.queryStr);
    solrQuery.setSortField("score", SolrQuery.ORDER.desc);
    solrQuery.setIncludeScore(true);
    solrQuery.setRows(Integer.valueOf(resultSetSize));
    
    final String testUrl = "http://localhost:8983/solr/collection1/select?"+ solrQuery.toString()+"&fq=type:issue&wt=json&indent=true";
    System.out.println("url = " + testUrl);
	String out = null;
	try {
		out = new Scanner(new URL(testUrl).openStream(), "UTF-8").useDelimiter("\\A").next();
	} catch (MalformedURLException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	SolrResult result = new Gson().fromJson(out, SolrResult.class);
	List<String> ids = new ArrayList<String>();
	HashMap<String, IssueData> issueDataMap = new HashMap<String, IssueData>();
	
	IssueData issueData = null;
	for (Doc doc : result.response.docs) {
		ids.add(doc.id);
		issueData = new IssueData();
		System.out.println("scoreSolr: " + doc.score + " issueTitle: " + doc.issueTitle + " id: " + doc.id);
		
		issueData.setIssueId(Integer.valueOf(doc.id));
		issueData.setScoreSolr(Double.valueOf(doc.score));
		issueData.setIssueTitle(doc.issueTitle);
		issueData.setIssueBody(doc.issueBody);
		issueData.setIssueState(doc.issueState);
		issueData.setIssueUserLogin(doc.issueUserLogin);
		issueData.setPullRequestUrl(doc.pullRequestUrl);
		issueData.setCommitUrl(doc.commitUrl);
		issueData.setIssueCreationDate(doc.issueCreationDate);
		issueData.setIssueClosedDate(doc.issueClosedDate);
		
		issueDataMap.put(doc.id, issueData);
	}
	return issueDataMap;
  }
  
  public IStatus run(IProgressMonitor monitor)
  {
    try
    {
      monitor.beginTask("", -1);
      HashMap<String, IssueData> result = doQuery();
      notifyResult(result);
      return Status.OK_STATUS;
    }
    catch (Exception e)
    {
      notifyError(e);
      return new Status(4, "NuggetsMiner", e.getMessage());
    }
  }
  
  public class SolrResult {
	    public Response response;
	}

	class Response {
	    public int numFound;
	    public int start;
	    public List<Doc> docs;
	}

	class Doc {
		public String id;
		public String score;
		public String issueTitle;
		public String issueBody;
		public String issueState;
		public String pullRequestUrl;
		public String commitUrl;
		public String issueUserLogin;
		public String issueCreationDate;
		public String issueClosedDate;
	}
}