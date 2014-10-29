package br.com.inf.nuggets.miner.keyword.filter;

import java.util.ArrayList;
import java.util.List;

public class RemoveOnLengthFilter
  implements IFilter
{
  private final int minLenght;
  
  public RemoveOnLengthFilter(int minLength)
  {
    this.minLenght = minLength;
  }
  
  public List<String> filter(List<String> tokens)
  {
    List<String> filtered = new ArrayList();
    for (String t : tokens) {
      if (t.length() > this.minLenght) {
        filtered.add(t);
      }
    }
    return filtered;
  }
}
