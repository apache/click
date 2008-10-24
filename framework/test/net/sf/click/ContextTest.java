package net.sf.click;

import javax.servlet.http.HttpSession;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test Context behavior.
 * 
 * @author Bob Schellink
 */
public class ContextTest extends TestCase {

    /**
     * Assert that when a HttpSession is invalidated, a new HttpSession
     * is created, and that the session was not cached by the Context.
     *
     * CLK-371.
     */
    public void testInvalidateSession() {
        try {
            MockContext context = MockContext.initContext();
            HttpSession session = context.getSession();
            Assert.assertNull(context.getSessionAttribute("key"));
            context.setSessionAttribute("key", "value");
            Assert.assertEquals("value", context.getSessionAttribute("key"));

            // Invalidate should clear all attributes from session instance
            session.invalidate();

            // Since we run outside a real Servlet Container, we simulate
            // a Servlet Container nullifying the session that was invalidated
            // above.
            context.getMockRequest().setSession(null);

            // Assert that context returns the newly created session, and not a
            // cached value
            Assert.assertNotSame(session, context.getSession());

            // Assert that session attribute was cleared
            Assert.assertNull(context.getSessionAttribute("key"));
        } catch (Throwable t) {
            Assert.fail("Test should not throw exception");
        }
    }
}
