package br.com.inf.nuggets.miner.utils;

import java.util.Comparator;

import br.com.inf.nuggets.miner.solr.IssueData;


public class ScoreSolrComparatorForIssues implements Comparator<IssueData> {
	
	@Override
	public int compare(IssueData i1, IssueData i2) {
		return i1.getScoreSolr() < i2.getScoreSolr() ? 1 : i1.getScoreSolr() > i2.getScoreSolr() ? -1 : 0;
	}
}
