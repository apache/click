package net.sf.click.clicklets;

import java.util.List;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.util.Format;

/**
 * A wrapper of a page class.
 * 
 * @author Naoki Takezoe
 */
public class ClickHTMLPage extends Page {
	
	private Page page;
	private List forms;
	
	public ClickHTMLPage(Page page, List forms){
		this.page  = page;
		this.forms = forms;
	}

	public void addControl(Control control) {
		page.addControl(control);
	}

	public void addModel(String name, Object value) {
		page.addModel(name, value);
	}

	public boolean equals(Object obj) {
		return page.equals(obj);
	}

	public String getContentType() {
		return page.getContentType();
	}

	public Context getContext() {
		return page.getContext();
	}

	public List getControls() {
		return page.getControls();
	}

	public Format getFormat() {
		return page.getFormat();
	}

	public String getForward() {
		return page.getForward();
	}

	public Map getHeaders() {
		return page.getHeaders();
	}

	public String getMessage(String key) {
		return page.getMessage(key);
	}

	public Map getMessages() {
		return page.getMessages();
	}

	public Map getModel() {
		return page.getModel();
	}

	public String getPath() {
		return page.getPath();
	}

	public String getRedirect() {
		return page.getRedirect();
	}

	public String getTemplate() {
		return page.getTemplate() + ".vm";
	}

	public boolean hasControls() {
		return page.hasControls();
	}

	public int hashCode() {
		return page.hashCode();
	}

	public void onDestroy() {
		page.onDestroy();
	}

	public void onGet() {
		page.onGet();
	}

	public void onInit() {
		for(int i=0;i<forms.size();i++){
			Form form = (Form)forms.get(i);
			addControl(form);
			
			java.lang.reflect.Field formField = getField(form.getName());
			if(formField!=null){
				try {
					formField.set(page, form);
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			List fields = form.getFieldList();
			for(int j=0;j<fields.size();j++){
				Field field = (Field)fields.get(j);
				java.lang.reflect.Field fieldField = getField(field.getName());
				if(fieldField!=null){
					try {
						fieldField.set(page, field);
					} catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		page.onInit();
	}
	
	private java.lang.reflect.Field getField(String name){
		// TODO Can't find methods which are declared at super classes.
		java.lang.reflect.Field[] fields = page.getClass().getDeclaredFields();
		for(int i=0;i<fields.length;i++){
			if(fields[i].getName().equals(name)){
				fields[i].setAccessible(true);
				return fields[i];
			}
		}
		return null;
	}
	
	public void onPost() {
		page.onPost();
	}

	public boolean onSecurityCheck() {
		return page.onSecurityCheck();
	}

	public void setContext(Context context) {
		page.setContext(context);
	}

	public void setFormat(Format value) {
		page.setFormat(value);
	}

	public void setForward(Page page) {
		page.setForward(page);
	}

	public void setForward(String value) {
		page.setForward(value);
	}

	public void setHeader(String name, Object value) {
		page.setHeader(name, value);
	}

	public void setHeaders(Map value) {
		page.setHeaders(value);
	}

	public void setPath(String value) {
		page.setPath(value);
	}

	public void setRedirect(String location) {
		page.setRedirect(location);
	}

	public String toString() {
		return page.toString();
	}
	
	
	
}
