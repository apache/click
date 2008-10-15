package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

/**
 * Tests for FieldSet.
 */
public class FieldSetTest extends TestCase {

    /** FieldSet which index position to test. */
    private FieldSet fieldSet;
    
    /** TextField which position is tracked in the FieldSet. */
    private TextField trackField;

    /**
     * Setup the fieldSet and trackField instances for testing the Control index
     * positions after being added or inserted.
     */
    public void setUp() {
        // Create form and fieldSet.
        Form form = new Form("form");
        fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        // Create the trackField at index 0 and check the fieldSet index
        // position for the lists FieldSet#controls and FieldSet#fieldList
        trackField = new TextField("track");
        fieldSet.add(trackField);

        // trackField index: #controls=0
        assertTrue(fieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#add(Control) inserts field at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not.
     */
    public void testInsertOrderAfterAddingField() {
        TextField nameField = new TextField("score");
        fieldSet.add(nameField);

        // nameField index: #controls=1
        assertTrue(fieldSet.getControls().indexOf(nameField) == 1);
        // nameField index: #fieldList=1
        assertTrue(fieldSet.getFieldList().indexOf(nameField) == 1);
        
       // trackField index: #controls=0
        assertTrue(fieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts field at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not.
     */
    public void testInsertOrderAfterInsertingField() {
        TextField nameField = new TextField("name");
        fieldSet.insert(nameField, 0);

        // nameindex: #controls=0
        assertTrue(fieldSet.getControls().indexOf(nameField) == 0);
        // nameindex: #fieldList=1
        assertTrue(fieldSet.getFieldList().indexOf(nameField) == 1);
        
       // trackField index: #controls=1
        assertTrue(fieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
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
        fieldSet.insert(table, 0);

        // table: #controls=0
        assertTrue(fieldSet.getControls().indexOf(table) == 0);
        // table: #fieldList=-1
        assertTrue(fieldSet.getFieldList().indexOf(table) == -1);
        
       // trackField index: #controls=1
        assertTrue(fieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that FieldSet#insert(Control, int) inserts button at the correct index.
     *
     * The FieldSet#controls list should be affected by the insert index, while
     * FieldSet#fieldList should not contain button as Button is not valid.
     */
    public void testInsertOrderAfterInsertingButton() {
        // Check that the fieldList contains only one item -> trackField
        assertTrue(fieldSet.getFieldList().size() == 1);

        Button button = new Button("button1");
        fieldSet.insert(button, 0);

        // button: #controls=0
        assertTrue(fieldSet.getControls().indexOf(button) == 0);
        // Check that fieldList still only contains one item
        assertTrue(fieldSet.getFieldList().size() == 1);
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
        fieldSet.insert(hidden, 0);

        // button: #controls=0
        assertTrue(fieldSet.getControls().indexOf(hidden) == 0);
        // hidden index: #fieldList=1
        assertTrue(fieldSet.getFieldList().indexOf(hidden) == 1);
        
        // trackField index: #controls=1
        assertTrue(fieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
    }

    /**
     * Test that Form#remove(Control) properly cleans up.
     */
    public void testRemove() {
        TextField field = new TextField("field");

        fieldSet.insert(field, 0);

        // field index: #controls=0
        assertTrue(fieldSet.getControls().indexOf(field) == 0);
        // field index: #fieldList=1
        assertTrue(fieldSet.getFieldList().indexOf(field) == 1);

        // trackField index: #controls=1
        assertTrue(fieldSet.getControls().indexOf(trackField) == 1);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
        
        int expectedSize = 2;
        // Check the list sizes to be 2
        assertTrue(fieldSet.getControls().size() == expectedSize);
        assertTrue(fieldSet.getFieldList().size() == expectedSize);
        
        // Removing field should shift up trackField index
        fieldSet.remove(field);

        expectedSize = 1;
        // Check the list sizes to be 1
        assertTrue(fieldSet.getControls().size() == expectedSize);
        assertTrue(fieldSet.getFieldList().size() == expectedSize);
        
        // trackField index: #controls=0
        assertTrue(fieldSet.getControls().indexOf(trackField) == 0);
        // trackField index: #fieldList=0
        assertTrue(fieldSet.getFieldList().indexOf(trackField) == 0);
    }
    
    /**
     * Test that FieldSet#add(Control, int) and FieldSet#remove(Control) properly
     * sets and removes the fieldWidth for a Field.
     */
    public void testFieldWidthForField() {
        TextField field = new TextField("field");

        // Check that fieldWidth is empty
        assertTrue(fieldSet.getFieldWidths().isEmpty());
            
        int colspan = 4;
        fieldSet.add(field, colspan);

        // Check that fieldWidth has entry for field
        assertTrue(fieldSet.getFieldWidths().size() == 1);
        
        Integer width = (Integer) fieldSet.getFieldWidths().get(field.getName());
        assertEquals(4, width.intValue());

        fieldSet.remove(field);
        
        // Check that fieldWidth is empty
        assertTrue(fieldSet.getFieldWidths().isEmpty());
    }

    /**
     * Test that FieldSet#add(Control, int) and FieldSet#remove(Control) properly
     * sets and removes the fieldWidth for a Control.
     */
    public void testFieldWidthForTable() {
        Table table = new Table("table");

        // Check that fieldWidth is empty
        assertTrue(fieldSet.getFieldWidths().isEmpty());
            
        int colspan = 4;
        fieldSet.add(table, colspan);

        // Check that fieldWidth has entry for table
        assertTrue(fieldSet.getFieldWidths().size() == 1);
        
        Integer width = (Integer) fieldSet.getFieldWidths().get(table.getName());
        assertEquals(4, width.intValue());

        fieldSet.remove(table);
        
        // Check that fieldWidth is empty
        assertTrue(fieldSet.getFieldWidths().isEmpty());
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
        //assertTrue(fieldSet.getFieldList().contains(anotherFieldSet));
        
        // Check that list does *not* contains the other FieldSet's Field
        //assertFalse(fieldSet.getFieldList().contains(anotherFieldSetField));

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
     * $form.fieldSet.fields.name   <- name this is a TextField
     * $form.fieldSet.fields.div    <- div is a AbstractContainer
     * $form.fieldSet.fields.button <- button is a Submit
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

    static class Div extends AbstractContainer {
        public Div(String name) {
            super(name);
        }

        public String getTag() {
            return "div";
        }
    }
}
