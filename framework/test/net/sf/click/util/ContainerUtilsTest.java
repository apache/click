package net.sf.click.util;

import java.util.List;
import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Button;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Label;
import net.sf.click.control.TextField;

public class ContainerUtilsTest extends TestCase {

    public void testGetFields() {
        MockContext.initContext();
        // set up the form
        Form form = new Form("sample");

        // HiddenField should be included
        HiddenField hiddenField = new HiddenField("hidden", boolean.class);
        form.add(hiddenField);

        // TextField should be included
        TextField idField = new TextField("id");
        form.add(idField);

        // FieldSet should NOT be included
        FieldSet fieldset = new FieldSet("fieldset");
        form.add(fieldset);

        // Label should NOT be included
        Label label = new Label("name");
        form.add(label);

        // Button should NOT be included
        Button button = new Button("button");
        form.add(button);
        
        List fields = ContainerUtils.getInputFields(form);
        
        // Total should be 3 consisting of the fields "hidden", "id" and the
        // Forms internal HiddenFields "form_name".
        assertEquals(3, fields.size());
    }
}
