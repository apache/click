package net.sf.clickide.ui.editor.forms;

import net.sf.clickide.ClickPlugin;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;

public class ClickTreeLabelProvider extends JFaceNodeLabelProvider {

	public Image getImage(Object object) {
		if(object instanceof IDOMElement){
			String name = ((IDOMElement)object).getNodeName();
			if(name.equals(ClickPlugin.TAG_CLICK_APP)){
				return ClickPlugin.getImageDescriptor("icons/click.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_HEADER)){
				return ClickPlugin.getImageDescriptor("icons/header.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_HEADERS)){
				return ClickPlugin.getImageDescriptor("icons/folder.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_PAGE)){
				return ClickPlugin.getImageDescriptor("icons/page.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_PAGES)){
				return ClickPlugin.getImageDescriptor("icons/folder.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_CONTROL)){
				return ClickPlugin.getImageDescriptor("icons/control.gif").createImage();
			}
			if(name.equals(ClickPlugin.TAG_CONTROLS)){
				return ClickPlugin.getImageDescriptor("icons/folder.gif").createImage();
			}
		}
		return super.getImage(object);
	}
}
