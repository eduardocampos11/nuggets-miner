package br.com.inf.nuggets.miner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.BundleContext;

import br.com.inf.nuggets.miner.dnd.DocumentDropListener;
import br.com.inf.nuggets.miner.observer.GlobalDocumentObserver;
import br.com.inf.nuggets.miner.observer.GlobalEditorOnTopObserver;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.sqlite.DatabaseManager;


public class Activator
  extends AbstractUIPlugin
{
  public static final String PLUGIN_ID = NuggetsMinerPreferencesConstant.BUNDLE;
  private static Activator plugin;
  private static GlobalEditorOnTopObserver editorObserver;
  private static GlobalDocumentObserver documentObserver;
  
  private void restartEclipse()
  {
    Display.getDefault().asyncExec(new Runnable()
    {
      public void run()
      {
        PlatformUI.getWorkbench().restart();
      }
    });
  }
  
  private void changeFirstRunSavedPreference()
  {
    IPreferenceStore store = getDefault().getPreferenceStore();
    store.setValue(NuggetsMinerPreferencesConstant.NUGGETS_MINER_FIRST_RUN, false);
  }
  
  private void registerDocumentDropHandler()
  {
    PlatformUI.getWorkbench().addWindowListener(new IWindowListener()
    {
      public void windowOpened(IWorkbenchWindow window)
      {
        window.getActivePage().addPartListener(new IPartListener()
        {
          final HashSet<IEditorPart> editors = new HashSet();
          
          private void removeListener(IWorkbenchPart part)
          {
            IEditorReference[] editorsRefs = part.getSite().getPage().getEditorReferences();
            
            List<IEditorPart> openedEditors = new ArrayList();
            for (IEditorReference e : editorsRefs)
            {
              IEditorPart editor = e.getEditor(false);
              if ((editor instanceof MultiPageEditorPart)) {
                editor = (IEditorPart)editor.getAdapter(ITextEditor.class);
              }
              if ((editor != null) && ((editor instanceof ITextEditor))) {
                openedEditors.add(e.getEditor(false));
              }
            }
            List<IEditorPart> toRemove = new ArrayList();
            for (IEditorPart e : this.editors) {
              if (!openedEditors.contains(e))
              {
                toRemove.add(e);
                System.out.println("REMOVED " + e.getTitle());
              }
            }
            for (IEditorPart e : toRemove) {
              this.editors.remove(e);
            }
          }
          
          private void addListener(IWorkbenchPart part)
          {
            IEditorReference[] editorRefs = part.getSite().getPage().getEditorReferences();
            for (IEditorReference e : editorRefs)
            {
              IEditorPart editor = e.getEditor(false);
              if ((editor instanceof MultiPageEditorPart)) {
                editor = (IEditorPart)editor.getAdapter(ITextEditor.class);
              }
              if ((editor != null) && ((editor instanceof ITextEditor)) && (!this.editors.contains(editor)))
              {
                System.out.println("ADDED " + editor.getTitle());
                this.editors.add(editor);
                Control ctrl = (Control)editor.getAdapter(Control.class);
                DropTarget dropTarget = (DropTarget)ctrl.getData("DropTarget");
                if (dropTarget != null)
                {
                  DocumentDropListener dropListener = new DocumentDropListener((ITextEditor)editor);
                  dropTarget.addDropListener(dropListener);
                  dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
                }
              }
            }
          }
          
          public void partOpened(IWorkbenchPart part)
          {
            addListener(part);
          }
          
          public void partDeactivated(IWorkbenchPart part)
          {
            removeListener(part);
          }
          
          public void partClosed(IWorkbenchPart part)
          {
            removeListener(part);
          }
          
          public void partBroughtToTop(IWorkbenchPart part)
          {
            addListener(part);
          }
          
          public void partActivated(IWorkbenchPart part)
          {
            addListener(part);
          }
        });
      }
      
      public void windowDeactivated(IWorkbenchWindow window) {}
      
      public void windowClosed(IWorkbenchWindow window) {}
      
      public void windowActivated(IWorkbenchWindow window) {}
    });
  }
  
  public void start(BundleContext context)
    throws Exception
  {
    super.start(context);
    plugin = this;
    IPreferenceStore store = getPreferenceStore();
    if ((store == null) || (store.getBoolean(NuggetsMinerPreferencesConstant.NUGGETS_MINER_FIRST_RUN)))
    {
      changeFirstRunSavedPreference();
      MessageDialog dialog = createRestartDialog();
      switch (dialog.open())
      {
      case 1: 
        restartEclipse();
        break;
      }
    }
    editorObserver = new GlobalEditorOnTopObserver();
    documentObserver = new GlobalDocumentObserver();
    registerDocumentDropHandler();
  }
  
  private MessageDialog createRestartDialog()
  {
    ImageDescriptor descriptor = getWorkbench().getSharedImages().getImageDescriptor("IMG_DEC_FIELD_WARNING");
    Image image = descriptor.createImage();
    String[] buttonLabels = { "Restart Later", "Restart Now" };
    MessageDialog dialog = new MessageDialog(PlatformUI.createDisplay().getActiveShell(), 
      "Nuggets Miner", image, 
      "Eclipse must be restarted to ensure that Nuggets Miner works correctly. You can postpone it but Nuggets Miner could not behave correctly.", 
      4, buttonLabels, 1);
    
    return dialog;
  }
  
  public void stop(BundleContext context)
    throws Exception
  {
    plugin = null;
    super.stop(context);
  }
  
  public static GlobalDocumentObserver getDocumentObserver()
  {
    return documentObserver;
  }
  
  public static GlobalEditorOnTopObserver getEditorOnTopObserver()
  {
    return editorObserver;
  }
  
  public static Activator getDefault()
  {
    return plugin;
  }
  
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin("br.com.inf.nuggets.miner", path);
  }
}
