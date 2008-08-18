package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.TextField;

public class HtmlFieldSetTest extends TestCase {

    /**
     * Check that HtmlFieldSet properly opens and closes the fieldset tag.
     * CLK-427.
     */
    public void testHtmlFieldSetRender() {
        MockContext.initContext();
        HtmlFieldSet fieldset = new HtmlFieldSet("fieldset");
        fieldset.add(new TextField("name"));
        fieldset.add(new TextField("id"));

        // Ensure fieldset tag is opened correctly
        assertTrue(fieldset.toString().indexOf("<fieldset") == 0);

        // Ensure fieldset tag is closed correctly
        assertTrue(fieldset.toString().indexOf("</fieldset>") > 0);
    }
}
