package examples.page;

import java.util.Date;

import net.sf.click.control.ActionLink;

/**
 * Provides an ActionLink demonstration Page.
 *
 * @author Malcolm Edgar
 */
public class ActionDemo extends BorderedPage {

    public ActionDemo() {
        ActionLink actionLink = new ActionLink("link");
        actionLink.setListener(this, "onLinkClick");
        addControl(actionLink);
    }

    public boolean onLinkClick() {
        String msg = getClass().getName() + ".onLinkClick invoked at " + (new Date());
        addModel("clicked", msg);

        return true;
    }
}
