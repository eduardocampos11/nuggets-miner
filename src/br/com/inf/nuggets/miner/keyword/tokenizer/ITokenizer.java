package br.com.inf.nuggets.miner.keyword.tokenizer;

import java.util.List;

public abstract interface ITokenizer
{
  public abstract List<String> tokenize(String paramString);
}
