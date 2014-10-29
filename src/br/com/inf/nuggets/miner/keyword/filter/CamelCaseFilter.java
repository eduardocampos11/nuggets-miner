package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class CamelCaseFilter
  implements IFilter
{
  private final boolean preserveOriginal;
  
  public CamelCaseFilter(boolean preserveOriginal)
  {
    this.preserveOriginal = preserveOriginal;
  }
  
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    boolean upperCase = false;
    for (String t : tokens) {
      if (!t.isEmpty())
      {
        if (this.preserveOriginal) {
          filtered.add(t);
        }
        int lastPos = 0;
        upperCase = Character.isUpperCase(t.charAt(0));
        for (int i = 1; i < t.length(); i++) {
          if (Character.isUpperCase(t.charAt(i)))
          {
            String token = t.substring(lastPos, i);
            if ((!token.isEmpty()) && (!upperCase))
            {
              filtered.add(token);
              lastPos = i;
            }
            upperCase = true;
          }
          else
          {
            upperCase = false;
          }
        }
        if (((lastPos != 0) && (this.preserveOriginal)) || ((lastPos == 0) && (!this.preserveOriginal)))
        {
          String token = t.substring(lastPos, t.length());
          if (!token.isEmpty()) {
            filtered.add(token);
          }
        }
      }
    }
    return filtered;
  }
}
