package net.sf.click.util;

import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.Page;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;

/**
 *
 * @author Bob Schellink
 */
public class ContainerMessageMapTest extends TestCase {

    /**
     * Assert that Control properties are resolved correctly when
     * the Control is part of a hierarchy of Controls e.g. Page -> Form -> Field.
     *
     * CLK-373.
     */
    public void testContainerMessageInheritance() {
        MockContext.initContext(Locale.ENGLISH);

        Page page = new Page();
        MyForm form = new MyForm("myform");
        page.addControl(form);
        Field customField = form.getField("customField");
        Map map = form.getMessages();
        assertFalse(map.isEmpty());
        assertTrue(map.size() >= 2);
        assertEquals("Custom Name", customField.getLabel());
        assertEquals("Enter the custom name!", customField.getTitle());
        assertEquals("Custom Name", map.get("customField.label"));
        assertEquals("Enter the custom name!", map.get("customField.title"));
    }

    /**
     * Custom Form class.
     */
    public class MyForm extends Form {

        /**
         * Construct a MyForm instance for the given name.
         * 
         * @param name the name of the form
         */
        public MyForm(String name) {
            super(name);
            buildForm();
        }

        /**
         * Builds the form contents.
         */
        private void buildForm() {
            TextField customField = new TextField("customField");
            this.add(customField);
        }
    }
}
