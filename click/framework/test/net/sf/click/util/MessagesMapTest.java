package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.Context;
import net.sf.click.MockContext;
import net.sf.click.control.Field;

public class MessagesMapTest extends TestCase {
    
    public void testMap() {
        Context context = new MockContext(Locale.ENGLISH);
        
        MessagesMap map = new MessagesMap(Field.CONTROL_MESSAGES, context);
        
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
        map = new MessagesMap(Field.CONTROL_MESSAGES, context);

        assertFalse(map.isEmpty());
        assertEquals(28, map.size());

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
        MessagesMap map = new MessagesMap("missingResource", context);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap("missingResource", context);
        
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

}
