/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.service;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.click.Page;
import org.apache.click.service.ConfigService;
import org.apache.click.service.LogService;
import org.apache.click.service.TemplateException;
import org.apache.click.service.TemplateService;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.Validate;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Provides a <a target="_blank" href="http://www.freemarker.org/">Freemarker</a> TemplateService class.
 *
 * <h3>Configuration</h3>
 * To configure the Freemarker TemplateService add the following element to your
 * <tt>click.xml</tt> configuration file.
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;click-app charset="UTF-8"&gt;
 *
 *     &lt;pages package="org.apache.click.examples.page"/&gt;
 *
 *     &lt;template-service classname="org.apache.click.extras.service.FreemarkerTemplateService"/&gt;
 *
 * &lt;/click-app&gt; </pre>
 *
 * <b>Please note</b> that Click ships with a default <em>error.htm</em> that
 * is tailored for Velocity.
 * <p/>
 * If you switch to Freemarker replace the default <em>error.htm</em> with the
 * one shown below.
 * <p/>
 * To ensure Click uses your template instead of the default one, copy/paste
 * the template below to the web application path <em>/click/error.htm</em>.
 * Click won't override your custom template.
 *
 * <pre class="prettyprint">
 * &lt;html&gt;
 *   &lt;head&gt;
 *     &lt;title&gt;Error Page&lt;/title&gt;
 *     &lt;style  type='text/css'&gt;
 *       body, table, td {
 *       font-family: arial, sans-serif;
 *       font-size: 12px;
 *     }
 *     td.header {
 *       color: white;
 *       background: navy;
 *     }
 *     .errorReport {
 *       display: none;
 *     }
 *     a {
 *       color: blue;
 *     }
 *     &lt;/style&gt;
 *     &lt;script type='text/javascript'&gt;
 *       function displayError() {
 *         errorReport.style.display = 'block';
 *       }
 *     &lt;/script&gt;
 *   &lt;/head&gt;
 *
 *   &lt;body&gt;
 *     &lt;h1&gt;Error Page&lt;/h1&gt;
 *
 *     &lt;#if errorReport??&gt;
 *       The application encountered an unexpected error.
 *       &lt;p/&gt;
 *       To return to the application click &lt;a href="${context}"&gt;here&lt;/a&gt;.
 *       &lt;p/&gt;
 *
 *       &lt;#if mode != "production"&gt;
 *         To view the error details click &lt;a href="#" onclick="displayError();"&gt;here&lt;/a&gt;.
 *         &lt;p/&gt;
 *         ${errorReport}
 *         &lt;p/&gt;
 *       &lt;/#if&gt;
 *     &lt;/#if&gt;
 *
 *   &lt;/body&gt;
 * &lt;/html&gt;
 * </pre>
 */
public class FreemarkerTemplateService implements TemplateService {

    /** The click error page template path. */
    protected static final String ERROR_PAGE_PATH = "/click/error.htm";

    /** The click not found page template path. */
    protected static final String NOT_FOUND_PAGE_PATH = "/click/not-found.htm";

    // -------------------------------------------------------------- Variables

    /** The Freemarker engine configuration. */
    protected Configuration configuration;

    /**
     * The production/profile mode cache duration in seconds. The default value
     * is 24 hours.
     */
    protected int cacheDuration = 60 * 60 * 24;

    /** The application configuration service. */
    protected ConfigService configService;

    /** The /click/error.htm page template has been deployed. */
    protected boolean deployedErrorTemplate;

    /** The /click/not-found.htm page template has been deployed. */
    protected boolean deployedNotFoundTemplate;

    // --------------------------------------------------------- Public Methods

    /**
     * @see TemplateService#onInit(javax.servlet.ServletContext)
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the Template Service
     */
    public void onInit(ServletContext servletContext) throws Exception {

        Validate.notNull(servletContext, "Null servletContext parameter");

        configService = ClickUtils.getConfigService(servletContext);

        // Attempt to match Freemarker Logger to configured LogService type
        LogService logService = configService.getLogService();
        if (logService instanceof Log4JLogService) {
            Logger.selectLoggerLibrary(Logger.LIBRARY_LOG4J);

        } else if (logService instanceof JdkLogService) {
            Logger.selectLoggerLibrary(Logger.LIBRARY_JAVA);
        }

        configuration = new Configuration();

        // Templates are stored in the / directory of the Web app.
        WebappTemplateLoader webloader = new WebappTemplateLoader(servletContext);

        // Templates are stored in the root of the classpath.
        ClassTemplateLoader classLoader = new ClassTemplateLoader(getClass(), "/");
        TemplateLoader[] loaders = new TemplateLoader[] { webloader, classLoader };
        MultiTemplateLoader multiLoader = new MultiTemplateLoader(loaders);

        configuration.setTemplateLoader(multiLoader);

        // Set the template cache duration in seconds
        if (configService.isProductionMode() || configService.isProfileMode()) {
            configuration.setTemplateUpdateDelay(getCacheDuration());

        } else {
            configuration.setTemplateUpdateDelay(0);
        }

        // Set an error handler that prints errors so they are readable with
        // a HTML browser.
        configuration.setTemplateExceptionHandler(
                TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        // Use beans wrapper (recommended for most applications)
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

        // Attempt to load click error page and not found templates from the
        // web click directory
        try {
            configuration.getTemplate(ERROR_PAGE_PATH);
            deployedErrorTemplate = true;
        } catch (IOException ioe) {
        }

        try {
            configuration.getTemplate(NOT_FOUND_PAGE_PATH);
            deployedNotFoundTemplate = true;
        } catch (IOException ioe) {
        }
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
     * @throws IOException if an IO error occurs
     * @throws TemplateException if template error occurs
     */
    public void renderTemplate(Page page, Map<String, ?> model, Writer writer)
        throws IOException, TemplateException {

        String templatePath = page.getTemplate();

        if (!deployedErrorTemplate && templatePath.equals(ERROR_PAGE_PATH)) {
            templatePath = "META-INF/resources" + ERROR_PAGE_PATH;
        }
        if (!deployedErrorTemplate && templatePath.equals(NOT_FOUND_PAGE_PATH)) {
            templatePath = "META-INF/resources" + NOT_FOUND_PAGE_PATH;
        }

        // Get the template object
        Template template = configuration.getTemplate(templatePath);

        // Merge the data-model and the template
        try {
            template.process(model, writer);

        } catch (freemarker.template.TemplateException fmte) {
            throw new TemplateException(fmte);
        }
    }

    /**
     * @see TemplateService#renderTemplate(String, Map, Writer)
     *
     * @param templatePath the path of the template to render
     * @param model the model to merge with the template and render
     * @param writer the writer to send the merged template and model data to
     * @throws IOException if an IO error occurs
     * @throws TemplateException if template error occurs
     */
    public void renderTemplate(String templatePath, Map<String, ?> model, Writer writer)
            throws IOException, TemplateException {

        // Get the template object
        Template template = configuration.getTemplate(templatePath);

        // Merge the data-model and the template
        try {
            template.process(model, writer);

        } catch (freemarker.template.TemplateException fmte) {
            throw new TemplateException(fmte);
        }
    }

    /**
     * Return the template cache duration in seconds to use when the application
     * is in "production" or "profile" mode.
     *
     * @return the cacheDuration the template cache duration in seconds
     */
    public int getCacheDuration() {
        return cacheDuration;
    }

    /**
     * Return the template cache duration in seconds to use when the application
     * is in "production" or "profile" mode.
     *
     * @param cacheDuration the template cache duration in seconds to set
     */
    public void setCacheDuration(int cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

}
