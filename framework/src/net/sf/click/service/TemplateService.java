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

import net.sf.click.Page;

/**
 * Provides a templating service interface.
 *
 * @author Malcolm Edgar
 */
public interface TemplateService {

    /**
     * Initialize the TemplateService with the given application configuration
     * service instance.
     * <p/>
     * This method is invoked after the TemplateService has been constructed.
     *
     * @param configService the application configuration service instance
     * @throws Exception if an error occurs initializing the Template Service
     */
    public void onInit(ConfigService configService) throws Exception;

    /**
     * Destroy the TemplateService.
     */
    public void onDestroy();

    /**
     * Render the given page to the writer.
     *
     * @param page the page template to render
     * @param model the model to merge with the template and render
     * @param writer the writer to send the merged template and model data to
     * @throws Exception if an error occurs
     */
    public void renderTemplate(Page page, Map model, Writer writer) throws Exception;

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
