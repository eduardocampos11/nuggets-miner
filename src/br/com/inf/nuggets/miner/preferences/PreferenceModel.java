package br.com.inf.nuggets.miner.preferences;

public class PreferenceModel
{
  private String fileExtension;
  private String openingDelimiter;
  private String closingDelimiter;
  
  public PreferenceModel()
  {
    this.fileExtension = "";
    this.openingDelimiter = "";
    this.closingDelimiter = "";
  }
  
  public PreferenceModel(String fileExtension, String openingDelimiter, String closingDelimiter)
  {
    this.fileExtension = fileExtension;
    this.openingDelimiter = openingDelimiter;
    this.closingDelimiter = closingDelimiter;
  }
  
  public String getFileExtension()
  {
    return this.fileExtension;
  }
  
  public void setFileExtension(String fileExtension)
  {
    this.fileExtension = fileExtension;
  }
  
  public String getOpeningDelimiter()
  {
    return this.openingDelimiter;
  }
  
  public void setOpeningDelimiter(String openingDelimiter)
  {
    this.openingDelimiter = openingDelimiter;
  }
  
  public String getClosingDelimiter()
  {
    return this.closingDelimiter;
  }
  
  public void setClosingDelimiter(String closingDelimiter)
  {
    this.closingDelimiter = closingDelimiter;
  }
  
  public boolean equals(Object o)
  {
    return ((o instanceof PreferenceModel)) && (((PreferenceModel)o).fileExtension.equals(this.fileExtension));
  }
}
