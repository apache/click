package net.sf.click.extras.validator;

import net.sf.click.control.TextField;

/**
 * The implementation of {@link net.sf.click.extras.validator.Validator}
 * for {@link net.sf.click.control.TextField}.
 * 
 * @author Naoki Takezoe
 */
public class TextFieldValidator implements Validator {
	
	private TextField field;
	
	public TextFieldValidator(TextField field){
		this.field = field;
	}
	
	public String getName(){
		return field.getName();
	}
	
	public String getClientValidationScript(){
		
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(JavaScriptUtils.createValidationFunction(field) + "{");
    	if(field.isRequired()){
	    	sb.append("if(").append(JavaScriptUtils.getFieldValue(field)).append("==''){");
	    	sb.append(JavaScriptUtils.fieldAlert("field-required-error", field));
    		sb.append(JavaScriptUtils.focusField(field));
	    	sb.append("return false;");
	    	sb.append("}");
    	}
    	if(field.getMinLength() > 0){
    		sb.append("if(").append(JavaScriptUtils.getFieldValue(field)).append(".length <").append(field.getMinLength()).append("){");
    		sb.append(JavaScriptUtils.fieldAlert("field-minlength-error", field, new Integer(field.getMinLength())));
    		sb.append(JavaScriptUtils.focusField(field));
	    	sb.append("return false;");
	    	sb.append("}");
    	}
    	if(field.getMaxLength() > 0){
    		sb.append("if(").append(JavaScriptUtils.getFieldValue(field)).append(".length >").append(field.getMaxLength()).append("){");
    		sb.append(JavaScriptUtils.fieldAlert("field-maxlength-error", field, new Integer(field.getMaxLength())));
    		sb.append(JavaScriptUtils.focusField(field));
	    	sb.append("return false;");
	    	sb.append("}");
    	}
    	sb.append("return true;");
    	sb.append("}");
    	
    	return sb.toString();
	}
	
}
