package br.com.inf.nuggets.miner.keyword.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringDelimiterTokenizer
  implements ITokenizer
{
  private final String delimiter;
  
  public StringDelimiterTokenizer(String delimiter)
  {
    this.delimiter = delimiter;
  }
  
  public List<String> tokenize(String content)
  {
    StringTokenizer st = new StringTokenizer(content, this.delimiter);
    List<String> tokens = new ArrayList();
    while (st.hasMoreTokens())
    {
      String token = st.nextToken();
      if (!token.isEmpty()) {
        tokens.add(token);
      }
    }
    return tokens;
  }
}
