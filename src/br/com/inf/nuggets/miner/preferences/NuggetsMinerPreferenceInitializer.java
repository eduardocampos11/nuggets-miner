package br.com.inf.nuggets.miner.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.solr.SolrManager;


public class NuggetsMinerPreferenceInitializer
  extends AbstractPreferenceInitializer
{
  public void initializeDefaultPreferences()
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    store.setDefault(NuggetsMinerPreferencesConstant.SOLR_URL, "http://localhost:8983/solr");
    store.setDefault(NuggetsMinerPreferencesConstant.SOLR_RESULTS_SIZE, 50);
    store.setDefault(NuggetsMinerPreferencesConstant.ANNOTATION_AUTHOR_NAME, "Eduardo");
    store.setDefault(NuggetsMinerPreferencesConstant.ANNOTATION_USER_SYSTEM_ENV, true);
    store.setDefault(NuggetsMinerPreferencesConstant.NUGGETS_MINER_FIRST_RUN, true);
    
    store.setDefault(NuggetsMinerPreferencesConstant.NUGGETS_MINER_DELIMITERS, createDefaultDelimiters());
    
    initPropertyChangedListeners();
  }
  
  private String createDefaultDelimiters()
  {
    try
    {
      List<PreferenceModel> defaultVals = new ArrayList<PreferenceModel>();
      
      defaultVals.addAll(Arrays.asList(
        new PreferenceModel[] {
        new PreferenceModel("java", "/*!", "*/"), 
        new PreferenceModel("cpp", "/*!", "*/"), 
        new PreferenceModel("hpp", "/*!", "*/"), 
        new PreferenceModel("c", "/*!", "*/"), 
        new PreferenceModel("h", "/*!", "*/"), 
        new PreferenceModel("css", "/*!", "*/"), 
        new PreferenceModel("scala", "/*!", "*/"), 
        new PreferenceModel("php", "/*!", "*/"), 
        new PreferenceModel("js", "/*!", "*/"), 
        new PreferenceModel("xml", "<!--!", "-->"), 
        new PreferenceModel("html", "<!--!", "-->"), 
        new PreferenceModel("htm", "<!--!", "-->"), 
        new PreferenceModel("py", "\"\"\"!", "\"\"\"") }));
      
      
      ObjectMapper map = new ObjectMapper();
      map.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
      String serializedStr = map.writeValueAsString(defaultVals);
      
      return serializedStr;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private void initPropertyChangedListeners()
  {
    Activator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent event)
      {
        if (event.getProperty().equals(NuggetsMinerPreferencesConstant.SOLR_URL)) {
          SolrManager.disposeInstance();
        }
      }
    });
  }
}
