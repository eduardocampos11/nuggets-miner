package br.com.inf.nuggets.miner.solr;

public class NotFoundPPAException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotFoundPPAException() {
		super("NotFoundPPAException: Not found ppa for entered crowd bug.");
	}
}
