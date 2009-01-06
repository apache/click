package org.apache.click.eclipse.ui.editor;


import org.apache.click.eclipse.core.validator.TemplateValidator;
import org.apache.click.eclipse.ui.actions.ToggleCommentAction;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
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
 *   <li>Syntax highlighting for Velocity directives</li>
 *   <li>Code completion for the Page public fields as variables</li>
 *   <li>TODO Code completion for the variables which declared by the #set directive</li>
 * </ul>
 *
 * @author Naoki Takezoe
 */
public class TemplateEditor extends StructuredTextEditor {

	protected void createActions() {
		super.createActions();
		setAction(ToggleCommentAction.class.getName(), new ToggleCommentAction(this));
	}

	protected void addContextMenuActions(IMenuManager menu){
		super.addContextMenuActions(menu);
		addAction(menu, ITextEditorActionConstants.GROUP_EDIT, ToggleCommentAction.class.getName());
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
