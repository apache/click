package net.sf.clickide.ui.editor.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;

public class ClickTreeContentProvider extends JFaceNodeContentProvider {
	
	private List accept;
	
	public ClickTreeContentProvider(List accept){
		this.accept = accept;
	}
	
	public Object[] getChildren(Object object) {
		List result = new ArrayList();
		Object[] children =  super.getChildren(object);
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof IDOMElement){
				String name = ((IDOMElement)children[i]).getNodeName();
				if(this.accept.contains(name)){
					result.add(children[i]);
				}
			}
		}
		return result.toArray();
	}

	public Object[] getElements(Object object) {
		Object[] obj = super.getElements(object);
		for(int i=0;i<obj.length;i++){
			if(obj[i] instanceof IDOMElement){
				return new Object[]{obj[i]};
			}
		}
		return null;
	}
}
