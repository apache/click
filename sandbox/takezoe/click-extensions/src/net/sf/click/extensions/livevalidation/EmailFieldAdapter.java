package net.sf.click.extensions.livevalidation;

import java.util.List;

import net.sf.click.extras.control.EmailField;

public class EmailFieldAdapter extends TextFieldAdapter<EmailField> {

	@Override
	public List<ValidationRule> getValidationRules(EmailField field) {
		List<ValidationRule> rules = super.getValidationRules(field);
		
		JSONBuilder properties = new JSONBuilder();
		properties.append("failureMessage", 
				field.getMessage("email-format-error", field.getLabel()));
		
		ValidationRule rule = new ValidationRule("Validate.Email", properties.toString());
		rules.add(rule);
		
		return rules;
	}
	
}
