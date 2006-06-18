package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import net.sf.click.Page;
import net.sf.click.control.Form;

/**
 * 
 * @author Naoki Takezoe
 */
public class ForeachHandler implements ElementHandler {

	public boolean handleElement(Page page, FuzzyXMLElement element, Form form) {
		String foreachValue = ClickHTMLUtil.getAttributeValue(element, ClickHTMLConstants.C_FOREACH);
		if(foreachValue!=null){
			FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
			parent.insertBefore(new FuzzyXMLTextImpl("\n#foreach(" + foreachValue + ")\n"), element);
			FuzzyXMLNode next = ClickHTMLUtil.getNextNode(parent, element);
			if(next==null){
				parent.appendChild(new FuzzyXMLTextImpl("\n#end\n"));
			} else {
				parent.insertBefore(new FuzzyXMLTextImpl("\n#end\n"), next);
			}
			ClickHTMLUtil.removeAttributes(element);
			
			return false;
		}
		return true;
	}

}
