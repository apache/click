package net.sf.click.extras.validator;

import net.sf.click.control.Checkbox;

/**
 * The implementation of {@link net.sf.click.extras.validator.Validator}
 * for {@link net.sf.click.control.Checkbox}.
 * 
 * @author Naoki Takezoe
 */
public class CheckboxValidator implements Validator {
	
	private Checkbox checkbox;
	
	public CheckboxValidator(Checkbox checkbox){
		this.checkbox = checkbox;
	}
	
	public String getName(){
		return checkbox.getName();
	}
	
	public String getClientValidationScript(){
		
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(JavaScriptUtils.createValidationFunction(checkbox) + "{");
    	if(checkbox.isRequired()){
	    	sb.append("if(").append(JavaScriptUtils.getFieldChecked(checkbox)).append("==false){");
	    	sb.append(JavaScriptUtils.fieldAlert("not-checked-error", checkbox));
	    	sb.append("return false;");
	    	sb.append("}");
    	}
    	sb.append("return true;");
    	sb.append("}");
    	
    	return sb.toString();
	}
}
