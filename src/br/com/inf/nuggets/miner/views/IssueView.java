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

import br.com.inf.nuggets.miner.solr.IssueData;
import br.com.inf.nuggets.miner.visualization.GitHubHTMLRenderer;


public class IssueView
  extends ViewPart
{
  public static final String ID = "br.com.inf.nuggets.miner.views.IssueView";
  private Browser browser;
  private IssueData issueData;
  
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
        IssueView.this.browser.forward();
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
    back.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        if ((!IssueView.this.browser.back()) || (IssueView.this.browser.getText().isEmpty())) {
          if (IssueView.this.issueData != null) {
        	  IssueView.this.setContent(IssueView.this.issueData);
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
  
  public void setContent(final IssueData issue)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
    	IssueView.this.issueData = issue;
        GitHubHTMLRenderer renderer;
		try {
			renderer = new GitHubHTMLRenderer();
//			IssueView.this.browser.setText(renderer.getDocumentHTML(IssueView.this.issueData));
			browser.setUrl(issueData.getCommitUrl());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
      }
    });
  }
}