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

public class LongFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        
        LongField longField = new LongField("id");
        assertEquals("id", longField.getName());

        request.getParameterMap().put("id", "1234");
        
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("1234", longField.getValue());
        assertEquals(new Long(1234), longField.getValueObject());
        
        request.getParameterMap().put("id", "123.4");
        
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("123.4", longField.getValue());
        assertNull(longField.getValueObject());

        // Test not required + blank value
        request.getParameterMap().put("id", "");
        
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("", longField.getValue());
        assertNull(longField.getValueObject());
        
        longField.setRequired(true);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("", longField.getValue());
        assertNull(longField.getValueObject());
        
        request.getParameterMap().put("id", "10");
        
        longField.setMinValue(10);     
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("10", longField.getValue());
        assertEquals(new Long(10), longField.getValueObject());
        
        longField.setMinValue(11);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("10", longField.getValue());
        assertEquals(new Long(10), longField.getValueObject());
        
        request.getParameterMap().put("id", "20");
        
        longField.setMaxValue(20);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        longField.setMaxValue(20);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        longField.setMaxValue(19);
        assertTrue(longField.onProcess());
        assertFalse(longField.isValid());
        assertEquals("20", longField.getValue());
        assertEquals(new Long(20), longField.getValueObject());
        
        assertEquals(new Long(20), longField.getLong());
        assertEquals(new Integer(20), longField.getInteger());
        
        request.getParameterMap().put("id", "-20");
        
        longField.setMinValue(-21);
        assertTrue(longField.onProcess());
        assertTrue(longField.isValid());
        assertEquals("-20", longField.getValue());
        assertEquals(new Long(-20), longField.getValueObject());
    }

}
