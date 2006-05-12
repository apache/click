package net.sf.click.extras.validator;

import net.sf.click.control.Checkbox;

/**
 * The implementation of {@link net.sf.click.extras.validator.Validator}
 * for {@link net.sf.click.control.Checkbox}.
 * 
 * @author Naoki Takezoe
 */
public class CheckboxValidator implements Validator {
	
	private Checkbox field;
	
	public CheckboxValidator(Checkbox field){
		this.field = field;
	}
	
	public String getName(){
		return field.getName();
	}
	
	public String getClientValidationScript(){
		
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(JavaScriptUtils.createValidationFunction(field) + "{");
    	if(field.isRequired()){
	    	sb.append("if(").append(JavaScriptUtils.getFieldChecked(field)).append("==false){");
	    	sb.append(JavaScriptUtils.fieldAlert("not-checked-error", field));
	    	sb.append("return false;");
	    	sb.append("}");
    	}
    	sb.append("return true;");
    	sb.append("}");
    	
    	return sb.toString();
	}
}
