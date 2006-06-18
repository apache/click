package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import net.sf.click.Page;
import net.sf.click.control.Form;

/**
 * 
 * @author Naoki Takezoe
 */
public interface ElementHandler {
	
	public boolean handleElement(Page page, FuzzyXMLElement element, Form form);
	
}
