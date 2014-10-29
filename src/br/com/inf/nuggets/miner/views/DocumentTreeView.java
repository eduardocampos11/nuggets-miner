package br.com.inf.nuggets.miner.views;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import br.com.inf.nuggets.miner.decorator.IDocumentTreeDecorator;
import br.com.inf.nuggets.miner.dnd.DocumentDragListener;
import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.solr.IQueryIssueResultReadyHandler;
import br.com.inf.nuggets.miner.solr.IQueryResultReadyHandler;
import br.com.inf.nuggets.miner.solr.IssueData;
import br.com.inf.nuggets.miner.solr.SolrManager;
import br.com.inf.nuggets.miner.utils.AlertManager;
import br.com.inf.nuggets.miner.utils.ManipuladorDeTexto;


public class DocumentTreeView
  extends ViewPart
{
  public static final String ID = "br.com.inf.nuggets.miner.views.DocumentTreeView";
  private TreeViewer docTreeViewer;
  private TreeViewer issueTreeViewer;
  private List<DocumentBase> docBaseEntries;
  private List<IssueData> issuesEntries;
  private Text searchText;
  private Button searchButton;
  private final TreeDocLabelProvider treeDocLabelProvider;
  private final TreeIssueLabelProvider treeIssueLabelProvider;
  private final TreeContentProvider docContentProvider;
  private final IssueTreeContentProvider issueContentProvider;
	
  public DocumentTreeView()
  {
    this.treeDocLabelProvider = new TreeDocLabelProvider();
    this.treeIssueLabelProvider = new TreeIssueLabelProvider();
    this.docContentProvider = new TreeContentProvider();
    this.issueContentProvider = new IssueTreeContentProvider();
    
    this.treeDocLabelProvider.setDecorator(new IDocumentTreeDecorator()
    {
      public StyledString addPrefixDecoration(Object element)
      {
        return null;
      }
      
      public StyledString addPostfixDecoration(Object element)
      {
        if ((element instanceof DocumentBase))
        {
          DocumentBase doc = (DocumentBase)element;
          String tags = doc.getTags();
          String tagsVet[] = tags.split(",");
          String tagName = null;
          
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < tagsVet.length; i++) {
          	tagName = tagsVet[i];
          	sb.append("<" + tagName + ">");
          }
          return new StyledString(" (Tags: " + sb.toString() + ")", StyledString.COUNTER_STYLER);
        }
        return new StyledString();
      }

	@Override
	public String getToolTipText(Object element) {
		if ((element instanceof DocumentBase)) {
			DocumentBase doc = (DocumentBase)element;
			String tooltip = "Score: " + formatDouble(doc.getScoreFinal());
			return tooltip;
		}
		return "";
	  }
    });
    
    this.treeIssueLabelProvider.setDecorator(new IDocumentTreeDecorator() {

		@Override
		public StyledString addPrefixDecoration(Object element) {
			return new StyledString();
		}

		@Override
		public StyledString addPostfixDecoration(Object element) {
			return new StyledString();
		}

		@Override
		public String getToolTipText(Object element) {
			if ((element instanceof IssueData)) {
				IssueData issue = (IssueData) element;
				String date = formatDate(issue.getIssueClosedDate());
				String tooltip = "Score: " + formatDouble(issue.getScoreSolr()) + "\nClosed in: " + date;
				return tooltip;
			}
			return "";
		}
    });
  }
  
  protected static String formatDouble(Double value) {
	  DecimalFormat df = new DecimalFormat("0.####");  
	  String formattedValue = df.format(value);  
	  return formattedValue.replaceAll(",", ".");
  }
  
  protected static String formatDate(String dateStr) {
	Date date = null;
	String oldDate = null;
	String newDate = null;
	try {
		//Parse date to format yyyy-MM-dd HH:mm:ss.SSS
		date = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(dateStr);
		
		String oldFormat = "yyyy-MM-dd HH:mm:ss.SSS";
		String newFormat = "dd-MM-yyyy HH:mm:ss.SSS";
		
	    DateFormat sdf1 = new SimpleDateFormat(oldFormat);
	    DateFormat sdf2 = new SimpleDateFormat(newFormat);
		
	    oldDate = sdf1.format(date);
	    newDate = sdf2.format(sdf1.parse(oldDate)).split("\\.")[0];
	    
	} catch (ParseException e) {
		e.printStackTrace();
	}
	return newDate;
  }
  
  
  
  private void doQuery(String query)
  {
    try
    {
      toggleSearchButton();
      SolrManager queryMan = SolrManager.getInstance();
      
      ManipuladorDeTexto textManipulator = new ManipuladorDeTexto();
      query = textManipulator.removeStopWordsEFazStemming(query);
      
      queryMan.doSearch(query, new IQueryResultReadyHandler()
      {
        public void onSuccess(Map<String, DocumentBase> result)
        {
//          DocumentTreeView.this.resetView();
          List<DocumentBase> docs = new ArrayList<DocumentBase>();
          for (DocumentBase doc : result.values()) {
            docs.add(doc);
          }
          DocumentTreeView.this.setDocInput(docs);
          DocumentTreeView.this.toggleSearchButton();
        }
        
        public void onError(Exception e)
        {
          DocumentTreeView.this.resetView();
          DocumentTreeView.this.toggleSearchButton();
          e.printStackTrace();
        }
      });
      
      queryMan.doSearch(query, new IQueryIssueResultReadyHandler()
      {
    	  @Override
    	  public void onSuccess(Map<String, IssueData> result) {
    		  List<IssueData> issues = new ArrayList<IssueData>();
    		  for (IssueData issue : result.values()) {
    			  issues.add(issue);
    		  }
    		  DocumentTreeView.this.setIssueInput(issues);
    		  DocumentTreeView.this.toggleSearchButton();
    	  }
    	  
    	  public void onError(Exception e) {
    		  DocumentTreeView.this.resetView();
    		  DocumentTreeView.this.toggleSearchButton();
    		  e.printStackTrace();
    	  }
      }); 
    }
    catch (MalformedURLException e)
    {
      AlertManager.showErrorMessage("Error while contacting Solr. Check Solr URL and port.", e.getMessage());
      e.printStackTrace();
    }
  }
  
  private void createSearchBox(Composite parent)
  {
    GridLayout layout = new GridLayout(2, false);
    parent.setLayout(layout);
    this.searchText = new Text(parent, 128);
    this.searchButton = new Button(parent, 524288);
    this.searchButton.setText("Search");
    this.searchButton.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        if (DocumentTreeView.this.searchText.getText().length() > 0) {
          DocumentTreeView.this.doQuery(DocumentTreeView.this.searchText.getText());
        }
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
    this.searchText.addKeyListener(new KeyListener()
    {
      public void keyReleased(KeyEvent e) {}
      
      public void keyPressed(KeyEvent e)
      {
        if ((e.keyCode == 13) && (DocumentTreeView.this.searchText.getText().length() > 0)) {
          DocumentTreeView.this.doQuery(DocumentTreeView.this.searchText.getText());
        }
      }
    });
    this.searchText.setLayoutData(new GridData(768));
  }
  
  private void createDocTreeViewer(Composite parent)
  {
    this.docTreeViewer = new TreeViewer(parent, 1);
    this.docTreeViewer.setLabelProvider(this.treeDocLabelProvider);
    this.docTreeViewer.setContentProvider(this.docContentProvider);
    
    GridData gridData = new GridData(2144);
    gridData.verticalAlignment = 4;
    gridData.horizontalSpan = 2; // SO
//    gridData.horizontalSpan = 1; // SO + GH
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 4;
    this.docTreeViewer.getControl().setLayoutData(gridData);
    
    this.docTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      Object lastSelected;
      
      public void selectionChanged(SelectionChangedEvent event)
      {
        if (!(event.getSelection() instanceof IStructuredSelection)) {
          return;
        }
        IStructuredSelection sel = (IStructuredSelection)event.getSelection();
        
        Object obj = sel.getFirstElement();
        if ((obj == null) || (obj.equals(this.lastSelected))) {
          return;
        }
        this.lastSelected = obj;
        if ((obj instanceof DocumentBase)) {
          DocumentTreeView.this.setBrowserContent((DocumentBase)obj);
        }
      }
    });
    Transfer[] transferTypes = { TextTransfer.getInstance() };
    DocumentDragListener dragListener = new DocumentDragListener(this.docTreeViewer);
    this.docTreeViewer.addDragSupport(1, transferTypes, dragListener);
    
    ColumnViewerToolTipSupport.enableFor(this.docTreeViewer);
  }
  
  //GH
  private void createIssueTreeViewer(Composite parent) 
  {
	  this.issueTreeViewer = new TreeViewer(parent, 1);
	  this.issueTreeViewer.setLabelProvider(this.treeIssueLabelProvider);
	  this.issueTreeViewer.setContentProvider(this.issueContentProvider);
	    
	  GridData gridData = new GridData(2144);
	  gridData.verticalAlignment = 4;
	  gridData.horizontalSpan = 2; // SO
//	  gridData.horizontalSpan = 1; // SO + GH
	  gridData.grabExcessHorizontalSpace = true;
	  gridData.grabExcessVerticalSpace = true;
	  gridData.horizontalAlignment = 4;
	  this.issueTreeViewer.getControl().setLayoutData(gridData);

	  this.issueTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
	  {
		  Object lastSelected;

		  public void selectionChanged(SelectionChangedEvent event)
		  {
			  if (!(event.getSelection() instanceof IStructuredSelection)) {
				  return;
			  }
			  IStructuredSelection sel = (IStructuredSelection)event.getSelection();

			  Object obj = sel.getFirstElement();
			  if ((obj == null) || (obj.equals(this.lastSelected))) {
				  return;
			  }
			  this.lastSelected = obj;
			  if ((obj instanceof IssueData)) {
				  DocumentTreeView.this.setBrowserContent((IssueData)obj);
			  }
		  }
	  });
	  Transfer[] transferTypes = { TextTransfer.getInstance() };
	  DocumentDragListener dragListener = new DocumentDragListener(this.issueTreeViewer);
	  this.issueTreeViewer.addDragSupport(1, transferTypes, dragListener);

	  ColumnViewerToolTipSupport.enableFor(this.issueTreeViewer);
  }
  
  public void createPartControl(Composite parent)
  {
    createSearchBox(parent);
    createDocTreeViewer(parent);
    createIssueTreeViewer(parent); //GH
  }
  
  public void setFocus()
  {
    this.searchText.setFocus();
  }
  
  public void setQueryString(final String query)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        DocumentTreeView.this.searchText.setText(query);
      }
    });
  }
  
  private void resetView()
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        DocumentTreeView.this.docTreeViewer.setInput(new ArrayList<DocumentBase>());
        DocumentTreeView.this.issueTreeViewer.setInput(new ArrayList<IssueData>());
      }
    });
  }
  
  public void setDocInput(final List<DocumentBase> documents)
  {
    System.out.println("documents size: " + documents.size());
	System.out.println("documents list: " + documents);
	
	// more code
	Job job = new Job("My Job") {
	  @Override
	  protected IStatus run(IProgressMonitor monitor) {
	    // If you want to update the UI
		  Display.getDefault().asyncExec(new Runnable() {
	      @Override
	      public void run() {
	    	  System.out.println("Entrou no run 1..................");
	        // do something in the user interface
	        // e.g. set a text field
	    	if (Display.getDefault().isDisposed()) {
	            return;
	        }
	        DocumentTreeView.this.docBaseEntries = documents;
	        System.out.println("docBaseEntries = " + docBaseEntries);
	        if (DocumentTreeView.this.docTreeViewer == null || documents.size() == 0) {
	        	System.out.println("documentTreeViewer is null!!");
	        	String msg = "Nenhum resultado foi encontrado para o Stack Overflow!";
	            MessageDialog.openInformation(null, "Erro", msg);
	        } else {
	        	System.out.println("docTreeViewer is not null!!! documents size: " + documents.size());
	        	DocumentTreeView.this.docTreeViewer.setInput(documents);	    
	        }	        
	      }
	    });
	    return Status.OK_STATUS;
	  }
	};

	// Start the Job
	job.schedule();
  }
  
  //GH
  public void setIssueInput(final List<IssueData> issues)
  {
    System.out.println("issues size: " + issues.size());
	System.out.println("issues list: " + issues);
	
	// more code
	Job job = new Job("My Job-Issue") {
	  @Override
	  protected IStatus run(IProgressMonitor monitor) {
	    // If you want to update the UI
		  Display.getDefault().asyncExec(new Runnable() {
	      @Override
	      public void run() {
	    	  System.out.println("Entrou no run 1..................");
	        // do something in the user interface
	        // e.g. set a text field
	    	if (Display.getDefault().isDisposed()) {
	            return;
	        }
	    	DocumentTreeView.this.issuesEntries = issues;
	        System.out.println("issuesEntries = " + issuesEntries);
	        if (DocumentTreeView.this.issueTreeViewer == null || issues.size() == 0) {
	        	System.out.println("issueTreeViewer is null!!");
	        	String msg = "Nenhum resultado foi encontrado para o GitHub!";
	        	MessageDialog.openInformation(null, "Erro", msg);
	        } else {
	        	System.out.println("IssueViewer is not null!!! issues size: " + issues.size());
	        	DocumentTreeView.this.issueTreeViewer.setInput(issues);
	        }	        
	      }
	    });
	    return Status.OK_STATUS;
	  }
	};

	// Start the Job
	job.schedule();
  }
  
  private void toggleSearchButton()
  {
	// more code
	Job job = new Job("My Job 2") {
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// If you want to update the UI
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					DocumentTreeView.this.searchButton.setEnabled(!DocumentTreeView.this.searchButton.getEnabled());
				}
			});
			return Status.OK_STATUS;
		}
	};

	// Start the Job
	job.schedule();   
  }
  
  public void performExternalQuery(String query)
  {
    this.searchText.setText(query);
    doQuery(query);
  }
  
  private void setBrowserContent(DocumentBase doc)
  {
    DocumentView view = (DocumentView)PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow().getActivePage().findView("br.com.inf.nuggets.miner.views.DocumentView");
    if (view == null) {
      return;
    }
    view.setContent(doc);
    view.showBusy(false);
  }
  
  private void setBrowserContent(IssueData issue)
  {
    IssueView view = (IssueView)PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow().getActivePage().findView("br.com.inf.nuggets.miner.views.IssueView");
    if (view == null) {
      return;
    }
    view.setContent(issue);
    view.showBusy(false);
  }
}
