package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.ClickServlet;
import net.sf.click.MockContext;
import net.sf.click.servlet.MockRequest;
import net.sf.click.servlet.MockResponse;
import net.sf.click.servlet.MockServletConfig;
import net.sf.click.servlet.MockServletContext;
import net.sf.click.servlet.MockSession;

public class AbstractContainerFieldText extends TestCase {

    /**
     * Check and ensure AbstractContainerField does not bind to the request
     * parameter by default. CLK-428.
     */
    public void testBindRequestValue() {
        initMockContext();

        AbstractContainerField field = new AbstractContainerField() {
            public String getTag() {
                return "div";
            }
        };

        try {
            assertTrue(field.onProcess());
        } catch (Exception e) {
            fail("AbstractContainerField#onProcess should not bind to request parameter");
        }
    }
    
    private void initMockContext() {
        MockServletContext servletContext = new MockServletContext();
        String servletName = "click-servlet";
        String servletPath = "test";
        MockServletConfig servletConfig = new MockServletConfig(servletName,
            servletContext);
        ClickServlet servlet = new ClickServlet();
        MockResponse response = new MockResponse();
        MockSession session = new MockSession(servletContext);
        MockRequest request = new MockRequest() {

            // Override getParameter to throw exception if argument is null
            public String getParameter(String name) {
                if (name == null) {
                    throw new IllegalArgumentException("Null parameter");
                }
                return super.getParameter(name);
            }
        };
        MockContext.initContext(servletConfig, request, response, servlet);
    }
}
