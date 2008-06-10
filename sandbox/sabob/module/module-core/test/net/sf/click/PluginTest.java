package net.sf.click;

import clickmodule.page.MyPage;
import junit.framework.TestCase;
import net.sf.click.service.ModuleConfigService;
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

            ModuleClickServlet servlet = new ModuleClickServlet();

            MockContext.initContext(servletConfig, request, response, servlet);

            String result = servlet.getConfigService().getPagePath(MyPage.class);
            System.out.println("MyPage path => " + result);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void testPluginInContainer() {
        MockContainer container = new MockContainer("test/web");
        container.setServletContext(new MockServletContext());
        container.getServletContext().addInitParameter("config-service-class", ModuleConfigService.class.getName());
        ModuleClickServlet servlet = new ModuleClickServlet();
        container.setClickServlet(servlet);
        container.start();
        
        Page page = container.testPage(MyPage.class);
        System.out.println(container.getHtml());

        container.stop();
    }
}
