package net.sf.click.extras.validator;

import net.sf.click.control.Field;

/**
 * Provides utility methods to generate JavaScript.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptUtils {
	
	public static String fieldAlert(String key, Field field){
		return fieldAlert(key, field, new Object[0]);
	}
	
	public static String fieldAlert(String key, Field field, Object arg){
		return fieldAlert(key, field, new Object[]{arg});
	}
	
	public static String fieldAlert(String key, Field field, Object[] args){
		Object[] newArgs = new Object[args.length + 1];
		newArgs[0] = field.getLabel();
		for(int i=0;i<args.length;i++){
			newArgs[i+1] = args[i];
		}
		return "alert('" + field.getMessage(key, newArgs) + "');\n";
	}
	
	public static String getFieldValue(Field field){
		return "form." + field.getName() + ".value";
	}
	
	public static String focusField(Field field){
		return "form." + field.getName() + ".focus();\n";
	}
	
	public static String createValidationFunction(Field field){
		return "function validate_" + field.getName() + "(form)";
	}
	
}
