package br.com.inf.nuggets.miner.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class HTMLToolTipSupport
  extends ColumnViewerToolTipSupport
{
  protected HTMLToolTipSupport(ColumnViewer viewer, int style, boolean manualActivation)
  {
    super(viewer, style, manualActivation);
  }
  
  protected Composite createToolTipContentArea(Event event, Composite parent)
  {
    String text = getText(event);
    Composite comp = new Composite(parent, 0);
    GridLayout l = new GridLayout(1, true);
    l.horizontalSpacing = 0;
    l.marginWidth = 0;
    l.marginHeight = 0;
    l.verticalSpacing = 0;
    Browser browser = new Browser(comp, 2048);
    GridData layout = new GridData(300, 500);
    layout.horizontalAlignment = 4;
    layout.grabExcessHorizontalSpace = true;
    layout.verticalAlignment = 4;
    layout.grabExcessVerticalSpace = true;
    browser.setText(text);
    browser.setLayoutData(layout);
    comp.setLayout(l);
    comp.pack();
    return comp;
  }
  
  public boolean isHideOnMouseDown()
  {
    return false;
  }
  
  public static final void enableFor(ColumnViewer viewer, int style)
  {
    new HTMLToolTipSupport(viewer, style, false);
  }
}
