package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContainer;
import net.sf.click.MockContext;

public class TabbedFormTest extends TestCase {

    public void testGetHtmlImport() {
        MockContainer container = new MockContainer("web");
        container.start();

        // MockContext is created when a container tests a page. There
        // are no pages to test here so we manually create a MockContext,
        // but reuse the Mock Servlet objects created in the container.
        MockContext.initContext(container.getServletConfig(), container.getRequest(), container.getResponse(), container.getClickServlet());

        TabbedForm form = new TabbedForm("form");

        assertTrue(form.toString().indexOf("<form") > 0);
        assertTrue(form.getHtmlImports().indexOf("/control.js") > 0);
        assertTrue(form.getHtmlImports().indexOf("/control.css") > 0);
        assertTrue(form.getHtmlImports().indexOf("/extras-control.css") > 0);
        
        container.stop();
    }
}
