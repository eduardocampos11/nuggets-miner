package br.com.inf.nuggets.miner.preferences;

import br.com.inf.nuggets.miner.Activator;

import org.eclipse.ant.internal.ui.preferences.URLFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SolrPreferencePage
  extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  public static final String ID = "br.com.inf.nuggets.miner.preferences.SolrPreferencePage";
  
  public SolrPreferencePage()
  {
    super(1);
  }
  
  public void createFieldEditors()
  {
    addField(new URLFieldEditor(NuggetsMinerPreferencesConstant.SOLR_URL, 
      "Apache SOLR &service url:", getFieldEditorParent()));
    
    addField(new IntegerFieldEditor(NuggetsMinerPreferencesConstant.SOLR_RESULTS_SIZE, 
      "Number of results to show:", getFieldEditorParent()));
  }
  
  public void init(IWorkbench workbench)
  {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
  }
}
