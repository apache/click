package examples.page;

import net.sf.click.Page;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

/**
 * Provides a simple Form example Page.
 *
 * @author Malcolm Edgar
 */
public class SimpleForm extends Page {
    
    Form form;
    
    /**
     * @see Page#onInit()
     */
    public void onInit() {
        form = new Form("form", getContext());
        addControl(form);
        
        TextField textField = new TextField("My Name");
        textField.setMaxLength(20);
        textField.setMinLength(5);
        textField.setRequired(true);
        form.add(textField);
        
        form.add(new Submit("Sumbit"));
    }
    
    /**
     * @see Page#onPost()
     */
    public void onPost() {
        if (form.isValid()) {
            addModel("message", "The posted form is valid.");
        } else {
            addModel("message", "The posted form is not valid.");
        }
    }

}
