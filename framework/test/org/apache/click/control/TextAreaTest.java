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
        assertTrue(textArea.toString().contains("class=\"error\""));
    }

    /**
     * Check that textfield value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        TextArea field = new TextArea("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        field.setValue(value);
        assertTrue(field.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }
    
    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        TextArea field = new TextArea();
        assertNull(field.getName());
        
        field = new TextArea("field", true);
        assertTrue(field.isRequired());
        
        field = new TextArea("field", "label");
        assertEquals("label", field.getLabel());
        
        field = new TextArea("field", "label", true);
        assertEquals("label", field.getLabel());
        assertTrue(field.isRequired());

        field = new TextArea("field", 25, 4);
        assertEquals(25, field.getCols());
        assertEquals(4, field.getRows());
        
        field = new TextArea("field", "label", 25, 4);
        assertEquals("label", field.getLabel());
        assertEquals(25, field.getCols());
        assertEquals(4, field.getRows());

        field = new TextArea("field", "label", 25, 4, true);
        assertEquals("label", field.getLabel());
        assertEquals(25, field.getCols());
        assertEquals(4, field.getRows());
        assertTrue(field.isRequired());
    }
    
    /**
     * Coverage test of tab-index.
     */
    public void testTabIndex() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");
        field.setTabIndex(5);

        assertTrue(field.toString().contains("tabindex=\"5\""));
    }

    /**
     * Coverage test of disabled property.
     */
    public void testDisabled() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");
        field.setDisabled(true);

        assertTrue(field.toString().contains("class=\"disabled\""));
        assertTrue(field.toString().contains("disabled=\"disabled\""));
    }

    /**
     * Coverage test of readonly property.
     */
    public void testReadonly() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");
        field.setReadonly(true);

        assertTrue(field.toString().contains("readonly=\"readonly\""));
    }

    /**
     * Coverage test of cols and rows property.
     */
    public void testColsRows() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");
        field.setCols(25);
        field.setRows(4);
        
        assertTrue(field.toString().contains("cols=\"25\""));
        assertTrue(field.toString().contains("rows=\"4\""));
    }

    /**
     * Coverage test of help property.
     */
    public void testHelp() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");
        field.setHelp("help");

        assertTrue(field.toString().contains("help"));
    }

    /**
     * Coverage test of validation javascript.
     */
    public void testValidationJS() {
        MockContext.initContext();
        
        TextArea field = new TextArea("field");

        assertTrue(field.getValidationJavaScript().startsWith("function validate_field()"));
    }
}
