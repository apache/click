package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import net.sf.click.Page;
import net.sf.click.control.Form;

/**
 * 
 * @author Naoki Takezoe
 */
public class ReplaceHandler implements ElementHandler {

	public boolean handleElement(Page page, FuzzyXMLElement element, Form form) {
		String replaceValue = ClickHTMLUtil.getAttributeValue(element, ClickHTMLConstants.C_REPLACE);
		if(replaceValue!=null){
			if(!replaceValue.startsWith("$") && !replaceValue.startsWith("#") && form!=null){
				replaceValue = "$" + form.getName() + ".fields." + replaceValue;
			}
			FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
			parent.replaceChild(new FuzzyXMLTextImpl(replaceValue), element);
			return false;
		}
		return true;
	}

}
