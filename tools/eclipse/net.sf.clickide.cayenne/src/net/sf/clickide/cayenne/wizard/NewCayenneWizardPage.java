package net.sf.clickide.cayenne.wizard;

import java.io.InputStream;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewCayenneWizardPage extends WizardNewFileCreationPage {

	public NewCayenneWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle("New Cayenne Mapping Project");
		setDescription("Create the new Cayenne mapping project.");
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("cayenne.xml");
	}
	
	protected InputStream getInitialContents() {
		return NewCayenneWizardPage.class.getResourceAsStream("cayenne.xml");
	}


}
