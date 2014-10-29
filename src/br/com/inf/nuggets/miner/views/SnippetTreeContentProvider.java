package br.com.inf.nuggets.miner.views;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.inf.nuggets.miner.solr.IssueData;
import br.com.inf.nuggets.miner.solr.SnippetData;
import br.com.inf.nuggets.miner.utils.ScoreSolrComparatorForSnippets;


public class SnippetTreeContentProvider implements ITreeContentProvider {  
  
  public Object getParent(Object element)
  {
    return null;
  }
  
  public boolean hasChildren(Object element)
  {
   return false;
  }

  @Override
  public void dispose() {
	System.out.println("SnippetTreeContentProvider dispose");
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	  System.out.println("SnippetTreeContentProvider inputChanged");
  }

  @Override
  public Object[] getElements(Object inputElement) {
	System.out.println("SnippetTreeContentProvider getElements");
	@SuppressWarnings("unchecked")
	List<SnippetData> snippetsLst = (List<SnippetData>) inputElement;
	Collections.sort(snippetsLst, new ScoreSolrComparatorForSnippets());
	
//	for (SnippetData snippet : snippetsLst) {
//		System.out.println("issueId: " + snippet.getId() + 
//				" code: " + snippet.getCodeText() + " score: " + snippet.getScoreSolr());
//	}
	return snippetsLst.toArray();
  }

  @Override
  public Object[] getChildren(Object parentElement) {
	  System.out.println("IssueTreeContentProvider getChildren");
	  return new Object[10];
  }
}

