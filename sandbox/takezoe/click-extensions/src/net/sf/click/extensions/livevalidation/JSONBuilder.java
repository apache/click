package net.sf.click.extensions.livevalidation;

import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

/**
 * The utility class to build JSON.
 * <p>
 * This class uses <a href="http://jsonic.sourceforge.jp/">JSONIC</a> to 
 * encode <code>java.util.Map</code> to JSON.
 * 
 * @author Naoki Takezoe
 */
public class JSONBuilder {
	
	Map<String, Object> map = new HashMap<String, Object>();
	
	/**
	 * Appends the string parameter.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return a reference to this object
	 */
	public JSONBuilder append(String name, String value){
		map.put(name, value);
		return this;
	}
	
	/**
	 * Appends the int parameter.
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return a reference to this object
	 */
	public JSONBuilder append(String name, int value){
		map.put(name, value);
		return this;
	}
	
	/**
	 * Returns a JSON formatted string.
	 */
	@Override public String toString(){
		return JSON.encode(map);
	}
	
}
