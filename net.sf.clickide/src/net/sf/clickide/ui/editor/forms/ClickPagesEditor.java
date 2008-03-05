package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.actions.ElementAppendAction;
import net.sf.clickide.ui.editor.attrs.ExcludesAttributeEditor;
import net.sf.clickide.ui.editor.attrs.IAttributeEditor;
import net.sf.clickide.ui.editor.attrs.PageAttributeEditor;
import net.sf.clickide.ui.editor.attrs.PagesAttributeEditor;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickPagesEditor extends AbstractMasterDetailEditor {

	public void updateMenu(){
		newMenu.removeAll();
		
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		Object obj = selection.getFirstElement();
		
		if(obj instanceof IDOMElement){
			IDOMElement element = (IDOMElement)obj;
			
			if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP)){
				deleteAction.setEnabled(false);
			} else {
				deleteAction.setEnabled(true);
				deleteAction.setElement(element);
			}
			
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
