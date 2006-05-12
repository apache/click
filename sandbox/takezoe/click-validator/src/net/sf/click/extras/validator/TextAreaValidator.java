package net.sf.click.extras.validator;

import net.sf.click.control.TextArea;

/**
 * The implementation of {@link net.sf.click.extras.validator.Validator}
 * for {@link net.sf.click.control.TextArea}.
 * 
 * @author Naoki Takezoe
 */
public class TextAreaValidator implements Validator {
	
	private TextArea field;
	
	public TextAreaValidator(TextArea field){
		this.field = field;
	}
	
	public String getClientValidationScript() {
		
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

	public String getName() {
		return field.getName();
	}

}
