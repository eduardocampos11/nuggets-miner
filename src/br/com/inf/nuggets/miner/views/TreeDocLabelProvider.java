package br.com.inf.nuggets.miner.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import br.com.inf.nuggets.miner.decorator.IDocumentTreeDecorator;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.solr.DocumentBase;


public class TreeDocLabelProvider
  extends StyledCellLabelProvider
{
  private IDocumentTreeDecorator decorator;
  
  public String getToolTipText(Object element)
  {
    String text = this.decorator.getToolTipText(element);
    return (text == null) || (text.isEmpty()) ? super.getToolTipText(element) : text;
  }
  
  protected Image getImage(Object element)
  {
    try
    {
      Bundle b = Platform.getBundle(NuggetsMinerPreferencesConstant.BUNDLE);
      URL url = null;
      if ((element instanceof DocumentBase))
      {
        url = b.getEntry("icons/se_document.png");
      }
      File resFile = new File(FileLocator.toFileURL(url).toURI());
      return new Image(Display.getCurrent(), new FileInputStream(resFile));
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  protected String getText(Object element)
  {
    if ((element instanceof DocumentBase)) {
    	DocumentBase docBase = (DocumentBase) element;
    	String docText = ""; 
    	/*if (docBase.getTitle().length() > 75) {
    		docText += docBase.getTitle().substring(0, 74) + "...";
    	} else {
    		docText += docBase.getTitle();
    	}*/
    	docText += docBase.getTitle();
    	System.out.println("docText: " + docText);
    	return docText;
    } 
    return "Error: DefaultText";
  }
    
  public void update(ViewerCell cell)
  {
    Object element = cell.getElement();
    StyledString prefixDecorations = new StyledString();
    StyledString postDecorations = new StyledString();
    
    StyledString pre = this.decorator.addPrefixDecoration(element);
    StyledString post = this.decorator.addPostfixDecoration(element);
    if (pre != null) {
      prefixDecorations.append(pre);
    }
    if (post != null) {
      postDecorations.append(post);
    }
    StyledString text = new StyledString(getText(element));
    cell.setImage(getImage(element));
    text.append(postDecorations);
    prefixDecorations.append(text);
    cell.setText(prefixDecorations.toString());
    cell.setStyleRanges(prefixDecorations.getStyleRanges());
    super.update(cell);
  }
  
  public void setDecorator(IDocumentTreeDecorator decorator)
  {
    this.decorator = decorator;
  }
  
  public void removeDecorator(IDocumentTreeDecorator decorator)
  {
    this.decorator = decorator;
  }
}
