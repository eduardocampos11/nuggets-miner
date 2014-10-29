package br.com.inf.nuggets.miner.solr;

public class IssueData {

	private Integer issueId;
	private Double scoreSolr;
	private String issueTitle;
	private String issueBody;
	private String issueState;
	private String pullRequestUrl;
	private String commitUrl;
	private String issueUserLogin;
	private String issueCreationDate;
	private String issueClosedDate;
	
	public Integer getIssueId() {
		return issueId;
	}
	public void setIssueId(Integer issueId) {
		this.issueId = issueId;
	}
	public Double getScoreSolr() {
		return scoreSolr;
	}
	public void setScoreSolr(Double scoreSolr) {
		this.scoreSolr = scoreSolr;
	}
	public String getIssueTitle() {
		return issueTitle;
	}
	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}
	public String getIssueBody() {
		return issueBody;
	}
	public void setIssueBody(String issueBody) {
		this.issueBody = issueBody;
	}
	public String getIssueState() {
		return issueState;
	}
	public void setIssueState(String issueState) {
		this.issueState = issueState;
	}
	public String getPullRequestUrl() {
		return pullRequestUrl;
	}
	public void setPullRequestUrl(String pullRequestUrl) {
		this.pullRequestUrl = pullRequestUrl;
	}
	public String getCommitUrl() {
		return commitUrl;
	}
	public void setCommitUrl(String commitUrl) {
		this.commitUrl = commitUrl;
	}
	public String getIssueUserLogin() {
		return issueUserLogin;
	}
	public void setIssueUserLogin(String issueUserLogin) {
		this.issueUserLogin = issueUserLogin;
	}
	public String getIssueCreationDate() {
		return issueCreationDate;
	}
	public void setIssueCreationDate(String issueCreationDate) {
		this.issueCreationDate = issueCreationDate;
	}
	public String getIssueClosedDate() {
		return issueClosedDate;
	}
	public void setIssueClosedDate(String issueClosedDate) {
		this.issueClosedDate = issueClosedDate;
	}
	@Override
	public String toString() {
		return "IssueData [issueId=" + issueId + ", issueTitle=" + issueTitle
				+ "," + " issueState=" + issueState
				+ ", pullRequestUrl=" + pullRequestUrl + ", commitUrl="
				+ commitUrl + ", issueUserLogin=" + issueUserLogin
				+ ", issueCreationDate=" + issueCreationDate
				+ ", issueClosedDate=" + issueClosedDate + "]";
	}
}
