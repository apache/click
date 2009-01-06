package org.apache.click.eclipse.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

/**
 * Extended StructuredTextViewerConfigurationXML
 * which provides the hyperlink support for click.xml.
 * 
 * @author Naoki Takezoe
 * @see org.apache.click.eclipse.ui.editor.ClickXMLHyperlinkDetector
 */
public class ClickXMLTextViewerConfiguration extends StructuredTextViewerConfigurationXML {
	
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		List result = new ArrayList(0);
		result.add(new ClickXMLHyperlinkDetector());
		
		IHyperlinkDetector[] superDetectors = super.getHyperlinkDetectors(sourceViewer);
		for (int m = 0; m < superDetectors.length; m++) {
			IHyperlinkDetector detector = superDetectors[m];
			if (!result.contains(detector)) {
				result.add(detector);
			}
		}
		
		return (IHyperlinkDetector[]) result.toArray(new IHyperlinkDetector[0]);
	}
	
}
