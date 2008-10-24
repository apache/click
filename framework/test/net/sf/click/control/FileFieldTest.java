package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

/**
 * Test FileField behavior.
 */
public class FileFieldTest extends TestCase {

    /**
     * Check that FileField value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        FileField field = new FileField("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";

        field.setValue(value);

        assertTrue(field.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }
}
