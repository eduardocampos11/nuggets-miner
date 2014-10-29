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


public abstract class DocumentBaseHTMLRenderer
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
    throws URISyntaxException, IOException {
    StringBuilder builder = new StringBuilder();
//    String runPrettifyArgs = "?autoload=true&amp;skin=sunburst&amp;lang=css\" defer=\"defer\"></script>";
    for (String s : this.scripts) {
    	System.out.println("set Scripts s: " + s);
    	if (s.equals("run_prettify.js")) {
        	builder.append("<script src=\"" + 
        	    resolveResourcePath(new StringBuilder(
        	    		String.valueOf(this.SCRIPT_PATH)).append(s).toString()) + "\"></script>");
//        	builder.append("<script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js\"></script>");
        	
    	} else {    		
    		builder.append("<script type=\"text/javascript\" src=\"" + 
    				resolveResourcePath(new StringBuilder(
    						String.valueOf(this.SCRIPT_PATH)).append(s).toString()) + "\"></script>");    		
    	}
    }
    
//    builder.append("<script src=\"https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js" + runPrettifyArgs);
    
//    this.documentTemplate = this.documentTemplate.replace("$STYLES", builder.toString());
    System.out.println("DocumentBaseHTMLRenderer setScripts: " + builder.toString());
    this.documentTemplate = this.documentTemplate.replace("$SCRIPTS", builder.toString());
  }
  
  private void setStyles()
    throws URISyntaxException, IOException
  {
    StringBuilder builder = new StringBuilder();
    for (String s : this.styles) {
      builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + resolveResourcePath(new StringBuilder(String.valueOf(this.STYLE_PATH)).append(s).toString()) + "\" />");
    }
//    this.documentTemplate = this.documentTemplate.replace("$SCRIPTS", builder.toString());
    this.documentTemplate = this.documentTemplate.replace("$STYLES", builder.toString());
  }
  
  private void setDocumentContent(String content)
  {
    this.documentTemplate = this.documentTemplate.replace("$DOCUMENT", content);
  }
  
  protected void setDocumentTitle(String title)
  {
    this.documentTemplate = this.documentTemplate.replace("$TITLE", title);
  }
  
  protected void setDocumentTags(String tags)
  {
	this.documentTemplate = this.documentTemplate.replace("$TAGS", "Tags: " + tags);
  }
  
  protected abstract String processDocument(DocumentBase paramDocumentBase);
  
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
  
  public String getDocumentHTML(DocumentBase document)
    throws URISyntaxException, IOException
  {
    this.documentTemplate = loadTemplate("doc_template.html");
    setScripts();
    setStyles();
    setDocumentTitle(document.getTitle());
    setDocumentContent(processDocument(document));
    
    System.out.println("documentTemplate: " + documentTemplate);
    
    return this.documentTemplate;
  }
  
  /*public String getDocumentNodeHTML(DocumentNode node)
    throws URISyntaxException, IOException
  {
    this.documentTemplate = loadTemplate("doc_template.html");
    setScripts();
    setStyles();
    setDocumentTitle("");
    setDocumentContent(processNode(node));
    return this.documentTemplate;
  }*/
  
  public List<String> getScripts()
  {
    return this.scripts;
  }
  
  public List<String> getStyles()
  {
    return this.styles;
  }
}
