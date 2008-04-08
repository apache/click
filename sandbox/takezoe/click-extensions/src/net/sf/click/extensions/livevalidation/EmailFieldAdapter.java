package net.sf.click.extensions.livevalidation;

import java.util.List;

import net.sf.click.extras.control.EmailField;

/**
* <code>LiveValidatorAdapter</code> implementation for the <code>EmailField</code>.
  * 
 * @author Naoki Takezoe
 */
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
