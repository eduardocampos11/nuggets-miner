package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class DigitsFilter
  implements IFilter
{
  private final boolean preserveOriginal;
  
  public DigitsFilter()
  {
    this.preserveOriginal = false;
  }
  
  public DigitsFilter(boolean preserveOriginal)
  {
    this.preserveOriginal = preserveOriginal;
  }
  
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    for (String t : tokens) {
      if (!t.isEmpty())
      {
        int lastPos = 0;
        for (int i = 0; i < t.length(); i++) {
          if (Character.isDigit(t.charAt(i)))
          {
            String token = t.substring(lastPos, i);
            if (!token.isEmpty()) {
              filtered.add(token);
            }
            lastPos = i + 1;
          }
        }
        if ((lastPos != 0) && (this.preserveOriginal)) {
          filtered.add(t);
        }
        String token = t.substring(lastPos, t.length());
        if (!token.isEmpty()) {
          filtered.add(token);
        }
      }
    }
    return filtered;
  }
}
