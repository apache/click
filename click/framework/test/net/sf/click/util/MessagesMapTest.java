package net.sf.click.util;

import java.util.Locale;
import java.util.MissingResourceException;

import junit.framework.TestCase;
import net.sf.click.Control;
import net.sf.click.MockContext;

public class MessagesMapTest extends TestCase {
    
    public void testEnglishLocale() {
        MockContext.initContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES);

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
        MockContext.initContext(Locale.CANADA);

        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES);

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
        MockContext.initContext(Locale.ENGLISH);
        MessagesMap map = new MessagesMap(Object.class, "missingResource");
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());

        assertFalse(map.containsKey("table-first-label"));
        
        try {
            assertNull(map.get("table-first-label"));
            assertTrue(false);
        } catch (MissingResourceException mre) {
            assertTrue(true);
        }
        
        map = new MessagesMap(Object.class, "missingResource");
        
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
        MockContext.initContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(TestPage.class, "click-page");

        assertFalse(map.isEmpty());
        assertEquals(2, map.size());
    }

    public void testMessageInheritance() {
        MockContext.initContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(Test2TextField.class, Control.CONTROL_MESSAGES);

        assertFalse(map.isEmpty());
        assertEquals(30, map.size());
        
        assertTrue(map.containsKey("name"));
        assertEquals("Test1TextField", map.get("name"));
        assertEquals("Test2TextField", map.get("classname"));
    }
    
    /**
     * CLK-274
     * 
     * Create two MessagesMaps for the same class eg. Object.class
     * with different global resources eg. "missingResource" and 
     * "click-control". The first messagesMap is created specifying 
     * "missingResource" as its global resource. Since no 
     * missingResource.properties file exists, this messagesMap will be empty.
     * 
     * Test that the second messagesMap, specifying "click-control" as its 
     * global resource, should pick up the properties from the 
     * click-control.properties file.
     */
    public void testGlobalResourceKey() {
        MockContext.initContext(Locale.ENGLISH);
        MessagesMap emptyMap = new MessagesMap(Object.class, "missingResource");
        assertTrue(emptyMap.isEmpty());

        MessagesMap map = new MessagesMap(Object.class, "click-control");
        assertFalse(map.isEmpty());
        assertEquals("First", map.get("table-first-label"));
}
}
