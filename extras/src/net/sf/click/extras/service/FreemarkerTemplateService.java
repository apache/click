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
package net.sf.click.extras.service;

import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Page;
import net.sf.click.service.ConfigService;
import net.sf.click.service.LogService;
import net.sf.click.service.TemplateService;
import net.sf.click.util.ClickUtils;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang.Validate;

/**
 * Provides a <a target="_blank" href="http://www.freemarker.org/">Freemarker</a> TemplateService class.
 *
 * <h3>Configuration</h3>
 * To configure the Freemarker TemplateService add the following element to your
 * <tt>click.xml</tt> configuration file.
 *
 * <pre class="codeConfig">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;click-app charset="UTF-8"&gt;
 *
 *     &lt;pages package="net.sf.click.examples.page"/&gt;
 *
 *     &lt;<span class="red">template-service</span> classname="<span class="blue">net.sf.click.extras.service.FreemarkerTemplateService</span>"&gt;
 *
 * &lt;/click-app&gt; </pre>
 *
 * @author Malcolm Edgar
 */
public class FreemarkerTemplateService implements TemplateService {

    /** The Freemarker engine configuration. */
    protected Configuration configuration;

    /**
     * The production/profile mode cache duration in seconds. The default value
     * is 24 hours.
     */
    protected int cacheDuration = 60 * 60 * 24;

    /**
     * @see TemplateService#onInit(javax.servlet.ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the Template Service
     */
    public void onInit(ServletContext servletContext) throws Exception {

        Validate.notNull(servletContext, "Null servletContext parameter");

        ConfigService configService = ClickUtils.getConfigService(servletContext);

        // Attempt to match Freemarker Logger to configured LogService type
        LogService logService = configService.getLogService();
        if (logService instanceof Log4JLogService) {
            Logger.selectLoggerLibrary(Logger.LIBRARY_LOG4J);

        } else if (logService instanceof JdkLogService) {
            Logger.selectLoggerLibrary(Logger.LIBRARY_JAVA);
        }

        configuration = new Configuration();

        // Templates are stoted in the / directory of the Web app.
        configuration.setServletContextForTemplateLoading(servletContext, "");

        // Set the template cache duration in seconds
        if (configService.isProductionMode() | configService.isProfileMode()) {
            configuration.setTemplateUpdateDelay(getCacheDuration());

        } else {
            configuration.setTemplateUpdateDelay(0);
        }

        // Set an error handler that prints errors so they are readable with
        // a HTML browser.
        configuration.setTemplateExceptionHandler(
                TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        // Use beans wrapper (recommmended for most applications)
        configuration.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);

        String charset = configService.getCharset();
        if (charset != null) {
            // Set the default charset of the template files
            configuration.setDefaultEncoding(charset);

            // Set the charset of the output. This is actually just a hint, that
            // templates may require for URL encoding and for generating META element
            // that uses http-equiv="Content-type".
            configuration.setOutputEncoding(charset);
        }

        // Set the default locale
        if (configService.getLocale() != null) {
            configuration.setLocale(configService.getLocale());
        }

        // Deploy Freemarker error.htm template
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/service/error.htm",
                              "click");
    }

    /**
     * @see TemplateService#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * @see TemplateService#renderTemplate(Page, Map, Writer)
     *
     * @param page the page template to render
     * @param model the model to merge with the template and render
     * @param writer the writer to send the merged template and model data to
     * @throws Exception if an error occurs
     */
    public void renderTemplate(Page page, Map model, Writer writer)
            throws Exception {

        // Get the template object
        Template template = configuration.getTemplate(page.getTemplate());

        // Merge the data-model and the template
        template.process(model, writer);
    }

    /**
     * @see TemplateService#renderTemplate(String, Map, Writer)
     *
     * @param templatePath the path of the template to render
     * @param model the model to merge with the template and render
     * @param writer the writer to send the merged template and model data to
     * @throws Exception if an error occurs
     */
    public void renderTemplate(String templatePath, Map model, Writer writer)
            throws Exception {

        // Get the template object
        Template template = configuration.getTemplate(templatePath);

        // Merge the data-model and the template
        template.process(model, writer);
    }

    /**
     * Return the template cache duration in seconds to use when the application
     * is in "productin" or "profile" mode.
     *
     * @return the cacheDuration the template cache duration in seconds
     */
    public int getCacheDuration() {
        return cacheDuration;
    }

    /**
     * Return the template cache duration in seconds to use when the application
     * is in "productin" or "profile" mode.
     *
     * @param cacheDuration the template cache duration in seconds to set
     */
    public void setCacheDuration(int cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

}
