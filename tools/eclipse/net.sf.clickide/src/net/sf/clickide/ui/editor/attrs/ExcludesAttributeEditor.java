package net.sf.clickide.ui.editor.attrs;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

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
public class ExcludesAttributeEditor implements IAttributeEditor {

	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text textName = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.pages.excludePattern"),
				ClickPlugin.ATTR_PATTERN);
		textName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textName.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_PATTERN);
				} else {
					element.setAttribute(ClickPlugin.ATTR_PATTERN, textName.getText());
				}
			}
		});
		
		return composite;
	}

}
