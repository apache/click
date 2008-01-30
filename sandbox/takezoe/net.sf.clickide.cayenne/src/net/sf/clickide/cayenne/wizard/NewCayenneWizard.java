package net.sf.clickide.cayenne.wizard;

import net.sf.clickide.cayenne.editor.CayenneModelerLauncher;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This wizard creates new Cayenne mapping project.
 * <p>
 * Generates <tt>cayenne.xml</tt> which has the following contents:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;domains project-version="2.0"&gt;
 * &lt;/domains&gt; </pre>
 * 
 * And open generated <tt>cayenne.xml</tt> by the Cayenne Modeler.
 * 
 * @author Naoki Takezoe
 */
public class NewCayenneWizard extends Wizard implements INewWizard {

	private NewCayenneWizardPage page;
	private ISelection selection;
	
	public NewCayenneWizard() {
	}
	
	public void addPages() {
		page = new NewCayenneWizardPage(selection);
		addPage(page);
	}


	public boolean performFinish() {
		IFile file = page.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, CayenneModelerLauncher.EDITOR_ID);
		} catch(PartInitException ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

}
