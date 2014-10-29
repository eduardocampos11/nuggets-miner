package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class SymbolsFilter
  implements IFilter
{
  private boolean preserveOriginal;
  
  public SymbolsFilter()
  {
    this.preserveOriginal = false;
  }
  
  public SymbolsFilter(boolean preserveOriginal)
  {
    this.preserveOriginal = preserveOriginal;
  }
  
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    StringBuilder sb = new StringBuilder();
    for (String t : tokens) {
      if (!t.isEmpty())
      {
        if (this.preserveOriginal) {
          filtered.add(t);
        }
        for (int i = 0; i < t.length(); i++)
        {
          char c = t.charAt(i);
          if ((Character.isLetter(c)) || (Character.isDigit(c)) || (c == '-'))
          {
            sb.append(c);
          }
          else if (!sb.toString().isEmpty())
          {
            filtered.add(sb.toString());
            sb = new StringBuilder();
          }
        }
        if (!sb.toString().isEmpty())
        {
          filtered.add(sb.toString());
          sb = new StringBuilder();
        }
      }
    }
    if (!sb.toString().isEmpty()) {
      filtered.add(sb.toString());
    }
    return filtered;
  }
}
