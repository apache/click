package net.sf.click.extensions.livevalidation;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.TextField;

/**
 * 
 * @author Naoki Takezoe
 */
public class TextFieldAdapter<T extends TextField> implements LiveValidatorAdapter<T> {

	public List<ValidationRule> getValidationRules(T field) {
		List<ValidationRule> rules = new ArrayList<ValidationRule>();
		if(field.isRequired()){
			JSONBuilder properties = new JSONBuilder();
			properties.append("failureMessage", 
					field.getMessage("field-required-error", field.getLabel()));
			
			ValidationRule rule = new ValidationRule("Validate.Presence", properties.toString());
			rules.add(rule);
		}
		
		if(field.getMinLength()!=0 || field.getMaxLength()!=0){
			JSONBuilder properties = new JSONBuilder();
			if(field.getMinLength()!=0){
				properties.append("minimum", field.getMinLength());
				properties.append("tooShortMessage", field.getMessage("field-minlength-error",
		                new Object[]{field.getLabel(), String.valueOf(field.getMinLength())}));
			}
			if(field.getMaxLength()!=0){
				properties.append("maximum", field.getMaxLength());
				properties.append("tooLongMessage", field.getMessage("field-maxlength-error",
		                new Object[]{field.getLabel(), String.valueOf(field.getMaxLength())}));
			}
			
			ValidationRule rule = new ValidationRule("Validate.Length", properties.toString());
			rules.add(rule);
		}
		return rules;
	}

}
