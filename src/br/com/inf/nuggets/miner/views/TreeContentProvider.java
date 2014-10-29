package br.com.inf.nuggets.miner.views;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.utils.ScoreFinalComparator;

public class TreeContentProvider
  implements ITreeContentProvider {
  
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
	System.out.println("TreeContentProvider dispose");
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	  System.out.println("TreeContentProvider inputChanged");
  }

  @Override
  public Object[] getElements(Object inputElement) {
	System.out.println("TreeContentProvider getElements");
	
	@SuppressWarnings("unchecked")
	List<DocumentBase> documentsLst = (List<DocumentBase>) inputElement;
	Collections.sort(documentsLst, new ScoreFinalComparator());
	
	for (DocumentBase docBase : documentsLst) {
		System.out.println("docId: " + docBase.getId() + "docTitle: " + docBase.getTitle() + " docScoreFinal: " + docBase.getScoreFinal());
	}
	return documentsLst.toArray();
  }

  @Override
  public Object[] getChildren(Object parentElement) {
	  System.out.println("TreeContentProvider getChildren");
	  return new Object[10];
  }
}