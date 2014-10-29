package br.com.inf.nuggets.miner.keyword.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class WhiteSpaceTokenizer
  implements ITokenizer
{
  public List<String> tokenize(String content)
  {
    List<String> tokens = new ArrayList();
    String[] split = content.split("\\s");
    for (String s : split) {
      if (!s.isEmpty()) {
        tokens.add(s);
      }
    }
    return tokens;
  }
}
