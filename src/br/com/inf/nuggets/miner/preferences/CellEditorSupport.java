package br.com.inf.nuggets.miner.preferences;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class CellEditorSupport<T1, T2>
  extends EditingSupport
{
  private final CellEditor textEditor;
  
  public CellEditorSupport(TableViewer viewer)
  {
    super(viewer);
    this.textEditor = new TextCellEditor(viewer.getTable());
  }
  
  protected boolean canEdit(Object element)
  {
    return true;
  }
  
  protected CellEditor getCellEditor(Object element)
  {
    return this.textEditor;
  }
  
  protected String getValue(Object element)
  {
    return getValueForElement((T1) element);
  }
  
  protected String getValueForElement(T1 element)
  {
    return "";
  }
  
  protected void setValueForElement(T1 element, T2 value) {}
  
  protected void setValue(Object element, Object value)
  {
    setValueForElement((T1) element, (T2) value);
    getViewer().update(element, null);
  }
}
