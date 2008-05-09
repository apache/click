package net.sf.click.pages;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;

public class FormPage extends Page {

    private Form form = new Form("form");

    public void onInit() {
        form.add(new TextField("myfield"));
        addControl(form);
    }
    
    public Form getForm() {
        return form;
    }
}
