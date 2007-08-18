package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

/**
 * The base class for form editors.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractFormEditor extends EditorPart {

	protected FormToolkit toolkit;
	protected Form form;
	
	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void updateMenu(){
	}
	
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText(ClickPlugin.getString("editor.clickXML.title"));
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.decorateFormHeading(form);
	}
	
	public abstract void initModel(IStructuredModel model);
	
	public abstract void modelUpdated(IStructuredModel model);
	
	public void setFocus() {
		form.setFocus();
	}

}
