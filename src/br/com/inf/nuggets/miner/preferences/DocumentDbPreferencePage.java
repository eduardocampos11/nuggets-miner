package br.com.inf.nuggets.miner.preferences;

import br.com.inf.nuggets.miner.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DocumentDbPreferencePage
  extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  public static final String ID = "ch.usi.inf.crawley.preferences.DocumentDbPreferencePage";
  private StringFieldEditor dbNameEdit;
  private StringFieldEditor dbUserEdit;
  private StringFieldEditor dbPasswdEdit;
  private StringFieldEditor sshUserEdit;
  private StringFieldEditor sshPasswordEdit;
  private StringFieldEditor sshIPEdit; 
  private StringFieldEditor sshPortEdit;
  private StringFieldEditor databasePortEdit;
  private BooleanFieldEditor anonymous;
  
  public DocumentDbPreferencePage()
  {
    super(1);
  }
  
  public void createFieldEditors()
  {
    final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String[][] dbTypes = 
      { { "SQLite", "sqlite" }, 
      { "Postgre SQL", "postgresql" }, 
      { "MySQL", "mysql" } };
    addField(new ComboFieldEditor(NuggetsMinerPreferencesConstant.DB_TYPE, "Db &type", dbTypes, getFieldEditorParent()));
    this.anonymous = new BooleanFieldEditor(NuggetsMinerPreferencesConstant.DB_ANONYMOUS_ACCESS, 
      "Anonymous &access", getFieldEditorParent());
    
    this.dbNameEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.DB_NAME, "Db &name:", getFieldEditorParent());
    this.dbUserEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.DB_USER, "Db &user:", getFieldEditorParent());
    this.dbPasswdEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.DB_PASSWORD, "Db &password:", getFieldEditorParent());
    this.sshUserEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.SSH_USER, "SSH &user:", getFieldEditorParent());
    this.sshPasswordEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.SSH_PASSWORD, "SSH &password:", getFieldEditorParent());
    this.sshIPEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.SSH_IP, "SSH &ip:", getFieldEditorParent());
    this.sshPortEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.SSH_PORT, "SSH &port:", getFieldEditorParent());
    this.databasePortEdit = new StringFieldEditor(NuggetsMinerPreferencesConstant.DATABASE_PORT, "Database &port:", getFieldEditorParent());
    
    this.dbPasswdEdit.getTextControl(getFieldEditorParent()).setEchoChar('*');
    this.sshPasswordEdit.getTextControl(getFieldEditorParent()).setEchoChar('*');
    
    this.dbPasswdEdit.setEnabled(!store.getBoolean(NuggetsMinerPreferencesConstant.DB_ANONYMOUS_ACCESS), getFieldEditorParent());
    this.dbUserEdit.setEnabled(!store.getBoolean(NuggetsMinerPreferencesConstant.DB_ANONYMOUS_ACCESS), getFieldEditorParent());
    this.anonymous.getDescriptionControl(getFieldEditorParent()).addMouseListener(new MouseListener()
    {
      public void mouseUp(MouseEvent e)
      {
        store.setValue(NuggetsMinerPreferencesConstant.DB_ANONYMOUS_ACCESS, DocumentDbPreferencePage.this.anonymous.getBooleanValue());
        DocumentDbPreferencePage.this.dbPasswdEdit.setEnabled(!DocumentDbPreferencePage.this.anonymous.getBooleanValue(), DocumentDbPreferencePage.this.getFieldEditorParent());
        DocumentDbPreferencePage.this.dbUserEdit.setEnabled(!DocumentDbPreferencePage.this.anonymous.getBooleanValue(), DocumentDbPreferencePage.this.getFieldEditorParent());
      }
      
      public void mouseDown(MouseEvent e) {}
      
      public void mouseDoubleClick(MouseEvent e) {}
    });
    addField(this.anonymous);
    addField(this.dbNameEdit);
    addField(this.dbUserEdit);
    addField(this.dbPasswdEdit);
    addField(this.sshUserEdit);
    addField(this.sshPasswordEdit);
    addField(this.sshIPEdit);
    addField(this.sshPortEdit);
    addField(this.databasePortEdit);
  }
  
  public boolean isValid()
  {
    if (this.dbNameEdit.getStringValue().isEmpty())
    {
      setErrorMessage("Database's name must be specified.");
      return false;
    }
    if (this.anonymous.getBooleanValue()) {
      return super.isValid();
    }
    if (this.dbUserEdit.getStringValue().isEmpty())
    {
      setErrorMessage("User name must be specified.");
      return false;
    }
    if (this.sshUserEdit.getStringValue().isEmpty())
    {
       setErrorMessage("SSH User must be specified.");
       return false;
    }
    if (this.sshPasswordEdit.getStringValue().isEmpty())
    {
    	setErrorMessage("SSH Password must be specified.");
    	return false;
    }
    if (this.sshIPEdit.getStringValue().isEmpty())
    {
    	setErrorMessage("SSH IP must be specified.");
    	return false;
    }
    if (this.sshPortEdit.getStringValue().isEmpty()) 
    {
    	setErrorMessage("SSH Port must be specified.");
    	return false;
    }
    if (this.databasePortEdit.getStringValue().isEmpty())
    {
    	setErrorMessage("Database Port must be specified.");
    	return false;
    }
    return super.isValid();
  }
  
  public void init(IWorkbench workbench)
  {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("Database preferences for document/source linking.");
  }
}
