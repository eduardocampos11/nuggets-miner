package br.com.inf.nuggets.miner.observer;

import org.eclipse.ui.IEditorPart;

public abstract interface IEditorOnTopListener
{
  public abstract void editorOnTopChanged(IEditorPart paramIEditorPart);
  
  public abstract void editorOnTopClosed(IEditorPart paramIEditorPart);
}
