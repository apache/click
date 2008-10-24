package net.sf.click.pages;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;

/**
 * Form test page.
 */
public class FormPage extends Page {

    /** Form instance. */
    private Form form = new Form("form");

    /**
     * Initialize page.
     */
    public void onInit() {
        form.add(new TextField("myfield"));
        addControl(form);
    }
    
    /**
     * Return form instance.
     *
     * @return form instance
     */
    public Form getForm() {
        return form;
    }
}
