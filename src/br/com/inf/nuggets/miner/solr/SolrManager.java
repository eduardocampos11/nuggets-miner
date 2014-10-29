package br.com.inf.nuggets.miner.solr;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.utils.AstParser;
import br.com.inf.nuggets.miner.utils.CodeParser;
import br.com.inf.nuggets.miner.utils.PPAParser;
import br.com.inf.nuggets.miner.utils.SnippetModification;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.eclipse.jface.preference.IPreferenceStore;

public class SolrManager
{
  private final CommonsHttpSolrServer server;
  private static SolrManager instance;
  
  private SolrManager(String url)
    throws MalformedURLException
  {
    this.server = new CommonsHttpSolrServer(url);
  }
  
  private void doQuery(String query, IQueryResultReadyHandler handler)
  {
    DocumentQueryExecuter qe = new DocumentQueryExecuter(this.server, query, new IQueryResultReadyHandler[] { handler });
    qe.setPriority(20);
    qe.schedule();
  }
  
  private void doQuery(String query, IQueryIssueResultReadyHandler handler)
  {
    IssueQueryExecuter qe = new IssueQueryExecuter(this.server, query, new IQueryIssueResultReadyHandler[] { handler });
    qe.setPriority(20);
    qe.schedule();
  }
  
  private void doQuery(String query, String language, IQuerySnippetReadyHandler handler)
  {
    SnippetQueryExecuter qe = new SnippetQueryExecuter(this.server, query, language, 
    		new IQuerySnippetReadyHandler[] { handler });
    qe.setPriority(20);
    qe.schedule();
  }
  
  
  private String escapeQuery(String query)
  {
    return 
      query.replaceAll("\\*", "").replaceAll("\\\\", "").replaceAll("\\(", "").replaceAll("\\)", "")
      .replaceAll(":", "").replaceAll("AND", "").replaceAll("OR", "").replaceAll("NOT", "").
      replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", "");
  }
  
  public void doSearch(String query, IQueryResultReadyHandler handler)
  {
    String escapedQuery = escapeQuery(query);
    doQuery(escapedQuery, handler);
  }
  
  public void doSearch(String query, IQueryIssueResultReadyHandler handler) {
	  	String escapedQuery = escapeQuery(query);
	    /*String[] tokens = escapedQuery.split(" ");
	    int initialIndex = tokens[0].isEmpty() ? 1 : 0;
	    StringBuilder docSb = new StringBuilder("(issue: " + tokens[initialIndex]);
	    StringBuilder titleSb = new StringBuilder("(title: " + tokens[initialIndex]);
	    for (int i = initialIndex + 1; i < tokens.length; i++)
	    {
	      docSb.append(" AND issue: " + tokens[i]);
	      titleSb.append(" OR title: " + tokens[i]);
	    }
	    docSb.append(")");
	    titleSb.append(")");
	    

	    String fullQuery = docSb.toString() + " OR " + titleSb.toString();
//	    System.out.println(fullQuery);
	    System.out.println("query = " + query);*/
	    doQuery(escapedQuery, handler);
  }
  
  /**
   * Method for AST Search without PPA (i.e., using PP2)
   * @param codeTxt
   * @param language
   * @param handler
   */
  public void doSearch(String codeTxt, String language, IQuerySnippetReadyHandler handler) {
	  String codeFacts = extractCodeFacts(codeTxt);
	  codeFacts = escapeQuery(codeFacts);
	  System.out.println("codeFacts: " + codeFacts);
	  doQuery(codeFacts, language, handler);
  }
  
  
  /**
   * Method for AST Search with PPA - Framework Dagenais
   * Generates AST from compilation units of incomplete Java Snippets
   * @param codeTxt
   * @param language
   * @param handler
   */
//  public void doSearch(String codeTxt, String crowdBugStr, String language, IQuerySnippetReadyHandler handler) {
//	  String ppaFacts = extractPPAFacts(codeTxt);
//	  if (ppaFacts.equals("")) {
//		  handler.onError(new CompilationUnitErrorException());
//	  } else {
//		  System.out.println("ppaFacts length: " + ppaFacts.length());
//		  if (crowdBugStr != null && !crowdBugStr.equals("")) {
//			  String reducedPPAFacts = extractPPAForCrowdBug(ppaFacts, crowdBugStr);
//			  if (reducedPPAFacts.equals("")) {
//				  handler.onError(new NotFoundPPAException());
//			  } else {
//				  System.out.println("reducedPPAFacts length: " + reducedPPAFacts.length());
//				  doQuery(reducedPPAFacts, language, handler);
//			  }
//		  } else {
//			  doQuery(ppaFacts, language, handler);
//		  }
//	  }
//  }
  
  public String extractPPAForCrowdBug(String ppaFacts, String crowdBugStr) {
	  int thre = 3; // threshold for number of lines above and below of breakpoint line
	  String[] lines = ppaFacts.split("\n");
	  crowdBugStr = unescapeSpecialCharacters(crowdBugStr);
	  
	  int crowdBugLine = -1;
	  for (int i = 0; i < lines.length; i++) {
		  if (lines[i].contains(crowdBugStr)) {
			  crowdBugLine = i;
			  break;
		  }
	  }
	  
	  String reducedPPAFacts = "";
	  if (crowdBugLine >= 0) {
		  if (crowdBugLine >= thre && crowdBugLine < (lines.length - thre)) {
			  for (int j = crowdBugLine - thre; j <= crowdBugLine + thre; j++) {
				  reducedPPAFacts = reducedPPAFacts + lines[j] + "\n";
			  }
		  } else {			  
			  if (crowdBugLine < thre) {
				  //pega acima da crowdBugLine
				  for (int k = crowdBugLine; k <= crowdBugLine + thre; k++) {
					  reducedPPAFacts = reducedPPAFacts + lines[k] + "\n";
				  }
			  } else {
				  if (crowdBugLine >= (lines.length - thre)) {
					  //pega abaixo da crowdBugLine
					  for (int z = crowdBugLine - thre; z <= crowdBugLine; z++) {
						  reducedPPAFacts = reducedPPAFacts + lines[z] + "\n";
					  }
				  }				  
			  }  
		  }
	  } 
	  return reducedPPAFacts;
  }
  
  /**
   * Method for Lexical Search
   * @param codeText
   * @param language
   * @param handler
   */
//  public void doSearch(String codeText, String language, IQuerySnippetReadyHandler handler) {
//	  String modifiedCodeSnippet = extractCodeWords(codeText);
//	  System.out.println("modifiedCodeSnippet: " + modifiedCodeSnippet);
//	  doQuery(modifiedCodeSnippet, language, handler);  
//  }
  
  /**
   * Method for Code Snippet Search without pre-processing
   * @param codeText
   * @return
   */
//  public void doSearch(String codeText, String language, IQuerySnippetReadyHandler handler) {
//	  String escapedQuery = escapeQuery(codeText);
//	  System.out.println("escapedQuery: " + escapedQuery);
//	  doQuery(escapedQuery, language, handler);  
//  }
  
  public String extractCodeFacts(String codeText) {
	  CodeParser codeParser = new CodeParser();
	  List<String> javaClazzLst = null;
	  StringBuilder formattedCode = null;
	  String codeFacts = "";
	  
	  String[] codeLines = codeParser.getCodeLinesVet(codeText);
	  javaClazzLst = codeParser.getJavaClasses(codeLines);
	  formattedCode = codeParser.getFormattedCode(codeText);
		
	  if (javaClazzLst != null && !javaClazzLst.isEmpty()) {
		  codeFacts = AstParser.extractFacts(formattedCode);
		  
		  if (codeFacts == null || codeFacts.equals("") || 
			  codeFacts.equals("IllegalArgumentException")) {
			  codeFacts = escapeQuery(codeText);
		  }
	  } else {
		  StringBuilder builder = new StringBuilder();
		  String clazzDefault = "public class Default {\n";
		  builder.append(clazzDefault);
		  builder.append(formattedCode.toString());
		  builder.append("} \n");	
		  System.out.println("builder: " + builder.toString());
		  codeFacts = AstParser.extractFacts(builder);
		  
		  if (codeFacts == null || codeFacts.equals("") || 
			  codeFacts.equals("IllegalArgumentException")) {
			  codeFacts = escapeQuery(codeText);
		  }
	  }
	  return codeFacts;
  }
  
  public String extractPPAFacts(String codeText) {
	  String ppaFacts = PPAParser.extractPPAFacts(codeText);
	  if (ppaFacts != null && !ppaFacts.equals("") && !ppaFacts.equals("null")) {
		  ppaFacts = unescapeSpecialCharacters(ppaFacts);		  
	  } else {
		  ppaFacts = "";
	  }
	  return ppaFacts;
  }
  
  private String unescapeSpecialCharacters(String query) {
	  String response = query;
	  response = response.replaceAll("\\+", " ");
	  response = response.replaceAll("\\-", " ");
	  response = response.replaceAll("\\&&", "");
	  response = response.replaceAll("\\||", "");
	  response = response.replaceAll("\\!", "");
	  response = response.replaceAll("\\(", " ");
	  response = response.replaceAll("\\)", " ");
	  response = response.replaceAll("\\{", "");
	  response = response.replaceAll("\\}", "");
	  response = response.replaceAll("\\[", "");
	  response = response.replaceAll("\\]", "");
	  response = response.replaceAll("\\^", "");
	  response = response.replaceAll("\\~", "");
	  response = response.replaceAll("\\*", " ");
	  response = response.replaceAll("\\?", "");
	  response = response.replaceAll("<EOF>", "");
	  response = response.replaceAll("\\/", " ");
	  response = response.replaceAll("\\:", " ");
	 
	  return response;
  }
  
  public String extractCodeWords(String codeText) {
	  SnippetModification modifier = new SnippetModification();
	  String modifiedCodeSnippet = modifier.modifySnippet(codeText);
	  return modifiedCodeSnippet;
  }
  
  public void retrieveDocument(String id, IQueryResultReadyHandler handler)
  {
    List<String> ids = new ArrayList<String>();
    ids.add(id);
    retrieveDocuments(ids, handler);
  }
  
  public void retrieveDocuments(List<String> ids, IQueryResultReadyHandler handler)
  {
    DocumentRetrieveJob docRe = new DocumentRetrieveJob(this.server, ids, new IQueryResultReadyHandler[] { handler });
    docRe.setPriority(20);
    docRe.schedule();
  }
  
  public static void main(String[] args) {
	System.out.println("field: " + String.valueOf((char) 1));
  }
  
  public static void disposeInstance()
  {
    instance = null;
  }
  
  public static SolrManager getInstance()
    throws MalformedURLException
  {
    if (instance == null)
    {
      IPreferenceStore store = Activator.getDefault().getPreferenceStore();
      instance = new SolrManager(store.getString(NuggetsMinerPreferencesConstant.SOLR_URL));
    }
    return instance;
  }
}
