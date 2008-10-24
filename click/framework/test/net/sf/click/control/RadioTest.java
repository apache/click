package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;

/**
 * Test Radio behavior.
 */
public class RadioTest extends TestCase {

    /**
     * Check that Radio value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();
        
        Form form = new Form("form");
        RadioGroup radioGroup = new RadioGroup("group");
        form.add(radioGroup);

        Radio radio = new Radio("name");
        radioGroup.add(radio);

        String value = "<script>";
        String expectedValue = "value=\"&lt;script&gt;\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedValue) > 1);
        
        String expectedId = "form_group_&lt;script&gt;";
        String expectedIdAttr = "id=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedIdAttr) > 1);
        
        String expectedLabelValue = ">&lt;script&gt;</label>";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelValue) > 1);
        
        String expectedLabelForAttr = "for=\"" + expectedId + "\"";
        radio.setValue(value);
        assertTrue(radio.toString().indexOf(expectedLabelForAttr) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(radio.toString().indexOf(value) < 0);
    }
}
