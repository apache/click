package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
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
}
