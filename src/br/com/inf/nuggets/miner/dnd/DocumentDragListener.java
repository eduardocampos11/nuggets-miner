package br.com.inf.nuggets.miner.dnd;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import br.com.inf.nuggets.miner.solr.DocumentBase;

public class DocumentDragListener
  implements DragSourceListener
{
  private ColumnViewer viewer;
  private static boolean isDragging;
  
  public DocumentDragListener(ColumnViewer viewer)
  {
    setViewer(viewer);
  }
  
  public void dragStart(DragSourceEvent event)
  {
    IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
    Object obj = selection.getFirstElement();
    if ((!(obj instanceof DocumentBase)) /*&& (!(obj instanceof CodeSnippetNode))*/)
    {
      event.doit = false;
      return;
    }
    isDragging = true;
  }
  
  public void dragSetData(DragSourceEvent event)
  {
    if (TextTransfer.getInstance().isSupportedType(event.dataType))
    {
      IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
      Object obj = selection.getFirstElement();
      if ((obj instanceof DocumentBase))
      {
        event.data = copySnippets((DocumentBase)obj);
        return;
      }
      /*if ((obj instanceof CodeSnippetNode))
      {
        event.data = ((CodeSnippetNode)obj).getBody();
        return;
      }*/
    }
  }
  
  public void dragFinished(DragSourceEvent event)
  {
    isDragging = false;
  }
 
  private String copySnippets(DocumentBase document)
  {
	  String questionCode = getCodigoPreCode(document.getQuestionBody());
	  String answerCode = getCodigoPreCode(document.getAnswerBody());
	  String copyText = "";
	  
	  if (questionCode != null && !questionCode.equals("")) {
		  copyText = questionCode + "\n";
	  }
	  
	  if (answerCode != null && !answerCode.equals("")) {
		  copyText = copyText + answerCode + "\n";
	  }
	  
	  if (copyText.equals("")) {
		  return "This StackOverflow document has no code!";
	  } else {
		  return replaceHtmlCharacters(copyText);		  
	  }
  }
  
  //metodo responsavel por pegar o conteudo de todas as tags "pre/code" do corpo de um post
  public String getCodigoPreCode(String texto) {
	  String textoCodigos = "";
	  try {
		  HtmlCleaner cleaner = new HtmlCleaner();
		  TagNode root = cleaner.clean( texto );
		  Object[] codeNodes = root.evaluateXPath("//pre/code");
		  for (int i=0; i<codeNodes.length; i++) {
			  textoCodigos += ((TagNode)codeNodes[i]).getText();
		  }
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
	  return textoCodigos;
  }
  
  public String replaceHtmlCharacters(String text) {
	  return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "");
  }
  
  public ColumnViewer getViewer()
  {
    return this.viewer;
  }
  
  public void setViewer(ColumnViewer viewer)
  {
    this.viewer = viewer;
  }
  
  public static boolean isDragging()
  {
    return isDragging;
  }
}
