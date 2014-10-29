package br.com.inf.nuggets.miner.menu;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.keyword.KeywordsExtractionJob;
import br.com.inf.nuggets.miner.observer.GlobalEditorOnTopObserver;
import br.com.inf.nuggets.miner.utils.AlertManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.FileEditorInput;

public class CompilationUnitEditorContextMenu
  extends AbstractHandler
{
  public Object execute(ExecutionEvent event)
    throws ExecutionException
  {
    try
    {
      GlobalEditorOnTopObserver editorObserver = Activator.getEditorOnTopObserver();
      IEditorPart editor = editorObserver.getEditorOnTop();
      ICompilationUnit unit = getCompilationUnit(editor);
      if (unit == null) {
        return null;
      }
      int cursorPosition = getCursorPosition(editor);
      IJavaElement element = unit.getElementAt(cursorPosition);
      KeywordsExtractionJob job = new KeywordsExtractionJob(element);
      job.setPriority(20);
      job.schedule();
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    catch (JavaModelException e)
    {
      AlertManager.showErrorMessage("Error on java model.", e.getMessage());
      e.printStackTrace();
    }
    return null;
  }
  
  private ICompilationUnit getCompilationUnit(IEditorPart editor)
  {
    IEditorInput input = editor.getEditorInput();
    if (!(input instanceof FileEditorInput)) {
      return null;
    }
    FileEditorInput fileInput = (FileEditorInput)input;
    IFile file = fileInput.getFile();
    IJavaElement element = JavaCore.create(file);
    if (!(element instanceof ICompilationUnit)) {
      return null;
    }
    return (ICompilationUnit)element;
  }
  
  private int getCursorPosition(IEditorPart editor)
    throws BadLocationException
  {
    ITextSelection textSelection = (ITextSelection)editor.getSite().getSelectionProvider().getSelection();
    int offset = textSelection.getOffset();
    return offset;
  }
}
