package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.ClickServlet;
import net.sf.click.Control;
import net.sf.click.MockContext;
import net.sf.click.control.TextField;
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
    
    /**
     * Check that overriding AbstractContainerField#insert(Control, int) will
     * still receive calls from AbstractContainerField#add(Control).
     */
    public void testInsertOverride() {
        FeedbackBorder border = new FeedbackBorder();
        border.add(new TextField("field1"));
        try {
            border.add(new TextField("field2"));
            fail("FeedbackBorder only allows one control to be added.");
        } catch (Exception expected) {
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
    
    class FeedbackBorder extends AbstractContainerField {

        public Control insert(Control control, int index) {

            // Enforce rule that only 1 control can be added
            if (getControls().size() > 0) {
                throw new IllegalStateException(
                    "Only one control is allowed on FeedbackBorder.");
            }

            super.insert(control, 0);
            return control;
        }
    }
}
