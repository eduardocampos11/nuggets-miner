package br.com.inf.nuggets.miner.solr;

import java.io.IOException;

public class DocumentBase {
	
  private String id;
  private String title;
  private String post;
  private String questionBody;
  private String questionTitle;
  private String answerBody;
  private Double scorePair;
  private Double scoreLucene;
  private Double scoreFinal;
  private String scoreQuestion;
  private String scoreAnswer;
  private String tags;
  private String questionCreationDate;
  private String answerCreationDate;
  private String questionOwnerReputation;
  private String questionOwnerName;
  private String answerOwnerReputation;
  private String answerOwnerName;
  
  public String getPost() {
	return post;
  }

  public void setPost(String post) {
	this.post = post;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }
  
  public String getTitle() {
	  return title;
  }

  public void setTitle(String title) {
	  this.title = title;
  }
  
  public String toJson() throws IOException
  {
	  return "";
  }

  public String getQuestionBody() {
	return questionBody;
  }

  public void setQuestionBody(String questionBody) {
	  this.questionBody = questionBody;
  }

  public String getQuestionTitle() {
	  return questionTitle;
  }

  public void setQuestionTitle(String questionTitle) {
	  this.questionTitle = questionTitle;
  }

  public String getAnswerBody() {
	  return answerBody;
  }

  public void setAnswerBody(String answerBody) {
	  this.answerBody = answerBody;
  }

  public Double getScorePair() {
	  return scorePair;
  }

  public void setScorePair(Double scorePair) {
	  this.scorePair = scorePair;
  }
  
  public Double getScoreLucene() {
	  return scoreLucene;
  }
  
  public void setScoreLucene(Double scoreLucene) {
	  this.scoreLucene = scoreLucene;
  }
  
  public Double getScoreFinal() {
	  return scoreFinal;
  }
  
  public void setScoreFinal(Double scoreFinal) {
	  this.scoreFinal = scoreFinal;
  }

  public String getScoreQuestion() {
	return scoreQuestion;
  }

  public void setScoreQuestion(String scoreQuestion) {
	this.scoreQuestion = scoreQuestion;
  }

  public String getScoreAnswer() {
	return scoreAnswer;
  }

  public void setScoreAnswer(String scoreAnswer) {
	this.scoreAnswer = scoreAnswer;
  }

  public String getTags() {
	  return tags;
  }

  public void setTags(String tags) {
	  this.tags = tags;
  }

  public String getQuestionCreationDate() {
	  return questionCreationDate;
  }

  public void setQuestionCreationDate(String questionCreationDate) {
	  this.questionCreationDate = questionCreationDate;
  }

  public String getAnswerCreationDate() {
	  return answerCreationDate;
  }

  public void setAnswerCreationDate(String answerCreationDate) {
	  this.answerCreationDate = answerCreationDate;
  }

  public String getQuestionOwnerReputation() {
	return questionOwnerReputation;
  }

  public void setQuestionOwnerReputation(String questionOwnerReputation) {
	this.questionOwnerReputation = questionOwnerReputation;
  }

  public String getQuestionOwnerName() {
	return questionOwnerName;
  }

  public void setQuestionOwnerName(String questionOwnerName) {
	this.questionOwnerName = questionOwnerName;
  }

  public String getAnswerOwnerReputation() {
	return answerOwnerReputation;
  }

  public void setAnswerOwnerReputation(String answerOwnerReputation) {
	this.answerOwnerReputation = answerOwnerReputation;
  }

  public String getAnswerOwnerName() {
	return answerOwnerName;
  }

  public void setAnswerOwnerName(String answerOwnerName) {
	this.answerOwnerName = answerOwnerName;
  }
}