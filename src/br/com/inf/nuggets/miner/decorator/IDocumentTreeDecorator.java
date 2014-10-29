package br.com.inf.nuggets.miner.decorator;

import org.eclipse.jface.viewers.StyledString;

public abstract interface IDocumentTreeDecorator
{
  public abstract StyledString addPrefixDecoration(Object paramObject);
  
  public abstract StyledString addPostfixDecoration(Object paramObject);

  public abstract String getToolTipText(Object element);
}
