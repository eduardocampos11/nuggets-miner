package br.com.inf.nuggets.miner.dialog;

import br.com.inf.nuggets.miner.preferences.PreferenceModel;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddDelimiterDialog
  extends TitleAreaDialog
{
  private Text fileExtension;
  private Text openingDelimiter;
  private Text closingDelimiter;
  private final PreferenceModel newDelimiters = new PreferenceModel();
  
  public AddDelimiterDialog(Shell parentShell)
  {
    super(parentShell);
  }
  
  public void create()
  {
    super.create();
    setTitle("Add new delimiter");
    setMessage("define the delimiters for the annotation and the file extension to be used with.");
  }
  
  protected Control createDialogArea(Composite parent)
  {
    GridLayout layout = new GridLayout(2, false);
    

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 4;
    gridData.verticalAlignment = 4;
    
    Composite comp = new Composite(parent, 0);
    comp.setLayout(layout);
    Label extLabel = new Label(comp, 0);
    extLabel.setText("File extension: ");
    this.fileExtension = createTextBox(comp);
    
    Label openLabel = new Label(comp, 0);
    openLabel.setText("Opening Delimiter: ");
    this.openingDelimiter = createTextBox(comp);
    
    Label closeLabel = new Label(comp, 0);
    closeLabel.setText("Closing Delimiter: ");
    this.closingDelimiter = createTextBox(comp);
    
    return parent;
  }
  
  private Text createTextBox(Composite parent)
  {
    Text box = new Text(parent, 2048);
    box.setText("");
    return box;
  }
  
  protected void okPressed()
  {
    this.newDelimiters.setFileExtension(this.fileExtension.getText());
    this.newDelimiters.setOpeningDelimiter(this.openingDelimiter.getText());
    this.newDelimiters.setClosingDelimiter(this.closingDelimiter.getText());
    super.okPressed();
  }
  
  protected void createButtonsForButtonBar(Composite parent)
  {
    GridData gridData = new GridData();
    gridData.verticalAlignment = 4;
    gridData.horizontalSpan = 2;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 16777216;
    
    parent.setLayoutData(gridData);
    createOkButton(parent, 0, "Add", true);
    
    Button cancelButton = createButton(parent, 1, "Cancel", false);
    cancelButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        AddDelimiterDialog.this.setReturnCode(1);
        AddDelimiterDialog.this.close();
      }
    });
  }
  
  protected Button createOkButton(Composite parent, int id, String label, boolean defaultButton)
  {
    ((GridLayout)parent.getLayout()).numColumns += 1;
    Button button = new Button(parent, 8);
    button.setText(label);
    button.setFont(JFaceResources.getDialogFont());
    button.setData(new Integer(id));
    button.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent event)
      {
        AddDelimiterDialog.this.okPressed();
      }
    });
    if (defaultButton)
    {
      Shell shell = parent.getShell();
      if (shell != null) {
        shell.setDefaultButton(button);
      }
    }
    setButtonLayoutData(button);
    return button;
  }
  
  public PreferenceModel getNewDelimiters()
  {
    return this.newDelimiters;
  }
}
