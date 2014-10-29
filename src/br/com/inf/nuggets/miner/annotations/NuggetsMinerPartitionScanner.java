package br.com.inf.nuggets.miner.annotations;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.observer.GlobalDocumentObserver;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.preferences.PreferenceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class NuggetsMinerPartitionScanner
  extends RuleBasedPartitionScanner
{
  private static Map<String, String[]> partitionsTypeList;
  private static Map<String, String[]> extensionsMap;
  private static Map<String, String> extensionsTypeMap;
  
  public NuggetsMinerPartitionScanner()
  {
    try
    {
      partitionsTypeList = new HashMap();
      extensionsMap = new HashMap();
      extensionsTypeMap = new HashMap();
      Map<List<String>, String> delimitersToTypeMap = new HashMap();
      
      List<IPredicateRule> rules = new ArrayList();
      IPreferenceStore store = Activator.getDefault().getPreferenceStore();
      
//      String jsonEntities = store.getDefaultString(NuggetsMinerPreferencesConstant.SEAHAWK_DELIMITERS);
      
//      List<PreferenceModel> delimiters = getDelimitersList(jsonEntities);
      /*for (PreferenceModel p : delimiters)
      {
        List<String> dels = Arrays.asList(new String[] { p.getOpeningDelimiter(), p.getClosingDelimiter() });
        if (!delimitersToTypeMap.containsKey(dels))
        {
          String type = "__seahawk_annotation_type_" + p.getFileExtension();
          IToken token = new Token(type);
          rules.add(new MultiLineRule(p.getOpeningDelimiter(), p.getClosingDelimiter(), token));
          partitionsTypeList.put(type, new String[] { p.getOpeningDelimiter(), p.getClosingDelimiter() });
          delimitersToTypeMap.put(dels, type);
        }
        String type = (String)delimitersToTypeMap.get(dels);
        extensionsTypeMap.put(p.getFileExtension(), type);
        extensionsMap.put(p.getFileExtension(), new String[] { p.getOpeningDelimiter(), p.getClosingDelimiter() });
      }
      setPredicateRules((IPredicateRule[])rules.toArray(new IPredicateRule[0])); */
    } 
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static List<PreferenceModel> getDelimitersList(String jsonEntities) {
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
  
  public static String[] getDelimiters(String fileExtension)
  {
    if (!extensionsMap.containsKey(fileExtension)) {
      return null;
    }
    return (String[])extensionsMap.get(fileExtension);
  }
  
  public static String[] getDelimiters(IDocument document)
  {
    ITextEditor editor = Activator.getDocumentObserver().getEditorForDocument(document);
    IFile file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
    if (file == null) {
      return null;
    }
    return getDelimiters(file.getFileExtension());
  }
  
  public static boolean isSeahawkPartition(String partitionId)
  {
    return partitionsTypeList.containsKey(partitionId);
  }
  
  public static String[] getPartitioningTypes()
  {
    return (String[])partitionsTypeList.keySet().toArray(new String[0]);
  }
  
  public static String getPartitioningTypeForFileExtension(String fileExtension)
  {
    if (!extensionsTypeMap.containsKey(fileExtension)) {
      return null;
    }
    return (String)extensionsTypeMap.get(fileExtension);
  }
  
  public static String getPartitioningTypeForDocument(IDocument document)
  {
    ITextEditor editor = Activator.getDocumentObserver().getEditorForDocument(document);
    IFile file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
    if (file == null) {
      return null;
    }
    return getPartitioningTypeForFileExtension(file.getFileExtension());
  }
}
