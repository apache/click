package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.MockContext;

public class MessagesMapTest extends TestCase {
    
    private static final String TEST_MESSAGES = "click-test";
    
    public void testMap() {
        Context context = new MockContext(Locale.ENGLISH);
        
        MessagesMap map = new MessagesMap(Control.CONTROL_MESSAGES, null, context);
        
        assertFalse(map.isEmpty());
        assertEquals(29, map.size());

        assertTrue(map.containsKey("table-first-label"));
        assertEquals("First", map.get("table-first-label")); 
        
        assertFalse(map.containsKey("First"));
        try {
            assertNull(map.get("First"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        context = new MockContext(Locale.CANADA);
        map = new MessagesMap(Control.CONTROL_MESSAGES, null, context);

        assertFalse(map.isEmpty());
        assertEquals(29, map.size());

        assertTrue(map.containsKey("table-first-label"));
        assertEquals("First", map.get("table-first-label")); 
        
        assertFalse(map.containsKey("First"));
        try {
            assertNull(map.get("First"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
    }
    
    public void testCaching() {
        Context context = new MockContext(Locale.ENGLISH);
        MessagesMap map = new MessagesMap("missingResource", null, context);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap("missingResource", null, context);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
    }
    
    public void testGlobalResourses() {
        Context context = new MockContext(Locale.ENGLISH);
        
        MessagesMap map = new MessagesMap(Control.CONTROL_MESSAGES, 
                                          TEST_MESSAGES,
                                          context);
        
        assertFalse(map.isEmpty());
        assertEquals(30, map.size());

        assertTrue(map.containsKey("table-first-label"));
        assertEquals("First", map.get("table-first-label")); 
        
        assertTrue(map.containsKey("test-key"));
        assertEquals("Test Key Value", map.get("test-key"));
        
        MessagesMap map2 = new MessagesMap(TEST_MESSAGES,
                                           Control.CONTROL_MESSAGES,
                                           context);

        assertFalse(map2.isEmpty());
        assertEquals(30, map2.size());
        
        assertTrue(map2.containsKey("table-first-label"));
        assertEquals("Test Value", map2.get("table-first-label")); 
        
        assertTrue(map2.containsKey("test-key"));
        assertEquals("Test Key Value", map2.get("test-key")); 

    }

}
