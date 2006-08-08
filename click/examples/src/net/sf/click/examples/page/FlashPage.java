package net.sf.click.examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides an example of a flash session attribute.
 *
 * @author Malcolm Edgar
 */
public class FlashPage extends BorderPage {

    public Form form = new Form();

    private TextField valueField = new TextField("value", "Value:", true);

    public FlashPage() {
        form.add(valueField);
        form.add(new Submit("ok", "  OK  ", this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            getContext().setFlashAttribute("flash", valueField.getValueObject());
            setRedirect(FlashPage.class);
            return false;
        }
        return true;
    }

}
