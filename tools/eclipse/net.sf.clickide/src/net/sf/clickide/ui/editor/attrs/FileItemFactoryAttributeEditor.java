package net.sf.clickide.ui.editor.attrs;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class FileItemFactoryAttributeEditor implements IAttributeEditor {

	public Composite createForm(FormToolkit toolkit, Composite parent,
			final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		IFile file = (IFile)ClickUtils.getResource(element.getStructuredDocument());
		IJavaProject project = JavaCore.create(file.getProject());
		
		final Text textClass = AttributeEditorUtils.createClassText(
				project, toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.class"), 
				ClickPlugin.ATTR_CLASSNAME, null, null);
		textClass.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textClass.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_CLASSNAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_CLASSNAME, textClass.getText());
				}
			}
		});
		
		return composite;
	}

}
