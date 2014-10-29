package br.com.inf.nuggets.miner.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import br.com.inf.nuggets.miner.partitioner.DocumentPartitioner;

public class GlobalDocumentObserver
{
  private final Map<IDocumentExtension3, ITextEditor> documents;
  private final List<IDocumentChangedListener> documentListeners;
  private final List<IDocumentPartitioningChangedListener> partitioningListeners;
  private final List<IDocumentSaveCommandListener> saveListeners;
  private final PartitioningCache partitionsCache;
  private final List<DocumentPartitioner> partitioners;
  private final IDocumentListener commonDocumentListener;
  private final WindowPartListener windowPartListener;
  
  private class DocumentChangedListener
    implements IDocumentListener
  {
    private final Timer timer = new Timer();
    private TimerTask task;
    private final int delay = 1000;
    
    private DocumentChangedListener() {}
    
    public void documentAboutToBeChanged(DocumentEvent event)
    {
      if (!(event.getDocument() instanceof IDocumentExtension3)) {
        return;
      }
      GlobalDocumentObserver.this.notifyDocumentAboutToBeChanged((IDocumentExtension3)event.getDocument());
    }
    
    public void documentChanged(DocumentEvent event)
    {
      if (this.task != null) {
        this.task.cancel();
      }
      this.task = new GlobalDocumentObserver.DocumentChangedTimeoutTask(GlobalDocumentObserver.this, (IDocumentExtension3)event.getDocument());
      this.timer.schedule(this.task, 1000L);
    }
  }
  
  private class DocumentChangedTimeoutTask
    extends TimerTask
  {
    private final GlobalDocumentObserver observer;
    private final IDocumentExtension3 document;
    
    
    
    public DocumentChangedTimeoutTask(GlobalDocumentObserver observer,
		IDocumentExtension3 document) {
		this.observer = observer;
		this.document = document;
	}

	public void run()
    {
      GlobalDocumentObserver.this.notifyDocumentChanged(this.document);
    }
  }
  
  private class WindowPartListener
    implements IPartListener
  {
    private WindowPartListener() {}
    
    public void partOpened(IWorkbenchPart part)
    {
      if (!(part instanceof IEditorPart)) {
        return;
      }
      if ((part instanceof MultiPageEditorPart))
      {
        MultiPageEditorPart multiPageEditor = (MultiPageEditorPart)part;
        ITextEditor activeEditor = (ITextEditor)multiPageEditor.getAdapter(ITextEditor.class);
        if (activeEditor == null) {
          return;
        }
        GlobalDocumentObserver.this.addEditor(activeEditor);
        return;
      }
      if (!(part instanceof ITextEditor)) {
        return;
      }
      ITextEditor editor = (ITextEditor)part;
      GlobalDocumentObserver.this.addEditor(editor);
    }
    
    public void partClosed(IWorkbenchPart part)
    {
      if (!(part instanceof ITextEditor)) {
        return;
      }
      ITextEditor editor = (ITextEditor)part;
      GlobalDocumentObserver.this.removeDocument(editor);
    }
    
    public void partBroughtToTop(IWorkbenchPart part) {}
    
    public void partActivated(IWorkbenchPart part)
    {
      if (!(part instanceof ITextEditor)) {
        return;
      }
      ITextEditor editor = (ITextEditor)part;
      GlobalDocumentObserver.this.addEditor(editor);
    }
    
    public void partDeactivated(IWorkbenchPart part) {}
  }
  
  public GlobalDocumentObserver()
  {
    this.documentListeners = new ArrayList();
    this.partitioningListeners = new ArrayList();
    this.saveListeners = new ArrayList();
    this.partitioners = new ArrayList();
    this.commonDocumentListener = new DocumentChangedListener();
    this.windowPartListener = new WindowPartListener();
    this.partitionsCache = new PartitioningCache();
    this.documents = new HashMap();
    

    PlatformUI.getWorkbench().addWindowListener(new IWindowListener()
    {
      public void windowOpened(IWorkbenchWindow window)
      {
        window.getPartService().removePartListener(GlobalDocumentObserver.this.windowPartListener);
        window.getPartService().addPartListener(GlobalDocumentObserver.this.windowPartListener);
      }
      
      public void windowClosed(IWorkbenchWindow window)
      {
        window.getPartService().removePartListener(GlobalDocumentObserver.this.windowPartListener);
      }
      
      public void windowDeactivated(IWorkbenchWindow window) {}
      
      public void windowActivated(IWorkbenchWindow window) {}
    });
    addListenerToCurrentWindows();
  }
  
  private void addListenerToCurrentWindows()
  {
    IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    for (int i = 0; i < windows.length; i++) {
      windows[i].getPartService().addPartListener(this.windowPartListener);
    }
  }
  
  private void notifyDocumentChanged(IDocumentExtension3 document)
  {
    for (IDocumentChangedListener l : this.documentListeners)
    {
      ITextEditor editor = (ITextEditor)this.documents.get(document);
      l.onDocumentChanged(editor, document);
    }
  }
  
  private void notifyDocumentAboutToBeChanged(IDocumentExtension3 document)
  {
    for (IDocumentChangedListener l : this.documentListeners)
    {
      ITextEditor editor = (ITextEditor)this.documents.get(document);
      l.onDocumentAboutToBeChanged(editor, document);
    }
  }
  
  private void addAllListenersToDocument(IDocumentExtension3 document)
  {
    for (DocumentPartitioner p : this.partitioners) {
      if (document.getDocumentPartitioner(p.getId()) == null) {}
    }
    ((IDocument)document).addDocumentListener(this.commonDocumentListener);
  }
  
  private void removeAllListenersFromDocument(IDocumentExtension3 document)
  {
    ((IDocument)document).removeDocumentListener(this.commonDocumentListener);
  }
  
  private void addEditor(ITextEditor editor)
  {
    IDocumentProvider dp = editor.getDocumentProvider();
    IDocumentExtension3 document = (IDocumentExtension3)dp.getDocument(editor.getEditorInput());
    if (this.documents.containsKey(document)) {
      return;
    }
    this.documents.put(document, editor);
    addAllListenersToDocument(document);
  }
  
  private void removeDocument(ITextEditor editor)
  {
    IDocumentProvider dp = editor.getDocumentProvider();
    IDocument document = dp.getDocument(editor.getEditorInput());
    
    this.documents.remove(document);
    this.partitionsCache.removePartitions(document);
    removeAllListenersFromDocument((IDocumentExtension3)document);
  }
  
  public void addPartitioningListener(IDocumentPartitioningChangedListener listener)
  {
    if (this.partitioningListeners.contains(listener)) {
      return;
    }
    this.partitioningListeners.add(listener);
  }
  
  public void removePartitioningListener(IDocumentPartitioningChangedListener listener)
  {
    this.partitioningListeners.remove(listener);
  }
  
  public void addDocumentListener(IDocumentChangedListener listener)
  {
    if (this.documentListeners.contains(listener)) {
      return;
    }
    this.documentListeners.add(listener);
  }
  
  public void removeDocumentListener(IDocumentChangedListener listener)
  {
    this.documentListeners.remove(listener);
  }
  
  public void addDocumentPartitioner(DocumentPartitioner partitioner)
  {
    if (this.partitioners.contains(partitioner)) {
      return;
    }
    this.partitioners.add(partitioner);
    for (IDocumentExtension3 doc : this.documents.keySet()) {
      if (doc.getDocumentPartitioner(partitioner.getId()) == null)
      {
        doc.setDocumentPartitioner(partitioner.getId(), partitioner.getPartitionerInstance());
        partitioner.getPartitionerInstance().connect((IDocument)doc);
      }
    }
  }
  
  public void removeDocumentPartitioner(DocumentPartitioner partitioner)
  {
    this.partitioners.remove(partitioner);
    for (IDocumentExtension3 d : this.documents.keySet()) {
      d.setDocumentPartitioner(partitioner.getId(), null);
    }
    partitioner.getPartitionerInstance().disconnect();
  }
  
  public void addDocumentSaveListener(IDocumentSaveCommandListener listener)
  {
    if (this.saveListeners.contains(listener)) {
      return;
    }
    this.saveListeners.add(listener);
  }
  
  public void removeDocumentSaveListener(IDocumentSaveCommandListener listener)
  {
    this.saveListeners.remove(listener);
  }
  
  public HashMap<String, List<ITypedRegion>> getCachedPartitions(IDocument document)
  {
    return this.partitionsCache.getPartitions(document);
  }
  
  public ITextEditor getEditorForDocument(IDocument document)
  {
    if (this.documents.containsKey(document)) {
      return (ITextEditor)this.documents.get(document);
    }
    return null;
  }
}
