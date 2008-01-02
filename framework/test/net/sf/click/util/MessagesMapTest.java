package net.sf.click.util;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import java.util.ResourceBundle;
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

    /**
     * CLK-269
     * 
     * Test that the base properties will be picked up when the specified
     * locale and default locale are non-existent. 
     * 
     * Also test that the base properties are picked up when default locale is 
     * non-existent and specified locale is English.
     */
    public void testBasePropertiesUsingNonExistentDefaultLocale() {
        Locale locale = new Locale("xx", "XX");//bogus locale

        //Set bogus as the default locale
        Locale.setDefault(locale);

        MockContext.initContext(locale);
        MessagesMap nonEnglishMessages = 
          new ReloadableMessagesMap(Object.class, "click-control");
        assertFalse(nonEnglishMessages.isEmpty());
        
        //Test that property from click-control.properties are picked up
        //when locale is bogus locale
        assertEquals("First", nonEnglishMessages.get("table-first-label"));

        //Test that property from click-control.properties are picked up
        //when specified locale is English and default Locale cannot be found
        MockContext.initContext(Locale.ENGLISH);
        MessagesMap englishMessages = new ReloadableMessagesMap(Object.class, 
          "click-control");
        assertFalse(englishMessages.isEmpty());
        assertEquals("First", englishMessages.get("table-first-label"));
    }

    /**
     * CLK-269
     * 
     * Test that the English locale properties will be picked up when the 
     * default locale is French.
     */
    public void testEnglishMessagesUsingFrenchDefaultLocale() {
        Locale locale = new Locale("fr", "FR");

        //Set French as default locale
        Locale.setDefault(locale);

        MockContext.initContext(locale);
        MessagesMap nonEnglishMessages = 
          new ReloadableMessagesMap(Object.class, "click-control");
        assertFalse(nonEnglishMessages.isEmpty());

        //Test that French property is picked up
        assertEquals("Première", nonEnglishMessages.get("table-first-label"));

        //While using a default French locale, test that a English specified
        //locale picks up properties from click-control.properties
        MockContext.initContext(Locale.ENGLISH);
        MessagesMap englishMessages = new ReloadableMessagesMap(Object.class, 
          "click-control");
        assertFalse(englishMessages.isEmpty());
        assertEquals("First", englishMessages.get("table-first-label"));
    }

    /**
     * This messagesMap subclass will clear its static cached properties each
     * time a new instance is created. The properties will then be reloaded.
     */
    private class ReloadableMessagesMap extends MessagesMap {

        public ReloadableMessagesMap(Class baseClass, String globalResource) {
            super(baseClass, globalResource);
            clearCache();
        }

        public void clearCache() {
            MESSAGES_CACHE.clear();
            clearResourceBundleCache();
        }

        private void clearResourceBundleCache() {
            try {
                Class type = ResourceBundle.class;
                Field cacheList = type.getDeclaredField("cacheList");
                cacheList.setAccessible(true);
                ((Map) cacheList.get(ResourceBundle.class)).clear();
                cacheList.setAccessible(false);
            } catch (Exception jvmNotSupported) {
                System.out.println("WARNING: Could not clear the MessagesMap " +
                  "cache. This could lead to some test cases failing, because " +
                  "cached properties from previous tests could be returned.");
            }

        }
    }
}
