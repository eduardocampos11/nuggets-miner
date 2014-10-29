package br.com.inf.nuggets.miner.views;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditCommentDialog
  extends TitleAreaDialog
{
  private Text commentText;
  private String comment;
  private final boolean isEditing;
  
  public EditCommentDialog(Shell parentShell)
  {
    super(parentShell);
    this.isEditing = false;
    this.comment = "";
  }
  
  public EditCommentDialog(Shell parentShell, String comment)
  {
    super(parentShell);
    this.comment = comment;
    this.isEditing = true;
  }
  
  public void create()
  {
    super.create();
    if (this.isEditing)
    {
      setTitle("Edit annotation's comment");
      setMessage("Edit the comment for this annotation.", 1);
    }
    else
    {
      setTitle("Insert new annotation");
      setMessage("Add a comment for this document before linking it to the source file.", 1);
    }
  }
  
  protected Control createDialogArea(Composite parent)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    
    parent.setLayout(layout);
    
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = 4;
    gridData.verticalAlignment = 4;
    
    this.commentText = new Text(parent, 2560);
    this.commentText.setLayoutData(gridData);
    this.commentText.setText(this.comment);
    

    return parent;
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
    createOkButton(parent, 0, this.isEditing ? "Edit" : "Add", true);
    
    Button cancelButton = createButton(parent, 1, "Cancel", false);
    cancelButton.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        EditCommentDialog.this.setReturnCode(1);
        EditCommentDialog.this.close();
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
        EditCommentDialog.this.okPressed();
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
  
  protected boolean isResizable()
  {
    return true;
  }
  
  protected void okPressed()
  {
    this.comment = this.commentText.getText();
    super.okPressed();
  }
  
  public String getComment()
  {
    return this.comment;
  }
}
