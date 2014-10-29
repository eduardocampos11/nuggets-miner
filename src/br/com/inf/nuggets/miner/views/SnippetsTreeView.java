package br.com.inf.nuggets.miner.views;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import br.com.inf.nuggets.miner.decorator.IDocumentTreeDecorator;
import br.com.inf.nuggets.miner.dnd.DocumentDragListener;
import br.com.inf.nuggets.miner.solr.IQuerySnippetReadyHandler;
import br.com.inf.nuggets.miner.solr.SnippetData;
import br.com.inf.nuggets.miner.solr.SolrManager;
import br.com.inf.nuggets.miner.utils.AlertManager;
import br.com.inf.nuggets.miner.utils.ScoreSolrComparatorForSnippets;

public class SnippetsTreeView
  extends ViewPart
{
  public static final String ID = "br.com.inf.nuggets.miner.views.SnippetsTreeView";
  private TreeViewer snippetTreeViewer;
  private List<SnippetData> snippetsEntries;
  private Text searchText;
  private Text crowdBugText;
  private Label languagesLb;
  private Label crowdBugLb;
  private Combo comboLanguage;
  private Button searchButton;
  private final TreeSnippetLabelProvider treeSnippetLabelProvider;  
  private final SnippetTreeContentProvider snippetContentProvider;
  
  @SuppressWarnings("serial")
  private static final List<String> LANGUAGES = new ArrayList<String>() {{
//	  add("C#");
//	  add("C++");
	  add("Java");
	  add("Javascript");
  }};
	
  public SnippetsTreeView()
  {
	this.treeSnippetLabelProvider = new TreeSnippetLabelProvider();
	this.snippetContentProvider = new SnippetTreeContentProvider(); 
	
	this.treeSnippetLabelProvider.setDecorator(new IDocumentTreeDecorator()
    {
      public StyledString addPrefixDecoration(Object element)
      {
        return null;
      }
      
      public StyledString addPostfixDecoration(Object element)
      {
        if ((element instanceof SnippetData)) {
          SnippetData snippet = (SnippetData) element;
          String tags = snippet.getTags();
          return new StyledString(" (Tags: " + tags + ")", StyledString.COUNTER_STYLER);
        }
        return new StyledString();
      }

	@Override
	public String getToolTipText(Object element) {
		if ((element instanceof SnippetData)) {
			SnippetData snippet = (SnippetData)element;
			String tooltip = "Score: " + formatDouble(snippet.getScoreSolr());
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
  
  private void doQuery(String query, String language)
  {
    try
    {
      String crowdBugStr = this.crowdBugText.getText();	
      toggleSearchButton();
      SolrManager queryMan = SolrManager.getInstance();
      
//      queryMan.doSearch(query, crowdBugStr, language, new IQuerySnippetReadyHandler()
      System.out.println("query: " + query);
      queryMan.doSearch(query, language, new IQuerySnippetReadyHandler() {
		
		@Override
		public void onSuccess(Map<String, SnippetData> resultMap) {
			List<SnippetData> snippets = new ArrayList<SnippetData>();
			for (SnippetData snippet : resultMap.values()) {
				snippets.add(snippet);
			}
			Collections.sort(snippets, new ScoreSolrComparatorForSnippets());
			List<String> listPostTitles = new ArrayList<String>();
			
			for (SnippetData snippet : snippets) {
				System.out.println("response postId: " + snippet.getId()+ " score: " + snippet.getScoreSolr());
				listPostTitles.add(snippet.getPostTitle());
			}
			
			System.out.println("verificando os resultados...................");
			System.out.println("snippetsLst size: " + snippets.size());
			
			String[] result = new String[2];
			boolean found = false;
			int page = 1;
			int size = snippets.size();
			for (String title : listPostTitles) {
				// si le post est retrouve  on renseigne le rang
				if("Why the result of Integer.toBinaryString for short type argument includes 32 bits?".equals(title)){
					System.out.println("rang : " + ((listPostTitles.indexOf(title)+1) + (size*(page-1))));
					AlertManager.showErrorMessage("rang: " + ((listPostTitles.indexOf(title)+1) + (size*(page-1))));
					result[1] = (listPostTitles.indexOf(title)+1) + (size*(page-1))+ "";
					found = true;
					break;
				}
			}
			if (!found) {
				AlertManager.showErrorMessage("rang > 1000");
			}

			SnippetsTreeView.this.setSnippetsInput(snippets);
	        SnippetsTreeView.this.toggleSearchButton();
		}
		
		@Override
		public void onError(Exception e) {
			AlertManager.showErrorMessage("" + e.getMessage());
			SnippetsTreeView.this.resetView();
        	SnippetsTreeView.this.toggleSearchButton();
        	e.printStackTrace();
		}
      });
    } catch (MalformedURLException e) {
      AlertManager.showErrorMessage("Error while contacting Solr. Check Solr URL and port.", e.getMessage());
      e.printStackTrace();
    }
  }
  
  private void createSearchBox(Composite parent)
  {
//    GridLayout layout = new GridLayout(3, false);
//    GridLayout layout = new GridLayout(4, false);
	  GridLayout layout = new GridLayout(6, false);
    parent.setLayout(layout);

    // Create a multiple-line text field
    this.searchText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.RESIZE | SWT.V_SCROLL);
    this.searchText.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.searchText.setEditable(true);
    this.searchText.setSize(1000, 300);
    
    this.crowdBugLb = new Label(parent, SWT.READ_ONLY);
    this.crowdBugLb.setLocation(80, 20);
    this.crowdBugLb.setText("Breakpoint:");
    
    this.crowdBugText = new Text(parent, 128);
    this.crowdBugText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    this.crowdBugText.setEditable(true);
    this.crowdBugText.setSize(1000, 50);

    this.languagesLb = new Label(parent, SWT.READ_ONLY);
    this.languagesLb.setLocation(80, 20);
    this.languagesLb.setText("Languages:");
    
    this.comboLanguage = new Combo(parent, SWT.VERTICAL |
    		   SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
    
    populateComboLanguage(this.comboLanguage, LANGUAGES);
    addComboLanguageSelectionEvent(this.comboLanguage);

    this.searchButton = new Button(parent, 524288);
    this.searchButton.setText("Search");
    

    
    this.searchButton.addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        if (SnippetsTreeView.this.searchText.getText().length() > 0) {
        	SnippetsTreeView.this.doQuery(SnippetsTreeView.this.searchText.getText(),
        	  SnippetsTreeView.this.comboLanguage.getItem(comboLanguage.getSelectionIndex()));
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
        if ((e.keyCode == 13) && (SnippetsTreeView.this.searchText.getText().length() > 0)) {
//        	SnippetsTreeView.this.doQuery(SnippetsTreeView.this.searchText.getText());
        	SnippetsTreeView.this.doQuery(SnippetsTreeView.this.searchText.getText(),
              	  SnippetsTreeView.this.comboLanguage.getItem(comboLanguage.getSelectionIndex()));
        	
        }
      }
    });
//    this.searchText.setLayoutData(new GridData(768));
  }
  
  private void populateComboLanguage(Combo combo, List<String> languages) {
	  combo.setText("Language");
	  for (String language : languages) {
		  combo.add(language);
	  }
  }
  
  private void addComboLanguageSelectionEvent(final Combo combo) {
	  combo.addSelectionListener(new SelectionAdapter() {
	    	
		  public void widgetSelected(SelectionEvent e) {
	    	String language = combo.getText();
	    	System.out.println("selectedLanguage: " + language);
		  }
	  });
  }
  
  private void createDocTreeViewer(Composite parent)
  {
    this.snippetTreeViewer = new TreeViewer(parent, 1);
    this.snippetTreeViewer.setLabelProvider(this.treeSnippetLabelProvider);
    this.snippetTreeViewer.setContentProvider(this.snippetContentProvider);
    
    GridData gridData = new GridData(2144);
    gridData.verticalAlignment = 4;
//    gridData.horizontalSpan = 2;
    gridData.horizontalSpan = 6;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 4;
    this.snippetTreeViewer.getControl().setLayoutData(gridData);
    
    this.snippetTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
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
        if ((obj instanceof SnippetData)) {
        	SnippetsTreeView.this.setBrowserContent((SnippetData) obj);
        }
      }
    });
    Transfer[] transferTypes = { TextTransfer.getInstance() };
    DocumentDragListener dragListener = new DocumentDragListener(this.snippetTreeViewer);
    this.snippetTreeViewer.addDragSupport(1, transferTypes, dragListener);
    
    ColumnViewerToolTipSupport.enableFor(this.snippetTreeViewer);
  }
    
  public void createPartControl(Composite parent)
  {
    createSearchBox(parent);
    createDocTreeViewer(parent);
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
    	  SnippetsTreeView.this.searchText.setText(query);
      }
    });
  }
  
  private void resetView()
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
    	  SnippetsTreeView.this.snippetTreeViewer.setInput(new ArrayList<SnippetData>());
      }
    });
  }
  
  public void setSnippetsInput(final List<SnippetData> snippets)
  {
//    System.out.println("snippets size: " + snippets.size());
//	System.out.println("snippets list: " + snippets);
	
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
	    	SnippetsTreeView.this.snippetsEntries = snippets;
	        System.out.println("snippetsEntries = " + snippetsEntries);
	        if (SnippetsTreeView.this.snippetTreeViewer == null || snippets.size() == 0) {
	        	System.out.println("snippetsTreeViewer is null!!");
	        	String msg = "Nenhum resultado foi encontrado para o Stack Overflow!";
	            MessageDialog.openInformation(null, "Erro", msg);
	        } else {
	        	System.out.println("snippetsTreeViewer is not null!!! snippets size: " + snippets.size());
	        	SnippetsTreeView.this.snippetTreeViewer.setInput(snippets);	    
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
					SnippetsTreeView.this.searchButton.setEnabled(!SnippetsTreeView.this.searchButton.getEnabled());
				}
			});
			return Status.OK_STATUS;
		}
	};

	// Start the Job
	job.schedule();   
  }
  
  private void setBrowserContent(SnippetData snippet)
  {
    SnippetView view = (SnippetView) PlatformUI.getWorkbench()
      .getActiveWorkbenchWindow().getActivePage().findView("br.com.inf.nuggets.miner.views.SnippetView");
    if (view == null) {
      return;
    }
    view.setContent(snippet);
    view.showBusy(false);
  }
}
