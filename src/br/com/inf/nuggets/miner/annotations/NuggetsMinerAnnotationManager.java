package br.com.inf.nuggets.miner.annotations;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.observer.GlobalDocumentObserver;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class NuggetsMinerAnnotationManager
{
  private static final NuggetsMinerAnnotationManager instance = new NuggetsMinerAnnotationManager();
  
  private boolean checkAnnotationConsistency(IDocument document, NuggetsMinerAnnotation annotation)
    throws BadLocationException
  {
    Integer offset = (Integer)annotation.getFieldValue(NuggetsMinerAnnotation.OFFSET_FIELD);
    Integer length = (Integer)annotation.getFieldValue(NuggetsMinerAnnotation.LENGTH_FIELD);
    String text = document.get(offset.intValue(), length.intValue());
    String[] delimiters = NuggetsMinerPartitionScanner.getDelimiters(document);
    if (delimiters == null) {
      return false;
    }
    List<NuggetsMinerAnnotation> parsed = NuggetsMinerAnnotationParser.parse(text, delimiters[0], delimiters[1]);
    if (parsed.size() != 1) {
      return false;
    }
    return annotation.equals(parsed.get(0));
  }
  
  public void deleteAnnotation(IDocument document, NuggetsMinerAnnotation annotation)
  {
    try
    {
      if (!checkAnnotationConsistency(document, annotation)) {
        return;
      }
      Integer offset = (Integer)annotation.getFieldValue(NuggetsMinerAnnotation.OFFSET_FIELD);
      Integer length = (Integer)annotation.getFieldValue(NuggetsMinerAnnotation.LENGTH_FIELD);
      if ((offset == null) || (length == null)) {
        return;
      }
      document.replace(offset.intValue(), length.intValue(), "");
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
  }
  
  public void putAnnotation(IDocument document, NuggetsMinerAnnotation annotation, int offset)
  {
    try
    {
//      SeahawkAnnotationPartitioner partitioner = new SeahawkAnnotationPartitioner();
      
      GlobalDocumentObserver docObserver = Activator.getDocumentObserver();
      Map<String, List<ITypedRegion>> partitions = docObserver.getCachedPartitions(document);
//      List<ITypedRegion> regions = (List)partitions.get(partitioner.getId());
      int newOffset = -1;
      

      ITextEditor editor = docObserver.getEditorForDocument(document);
      IEditorInput input = editor.getEditorInput();
      if (input == null) {
        return;
      }
      IFile file = (IFile)input.getAdapter(IFile.class);
      if (file == null) {
        return;
      }
      /*String annotationStr = annotation.buildString(file.getFileExtension());
      for (ITypedRegion r : regions) {
        if (SeahawkPartitionScanner.isSeahawkPartition(r.getType())) {
          if ((offset >= r.getOffset()) && (offset <= r.getOffset() + r.getLength())) {
            newOffset = r.getOffset() + r.getLength() + 1;
          }
        }
      }
      if (annotationStr == null) {
        return;
      }
      if (newOffset == -1) {
        document.replace(offset, 0, annotationStr);
      } else if (newOffset >= document.getLength()) {
        document.set(document.get() + "\n" + annotationStr);
      } else {
        document.replace(newOffset, 0, "\n" + annotationStr);
      } */
    } catch (Exception e)
    {
      e.printStackTrace();
    } 
  }
  
  public void replaceAnnotation(IDocument document, NuggetsMinerAnnotation toReplace, NuggetsMinerAnnotation replacement)
  {
    Integer offset = (Integer)toReplace.getFieldValue(NuggetsMinerAnnotation.OFFSET_FIELD);
    if (offset == null) {
      return;
    }
    deleteAnnotation(document, toReplace);
    try
    {
      ITextEditor editor = Activator.getDocumentObserver().getEditorForDocument(document);
      IFile file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
      if (file == null) {
        return;
      }
      document.replace(offset.intValue(), 0, replacement.buildString(file.getFileExtension()));
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
  }
  
  public static NuggetsMinerAnnotationManager getInstance()
  {
    return instance;
  }
}

