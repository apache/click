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
package org.apache.click.control;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.click.MockContext;

/**
 * Test Radio behavior.
 */
public class RadioGroupTest extends TestCase {

    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        MockContext.initContext();

        RadioGroup group = new RadioGroup("group");
        assertEquals("group", group.getName());
        assertFalse(group.isRequired());

        group = new RadioGroup("group", true);
        assertEquals("group", group.getName());
        assertTrue(group.isRequired());

        group = new RadioGroup("group", "label");
        assertEquals("group", group.getName());
        assertEquals("label", group.getLabel());

        group = new RadioGroup("group", "label", true);
        assertEquals("group", group.getName());
        assertEquals("label", group.getLabel());
        assertTrue(group.isRequired());

        group = new RadioGroup();
        assertNull(group.getName());
        assertEquals("", group.getLabel());
        assertFalse(group.isRequired());
    }
    
    /**
     * Coverage test of add(Radio).
     */
    public void testAdd() {
        MockContext.initContext();
        
        RadioGroup group = new RadioGroup("group");
        Radio r1 = new Radio("val1");
        Radio r2 = new Radio("val2");
        group.add(r1);
        group.add(r2);
        
        assertSame(group, r1.getParent());
        assertSame(group, r2.getParent());
        
        assertTrue(group.getRadioList().contains(r1));
        assertTrue(group.getRadioList().contains(r2));
    }

    /**
     * Coverage test of addAll(Collection<Radio>).
     */

    public void testAddAll() {
        MockContext.initContext();
        
        RadioGroup group = new RadioGroup("group");
        
        Radio r1 = new Radio("val1");
        Radio r2 = new Radio("val2");

        group.addAll(Arrays.asList(r1, r2));
        
        assertSame(group, r1.getParent());
        assertSame(group, r2.getParent());
        
        assertTrue(group.getRadioList().contains(r1));
        assertTrue(group.getRadioList().contains(r2));
    }
    
    /**
     * Coverage test of addAll(Map).
     */
    public void testAddAllMap() {
        MockContext.initContext();
        
        RadioGroup group = new RadioGroup("group");
        
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
        map.put(1, "one");
        map.put("two", 2);
        
        group.addAll(map);
        
        assertEquals(group.getRadioList().get(0).getValue(), "1");
        assertEquals(group.getRadioList().get(0).getLabel(), "one");
        assertEquals(group.getRadioList().get(1).getValue(), "two");
        assertEquals(group.getRadioList().get(1).getLabel(), "2");
    }

    /**
     * Coverage test of addAll(Collection<Object>).
     */
    public void testAddAllObject() {
        MockContext.initContext();
        
        RadioGroup group = new RadioGroup("group");
        
        List<RadioFoo> list = Arrays.asList(new RadioFoo("value1", "label1"), new RadioFoo("value2", "label2"));
        
        group.addAll(list, "val", "lab");
        
        assertEquals(group.getRadioList().get(0).getValue(), "value1");
        assertEquals(group.getRadioList().get(0).getLabel(), "label1");
        assertEquals(group.getRadioList().get(1).getValue(), "value2");
        assertEquals(group.getRadioList().get(1).getLabel(), "label2");
    }

    public static class RadioFoo {
        String value;
        String label;
        
        RadioFoo(String value, String label) {
            this.value = value;
            this.label = label;
        }
        
        public String getVal() {
            return value;
        }
        public String getLab() {
            return label;
        }
    }
    
    /**
     * Coverage test of addAll(Collection<Object>).
     */
    public void testFocusJs() {
        MockContext.initContext();
        
        RadioGroup group = new RadioGroup("group");
        Radio r1 = new Radio("val1");
        Radio r2 = new Radio("val2");
        group.add(r1);
        group.add(r2);

        System.out.println(group.getFocusJavaScript());
        
        group = new RadioGroup("group");
        System.out.println(group.getFocusJavaScript());

    }
}
