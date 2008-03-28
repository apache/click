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
package net.sf.click.service;

import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Provides a templating service interface.
 *
 * @author Malcolm Edgar
 */
public interface TemplateService {

    /**
     * Initialize the Template Service with the given application servlet context,
     * application mode and the application character set.
     * <p/>
     * This method is invoked after the Template Service has been constructed.
     *
     * @param servletContext the application servlet context
     * @param applicationMode the Click application mode
     * @param charSet the application character set
     * @throws Exception if an error occurs initializing the Template Service
     */
    public void onInit(ServletContext servletContext, String applicationMode,
            String charSet) throws Exception;

    /**
     * Destroy the Template Service.
     */
    public void onDestroy();

    /**
     * Render the given template and model to the writer.
     *
     * @param templatePath the path of the template to render
     * @param model the model to merge with the template and render
     * @param writer the writer to send the merged template and model data to
     * @throws Exception if an error occurs
     */
    public void renderTemplate(String templatePath, Map model, Writer writer)
        throws Exception;

}
