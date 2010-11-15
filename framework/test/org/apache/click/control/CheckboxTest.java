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

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;
import org.apache.commons.lang.StringUtils;

/**
 * Test Checkbox behavior.
 */
public class CheckboxTest extends TestCase {

    /**
     * Check that Checkbox value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Checkbox checkbox = new Checkbox("name");
        String value = "<script>";
        String valueAttr = "value=";

        checkbox.setValue(value);

        // Check that checkbox does not render a value attribute
        assertEquals(false, StringUtils.contains(checkbox.toString(), valueAttr));
        
        // Check that the value <script> is not rendered
        assertTrue(checkbox.toString().indexOf(value) < 0);
    }
    
    /**
     * Test TextField onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        Checkbox checkbox = new Checkbox("checkbox");
        assertEquals("checkbox", checkbox.getName());
        
        request.setParameter("checkbox", "");
        
        assertTrue(checkbox.onProcess());
        assertTrue(checkbox.isValid());
        assertEquals("true", checkbox.getValue());
        assertEquals(Boolean.TRUE, checkbox.getValueObject());
        
        request.setParameter("checkbox", "true");
        
        assertTrue(checkbox.onProcess());
        assertTrue(checkbox.isValid());
        assertEquals("true", checkbox.getValue());
        assertEquals(Boolean.TRUE, checkbox.getValueObject());
        
        checkbox.setRequired(true);
        request.removeParameter("checkbox");
        
        assertTrue(checkbox.onProcess());
        assertFalse(checkbox.isValid());
        assertEquals("false", checkbox.getValue());
        assertEquals(Boolean.FALSE, checkbox.getValueObject());
        assertTrue(checkbox.toString().contains("class=\"error\""));
        
        request.setParameter("checkbox", "true");

        assertTrue(checkbox.onProcess());
        assertTrue(checkbox.isValid());
        assertEquals("true", checkbox.getValue());
        assertEquals(Boolean.TRUE, checkbox.getValueObject());
        
        request.setParameter("checkbox", "true");

        checkbox.setDisabled(true);
        assertTrue(checkbox.onProcess());
        assertTrue(checkbox.isValid());
        assertFalse(checkbox.isDisabled());
    }
    
    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        Checkbox field = new Checkbox("field", true);
        assertTrue(field.isRequired());
        
        field = new Checkbox("field", "label");
        assertEquals("label", field.getLabel());
        
        field = new Checkbox();
        assertNull(field.getName());
    }
    
    /**
     * Coverage test of tab-index.
     */
    public void testTabIndex() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        field.setTabIndex(5);

        assertTrue(field.toString().contains("tabindex=\"5\""));
    }

    /**
     * Coverage test of disabled property.
     */
    public void testDisabled() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        field.setDisabled(true);
        assertTrue(field.toString().contains("disabled=\"disabled\""));
    }

    /**
     * Coverage test of readonly property.
     */
    public void testReadonly() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        field.setReadonly(true);

        assertTrue(field.toString().contains("disabled=\"disabled\""));
        
        field = new Checkbox("field");
        field.setReadonly(true);
        field.setChecked(true);

        assertTrue(field.toString().contains("disabled=\"disabled\""));
        assertTrue(field.toString().contains("<input type=\"hidden\""));

    }

    /**
     * Coverage test of help property.
     */
    public void testHelp() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        field.setHelp("help");

        assertTrue(field.toString().contains("help"));
    }

    /**
     * Coverage test of validation javascript.
     */
    public void testValidationJS() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        assertNull(field.getValidationJavaScript());
        
        field = new Checkbox("field");
        field.setRequired(true);

        assertTrue(field.getValidationJavaScript().startsWith("function validate_field()"));
    }
    
    public void testValue() {
        MockContext.initContext();
        
        Checkbox field = new Checkbox("field");
        field.setValue("xxx");
        assertFalse(field.isChecked());
        
        field.setValue("true");
        assertTrue(field.isChecked());
        field.setValue("false");
        assertFalse(field.isChecked());
        
        field.setChecked(false);
        
        field.setValueObject("xxx");
        assertFalse(field.isChecked());
        field.setValueObject(null);
        assertFalse(field.isChecked());

        field.setValueObject(true);
        assertTrue(field.isChecked());
    }

    /**
     * Coverage test of onProcess for an unchecked Checkbox.
     */
    public void testUncheckedOnProcess() {
        MockContext.initContext();

        Checkbox field = new Checkbox("field");

        // Initially checkbox is checked
        field.setChecked(true);

        assertTrue(field.isChecked());

        // Note, no request parameter for the checkbox has been set, so it should
        // be unchecked
        field.onProcess();

        // Check that checkbox hs been unchecked
        assertFalse(field.isChecked());
    }
}
