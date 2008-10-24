package net.sf.click.pages;

import net.sf.click.*;

/**
 * A test page.
 */
public class TestPage extends Page {

    /**
     * Initialize page.
     */
    public void onInit() {
        getContext().setRequestAttribute("id", "200");
        addModel("myparam", getContext().getRequestParameter("myparam"));
    }
}
