package net.sf.clickide.ui.editor.attrs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

/**
 * The interface of element editors in the {@link net.sf.clickide.ui.editor.ClickTreeEditor}.
 * <p>
 * Subclasses would provide the form editor to edit the element attributes.
 * 
 * @author Naoki Takezoe
 * @see net.sf.clickide.ui.editor.ClickTreeEditor
 */
public interface IAttributeEditor {
	
	/**
	 * Creates the form for the element editing.
	 * 
	 * @param toolkit FormToolkit
	 * @param parent the parent composite
	 * @param element the target element
	 * @return the form composite
	 */
	public Composite createForm(FormToolkit toolkit, Composite parent, IDOMElement element);
	
}
