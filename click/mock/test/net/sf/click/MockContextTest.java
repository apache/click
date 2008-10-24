package net.sf.click;

import junit.framework.TestCase;
import net.sf.click.servlet.MockServletConfig;
import net.sf.click.servlet.MockServletContext;
import net.sf.click.servlet.MockRequest;
import net.sf.click.servlet.MockResponse;

/**
 * MockContext tests.
 */
public class MockContextTest extends TestCase {

    /**
     * Test MockContext.
     */
    public void testContext() {
        MockServletContext servletContext = new MockServletContext();
        MockContext.initContext(new MockServletConfig(servletContext), 
            new MockRequest(), new MockResponse(), new ClickServlet());
    }
}
