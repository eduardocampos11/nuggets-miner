package br.com.inf.nuggets.miner.annotations;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.solr.DocumentBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;

public class NuggetsMinerAnnotation
{
  public static String DOCUMENT_ID_FIELD = "documentId";
  public static String COMMENT_FIELD = "comment";
  public static String DOCUMENT_TITLE_FIELD = "title";
  public static String OFFSET_FIELD = "offset";
  public static String LENGTH_FIELD = "lenght";
  public static String AUTHOR_FIELD = "author";
  public static String CREATION_TIME_FIELD = "creationTime";
  private final HashMap<String, Object> fields;
  
  public NuggetsMinerAnnotation()
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    this.fields = new HashMap();
    String userName = store.getString(NuggetsMinerPreferencesConstant.ANNOTATION_AUTHOR_NAME);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z");
    String creationDate = formatter.format(new Date());
    if (userName != null) {
      this.fields.put(AUTHOR_FIELD, userName);
    } else {
      this.fields.put(AUTHOR_FIELD, "");
    }
    this.fields.put(CREATION_TIME_FIELD, creationDate);
  }
  
  public NuggetsMinerAnnotation(NuggetsMinerAnnotation annotation)
  {
    this();
    this.fields.putAll(annotation.fields);
  }
  
  public NuggetsMinerAnnotation(DocumentBase document, String comment)
  {
    this();
    this.fields.put(DOCUMENT_ID_FIELD, document.getId());
    this.fields.put(COMMENT_FIELD, comment);
    this.fields.put(DOCUMENT_TITLE_FIELD, document.getTitle());
  }
  
  public void setFieldValue(String fieldId, Object fieldValue)
  {
    if (fieldId == COMMENT_FIELD) {
      this.fields.put(fieldId, formatComment((String)fieldValue));
    } else {
      this.fields.put(fieldId, fieldValue);
    }
  }
  
  private String formatComment(String comment)
  {
    return comment.replaceAll("\n", "\n\\* ");
  }
  
  public Object getFieldValue(String fieldId)
  {
    if (!this.fields.containsKey(fieldId)) {
      return null;
    }
    return this.fields.get(fieldId);
  }
  
  public boolean isValid()
  {
    if ((this.fields.containsKey(DOCUMENT_ID_FIELD)) && 
      (this.fields.containsKey(COMMENT_FIELD)) && 
      (this.fields.containsKey(AUTHOR_FIELD)) && 
      (this.fields.containsKey(CREATION_TIME_FIELD)) && 
      (this.fields.containsKey(DOCUMENT_TITLE_FIELD))) {
      return true;
    }
    return false;
  }
  
  private String buildString(String[] delimiters)
  {
    if (delimiters == null) {
      return "";
    }
    String docId = (String)this.fields.get(DOCUMENT_ID_FIELD);
    String comment = (String)this.fields.get(COMMENT_FIELD);
    String title = (String)this.fields.get(DOCUMENT_TITLE_FIELD);
    String creationDate = (String)this.fields.get(CREATION_TIME_FIELD);
    String author = (String)this.fields.get(AUTHOR_FIELD);
    
    StringBuilder annotation = new StringBuilder();
    
    annotation.append(String.format("@%s %s\n* ", new Object[] { DOCUMENT_ID_FIELD, docId }));
    annotation.append(String.format("@%s %s\n* ", new Object[] { DOCUMENT_TITLE_FIELD, title }));
    annotation.append(String.format("@%s %s\n* ", new Object[] { COMMENT_FIELD, comment }));
    annotation.append(String.format("@%s %s\n* ", new Object[] { AUTHOR_FIELD, author }));
    annotation.append(String.format("@%s %s", new Object[] { CREATION_TIME_FIELD, creationDate }));
    
    return String.format(delimiters[0] + "\n* %s\n" + delimiters[1], new Object[] { annotation.toString() });
  }
  
  public String buildString(String fileExtension)
  {
    String[] delimiters = NuggetsMinerPartitionScanner.getDelimiters(fileExtension);
    return buildString(delimiters);
  }
  
  public NuggetsMinerAnnotation clone()
  {
    return new NuggetsMinerAnnotation(this);
  }
  
  public boolean equals(Object obj)
  {
    if (!(obj instanceof NuggetsMinerAnnotation)) {
      return false;
    }
    NuggetsMinerAnnotation annotation = (NuggetsMinerAnnotation)obj;
    




    return (annotation.getFieldValue(DOCUMENT_ID_FIELD).equals(getFieldValue(DOCUMENT_ID_FIELD))) && (annotation.getFieldValue(COMMENT_FIELD).equals(getFieldValue(COMMENT_FIELD))) && (annotation.getFieldValue(DOCUMENT_TITLE_FIELD).equals(getFieldValue(DOCUMENT_TITLE_FIELD))) && (annotation.getFieldValue(AUTHOR_FIELD).equals(getFieldValue(AUTHOR_FIELD))) && (annotation.getFieldValue(CREATION_TIME_FIELD).equals(getFieldValue(CREATION_TIME_FIELD)));
  }
  
  public String toString()
  {
    return this.fields.toString();
  }
}

