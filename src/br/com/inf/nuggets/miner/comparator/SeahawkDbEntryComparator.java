package br.com.inf.nuggets.miner.comparator;

import br.com.inf.nuggets.miner.sqlite.MrCrawleyDbEntry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.internal.dialogs.ViewComparator;
import org.eclipse.ui.internal.registry.ViewRegistry;

public class SeahawkDbEntryComparator
  extends ViewComparator
{
  private static final int DESCENDING = -1;
  private static final int ASCENDING = 1;
  private int direction = -1;
  
  public SeahawkDbEntryComparator(ViewRegistry reg)
  {
    super(reg);
  }
  
  public void setDirection(int dir)
  {
    if (dir == 1024) {
      this.direction = 1;
    } else {
      this.direction = -1;
    }
  }
  
  public void switchDirection()
  {
    if (this.direction == -1) {
      this.direction = 1;
    } else {
      this.direction = -1;
    }
  }
  
  public int getDirection()
  {
    return this.direction == -1 ? 1024 : 128;
  }
  
  public int compare(Viewer viewer, Object obj1, Object obj2)
  {
    MrCrawleyDbEntry e1 = (MrCrawleyDbEntry)obj1;
    MrCrawleyDbEntry e2 = (MrCrawleyDbEntry)obj2;
    
    return this.direction * e1.getTitle().compareTo(e2.getTitle());
  }
}
