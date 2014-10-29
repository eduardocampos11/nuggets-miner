package br.com.inf.nuggets.miner.dnd;

import br.com.inf.nuggets.miner.annotations.NuggetsMinerAnnotation;
import br.com.inf.nuggets.miner.annotations.NuggetsMinerAnnotationManager;
import br.com.inf.nuggets.miner.annotations.NuggetsMinerAnnotationParser;
import br.com.inf.nuggets.miner.dialog.EditCommentDialog;
import br.com.inf.nuggets.miner.utils.AlertManager;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.undo.DocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class DocumentDropListener
  implements DropTargetListener
{
  private final ITextEditor editor;
  
  public DocumentDropListener(ITextEditor editor)
  {
    this.editor = editor;
  }
  
  public void dragEnter(DropTargetEvent event) {}
  
  public void dragLeave(DropTargetEvent event) {}
  
  public void dragOperationChanged(DropTargetEvent event) {}
  
  public void dragOver(DropTargetEvent event) {}
  
  public void drop(DropTargetEvent event)
  {
    try
    {
      if (event.data == null)
      {
        event.detail = 0;
        return;
      }
      String data = (String)event.data;
      if (!NuggetsMinerAnnotationParser.isAnnotation(data)) {
        return;
      }
      if (!DocumentDragListener.isDragging()) {
        return;
      }
      ISelectionProvider selectionProvider = this.editor.getSelectionProvider();
      ISelection selection = selectionProvider.getSelection();
      ITextSelection textSelection = (ITextSelection)selection;
      int offset = textSelection.getOffset();
      if (!(selection instanceof ITextSelection)) {
        return;
      }
      IDocumentProvider dp = this.editor.getDocumentProvider();
      IDocument document = dp.getDocument(this.editor.getEditorInput());
      DocumentUndoManager man = new DocumentUndoManager(document);
      IFile file = (IFile)this.editor.getEditorInput().getAdapter(IFile.class);
      if (file == null) {
        return;
      }
      String fileExtension = file.getFileExtension();
      List<NuggetsMinerAnnotation> annotationList = NuggetsMinerAnnotationParser.parse(data, fileExtension);
      if ((annotationList == null) || (annotationList.size() <= 0))
      {
        man.undo();
        return;
      }
      NuggetsMinerAnnotation annotation = (NuggetsMinerAnnotation)annotationList.get(0);
      EditCommentDialog dialog = new EditCommentDialog(PlatformUI.createDisplay().getActiveShell());
      dialog.create();
      annotation.setFieldValue(NuggetsMinerAnnotation.LENGTH_FIELD, Integer.valueOf(data.length()));
      annotation.setFieldValue(NuggetsMinerAnnotation.OFFSET_FIELD, Integer.valueOf(offset));
      man.undo();
      switch (dialog.open())
      {
      case 0: 
        annotation.setFieldValue(NuggetsMinerAnnotation.COMMENT_FIELD, dialog.getComment());
        NuggetsMinerAnnotationManager.getInstance().putAnnotation(document, annotation, offset);
      }
    }
    catch (ExecutionException e)
    {
      AlertManager.showErrorMessage("Error while creating new annotation.", e.getMessage());
      e.printStackTrace();
    }
  }
  
  public void dropAccept(DropTargetEvent event) {}
}
