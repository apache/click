package net.sf.clickide.core.builder;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.clickide.ClickPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * The validator for the click.xml.
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
public class ClickXMLValidator {
	
	private static ResourceBundle resource 
		= ResourceBundle.getBundle("net.sf.clickide.core.validator.validation");
	
	private String packageName = null;
	
	/**
	 * Validates click.xml.
	 * 
	 * @param file the <code>IFile</code> of click.xml.
	 */
	public void validate(IFile file, IProgressMonitor monitor){
		
		packageName = null;
		
		try {
			file.deleteMarkers(IMarker.PROBLEM, false, 0);
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
		
		IStructuredModel model = null;
		
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(file);
			IStructuredDocument doc = model.getStructuredDocument();
			IStructuredDocumentRegion curNode = doc.getFirstStructuredDocumentRegion();
			while (null != (curNode = curNode.getNext()) && !monitor.isCanceled()) {
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
								validateAttributeValue(file, tagName, attrName, attrValue, 
										curNode.getStart() + region.getStart(), length);
							}
							attrName = null;
						}
					}
				}
			}
		} catch (Exception e) {
			ClickPlugin.log(e);
		} finally {
			if (null != model){
				model.releaseFromRead();
			}
		}
	}
	
	/**
	 * Validates the attribute value.
	 */
	private void validateAttributeValue(IFile file, 
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
					createWarningMarker(file, "notExist", new String[]{attrValue}, start, length);
				}
			}
		}
		// automapping and package of <pages>
		if(tagName.equals(ClickPlugin.TAG_PAGES)){
			if(attrName.equals(ClickPlugin.ATTR_AUTO_MAPPING)){
				if(!containsValue(ClickPlugin.AUTO_MAPPING_VALUES, attrValue)){
					createWarningMarker(file, "autoMapping", new String[0], start, length);
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
					createWarningMarker(file, "headerType", new String[0], start, length);
				}
			}
		}
		// value and logto of <mode>
		if(tagName.equals(ClickPlugin.TAG_MODE)){
			if(attrName.equals(ClickPlugin.ATTR_VALUE)){
				if(!containsValue(ClickPlugin.MODE_VALUES, attrValue)){
					createWarningMarker(file, "modeValue", new String[0], start, length);
				}
			}
		}
		// charset of <click-app>
		if(tagName.equals(ClickPlugin.TAG_CLICK_APP)){
			if(attrName.equals(ClickPlugin.ATTR_CHARSET)){
				if(!isSupportedEncoding(attrValue)){
					createWarningMarker(file, "unsupportedEncoding", new String[]{attrValue}, start, length);
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
	
	/**
	 * Create the warning marker.
	 */
	private void createWarningMarker(IFile file, String key, String[] values, 
			int start, int length){
		try {
			String message = resource.getString(key);
			message = MessageFormat.format(message, values);
			
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			Map map = new HashMap();
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
			map.put(IMarker.MESSAGE, message);
//			map.put(IMarker.LINE_NUMBER,new Integer(line));
			map.put(IMarker.CHAR_START,new Integer(start));
			map.put(IMarker.CHAR_END,new Integer(start + length));
			marker.setAttributes(map);
			
		} catch(Exception e){
			ClickPlugin.log(e);
		}
	}	
	
}
