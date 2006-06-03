package net.sf.clickide.ui.editor.actions;

import net.sf.clickide.ui.editor.forms.AbstractFormEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Element;

/**
 * 
 * @author Naoki Takezoe
 */
public class ElementAppendAction extends Action {
	
	private IDOMElement parentElement;
	private IDOMElement refElement;
	private String newElement;
	private AbstractFormEditor editor;
	
	public ElementAppendAction(String name, IDOMElement parentElement, 
			IDOMElement refElement, AbstractFormEditor editor){
		
		super(name);
		this.parentElement = parentElement;
		this.refElement = refElement;
		this.newElement = name;
		this.editor = editor;
	}
	
	public void run(){
		Element element = parentElement.getOwnerDocument().createElement(newElement);
		
		if(refElement!=null){
			parentElement.insertBefore(element, refElement);
		} else {
			parentElement.appendChild(element);
		}
		
		TreeViewer viewer = (TreeViewer)editor.getAdapter(TreeViewer.class);
		if(viewer!=null){
			viewer.setSelection(new StructuredSelection(element));
			viewer.refresh();
		}
		
		editor.updateMenu();
	}
	
}
