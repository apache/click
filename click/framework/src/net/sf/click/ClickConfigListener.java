/*
 * Copyright 2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;

import net.sf.click.service.ConfigService;
import net.sf.click.service.XmlConfigService;
import net.sf.click.util.ClickUtils;

/**
 * Provides servlet context listener which will bootstrap the Click application
 * <tt>ConfigService</tt>.
 *
 * <h3>Configuration</h3>
 * To configure the Freemarker TemplateService add the following element to your
 * <tt>click.xml</tt> configuration file.
 *
 * <pre class="codeConfig">
 * &lt;<span class="red">template-service</span> classname="<span class="blue">net.sf.click.extras.service.FreemarkerTemplateService</span>"&gt; </pre>
 *
 * @author Malcolm Edgar
 */
public class ClickConfigListener implements ServletContextListener {

    /**
     * The click application configuration service classname init parameter name:
     * &nbsp; "<tt>config-service-class</tt>".
     */
    protected final static String CONFIG_SERVICE_CLASS = "config-service-class";

    /**
     * Initialize the Click application <tt>ConfigService</tt> instance and bind
     * it to the ServletContext as an attribute.
     *
     * {@link ServletContextListener#contextInitialized(ServletContextEvent)}
     *
     * @param event the servlet context initialization event
     */
    public void contextInitialized(ServletContextEvent event) {

        ServletContext servletContext = event.getServletContext();
        try {

            // Create the global application ConfigService instance
            ConfigService configService = createConfigService(servletContext);

            // Note this order is very important as components need to lookup
            // the configService out of the ServletContext while the service
            // is being initialized.
            servletContext.setAttribute(ConfigService.CONTEXT_NAME, configService);

            // Initialize the ConfigService instance
            configService.onInit(servletContext);

        } catch (Exception e) {
            // In mock mode this exception can occur if click.xml is not
            // available.
            if (servletContext.getAttribute(ClickServlet.MOCK_MODE_ENABLED) != null) {
                return;
            }

            e.printStackTrace();

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Destroy the Click application <tt>ConfigService</tt> instance and remove
     * it as an attribute from the ServletContext.
     *
     * {@link ServletContextListener#contextDestroyed(ServletContextEvent)
     *
     * @param event the servlet context destory event
     */
    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        ConfigService configService = ClickUtils.getConfigService(servletContext);

        if (configService != null) {

            try {
                configService.onDestroy();

                servletContext.setAttribute(ConfigService.CONTEXT_NAME, null);

            } catch (Exception e) {
                // TODO: logging exceptions
                e.printStackTrace();

                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Create a Click application ConfigService instance.
     *
     * @param servletContext the Servlet Context
     * @return a new application ConfigService instance
     * @throws Exception if an initialization error occurs
     */
    protected ConfigService createConfigService(ServletContext servletContext)
        throws Exception {

        Class serviceClass = XmlConfigService.class;

        String classname = servletContext.getInitParameter(CONFIG_SERVICE_CLASS);
        if (StringUtils.isNotBlank(classname)) {
            serviceClass = ClickUtils.classForName(classname);
        }

        return (ConfigService) serviceClass.newInstance();
    }
}
