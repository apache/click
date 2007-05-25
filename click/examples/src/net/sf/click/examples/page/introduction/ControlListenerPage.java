package net.sf.click.examples.page.introduction;

import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides a control listener example Page.
 *
 * @author Malcolm Edgar
 */
public class ControlListenerPage extends BorderPage {

    /* Public scope controls are automatically added to the page. */
    public ActionLink myLink = new ActionLink(this, "onMyLinkClick");

    /* Public scope variable are automatically added to the model. */
    public String msg;

    // --------------------------------------------------------- Event Handlers

    /**
     * Handle the myLink control click event.
     */
    public boolean onMyLinkClick() {
        msg = "ControlListenerPage#" + hashCode()
            + " object method <tt>onMyLinkClick()</tt> invoked.";

        return true;
    }

}
