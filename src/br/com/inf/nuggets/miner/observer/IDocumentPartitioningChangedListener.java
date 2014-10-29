package br.com.inf.nuggets.miner.observer;

import java.util.HashMap;
import java.util.List;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract interface IDocumentPartitioningChangedListener
{
  public abstract void onPartitioningChanged(ITextEditor paramITextEditor, IDocumentExtension3 paramIDocumentExtension3, HashMap<String, List<ITypedRegion>> paramHashMap);
  
  public abstract void onPartitioningFailed(IDocumentExtension3 paramIDocumentExtension3, Exception paramException);
}
