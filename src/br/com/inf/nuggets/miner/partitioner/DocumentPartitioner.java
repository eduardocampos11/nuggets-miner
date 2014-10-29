package br.com.inf.nuggets.miner.partitioner;

import org.eclipse.jface.text.IDocumentPartitioner;

public abstract class DocumentPartitioner
{
  protected final IDocumentPartitioner partitionerInstance;
  protected final String id;
  
  public DocumentPartitioner()
  {
    this.id = defineId();
    this.partitionerInstance = definePartitioner();
  }
  
  protected abstract String defineId();
  
  protected abstract IDocumentPartitioner definePartitioner();
  
  public String getId()
  {
    return this.id;
  }
  
  public IDocumentPartitioner getPartitionerInstance()
  {
    return this.partitionerInstance;
  }
  
  public boolean equals(Object obj)
  {
    return ((obj instanceof DocumentPartitioner)) && (((DocumentPartitioner)obj).id == this.id);
  }
}
