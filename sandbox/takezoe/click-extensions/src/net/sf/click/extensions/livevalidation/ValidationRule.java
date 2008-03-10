package net.sf.click.extensions.livevalidation;

/**
 * 
 * @author Naoki Takezoe
 */
public class ValidationRule {
	
	private String rule;
	private String properties;
	
	public ValidationRule(){
	}
	
	public ValidationRule(String rule){
		setRule(rule);
	}
	
	public ValidationRule(String rule, String properties){
		setRule(rule);
		setProperties(properties);
	}
	
	public String getRule() {
		return rule;
	}
	
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public String getProperties() {
		return properties;
	}
	
	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	@Override public String toString(){
		return getRule() + ", " + getProperties();
	}
}
