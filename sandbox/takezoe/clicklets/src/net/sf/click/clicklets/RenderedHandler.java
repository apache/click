package net.sf.click.clicklets;

import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import net.sf.click.Page;
import net.sf.click.control.Form;

/**
 * This handler handles <code>c:rendered</code>.
 * <p>
 * For example, here is a html template.
 * <pre>
 * &lt;span c:rendered=&quot;$form.fields.text.error&quot;&gt;$form.fields.text.error&lt;/span&gt;
 * </pre>
 * This handler translates this html to following Velocity template.
 * <pre>
 * #if($form.fields.text.error)
 *   &lt;span&gt;$form.fields.text.error&lt;/span&gt;
 * #end
 * </pre>
 * If you want to write tags which are displayed only preview time, 
 * you can use <code>c:rendered=&quot;false&quot;</code>.
 * <pre>
 * &lt;span c:rendered=&quot;false&quot;&gt;This part is removed at runtime.&lt;/span&gt;
 * </pre>
 * 
 * @author Naoki Takezoe
 */
public class RenderedHandler implements ElementHandler {

	public boolean handleElement(Page page, FuzzyXMLElement element, Form form) {
		String renderValue = ClickHTMLUtil.getAttributeValue(element, ClickHTMLConstants.C_RENDERED);
		if(renderValue!=null){
			FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
			if(renderValue.equals("false")){
				parent.removeChild(element);
				return false;
			}
			parent.insertBefore(new FuzzyXMLTextImpl("\n#if(" + renderValue + ")\n"), element);
			FuzzyXMLNode next = ClickHTMLUtil.getNextNode(parent, element);
			if(next==null){
				parent.appendChild(new FuzzyXMLTextImpl("\n#end\n"));
			} else {
				parent.insertBefore(new FuzzyXMLTextImpl("\n#end\n"), next);
			}
			
			element.removeAttribute(ClickHTMLConstants.C_RENDERED);
		}
		return true;
	}

}
