package net.sf.click;

import clickplugin.page.MyPage;
import junit.framework.TestCase;
import net.sf.click.servlet.MockRequest;
import net.sf.click.servlet.MockResponse;
import net.sf.click.servlet.MockServletConfig;
import net.sf.click.servlet.MockServletContext;

/**
 *
 * @author Bob Schellink
 */
public class PluginTest extends TestCase {

    public void testPlugin() {

        try {
            MockRequest request = new MockRequest();
            MockServletContext servletContext = new MockServletContext();
            servletContext.setWebappPath("test/web");
            String servletName = "click-servlet";
            MockServletConfig servletConfig = new MockServletConfig(servletName,
              servletContext);
            MockResponse response = new MockResponse();

            PluginClickServlet servlet = new PluginClickServlet();

            MockContext.initContext(servletConfig, request, response, servlet);

            String result = servlet.getClickApp().getPagePath(MyPage.class);
            System.out.println("MyPage path => " + result);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void testPluginInContainer() {
        MockContainer container = new MockContainer("test/web");
        PluginClickServlet servlet = new PluginClickServlet();
        container.setClickServlet(servlet);
        container.start();
        
        Page page = container.testPage(MyPage.class);
        System.out.println(container.getHtml());

        container.stop();
    }
}
