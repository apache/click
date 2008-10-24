package net.sf.click.pages;

/**
 * Basic test page.
 */
public class BorderTestPage extends BorderPage {

    /**
     * Initialize page.
     */
    public void onInit() {
        getContext().setRequestAttribute("id", "200");
        addModel("myparam", getContext().getRequestParameter("myparam"));
    }
}
