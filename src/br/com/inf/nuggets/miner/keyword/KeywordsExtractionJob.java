package br.com.inf.nuggets.miner.keyword;

import br.com.inf.nuggets.miner.views.DocumentTreeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class KeywordsExtractionJob
  extends Job
{
  final Object selection;
  
  public KeywordsExtractionJob(IJavaElement element)
  {
    super("Keyword Extraction");
    this.selection = element;
  }
  
  public KeywordsExtractionJob(IStructuredSelection selection)
  {
    super("Keyword Extraction");
    this.selection = selection;
  }
  
  protected IStatus run(IProgressMonitor monitor)
  {
    try
    {
      CodeKeywordsExtractor keygen = new DefaultKeywordExtractor();
      List<String> keywords;
      if ((this.selection instanceof IStructuredSelection)) {
        keywords = extractFromSelection((IStructuredSelection)this.selection, monitor, keygen);
      } else {
        keywords = extractFromElement((IJavaElement)this.selection, monitor, keygen);
      }
      if (keywords.size() <= 0) {
        return Status.OK_STATUS;
      }
      performQuery(new ArrayList(keywords));
      return Status.OK_STATUS;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new Status(4, "Seahawk", e.getMessage());
    }
  }
  
  private List<String> extractFromElement(IJavaElement element, IProgressMonitor monitor, CodeKeywordsExtractor keygen)
    throws JavaModelException
  {
    monitor.beginTask(element.getElementName(), -1);
    return keygen.extractKeywords(element);
  }
  
  private List<String> extractFromSelection(IStructuredSelection selection, IProgressMonitor monitor, CodeKeywordsExtractor keygen)
    throws JavaModelException
  {
    Set<String> keywords = new HashSet();
    monitor.beginTask("", selection.toList().size());
    for (Object obj : selection.toList()) {
      if ((obj instanceof IJavaElement))
      {
        IJavaElement element = (IJavaElement)obj;
        monitor.beginTask(element.getElementName(), 100);
        keywords.addAll(keygen.extractKeywords(element));
        monitor.worked(1);
      }
    }
    return new ArrayList(keywords);
  }
  
  private void performQuery(final List<String> keywords)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("br.com.inf.nuggets.miner.views.DocumentTreeView");
          DocumentTreeView view = (DocumentTreeView)PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage().findView("br.com.inf.nuggets.miner.views.DocumentTreeView");
          String query = KeywordsExtractionJob.this.buildQuery(keywords);
          view.performExternalQuery(query);
        }
        catch (PartInitException e)
        {
          e.printStackTrace();
        }
      }
    });
  }
  
  private String buildQuery(List<String> keywords)
  {
    StringBuilder sb = new StringBuilder();
    for (String k : keywords) {
      sb.append(k + " ");
    }
    return sb.toString();
  }
}
