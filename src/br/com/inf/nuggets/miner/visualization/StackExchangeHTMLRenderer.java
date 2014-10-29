package br.com.inf.nuggets.miner.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang3.StringEscapeUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import br.com.inf.nuggets.miner.solr.AnswerNode;
import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.solr.QuestionNode;


public class StackExchangeHTMLRenderer
  extends DocumentBaseHTMLRenderer
{
  private final String questionTemplate;
  private final String answerTemplate;
  private final String commentTemplate;
  private final String codeTemplate;
  private int commentDivCounter = 0;
  
  public StackExchangeHTMLRenderer()
    throws IOException, URISyntaxException
  {
    this.questionTemplate = loadTemplate("question_template.html");
    this.answerTemplate = loadTemplate("answer_template.html");
    this.commentTemplate = loadTemplate("comment_template.html");
    this.codeTemplate = loadTemplate("code_template.html");
    
    addScript("jquery-1.7.js");
    addScript("utils.js");
    addScript("run_prettify.js");
//    addScript("prettify.js");
    
    addStylesSheet("prettify2.css");
    addStylesSheet("style.css");
//    addStylesSheet("sunburst.css");
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
      .replace("$CONTENT", removeCodeTags(question.getBody()))
      .replace("$AUTHOR", question.getOwner() == null ? "" : question.getOwner())
      .replace("$DATE", question.getCreationDate());
//      .replace("$COMMENTS", "Without comments.")
//      .replace("$COMMENT_DIV_ID", "commentDiv" + this.commentDivCounter)
//      .replace("$SHOW_HIDE_COMMENTS", commentHideShow);
    

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
      .replace("$CONTENT", removeCodeTags(answer.getBody()))
      .replace("$AUTHOR", answer.getOwner() == null ? "" : answer.getOwner())
      .replace("$DATE", answer.getCreationDate());
//      .replace("$COMMENTS", "Without comments.")
//      .replace("$COMMENT_DIV_ID", "commentDiv" + this.commentDivCounter)
//      .replace("$SHOW_HIDE_COMMENTS", commentHideShow);
    
    this.commentDivCounter += 1;
    
    return html;
  }
  
  protected String processCodeSnippet(String code)
  {
    String html = this.codeTemplate
    	.replace("$CONTENT", StringEscapeUtils.escapeHtml4(replaceHtmlCharacters(code)));
    return html;
  }
  
  public String replaceHtmlCharacters(String text) {
	  return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&");
  }
  
  /*private String processComments(List<CommentNode> comments)
  {
    StringBuilder builder = new StringBuilder();
    for (CommentNode c : comments) {
      builder.append(processComment(c));
    }
    return builder.toString();
  }*/
  
  /*protected String processComment(CommentNode comment)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy");
    return this.commentTemplate
      .replace("$CONTENT", comment.getBody())
      .replace("$AUTHOR", comment.getOwner() == null ? "" : comment.getOwner().getDisplayName())
      .replace("$DATE", formatter.format(comment.getCreationDate()));
  }*/
  
  /*protected String processDocument(DocumentBase document)
  {
    StringBuilder pageBuilder = new StringBuilder();
    QuestionNode question = (QuestionNode)document.getRoot();
    List<DocumentNode> answers = question.getChildren();
    
    pageBuilder.append(processQuestion(question));
    pageBuilder.append(processAnswers(answers));
    
    setDocumentTitle(document.getTitle());
    return "<div class=\"mainDiv\">" + pageBuilder.toString() + "</div>";
  }*/
  
  /*protected String processNode(DocumentNode node)
  {
    StringBuilder pageBuilder = new StringBuilder();
    pageBuilder.append("<div class=\"mainDiv\">");
    if ((node instanceof QuestionNode)) {
      pageBuilder.append(processQuestion((QuestionNode)node));
    } else if ((node instanceof AnswerNode)) {
      pageBuilder.append(processAnswer((AnswerNode)node));
    } else if ((node instanceof CodeSnippetNode)) {
      pageBuilder.append(processCodeSnippet((CodeSnippetNode)node));
    } else {
      pageBuilder.append("<h1><b>Unable to render this document type.</b></h1>");
    }
    pageBuilder.append("</div>");
    return pageBuilder.toString();
  }*/
  
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
  protected String processDocument(DocumentBase paramDocumentBase) {
	StringBuilder pageBuilder = new StringBuilder();
	
	String oldFormat = "yyyy-MM-dd HH:mm:ss.SSS";
	String newFormat = "dd-MM-yyyy HH:mm:ss.SSS";
	
    DateFormat sdf1 = new SimpleDateFormat(oldFormat);
    DateFormat sdf2 = new SimpleDateFormat(newFormat);
   
	QuestionNode questionNode = new QuestionNode();
	questionNode.setScore(paramDocumentBase.getScoreQuestion());
//	questionNode.setViewCount(14300);
	
    String questionCreationDate = paramDocumentBase.getQuestionCreationDate();
    try {
		String date = sdf2.format(sdf1.parse(questionCreationDate));
		questionNode.setCreationDate(date.split("\\.")[0]);
	} catch (ParseException e) {
		e.printStackTrace();
	}
    
	questionNode.setOwner(paramDocumentBase.getQuestionOwnerName() +
	" (Reputation: " + paramDocumentBase.getQuestionOwnerReputation() + ")");
	
	questionNode.setBody(paramDocumentBase.getQuestionBody());
	
	AnswerNode answerNode = new AnswerNode();
	answerNode.setScore(paramDocumentBase.getScoreAnswer());
//	answerNode.setViewCount(15000);
	
	String answerCreationDate = paramDocumentBase.getAnswerCreationDate();
	try {
		String answerDate = sdf2.format(sdf1.parse(answerCreationDate));
		answerNode.setCreationDate(answerDate.split("\\.")[0]);
	} catch (ParseException e) {
		e.printStackTrace();
	}
	
	answerNode.setOwner(paramDocumentBase.getAnswerOwnerName() + 
			" (Reputation: " + paramDocumentBase.getAnswerOwnerReputation() + ")");
	answerNode.setBody(paramDocumentBase.getAnswerBody());

	String questionCode = getCodigoPreCode(questionNode.getBody());
	String answerCode = getCodigoPreCode(answerNode.getBody());
	
	pageBuilder.append(processQuestion(questionNode));
	if (questionCode != null && !questionCode.equals("")) {
		pageBuilder.append(processCodeSnippet(questionCode));		
	}
    pageBuilder.append(processAnswers(answerNode));
    if (answerCode != null && !answerCode.equals("")) {
    	pageBuilder.append(processCodeSnippet(answerCode));    	
    }
    
    setDocumentTitle(paramDocumentBase.getTitle());
    
    String tags[] =	paramDocumentBase.getTags().split(",");
    String tagLink = null;
    String tagsStr = "";
    
    int count = 0;
    int size = tags.length;
    for (int i = 0; i < tags.length; i++) {
    	tagLink = "<a href=\"http://stackoverflow.com/tags/" + 
    			getFormattedTag(tags[i]) + "/info\">" + tags[i] + "</a>";
    	count++;
    	if (count < size) {
    		tagLink += ", ";    		
    	}
    	tagsStr += tagLink;
    }
   
    
    setDocumentTags(tagsStr);
   
    String html = "<div class=\"mainDiv\">" + pageBuilder.toString() + "</div>";
    
    return html;
  }
  
  private static String getFormattedTag(String tag) {
	  String formattedTag = tag;
	  if (tag.contains("#")) {
		  formattedTag = tag.replaceAll("\\#", "%23");
	  } else if (tag.contains("+")) {
		  formattedTag = tag.replaceAll("\\+", "%2b");
	  }
	  return formattedTag;
  }

  public String getCode(String body) {
	  String codeText = null;
	  try {
		  codeText = getCodigoPreCode(body);
      } catch(Exception e) {
          e.printStackTrace();
      }
      return codeText;
  }
  
  //metodo responsavel por pegar o conteudo de todas as tags "pre/code" do corpo de um post
  public String getCodigoPreCode(String texto) {
	  String textoCodigos = "";
	  try {
		  HtmlCleaner cleaner = new HtmlCleaner();
		  TagNode root = cleaner.clean( texto );
		  Object[] codeNodes = root.evaluateXPath("//pre/code");
		  for (int i=0; i<codeNodes.length; i++) {
			  textoCodigos += ((TagNode)codeNodes[i]).getText();
		  }
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
	  return textoCodigos;
  	}
  
	public static String removeCodeTags(String htmlFragment) {
		Source source = new Source(htmlFragment);
		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();
		
		Set<String> NOT_ALLOWED_HTML_TAGS = new HashSet<String>(
				Arrays.asList(
			      HTMLElementName.CODE,
			      HTMLElementName.PRE)
		);
		
		for (Element element : elements) {
			if (NOT_ALLOWED_HTML_TAGS.contains(element.getName())) {
				outputDocument.remove(element.getStartTag());
				if (!element.getStartTag().isSyntacticalEmptyElementTag()) {
					if (element.getEndTag() != null) {
						outputDocument.remove(element.getEndTag());						
					}
					if (element.getContent() != null) {
						outputDocument.remove(element.getContent());						
					}
				}
			}
		}
		return outputDocument.toString();
	}
}
