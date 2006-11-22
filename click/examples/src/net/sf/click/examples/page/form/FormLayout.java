package net.sf.click.examples.page.form;

import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.EmailField;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.TelephoneField;

public class FormLayout extends BorderPage {

    public Form form1 = new Form();
    public Form form2 = new Form();

    public FormLayout() {
        // Form 1
        form1.setColumns(3);

        form1.add(new TextField("name"));
        form1.add(new TextField("type"));
        form1.add(new TelephoneField("telephone"));
        form1.add(new TextArea("description", 70, 3), 3);
        form1.add(new EmailField("email"), 2);

        form1.add(new Submit("ok", " OK "));
        form1.add(new PageSubmit("cancel", HomePage.class));

        // Form 2
        form2.setColumns(2);

        FieldSet fieldSet = new FieldSet("fieldSet", "FieldSet");
        form2.add(fieldSet);

        fieldSet.add(new TextField("name"));
        fieldSet.add(new TextField("type"));
        fieldSet.add(new TextArea("description", 39, 3), 3);
        fieldSet.add(new EmailField("email"), 3);
        fieldSet.add(new TelephoneField("telephone"));

        form2.add(new Submit("ok", " OK "));
        form2.add(new PageSubmit("cancel", HomePage.class));
    }

}
