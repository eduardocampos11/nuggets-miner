package br.com.inf.nuggets.miner.menu;

import br.com.inf.nuggets.miner.keyword.KeywordsExtractionJob;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;

public class JavaElementAction
  implements IActionDelegate
{
  private IStructuredSelection sel;
  
  public void run(IAction action)
  {
    if (this.sel == null) {
      return;
    }
    KeywordsExtractionJob job = new KeywordsExtractionJob(this.sel);
    job.setPriority(30);
    job.schedule();
  }
  
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.sel = ((IStructuredSelection)selection);
  }
}
