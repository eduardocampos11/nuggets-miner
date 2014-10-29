package br.com.inf.nuggets.miner.solr;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class DocumentRetrieveJob
  extends Job
{
  private final List<String> ids;
  private final List<IQueryResultReadyHandler> handlers;
  private final CommonsHttpSolrServer server;
  
  public DocumentRetrieveJob(CommonsHttpSolrServer server, List<String> ids, IQueryResultReadyHandler... handlers)
  {
    super("Retrieving Documents");
    this.ids = ids;
    this.handlers = Arrays.asList(handlers);
    this.server = server;
  }
  
  private void notifyResult(Map<String, DocumentBase> result)
  {
    for (IQueryResultReadyHandler h : this.handlers) {
//      h.onSuccess(result);
    }
  }
  
  private void notifyError(Exception e)
  {
    for (IQueryResultReadyHandler h : this.handlers) {
      h.onError(e);
    }
  }
  
  private String prepareQueryString()
  {
    StringBuilder sb = new StringBuilder();
    if (this.ids.size() <= 0) {
      return "";
    }
    sb.append("id:\"" + (String)this.ids.get(0) + "\"");
    for (int i = 1; i < this.ids.size(); i++) {
      sb.append(" OR id:\"" + (String)this.ids.get(i) + "\"");
    }
    return sb.toString();
  }
  
  private Map<String, DocumentBase> doQuery()
    throws SolrServerException, IOException
  {
    SolrQuery query = new SolrQuery();
    query.setQuery(prepareQueryString())
      .setSortField("id", SolrQuery.ORDER.desc)
      .setRows(Integer.valueOf(this.ids.size()));
    
    QueryResponse resp = this.server.query(query, SolrRequest.METHOD.POST);
    SolrDocumentList resultList = resp.getResults();
    HashMap<String, DocumentBase> docList = new HashMap();
    DocumentCache cache = DocumentCache.getInstance();
    for (SolrDocument d : resultList)
    {
      DocumentBase doc = new DocumentDeserializer().deserialize((String)d.getFieldValue("document"));
      
      docList.put(doc.getId(), doc);
//      cache.storeDocument(doc);
    }
    return docList;
  }
  
  public IStatus run(IProgressMonitor monitor)
  {
    try
    {
      monitor.beginTask("", -1);
      Map<String, DocumentBase> result = doQuery();
      notifyResult(result);
      return Status.OK_STATUS;
    }
    catch (Exception e)
    {
      notifyError(e);
      return new Status(4, "Seahawk", e.getMessage());
    }
  }
}
