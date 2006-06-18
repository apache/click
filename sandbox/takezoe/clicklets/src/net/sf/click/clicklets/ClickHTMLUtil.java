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
	
	public static void removeAttributes(FuzzyXMLElement element){
		FuzzyXMLAttribute[] attrs = element.getAttributes();
		for(int i=0;i<attrs.length;i++){
			if(attrs[i].getName().startsWith("c:")){
				element.removeAttributeNode(attrs[i]);
			}
		}
	}
	
	public static String getAttributeValue(FuzzyXMLElement element, String name){
		FuzzyXMLAttribute attr = element.getAttributeNode(name);
		if(attr!=null){
			return attr.getValue();
		}
		return null;
	}
	
	public static FuzzyXMLAttribute createAttribute(String name, String value){
		FuzzyXMLAttribute attr = new FuzzyXMLAttributeImpl(name);
		attr.setValue(value);
		return attr;
	}
}
