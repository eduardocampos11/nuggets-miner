package br.com.inf.nuggets.miner.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.solr.IssueData;

public abstract class IssueDataHTMLRenderer
{
  private final List<String> scripts = new ArrayList();
  private final List<String> styles = new ArrayList();
  private String documentTemplate;
  public String TEMPLATE_PATH = "browser/template/";
  public String SCRIPT_PATH = "browser/js/";
  public String STYLE_PATH = "browser/style/";
  
  protected String resolveResourcePath(String resource)
    throws URISyntaxException, IOException
  {
    Bundle b = Platform.getBundle(NuggetsMinerPreferencesConstant.BUNDLE);
    URL url = b.getEntry(resource);
    File resFile = new File(FileLocator.toFileURL(url).toURI());
    return resFile.getAbsolutePath();
  }
  
  public void addScript(String script)
  {
    this.scripts.add(script);
  }
  
  public void addStylesSheet(String style)
  {
    this.styles.add(style);
  }
  
  private void setScripts()
    throws URISyntaxException, IOException
  {
    StringBuilder builder = new StringBuilder();
    for (String s : this.scripts) {
      builder.append("<script type=\"text/javascript\" src=\"" + resolveResourcePath(new StringBuilder(String.valueOf(this.SCRIPT_PATH)).append(s).toString()) + "\"></script>");
    }
    this.documentTemplate = this.documentTemplate.replace("$STYLES", builder.toString());
  }
  
  private void setStyles()
    throws URISyntaxException, IOException
  {
    StringBuilder builder = new StringBuilder();
    for (String s : this.styles) {
      builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + resolveResourcePath(new StringBuilder(String.valueOf(this.STYLE_PATH)).append(s).toString()) + "\" />");
    }
    this.documentTemplate = this.documentTemplate.replace("$SCRIPTS", builder.toString());
  }
  
  private void setIssueContent(String content)
  {
    this.documentTemplate = this.documentTemplate.replace("$DOCUMENT", content);
  }
  
  protected void setIssueTitle(String title)
  {
    this.documentTemplate = this.documentTemplate.replace("$TITLE", title);
  }
  
  protected void setCommitUrl(String commitUrl)
  {
	this.documentTemplate = this.documentTemplate.replace("$TAGS", "Commit URL: " + commitUrl);
  }
  
  protected abstract String processIssue(IssueData issueData);
  
//  protected abstract String processNode(DocumentNode paramDocumentNode);
  
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
  
  public String getDocumentHTML(IssueData issue)
    throws URISyntaxException, IOException
  {
    this.documentTemplate = loadTemplate("doc_template.html");
    setScripts();
    setStyles();
//    setIssueTitle(issue.getIssueTitle());
    setIssueContent(processIssue(issue));
    return this.documentTemplate;
  }
    
  public List<String> getScripts()
  {
    return this.scripts;
  }
  
  public List<String> getStyles()
  {
    return this.styles;
  }
}

