package net.sf.click.examples.page.introduction;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides a simple Form example Page.
 * <p/>
 * Note the public scope Form control field is automatically added to the Page's
 * list of controls and the String msg field is automatically added to the
 * Page's model.
 * <p/>
 * The form <tt>onSubmit</tt> control listener is invoked when the submit button
 * is clicked.
 *
 * @author Malcolm Edgar
 */
public class SimpleForm extends BorderPage {

    public Form form = new Form();
    public String msg;

    // ------------------------------------------------------------ Constructor

    public SimpleForm() {
        form.add(new TextField("name", true));
        form.add(new Submit("OK"));

        form.setListener(this, "onSubmit");
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Handle the form submit event.
     */
    public boolean onSubmit() {
        if (form.isValid()) {
            msg = "Your name is " + form.getFieldValue("name");
        }
        return true;
    }

}
