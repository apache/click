package net.sf.click.clicklets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.internal.FuzzyXMLElementImpl;
import jp.aonir.fuzzyxml.internal.FuzzyXMLTextImpl;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Form;

/**
 * Translates a plain html to a Velocity template.
 * And this also extracts informations about fields and controls.
 * 
 * @author Naoki Takezoe
 */
public class ClickHTMLCompiler {
	
	private static String ERROR_MESSAGES =  
		"<div id=\"form-errorsTr\" style=\"display:none\">\n"+
		"  <div  class=\"errors\" id=\"form-errorsDiv\"></div>\n"+
		"</div>\n";
	
	/**
	 * handlers
	 */
	private static final ElementHandler[] handlers = {
		new RenderedHandler(),
		new ReplaceHandler(),
		new ForeachHandler(),
		new FieldHandler()
	};
	
	public static List compile(Page page, InputStream in, OutputStream out) throws IOException {
		// parse
		FuzzyXMLDocument doc = new FuzzyXMLParser().parse(in);
		in.close();
		
		// process elements
		FuzzyXMLElement html = (FuzzyXMLElement)doc.getDocumentElement().getChildren()[0];
		List forms = new ArrayList();
		processElement(page, html, null, forms);
		
		// html importing
		FuzzyXMLNode[] children = html.getChildren();
		for(int i=0;i<children.length;i++){
			FuzzyXMLNode node = children[i];
			if(node instanceof FuzzyXMLElement && ((FuzzyXMLElement)node).getName().equalsIgnoreCase("head")){
				FuzzyXMLElement element = (FuzzyXMLElement)node;
				String htmlImports = ClickHTMLUtil.getAttributeValue(element, ClickHTMLConstants.C_HTMLIMPORTS);
				if(htmlImports==null || htmlImports.equalsIgnoreCase("true")){
					element.appendChild(new FuzzyXMLTextImpl("$imports"));
				}
				for(int j=0;j<forms.size();j++){
					ClickHTMLForm form = (ClickHTMLForm)forms.get(j);
					if(form.getValidate() && form.getJavaScriptValidation()){
						element.appendChild(new FuzzyXMLTextImpl("$" + form.getName() + ".validationJavaScript"));
					}
				}
			}
		}
		
		// output
		if(out!=null){
			String source = html.toXMLString();
			source = source.replaceAll("\\$client-side-errors", ERROR_MESSAGES);
			
			out.write(source.getBytes("UTF-8")); // TODO charset
			out.close();
		}
		return forms;
	}
	
	private static void processElement(Page page, FuzzyXMLElement element, String formName, List forms){
		
		FuzzyXMLNode[] children = element.getChildren();
		
		for(int i=0;i<handlers.length;i++){
			Form form = null;
			if(formName!=null){
				form = (Form)forms.get(forms.size()-1);
			}
			boolean result = handlers[i].handleElement(page, element, form);
			if(result==false){
				break;
			}
		}
		
		// TODO These processing should move to ElementHandler.
		
		if(element.getName().equalsIgnoreCase("form")){
			formName = ClickHTMLUtil.getAttributeValue(element, "name");
			if(formName!=null){
				FuzzyXMLElement hidden = new FuzzyXMLElementImpl("input");
				hidden.setAttribute("name", "form_name");
				hidden.setAttribute("value", "$" + formName + ".name");
				hidden.setAttribute("type", "hidden");
				element.appendChild(hidden);
				
				element.setAttribute("method", "$" + formName + ".method");
				element.setAttribute("name", "$" + formName + ".name");
				element.setAttribute("action", "$" + formName + ".actionURL");
				
				String id = element.getAttributeValue("id");
				if(id==null){
					id = formName;
				}
				element.setAttribute("id", id);
				
				ClickHTMLForm form = new ClickHTMLForm();
				form.setName(formName);
				form.setAttribute("id", id);
				
				String clientValidation = element.getAttributeValue("clientValidation");
				if(clientValidation!=null){
					element.removeAttribute("clientValidation");
					if(clientValidation.equals("true")){
						form.setJavaScriptValidation(true);
			            element.setAttribute("onsubmit", "return on_" + id + "_submit();");
					}
		        }
				
				forms.add(form);
			}
		}
		
		if(element.getName().equalsIgnoreCase("a") && element.getAttributeValue("action")!=null){
			// TODO ActionLink injection to the page class.
			ActionLink link = new ActionLink();
			link.setName(element.getAttributeValue("name"));
			link.setLabel(element.getValue());
			link.setListener(page, element.getAttributeValue("action"));
			page.addControl(link);
			
			FuzzyXMLElement parent = (FuzzyXMLElement)element.getParentNode();
			parent.replaceChild(new FuzzyXMLTextImpl("$" + link.getName() + "\n"), element);
		}
		
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLElement){
				processElement(page, (FuzzyXMLElement)children[i], formName, forms);
			}
		}
	}
}
