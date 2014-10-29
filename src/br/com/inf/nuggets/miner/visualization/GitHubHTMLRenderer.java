package br.com.inf.nuggets.miner.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringEscapeUtils;

import br.com.inf.nuggets.miner.solr.AnswerNode;
import br.com.inf.nuggets.miner.solr.IssueData;
import br.com.inf.nuggets.miner.solr.QuestionNode;


public class GitHubHTMLRenderer
  extends IssueDataHTMLRenderer
{
  private final String questionTemplate;
  private final String answerTemplate;
  private final String commentTemplate;
  private final String codeTemplate;
  private int commentDivCounter = 0;
  
  public GitHubHTMLRenderer()
    throws IOException, URISyntaxException
  {
    this.questionTemplate = loadTemplate("question_template.html");
    this.answerTemplate = loadTemplate("answer_template.html");
    this.commentTemplate = loadTemplate("comment_template.html");
    this.codeTemplate = loadTemplate("code_template.html");
    
    addScript("jquery-1.7.js");
    addScript("utils.js");
    addScript("prettify.js");
    
    addStylesSheet("prettify.css");
    addStylesSheet("style.css");
  }
  
  protected String processQuestion(QuestionNode question)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
    String commentHideShow = "";
    /*if (question.getComments().size() > 0) {
      commentHideShow = 
        "<a title=\"" + question.getComments().size() + "Comments\" href=\"javascript:toggleComments(this,'commentDiv'+commentDivCounter);\"></a>";
    }*/
    String html = this.questionTemplate
      .replace("$SCORE", question.getScore())
      .replace("$CONTENT", "no content available"/*removeCodeTags(question.getBody())*/)
      .replace("$AUTHOR", question.getOwner() == null ? "" : question.getOwner())
      .replace("$DATE", question.getCreationDate())
      .replace("$COMMENTS", "Without comments.")
      .replace("$COMMENT_DIV_ID", "commentDiv" + this.commentDivCounter)
      .replace("$SHOW_HIDE_COMMENTS", commentHideShow);
    

    this.commentDivCounter += 1;
    
    return html;
  }
  
  private String processAnswers(AnswerNode answer)
  {
    StringBuilder builder = new StringBuilder();
//    for (DocumentNode a : answers) {
      builder.append(processAnswer(answer));
//    }
    return builder.toString();
  }
  
  protected String processAnswer(AnswerNode answer)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
    String commentHideShow = "";
    /*if (answer.getComments().size() > 0)
    {
      String nComments = answer.getComments().size() > 1 ? "comments" : "comment";
      
      commentHideShow = "<a href=\"javascript:toggleComments('#commentDiv" + this.commentDivCounter + "');\">" + 
        answer.getComments().size() + " " + nComments + "</a>";
    }*/
    String html = this.answerTemplate
      .replace("$ANSWER_TYPE", answer.isAccepted() ? "acceptedAnswer" : "answer")
      .replace("$SCORE", answer.getScore())
      .replace("$AUTHOR", answer.getOwner() == null ? "" : answer.getOwner())
      .replace("$DATE", answer.getCreationDate())
      .replace("$COMMENTS", "Without comments.")
      .replace("$COMMENT_DIV_ID", "commentDiv" + this.commentDivCounter)
      .replace("$SHOW_HIDE_COMMENTS", commentHideShow);
    
    this.commentDivCounter += 1;
    
    return html;
  }
  
  protected String processCodeSnippet(String questionCode)
  {
    String html = this.codeTemplate
      .replace("$CONTENT", StringEscapeUtils.escapeHtml4(questionCode));
   
    return html;
  }
  
  private String loadTemplate(String fileName)
    throws IOException, URISyntaxException
  {
    File file = new File(resolveResourcePath(this.TEMPLATE_PATH + fileName));
    BufferedReader reader = new BufferedReader(new FileReader(file));
    
    StringBuilder builder = new StringBuilder();
    String str = reader.readLine();
    while (str != null)
    {
      builder.append(str);
      str = reader.readLine();
    }
    return builder.toString();
  }
  
  public String getQuestionTemplate()
  {
    return this.questionTemplate;
  }
  
  public String getAnswerTemplate()
  {
    return this.answerTemplate;
  }
  
  public String getCommentTemplate()
  {
    return this.commentTemplate;
  }

  @Override
  protected String processIssue(IssueData issue) {
	StringBuilder pageBuilder = new StringBuilder();
	setIssueTitle("Issue #" + issue.getIssueId() + " - " + issue.getIssueTitle());
    String commitUrl = "<a href=\"" + issue.getCommitUrl() + "\">" + issue.getCommitUrl() + "</a>";
    
    System.out.println("commitUrl: " + commitUrl);
    
    setCommitUrl(commitUrl);
    
    return "<div class=\"mainDiv\">" + issue.getIssueBody() + "</div>";
  }
}