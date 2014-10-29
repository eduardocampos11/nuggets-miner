package br.com.inf.nuggets.miner.observer;

import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract interface IDocumentChangedListener
{
  public abstract void onDocumentChanged(ITextEditor paramITextEditor, IDocumentExtension3 paramIDocumentExtension3);
  
  public abstract void onDocumentAboutToBeChanged(ITextEditor paramITextEditor, IDocumentExtension3 paramIDocumentExtension3);
}
