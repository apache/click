package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.actions.ElementAppendAction;
import net.sf.clickide.ui.editor.attrs.HeaderAttributeEditor;
import net.sf.clickide.ui.editor.attrs.IAttributeEditor;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickHeadersEditor extends AbstractMasterDetailEditor {
	
	protected void createMenu(IDOMElement element){
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
				ClickUtils.getElement(element, ClickPlugin.TAG_HEADERS)==null){
			IDOMElement[] elements = {
					ClickUtils.getElement(element, ClickPlugin.TAG_FORMAT),
					ClickUtils.getElement(element, ClickPlugin.TAG_MODE),
					ClickUtils.getElement(element, ClickPlugin.TAG_CONTROLS)};
			for(int i=0;i<elements.length;i++){
				if(elements[i]!=null){
					newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADERS, element, elements[i], this));
					break;
				}
			}
			if(newMenu.getItems().length==0){
				newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADERS, element, null, this));
			}
		}
		if(element.getNodeName().equals(ClickPlugin.TAG_HEADERS)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_HEADER, element, null, this));
		}
	}

	protected String[] getAcceptElementNames() {
		return new String[]{ClickPlugin.TAG_HEADERS, ClickPlugin.TAG_HEADER};
	}

	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_HEADER)){
			return new HeaderAttributeEditor();
		}
		return null;
	}
	
}
