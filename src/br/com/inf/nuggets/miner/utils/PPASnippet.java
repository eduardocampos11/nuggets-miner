package br.com.inf.nuggets.miner.utils;

public class PPASnippet {

	private String facts;
	
	public PPASnippet() {}

	public String getFacts() {
		return facts;
	}

	public void setFacts(String facts) {
		this.facts = facts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facts == null) ? 0 : facts.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PPASnippet other = (PPASnippet) obj;
		if (facts == null) {
			if (other.facts != null)
				return false;
		} else if (!facts.equals(other.facts))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SnippetPPA [facts=" + facts + "]";
	}
}
