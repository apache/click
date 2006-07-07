package net.sf.click.examples.page;

import java.util.Date;

import net.sf.click.control.ActionButton;
import net.sf.click.control.ActionLink;

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

    public ActionLink link = new ActionLink(this, "onLinkClick");
    public ActionButton button = new ActionButton(this, "onButtonClick");
    public String clicked;

    public boolean onLinkClick() {
        clicked = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        return true;
    }

    public boolean onButtonClick() {
        clicked = getClass().getName() + ".onButtonClick invoked at " + (new Date());
        return true;
    }
}
