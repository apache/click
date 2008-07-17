package net.sf.click.control;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Miscellaneous Control tests.
 * 
 * @author Bob Schellink
 */
public class ControlTest extends TestCase {

    /**
     * Description: A Control cannot change its name after it was added to a 
     * container. The reason is that when a control is added to a container, its
     * name is used as the hash in the container's HashMap. If a controls name 
     * is changed after it was added, it will NOT be possible to find that 
     * control again using its new name, as the HashMap will not be updated with
     * the new name.
     *
     * If a Control does not have its name set, and it tries to set a name
     * after its been added to a container, an exception will still be thrown.
     * 
     * Note: changing a controls name is really a corner case, but it would be
     * better to throw a nice descriptive exception than the alternative, which
     * is not returning the Control.
     */
    public void testChangingControlNameAfterParentIsSet() {
        BasicForm form = new BasicForm("form");

        // Run test with a Control having a name 
        String fieldName = "field";
        TextField field = new TextField();
        field.setName(fieldName);
        form.add(field);
        
        // Test retrieve field
        Field fieldFound = form.getField(fieldName);
        Assert.assertNotNull(fieldFound);

        // Changing field's name must throw exception once added to a container
        try {
            field.setName("gone");
            fail("Once a control is added to a container, invoking "
                + "setName(String) should throw an exception");
        } catch (IllegalStateException expected) {
            //success
        }


        form = new BasicForm("form");

        // Run test with a Control without having a name 
        field = new TextField();
        form.add(field);

        // Should not be able to retrieve field
        fieldFound = form.getField(fieldName);
        Assert.assertNull(fieldFound);

        // Changing field's name must throw exception once added to a container
        try {
            field.setName("gone");
            fail("Once a control is added to a container, invoking "
                + "setName(String) should throw an exception");
        } catch (IllegalStateException expected) {
            //success
        }
    }
}