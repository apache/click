package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.control.Field;

public class MessagesMapTest extends TestCase {
    
    public void testMap() {
        Locale locale = Locale.ENGLISH;
        MessagesMap map = new MessagesMap(Field.CONTROL_MESSAGES, locale);
        
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
        
        locale = Locale.CANADA;
        map = new MessagesMap(Field.CONTROL_MESSAGES, locale);

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
        Locale locale = Locale.ENGLISH;
        MessagesMap map = new MessagesMap("missingResource", locale);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap("missingResource", locale);
        
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
