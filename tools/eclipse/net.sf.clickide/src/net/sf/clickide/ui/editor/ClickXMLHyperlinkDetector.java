package net.sf.clickide.ui.editor;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.util.StringUtils;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The IHyperlinkDetector implementation which provides hyperlink
 * for classes and page path.
 * 
 * @author Naoki Takezoe
 */
public class ClickXMLHyperlinkDetector implements IHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		Node currentNode = getCurrentNode(document, region.getOffset());
		if (currentNode != null) {
			short nodeType = currentNode.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE) {
				// nothing to do
			} else if (nodeType == Node.ELEMENT_NODE) {
				// element nodes
				Attr currentAttr = getCurrentAttrNode(currentNode, region.getOffset());
				if (currentAttr != null){
					IRegion hyperlinkRegion = getHyperlinkRegion(currentAttr);
					IHyperlink hyperLink = createHyperlinkForClass(
							currentAttr.getName(), currentAttr.getNodeValue(), hyperlinkRegion, document);
					if (hyperLink != null) {
						return new IHyperlink[] { hyperLink };
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the attribute node within node at offset
	 */
	private Attr getCurrentAttrNode(Node node, int offset) {
		if ((node instanceof IndexedRegion)
				&& ((IndexedRegion) node).contains(offset)
				&& (node.hasAttributes())) {
			NamedNodeMap attrs = node.getAttributes();
			// go through each attribute in node and if attribute contains
			// offset, return that attribute
			for (int i = 0; i < attrs.getLength(); ++i) {
				// assumption that if parent node is of type IndexedRegion,
				// then its attributes will also be of type IndexedRegion
				IndexedRegion attRegion = (IndexedRegion) attrs.item(i);
				if (attRegion.contains(offset)) {
					return (Attr) attrs.item(i);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the node the cursor is currently on in the document. null if no
	 * node is selected
	 * 
	 * @param offset
	 * @return Node either element, doctype, text, or null
	 */
	private Node getCurrentNode(IDocument document, int offset) {
		// get the current node at the offset (returns either: element,
		// doctype, text)
		IndexedRegion inode = null;
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager()
					.getExistingModelForRead(document);
			inode = sModel.getIndexedRegion(offset);
			if (inode == null)
				inode = sModel.getIndexedRegion(offset - 1);
		} finally {
			if (sModel != null)
				sModel.releaseFromRead();
		}

		if (inode instanceof Node) {
			return (Node) inode;
		}
		return null;
	}
	
	private IRegion getHyperlinkRegion(Node node) {
		IRegion hyperRegion = null;

		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == Node.DOCUMENT_TYPE_NODE
					|| nodeType == Node.ELEMENT_NODE
					|| nodeType == Node.TEXT_NODE) {
				// handle doc type node
				IDOMNode docNode = (IDOMNode) node;
				hyperRegion = new Region(docNode.getStartOffset(), 
						docNode.getEndOffset() - docNode.getStartOffset());
			} else if (nodeType == Node.ATTRIBUTE_NODE) {
				// handle attribute nodes
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				int regLength = att.getValueRegionText().length();
				String attValue = att.getValueRegionText();
				if (StringUtils.isQuoted(attValue)) {
					regOffset = ++regOffset;
					regLength = regLength - 2;
				}
				hyperRegion = new Region(regOffset, regLength);
			}
		}
		return hyperRegion;
	}
	
	/**
	 * Create the appropriate hyperlink.
	 */
	private IHyperlink createHyperlinkForClass(String name, String target,
			IRegion hyperlinkRegion, IDocument document) {
		
		IHyperlink link = null;

		if (name != null) {
			if (ClickPlugin.ATTR_CLASSNAME.equals(name)) {
				try {
					IFile file = ClickUtils.getResource(document);
					IJavaProject project = JavaCore.create(file.getProject());
					IType type = project.findType(target);
					if (type != null) {
						link = new AttributeHyperlink(hyperlinkRegion, type);
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			} else if(ClickPlugin.ATTR_PATH.equals(name)){
				try {
					IFile file = ClickUtils.getResource(document);
					IProject project = file.getProject();
					String root = ClickUtils.getWebAppRootFolder(project);
					IFile targetFile = project.getFile(new Path(root).append(target));
					if(targetFile.exists()){
						link = new AttributeHyperlink(hyperlinkRegion, targetFile);
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		}
		return link;
	}
	
	/**
	 * IHyperlink implementation for the java class and other files.
	 */
	private class AttributeHyperlink implements IHyperlink {

	    private final IRegion region;
	    private final IJavaElement element;
	    private final IFile file;

	    /**
	     * Creates a new Java element hyperlink.
	     */
	    public AttributeHyperlink(IRegion region, IJavaElement element) {
	        this.region = region;
	        this.element = element;
	        this.file = null;
	    }
	    
	    /**
	     * Creates a new Java element hyperlink.
	     */
	    public AttributeHyperlink(IRegion region, IFile file) {
	        this.region = region;
	        this.element = null;
	        this.file = file;
	    }

	    public IRegion getHyperlinkRegion() {
	        return this.region;
	    }
	    
	    /**
	     * opens the standard Java Editor for the given IJavaElement
	     */
	    public void open() {
	        if (this.element != null) {
                try {
                    JavaUI.revealInEditor(JavaUI.openInEditor(element), element);
                } catch (Exception e) {
                }
	        }
	        if(this.file != null){
	        	try {
	        		IDE.openEditor(ClickUtils.getActivePage(), file);
	        	} catch(Exception ex){
	        	}
	        }
	    }

	    public String getTypeLabel() {
	        return null;
	    }

	    public String getHyperlinkText() {
	        return null;
	    }
	}
}
