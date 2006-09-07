package net.sf.clickide.ui.editor.attrs;

import net.sf.clickide.ClickPlugin;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The implementation of <code>IElementEditor</code> for &lt;control&gt;.
 * 
 * @author Naoki Takezoe
 */
public class ControlAttributeEditor implements IAttributeEditor {
	
	public Composite createForm(FormToolkit toolkit, Composite parent, final IDOMElement element) {
		final Composite composite = toolkit.createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text text = AttributeEditorUtils.createClassText(toolkit, composite, element, 
				ClickPlugin.getString("editor.clickXML.controls.class"),
				ClickPlugin.ATTR_CLASSNAME, ClickPlugin.CLICK_CONTROL_IF, null);
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e){
				if(text.getText().equals("")){
					element.removeAttribute(ClickPlugin.ATTR_CLASSNAME);
				} else {
					element.setAttribute(ClickPlugin.ATTR_CLASSNAME, text.getText());
				}
			}
		});
		
		return composite;
	}

}
