package org.apache.click.eclipse.ui.editor.forms;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.ui.editor.actions.ElementAppendAction;
import org.apache.click.eclipse.ui.editor.attrs.FileItemFactoryAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.IAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.PropertyAttributeEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickFileItemFactoryEditor extends AbstractMasterDetailEditor {

	protected void createMenu(IDOMElement element){
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
				ClickUtils.getElement(element, ClickPlugin.TAG_FILE_ITEM_FACTORY)==null){
			if(newMenu.getItems().length==0){
				newMenu.add(new ElementAppendAction(
						ClickPlugin.TAG_FILE_ITEM_FACTORY, element, null, this));
			}
		}
		if(element.getNodeName().equals(ClickPlugin.TAG_FILE_ITEM_FACTORY)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_PROPERTY, element, null, this));
		}
	}

	protected String[] getAcceptElementNames() {
		return new String[]{ClickPlugin.TAG_FILE_ITEM_FACTORY, ClickPlugin.TAG_PROPERTY};
	}

	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_FILE_ITEM_FACTORY)){
			return new FileItemFactoryAttributeEditor();
		}
		if(elementName.equals(ClickPlugin.TAG_PROPERTY)){
			return new PropertyAttributeEditor();
		}
		return null;
	}

}
