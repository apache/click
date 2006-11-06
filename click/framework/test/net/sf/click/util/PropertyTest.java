package net.sf.click.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;

import junit.framework.TestCase;

public class PropertyTest extends TestCase {
	
	public void testGetProperty() {
		try {
			ClickUtils.getPropertyValue(new Object(), "username", new HashMap());
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			ClickUtils.getPropertyValue(new Object(), "class", new HashMap());
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}		
		
		ParentObject testObject = new ParentObject();
		try {
			Map cache = new HashMap();
			
			assertNull(ClickUtils.getPropertyValue(testObject, "name", cache));
			assertNull(ClickUtils.getPropertyValue(testObject, "value", cache));
			assertNull(ClickUtils.getPropertyValue(testObject, "date", cache));
			assertNull(ClickUtils.getPropertyValue(testObject, "child", cache));
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}		
	}
	
}
