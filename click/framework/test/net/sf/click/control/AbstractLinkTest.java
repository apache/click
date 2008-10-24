package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

/**
 * Test AbstractLink behavior.
 */
public class AbstractLinkTest extends TestCase {

    /**
     * Check that AbstractLink value is encoded.
     */
    public void testEscapeValue() {
        MockContext.initContext();

        ActionLink link = new ActionLink("name");
        String value = "<script>";
        String expected = "value=%3Cscript%3E";

        link.setValue(value);

        // Check that link encodes value properly
        assertTrue(link.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(link.toString().indexOf(value) < 0);
    }
}
