package net.sf.clickide.ui.editor;

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
 *   <li>TODO Velocity syntax validation</li>
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
