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

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;

public class IntegerFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        
        IntegerField intField = new IntegerField("id");
        assertEquals("id", intField.getName());
        
        request.getParameterMap().put("id", "1234");
        
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("1234", intField.getValue());
        assertEquals(new Integer(1234), intField.getValueObject());
        
        request.getParameterMap().put("id", "123.4");
        
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("123.4", intField.getValue());
        assertNull(intField.getValueObject());
        
        // Test not required + blank value
        request.getParameterMap().put("id", "");
        
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("", intField.getValue());
        assertNull(intField.getValueObject());

        // Test not required + blank value
        request.getParameterMap().put("id", "");
        intField.setRequired(true);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("", intField.getValue());
        assertNull(intField.getValueObject());
        
        request.getParameterMap().clear();
        
        request.getParameterMap().put("id", "0");
        
        intField.setRequired(true);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("0", intField.getValue());
        assertNotNull(intField.getValueObject());
        assertEquals(new Integer(0), intField.getValueObject());
        
        intField.setRequired(false);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("0", intField.getValue());
        assertNotNull(intField.getValueObject());
        assertEquals(new Integer(0), intField.getValueObject());
        
        request.getParameterMap().put("id", "10");
        
        intField.setMinValue(10);     
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("10", intField.getValue());
        assertEquals(new Integer(10), intField.getValueObject());
        
        intField.setMinValue(11);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("10", intField.getValue());
        assertEquals(new Integer(10), intField.getValueObject());
        
        request.getParameterMap().put("id", "20");
        
        intField.setMaxValue(20);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        intField.setMaxValue(20);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        intField.setMaxValue(19);
        assertTrue(intField.onProcess());
        assertFalse(intField.isValid());
        assertEquals("20", intField.getValue());
        assertEquals(new Integer(20), intField.getValueObject());
        
        assertEquals(new Integer(20), intField.getInteger());
        assertEquals(new Long(20), intField.getLong());
        
        request.getParameterMap().put("id", "-20");
        
        intField.setMinValue(-21);
        assertTrue(intField.onProcess());
        assertTrue(intField.isValid());
        assertEquals("-20", intField.getValue());
        assertEquals(new Integer(-20), intField.getValueObject());
    }

}
