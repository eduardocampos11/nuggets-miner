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

public class DocumentQueryExecuter
  extends Job
{
  private final List<IQueryResultReadyHandler> handlers;
  private final CommonsHttpSolrServer server;
  private String queryStr;
  
  public DocumentQueryExecuter(CommonsHttpSolrServer server, String query, IQueryResultReadyHandler... handlers)
  {
    super("Query execution (Solr)");
    this.queryStr = query;
    this.handlers = new ArrayList<IQueryResultReadyHandler>();
    Collections.addAll(this.handlers, handlers);
    this.server = server;
  }
  
  private void notifyResult(Map<String, DocumentBase> result)
  {
    for (IQueryResultReadyHandler h : this.handlers) {
      h.onSuccess(result);
    }
  }
  
  private void notifyError(Exception e)
  {
    for (IQueryResultReadyHandler h : this.handlers) {
      h.onError(e);
    }
  }
  
  private HashMap<String, DocumentBase> doQuery()
    throws IOException, SolrServerException
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    int resultSetSize = store.getInt(NuggetsMinerPreferencesConstant.SOLR_RESULTS_SIZE);
    
    String query = "post:(" + this.queryStr+")";
    
    SolrQuery solrQuery = new SolrQuery();
    solrQuery.setQuery(query);
    solrQuery.setSortField("score", SolrQuery.ORDER.desc);
    solrQuery.setIncludeScore(true);
    solrQuery.setRows(Integer.valueOf(resultSetSize));
    
    final String testUrl = "http://localhost:8983/solr/collection1/select?"+ solrQuery.toString()+"&fq=type:pair&wt=json&indent=true";
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
	HashMap<String, DocumentBase> docBaseMap = new HashMap<String, DocumentBase>();
	List<DocumentBase> docBaseLst = new ArrayList<DocumentBase>();
	
	DocumentBase documentBase = null;
	for (Doc doc : result.response.docs) {
//		System.out.println("score lucene: " + Double.valueOf(doc.score));		
		ids.add(doc.id);
		documentBase = new DocumentBase();
		documentBase.setId(doc.id);
		documentBase.setPost(doc.post);
		documentBase.setTitle(doc.title);
		documentBase.setQuestionBody(doc.questionBody);
		documentBase.setAnswerBody(doc.answerBody);
		documentBase.setScoreLucene(Double.valueOf(doc.score));
		documentBase.setScorePair(Double.valueOf(doc.scorePair));
		documentBase.setScoreQuestion(doc.scoreQuestion);
		documentBase.setScoreAnswer(doc.scoreAnswer);
		documentBase.setTags(doc.tags);
		documentBase.setQuestionCreationDate(doc.questionCreationDate);
		documentBase.setAnswerCreationDate(doc.answerCreationDate);
		documentBase.setQuestionOwnerReputation(doc.questionOwnerReputation);
		documentBase.setQuestionOwnerName(doc.questionOwnerName);
		documentBase.setAnswerOwnerReputation(doc.answerOwnerReputation);
		documentBase.setAnswerOwnerName(doc.answerOwnerName);

		docBaseMap.put(doc.id, documentBase);
		docBaseLst.add(documentBase);
	}
	
	if (docBaseLst != null && !docBaseLst.isEmpty()) {
		//Normalizando o score do Lucene
		int size = docBaseLst.size();
		Double max = docBaseLst.get(0).getScoreLucene();
		Double min = docBaseLst.get(size-1).getScoreLucene();
		System.out.println("maxScore: " + max + " minScore: " + min);
		Double normalizedValue = 0d;
		DocumentBase docBase = null;
		
		//Setando o score do Lucene normalizado nos pares pergunta-resposta
		for (String docId : docBaseMap.keySet()) {
			docBase = docBaseMap.get(docId);
			normalizedValue = normalize(docBase.getScoreLucene(), max, min);
			docBase.setScoreLucene(normalizedValue);
		}
		
		//Descobrindo o menor e o maior valor de "scorePair"
		max = Double.MIN_VALUE;
		min = Double.MAX_VALUE;
		Double scorePair = 0d;
		docBase = null;
		for (String docId : docBaseMap.keySet()) {
			docBase = docBaseMap.get(docId);
			scorePair = docBase.getScorePair();
			if (scorePair > max) {
				max = scorePair;
			}
			if (scorePair < min) {
				min = scorePair;
			}
		}
		
		//Normalizando o score do par pergunta-resposta e calculando o scoreFinal
		normalizedValue = 0d;
		docBase = null;
		Double scoreFinal = 0d;
		for (String docId : docBaseMap.keySet()) {
			//normaliza o scorePair e seta no docBase
			docBase = docBaseMap.get(docId);
			normalizedValue = normalize(docBase.getScorePair(), max, min);
			docBase.setScorePair(normalizedValue);
			
			//calcula o scoreFinal e seta no docBase
			scoreFinal = (docBase.getScorePair() + docBase.getScoreLucene()) / 2;
			docBase.setScoreFinal(scoreFinal);
		}
		return docBaseMap;
	}
	return new HashMap<String, DocumentBase>();
  }
  
  public IStatus run(IProgressMonitor monitor)
  {
    try
    {
      monitor.beginTask("", -1);
      HashMap<String, DocumentBase> result = doQuery();
      notifyResult(result);
      return Status.OK_STATUS;
    }
    catch (Exception e)
    {
      notifyError(e);
      return new Status(4, "Seahawk", e.getMessage());
    }
  }
  
  public Double normalize(Double value, Double max, Double min) {
	  Double normalizedValue = 0d;
	  normalizedValue += ((value - min) / (max - min));
	  return normalizedValue;
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
	    public String post;
	    public String title;
	    public String questionBody;
	    public String answerBody;
		public String score;
		public String scorePair;
		public String scoreQuestion;
		public String scoreAnswer;
	    public String tags;
	    public String questionCreationDate;
		public String answerCreationDate;
		public String questionOwnerReputation;
		public String questionOwnerName;
		public String answerOwnerReputation;
		public String answerOwnerName;
	}  
}
