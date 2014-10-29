package br.com.inf.nuggets.miner.views;

import java.io.IOException;
import java.net.URISyntaxException;

import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.visualization.StackExchangeHTMLRenderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.part.ViewPart;

public class DocumentView
  extends ViewPart
{
  public static final String ID = "br.com.inf.nuggets.miner.views.DocumentView";
  private Browser browser;
  private DocumentBase document;
  
  public void createPartControl(Composite parent)
  {
    Composite page = new Composite(parent, 0);
    GridLayout layout = new GridLayout(1, false);
    page.setLayout(layout);
    
    createControls(page);
    createBrowser(page);
  }
  
  private void createControls(Composite parent)
  {
    Composite page = new Composite(parent, 0);
    Layout layout = new RowLayout(256);
    RowData rowData = new RowData(40, 20);
    Button back = new Button(page, 16388);
    Button forward = new Button(page, 131076);
    page.setLayout(layout);
    back.setLayoutData(rowData);
    forward.setLayoutData(rowData);
    forward.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        DocumentView.this.browser.forward();
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
    back.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        if ((!DocumentView.this.browser.back()) || (DocumentView.this.browser.getText().isEmpty())) {
          if (DocumentView.this.document != null) {
            DocumentView.this.setContent(DocumentView.this.document);
          } /*else {
            DocumentView.this.setContent(DocumentView.this.node);
          }*/
        }
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
  }
  
  private void createBrowser(Composite parent)
  {
    GridData layout = new GridData();
    layout.horizontalAlignment = 4;
    layout.grabExcessHorizontalSpace = true;
    layout.verticalAlignment = 4;
    layout.grabExcessVerticalSpace = true;
//    this.browser = new Browser(parent, 65536);
    this.browser = new Browser(parent, SWT.NATIVE);
    this.browser.setLayoutData(layout);
  }
  
  public void setFocus() {}
  
  public Browser getBrowser()
  {
    return this.browser;
  }
  
  public void setContent(final DocumentBase doc)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        DocumentView.this.document = doc;
//        DocumentView.this.node = null;
        StackExchangeHTMLRenderer renderer;
		try {
			renderer = new StackExchangeHTMLRenderer();
			DocumentView.this.browser.setText(renderer.getDocumentHTML(DocumentView.this.document));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
      }
    });
  }
  
  /*public void setContent(final DocumentNode docNode)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          DocumentView.this.document = null;
          DocumentView.this.node = docNode;
          StackExchangeHTMLRenderer renderer = new StackExchangeHTMLRenderer();
          DocumentView.this.browser.setText(renderer.getDocumentNodeHTML(DocumentView.this.node));
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
          e.printStackTrace();
        }
      }
    });
  }*/
}
