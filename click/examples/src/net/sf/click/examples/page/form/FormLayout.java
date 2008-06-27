package net.sf.click.examples.page.form;

import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import net.sf.click.control.Reset;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.TelephoneField;

/**
 * Provides Form layout examples using the Form and FieldSet controls.
 *
 * @author Malcolm Edgar
 */
public class FormLayout extends BorderPage {

    public Form form1 = new Form();
    public Form form2 = new Form();

    public FormLayout() {
        // ------
        // Form 1
        form1.setColumns(3);

        // Row 1
        Field titleField = new TextField("title");
        titleField.setStyle("width", "100%");
        form1.add(titleField, 2);
        form1.add(new Label("blank", ""));

        // Row 2
        form1.add(new TextArea("description", 70, 3), 3);

        // Row 3
        form1.add(new TextField("name"));
        form1.add(new TextField("type"));
        form1.add(new TelephoneField("telephone"));

        form1.add(new Submit("ok", " OK "));
        form1.add(new PageSubmit("cancel", HomePage.class));

        //-------
        // Form 2
        form2.setColumns(2);

        FieldSet fieldSet = new FieldSet("fieldSet", "FieldSet");
        form2.add(fieldSet);

        // Row 1
        fieldSet.add(new TextField("name"));
        fieldSet.add(new TextField("type"));

        // Row 2
        fieldSet.add(new TextArea("description", 39, 3), 2);

        // Row 3
        fieldSet.add(new EmailField("email"), 2);

        // Row 4
        fieldSet.add(new TelephoneField("telephone"));
        
        fieldSet.add(new Submit("ok", " OK "));
        fieldSet.add(new PageSubmit("cancel", HomePage.class));
    }

}
