package br.com.inf.nuggets.miner.observer;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class IDocumentSaveCommandListener
{
  private class SaveCommandListener
    implements IExecutionListener
  {
    private final IDocument document;
    private final String saveCommandId = "org.eclipse.ui.file";
    
    public SaveCommandListener(IDocument document)
    {
      this.document = document;
    }
    
    public void notHandled(String commandId, NotHandledException exception) {}
    
    public void postExecuteFailure(String commandId, ExecutionException exception) {}
    
    public void postExecuteSuccess(String commandId, Object returnValue) {}
    
    public void preExecute(String commandId, ExecutionEvent event)
    {
      if (commandId.equals("org.eclipse.ui.file")) {
        IDocumentSaveCommandListener.this.onSaveCommand(this.document);
      }
    }
  }
  
  public IDocumentSaveCommandListener(ITextEditor editor)
  {
    ICommandService commandService = (ICommandService)editor.getSite().getService(ICommandService.class);
    IDocumentProvider dp = editor.getDocumentProvider();
    IDocument doc = dp.getDocument(editor.getEditorInput());
    commandService.addExecutionListener(new SaveCommandListener(doc));
  }
  
  public abstract void onSaveCommand(IDocument paramIDocument);
}
