package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.actions.ElementAppendAction;
import net.sf.clickide.ui.editor.attrs.ControlAttributeEditor;
import net.sf.clickide.ui.editor.attrs.IAttributeEditor;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickControlsEditor extends AbstractMasterDetailEditor {
	
	protected void createMenu(IDOMElement element){
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
				ClickUtils.getElement(element, ClickPlugin.TAG_CONTROLS)==null){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_CONTROLS, element, null, this));
		}
		
		if(element.getNodeName().equals(ClickPlugin.TAG_CONTROLS)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_CONTROL, element, null, this));
		}
	}

	protected String[] getAcceptElementNames() {
		return new String[]{ClickPlugin.TAG_CONTROLS, ClickPlugin.TAG_CONTROL};
	}

	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_CONTROL)){
			return new ControlAttributeEditor();
		}
		return null;
	}

}
