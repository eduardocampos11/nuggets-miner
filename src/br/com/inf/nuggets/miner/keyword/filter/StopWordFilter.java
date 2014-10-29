package br.com.inf.nuggets.miner.keyword.filter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWordFilter
  implements IFilter
{
  private final Set<String> stopWords;
  private boolean caseInsensitive;
  
  public StopWordFilter(File listFile, boolean caseInensitive, boolean includeJavaStopWords)
    throws IOException
  {
    this.caseInsensitive = caseInensitive;
    this.stopWords = new HashSet();
    FileInputStream fstream = new FileInputStream(listFile);
    DataInputStream in = new DataInputStream(fstream);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    if (includeJavaStopWords) {
      addJavaStopWords();
    }
    String strLine;
    while ((strLine = br.readLine()) != null)
    {
      if (!strLine.isEmpty()) {
        this.stopWords.add(strLine);
      }
    }
  }
  
  public StopWordFilter(boolean caseIsensitive)
  {
    this.caseInsensitive = caseIsensitive;
    this.stopWords = new HashSet();
    addJavaStopWords();
  }
  
  private void addJavaStopWords()
  {
    this.stopWords.addAll(Arrays.asList(new String[] {
      "if", "else", "while", "do", "for", "switch", "case", "default", "break", 
      "new", "import", "package", "public", "private", "protected", "static", 
      "long", "int", "float", "double", "char", "byte", "void", "short", "boolean", 
      "return", "class", "interface", "implements", "extends", "final", "continue", 
      "string", "integer", "const", "null", "true", "false" }));
  }
  
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    for (String t : tokens) {
      if ((!this.stopWords.contains(this.caseInsensitive ? t.toLowerCase() : t)) && (!t.isEmpty())) {
        filtered.add(t);
      }
    }
    return filtered;
  }
}
