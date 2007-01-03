package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.MockContext;

public class MessagesMapTest extends TestCase {
    
    public void testEnglishLocale() {
        Context context = new MockContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES, context);

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

    public void testCanadianLocale() {
        Context context = new MockContext(Locale.CANADA);

        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES, context);

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
    
    public void testMissingResourceCaching() {
        Context context = new MockContext(Locale.ENGLISH);
        MessagesMap map = new MessagesMap(Object.class, "missingResource", context);
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap(Object.class, "missingResource", context);
        
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
    
    public void testPageResources() {
        Context context = new MockContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(TestPage.class, "click-page", context);

        assertFalse(map.isEmpty());
        assertEquals(2, map.size());
    }

    public void testMessageInheritance() {
        Context context = new MockContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(Test2TextField.class, Control.CONTROL_MESSAGES, context);

        assertFalse(map.isEmpty());
        assertEquals(30, map.size());
        
        assertTrue(map.containsKey("name"));
        assertEquals("Test1TextField", map.get("name"));
        assertEquals("Test2TextField", map.get("classname"));
    }
    
}
