package net.sf.click.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class PropertyUtilsTest extends TestCase {
    
	public void testGetProperty() {
		try {
			PropertyUtils.getValue(new Object(), "username", new HashMap());
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			PropertyUtils.getValue(new Object(), "class", new HashMap());
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}		
		
		ParentObject testObject = new ParentObject();
		Map cache = new HashMap();
			
		assertNull(PropertyUtils.getValue(testObject, "name", cache));
		assertNull(PropertyUtils.getValue(testObject, "value", cache));
		assertNull(PropertyUtils.getValue(testObject, "date", cache));
		assertNull(PropertyUtils.getValue(testObject, "child", cache));

        assertNull(PropertyUtils.getValue(testObject, "name"));
        assertNull(PropertyUtils.getValue(testObject, "value"));
        assertNull(PropertyUtils.getValue(testObject, "date"));
        assertNull(PropertyUtils.getValue(testObject, "child"));
        
		ParentObject parentObject = 
			new ParentObject("malcolm", null, new Date(), Boolean.TRUE, new ChildObject("edgar", "medgar@avoka.com"));
		
		assertEquals("malcolm", PropertyUtils.getValue(parentObject, "name", cache));
		assertNull(PropertyUtils.getValue(parentObject, "value", cache));
		assertNotNull(PropertyUtils.getValue(parentObject, "date", cache));
		assertNotNull(PropertyUtils.getValue(parentObject, "valid", cache));
		assertEquals("edgar", PropertyUtils.getValue(parentObject, "child.name", cache));
        assertEquals("medgar@avoka.com", PropertyUtils.getValue(parentObject, "child.email", cache));
        
        
        assertEquals("malcolm", PropertyUtils.getValue(parentObject, "name"));
        assertNull(PropertyUtils.getValue(parentObject, "value"));
        assertNotNull(PropertyUtils.getValue(parentObject, "date"));
        assertNotNull(PropertyUtils.getValue(parentObject, "valid"));
        assertEquals("edgar", PropertyUtils.getValue(parentObject, "child.name"));
        assertEquals("medgar@avoka.com", PropertyUtils.getValue(parentObject, "child.email"));
	}
    
}
