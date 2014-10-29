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

public class SnippetQueryExecuter
  extends Job
{
  private final List<IQuerySnippetReadyHandler> handlers;
  private final CommonsHttpSolrServer server;
  private String queryStr;
  private String language;
  
  public SnippetQueryExecuter(CommonsHttpSolrServer server, String query, String language, IQuerySnippetReadyHandler... handlers)
  {
    super("SnippetQueryExecuter execution (Solr)");
    this.queryStr = query;
    this.language = language;
    this.handlers = new ArrayList<IQuerySnippetReadyHandler>();
    Collections.addAll(this.handlers, handlers);
    this.server = server;
  }
  
  private void notifyResult(Map<String, SnippetData> result)
  {
    for (IQuerySnippetReadyHandler h : this.handlers) {
      h.onSuccess(result);
    }
  }
  
  private void notifyError(Exception e)
  {
    for (IQuerySnippetReadyHandler h : this.handlers) {
      h.onError(e);
    }
  }
  
  private HashMap<String, SnippetData> doQuery()
    throws IOException, SolrServerException
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    int resultSetSize = store.getInt(NuggetsMinerPreferencesConstant.SOLR_RESULTS_SIZE);
    
    /*@SuppressWarnings("serial")
	List<String> keywords = new ArrayList<String>() {{
		add("unexpect");
		add("unexpected");
    	add("incorrect");
    	add("incorrected");
    	add("wrong");
    	add("output");
    	add("return");
    	add("returns");
    	add("result");
    	add("results");
    	add("behavior");
    	add("behaviour");
    	add("weird");
    	add("strange");
    	add("odd");
    	add("problem");
    	add("problems");
    }};
    
    String query = "(";
    int count = 0;
    for (String keyword : keywords) {
    	if (count <= 0) {
    		query = query + "(postTitle:" + keyword + ")";
    	} else {
    		query = query + " OR (postTitle:" + keyword + ")";
    	}
    	count++;
    }
    
    query = query + ") AND codeText:(" + this.queryStr+")"; */
    
    String query = "codeText:(" + this.queryStr+")";
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(query);
    solrQuery.setSortField("score", SolrQuery.ORDER.desc);
    solrQuery.setIncludeScore(true);
    solrQuery.setRows(Integer.valueOf(resultSetSize));

    System.out.println("query ==> " + query);
    
//    System.out.println("doQuery For Language: " + language);
    String filterQuery = "fq=language:"+ formatLanguage(language);
    
    final String testUrl = "http://localhost:8983/solr/collection1/select?"+ solrQuery.toString()+"&" + filterQuery + "&wt=json&indent=true";
//    System.out.println("url = " + testUrl);
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
	HashMap<String, SnippetData> snippetsMap = new HashMap<String, SnippetData>();
	
	SnippetData snippetData = null;
	List<Doc> docs = result.response.docs; 
	
	if (docs != null && !docs.isEmpty()) {
		for (Doc doc : docs) {
			ids.add(doc.id);
			snippetData = new SnippetData();
//			System.out.println("scoreSolr: " + doc.score + " snippetCode: " + doc.codeText + " id: " + doc.id);
			
			snippetData.setId(doc.id);
			snippetData.setPostTitle(doc.postTitle);
			snippetData.setScoreSolr(Double.valueOf(doc.score));
			snippetData.setCodeText(doc.codeText);
			snippetData.setLanguage(doc.language);
			snippetData.setTags(doc.tags);
			snippetData.setType(doc.type);
			
			snippetsMap.put(doc.id, snippetData);
		}
	}	
	return snippetsMap;
  }
 
  private static String formatLanguage(String language) {
	  String formattedLanguage = language.toLowerCase();
	  if (formattedLanguage.equals("c++")) {
		  formattedLanguage = "cplusplus";
	  } else {
		  
		  if (formattedLanguage.equals("c#")) {
			  formattedLanguage = "csharp";
		  }
		  
	  }
	  return formattedLanguage;
  }
  
  public IStatus run(IProgressMonitor monitor)
  {
    try
    {
      monitor.beginTask("", -1);
      HashMap<String, SnippetData> result = doQuery();
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
		public String postTitle;
		public String score;
		public String type;
		public String codeText;
		public String tags;
		public String language;
	}
}
