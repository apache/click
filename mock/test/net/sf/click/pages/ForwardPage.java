package net.sf.click.pages;

import net.sf.click.*;

/**
 * Forward test page.
 */
public class ForwardPage extends Page {

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
        setForward(TestPage.class);
    }
}
