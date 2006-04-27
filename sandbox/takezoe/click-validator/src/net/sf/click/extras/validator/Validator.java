package net.sf.click.extras.validator;

/**
 * The interface for the client side validator.
 * 
 * @author Naoki Takezoe
 */
public interface Validator {
	
	/**
	 * Returns JavaScript which validates to this field.
	 * 
	 * @return JavaScript
	 */
    public String getClientValidationScript();
    
    /**
     * Returns the field name.
     * 
     * @return the field name
     */
    public String getName();
}
