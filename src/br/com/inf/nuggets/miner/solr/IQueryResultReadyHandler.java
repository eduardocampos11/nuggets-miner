package br.com.inf.nuggets.miner.solr;

import java.util.Map;

public abstract interface IQueryResultReadyHandler
{
  public abstract void onSuccess(Map<String, DocumentBase> paramMap);
  
  public abstract void onError(Exception paramException);
}
