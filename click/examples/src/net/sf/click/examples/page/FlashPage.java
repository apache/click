package net.sf.click.examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;

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
        form.add(new Submit("flashPage", "  Flash Page ", this, "onFlashClick"));
        form.add(new Submit("homePage", "  Home Page ", this, "onHomeClick"));
    }

    public boolean onFlashClick() {
        if (form.isValid()) {
            getContext().setFlashAttribute("flash", valueField.getValueObject());
            setRedirect(FlashPage.class);
            return false;
        }
        return true;
    }

    public boolean onHomeClick() {
        if (form.isValid()) {
            getContext().setFlashAttribute("flash", valueField.getValueObject());
            setRedirect(HomePage.class);
            return false;
        }
        return true;
    }

}
