package net.sf.click;

import junit.framework.TestCase;
import net.sf.click.servlet.MockServletConfig;
import net.sf.click.servlet.MockServletContext;
import net.sf.click.servlet.MockRequest;
import net.sf.click.servlet.MockResponse;

/**
 *
 * @author Bob Schellink
 */
public class MockContextTest extends TestCase {

    public void testContext() {
        MockServletContext servletContext = new MockServletContext();
        MockContext.initContext(new MockServletConfig(servletContext), 
            new MockRequest(), new MockResponse(), new ClickServlet());
    }
}
