package br.com.inf.nuggets.miner.views;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.inf.nuggets.miner.solr.IssueData;
import br.com.inf.nuggets.miner.utils.ScoreSolrComparatorForIssues;

public class IssueTreeContentProvider implements ITreeContentProvider {  
  
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
	System.out.println("IssueTreeContentProvider dispose");
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	  System.out.println("IssueTreeContentProvider inputChanged");
  }

  @Override
  public Object[] getElements(Object inputElement) {
	System.out.println("IssueTreeContentProvider getElements");
	@SuppressWarnings("unchecked")
	List<IssueData> issuesLst = (List<IssueData>) inputElement;
	Collections.sort(issuesLst, new ScoreSolrComparatorForIssues());
	
	for (IssueData issue : issuesLst) {
		System.out.println("issueId: " + issue.getIssueId() + " title: " + issue.getIssueTitle() + " score: " + issue.getScoreSolr());
	}
	return issuesLst.toArray();
  }

  @Override
  public Object[] getChildren(Object parentElement) {
	  System.out.println("IssueTreeContentProvider getChildren");
	  return new Object[10];
  }
}
