package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;

public abstract class AbstractFormEditor extends EditorPart {

	protected FormToolkit toolkit;
	protected ScrolledForm form;
	
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
		form = toolkit.createScrolledForm(parent);
		form.setText(ClickPlugin.getString("editor.clickXML.title"));
		form.getBody().setLayout(new GridLayout(2, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public abstract void initModel(IStructuredModel model);
	
	public abstract void modelUpdated(IStructuredModel model);
	
	public void setFocus() {
		form.setFocus();
	}

}
