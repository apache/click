package net.sf.click.pages;

import net.sf.click.Page;

/**
 * Page which redirects to another Page through its class.
 */
public class RedirectToJsp extends Page {

    /**
     * Redirect to JspPage.
     */
    public void onInit() {
        setRedirect(JspPage.class);
    }
}
