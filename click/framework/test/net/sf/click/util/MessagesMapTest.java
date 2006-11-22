package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.MockContext;

public class MessagesMapTest extends TestCase {
    
    private static final String TEST_RESOURCE = "test-resource";
    private static final String TEST_GLOBAL = "test-global";
    
    public void testMap() {
        Context context = new MockContext(Locale.ENGLISH);
        
        MessagesMap map = new MessagesMap(Control.CONTROL_MESSAGES, null, context);
        
        assertFalse(map.isEmpty());
        assertEquals(27, map.size());

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
        assertEquals(27, map.size());

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
        
        MessagesMap map = new MessagesMap(TEST_RESOURCE, 
                                          TEST_GLOBAL,
                                          context);
        
        assertFalse(map.isEmpty());
        assertEquals(26, map.size());

        assertTrue(map.containsKey("version"));
        assertEquals("Version 0.21", map.get("version")); 
        
        MessagesMap map2 = new MessagesMap(TEST_GLOBAL,
                                           Control.CONTROL_MESSAGES,
                                           context);
        
        assertFalse(map2.isEmpty());
        assertEquals(28, map2.size());

        assertTrue(map.containsKey("version"));
        assertEquals("Version 0.21", map2.get("version")); 

    }

}
