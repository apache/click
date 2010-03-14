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

    /**
     * Check that TextField label style is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testLabelStyle() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setLabelStyle("color: green");
        assertTrue(form.toString().contains("<label for=\"form_field\" style=\"color: green\">"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setLabelStyle("color: green");
        assertTrue(fs.toString().contains("<label for=\"form_field\" style=\"color: green\">"));
    }

    /**
     * Check that TextField label style class is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testLabelStyleClass() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setLabelStyleClass("autumn");
        assertTrue(form.toString().contains("<label for=\"form_field\" class=\"autumn\">"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        // FieldStyle value should be overridden by the parentStyleHint below
        form.setFieldStyle("font-weight:bold");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setLabelStyleClass("autumn");
        assertTrue(fs.toString().contains("<label for=\"form_field\" class=\"autumn\">"));
    }

    /**
     * Check that TextField parent style hint is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testParentStyleHint() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setParentStyleHint("color: green");
        // Check that style hint is rendered on the label and field cells
        assertTrue(form.toString().contains("<td class=\"fields\" align=\"left\" style=\"color: green\"><label"));
        assertTrue(form.toString().contains("<td align=\"left\" style=\"color: green\"><input"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        // FieldStyle value should be overridden by the parentStyleHint below
        form.setFieldStyle("font-weight:bold");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setParentStyleHint("color: green");
        // Check that style hint is rendered on the label and field cells
        assertTrue(fs.toString().contains("<td class=\"fields\" align=\"left\" style=\"color: green\"><label"));
        assertTrue(fs.toString().contains("<td align=\"left\" style=\"color: green\"><input"));
    }

    /**
     * Check that TextField parent style class hint is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testParentStyleClassHint() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setParentStyleClassHint("autumn");
        // Check that style class hint is rendered on the label and field cells
        assertTrue(form.toString().contains("<td class=\"fields autumn\" align=\"left\"><label"));
        assertTrue(form.toString().contains("<td class=\"autumn\" align=\"left\"><input"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setParentStyleClassHint("autumn");
        // Check that style class hint is rendered on the label and field cells
        assertTrue(fs.toString().contains("<td class=\"fields autumn\" align=\"left\"><label"));
        assertTrue(fs.toString().contains("<td class=\"autumn\" align=\"left\"><input"));
    }

    /**
     * Test the TextField trim property behavior.
     *
     * CLK-627
     */
    public void testTrim() {
         String trimmedValue = "value";
         String value = " " + trimmedValue + " ";

         MockContext context = MockContext.initContext();
         context.getMockRequest().setParameter("field", value);

        Field field = new TextField("field");
        field.onProcess();

        // Check that the field trims its request value
        assertEquals(trimmedValue, field.getValue());

        // Test again, this time switching off the trim property
        field = new TextField("field");
        field.setTrim(false);
        field.onProcess();

        // Check that field does not trim its request value
        assertEquals(value, field.getValue());
    }
}
