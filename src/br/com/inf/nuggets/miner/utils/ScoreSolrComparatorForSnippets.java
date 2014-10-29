package br.com.inf.nuggets.miner.utils;

import java.util.Comparator;

import br.com.inf.nuggets.miner.solr.SnippetData;

public class ScoreSolrComparatorForSnippets implements Comparator<SnippetData> {
	
	@Override
	public int compare(SnippetData s1, SnippetData s2) {
		return s1.getScoreSolr() < s2.getScoreSolr() ? 1 : s1.getScoreSolr() > s2.getScoreSolr() ? -1 : 0;
	}
}

