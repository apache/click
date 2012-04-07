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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for PropertyUtils.
 */
public class PropertyUtilsTest extends TestCase {

    /**
     * Sanity checks for PropertyUtils.
     */
    public void testGetProperty() {
        try {
            PropertyUtils.getValue(new Object(), "username", new HashMap<Object, Object>());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            PropertyUtils.getValue(new Object(), "class", new HashMap<Object, Object>());
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        ParentObject testObject = new ParentObject();
        Map<?, ?> cache = new HashMap<Object, Object>();

        assertNull(PropertyUtils.getValue(testObject, "name", cache));
        assertNull(PropertyUtils.getValue(testObject, "value", cache));
        assertNull(PropertyUtils.getValue(testObject, "date", cache));
        assertNull(PropertyUtils.getValue(testObject, "child", cache));

        assertNull(PropertyUtils.getValue(testObject, "name"));
        assertNull(PropertyUtils.getValue(testObject, "value"));
        assertNull(PropertyUtils.getValue(testObject, "date"));
        assertNull(PropertyUtils.getValue(testObject, "child"));

        ParentObject parentObject =
            new ParentObject("malcolm", null, new Date(), Boolean.TRUE,
            new ChildObject("edgar", "medgar@avoka.com"));

        assertEquals("malcolm", PropertyUtils.getValue(parentObject, "name",
            cache));
        assertNull(PropertyUtils.getValue(parentObject, "value", cache));
        assertNotNull(PropertyUtils.getValue(parentObject, "date", cache));
        assertNotNull(PropertyUtils.getValue(parentObject, "valid", cache));
        assertEquals("edgar", PropertyUtils.getValue(parentObject, "child.name",
            cache));
        assertEquals("medgar@avoka.com", PropertyUtils.getValue(parentObject,
            "child.email", cache));


        assertEquals("malcolm", PropertyUtils.getValue(parentObject, "name"));
        assertNull(PropertyUtils.getValue(parentObject, "value"));
        assertNotNull(PropertyUtils.getValue(parentObject, "date"));
        assertNotNull(PropertyUtils.getValue(parentObject, "valid"));
        assertEquals("edgar", PropertyUtils.getValue(parentObject, "child.name"));
        assertEquals("medgar@avoka.com", PropertyUtils.getValue(parentObject,
            "child.email"));
    }

    /**
     * Test that PropertyUtils can extract value from Map.
     */
    public void testMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "malcolm");

        assertEquals("malcolm", PropertyUtils.getValue(map, "name"));
    }
    
}
