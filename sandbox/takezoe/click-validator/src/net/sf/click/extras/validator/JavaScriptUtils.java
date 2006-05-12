package net.sf.click.extras.validator;

import net.sf.click.control.Field;

/**
 * Provides utility methods to generate JavaScript.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptUtils {
	
	/**
	 * Returns JavaScript to display the alert dialog which shows an error message.
	 * 
	 * @param key the message key
	 * @param field the target field
	 * @return JavaScript like: <code>alert('<strong>error message</strong>');</code>
	 */
	public static String fieldAlert(String key, Field field){
		return fieldAlert(key, field, new Object[0]);
	}
	
	/**
	 * Returns JavaScript to display the alert dialog which shows an error message.
	 * 
	 * @param key the message key
	 * @param field the target field
	 * @param arg the parameter for the message
	 * @return JavaScript like: <code>alert('<strong>error message</strong>');</code>
	 */
	public static String fieldAlert(String key, Field field, Object arg){
		return fieldAlert(key, field, new Object[]{arg});
	}
	
	/**
	 * Returns JavaScript to display the alert dialog which shows an error message.
	 * 
	 * @param key the message key
	 * @param field the target field
	 * @param args parameters for the message
	 * @return JavaScript like: <code>alert('<strong>error message</strong>');</code>
	 */
	public static String fieldAlert(String key, Field field, Object[] args){
		Object[] newArgs = new Object[args.length + 1];
		newArgs[0] = field.getLabel();
		for(int i=0;i<args.length;i++){
			newArgs[i+1] = args[i];
		}
		return "alert('" + field.getMessage(key, newArgs) + "');";
	}
	
	/**
	 * Returns JavaScript to get selection status of the checkbox.
	 * 
	 * @param field the target field
	 * @return JavaScript like: <code>form.<strong>fieldname</strong>.checked</code>
	 */
	public static String getFieldChecked(Field field){
		return "form." + field.getName() + ".checked";
	}
	
	/**
	 * Returns JavaScript to get the value of the field.
	 * 
	 * @param field the target field
	 * @return JavaScript like: <code>form.<strong>fieldname</strong>.value</code>
	 */
	public static String getFieldValue(Field field){
		return "form." + field.getName() + ".value";
	}
	
	/**
	 * Returns JavaScript to set focus to the field.
	 * 
	 * @param field the target field
	 * @return JavaScript like: <code>form.<strong>fieldname</strong>.focus()</code>
	 */
	public static String focusField(Field field){
		return "form." + field.getName() + ".focus();";
	}
	
	/**
	 * Returns JavaScript to define the function to validates the field.
	 * 
	 * @param field the target field
	 * @return JavaScript like: 
	 * <code>function validate_<strong>fieldname</strong>(form)</code>
	 */
	public static String createValidationFunction(Field field){
		return "function validate_" + field.getName() + "(form)";
	}
	
}
