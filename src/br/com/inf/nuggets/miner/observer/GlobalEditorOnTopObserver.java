package br.com.inf.nuggets.miner.observer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class GlobalEditorOnTopObserver
{
  private final List<IEditorOnTopListener> listeners;
  private final WindowPartListener windowPartListener;
  private IEditorPart editorOnTop;
  
  private class WindowPartListener
    implements IPartListener
  {
    private WindowPartListener() {}
    
    public void partOpened(IWorkbenchPart part) {}
    
    public void partClosed(IWorkbenchPart part)
    {
      if (!(part instanceof IEditorPart)) {
        return;
      }
      IEditorPart editor = (IEditorPart)part;
      GlobalEditorOnTopObserver.this.editorClosed(editor);
    }
    
    public void partBroughtToTop(IWorkbenchPart part)
    {
      if (!(part instanceof IEditorPart)) {
        return;
      }
      IEditorPart editor = (IEditorPart)part;
      GlobalEditorOnTopObserver.this.setEditorOnTop(editor);
    }
    
    public void partActivated(IWorkbenchPart part) {}
    
    public void partDeactivated(IWorkbenchPart part) {}
  }
  
  private class WindowsListener
    implements IWindowListener
  {
    private WindowsListener() {}
    
    public void windowOpened(IWorkbenchWindow window)
    {
      window.getPartService().addPartListener(GlobalEditorOnTopObserver.this.windowPartListener);
    }
    
    public void windowClosed(IWorkbenchWindow window)
    {
      window.getPartService().removePartListener(GlobalEditorOnTopObserver.this.windowPartListener);
    }
    
    public void windowDeactivated(IWorkbenchWindow window)
    {
      window.getPartService().removePartListener(GlobalEditorOnTopObserver.this.windowPartListener);
    }
    
    public void windowActivated(IWorkbenchWindow window)
    {
      window.getPartService().addPartListener(GlobalEditorOnTopObserver.this.windowPartListener);
    }
  }
  
  public GlobalEditorOnTopObserver()
  {
    this.listeners = new ArrayList();
    this.windowPartListener = new WindowPartListener();
    PlatformUI.getWorkbench().addWindowListener(new WindowsListener());
  }
  
  public IEditorPart getEditorOnTop()
  {
    if (this.editorOnTop != null) {
      return this.editorOnTop;
    }
    IEditorPart part = PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    if ((part instanceof IEditorPart)) {
      return part;
    }
    return null;
  }
  
  private void setEditorOnTop(IEditorPart editor)
  {
    if (editor.equals(this.editorOnTop)) {
      return;
    }
    this.editorOnTop = editor;
    for (IEditorOnTopListener e : this.listeners) {
      e.editorOnTopChanged(editor);
    }
  }
  
  private void editorClosed(IEditorPart editor)
  {
    if ((editor == null) || (!editor.equals(this.editorOnTop))) {
      return;
    }
    for (IEditorOnTopListener e : this.listeners) {
      e.editorOnTopClosed(editor);
    }
  }
  
  public void addEditorOnTopListener(IEditorOnTopListener listener)
  {
    if (this.listeners.contains(listener)) {
      return;
    }
    this.listeners.add(listener);
  }
  
  public void removeEditorOnTopListener(IEditorOnTopListener listener)
  {
    this.listeners.remove(listener);
  }
}
