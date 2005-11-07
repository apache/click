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
        assertEquals(21, map.size());

        assertTrue(map.containsKey("calendar-image-title"));
        assertEquals("Calendar", map.get("calendar-image-title")); 
        
        assertFalse(map.containsKey("Calendar"));
        try {
            assertNull(map.get("Calendar"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        locale = Locale.JAPANESE;
        map = new MessagesMap(Field.CONTROL_MESSAGES, locale);
        
        assertFalse(map.isEmpty());
        assertEquals(21, map.size());

        assertTrue(map.containsKey("calendar-image-title"));
        assertEquals("Calendar", map.get("calendar-image-title")); 
        
        assertFalse(map.containsKey("Calendar"));
        try {
            assertNull(map.get("Calendar"));
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

        assertFalse(map.containsKey("Calendar"));
        
        try {
            assertNull(map.get("Calendar"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap("missingResource", locale);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("Calendar"));
        
        try {
            assertNull(map.get("Calendar"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
    }

}
