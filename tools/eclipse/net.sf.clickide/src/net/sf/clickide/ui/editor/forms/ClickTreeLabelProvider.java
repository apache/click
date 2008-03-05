package net.sf.clickide.ui.editor.forms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.clickide.ClickPlugin;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickTreeLabelProvider extends JFaceNodeLabelProvider {
	
	private Map images = new HashMap();
	
	public ClickTreeLabelProvider(){
		images.put(ClickPlugin.TAG_CLICK_APP, 
				ClickPlugin.getImageDescriptor("icons/click.gif").createImage());
		images.put(ClickPlugin.TAG_HEADER, 
				ClickPlugin.getImageDescriptor("icons/header.gif").createImage());
		images.put(ClickPlugin.TAG_HEADERS, 
				ClickPlugin.getImageDescriptor("icons/folder.gif").createImage());
		images.put(ClickPlugin.TAG_PAGE, 
				ClickPlugin.getImageDescriptor("icons/page.gif").createImage());
		images.put(ClickPlugin.TAG_PAGES, 
				ClickPlugin.getImageDescriptor("icons/folder.gif").createImage());
		images.put(ClickPlugin.TAG_CONTROL, 
				ClickPlugin.getImageDescriptor("icons/control.gif").createImage());
		images.put(ClickPlugin.TAG_CONTROLS, 
				ClickPlugin.getImageDescriptor("icons/folder.gif").createImage());
		images.put(ClickPlugin.TAG_EXCLUDES, 
				ClickPlugin.getImageDescriptor("icons/page.gif").createImage());
		images.put(ClickPlugin.TAG_FILE_ITEM_FACTORY, 
				ClickPlugin.getImageDescriptor("icons/folder.gif").createImage());
		images.put(ClickPlugin.TAG_PROPERTY, 
				ClickPlugin.getImageDescriptor("icons/property.gif").createImage());
	}
	
	public void dispose(){
		for(Iterator ite = images.values().iterator(); ite.hasNext();){
			Image image = (Image)ite.next();
			image.dispose();
		}
		super.dispose();
	}
	
	public String getText(Object object) {
		if(object instanceof Element){
			Element element = (Element)object;
			StringBuffer sb = new StringBuffer();
			sb.append(element.getNodeName());
			sb.append(" ");
			NamedNodeMap attrs = element.getAttributes();
			for(int i=0;i<attrs.getLength();i++){
				if(i!=0){
					sb.append(", ");
				}
				Attr attr = (Attr)attrs.item(i);
				sb.append(attr.getName());
				sb.append("=");
				sb.append(attr.getValue());
			}
			return sb.toString();
		}
		return super.getText(object);
	}

	public Image getImage(Object object) {
		if(object instanceof IDOMElement){
			String name = ((IDOMElement)object).getNodeName();
			Image image = (Image)images.get(name);
			if(image != null){
				return image;
			}
		}
		return super.getImage(object);
	}
}
