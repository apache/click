package net.sf.clickide.ui.editor;

import net.sf.clickide.core.validator.TemplateValidator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * The editor for page templates which uses Velocity.
 * <p>
 * This editor extends WTP HTML editor and provides 
 * some new features for Velocity.
 * 
 * <ul>
 *   <li>Code completion for Velocity directives</li>
 *   <li>Velocity syntax validation by {@link TemplateValidator}</li>
 *   <li>TODO syntax hilighting for Velocity directives</li>
 *   <li>TODO Code completion in the HTML attribute value</li>
 *   <li>TODO Code completion for the Page public fields as variables</li>
 *   <li>TODO Code completion for the variables which declared by the #set directive</li>
 * </ul>
 * 
 * @author Naoki Takezoe
 */
public class TamplateEditor extends StructuredTextEditor {

	public void createPartControl(Composite parent) {
		setSourceViewerConfiguration(new TemplateEditorConfiguration());
		super.createPartControl(parent);
	}
	
}
