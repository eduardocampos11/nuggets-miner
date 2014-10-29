package br.com.inf.nuggets.miner.menu;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.keyword.CodeKeywordsExtractor;
import br.com.inf.nuggets.miner.keyword.DefaultKeywordExtractor;
import br.com.inf.nuggets.miner.observer.GlobalEditorOnTopObserver;
import br.com.inf.nuggets.miner.views.DocumentTreeView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class SearchViewShortcutHandler
  extends AbstractHandler
{
  public Object execute(ExecutionEvent event)
    throws ExecutionException
  {
    try
    {
      String query = getEnhancedQuery();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("br.com.inf.nuggets.miner.views.DocumentTreeView");
      DocumentTreeView view = (DocumentTreeView)PlatformUI.getWorkbench()
        .getActiveWorkbenchWindow().getActivePage().findView("br.com.inf.nuggets.miner.views.DocumentTreeView");
      

      view.setFocus();
      view.setQueryString(query);
    }
    catch (PartInitException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private IJavaElement getCurrentEntity()
  {
    try
    {
      IEditorPart onTopEditor = Activator.getEditorOnTopObserver().getEditorOnTop();
      if (onTopEditor == null) {
        return null;
      }
      IEditorInput input = onTopEditor.getEditorInput();
      if (input == null) {
        return null;
      }
      IFile file = (IFile)input.getAdapter(IFile.class);
      if (file == null) {
        return null;
      }
      IJavaElement javaElement = JavaCore.create(file);
      if (!(javaElement instanceof ICompilationUnit)) {
        return null;
      }
      ICompilationUnit unit = (ICompilationUnit)javaElement;
      ITextSelection textSelection = (ITextSelection)onTopEditor.getSite().getSelectionProvider().getSelection();
      
      return unit.getElementAt(textSelection.getOffset());
    }
    catch (JavaModelException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private String getEnhancedQuery()
    throws IOException, URISyntaxException
  {
    try
    {
      IJavaElement element = getCurrentEntity();
      if (element == null) {
        return "";
      }
      CodeKeywordsExtractor extractor = new DefaultKeywordExtractor();
      List<String> entityKeywords = extractor.extractKeywords(element);
      StringBuilder sb = new StringBuilder();
      for (String s : entityKeywords) {
        sb.append(" " + s);
      }
      return sb.toString();
    }
    catch (JavaModelException e)
    {
      e.printStackTrace();
    }
    return "";
  }
}
