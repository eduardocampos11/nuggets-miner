package br.com.inf.nuggets.miner.solr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentCache
{
  private static DocumentCache instance;
  private final Map<String, DocumentBase> cache;
  private final Map<DocumentBase, Long> lruMap;
  private final int CACHE_SIZE = 1000;
  
  private DocumentCache()
  {
    this.cache = new HashMap<String, DocumentBase>();
    this.lruMap = new HashMap<DocumentBase, Long>();
  }
  
  public void storeDocument(DocumentBase document)
  {
    if (this.cache.size() >= 1000) {
      deleteLeastRecentlyUsed();
    }
    this.cache.put(document.getId(), document);
    this.lruMap.put(document, Long.valueOf(0L));
  }
  
  private void deleteLeastRecentlyUsed()
  {
    List<DocumentBase> docs = new ArrayList(this.lruMap.keySet());
    DocumentBase lruDoc = (DocumentBase)docs.get(0);
    long lruTime = ((Long)this.lruMap.get(lruDoc)).longValue();
    for (int i = 1; i < docs.size(); i++)
    {
      long docLru = ((Long)this.lruMap.get(docs.get(i))).longValue();
      if (docLru < lruTime)
      {
        lruTime = docLru;
        lruDoc = (DocumentBase)docs.get(i);
      }
    }
    this.lruMap.remove(lruDoc);
    this.cache.remove(lruDoc.getId());
  }
  
  public boolean isCached(String documentId)
  {
    return this.cache.containsKey(documentId);
  }
  
  public boolean isCached(DocumentBase document)
  {
    return this.cache.containsValue(document);
  }
  
  public DocumentBase getCachedDocument(String documentId)
  {
    if (!isCached(documentId)) {
      return null;
    }
    DocumentBase doc = (DocumentBase) this.cache.get(documentId);
    this.lruMap.put(doc, Long.valueOf(new Date().getTime()));
    return doc;
  }
  
  public static DocumentCache getInstance()
  {
    if (instance == null) {
      instance = new DocumentCache();
    }
    return instance;
  }
}
