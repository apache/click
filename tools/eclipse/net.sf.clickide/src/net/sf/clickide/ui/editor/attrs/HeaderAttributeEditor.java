package net.sf.clickide.ui.editor.attrs;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The implementation of <code>IElementEditor</code> for &lt;header&gt;.
 * 
 * @author Naoki Takezoe
 */
public class HeaderAttributeEditor implements IAttributeEditor {
	
	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		Composite composite = toolkit.createComposite(parent);
		composite.setLayout(ClickUtils.createGridLayout(2));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text textName = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.name"),
				ClickPlugin.ATTR_NAME);
		textName.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textName.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_NAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_NAME, textName.getText());
				}
			}
		});
		
		final Text textValue = AttributeEditorUtils.createText(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.value"), 
				ClickPlugin.ATTR_VALUE);
		textValue.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(textValue.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_VALUE);
				} else {
					element.setAttribute(ClickPlugin.ATTR_VALUE, textValue.getText());
				}
			}
		});
		
		final Combo combo = AttributeEditorUtils.createCombo(
				toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.headers.type"), 
				ClickPlugin.ATTR_TYPE, 
				ClickUtils.createComboValues(ClickPlugin.HEADER_TYPE_VALUES));
		combo.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent evt){
				if(combo.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_TYPE);
				} else {
					element.setAttribute(ClickPlugin.ATTR_TYPE, combo.getText());
				}
			}
		});
		
		return composite;
	}

}
