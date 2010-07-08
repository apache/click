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
package org.apache.click.extras.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.util.HtmlStringBuffer;

import junit.framework.TestCase;

public class HiddenListTest extends TestCase {

    public void testRender() {
        HiddenList hiddenList = new HiddenList("hiddenList");
        hiddenList.addValue("A");
        hiddenList.addValue("B");
        hiddenList.addValue("C");
        
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        hiddenList.render(buffer);
        
        assertEquals(
                "<input type=\"hidden\" name=\"hiddenList\" id=\"hiddenList_1\" value=\"A\"/>" +
                "<input type=\"hidden\" name=\"hiddenList\" id=\"hiddenList_2\" value=\"B\"/>" + 
                "<input type=\"hidden\" name=\"hiddenList\" id=\"hiddenList_3\" value=\"C\"/>", 
                buffer.toString());
    }

    public void testIsHidden() {
        HiddenList hiddenList = new HiddenList("hiddenList");
        assertTrue(hiddenList.isHidden());
    }

    public void testGetValueObject() {
        HiddenList hiddenList = new HiddenList("hiddenList");
        hiddenList.addValue("A");
        hiddenList.addValue("B");
        hiddenList.addValue("C");
        
        List<?> valueObject = (List<?>) hiddenList.getValueObject();
        assertEquals(3, valueObject.size());
        assertEquals("A", valueObject.get(0));
        assertEquals("B", valueObject.get(1));
        assertEquals("C", valueObject.get(2));
    }

    public void testSetValueObject() {
        HiddenList hiddenList = new HiddenList("hiddenList");
        
        List<String> valueObject = new ArrayList<String>();
        valueObject.add("A");
        valueObject.add("B");
        valueObject.add("C");
        
        hiddenList.setValueObject(valueObject);
        
        List<String> values = hiddenList.getValues();
        assertEquals(3, values.size());
        assertEquals("A", values.get(0));
        assertEquals("B", values.get(1));
        assertEquals("C", values.get(2));
    }

}
