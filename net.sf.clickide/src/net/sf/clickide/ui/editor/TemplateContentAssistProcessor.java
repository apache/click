package net.sf.clickide.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;
import net.sf.clickide.ui.editor.TemplateObject.TemplateObjectElement;
import net.sf.clickide.ui.editor.TemplateObject.TemplateObjectMethod;
import net.sf.clickide.ui.editor.TemplateObject.TemplateObjectProperty;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
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
	private final Image IMAGE_METHOD = ClickPlugin.getImageDescriptor("/icons/method.gif").createImage();
	private final Image IMAGE_FIELD = ClickPlugin.getImageDescriptor("/icons/field.gif").createImage();
	private final Image IMAGE_VAR = ClickPlugin.getImageDescriptor("/icons/localvar.gif").createImage();
	
	private static final Map defaultObjects = new HashMap();
	static {
		defaultObjects.put("imports", "net.sf.click.util.PageImports");
		defaultObjects.put("context", "java.lang.String");
		defaultObjects.put("messages", "java.util.Map");
		defaultObjects.put("path", "java.lang.String");
		defaultObjects.put("request", "javax.servlet.http.HttpServletRequest");
		defaultObjects.put("response", "javax.servlet.http.HttpServletResponse");
		defaultObjects.put("session", "net.sf.click.util.SessionMap");
	};
	
	/**
	 * Returns the word under the caret position.
	 */
	private static String getLastWord(ITextViewer textViewer, int documentPosition){
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
	
	/**
	 * Appends the completion proposal to the <code>result</code>.
	 */
	private static void registerProposal(List result, int offset, 
			String matchString, String replaceString, String displayString, Image image){
		int position = replaceString.length();
		if(replaceString.endsWith(")") && displayString.indexOf("()") < 0){
			position--;
		}
		if(replaceString.endsWith("}") && displayString.indexOf("{}") < 0){
			position--;
		}
		if(replaceString.startsWith(matchString)){
			result.add(new CompletionProposal(
			        replaceString, offset - matchString.length(), 
			        matchString.length(), position, image, displayString, null, null));
		}
		if(matchString.startsWith("${") && replaceString.startsWith("$") &&
				!replaceString.startsWith("${")){
			registerProposal(result, offset, matchString, 
					"${" + replaceString.substring(1), displayString, image);
		}
	}
	
	/**
	 * Returns completion proposals.
	 */
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
			// for the format object
			format = ClickUtils.getFormat(file.getProject());
			if(matchString.startsWith("$format.") || matchString.startsWith("${format.")){
				if(format != null){
					return processType(format, result, matchString, offset);
				}
			}
			// other default objects
			for(Iterator ite = defaultObjects.entrySet().iterator(); ite.hasNext(); ){
				Map.Entry entry = (Map.Entry)ite.next();
				if(matchString.startsWith("$" + entry.getKey() + ".") || matchString.startsWith("${" + entry.getKey() + ".")){
					IType type = findType((String)entry.getValue());
					if(type != null){
						return processType(type, result, matchString, offset);
					}
				}
			}
		}
		
		Map fields = extractPageFields();
		for(Iterator ite = fields.entrySet().iterator(); ite.hasNext();){
			Map.Entry entry = (Map.Entry)ite.next();
			String name = (String)entry.getKey();
			if(matchString.startsWith("$" + name + ".") || matchString.startsWith("${" + name + ".")){
				TemplateObject obj = (TemplateObject)entry.getValue();
				if(obj.getType()!=null){
					return processType(obj.getType(), result, matchString, offset);
				}
			}
		}
		
		if(format==null){
			registerProposal(result, offset, matchString, "$format", "$format", IMAGE_VAR);
		} else {
			registerProposal(result, offset, matchString, 
					"$format", "$format - " + format.getFullyQualifiedName(), IMAGE_VAR);
		}
		
		// for page class fields
		for(Iterator ite = fields.entrySet().iterator(); ite.hasNext();){
			Map.Entry entry = (Map.Entry)ite.next();
			String name = (String)entry.getKey();
			TemplateObject obj = (TemplateObject)entry.getValue();
			registerProposal(result, offset, matchString, 
				"$" + name, "$" + name + " - " + obj.getTypeName(), IMAGE_FIELD);
		}
		
		registerProposal(result, offset, matchString, "$imports", "$imports - PageImports", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$context", "$context - String", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$messages", "$messages - Map", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$path", "$path - String", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$request", "$request - HttpServletRequest", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$response", "$response - HttpServletResponse", IMAGE_VAR);
		registerProposal(result, offset, matchString, "$session", "$session - SessionMap", IMAGE_VAR);
		// TODO It should be provided as the auto editing?
		registerProposal(result, offset, matchString, "${}", "${}", IMAGE_VAR);
		
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
	
	private IType findType(String className){
		try {
			IJavaProject project = JavaCore.create(this.file.getProject());
			if(project != null){
				IType type = project.findType(className);
				return type;
			}
		} catch(Exception ex){
		}
		return null;
	}
	
	/**
	 * Returns completion proposals for the java object.
	 */
	private ICompletionProposal[] processType(IType type, List result, String matchString, int offset){
		String prefix = matchString;
		int index = matchString.lastIndexOf('.');
		if(index >= 0){
			prefix = prefix.substring(0, index);
		}
		
		TemplateObject obj = new TemplateObject(type);
		obj = evaluate(obj, matchString);
		if(obj != null){
			TemplateObjectElement[] children = obj.getChildren();
			for(int i=0;i<children.length;i++){
				if(children[i] instanceof TemplateObjectMethod){
					registerProposal(result, offset, matchString, 
							prefix + "." + children[i].getName()+"()", children[i].getDisplayName(), IMAGE_METHOD);
				} else {
					registerProposal(result, offset, matchString, 
							prefix + "." + children[i].getName(), children[i].getDisplayName(), IMAGE_FIELD);
				}
			}
		}
		return (ICompletionProposal[])result.toArray(new ICompletionProposal[result.size()]);
	}
	
	/**
	 * Evaluates the given expression and returns the return type.
	 * 
	 * @param obj the <code>TemplateObject</code> of the top level object
	 * @param expression the Velocity expression
	 * @return the return type of the given expression or <code>null</code>
	 */
	private TemplateObject evaluate(TemplateObject obj, String expression){
		if(expression.endsWith(".")){
			expression += "_";
		}
		String[] dim = expression.split("\\.");
		for(int i=0; i < dim.length && obj != null; i++){
			if(i == 0 || i == dim.length-1){
				continue;
			}
			if(dim[i].endsWith(")")){
				// method
				String[] methodInfo = dim[i].split("\\(");
				if(methodInfo.length > 0){
					TemplateObjectMethod method = obj.getMethod(methodInfo[0]);
					if(method != null){
						obj = method.toTemplateObject();
						continue;
					}
				}
			} else {
				// property
				TemplateObjectProperty property = obj.getProperty(dim[i]);
				if(property != null){
					obj = property.toTemplateObject();
					continue;
				}
			}
			obj = null;
		}
		return obj;
	}
	
	/**
	 * Extracts public fields from the page class.
	 */
	private Map extractPageFields(){
		HashMap map = new HashMap();
		if(this.file != null){
			try {
				IType type = ClickUtils.getPageClassFromTemplate(this.file);
				IJavaProject javaProject = type.getJavaProject();
				
				IField[] fields = type.getFields();
				for(int i=0;i<fields.length;i++){
					if(!Flags.isPublic(fields[i].getFlags())){
						continue;
					}
					String className = ClickUtils.removeTypeParameter(Signature.toString(fields[i].getTypeSignature()));
					// primitive types
					if(ClickUtils.isPrimitive(className)){
						TemplateObject obj = new TemplateObject(className);
						map.put(fields[i].getElementName(), obj);
						continue;
					}
					// object types
					className = ClickUtils.resolveClassName(type, className);
					IType fieldType = javaProject.findType(className);
					if(fieldType != null){
						TemplateObject obj = new TemplateObject(fieldType);
						map.put(fields[i].getElementName(), obj);
					}
				}
			} catch(Exception ex){
				//ClickPlugin.log(ex);
			}
		}
		return map;
	}
	
	/**
	 * Sets the editing filr in the editor.
	 * 
	 * @param file the editing file
	 */
	public void setFile(IFile file){
		this.file = file;
	}
	
	/**
	 * Releases internal resources such as icons.
	 */
	public void release() {
		IMAGE_DIRECTIVE.dispose();
		IMAGE_METHOD.dispose();
		IMAGE_FIELD.dispose();
		IMAGE_VAR.dispose();
		super.release();
	}
	
}
