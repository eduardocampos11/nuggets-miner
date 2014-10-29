package br.com.inf.nuggets.miner.utils;

import java.util.Comparator;

import br.com.inf.nuggets.miner.solr.DocumentBase;


public class ScoreFinalComparator implements Comparator<DocumentBase> {
	
	@Override
	public int compare(DocumentBase d1, DocumentBase d2) {
		return d1.getScoreFinal() < d2.getScoreFinal() ? 1 : d1.getScoreFinal() > d2.getScoreFinal() ? -1 : 0;
	}
}
