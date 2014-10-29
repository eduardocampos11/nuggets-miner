package br.com.inf.nuggets.miner.solr;

public class CompilationUnitErrorException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompilationUnitErrorException() {
		super("CompilationUnitError: Failed to generate compilation unit for snippet.");
	}
	
}
