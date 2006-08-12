package net.sf.clickide.ui.editor;

import net.sf.clickide.ClickPlugin;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;

/**
 * {@link IContentAssistProcessor} implementation for the Velocity Template Editor.
 * 
 * @author Naoki Takezoe
 */
public class TemplateContentAssistProcessor extends XMLContentAssistProcessor {
	
	private Image IMAGE_DIRECTIVE = ClickPlugin.getImageDescriptor("/icons/directive.gif").createImage();
	
	protected String getMatchString(IStructuredDocumentRegion parent, ITextRegion aRegion, int offset) {
		if (aRegion == null || isCloseRegion(aRegion))
			return ""; //$NON-NLS-1$
		String matchString = null;
		String regionType = aRegion.getType();
		if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS || regionType == DOMRegionContext.XML_TAG_OPEN || (offset > parent.getStartOffset(aRegion) + aRegion.getTextLength())) {
			matchString = ""; //$NON-NLS-1$
		}
		else if (regionType == DOMRegionContext.XML_CONTENT) {
			// original region start
			String text = parent.getText(aRegion);
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<text.length();i++){
				if(i + parent.getStartOffset(aRegion) == offset){
					break;
				}
				char c = text.charAt(i);
				if(Character.isWhitespace(c)){
					sb.setLength(0);
				} else {
					sb.append(c);
				}
			}
			matchString = sb.toString();
			// original region end
		}
		else {
			if (parent.getText(aRegion).length() > 0 && parent.getStartOffset(aRegion) < offset)
				matchString = parent.getText(aRegion).substring(0, offset - parent.getStartOffset(aRegion));
			else
				matchString = ""; //$NON-NLS-1$
		}
		return matchString;
	}	
	
	private void registerProposal(ContentAssistRequest request, String replaceString, String displayString, Image image){
		String matchString = request.getMatchString();
		int position = replaceString.length();
		if(replaceString.endsWith("}") || replaceString.endsWith(")")){
			position--;
		}
		if(replaceString.startsWith(matchString)){
			request.addProposal(new CompletionProposal(
			        replaceString, 
			        request.getReplacementBeginPosition() - matchString.length(), 
			        matchString.length(), position, image, displayString, null, null));
		}
	}
	
	protected void addTagInsertionProposals(ContentAssistRequest request, int childPosition) {
		String matchString = request.getMatchString();
		if(!matchString.startsWith("#")){
			super.addTagInsertionProposals(request, childPosition);
		}
		registerProposal(request, "${}", "${}", IMAGE_DIRECTIVE);
//		registerProposal(request, "##");
		registerProposal(request, "#if()", "if", IMAGE_DIRECTIVE);
		registerProposal(request, "#set()", "set", IMAGE_DIRECTIVE);
		registerProposal(request, "#foreach()", "foreach", IMAGE_DIRECTIVE);
		registerProposal(request, "#else", "else", IMAGE_DIRECTIVE);
		registerProposal(request, "#elsif()", "elsif", IMAGE_DIRECTIVE);
		registerProposal(request, "#end", "end", IMAGE_DIRECTIVE);
		registerProposal(request, "#include()", "include", IMAGE_DIRECTIVE);
		registerProposal(request, "#parse()", "parse", IMAGE_DIRECTIVE);
		registerProposal(request, "#macro()", "macro", IMAGE_DIRECTIVE);
	}

	public void release() {
		IMAGE_DIRECTIVE.dispose();
		super.release();
	}
	
}
