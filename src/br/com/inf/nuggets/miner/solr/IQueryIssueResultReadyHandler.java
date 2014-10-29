package br.com.inf.nuggets.miner.solr;

import java.util.Map;

public interface IQueryIssueResultReadyHandler {

	 public abstract void onSuccess(Map<String, IssueData> paramMap);
	  
	 public abstract void onError(Exception paramException);
}
