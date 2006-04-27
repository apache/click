package net.sf.click.extras.validator;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.util.ClickUtils;

/**
 * The extended Form control which supports the client side validation.
 * You can enable the client side validation by using this class instead 
 * of {@link net.sf.click.control.Form}.
 * <p>
 * There are two ways to make client side validation:
 * </p>
 * <ol>
 *   <li><strong>The Field which implements {@link net.sf.click.extras.validator.Validator}</strong>
 *   <p>
 *     If added field implements {@link net.sf.click.extras.validator.Validator},
 *     ValidatableForm generates the validation script for the field.
 *   </p>
 *   </li>
 *   <li><strong>Add {@link net.sf.click.extras.validator.Validator} by your hand</strong>
 *   <p>
 *     Client-Side Validation Framework provides some implementations of 
 *     {@link net.sf.click.extras.validator.Validator} for Click
 *     core controls. You can add them by {@link #addValidator(Validator)}.
 *   </p>
 *   <p>
 *     For example:
 *   </p>
 *   <pre>
 *   ValidatableForm form = new ValidatableForm("form");
 *   TextField text = new TextField("text", true);
 *   form.add(text);
 *   form.addValidator(new TextFieldValidator(text)); </pre>
 *   <p>
 *     Here is the validators list.
 *   </p>
 *   <ul>
 *     <li>{@link net.sf.click.extras.validator.TextFieldValidator}</li>
 *   </ul>
 *   </li>
 * </ol>
 * 
 * @author Naoki Takezoe
 */
public class ValidatableForm extends Form {
	
	private static final long serialVersionUID = 4879406010573266508L;
	private List validators = new ArrayList();
	
	public ValidatableForm() {
		super();
	}

	public ValidatableForm(String name) {
		super(name);
	}
	
	public void addValidator(Validator validator){
		this.validators.add(validator);
	}
	
	public String getHtmlImports(){
		return super.getHtmlImports() + getValidationScripts();
	}
	
	private String getValidationScripts(){
    	
        StringBuffer validateBuffer = new StringBuffer();
        StringBuffer formValidateBuffer = new StringBuffer();
    	
        List list = ClickUtils.getFormFields(this);
        for (int i = 0, size = list.size(); i < size; i++) {

            Field field = (Field) list.get(i);
            String validate = null;
            
            if(field instanceof Validator){
                validate = ((Validator)field).getClientValidationScript();
            }
            
            if(validate != null){
            	validateBuffer.append(validate);
            	if(formValidateBuffer.length()!=0){
            		formValidateBuffer.append(" && ");
            	}
            	formValidateBuffer.append("validate_").append(field.getName()).append("(form)");
            }
        }
        
        for (int i = 0, size = validators.size(); i < size; i++) {
        	Validator validator = (Validator)validators.get(i);
        	validateBuffer.append(validator.getClientValidationScript());
        	if(formValidateBuffer.length()!=0){
        		formValidateBuffer.append(" && ");
        	}
        	formValidateBuffer.append("validate_").append(validator.getName()).append("(form)");
        }
        
        if(validateBuffer.length()==0){
        	return "";
        }
        	
       	StringBuffer buffer = new StringBuffer();
       	
       	buffer.append("<script language=\"JavaScript\">");
       	buffer.append(validateBuffer.toString());
       	buffer.append("function validate_").append(getName()).append("(form){");
       	buffer.append("  if(").append(formValidateBuffer.toString()).append("){");
       	buffer.append("    return true;");
       	buffer.append("  }");
       	buffer.append("  return false;");
       	buffer.append("}");
       	buffer.append("</script>");
       	
       	setAttribute("onsubmit","return validate_" + getName() + "(this)");
       	
       	return buffer.toString();
    }
}
