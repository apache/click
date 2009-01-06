package org.apache.click.eclipse.ui.editor.forms;


import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ClickUtils;
import org.apache.click.eclipse.ui.editor.actions.ElementAppendAction;
import org.apache.click.eclipse.ui.editor.attrs.ExcludesAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.IAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.PageAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.PagesAttributeEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickPagesEditor extends AbstractMasterDetailEditor {

	protected void createMenu(IDOMElement element){
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP) && 
				ClickUtils.getElement(element, ClickPlugin.TAG_PAGES)==null){
			IDOMElement[] elements = {
					ClickUtils.getElement(element, ClickPlugin.TAG_HEADERS),
					ClickUtils.getElement(element, ClickPlugin.TAG_FORMAT),
					ClickUtils.getElement(element, ClickPlugin.TAG_MODE),
					ClickUtils.getElement(element, ClickPlugin.TAG_CONTROLS)};
			for(int i=0;i<elements.length;i++){
				if(elements[i]!=null){
					newMenu.add(new ElementAppendAction(
							ClickPlugin.TAG_PAGES, element, elements[i], this));
					break;
				}
			}
			if(newMenu.getItems().length==0){
				newMenu.add(new ElementAppendAction(
						ClickPlugin.TAG_PAGES, element, null, this));
			}
		}
		if(element.getNodeName().equals(ClickPlugin.TAG_PAGES)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_PAGE, element, null, this));
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_EXCLUDES, element, null, this));
		}
	}

	protected String[] getAcceptElementNames() {
		return new String[]{ClickPlugin.TAG_PAGES, 
				ClickPlugin.TAG_PAGE, ClickPlugin.TAG_EXCLUDES};
	}

	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_PAGE)){
			return new PageAttributeEditor();
		}
		if(elementName.equals(ClickPlugin.TAG_EXCLUDES)){
			return new ExcludesAttributeEditor();
		}
		if(elementName.equals(ClickPlugin.TAG_PAGES)){
			return new PagesAttributeEditor();
		}
		return null;
	}

}
