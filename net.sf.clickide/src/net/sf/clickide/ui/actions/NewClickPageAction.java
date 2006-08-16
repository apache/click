package net.sf.clickide.ui.actions;

import net.sf.clickide.ui.wizard.NewClickPageWizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Opens the New Click Page wizard.
 * 
 * @author Naoki Takezoe
 */
public class NewClickPageAction implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;
	private IStructuredSelection selection;
	
	public NewClickPageAction() {
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			this.selection = (IStructuredSelection)selection;
		} else {
			this.selection = null;
		}
	}

	/**
	 * Opens {@link net.sf.clickide.ui.wizard.NewClickPageWizard}.
	 */
	public void run(IAction action) {
		NewClickPageWizard wizard = new NewClickPageWizard();
		wizard.init(PlatformUI.getWorkbench(), this.selection);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}