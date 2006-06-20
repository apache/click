package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.FuzzyXMLAttributeImpl;

/**
 * Provides utility methods.
 * 
 * @author Naoki Takezoe
 */
public class ClickHTMLUtil {
	
	/**
	 * Returns the next node.
	 * 
	 * @param parent the parent element
	 * @param node the node
	 * @return the next node of the node which is specified by the argument
	 */
	public static FuzzyXMLNode getNextNode(FuzzyXMLElement parent, FuzzyXMLNode node){
		FuzzyXMLNode[] children = parent.getChildren();
		boolean flag = false;
		for(int i=0;i<children.length;i++){
			if(flag==true){
				return children[i];
			}
			if(children[i]==node){
				flag = true;
			}
		}
		return null;
	}
	
	/**
	 * Removes all attributes which start with <code>c:</code>.
	 * 
	 * @param element the element
	 */
	public static void removeAttributes(FuzzyXMLElement element){
		FuzzyXMLAttribute[] attrs = element.getAttributes();
		for(int i=0;i<attrs.length;i++){
			if(attrs[i].getName().startsWith("c:")){
				element.removeAttributeNode(attrs[i]);
			}
		}
	}
	
	/**
	 * Returns the attribute value.
	 * 
	 * @param element the element
	 * @param name the attribute name
	 * @return the attribute value.
	 *   If element does not have specified attribute, returns <code>null</code>.
	 */
	public static String getAttributeValue(FuzzyXMLElement element, String name){
		FuzzyXMLAttribute attr = element.getAttributeNode(name);
		if(attr!=null){
			return attr.getValue();
		}
		return null;
	}
	
	/**
	 * Creates the new attribute.
	 * 
	 * @param name the new attribute name
	 * @param value the new attribute value
	 * @return the created attribute
	 */
	public static FuzzyXMLAttribute createAttribute(String name, String value){
		FuzzyXMLAttribute attr = new FuzzyXMLAttributeImpl(name);
		attr.setValue(value);
		return attr;
	}
}
