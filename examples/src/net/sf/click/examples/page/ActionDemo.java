package net.sf.click.examples.page;

import java.util.Date;

import net.sf.click.control.ActionButton;
import net.sf.click.control.ActionLink;

/**
 * Provides an ActionLink and ActionButton demonstration Page.
 * <p/>
 * In this example the controls are automatically added to the Page model
 * because they have public visiblity. The controls name is automatically set
 * to their field name.
 *
 * @author Malcolm Edgar
 */
public class ActionDemo extends BorderPage {

    public ActionLink link = new ActionLink(this, "onLinkClick");
    public ActionButton button = new ActionButton(this, "onButtonClick");

    public boolean onLinkClick() {
        String msg = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        addModel("clicked", msg);
        return true;
    }

    public boolean onButtonClick() {
        String msg = getClass().getName() + ".onButtonClick invoked at " + (new Date());
        addModel("clicked", msg);
        return true;
    }
}
