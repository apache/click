package net.sf.click.examples.page.control;

import java.util.Date;

import net.sf.click.ActionListener;
import net.sf.click.Control;
import net.sf.click.control.ActionButton;
import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides an ActionLink and ActionButton demonstration Page.
 * <p/>
 * In this example public fields are automatically added to the Page model using
 * their field name. In the case of controls their name will be automatically
 * set to their field name.
 *
 * @author Malcolm Edgar
 */
public class ActionDemo extends BorderPage {

    public ActionLink link = new ActionLink();
    public ActionButton button = new ActionButton();
    public String clicked;

    public ActionDemo() {

        link.setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                clicked = source.getClass().getName() + ".onAction invoked at " + (new Date());
                return true;
            }
        });

        button.setListener(this, "onButtonClick");
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onButtonClick() {
        clicked = getClass().getName() + ".onButtonClick invoked at " + (new Date());
        return true;
    }
}
