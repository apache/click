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
		
		TestObject testObject = new TestObject();
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
	
	public void testPerformance() throws Exception {
		int ITERATIONS = 10000;
		
		Date date = new Date();
		
		TestObject testObject = new TestObject("malcolm", null, date, Boolean.TRUE);
		testObject.setChild(new Child("edgar"));

		Map cache = new HashMap();
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < ITERATIONS; i++) {
			assertNotNull(getOgnlPropertyValue(testObject, "name", cache));
			assertNull(getOgnlPropertyValue(testObject, "value", cache));
			assertNotNull(getOgnlPropertyValue(testObject, "date", cache));
			assertNotNull(getOgnlPropertyValue(testObject, "valid", cache));
			assertNotNull(getOgnlPropertyValue(testObject, "child.name", cache));
		}
		
		System.out.println("OGNL duration with cache: " + (System.currentTimeMillis() - time) + "ms");
		
		time = System.currentTimeMillis();
		
		cache = new HashMap();
		
		for (int i = 0; i < ITERATIONS; i++) {
			assertNotNull(ClickUtils.getPropertyValue(testObject, "name", cache));
			assertNull(ClickUtils.getPropertyValue(testObject, "value", cache));
			assertNotNull(ClickUtils.getPropertyValue(testObject, "date", cache));
			assertNotNull(ClickUtils.getPropertyValue(testObject, "valid", cache));
			assertNotNull(ClickUtils.getPropertyValue(testObject, "child.name", cache));
		}
		
		System.out.println("Reflection duration with cache: " + (System.currentTimeMillis() - time) + "ms");		
	}
	
    public static class TestObject {
        private String name;
        private Object value;
        private Date date;
        private Child child; 
        private Boolean valid;
        
        public TestObject(String name, Object value, Date date, Boolean valid) {
            this.name = name;
            this.value = value;
            this.date = date;
            this.valid = valid;
        }
        
        public TestObject() {
        }
        
        public String getName() {
            return name;
        }
        
        public Date getDate() {
        	return date;
        }
        
        public Object getValue() {
            return value;
        }
        
        public Child getChild() {
        	return child;
        }
        
        public void setChild(Child child) {
        	this.child = child;
        }
        
        public Boolean isValid() {
        	return valid;
        }
    }
    
    public static class Child {
    	
    	public Child() {
    	}

    	public Child(String name) {
    		this.name = name;
    	}

    	private String name;
    	
    	public String getName() {
    		return name;
    	}
    	
    	public void setName(String name) {
    		this.name = name;
    	}
    }
    
    /**
     * Return the property value for the given object and property name.
     *
     * @param source the source object
     * @param name the name of the property
     * @param cache the reflection method and OGNL cache
     * @return the property value fo the given source object and property name
     */
    public static Object getOgnlPropertyValue(Object source, String name, Map cache) {

    	try {
    		Object expressionTree = null;

    		// Cache the OGNL expression for performance
    		if (cache != null) {
    			expressionTree = cache.get(name);
    			if (expressionTree == null) {
    				expressionTree = Ognl.parseExpression(name);
    				cache.put(name, expressionTree);
    			}

    			return Ognl.getValue(expressionTree, cache, source);

    		} else {
    			return Ognl.getValue(name, source);
    		}

    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }

}
