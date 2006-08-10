package net.sf.clickide.ui.editor;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;

/**
 * {@link SourceViewerConfiguration} for the Velocity Template Editor.
 * Provides the extended code completion for Velocity.
 * 
 * @author Naoki Takezoe
 * @see TemplateContentAssistProcessor
 */
public class TemplateEditorConfiguration extends StructuredTextViewerConfigurationHTML {

	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer viewer, String partitionType) {
		return new IContentAssistProcessor[]{new TemplateContentAssistProcessor()};
	}
	
}
