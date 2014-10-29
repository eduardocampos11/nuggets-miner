package br.com.inf.nuggets.miner.sqlite;

public class MrCrawleyDbEntry
{
  private String filePath;
  private String docId;
  private String title;
  private String comment;
  
  public MrCrawleyDbEntry() {}
  
  public MrCrawleyDbEntry(String filePath, String documentId, String title, String comment)
  {
    this.filePath = filePath;
    this.docId = documentId;
    this.title = title;
    this.comment = comment;
  }
  
  public String getComment()
  {
    return this.comment;
  }
  
  public void setComment(String comment)
  {
    this.comment = comment;
  }
  
  public String getFilePath()
  {
    return this.filePath;
  }
  
  public void setFilePath(String filePath)
  {
    this.filePath = filePath;
  }
  
  public String getDocId()
  {
    return this.docId;
  }
  
  public void setDocId(String docId)
  {
    this.docId = docId;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void setTitle(String title)
  {
    this.title = title;
  }
}
