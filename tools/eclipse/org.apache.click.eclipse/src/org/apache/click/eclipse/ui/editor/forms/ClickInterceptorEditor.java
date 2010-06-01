package org.apache.click.eclipse.ui.editor.forms;

import org.apache.click.eclipse.ClickPlugin;
import org.apache.click.eclipse.ui.editor.actions.ElementAppendAction;
import org.apache.click.eclipse.ui.editor.attrs.IAttributeEditor;
import org.apache.click.eclipse.ui.editor.attrs.PageInterceptorAttributeEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

public class ClickInterceptorEditor extends AbstractMasterDetailEditor {

	@Override
	protected void createMenu(IDOMElement element) {
		if(element.getNodeName().equals(ClickPlugin.TAG_CLICK_APP)){
			newMenu.add(new ElementAppendAction(ClickPlugin.TAG_PAGE_INTERCEPTOR, element, null, this));
		}
	}

	@Override
	protected String[] getAcceptElementNames() {
		return new String[]{ ClickPlugin.TAG_PAGE_INTERCEPTOR };
	}

	@Override
	protected IAttributeEditor getAttributeEditor(String elementName) {
		if(elementName.equals(ClickPlugin.TAG_PAGE_INTERCEPTOR)){
			return new PageInterceptorAttributeEditor();
		}
		return null;
	}

}
