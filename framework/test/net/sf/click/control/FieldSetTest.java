package net.sf.click.control;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Bob Schellink
 */
public class FieldSetTest extends TestCase {

    /**
     * Test that form inserts controls before hidden fields
     */
    public void testInsertOrderWithHiddenFields() {
        Form form = new Form("form");

        // Create fieldSet and fields
        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        TextField nameField = new TextField("name");
        fieldSet.add(nameField);

        nameField = new TextField("hidden1") {
             public boolean isHidden() {
                return true;
            }
        };
        fieldSet.add(nameField);

        nameField = new TextField("hidden2") {
            public boolean isHidden() {
                return true;
            }
        };
        fieldSet.add(nameField);

        nameField = new TextField("name2");
        fieldSet.add(nameField);

        // If form correctly places hidden fields at the bottom, then field 3
        // should be hidden2
        Field hiddenField = ((Field) fieldSet.getControls().get(3));
        Assert.assertEquals("hidden2", hiddenField.getName());
    }
}
