package org.apache.click.eclipse.cayenne;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Naoki Takezoe
 */
public class CayenneClasspathContainerPage extends WizardPage implements IClasspathContainerPage {
	
	private IClasspathEntry containerEntryResult;
	
	public CayenneClasspathContainerPage() {
		super("CayenneClasspathContainerPage");
		setTitle("Cayenne Libraries");
		setDescription("Add Cayenne libraries to classpath.");
	}

	public boolean finish() {
		containerEntryResult = JavaCore.newContainerEntry(
				new Path(CayenneClasspathContainerInitializer.CONTAINER_ID));
		return true;
	}

	public IClasspathEntry getSelection() {
		return containerEntryResult;
	}

	public void setSelection(IClasspathEntry entry) {
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

}
