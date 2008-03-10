package net.sf.click.extensions.livevalidation;

import java.util.List;

import net.sf.click.control.Field;

/**
 * 
 * @author Naoki Takezoe
 */
public interface LiveValidatorAdapter<T extends Field> {
	
	public List<ValidationRule> getValidationRules(T field);
	
}
