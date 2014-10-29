package br.com.inf.nuggets.miner.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class AlertManager
{
  private static String pluginName = "Seahawk";
  
  public static void showErrorMessage(final String message)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        Shell shell = Display.getDefault().getActiveShell();
        MessageDialog.openError(shell, AlertManager.pluginName, message);
      }
    });
  }
  
  public static void showErrorMessage(final String message, String reason)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        Shell shell = Display.getDefault().getActiveShell();
        IStatus status = new Status(4, AlertManager.pluginName, message);
        ErrorDialog.openError(shell, AlertManager.pluginName, message, status);
      }
    });
  }
  
  public static void showWarningMessage(final String message)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        Shell shell = Display.getDefault().getActiveShell();
        MessageDialog.openWarning(shell, AlertManager.pluginName, message);
      }
    });
  }
  
  public static void showInformationMessage(final String message)
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        Shell shell = Display.getDefault().getActiveShell();
        MessageDialog.openInformation(shell, AlertManager.pluginName, message);
      }
    });
  }
}
