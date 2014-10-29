package br.com.inf.nuggets.miner.solr;

public class SnippetData {

	private String id;
	private String postTitle;
	private Double scoreSolr;
	private String type;
	private String codeText;
	private String tags;
	private String language;
	
	public SnippetData() {}
	
	public SnippetData(String id, String postTitle, Double scoreSolr,
			String type, String codeText, String tags, String language) {
		super();
		this.id = id;
		this.postTitle = postTitle;
		this.scoreSolr = scoreSolr;
		this.type = type;
		this.codeText = codeText;
		this.tags = tags;
		this.language = language;
	}

	public String getPostTitle() {
		return postTitle;
	}
	
	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}
	
	public Double getScoreSolr() {
		return scoreSolr;
	}
	public void setScoreSolr(Double scoreSolr) {
		this.scoreSolr = scoreSolr;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCodeText() {
		return codeText;
	}
	public void setCodeText(String codeText) {
		this.codeText = codeText;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	@Override
	public String toString() {
		return "SnippetData [id=" + id + ", type=" + type + ", codeText="
				+ codeText + ", tags=" + tags + ", language=" + language + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codeText == null) ? 0 : codeText.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SnippetData other = (SnippetData) obj;
		if (codeText == null) {
			if (other.codeText != null)
				return false;
		} else if (!codeText.equals(other.codeText))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
