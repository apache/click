package net.sf.click.clicklets;

import net.sf.click.control.Form;
import net.sf.click.util.HtmlStringBuffer;

public class ClickHTMLForm extends Form {

	private static final long serialVersionUID = 5259553221137469762L;
	
	public String getValidationJavaScript(){
		HtmlStringBuffer buffer = new HtmlStringBuffer();
		renderValidationJavaScript(buffer);
		return buffer.toString();
	}
	
}
