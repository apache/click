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
public class PropertyAttributeEditor implements IAttributeEditor {

	public Composite createForm(FormToolkit toolkit, Composite parent,
			final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text nameText = AttributeEditorUtils.createText(
				toolkit, composite, element,
				ClickPlugin.getString("editor.clickXML.property.name"), 
				ClickPlugin.ATTR_NAME);
		nameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(nameText.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_NAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_NAME, nameText.getText());
				}
			}
		});
		
		final Text valueText = AttributeEditorUtils.createText(
				toolkit, composite, element,
				ClickPlugin.getString("editor.clickXML.property.value"), 
				ClickPlugin.ATTR_VALUE);
		valueText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(valueText.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_VALUE);
				} else {
					element.setAttribute(ClickPlugin.ATTR_VALUE, valueText.getText());
				}
			}
		});
		
		return composite;
	}

}
