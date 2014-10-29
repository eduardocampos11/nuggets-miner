package br.com.inf.nuggets.miner.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

class PartitioningCache
{
  private final HashMap<IDocument, HashMap<String, List<ITypedRegion>>> cache;
  
  public PartitioningCache()
  {
    this.cache = new HashMap();
  }
  
  public HashMap<String, List<ITypedRegion>> updatePartitions(IDocument document, HashMap<String, List<ITypedRegion>> partitions)
  {
    if (!this.cache.containsKey(document))
    {
      this.cache.put(document, partitions);
      return partitions;
    }
    HashMap<String, List<ITypedRegion>> newPartitions = new HashMap();
    HashMap<String, List<ITypedRegion>> currentPartitions = (HashMap)this.cache.get(document);
    for (Map.Entry<String, List<ITypedRegion>> e : partitions.entrySet())
    {
      List<ITypedRegion> toAdd = new ArrayList();
      for (ITypedRegion region : e.getValue()) {
        if (!((List)currentPartitions.get(e.getKey())).contains(region)) {
          toAdd.add(region);
        }
      }
      if (toAdd.size() > 0)
      {
        newPartitions.put((String)e.getKey(), toAdd);
        updateCache(document, (String)e.getKey(), (List)e.getValue());
      }
    }
    return newPartitions;
  }
  
  private void updateCache(IDocument document, String partitioner, List<ITypedRegion> regions)
  {
    HashMap<String, List<ITypedRegion>> current = (HashMap)this.cache.get(document);
    current.remove(partitioner);
    current.put(partitioner, regions);
  }
  
  public void removePartitions(IDocument document)
  {
    this.cache.remove(document);
  }
  
  public HashMap<String, List<ITypedRegion>> getPartitions(IDocument document)
  {
    if (!this.cache.containsKey(document)) {
      return new HashMap();
    }
    return (HashMap)this.cache.get(document);
  }
}
