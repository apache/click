package net.sf.click.pages;

import net.sf.click.*;

/**
 * Redirect test page.
 */
public class RedirectPage extends Page {

    /**
     * Initialize page.
     */
    public void onInit() {
        getContext().setRequestAttribute("id", "200");
        addModel("myparam", getContext().getRequestParameter("myparam"));
    }

    /**
     * onPost event handler.
     */
    public void onPost() {
        setRedirect(TestPage.class);
    }
}
