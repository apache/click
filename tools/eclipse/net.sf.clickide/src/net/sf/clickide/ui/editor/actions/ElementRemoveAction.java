package net.sf.clickide.ui.editor.actions;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * <code>IAction</code> which removes the <code>IDOMElement</code> 
 * from the parent element.
 * 
 * @author Naoki Takezoe
 */
public class ElementRemoveAction extends Action{
	
	private IDOMElement element;
	
	public ElementRemoveAction(){
		super(ClickPlugin.getString("action.delete"));
	}
	
	public void setElement(IDOMElement element){
		this.element = element;
	}
	
	public void run(){
		element.getParentNode().removeChild(element);
	}
	
}
