package br.com.inf.nuggets.miner.preferences;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.dialog.AddDelimiterDialog;
import br.com.inf.nuggets.miner.utils.AlertManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class NuggetsMinerPreferencePage
  extends PreferencePage
  implements IWorkbenchPreferencePage
{
  private TableViewer viewer;
  private List<PreferenceModel> entries;
  private Button check;
  private Text authorName;
  
  private final void loadEntries(boolean defaultValues)
  {
    try
    {
      IPreferenceStore store = getPreferenceStore();
      String jsonEntities = defaultValues ? 
        store.getDefaultString(NuggetsMinerPreferencesConstant.NUGGETS_MINER_DELIMITERS) : 
        store.getString(NuggetsMinerPreferencesConstant.NUGGETS_MINER_DELIMITERS);
        this.entries = getDelimitersList(jsonEntities);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      this.entries = new ArrayList<PreferenceModel>();
    }
  }
  
  private static List<PreferenceModel> getDelimitersList(String jsonEntities) {
	  ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        TypeFactory typeFactory = objectMapper.getTypeFactory();
	        CollectionType collectionType = typeFactory.constructCollectionType(
	                                            List.class, PreferenceModel.class);
	        
	        return objectMapper.readValue(jsonEntities, collectionType);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
  }
  
  protected Control createContents(Composite parent)
  {
    createDescription(parent);
    Composite container = new Composite(parent, 0);
    GridLayout layoutData = new GridLayout(2, false);
    container.setLayout(layoutData);
    createTable(container);
    createTableControls(container);
    createAnnotationsAuthorFields(parent);
    return container;
  }
  
  private void createDescription(Composite parent)
  {
    Composite container = new Composite(parent, 0);
    GridLayout layoutData = new GridLayout(1, false);
    container.setLayout(layoutData);
    Label description = new Label(container, 0);
    description.setText(
      "Comment delimiters for file associaton. In order to avoid compilation problems\ndefine delimiters by extending language's multi-line comments construct with additional chars.\n(e.g. java/cpp defaults delimiters or javadoc delimiters)");
  }
  
  private void createAnnotationsAuthorFields(Composite parent)
  {
    final IPreferenceStore store = getPreferenceStore();
    Composite container = new Composite(parent, 0);
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = 4;
    
    GridLayout layoutData = new GridLayout(2, false);
    container.setLayout(layoutData);
    
    Composite authorContainer = new Composite(parent, 0);
    layoutData = new GridLayout(1, false);
    authorContainer.setLayout(layoutData);
    Label authorLabel = new Label(authorContainer, 0);
    authorLabel.setText("Author's name");
    this.authorName = new Text(authorContainer, 2048);
    this.authorName.setText(store.getString(NuggetsMinerPreferencesConstant.ANNOTATION_AUTHOR_NAME));
    this.authorName.setLayoutData(gridData);
    this.check = new Button(container, 32);
    this.check.setSelection(store.getBoolean(NuggetsMinerPreferencesConstant.ANNOTATION_USER_SYSTEM_ENV));
    this.authorName.setEnabled(!this.check.getSelection());
    this.check.addListener(4, new Listener()
    {
      public void handleEvent(Event event)
      {
        String defaultVal = store.getDefaultString(NuggetsMinerPreferencesConstant.ANNOTATION_AUTHOR_NAME);
        NuggetsMinerPreferencePage.this.authorName.setText(defaultVal);
        NuggetsMinerPreferencePage.this.authorName.setEnabled(!NuggetsMinerPreferencePage.this.check.getSelection());
      }
    });
    Label checkLabel = new Label(container, 0);
    checkLabel.setText("Check this box to use system environment user as default @author value in Seahawk annotations.");
  }
  
  private void createTableControls(final Composite parent)
  {
    Composite container = new Composite(parent, 0);
    
    GridLayout layoutData = new GridLayout(1, false);
    container.setLayout(layoutData);
    
    Button removeButton = new Button(container, 2048);
    removeButton.setText("Remove");
    removeButton.addListener(4, new Listener()
    {
      public void handleEvent(Event event)
      {
        IStructuredSelection sel = (IStructuredSelection)NuggetsMinerPreferencePage.this.viewer.getSelection();
        PreferenceModel entry = (PreferenceModel)sel.getFirstElement();
        NuggetsMinerPreferencePage.this.entries.remove(entry);
        NuggetsMinerPreferencePage.this.setInput(NuggetsMinerPreferencePage.this.entries);
      }
    });
    Button addButton = new Button(container, 2048);
    addButton.setText("Add");
    addButton.addListener(4, new Listener()
    {
      public void handleEvent(Event event)
      {
        AddDelimiterDialog dialog = new AddDelimiterDialog(parent.getShell());
        if (dialog.open() == 0)
        {
          if (NuggetsMinerPreferencePage.this.getControl().isDisposed()) {
            return;
          }
          PreferenceModel pref = dialog.getNewDelimiters();
          if (NuggetsMinerPreferencePage.this.entries.contains(pref))
          {
            AlertManager.showWarningMessage(
              "Cannot add delimiters for extension " + pref.getFileExtension() + " since it is already present.");
          }
          else
          {
            NuggetsMinerPreferencePage.this.entries.add(pref);
            NuggetsMinerPreferencePage.this.setInput(NuggetsMinerPreferencePage.this.entries);
          }
        }
      }
    });
  }
  
  private void createTable(Composite parent)
  {
    this.viewer = new TableViewer(parent, 2816);
    this.viewer.setContentProvider(new ArrayContentProvider());
    
    Table table = this.viewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    
    createColumns(this.viewer);
    setInput(this.entries);
  }
  
  private void setInput(List<PreferenceModel> entries)
  {
    this.viewer.setInput(entries);
    this.viewer.refresh();
  }
  
  private void createColumns(TableViewer viewer)
  {
    TableViewerColumn col = new TableViewerColumn(viewer, 0);
    col.getColumn().setWidth(120);
    col.getColumn().setText("File Extension");
    col.setLabelProvider(new ColumnLabelProvider()
    {
      public String getText(Object element)
      {
        return ((PreferenceModel)element).getFileExtension();
      }
    });
    col.setEditingSupport(new CellEditorSupport(viewer)
    {
      protected String getValueForElement(PreferenceModel element)
      {
        return element.getFileExtension();
      }
      
      protected void setValueForElement(PreferenceModel element, String value)
      {
        element.setFileExtension(value);
      }
    });
    col = new TableViewerColumn(viewer, 0);
    col.getColumn().setWidth(120);
    col.getColumn().setText("Opening Delimiter");
    col.setLabelProvider(new ColumnLabelProvider()
    {
      public String getText(Object element)
      {
        return ((PreferenceModel)element).getOpeningDelimiter();
      }
    });
    col.setEditingSupport(new CellEditorSupport(viewer)
    {
      protected String getValueForElement(PreferenceModel element)
      {
        return element.getOpeningDelimiter();
      }
      
      protected void setValueForElement(PreferenceModel element, String value)
      {
        element.setOpeningDelimiter(value);
      }
    });
    col = new TableViewerColumn(viewer, 0);
    col.getColumn().setWidth(120);
    col.getColumn().setText("Closing Delimiter");
    col.setLabelProvider(new ColumnLabelProvider()
    {
      public String getText(Object element)
      {
        return ((PreferenceModel)element).getClosingDelimiter();
      }
    });
    col.setEditingSupport(new CellEditorSupport(viewer)
    {
      protected String getValueForElement(PreferenceModel element)
      {
        return element.getClosingDelimiter();
      }
      
      protected void setValueForElement(PreferenceModel element, String value)
      {
        element.setClosingDelimiter(value);
      }
    });
  }
  
  public void performApply()
  {
    try
    {
      IPreferenceStore store = getPreferenceStore();
      ObjectMapper map = new ObjectMapper();
      map.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
      String json = map.writeValueAsString(this.entries);
      store.setValue(NuggetsMinerPreferencesConstant.NUGGETS_MINER_DELIMITERS, json);
      store.setValue(NuggetsMinerPreferencesConstant.ANNOTATION_USER_SYSTEM_ENV, this.check.getSelection());
      store.setValue(NuggetsMinerPreferencesConstant.ANNOTATION_AUTHOR_NAME, this.authorName.getText());
    }
    catch (JsonGenerationException e)
    {
      e.printStackTrace();
    }
    catch (JsonMappingException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    super.performApply();
  }
  
  public void performDefaults()
  {
    loadEntries(true);
    setInput(this.entries);
    super.performDefaults();
  }
  
  public void init(IWorkbench workbench)
  {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    loadEntries(false);
  }
}
