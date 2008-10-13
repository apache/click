package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Form;

public class MenuTest extends TestCase {

    /**
     * Check that FileField value is escaped.
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Form form = new Form("form");
        Menu menu = new Menu("menu");
        form.add(menu);

        String value = "<script>";
        String expected = "title=\"&lt;script&gt;\"";

        menu.setTitle(value);
        menu.setLabel(value);

        assertTrue(menu.toString().indexOf(expected) > 1);
    }
}
