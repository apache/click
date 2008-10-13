package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

public class SelectTest extends TestCase {

    /**
     * Check that Select value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Select select = new Select("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        select.add(value);
        assertTrue(select.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(select.toString().indexOf(value) < 0);
    }
}
