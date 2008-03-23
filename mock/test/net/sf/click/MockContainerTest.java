package net.sf.click;

import java.io.File;
import java.net.URI;
import java.net.URL;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.click.pages.BorderTestPage;
import net.sf.click.pages.ForwardPage;
import net.sf.click.pages.RedirectPage;
import net.sf.click.pages.TestPage;

/**
 *
 * @author Bob Schellink
 */
public class MockContainerTest extends TestCase {

    public void testPage() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();//TODO remove need for start

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // First run
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml() 
                + "\n===============================================================\n");

            Assert.assertEquals("200", testPage.getContext().getRequestAttribute("id"));

            // Second run
            testPage = container.testPage("test.htm");
            Assert.assertEquals("200", testPage.getContext().getRequestAttribute("id"));

            System.out.println("\nSecond run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            container.stop();//TODO remove need for stop???

            container.start();

            // Third run
            testPage = container.testPage("/test.htm");
            System.out.println("\nThird run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            container.stop();

        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    public void testBroderPage() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(BorderTestPage.class);

            System.out.println(
                "\n======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // Check that border page markup is available
            Assert.assertTrue(container.getHtml().indexOf("<h1>Header</h1>") >= 0);

        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    public void testForward() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // ForwardPage forwards to TestPage.class
            testPage = container.testPage(ForwardPage.class);

            // Assert that forwardUrl was set
            Assert.assertEquals("/test.htm", container.getForward());

            // ForwardPage result will be empty because the template is NOT
            // rendered when forwarding a request.
            System.out.println("Forward result: " + container.getHtml());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }

    public void testRedirect() {
        try {
            MockContainer container = new MockContainer("web");

            container.start();

            // Set a parameter
            container.setParameter("myparam", "Some parameter");

            // Upload a file
            URL resource = container.getClass().getResource("/web/test.htm");
            URI uri = new URI(resource.toString());
            container.setParameter("myfile", new File(uri), "text/html");

            // Process page
            Page testPage = container.testPage(TestPage.class);

            System.out.println("\nFirst run finished");
            System.out.println(
                "======================== HTML Document ========================\n"
                + container.getHtml()
                + "\n===============================================================\n");

            // RedirectPage redirects to TestPage.class
            testPage = container.testPage(RedirectPage.class);

            // Assert that redirectUrl was set
            System.out.println("Redirect " + container.getRedirect());
            Assert.assertEquals("/mock/test.htm", container.getRedirect());

            // Alternatively use the container.getDestination() call which returns
            // either the forward or redirect value. For redirect values the 
            // context is removed making for easier testing.
            Assert.assertEquals("/test.htm", container.getForwardOrRedirectUrl());

            // RedirectPage result will be empty because the template is NOT
            // rendered when redirecting a request.
            System.out.println("Redirect result: " + container.getHtml());
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            Assert.fail();
        }
    }
}
