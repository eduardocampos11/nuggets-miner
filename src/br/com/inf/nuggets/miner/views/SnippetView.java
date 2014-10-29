package br.com.inf.nuggets.miner.views;

import java.io.IOException;
import java.net.URISyntaxException;
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

import br.com.inf.nuggets.miner.solr.SnippetData;
import br.com.inf.nuggets.miner.visualization.SnippetHTMLRenderer;


public class SnippetView
  extends ViewPart
{
  public static final String ID = "br.com.inf.nuggets.miner.views.SnippetView";
  private Browser browser;
  private SnippetData snippet;
  
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
    	  SnippetView.this.browser.forward();
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
    back.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        if ((!SnippetView.this.browser.back()) || (SnippetView.this.browser.getText().isEmpty())) {
          if (SnippetView.this.snippet != null) {
        	  SnippetView.this.setContent(SnippetView.this.snippet);
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
  
  public void setContent(final SnippetData snippet)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
    	  SnippetView.this.snippet = snippet;
    	  SnippetHTMLRenderer renderer;
    	  renderer = new SnippetHTMLRenderer();
//			SnippetView.this.browser.setText(renderer.getDocumentHTML(SnippetView.this.snippet));
    	  String postId = snippet.getId();
    	  String postLink = "http://stackoverflow.com/questions/" + postId;		
    	  SnippetView.this.browser.refresh();
    	  SnippetView.this.browser.setUrl(postLink);
    	  SnippetView.this.browser.refresh();
      }
    });
  }
}

