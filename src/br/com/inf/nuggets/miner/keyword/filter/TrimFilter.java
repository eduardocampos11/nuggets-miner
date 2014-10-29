package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class TrimFilter
  implements IFilter
{
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    for (String t : tokens)
    {
      String token = t.trim();
      if (!t.isEmpty()) {
        filtered.add(token);
      }
    }
    return filtered;
  }
}
