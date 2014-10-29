package br.com.inf.nuggets.miner.solr;

import java.util.Map;

public interface IQuerySnippetReadyHandler {
	
	public abstract void onSuccess(Map<String, SnippetData> paramMap);
	  
	public abstract void onError(Exception paramException);
}
