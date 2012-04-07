package org.apache.click.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.click.util.ChildObject;
import org.apache.click.util.ParentObject;

public abstract class PropertyServiceTestCase extends TestCase {
	
	protected PropertyService propertyService = null;

    public void test_getValue() {
    	
        try {
        	propertyService.getValue(new Object(), "username", new HashMap<Object, Object>());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
        	propertyService.getValue(new Object(), "class", new HashMap<Object, Object>());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        ParentObject testObject = new ParentObject();
        Map<?, ?> cache = new HashMap<Object, Object>();

        assertNull(propertyService.getValue(testObject, "name", cache));
        assertNull(propertyService.getValue(testObject, "value", cache));
        assertNull(propertyService.getValue(testObject, "date", cache));
        assertNull(propertyService.getValue(testObject, "child", cache));

        assertNull(propertyService.getValue(testObject, "name"));
        assertNull(propertyService.getValue(testObject, "value"));
        assertNull(propertyService.getValue(testObject, "date"));
        assertNull(propertyService.getValue(testObject, "child"));

        ParentObject parentObject =
            new ParentObject("malcolm", null, new Date(), Boolean.TRUE,
            new ChildObject("edgar", "medgar@avoka.com"));

        assertEquals("malcolm", propertyService.getValue(parentObject, "name", cache));
        assertNull(propertyService.getValue(parentObject, "value", cache));
        assertNotNull(propertyService.getValue(parentObject, "date", cache));
        assertNotNull(propertyService.getValue(parentObject, "valid", cache));
        assertEquals("edgar", propertyService.getValue(parentObject, "child.name", cache));
        assertEquals("medgar@avoka.com", propertyService.getValue(parentObject, "child.email", cache));


        assertEquals("malcolm", propertyService.getValue(parentObject, "name"));
        assertNull(propertyService.getValue(parentObject, "value"));
        assertNotNull(propertyService.getValue(parentObject, "date"));
        assertNotNull(propertyService.getValue(parentObject, "valid"));
        assertEquals("edgar", propertyService.getValue(parentObject, "child.name"));
        assertEquals("medgar@avoka.com", propertyService.getValue(parentObject, "child.email"));
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "malcolm");

        assertEquals("malcolm", propertyService.getValue(map, "name"));
    }

    public void test_setValue() {
    	ParentObject parentObject = new ParentObject();

    	propertyService.setValue(parentObject, "name", "malcolm");
    	assertEquals("malcolm", parentObject.getName());
    	
    	propertyService.setValue(parentObject, "value", "value");
    	assertEquals("value", parentObject.getValue());
    	
    	Date date = new Date();
    	propertyService.setValue(parentObject, "date", date);
    	assertEquals(date, parentObject.getDate());
    	
    	propertyService.setValue(parentObject, "valid", true);
    	assertEquals(Boolean.TRUE, parentObject.getValid());
    	
        Map<String, Object> map = new HashMap<String, Object>();
        propertyService.setValue(map, "name", "malcolm");
    	assertEquals("malcolm", map.get("name"));
    	
    	parentObject.setChild(new ChildObject());
    	
        propertyService.setValue(parentObject, "child.name", "malcolm");
        assertEquals("malcolm", parentObject.getChild().getName());
    }

}
