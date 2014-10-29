package br.com.inf.nuggets.miner.views;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

class InfoControl
  extends DefaultInformationControl
{
  private final String htmlContent;
  
  public InfoControl(Shell parentShell, String htmlContent, boolean isResizable)
  {
    super(parentShell, isResizable);
    this.htmlContent = htmlContent;
  }
  
  public boolean hasContents()
  {
    return (this.htmlContent != null) && (!this.htmlContent.isEmpty());
  }
  
  protected void createContent(Composite parent)
  {
    if (this.htmlContent == null) {}
  }
}
