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
 * Test TextArea behavior.
 */
public class TextAreaTest extends TestCase {

    /**
     * Test TextArea onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        TextArea textArea = new TextArea("text");
        assertEquals("text", textArea.getName());
        
        request.setParameter("text", "textvalue");
        
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("textvalue", textArea.getValue());
        assertEquals("textvalue", textArea.getValueObject());
        
        request.setParameter("text", "");
        
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("", textArea.getValue());
        assertEquals(null, textArea.getValueObject());
        
        textArea.setRequired(true);
        
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("", textArea.getValue());
        assertEquals(null, textArea.getValueObject());
        
        request.setParameter("text", "ratherlongtextvalue");
        
        textArea.setMinLength(10);
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());
                
        textArea.setMinLength(20);
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());   
        
        textArea.setMinLength(0);
        
        textArea.setMaxLength(20);
        assertTrue(textArea.onProcess());
        assertTrue(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());
                
        textArea.setMaxLength(10);
        assertTrue(textArea.onProcess());
        assertFalse(textArea.isValid());
        assertEquals("ratherlongtextvalue", textArea.getValue());
        assertEquals("ratherlongtextvalue", textArea.getValueObject());   
    }

    /**
     * Check that textfield value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        TextArea field = new TextArea("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        field.setValue(value);
        assertTrue(field.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }
}
