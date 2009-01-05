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

import org.apache.click.MockContext;
import junit.framework.TestCase;
import org.apache.click.servlet.MockRequest;

/**
 * Test TextField behavior.
 */
public class TextFieldTest extends TestCase {

    /**
     * Test TextField onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        TextField textField = new TextField("text");
        assertEquals("text", textField.getName());
        
        request.getParameterMap().put("text", "textvalue");
        
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("textvalue", textField.getValue());
        assertEquals("textvalue", textField.getValueObject());
        
        request.getParameterMap().put("text", "");
        
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("", textField.getValue());
        assertEquals(null, textField.getValueObject());
        
        textField.setRequired(true);
        
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("", textField.getValue());
        assertEquals(null, textField.getValueObject());
        
        request.getParameterMap().put("text", "ratherlongtextvalue");
        
        textField.setMinLength(10);
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());
                
        textField.setMinLength(20);
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());   
        
        textField.setMinLength(0);
        
        textField.setMaxLength(20);
        assertTrue(textField.onProcess());
        assertTrue(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());
                
        textField.setMaxLength(10);
        assertTrue(textField.onProcess());
        assertFalse(textField.isValid());
        assertEquals("ratherlongtextvalue", textField.getValue());
        assertEquals("ratherlongtextvalue", textField.getValueObject());   
    }

    /**
     * Check that textfield value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        TextField field = new TextField("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        field.setValue(value);
        assertTrue(field.toString().indexOf(expected) > 1);

        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }
}
