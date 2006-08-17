package net.sf.clickide.ui.editor;

import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.TemplateObject.TemplateObjectElement;
import net.sf.clickide.ui.editor.TemplateObject.TemplateObjectMethod;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;

/**
 * {@link IContentAssistProcessor} implementation for the Velocity Template Editor.
 * 
 * @author Naoki Takezoe
 */
public class TemplateContentAssistProcessor extends XMLContentAssistProcessor {
	
	private IFile file;
	
	private final Image IMAGE_DIRECTIVE = ClickPlugin.getImageDescriptor("/icons/directive.gif").createImage();
	private final Image IMAGE_CLASS = ClickPlugin.getImageDescriptor("/icons/class.gif").createImage();
	private final Image IMAGE_METHOD = ClickPlugin.getImageDescriptor("/icons/method.gif").createImage();
	private final Image IMAGE_FIELD = ClickPlugin.getImageDescriptor("/icons/field.gif").createImage();
	
	private String getLastWord(ITextViewer textViewer, int documentPosition){
		String source = textViewer.getDocument().get();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<documentPosition;i++){
			char c = source.charAt(i);
			if(Character.isWhitespace(c)){
				sb.setLength(0);
			} else if(c=='#' || c=='$'){
				sb.setLength(0);
				sb.append(c);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private void registerProposal(List result, int offset, 
			String matchString, String replaceString, String displayString, Image image){
		int position = replaceString.length();
		if(replaceString.endsWith("}") || replaceString.endsWith(")")){
			position--;
		}
		if(replaceString.startsWith(matchString)){
			result.add(new CompletionProposal(
			        replaceString, 
			        offset - matchString.length(), 
			        matchString.length(), position, image, displayString, null, null));
		}
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer textViewer, int offset) {
		
		String matchString = getLastWord(textViewer, offset);
		List result = new ArrayList();
		
		if(!matchString.startsWith("#") && !matchString.startsWith("$")){
			ICompletionProposal[] proposals = super.computeCompletionProposals(textViewer, offset);
			if(proposals!=null){
				for(int i=0;i<proposals.length;i++){
					result.add(proposals[i]);
				}
			}
		}
		
		IType format = null;
		
		if(this.file != null){
			format = ClickUtils.getFormat(file.getProject());
			
			// for the format object
			if(matchString.startsWith("$format.")){
				if(format != null){
					TemplateObject obj = new TemplateObject(format);
					TemplateObjectElement[] children = obj.getChildren();
					for(int i=0;i<children.length;i++){
						if(children[i] instanceof TemplateObjectMethod){
							registerProposal(result, offset, matchString, 
									"$format." + children[i].getName()+"()", children[i].getDisplayName(), IMAGE_METHOD);
						} else {
							registerProposal(result, offset, matchString, 
									"$format." + children[i].getName(), children[i].getDisplayName(), IMAGE_FIELD);
						}
					}
					return (ICompletionProposal[])result.toArray(new ICompletionProposal[result.size()]);
				}
			}
		}
		
		if(format==null){
			registerProposal(result, offset, matchString, 
					"$format", "$format", IMAGE_CLASS);
		} else {
			registerProposal(result, offset, matchString, 
					"$format", "$format - " + format.getFullyQualifiedName(), IMAGE_CLASS);
		}
		
		registerProposal(result, offset, matchString, "#if()", "if", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#set()", "set", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#foreach()", "foreach", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#else", "else", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#elsif()", "elsif", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#end", "end", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#include()", "include", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#parse()", "parse", IMAGE_DIRECTIVE);
		registerProposal(result, offset, matchString, "#macro()", "macro", IMAGE_DIRECTIVE);
		
		
		return (ICompletionProposal[])result.toArray(new ICompletionProposal[result.size()]);
	}
	
	/**
	 * Sets the editing filr in the editor.
	 * 
	 * @param file the editing file
	 */
	public void setFile(IFile file){
		this.file = file;
	}

	public void release() {
		IMAGE_DIRECTIVE.dispose();
		IMAGE_CLASS.dispose();
		IMAGE_METHOD.dispose();
		IMAGE_FIELD.dispose();
		super.release();
	}
	
}
