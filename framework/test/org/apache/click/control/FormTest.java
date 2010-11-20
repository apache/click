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
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.servlet.MockRequest;
import org.apache.commons.lang.StringUtils;

/**
 * Test Form behavior.
 */
public class FormTest extends TestCase {

    /**
     * Test that it is not possible to add a duplicate SUBMIT_CHECK
     * HiddenField.
     *
     * CLK-267.
     */
    public void testDuplicateOnSubmitCheck() {
        MockContext context = MockContext.initContext("test-form.htm");
        MockRequest request = context.getMockRequest();
        request.setParameter("form_name", "form");

        Page page = new Page();

        // Set the page to stateful
        page.setStateful(true);
        Form form = new Form("form");

        // Construct name of submit token
        String submitCheckName = Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath();

        // Simulate a submit check
        boolean valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);

        // Assert that the submitCheck hidden field was created
        Field submitCheckField = form.getField(submitCheckName);
        Assert.assertNotNull(submitCheckField);

        // Add submitCheckField as a request parameter
        request.setParameter(Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath(), submitCheckField.getValue());

        // Simulate a second submit check.
        valid = form.onSubmitCheck(page, "/invalid-submit.html");

        // Assert the second onSubmitCheck did succeed as well.
        Assert.assertTrue(valid);
    }

    /**
     * Test Form#onSubmitCheck when the SUBMIT_CHECK parameter is
     * missing.
     *
     * CLK-289.
     */
    public void testOnSubmitCheckMissingParam() {
        MockContext context = MockContext.initContext("test-form.htm");
        MockRequest request = context.getMockRequest();
        request.setParameter("form_name", "form");
        Page page = new Page();
        Form form = new Form("form");

        // Construct name of submit token
        String submitTokenName = Form.SUBMIT_CHECK + form.getName() + "_" + context.getResourcePath();

        // Ensure there are no submitCheck hidden field yet
        Field submitCheckField = form.getField(submitTokenName);
        Assert.assertNull(submitCheckField);

        // Simulate a submit check
        boolean valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);

        // Add the submitCheckField name and value to the parameters.
        submitCheckField = form.getField(submitTokenName);
        request.setParameter(submitTokenName, submitCheckField.getValue());

        // If we submit again, the assert should be true because the submit
        // token is set in the request parameters.
        valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertTrue(valid);

        // Now imagine the SUBMIT_CHECK token is removed by a hacker. To simulate
        // such a scenario we remove the submitTokenName from the request paramters.
        request.removeParameter(submitTokenName);
        valid = form.onSubmitCheck(page, "/invalid-submit.html");
        Assert.assertFalse(valid);
    }

    /**
     * Test that form processing binds a request parameter to a field value.
     */
    public void testFormOnProcessRequestBinding() {
        // Create a mock context
        MockContext context = MockContext.initContext("test-form.htm");
        MockRequest request = context.getMockRequest();

        // The request value that should be set as the textField value
        String requestValue = "one";

        // Set form name and field name parameters
        request.setParameter("form_name", "form");
        request.setParameter("name", requestValue);

        // Create form and fields
        Form form = new Form("form");
        TextField nameField = new TextField("name");
        form.add(nameField);

        // Check that nameField value is null
        Assert.assertNull(nameField.getValueObject());

        // Simulate a form onProcess callback
        form.onProcess();

        // Check that nameField value is now bound to request value
        Assert.assertEquals(requestValue, nameField.getValueObject());
    }

    /**
     * Check that Form processes controls even if their names is not defined.
     *
     * CLK-463
     */
    public void testProcessControlWhenNameIsNull() {
        MockContext context = MockContext.initContext();
        context.getMockRequest().setParameter("form_name", "form");
        String fieldValue = "test";
        context.getMockRequest().setParameter("field", fieldValue);

        Form form = new Form("form");
        Panel panel = new Panel();
        TextField textField = new TextField("field");
        panel.add(textField);
        form.add(panel);

        assertEquals("", textField.getValue());

        form.onProcess();

        assertEquals(fieldValue, textField.getValue());
    }

    /** Form which index position to test. */
    private Form testForm;

    /** Field which position is tracked in the Form. */
    private HiddenField trackField;

    /**
     * Setup the form and trackField instances for testing the Control index
     * positions after being added or inserted.
     */
    @Override
    public void setUp() {
        // Create form.
        testForm = new Form("form");

        // Form automatically creates and adds one HiddenField for storing
        // the form name between requests. The form name field is at index 1
        // at the start of each test.
        // The tests below checks the trackField index position in the Form
        // for the lists Form#controls, Form#fieldList and Form#buttonList
        trackField = (HiddenField) testForm.getField(Form.FORM_NAME);

        // trackField index: #controls=0
        assertTrue(testForm.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(testForm.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that Form#add(Control) inserts field at the correct index.
     *
     * The Form#controls list should be affected by the insert index, while
     * Form#fieldList should not.
     */
    public void testInsertOrderAfterAddingField() {
        TextField nameField = new TextField("name");

        // Add new field
        testForm.add(nameField);

        // nameField index: #controls=0
        assertTrue(testForm.getControls().indexOf(nameField) == 0);
        // nameField index: #fieldList=0
        assertTrue(testForm.getFieldList().indexOf(nameField) == 0);

        // trackField index: #controls=1
        assertTrue(testForm.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=1
        assertTrue(testForm.getFieldList().indexOf(trackField) == 1);
    }

    /**
     * Test that Form#insert(Control, int) inserts field at the correct index.
     *
     * The Form#controls list should be affected by the insert index, while
     * Form#fieldList should not.
     */
    public void testInsertOrderAfterInsertingField() {
        TextField nameField = new TextField("name");

        // Insert field at index 0
        testForm.insert(nameField, 0);

        // nameField index: #controls=0
        assertTrue(testForm.getControls().indexOf(nameField) == 0);
        // nameField index: #fieldList=0
        assertTrue(testForm.getFieldList().indexOf(nameField) == 0);

        // trackField index: #controls=1
        assertTrue(testForm.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=1
        assertTrue(testForm.getFieldList().indexOf(trackField) == 1);
    }

    /**
     * Test that Form#insert(Control, int) inserts table at the correct index.
     *
     * The Form#controls list should be affected by the insert index, while
     * Form#fieldList should not contain table as Table is not a Field.
     */
    public void testInsertOrderAfterInsertingTable() {
        Table table = new Table("table");

        // Insert table at index 0
        testForm.insert(table, 0);

        // table index: #controls=0
        assertTrue(testForm.getControls().indexOf(table) == 0);
        // table index: #fieldList=-1
        assertTrue(testForm.getFieldList().indexOf(table) == -1);

        // trackField index: #controls=1
        assertTrue(testForm.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(testForm.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that Form#insert(Control, int) inserts Button at the correct index.
     *
     * The Form#controls list should be affected by the insert index, while
     * Form#buttonList should not.
     */
    public void testInsertOrderAfterInsertingButton() {
        // Check that the fieldList includes only hidden field, FORM_NAME,
        // thus size is 1
        assertTrue(testForm.getFieldList().size() == 1);

        Button button = new Button("button1");

        testForm.insert(button, 0);

        // button index: #controls=0
        assertTrue(testForm.getControls().indexOf(button) == 0);
        // button index: #buttonList=0
        assertTrue(testForm.getButtonList().indexOf(button) == 0);
        // Check that button was not added to fieldList accidentally
        assertTrue(testForm.getFieldList().size() == 1);
    }

    /**
     * Test that Form#insert(Control, int) inserts HiddenField at the correct index.
     *
     * The Form#controls list should be affected by the insert index, while
     * Form#fieldList should not.
     */
    public void testInsertOrderAfterInsertingHiddenField() {
        HiddenField hidden = new HiddenField("hidden", Boolean.class);

        // Insert hidden at index 0
        testForm.insert(hidden, 0);

        // hidden index: #controls=0
        assertTrue(testForm.getControls().indexOf(hidden) == 0);
        // hidden index: #fieldList=0
        assertTrue(testForm.getFieldList().indexOf(hidden) == 0);

        // trackField index: #controls=1
        assertTrue(testForm.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=1
        assertTrue(testForm.getFieldList().indexOf(trackField) == 1);
    }

    /**
     * Test that Form#remove(Control) properly cleans up.
     */
    public void testRemove() {
        TextField field = new TextField("field");

        testForm.insert(field, 0);

        int expectedIndex = 0;
        // field index: #controls=0
        assertEquals(expectedIndex, testForm.getControls().indexOf(field));
        // field index: #fieldList=0
        assertEquals(expectedIndex, testForm.getFieldList().indexOf(field));

        int expectedSize = 1;
        // trackField index: #controls=1
        assertEquals(expectedSize, testForm.getControls().indexOf(trackField));
        // trackField index: #fieldList=1
        assertEquals(expectedSize, testForm.getFieldList().indexOf(trackField));

        expectedSize = 2;
        // Check the list sizes to be 2
        assertEquals(expectedSize, testForm.getControls().size());
        assertEquals(expectedSize, testForm.getFieldList().size());

        // Removing field should shift up trackField index
        testForm.remove(field);

        expectedSize = 1;
        // Check the list sizes to be 1
        assertEquals(expectedSize, testForm.getControls().size());
        assertEquals(expectedSize, testForm.getFieldList().size());

        // trackField index: #controls=0
        expectedSize = 0;
        assertEquals(expectedSize, testForm.getControls().indexOf(trackField));
        // trackField index: #fieldList=0
        assertEquals(expectedSize, testForm.getFieldList().indexOf(trackField));
    }

    /**
     * Test that Form#add(Control, int) and Form#remove(Control) properly
     * sets and removes the fieldWidth for a Field.
     */
    public void testFieldWidthForField() {
        TextField field = new TextField("field");

        // Check that fieldWidth is empty
        assertTrue(testForm.getFieldWidths().isEmpty());

        int colspan = 4;
        testForm.add(field, colspan);

        // Check that fieldWidth has entry for field
        assertTrue(testForm.getFieldWidths().size() == 1);

        Integer width = testForm.getFieldWidths().get(field.getName());
        assertEquals(4, width.intValue());

        testForm.remove(field);

        // Check that fieldWidth is empty
        assertTrue(testForm.getFieldWidths().isEmpty());
    }

    /**
     * Test that Form#add(Control, int) and Form#remove(Control) properly
     * sets and removes the fieldWidth for a Control.
     */
    public void testFieldWidthForTable() {
        Table table = new Table("table");

        // Check that fieldWidth is empty
        assertTrue(testForm.getFieldWidths().isEmpty());

        int colspan = 4;
        testForm.add(table, colspan);

        // Check that fieldWidth has entry for table
        assertTrue(testForm.getFieldWidths().size() == 1);

        Integer width = testForm.getFieldWidths().get(table.getName());
        assertEquals(4, width.intValue());

        testForm.remove(table);

        // Check that fieldWidth is empty
        assertTrue(testForm.getFieldWidths().isEmpty());
    }

    /**
     * Test that Form.getFieldList() return only Fields directly added to the
     * field list. The list should include FieldSets but exclude Buttons.
     *
     * The main use case for Form.getFieldList() is that it enables one to use
     * Velocity to render Fields e.g.:
     *
     * #foreach ($field in $form.fieldList)
     *   <td>$field.label:</td> <td>$field</td>
     * #end
     *
     * Also check that Form.getFieldList() returns a cached List so that access
     * to Form.getFieldList() is fast.
     *
     * Q: Why not include fields recursively?
     * A: Its not really needed for Velocity usage. For example one would rarely
     *    wrap a field inside a "border container" when the rendering will be
     *    done with Velocity. Instead one would rather wrap the field inside
     *    a "border container" in the Velocity template e.g. wrapping it inside a
     *    span element.
     *        <span>$field</span>
     *
     *    If recursive fields are needed users can use ContainerUtils.getFields().
     */
    public void testGetFieldList() {
        Form form = new Form("form");

        // Assemble Fields
        TextField nameField = new TextField("name");
        form.add(nameField);

        Div div = new Div("div");
        form.add(div);

        Button button = new Button("button");
        form.add(button);

        // Assemble FieldSet
        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        // Add Field to FieldSet
        TextField fieldSetField = new TextField("fieldSetField");
        fieldSet.add(fieldSetField);

        // Check that list contains TextField
        assertTrue(form.getFieldList().contains(nameField));

        // Check that list contains FieldSet
        assertTrue(form.getFieldList().contains(fieldSet));

        // Check that list does *not* contains the FieldSet's Field
        assertFalse(form.getFieldList().contains(fieldSetField));

        // Check that list does *not* contain Div
        assertFalse(form.getFieldList().contains(div));

        // Check that list does *not* contain Button
        assertFalse(form.getFieldList().contains(button));

        // Check that field list is cached
        assertSame(form.getFieldList(), form.getFieldList());
    }

    /**
     * Test that Form.getButtonList() return only Buttons directly added to
     * Form and exclude all other Controls.
     *
     * The main use case for Form.getButtonList() is that it enables one to use
     * Velocity to render Buttons e.g.:
     *
     * #foreach ($button in $form.buttonList)
     *   $button
     * #end
     *
     * Also check that Form.getButtonList() returns a cached List so that access
     * to Form.getButtonList() is fast.
     *
     * Q: Why not include buttons recursively?
     * A: Its not really needed for Velocity usage. For example one would rarely
     *    wrap a button inside a "border container" when the rendering will be
     *    done with Velocity. Instead one would rather wrap the button inside
     *    a "border container" in the Velocity template e.g. wrapping it inside a
     *    span element:
     *        <span>$button</span>
     *
     *    If recursive buttons are needed users can use ContainerUtils.getButtons().
     */
    public void testGetButtonList() {
        Form form = new Form("form");

        // Assemble Fields
        TextField nameField = new TextField("name");
        form.add(nameField);

        Div div = new Div("div");
        form.add(div);

        Button button = new Button("button");
        form.add(button);

        // Assemble FieldSet
        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        // Add Field to FieldSet
        TextField fieldSetField = new TextField("fieldSetField");
        fieldSet.add(fieldSetField);

        // Add Button to FieldSet
        Button fieldSetButton = new Button("fieldSetButton");
        fieldSet.add(fieldSetButton);

        // Check that button list does *not* contains TextField
        assertFalse(form.getButtonList().contains(nameField));

        // Check that button list does *not* contains FieldSet
        assertFalse(form.getButtonList().contains(fieldSet));

        // Check that button list does *not* contains the FieldSet's Field
        assertFalse(form.getButtonList().contains(fieldSetField));

        // Check that button list does *not* contain Div
        assertFalse(form.getButtonList().contains(div));

        // Check that button list does contain Button
        assertTrue(form.getButtonList().contains(button));

        // Check that button list does *not* contain FieldSetButton since that
        // button was not directly added to Form
        assertFalse(form.getButtonList().contains(fieldSetButton));

        // Check that button list is cached
        assertSame(form.getButtonList(), form.getButtonList());
    }

    /**
     * Test that Form.getFields() return all Controls by delegating to
     * AbstractContainer#getControlMap().
     *
     * The main use case for Form.getFields() is that it enables one to use
     * Velocity to render Controls and Fields e.g.:
     *
     * $form.fields.name   <- name this is a TextField
     * $form.fields.div    <- div is a AbstractContainer
     * $form.fields.button <- button is a Submit
     *
     * Also check that Form.getFields() returns a cached Map so that access to
     * Form.getFields() is fast.
     *
     * Q: Why not return Fields only?
     * A: Form will then have to maintain another map besides #controlMap.
     *    However it could lead to issues when users iterate the map and receive
     *    ClassCastExceptions when casting to Field. In future release this issue
     *    could be revisited.
     */
    public void testGetFields() {
        Form form = new Form("form");

        // Assemble Fields
        String fieldName = "name";
        form.add(new TextField(fieldName));

        String divName = "div";
        form.add(new Div(divName));

        String buttonName = "button";
        form.add(new Button(buttonName));

        // Assemble FieldSet
        String fieldSetName = "fieldSet";
        FieldSet fieldSet = new FieldSet(fieldSetName);
        form.add(fieldSet);

        // Add Field to FieldSet
        String fieldSetFieldName = "fieldSetField";
        fieldSet.add(new TextField(fieldSetFieldName));

        // Check that map contains both TextField, Button and Div
        assertTrue(form.getFields().containsKey(fieldName));
        assertTrue(form.getFields().containsKey(divName));
        assertTrue(form.getFields().containsKey(buttonName));

        // Check that map contains FieldSet
        assertTrue(form.getFields().containsKey(fieldSetName));

        // Check that map does *not* contain the FieldSet's Field
        assertFalse(form.getFields().containsKey(fieldSetFieldName));

        // Check that field map is cached
        assertSame(form.getFields(), form.getFields());
        assertSame(form.getFields(), form.getControlMap());
    }

    /**
     * Test that insert(int, Control) works properly.
     */
    public void testInsert() {
        Form form = new Form("form");

        int defaultFieldSize = 17;
        form.setDefaultFieldSize(defaultFieldSize);

        TextField field = new TextField("name");

        // Assert that defaultFieldSize is not equal to field size
        assertFalse(defaultFieldSize == field.getSize());

        form.add(field);

        // Assert that after adding field, defaultFieldSize is equal to field size
        assertTrue(defaultFieldSize == field.getSize());
    }

    /**
     * Test that add(Control) works properly.
     */
    public void testAdd() {
        Form form = new Form("form");
        form.add(new TextField("name"), 1);

        // Check that add does not allow null arguments
        try {
            form.add(null);
            fail("Control cannot be null");
        } catch (Exception expected) {
        }

        // Check that field can be added
        try {
            form.add(new TextField("field"));
        } catch (Exception e) {
            fail("TextField can be added");
        }

        // Check that button can be added
        try {
            form.add(new Button("button"));
        } catch (Exception e) {
            fail("Button can be added");
        }

        // Check that table can be added
        try {
            form.add(new Table("table"));
        } catch (Exception e) {
            fail("Table can be added");
        }
    }

    /**
     * Test that add(Control, int) works properly.
     */
    public void testAddWidth() {
        Form form = new Form("form");
        form.add(new TextField("name"), 1);

        // Check that add does not allow null arguments
        try {
            form.add(null, 1);
            fail("Control cannot be null");
        } catch (Exception expected) {
        }

        // Check that field can be added
        try {
            form.add(new TextField("field"), 1);
        } catch (Exception e) {
            fail("TextField can be added");
        }

        // Check that table can be added
        try {
            form.add(new Table("table"), 1);
        } catch (Exception e) {
            fail("Table can be added");
        }

        // Check that add fails when width is less than 1
        try {
            form.add(new TextField("name"), 0);
            fail("Width cannot be less than 1");
        } catch (Exception expected) {
        }

        // Check that add fails when button is added
        try {
            form.add(new Button("button"), 1);
            fail("Button cannot have width set");
        } catch (Exception expected) {
        }
    }

    /**
     * Tests the Form insert performance.
     *
     * Creating 100 000 forms with 20 fields takes 1034 ms -> 1934 inserts per ms.
     *
     * System used: Sun JDK 1.4, Dual core, 2.16GHz, 2.00GB RAM, Windows XP
     */
    public void testInsertPerf() {
        // Specify the amount of text fields to create
        int textFieldCount = 15;

        // Number of times to populate a form with fields
        int loops = Integer.getInteger("click.perf.loops", 100000);

        long time = 0;
        for (int i = 0; i < loops; i++) {
            Form form = new Form("form");

            long start = System.currentTimeMillis();
            populateForm(textFieldCount, form);
            time += System.currentTimeMillis() - start;
        }
        System.out.println("Time :" + time);
    }

    /**
     * Test the isDisabled() for a child component. Intent is to ensure that a
     * child component of the Form is disabled when the Form is itself disabled.
     */
    public void testIsDisabled() {
        // Form is not disabled.
        assertFalse(this.testForm.isDisabled());

        // Hiddenfield inside is not disabled.
        assertFalse(this.trackField.isDisabled());

        // Change form state.
        this.testForm.setDisabled(true);

        // Hiddenfield is now disabled too.
        assertTrue(this.trackField.isDisabled());
    }

    /**
     * Test the isReadonly() for a child component. Intent is to ensure that a
     * child component of the Form is disabled when the Form is itself disabled.
     */
    public void testIsReadonly() {
        // Form is not disabled.
        assertFalse(this.testForm.isReadonly());

        // Hiddenfield inside is not disabled.
        assertFalse(this.trackField.isReadonly());

        // Change form state.
        this.testForm.setReadonly(true);

        // Hiddenfield is now disabled too.
        assertTrue(this.trackField.isReadonly());
    }

    /**
     * Check that adding fields replace existing fields with the same name.
     *
     * CLK-666
     */
    public void testReplaceFields() {
        Form form = new Form("form");

        // Add two fields named child1 and child2 (form auto adds FORM_NAME HiddenField as well)
        Field child1 = new TextField("child1");
        Field child2 = new TextField("child2");
        form.add(child1);
        form.add(child2);
        assertEquals(3, form.getControlMap().size());
        assertEquals(3, form.getControls().size());
        assertEquals(3, form.getFields().size());
        assertSame(child1, form.getControls().get(0));
        assertSame(child2, form.getControls().get(1));
        assertSame(child1, form.getFieldList().get(0));
        assertSame(child2, form.getFieldList().get(1));

        // Add another two fields named child1 and child2 and test that these
        // panels replaces the previous fields
        child1 = new TextField("child1");
        child2 = new TextField("child2");
        form.add(child1);
        form.add(child2);
        assertEquals(3, form.getControlMap().size());
        assertEquals(3, form.getControls().size());
        assertEquals(3, form.getFields().size());
        assertSame(child1, form.getControls().get(0));
        assertSame(child2, form.getControls().get(1));
        assertSame(child1, form.getFieldList().get(0));
        assertSame(child2, form.getFieldList().get(1));
    }

    /**
     * Check that adding buttons replace existing buttons with the same name.
     *
     * CLK-666
     */
    public void testReplaceButtons() {
        Form form = new Form("form");

        // Add two fields named child1 and child2 (form auto adds FORM_NAME HiddenField as well)
        Button child1 = new Button("child1");
        Button child2 = new Button("child2");
        form.add(child1);
        form.add(child2);
        assertEquals(3, form.getControlMap().size());
        assertEquals(3, form.getControls().size());
        assertEquals(3, form.getFields().size());
        assertSame(child1, form.getControls().get(0));
        assertSame(child2, form.getControls().get(1));
        assertSame(child1, form.getButtonList().get(0));
        assertSame(child2, form.getButtonList().get(1));

        // Add another two fields named child1 and child2 and test that these
        // panels replaces the previous fields
        child1 = new Button("child1");
        child2 = new Button("child2");
        form.add(child1);
        form.add(child2);
        assertEquals(3, form.getControlMap().size());
        assertEquals(3, form.getControls().size());
        assertEquals(3, form.getFields().size());
        assertSame(child1, form.getControls().get(0));
        assertSame(child2, form.getControls().get(1));
        assertSame(child1, form.getButtonList().get(0));
        assertSame(child2, form.getButtonList().get(1));
    }

    /**
     * Check that Label style attribute is not rendered on the parent TD element.
     *
     * CLK-712: In Click 2.2.0 the label style attribute was removed before rendering the
     * label attributes, after which the label style attribute was added again.
     * This could cause concurrent modification exceptions if the Form is rendered
     * by multiple threads.
     */
    public void testLabelStyle() {
        Form form = new Form("form");

        Label label = new Label("label");
        form.add(label);

        // Parent hint should be rendered as the TD style
        label.setParentStyleHint("color: white;");

        // Label style should not be rendered
        label.setStyle("color", "black");

        String formStr = form.toString();

        // Check that parentStyleHint style was rendered
        assertEquals(1, StringUtils.countMatches(formStr, "style=\"color: white;\""));

        // Check that the label style was not rendered
        assertEquals(1, StringUtils.countMatches(formStr, "style="));
    }

    /**
     * Test that Form.getState contains the state of all the Fields in the Form.
     *  Also test that Immutable HiddenFields are not saved as it is unnecessary
     * state to keep in memory.

     * CLK-715
     */
    public void testGetState() {
        MockContext.initContext();

        // Setup Form and Fields
        Form form = new Form("form");
        Field nameField  = new TextField("name");
        Field ageField = new TextField("age");
        nameField.setValue("Steve");
        ageField.setValue("10");
        form.add(nameField);
        form.add(ageField);

        FieldSet fs = new FieldSet("address");
        Field streetField  = new TextField("street");
        streetField.setValue("short");
        fs.add(streetField);
        form.add(fs);

        // Dummy onSubmitCheck to ensure submit check hiddenField is added by the Form
        form.onSubmitCheck(new Page(), "dummy.htm");

        Object state = form.getState();
        Map formStateMap = (Map) state;

        assertEquals(formStateMap.get(nameField.getName()), nameField.getValue());
        assertEquals(formStateMap.get(ageField.getName()), ageField.getValue());
        assertNotNull(formStateMap.get(fs.getName()));

        // Retrieve FieldSet state
        Object fsState = formStateMap.get(fs.getName());
        Map fsStateMap = (Map) fsState;
        assertEquals(fsStateMap.get(streetField.getName()), streetField.getValue());

        // Check that only the fields defined above are returned
        assertEquals(3, formStateMap.size());
    }

    /**
     * Test that Form.setState correctly set the state of the Fields in the Form.
     *
     * CLK-715
     */
    public void testSetState() {
        MockContext.initContext();

        // Setup Form and Fields
        Form form = new Form("form");
        Field nameField  = new TextField("name");
        Field ageField = new TextField("age");
        form.add(nameField);
        form.add(ageField);

        FieldSet fs = new FieldSet("address");
        Field streetField  = new TextField("street");
        fs.add(streetField);
        form.add(fs);

        // Setup state
        Map formStateMap = new HashMap();
        formStateMap.put("name", "Steve");
        formStateMap.put("age", "10");
        Map fsStateMap = new HashMap();
        fsStateMap.put("street", "short");
        formStateMap.put("address", fsStateMap);

        form.setState(formStateMap);

        // Check that field values were restored
        assertEquals("Steve", nameField.getValue());
        assertEquals("10", ageField.getValue());
        assertEquals("short", streetField.getValue());
    }

    /**
     * Populate the given Form with hidden fields.
     *
     * @param count the number of hidden fields to add
     * @param form the form to add hidden fields to
     */
    public void populateForm(int count, Form form) {
        // Add 5 hidden fields
        form.add(new HiddenField("-1", Boolean.class));
        form.add(new HiddenField("-2", Boolean.class));
        form.add(new HiddenField("-3", Boolean.class));
        form.add(new HiddenField("-4", Boolean.class));
        form.add(new HiddenField("-5", Boolean.class));
        for (int i = 0; i < count; i++) {
            form.add(new TextField(String.valueOf(i)));
        }
    }

    /**
     * Div container used for testing.
     */
    static class Div extends AbstractContainer {
        private static final long serialVersionUID = 1L;

        /**
         * Constructor.
         */
        public Div(String name) {
            super(name);
        }

        /**
         * @return div tag
         */
        @Override
        public String getTag() {
            return "div";
        }
    }
}
