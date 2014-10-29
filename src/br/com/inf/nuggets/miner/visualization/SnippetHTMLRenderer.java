package br.com.inf.nuggets.miner.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.osgi.framework.Bundle;

import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.solr.SnippetData;


public class SnippetHTMLRenderer
{
  private final List<String> scripts = new ArrayList<String>();
  private final List<String> styles = new ArrayList<String>();
  private String codeTemplate;
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
    this.codeTemplate = this.codeTemplate.replace("$STYLES", builder.toString());
  }
  
  private void setStyles()
    throws URISyntaxException, IOException
  {
    StringBuilder builder = new StringBuilder();
    for (String s : this.styles) {
      builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + resolveResourcePath(new StringBuilder(String.valueOf(this.STYLE_PATH)).append(s).toString()) + "\" />");
    }
    this.codeTemplate = this.codeTemplate.replace("$SCRIPTS", builder.toString());
  }
  
  /*private void setSnippetContent(String content)
  {
    this.codeTemplate = this.codeTemplate.replace("$CONTENT", content);
  }*/
  
  protected String processCodeSnippet(String code)
  {
    String html = this.codeTemplate
      .replace("$CONTENT", code);
   
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
  
  public String getDocumentHTML(SnippetData snippet)
    throws URISyntaxException, IOException {
    this.codeTemplate = loadTemplate("code_template.html");
    setScripts();
    setStyles();
    String codeContent = getCodigoPreCode(snippet.getCodeText());
  
    StringBuilder pageBuilder = new StringBuilder();
    pageBuilder.append(processCodeSnippet(codeContent));
    
    return "<div class=\"mainDiv\">" + pageBuilder.toString() + "</div>";
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
  
  public List<String> getScripts()
  {
    return this.scripts;
  }
  
  public List<String> getStyles()
  {
    return this.styles;
  }
}
