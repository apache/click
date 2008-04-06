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
import net.sf.click.service.TemplateService;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * Provides a Freemarker TemplateService class.
 *
 * @author Malcolm Edgar
 */
public class FreemarkerTemplateService implements TemplateService {

    /** The Freemarker engine configuration. */
    protected Configuration configuration = new Configuration();

    /**
     * @see TemplateService#onInit(ConfigService)
     *
     * @param configService the application configuration service instance
     * @throws Exception if an error occurs initializing the Template Service
     */
    public void onInit(ConfigService configService) throws Exception {

        ServletContext servletContext = configService.getServletContext();

        // Templates are stoted in the / directory of the Web app.
        configuration.setServletContextForTemplateLoading(servletContext, "");

        // Set update dealy to 0 for now, to ease debugging and testing.
        // Higher value should be used in production environment.
        if (!configService.isProductionMode() && configService.isProfileMode()) {
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

}
