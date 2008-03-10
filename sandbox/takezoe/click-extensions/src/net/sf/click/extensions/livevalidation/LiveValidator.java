package net.sf.click.extensions.livevalidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.control.AbstractControl;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;

/**
 * Generates JavaScript to validate form fields using LiveValidation.
 * <p>
 * Ready the <code>LiveValidator</code> control in the page class:
 * <pre>
 * Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 * ...
 * 
 * addControl(<span class="kw">new</span> LiveValidator(<span class="st">"validator"</span>, form)); </pre>
 * In the page template, generates JavaScript for LivaValidation 
 * by the <code>LiveValidator</code> control which added to the page class:
 * <pre>
 * $form
 * $validator </pre>
 * 
 * @author Naoki Takezoe
 */
public class LiveValidator extends AbstractControl {

	private static final long serialVersionUID = 1L;
	
    public static final String HTML_IMPORTS =
    	"<script type=\"text/javascript\" src=\"{0}/click/livevalidation_standalone.compressed.js\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/click/livevalidation.css\" />\n";
    
	private Form form;
	
	private Map<String, List<ValidationRule>> customRules = new HashMap<String, List<ValidationRule>>();
	
	public LiveValidator(){
	}
	
	public LiveValidator(String name){
		setName(name);
	}
	
	public LiveValidator(String name, Form form){
		setName(name);
		this.form = form;
	}
	
	public void setForm(Form form){
		this.form = form;
	}
	
	public Form getForm(){
		return this.form;
	}
	
	public void addCustomValidation(Field field, String properties){
		List<ValidationRule> rules = customRules.get(field.getId());
		if(rules == null){
			rules = new ArrayList<ValidationRule>();
			customRules.put(field.getId(), rules);
		}
		rules.add(new ValidationRule("Validate.Custom", properties));
	}
	
	/**
	 * 
	 * 
	 */
	@Override public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("<script type=\"text/javascript\">\n");
		
		@SuppressWarnings("unchecked")
		List<Field> fieldList = (List<Field>) form.getFieldList();
		
		for(Field field: fieldList){
			@SuppressWarnings("unchecked")
			LiveValidatorAdapter<Field> adapter = getAdapter(field);
			if(adapter != null){
				@SuppressWarnings("unchecked")
				List<ValidationRule> rules = adapter.getValidationRules(field);
				if(rules.isEmpty() && !customRules.containsKey(field.getId())){
					continue;
				}
				String varName = "validator_" + field.getId();
				sb.append("var ").append(varName).append(" = new LiveValidation('")
					.append(field.getId()).append("', {validMessage: 'Valid.', wait: 500});\n");
				
				for(ValidationRule rule: rules){
					sb.append(varName).append(".add(").append(rule.toString()).append(");\n");
				}
				
				if(customRules.containsKey(field.getId())){
					for(ValidationRule rule: customRules.get(field.getId())){
						sb.append(varName).append(".add(").append(rule.toString()).append(");\n");
					}
				}
			}
		}
		sb.append("</script>\n");
		
		return sb.toString();
	}
	
	/**
	 * Returns the <code>LiveValidatorAdapter</code> implementation.
	 * 
	 * @param field the field
	 * @return the <code>LiveValidatorAdapter</code> implementation
	 *   or <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	protected LiveValidatorAdapter getAdapter(Field field){
		if(field instanceof TextField){
			return new TextFieldAdapter();
		}
		return null;
	}

	public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
	}

	public void onDeploy(ServletContext servletContext) {
		ClickUtils.deployFile(servletContext, 
				"net/sf/click/extensions/livevalidation/livevalidation_standalone.compressed.js", 
				"click");
		ClickUtils.deployFile(servletContext, 
				"net/sf/click/extensions/livevalidation/livevalidation.css", 
				"click");
	}

	public void onDestroy() {
	}

	public void onInit() {
	}

	public boolean onProcess() {
		return true;
	}

	public void onRender() {
	}

	public void setListener(Object listener, String method) {
	}
	
}
