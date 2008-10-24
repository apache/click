package net.sf.click;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.click.pages.ListenerPage;

/**
 * Test ClickServlet behavior.
 * 
 * @author Bob Schellink
 */
public class ClickServletTest extends TestCase {

    /**
     * Assert the ListenerPage that the Submit button listener is invoked *after*
     * the TextField value was bound to the request value.
     *
     * CLK-365.
     */
    public void testRegisterListener() {
        MockContainer container = new MockContainer("web");
        container.start();
        container.setParameter("form_name", "form"); // Simulate form submitted
        container.setParameter("submit", "submit"); // Simulate Submit button clicked
        container.setParameter("field", "one"); // Simulate TextField value set

        ListenerPage page = (ListenerPage) container.testPage(ListenerPage.class);

        // assert that the Page did successfully execute
        Assert.assertTrue(page.success);
        container.stop();
    }
}
