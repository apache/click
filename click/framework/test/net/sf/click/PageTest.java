package net.sf.click;

import junit.framework.TestCase;
import net.sf.click.pages.JspRedirectPage;
import net.sf.click.pages.RedirectToHtm;
import net.sf.click.pages.RedirectToJsp;

/**
 * Test Page class.
 */
public class PageTest extends TestCase {

    public void testRedirect() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");
        
        RedirectToHtm page = (RedirectToHtm) container.testPage(RedirectToHtm.class);

        // assert that the Page successfully redirected to test.htm
        String expected = contextPath + "/test.htm";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }

    /**
     * Test that redirecting to a jsp is converted to htm before redirecting.
     * CLK-338
     */
    public void testRedirectToJSP() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");
        
        RedirectToJsp page = (RedirectToJsp) container.testPage(RedirectToJsp.class);

        // assert that the Page successfully redirected to jsp-page.htm, meaning
        // Click converted the location from jsp-page.jsp to jsp-page.htm
        String expected = contextPath + "/jsp-page.htm";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }

    /**
     * Test custom redirecting which does convert jsp extension to htm.
     * CLK-429
     */
    public void testCustomRedirectToJSP() {
        MockContainer container = new MockContainer("web");
        container.start();
        String contextPath = container.getRequest().getContextPath();
        container.getRequest().setMethod("GET");
        
        JspRedirectPage page = (JspRedirectPage) container.testPage(JspRedirectPage.class);

        // assert that the Page successfully redirected to test.jsp, meaning
        // Click DID NOT convert the location from test.jsp to test.htm
        String expected = contextPath + "/test.jsp";
        assertEquals(expected, container.getRedirect());
        container.stop();
    }
}
