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
import junit.framework.TestCase;
import org.apache.click.Control;
import org.apache.click.MockContext;
import org.apache.commons.lang.StringUtils;

/**
 * Test FieldSet behavior.
 */
public class FieldSetTest extends TestCase {

    /** FieldSet which index position to test. */
    private FieldSet testFieldSet;
    
    /** TextField which position is tracked in the FieldSet. */
    private TextField trackField;

    /**
     * Setup the testFieldSet and trackField instances for testing the Control index
     * positions after being added or inserted.
     */
    @Override
    public void setUp() {
        // Create form and testFieldSet.
        Form form = new Form("form");
        testFieldSet = new FieldSet("fieldSet");
        form.add(testFieldSet);

        // Create the trackField at index 0 and check the testFieldSet index
        // position for the lists FieldSet#controls and FieldSet#fieldList
        trackField = new TextField("track");
        testFieldSet.add(trackField);

        // trackField index: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#add(Control) inserts field at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not.
     */
    public void testInsertOrderAfterAddingField() {
        TextField nameField = new TextField("score");
        testFieldSet.add(nameField);

        // nameField index: #controls=1
        assertTrue(testFieldSet.getControls().indexOf(nameField) == 1);
        // nameField index: #fieldList=1
        assertTrue(testFieldSet.getFieldList().indexOf(nameField) == 1);
        
       // trackField index: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts field at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not.
     */
    public void testInsertOrderAfterInsertingField() {
        TextField nameField = new TextField("name");
        testFieldSet.insert(nameField, 0);

        // nameindex: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(nameField) == 0);
        // nameindex: #fieldList=1
        assertTrue(testFieldSet.getFieldList().indexOf(nameField) == 1);
        
       // trackField index: #controls=1
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts table at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not contain table as Table is not a Field.
     */
    public void testInsertOrderAfterInsertingTable() {
        Table table = new Table("table");

        // Insert table at index 0
        testFieldSet.insert(table, 0);

        // table: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(table) == 0);
        // table: #fieldList=-1
        assertTrue(testFieldSet.getFieldList().indexOf(table) == -1);
        
       // trackField index: #controls=1
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts button at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not contain button as Button is not valid.
     */
    public void testInsertOrderAfterInsertingButton() {
        // Check that the fieldList contains only one item -> trackField
        assertTrue(testFieldSet.getFieldList().size() == 1);

        Button button = new Button("button1");
        testFieldSet.insert(button, 0);

        // button: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(button) == 0);
        // Check that fieldList still only contains one item
        assertTrue(testFieldSet.getFieldList().size() == 1);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts HiddenField at the
     * correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not.
     */
    public void testInsertOrderAfterInsertingHiddenField() {
        HiddenField hidden = new HiddenField("hidden", Boolean.class);

        // Insert hidden at index 0
        testFieldSet.insert(hidden, 0);

        // button: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(hidden) == 0);
        // hidden index: #fieldList=1
        assertTrue(testFieldSet.getFieldList().indexOf(hidden) == 1);
        
        // trackField index: #controls=1
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that Form#remove(Control) properly cleans up.
     */
    public void testRemove() {
        TextField field = new TextField("field");

        testFieldSet.insert(field, 0);

        // field index: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(field) == 0);
        // field index: #fieldList=1
        assertTrue(testFieldSet.getFieldList().indexOf(field) == 1);

        // trackField index: #controls=1
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
        
        int expectedSize = 2;
        // Check the list sizes to be 2
        assertTrue(testFieldSet.getControls().size() == expectedSize);
        assertTrue(testFieldSet.getFieldList().size() == expectedSize);
        
        // Removing field should shift up trackField index
        testFieldSet.remove(field);

        expectedSize = 1;
        // Check the list sizes to be 1
        assertTrue(testFieldSet.getControls().size() == expectedSize);
        assertTrue(testFieldSet.getFieldList().size() == expectedSize);
        
        // trackField index: #controls=0
        assertTrue(testFieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(testFieldSet.getFieldList().indexOf(trackField) == 0);
    }
    
    /**
     * Test that FieldSet#add(Control, int) and FieldSet#remove(Control) properly
     * sets and removes the fieldWidth for a Field.
     */
    public void testFieldWidthForField() {
        TextField field = new TextField("field");

        // Check that fieldWidth is empty
        assertTrue(testFieldSet.getFieldWidths().isEmpty());
            
        int colspan = 4;
        testFieldSet.add(field, colspan);

        // Check that fieldWidth has entry for field
        assertTrue(testFieldSet.getFieldWidths().size() == 1);
        
        Integer width = testFieldSet.getFieldWidths().get(field.getName());
        assertEquals(4, width.intValue());

        testFieldSet.remove(field);
        
        // Check that fieldWidth is empty
        assertTrue(testFieldSet.getFieldWidths().isEmpty());
    }

    /**
     * Test that FieldSet#add(Control, int) and FieldSet#remove(Control) properly
     * sets and removes the fieldWidth for a Control.
     */
    public void testFieldWidthForTable() {
        Table table = new Table("table");

        // Check that fieldWidth is empty
        assertTrue(testFieldSet.getFieldWidths().isEmpty());
            
        int colspan = 4;
        testFieldSet.add(table, colspan);

        // Check that fieldWidth has entry for table
        assertTrue(testFieldSet.getFieldWidths().size() == 1);
        
        Integer width = testFieldSet.getFieldWidths().get(table.getName());
        assertEquals(4, width.intValue());

        testFieldSet.remove(table);
        
        // Check that fieldWidth is empty
        assertTrue(testFieldSet.getFieldWidths().isEmpty());
    }

    /**
     * Test that FieldSet.getFieldList() return only Fields directly added to the
     * field list. The list should include other FieldSets but exclude Buttons.
     *
     * The main use case for FieldSet.getFieldList() is that it enables one to use
     * Velocity to render Fields e.g.:
     * 
     * #foreach ($field in $fieldSet.fieldList)
     *   <td>$field.label:</td> <td>$field</td>
     * #end
     *
     * Also check that FieldSet.getFieldList() returns a cached List so that access
     * to FieldSet.getFieldList() is fast.
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
        MockContext.initContext();

        Form form = new Form("form");

        // Assemble FieldSet
        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        // Assemble Fields
        TextField nameField = new TextField("name");
        fieldSet.add(nameField);

        Div div = new Div("div");
        fieldSet.add(div);

        Button button = new Button("button");
        fieldSet.add(button);

        // Assemble another FieldSet
        FieldSet anotherFieldSet = new FieldSet("anotherFieldSet");
        fieldSet.add(anotherFieldSet);

        // Add Field to FieldSet
        TextField anotherFieldSetField = new TextField("anotherFieldSetField");
        anotherFieldSet.add(anotherFieldSetField);

        // Check that list contains TextField
        assertTrue(fieldSet.getFieldList().contains(nameField));

        // Check that list does *not* contain Div
        assertFalse(fieldSet.getFieldList().contains(div));
        
        // Check that list does *not* contain Button
        assertFalse(fieldSet.getFieldList().contains(button));

        // Check that list contains other FieldSet
        //assertTrue(testFieldSet.getFieldList().contains(anotherFieldSet));
        
        // Check that list does *not* contains the other FieldSet's Field
        //assertFalse(testFieldSet.getFieldList().contains(anotherFieldSetField));

        // Check that field list is cached
        assertSame(fieldSet.getFieldList(), fieldSet.getFieldList());
    }

    /**
     * Test that FieldSet.getFields() return all Controls by delegating to
     * AbstractContainer#getControlMap().
     *
     * The main use case for FieldSet.getFields() is that it enables one to use
     * Velocity to render Controls and Fields e.g.:
     *
     * $form.testFieldSet.fields.name   <- name this is a TextField
     * $form.testFieldSet.fields.div    <- div is a AbstractContainer
     * $form.testFieldSet.fields.button <- button is a Submit
     * 
     * Also check that FieldSet.getFields() returns a cached Map so that access to
     * FieldSet.getFields() is fast.
     * 
     * Q: Why not return "only" Fields?
     * A: FieldSet will then have to maintain another map besides #controlMap.
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
     * Test that Fields added to FieldSet, correctly returns FieldSet as their
     * parent.
     *
     * CLK-497
     */
    public void testFieldParent() {
        // Initially testFieldSet contains 1 hidden field
        assertTrue(testFieldSet.getControls().size() == 1);

        TextField nameField = new TextField("score");
        testFieldSet.add(nameField);

        assertTrue(testFieldSet.getControls().size() == 2);

        // Test that Field parent is a FieldSet
        assertEquals(FieldSet.class, nameField.getParent().getClass());

        assertTrue(testFieldSet.remove((Control) nameField));

        assertTrue(nameField.getParent() == null);
        assertTrue(testFieldSet.getControls().size() == 1);
    }

    /**
     * Test the isDisabled() for a child component. Intent is to ensure that a child
     * component of the FieldSet is disabled when the FieldSet is itself disabled.
     */
    public void testIsDisabled() {
    	// Fieldset is not disabled.
    	assertFalse(this.testFieldSet.isDisabled());

    	// Textfield inside is not disabled.
    	assertFalse(this.trackField.isDisabled());

    	// Change fieldset state.
    	this.testFieldSet.setDisabled(true);

    	// Textfield is now disabled too.
    	assertTrue(this.trackField.isDisabled());
    }

    /**
     * Test the isReadonly() for a child component. Intent is to ensure that a child
     * component of the FieldSet is disabled when the FieldSet is itself disabled.
     */
    public void testIsReadonly() {
    	  // Fieldset is not disabled.
   	    assertFalse(this.testFieldSet.isReadonly());

    	  // Textfield inside is not disabled.
    	  assertFalse(this.trackField.isReadonly());

    	  // Change fieldset state.
    	  this.testFieldSet.setReadonly(true);

    	  // Textfield is now disabled too.
    	  assertTrue(this.trackField.isReadonly());
    }

    /**
     * Check that adding controls replace existing controls with the same name.
     *
     * CLK-666
     */
    public void testReplace() {
        FieldSet fieldset = new FieldSet("fieldset");

        // Add two fields named child1 and child2
        Field child1 = new TextField("child1");
        Field child2 = new TextField("child2");
        fieldset.add(child1);
        fieldset.add(child2);
        assertEquals(2, fieldset.getControlMap().size());
        assertEquals(2, fieldset.getControls().size());
        assertEquals(2, fieldset.getFields().size());
        assertSame(child1, fieldset.getControls().get(0));
        assertSame(child2, fieldset.getControls().get(1));
        assertSame(child1, fieldset.getFieldList().get(0));
        assertSame(child2, fieldset.getFieldList().get(1));

        // Add another two fields named child1 and child2 and test that these
        // panels replaces the previous fields
        child1 = new TextField("child1");
        child2 = new TextField("child2");
        fieldset.add(child1);
        fieldset.add(child2);
        assertEquals(2, fieldset.getControlMap().size());
        assertEquals(2, fieldset.getControls().size());
        assertEquals(2, fieldset.getFields().size());
        assertSame(child1, fieldset.getControls().get(0));
        assertSame(child2, fieldset.getControls().get(1));
        assertSame(child1, fieldset.getFieldList().get(0));
        assertSame(child2, fieldset.getFieldList().get(1));
    }

    /**
     * Check that Label style attribute is not rendered on the parent TD element.
     *
     * CLK-712: In Click 2.2.0 the label style attribute was removed before rendering the
     * label attributes, after which the label style attribute was added again.
     * This could cause concurrent modification exceptions if the FieldSet is rendered
     * by multiple threads.
     */
    public void testLabelStyle() {
        MockContext.initContext();

        Form form = new Form("form");
        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        Label label = new Label("label");
        fieldset.add(label);

        // Parent hint should be rendered as the TD style
        label.setParentStyleHint("color: white;");

        // Label style should not be rendered
        label.setStyle("color", "black");

        String fieldsetStr = fieldset.toString();

        // Check that parentStyleHint style was rendered
        assertEquals(1, StringUtils.countMatches(fieldsetStr, "style=\"color: white;\""));

        // Check that the label style was not rendered
        assertEquals(1, StringUtils.countMatches(fieldsetStr, "style="));
    }

    /**
     * Test that FieldSet.getState contains the state of all the Fields in the
     * FieldSet.
     * CLK-715
     */
    public void testGetState() {
        // Setup FieldSet and Fields
        FieldSet fs = new FieldSet("fieldSet");
        Field nameField  = new TextField("name");
        Field ageField = new TextField("age");
        nameField.setValue("Steve");
        ageField.setValue("10");
        fs.add(nameField);
        fs.add(ageField);

        FieldSet childFs = new FieldSet("address");
        Field streetField  = new TextField("street");
        streetField.setValue("short");
        childFs.add(streetField);
        fs.add(childFs);

        Object state = fs.getState();
        Map fsStateMap = (Map) state;

        // Check that only the fields defined above are returned
        assertEquals(3, fsStateMap.size());

        assertEquals(fsStateMap.get(nameField.getName()), nameField.getValue());
        assertEquals(fsStateMap.get(ageField.getName()), ageField.getValue());
        assertNotNull(fsStateMap.get(childFs.getName()));

        // Retrieve FieldSet state
        Object childFsState = fsStateMap.get(childFs.getName());
        Map childFsStateMap = (Map) childFsState;
        assertEquals(childFsStateMap.get(streetField.getName()), streetField.getValue());
    }

    /**
     * Test that FieldSet.setState correctly set the state of the Fields in the
     * FieldSet.
     *
     * CLK-715
     */
    public void testSetState() {
        // Setup FieldSet and Fields
        FieldSet fs = new FieldSet("fieldSet");
        Field nameField  = new TextField("name");
        Field ageField = new TextField("age");
        fs.add(nameField);
        fs.add(ageField);

        FieldSet childFs = new FieldSet("address");
        Field streetField  = new TextField("street");
        childFs.add(streetField);
        fs.add(childFs);

        // Setup state
        Map fsStateMap = new HashMap();
        fsStateMap.put("name", "Steve");
        fsStateMap.put("age", "10");
        Map childFsStateMap = new HashMap();
        childFsStateMap.put("street", "short");
        fsStateMap.put("address", childFsStateMap);

        fs.setState(fsStateMap);

        // Check that field values were restored
        assertEquals("Steve", nameField.getValue());
        assertEquals("10", ageField.getValue());
        assertEquals("short", streetField.getValue());
    }

    /**
     * A custom Div container.
     */
    static class Div extends AbstractContainer {
        private static final long serialVersionUID = 1L;

        /**
         * Construct a new Div with the given name.
         *
         * @param name the div name
         */
        public Div(String name) {
            super(name);
        }

        /**
         * Return the Div tag.
         * 
         * @return the div tag
         */
        @Override
        public String getTag() {
            return "div";
        }
    }
}
