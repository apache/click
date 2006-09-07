package net.sf.clickide.ui.editor;

import net.sf.clickide.core.validator.TemplateValidator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * The editor for page templates which uses Velocity.
 * <p>
 * This editor extends WTP HTML editor and provides 
 * some new features for Velocity.
 * 
 * <ul>
 *   <li>Code completion for Velocity directives and the $format object</li>
 *   <li>Velocity syntax validation by {@link TemplateValidator}</li>
 *   <li>TODO syntax highlighting for Velocity directives</li>
 *   <li>TODO Code completion for the Page public fields as variables</li>
 *   <li>TODO Code completion for the variables which declared by the #set directive</li>
 * </ul>
 * 
 * @author Naoki Takezoe
 */
public class TamplateEditor extends StructuredTextEditor {

	public void createPartControl(Composite parent) {
//		setSourceViewerConfiguration(new TemplateEditorConfiguration());
		super.createPartControl(parent);
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		
		setSourceViewerConfiguration(new TemplateEditorConfiguration());
		
		TemplateEditorConfiguration config
			= (TemplateEditorConfiguration)getSourceViewerConfiguration();
		IContentAssistProcessor[] processors
			= config.getContentAssistProcessors(null, null);
		TemplateContentAssistProcessor processor
			= (TemplateContentAssistProcessor)processors[0];
		
		if(input instanceof IFileEditorInput){
			processor.setFile(((IFileEditorInput)input).getFile());
		} else {
			processor.setFile(null);
		}
	}
	
}
