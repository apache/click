package net.sf.clickide.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.html.ui.StructuredTextViewerConfigurationHTML;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * {@link SourceViewerConfiguration} for the Velocity Template Editor.
 * Provides the extended code completion for Velocity.
 * 
 * @author Naoki Takezoe
 * @see TemplateContentAssistProcessor
 */
public class TemplateEditorConfiguration extends StructuredTextViewerConfigurationHTML {
	
	private TemplateContentAssistProcessor processor = null;
	
	/**
	 * @see TemplateContentAssistProcessor
	 */
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer viewer, String partitionType) {
		if(this.processor == null){
			this.processor = new TemplateContentAssistProcessor();
		}
		return new IContentAssistProcessor[]{this.processor};
	}

	/**
	 * @see TemplateAutoEditStrategy
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		List allStrategies = new ArrayList(0);
		
		IAutoEditStrategy[] superStrategies = super.getAutoEditStrategies(sourceViewer, contentType);
		for (int i = 0; i < superStrategies.length; i++) {
			allStrategies.add(superStrategies[i]);
		}
		
		allStrategies.add(new TemplateAutoEditStrategy());

		return (IAutoEditStrategy[]) allStrategies.toArray(new IAutoEditStrategy[allStrategies.size()]);
	}
	
	/**
	 * @see LineStyleProviderForVelocity
	 */
	public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
		LineStyleProvider[] providers = null;

		if (partitionType == IHTMLPartitions.HTML_DEFAULT || partitionType == IHTMLPartitions.HTML_COMMENT || partitionType == IHTMLPartitions.HTML_DECLARATION) {
			providers = new LineStyleProvider[]{new LineStyleProviderForVelocity()};
		} else {
			providers = super.getLineStyleProviders(sourceViewer, partitionType);
		}
//		else if (partitionType == IHTMLPartitions.SCRIPT) {
//			providers = new LineStyleProvider[]{getLineStyleProviderForJavascript()};
//		}
//		else if (partitionType == ICSSPartitions.STYLE) {
//			providers = new LineStyleProvider[]{getLineStyleProviderForEmbeddedCSS()};
//		}

		return providers;
	}
	
	
	
}
