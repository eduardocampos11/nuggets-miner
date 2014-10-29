package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class LowerCaseFilter
  implements IFilter
{
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    for (String token : tokens) {
      filtered.add(token.toLowerCase());
    }
    return filtered;
  }
}
