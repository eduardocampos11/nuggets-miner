package br.com.inf.nuggets.miner.keyword.filter;

import java.util.List;

public abstract interface IFilter
{
  public abstract List<String> filter(List<String> paramList);
}
