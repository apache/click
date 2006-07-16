package net.sf.click.examples.page.panel;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.panel.Panel;

/**
 * Provides a simple Panel demonstration.
 * <p/>
 * Note this page uses <tt>onInit()</tt> method rather than the pages
 * constructor for adding the messages. This is because the page Context is
 * available in the onInit() method, and is required to lookup the localized
 * messages for the request's Locale.
 *
 * @author Malcolm Edgar
 */
public class SimplePanelDemo extends BorderPage {

    public Panel panel = new Panel("panel", "/panel/simple-panel.htm");

    public void onInit() {
        addModel("heading", getMessage("heading"));
        addModel("content", getMessage("content"));
    }

}
