package examples.page;

import java.util.Date;

import net.sf.click.control.ActionLink;
import net.sf.click.extras.control.ActionButton;

/**
 * Provides an ActionLink and ActionButton demonstration Page.
 *
 * @author Malcolm Edgar
 */
public class ActionDemo extends BorderedPage {

    public ActionDemo() {
        ActionLink actionLink = new ActionLink("link");
        actionLink.setListener(this, "onLinkClick");
        addControl(actionLink);

        ActionButton actionButton = new ActionButton("button");
        actionButton.setListener(this, "onButtonClick");
        addControl(actionButton);
    }

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
