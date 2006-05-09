package net.sf.clickide.preferences;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.clickide.ClickPlugin;
import net.sf.clickide.ClickUtils;

import org.apache.xerces.parsers.DOMParser;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * 
 * @author Naoki Takezoe
 */
public class Template {
	
	private String name;
	private String pageClass;
	private String htmlTemplate;
	
	/**
	 * Returns the HTML template.
	 * @return the HTML template
	 */
	public String getHtmlTemplate() {
		return htmlTemplate;
	}
	
	/**
	 * Sets the HTML template.
	 * @param htmlTemplate the HTML template
	 */
	public void setHtmlTemplate(String htmlTemplate) {
		this.htmlTemplate = htmlTemplate;
	}
	
	/**
	 * Returns the template name.
	 * @return the template name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the template name.
	 * @param name the template name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the page class template.
	 * @return the page class template
	 */
	public String getPageClass() {
		return pageClass;
	}
	
	/**
	 * Sets the page class tenmplate.
	 * @param pageClass the page class template
	 */
	public void setPageClass(String pageClass) {
		this.pageClass = pageClass;
	}
	
	public String toXML(){
		StringBuffer sb = new StringBuffer();
		sb.append("<template name=\"").append(ClickUtils.escapeXML(name)).append("\">");
		sb.append("<class>").append(ClickUtils.escapeXML(pageClass)).append("</class>");
		sb.append("<html>").append(ClickUtils.escapeXML(htmlTemplate)).append("</html>");
		sb.append("</template>");
		return sb.toString();
	}
	
	public static List loadFromXML(String xml){
		List list = new ArrayList();
		try {
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(new StringReader(xml)));
			Document doc = parser.getDocument();
			Element element = doc.getDocumentElement();
			NodeList templates = element.getElementsByTagName("template");
			for(int i=0;i<templates.getLength();i++){
				Element template = (Element)templates.item(i);
				String name = template.getAttribute("name");
				
				Element clazz = (Element)template.getElementsByTagName("class").item(0);
				String classTemplate = getChildText(clazz).trim();
				
				Element html = (Element)template.getElementsByTagName("html").item(0);
				String htmlTemplate = getChildText(html).trim();
				
				Template tmpl = new Template();
				tmpl.setName(name);
				tmpl.setPageClass(classTemplate);
				tmpl.setHtmlTemplate(htmlTemplate);
				list.add(tmpl);
			}
		} catch(Exception ex){
			ClickPlugin.log(ex);
		}
		return list;
	}
	
	
	public static List loadFromPreference(){
		IPreferenceStore store = ClickPlugin.getDefault().getPreferenceStore();
		String xml = store.getString(ClickPlugin.PREF_TEMPLATES);
		return loadFromXML(xml);
	}
	
	private static String getChildText(Element element){
		StringBuffer sb = new StringBuffer();
		NodeList children = element.getChildNodes();
		for(int i=0;i<children.getLength();i++){
			Node node = children.item(i);
			if(node instanceof Text){
				sb.append(((Text)node).getNodeValue());
			}
		}
		return sb.toString();
	}
	
	public static void saveToPreference(List templates){
		StringBuffer sb = new StringBuffer();
		sb.append("<templates>");
		for(int i=0;i<templates.size();i++){
			sb.append(((Template)templates.get(i)).toXML());
		}
		sb.append("</templates>");
		
		IPreferenceStore store = ClickPlugin.getDefault().getPreferenceStore();
		store.setValue(ClickPlugin.PREF_TEMPLATES, sb.toString());
	}
}
