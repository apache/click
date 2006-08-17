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
	
	private TemplateContentAssistProcessor processor = null;
	
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer viewer, String partitionType) {
		if(this.processor == null){
			this.processor = new TemplateContentAssistProcessor();
		}
		return new IContentAssistProcessor[]{this.processor};
	}
	
}
