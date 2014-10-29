package br.com.inf.nuggets.miner.solr;

import java.util.Date;

public class QuestionNode 
{
  private int viewCount;
  private String score;
  private String body;
  private String owner;
  private String creationDate;
  
  public QuestionNode()
  {
    this.viewCount = 0;
    this.score = "0";
  }
  
  public int getViewCount()
  {
    return this.viewCount;
  }
  
  public void setViewCount(int viewCount)
  {
    this.viewCount = viewCount;
  }
  
  public String getScore()
  {
    return this.score;
  }
  
  public void setScore(String score)
  {
    this.score = score;
  }

  public String getBody() {
	return body;
  }

  public void setBody(String body) {
	this.body = body;
  }

  public String getOwner() {
	return owner;
  }

  public void setOwner(String owner) {
	this.owner = owner;
  }

  public String getCreationDate() {
	return creationDate;
  }

  public void setCreationDate(String creationDate) {
	this.creationDate = creationDate;
  }
    
  /*@JsonIgnore
  public AnswerNode getAcceptedAnswer()
  {
    for (DocumentNode d : this.children)
    {
      AnswerNode answer = (AnswerNode)d;
      if (answer.getAccepted()) {
        return answer;
      }
    }
    return null;
  }*/
  
  /*public List<CodeSnippetNode> extractCodeSnippetNodes()
  {
    List<CodeSnippetNode> codeSnippets = new ArrayList();
    Document doc = Jsoup.parse(this.body);
    for (Element e : doc.getElementsByTag("code")) {
      if (e.text().split("\\s").length > 1) {
        codeSnippets.add(new CodeSnippetNode(e.text(), this));
      }
    }
    return codeSnippets;
  }*/
}

