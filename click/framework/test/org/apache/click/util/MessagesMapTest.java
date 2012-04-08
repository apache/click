/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.util;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import java.util.ResourceBundle;
import junit.framework.TestCase;
import org.apache.click.Control;
import org.apache.click.MockContext;

/**
 * Tests for MessagesMap.
 */
public class MessagesMapTest extends TestCase {

    /**
     * Test MessagesMap for English Locale.
     */
    public void testEnglishLocale() {
        MockContext.initContext(Locale.ENGLISH);

        // Load click-control.properties into map
        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES);

        assertFalse(map.isEmpty());
        assertTrue(map.size() > 28);

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

    /**
     * Test MessagesMap works for alternative Locales.
     */
    public void testCanadianLocale() {
        MockContext.initContext(Locale.CANADA);

        // Load click-control.properties into map
        MessagesMap map = new MessagesMap(getClass(), Control.CONTROL_MESSAGES);

        assertFalse(map.isEmpty());
        assertTrue(map.size() > 28);

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
    
    /**
     * Tests MessagesMap behavior when properties are missing.
     */
    public void testMissingResourceCaching() {
        MockContext.initContext(Locale.ENGLISH);
        
        // Load the non existing missingResource.properties into map
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

    /**
     * Check that a custom Page's properties are picked up properly.
     */
    public void testPageResources() {
        MockContext.initContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(TestPage.class, "click-page");

        assertFalse(map.isEmpty());
        assertEquals(2, map.size());
    }

    /**
     * Check that message inheritance works properly for custom controls.
     */
    public void testMessageInheritance() {
        MockContext.initContext(Locale.ENGLISH);

        MessagesMap map = new MessagesMap(Test2TextField.class, Control.CONTROL_MESSAGES);

        assertFalse(map.isEmpty());
        assertTrue(map.size() > 30);
        
        assertTrue(map.containsKey("name"));
        assertEquals("Test1TextField", map.get("name"));
        assertEquals("Test2TextField", map.get("classname"));
    }

    /**
     * Create two MessagesMaps for the same class eg. Object.class
     * with different global resources eg. "missingResource" and 
     * "click-control". The first messagesMap is created specifying 
     * "missingResource" as its global resource. Since no 
     * missingResource.properties file exists, this messagesMap will be empty.
     * 
     * Test that the second messagesMap, specifying "click-control" as its 
     * global resource, should pick up the properties from the 
     * click-control.properties file.
     *
     * CLK-274
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
     * Test that the base properties will be picked up when the specified
     * locale and default locale are non-existent. 
     * 
     * Also test that the base properties are picked up when default locale is 
     * non-existent and specified locale is English.
     *
     * CLK-269
     */
    public void testBasePropertiesUsingNonExistentDefaultLocale() {
        Locale defaultLocale = Locale.getDefault();

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

        // Restore default locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * Test that the English locale properties will be picked up when the 
     * default locale is French.
     *
     * CLK-269
     */
    public void testEnglishMessagesUsingFrenchDefaultLocale() {
        Locale defaultLocale = Locale.getDefault();

        Locale locale = new Locale("fr", "FR");

        //Set French as default locale
        Locale.setDefault(locale);

        MockContext.initContext(locale);
        MessagesMap nonEnglishMessages = 
          new ReloadableMessagesMap(Object.class, "click-control");
        assertFalse(nonEnglishMessages.isEmpty());

        //Test that French property is picked up
        assertTrue(nonEnglishMessages.get("table-first-label").toString().indexOf("Premi") == 0);

        //While using a default French locale, test that a English specified
        //locale picks up properties from click-control.properties
        MockContext.initContext(Locale.ENGLISH);
        MessagesMap englishMessages = new ReloadableMessagesMap(Object.class, 
          "click-control");
        assertFalse(englishMessages.isEmpty());
        assertEquals("First", englishMessages.get("table-first-label"));

        // Restore default locale
        Locale.setDefault(defaultLocale);
    }

    /**
     * This messagesMap subclass will clear its static cached properties each
     * time a new instance is created. The properties will then be reloaded.
     */
    private class ReloadableMessagesMap extends MessagesMap {

        public ReloadableMessagesMap(Class<?> baseClass, String globalResource) {
            super(baseClass, globalResource);
            clearCache();
        }

        public void clearCache() {
        	MESSAGES_CLASSLOADER_CACHE.clear();
            clearResourceBundleCache();
        }

        private void clearResourceBundleCache() {
            try {
                Class<?> type = ResourceBundle.class;
                Field cacheList = type.getDeclaredField("cacheList");
                cacheList.setAccessible(true);
                ((Map<?, ?>) cacheList.get(ResourceBundle.class)).clear();
                cacheList.setAccessible(false);
            } catch (Exception jvmNotSupported) {
                System.out.println("WARNING: Could not clear the MessagesMap " +
                  "cache. This could lead to some test cases failing, because " +
                  "cached properties from previous tests could be returned.");
            }

        }
    }
}
