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

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;

/**
 * Test Radio behavior.
 */
public class RadioTest extends TestCase {

    /**
     * Check that Radio value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();
        
        Form form = new Form("form");
        RadioGroup radioGroup = new RadioGroup("group");
        form.add(radioGroup);

        Radio radio = new Radio("name");
        radioGroup.add(radio);

        String value = "<script>";
        String expectedValue = "value=\"&lt;script&gt;\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedValue) > 1);
        
        String expectedId = "form_group_&lt;script&gt;";
        String expectedIdAttr = "id=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedIdAttr) > 1);
        
        String expectedLabelValue = ">&lt;script&gt;</label>";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelValue) > 1);
        
        String expectedLabelForAttr = "for=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelForAttr) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(radio.toString().indexOf(value) < 0);
    }
    
    /**
     * Radio Submit onProcess behavior.
     */
    public void testOnProcess() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        
        Radio button = new Radio("value", "label", "button");
        assertEquals("button", button.getName());
        
        assertTrue(button.onProcess());
        
        request.setParameter("button", "true");
        assertTrue(button.onProcess());
        
        Listener l = new Listener();
        button.setActionListener(l);

        // No request param -> no action listener executed
        request.removeParameter("button");
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertFalse(l.fired);

        // No request param -> no action listener executed
        request.setParameter("button", "value");
        assertTrue(button.onProcess());
        context.executeActionListeners();
        assertTrue(button.isChecked());
        assertTrue(l.fired);

        // Disabled button with request param
        request.setParameter("button", "true");
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertFalse(button.isDisabled());
        
        // Disabled button without request param
        request.removeParameter("button");
        button.setDisabled(true);
        assertTrue(button.onProcess());
        assertTrue(button.isValid());
        assertTrue(button.isDisabled());
    }

    /**
     * Coverage test of constructors.
     */
    public void testConstructors() {
        Radio field = new Radio("value", "label", "field");
        assertEquals("value", field.getValue());
        assertEquals("label", field.getLabel());
        assertEquals("field", field.getName());
        
        field = new Radio("value", "label");
        assertEquals("value", field.getValue());
        assertEquals("label", field.getLabel());
        
        field = new Radio();
        assertEquals("", field.getValue());
        assertEquals("", field.getLabel());
        assertNull(field.getName());
    }
    
    /**
     * Coverage test of getId()
     */
    public void testId() {
        Radio field = new Radio("value", "label", "a/b c<d>e");
        assertEquals("a_b_c_d_e_value", field.getId());
    }

    /**
     * Coverage test of tab-index.
     */
    public void testTabIndex() {
        MockContext.initContext();
        
        Radio field = new Radio("value");
        field.setTabIndex(5);

        assertTrue(field.toString().contains("tabindex=\"5\""));
    }

    /**
     * Coverage test of disabled property.
     */
    public void testDisabled() {
        MockContext.initContext();
        
        Radio field = new Radio("value");
        field.setDisabled(true);
        assertTrue(field.toString().contains("disabled=\"disabled\""));
    }

    /**
     * Coverage test of readonly property.
     */
    public void testReadonly() {
        MockContext.initContext();
        
        Radio field = new Radio("value");
        field.setReadonly(true);

        assertTrue(field.toString().contains("disabled=\"disabled\""));
        
        field = new Radio("value");
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
        
        Radio field = new Radio("value");
        field.setHelp("help");

        // Note that help is currently not rendered.
        assertFalse(field.toString().contains("help"));
    }

    /**
     * Coverage test of validation javascript.
     */
    public void testValidationJS() {
        MockContext.initContext();
        
        Radio field = new Radio("value");
        assertNull(field.getValidationJavaScript());
    }
    
    public void testValue() {
        MockContext.initContext();
        
        Radio field = new Radio("value", "label", "field");
        assertFalse(field.isChecked());
        
        field.setValue("value");
        assertTrue(field.isChecked());
        field.setValue("xxx");
        assertFalse(field.isChecked());
        
        field.setChecked(false);
        
        field.setValueObject("xxx");
        assertFalse(field.isChecked());
        field.setValueObject(null);
        assertFalse(field.isChecked());
    }

    static class Listener implements ActionListener {
        private static final long serialVersionUID = 1L;
        
        public boolean fired;

        public boolean onAction(Control source) {
            fired = true;
            return true;
        }
    }
}
