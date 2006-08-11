package net.sf.clickide.core.validator;

import java.io.UnsupportedEncodingException;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * The IValidator implementation for click.xml.
 * <p>
 * This validator validates:
 * </p>
 * <ul>
 *   <li>charset of &lt;click-app&gt;.</li>
 *   <li>package of &lt;pages&gt;.</li>
 *   <li>automapping of &lt;pages&gt;.</li>
 *   <li>type of &lt;header&gt;.</li>
 *   <li>classname of &lt;page&gt;, &lt;control&gt; and &lt;format&gt;.</li>
 *   <li>value and logto of &lt;mode&gt;.</li>
 * </ul>
 * <p>
 * All detected errors are marked as WARNING.
 * </p>
 * 
 * @author Naoki Takezoe
 */
public class ClickXMLValidator implements IValidator {
	
	private String packageName = null;
	
	public void cleanup(IReporter reporter) {
		packageName = null;
	}

	public void validate(IValidationContext context, IReporter reporter) throws ValidationException {
		String[] uris = context.getURIs();
		reporter.removeAllMessages(this);
		
		// IValidationContext may return a null array
		if (uris != null) {
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uris[i]));
				
				if(!file.getName().equals("click.xml")){
					continue;
				}
				
//			    Message message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, NLS.bind(JSPCoreMessages.MESSAGE_JSP_VALIDATING_MESSAGE_UI_, new String[]{file.getFullPath().toString()}));
//			    reporter.displaySubtask(this, message);
				
				IStructuredModel model = null;
				try {
					model = StructuredModelManager.getModelManager().getModelForRead(file);
					IStructuredDocument doc = model.getStructuredDocument();
					IStructuredDocumentRegion curNode = doc.getFirstStructuredDocumentRegion();
					while (null != (curNode = curNode.getNext()) && !reporter.isCancelled()) {
						if (curNode.getType() == DOMRegionContext.XML_TAG_NAME) {
							ITextRegionList list = curNode.getRegions();
							String text = curNode.getText();
							String tagName  = null;
							String attrName = null;
							for(int j=0;j<list.size();j++){
								ITextRegion region = list.get(j);
								if(region.getType()==DOMRegionContext.XML_TAG_NAME){
									tagName = text.substring(region.getStart(), region.getEnd()).trim();
									
								} else if(region.getType()==DOMRegionContext.XML_TAG_ATTRIBUTE_NAME){
									attrName = text.substring(region.getStart(), region.getEnd()).trim();
									
								} else if(region.getType()==DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE){
									String attrValue = text.substring(region.getStart(), region.getEnd()).trim();
									int length = attrValue.length();
									attrValue = attrValue.replaceAll("^\"|\"$","");
									if(tagName!=null && attrName!=null){
										validateAttributeValue(reporter, file, tagName, attrName, attrValue, 
												curNode.getStart() + region.getStart(), length);
									}
									attrName = null;
								}
							}
						}
					}
				} catch (Exception e) {
				} finally {
					if (null != model){
						model.releaseFromRead();
					}
				}
			}
		}
	}
	
	/**
	 * Validates the attribute value.
	 */
	private void validateAttributeValue(IReporter reporter, IFile file, 
			String tagName, String attrName, String attrValue, int start, int length){
		
		// package of <pages>
		if(tagName.equals(ClickPlugin.TAG_PAGES) && attrName.equals(ClickPlugin.ATTR_PACKAGE)){
			packageName = attrValue;
			return;
		}
		
		// classname of <control>, <page> and <format>
		if(tagName.equals(ClickPlugin.TAG_CONTROL) || tagName.equals(ClickPlugin.TAG_PAGE) || tagName.equals(ClickPlugin.TAG_FORMAT)){
			if(tagName.equals(ClickPlugin.TAG_PAGE) && packageName!=null && !packageName.equals("")){
				attrValue = packageName + "." + attrValue;
			}
			if(attrName.equals(ClickPlugin.ATTR_CLASSNAME)){
				if(!existsJavaClass(file, attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "notExist", 
							new String[]{attrValue}, start, length, -1);
				}
			}
		}
		// automapping and package of <pages>
		if(tagName.equals(ClickPlugin.TAG_PAGES)){
			if(attrName.equals(ClickPlugin.ATTR_AUTO_MAPPING)){
				if(!containsValue(ClickPlugin.AUTO_MAPPING_VALUES, attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "autoMapping", 
							new String[0], start, length, -1);
				}
			} if(attrName.equals(ClickPlugin.ATTR_PACKAGE)){
				
			}
		}
		// path of <page>
		if(tagName.equals(ClickPlugin.TAG_PAGE)){
			if(attrName.equals(ClickPlugin.ATTR_PATH)){
				
			}
		}
		// type of <header>
		if(tagName.equals(ClickPlugin.TAG_HEADER)){
			if(attrName.equals(ClickPlugin.ATTR_TYPE)){
				if(!containsValue(ClickPlugin.HEADER_TYPE_VALUES, attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "headerType", 
							new String[0], start, length, -1);
				}
			}
		}
		// value and logto of <mode>
		if(tagName.equals(ClickPlugin.TAG_MODE)){
			if(attrName.equals(ClickPlugin.ATTR_VALUE)){
				if(!containsValue(ClickPlugin.MODE_VALUES, attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "modeValue", 
							new String[0], start, length, -1);
				}
			} else if(attrName.equals(ClickPlugin.ATTR_LOGTO)){
				if(!containsValue(ClickPlugin.LOGTO_VALUES, attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "modeLogTo", 
							new String[0], start, length, -1);
				}
			}
		}
		// charset of <click-app>
		if(tagName.equals(ClickPlugin.TAG_CLICK_APP)){
			if(attrName.equals(ClickPlugin.ATTR_CHARSET)){
				if(!isSupportedEncoding(attrValue)){
					ValidatorUtils.createWarningMessage(this, reporter, file, "unsupportedEncoding", 
							new String[]{attrValue}, start, length, -1);
				}
			}
		}
	}
	
	private boolean existsJavaClass(IFile file, String typename){
		IJavaProject project = JavaCore.create(file.getProject());
		boolean exist = false;
		try {
			IType type = project.findType(typename);
			exist = type.exists();
		} catch(Exception ex){
			exist = false;
		}
		return exist;
	}
	
	private boolean containsValue(String[] proposals, String value){
		for(int i=0;i<proposals.length;i++){
			if(proposals[i].equals(value)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isSupportedEncoding(String encoding){
		try {
			new String(new byte[0], encoding);
		} catch(UnsupportedEncodingException ex){
			return false;
		}
		return true;
	}

}
