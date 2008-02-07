/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * Provides a package visibility interface for the Context class for
 * accessing ClickApp and ClickServlet services.
 *
 * @author Malcolm Edgar
 */
class ClickService {

    /** The application's ClickServlet instance. */
    private final ClickServlet clickServlet;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a ClickService class using the given ClickSerlvet.
     *
     * @param clickServlet the application's ClickServlet instance
     */
    ClickService(ClickServlet clickServlet) {
        this.clickServlet = clickServlet;
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Return a new Page instance for the given path.
     *
     * @param path the Page path configured in the click.xml file
     * @return a new Page object
     * @throws IllegalArgumentException if the Page is not found
     */
    Page createPage(String path, HttpServletRequest request) {
        Class pageClass = clickServlet.clickApp.getPageClass(path);

        if (pageClass == null) {
            String msg = "No Page class configured for path: " + path;
            throw new IllegalArgumentException(msg);
        }

        return clickServlet.initPage(path, pageClass, request);
    }

    /**
     * Return a new Page instance for the page Class.
     *
     * @param pageClass the class of the Page to create
     * @return a new Page object
     * @throws IllegalArgumentException if the Page Class is not configured
     * with a unique path
     */
    Page createPage(Class pageClass, HttpServletRequest request) {
        String path = clickServlet.clickApp.getPagePath(pageClass);

        if (path == null) {
            String msg =
                "No path configured for Page class: " + pageClass.getName();
            throw new IllegalArgumentException(msg);
        }

        return clickServlet.initPage(path, pageClass, request);
    }

    /**
     * Return the Click application mode value: &nbsp;
     * <tt>["production", "profile", "development", "debug", "trace"]</tt>.
     *
     * @return the application mode value
     */
    String getApplicationMode() {
        return clickServlet.clickApp.getModeValue();
    }

    /**
     * Return the Click application charset or null if not defined.
     *
     * @return the application charset value
     */
    String getCharset() {
        return clickServlet.clickApp.getCharset();
    }

    /**
     * Return the Click application FileItemFactory.
     *
     * @return the application FileItemFactory
     */
    FileItemFactory getFileItemFactory() {
        return clickServlet.clickApp.getFileItemFactory();
    }

    /**
     * Return the Click application locale or null if not defined.
     *
     * @return the application locale value
     */
    Locale getLocale() {
        return clickServlet.clickApp.getLocale();
    }

    /**
     * Return the path for the given page Class.
     *
     * @param pageClass the class of the Page to lookup the path for
     * @return the path for the given page Class
     * @throws IllegalArgumentException if the Page Class is not configured
     * with a unique path
     */
    String getPagePath(Class pageClass) {
        String path = clickServlet.clickApp.getPagePath(pageClass);

        if (path == null) {
            String msg =
                "No path configured for Page class: " + pageClass.getName();
            throw new IllegalArgumentException(msg);
        }

        return path;
    }

    /**
     * Return a rendered Velocity template and model for the given
     * class and model data.
     * <p/>
     * This method will merge the class <tt>.htm</tt> and model using the
     * Velocity Engine.
     * <p/>
     * An example of the class template resolution is provided below:
     * <pre class="codeConfig">
     * <span class="cm">// Full class name</span>
     * com.mycorp.control.CustomTextField
     *
     * <span class="cm">// Template path name</span>
     * /com/mycorp/control/CustomTextField.htm </pre>
     *
     * Example method usage:
     * <pre class="codeJava">
     * <span class="kw">public String</span> toString() {
     *     Map model = getModel();
     *     <span class="kw">return</span> getContext().renderTemplate(getClass(), model);
     * } </pre>
     *
     * @param templateClass the class to resolve the template for
     * @param model the model data to merge with the template
     * @return rendered Velocity template merged with the model data
     * @throws RuntimeException if an error occurs
     */
    String renderTemplate(Class templateClass, Map model) {

        if (templateClass == null) {
            String msg = "Null templateClass parameter";
            throw new IllegalArgumentException(msg);
        }

        String templatePath = templateClass.getName();
        templatePath = '/' + templatePath.replace('.', '/') + ".htm";

        return renderTemplate(templatePath, model);
    }

    /**
     * Return a rendered Velocity template and model data.
     * <p/>
     * Example method usage:
     * <pre class="codeJava">
     * <span class="kw">public String</span> toString() {
     *     Map model = getModel();
     *     <span class="kw">return</span> getContext().renderTemplate(<span class="st">"/custom-table.htm"</span>, model);
     * } </pre>
     *
     * @param templatePath the path of the Velocity template to render
     * @param model the model data to merge with the template
     * @return rendered Velocity template merged with the model data
     * @throws RuntimeException if an error occurs
     */
    String renderTemplate(String templatePath, Map model) {

        if (templatePath == null) {
            String msg = "Null templatePath parameter";
            throw new IllegalArgumentException(msg);
        }

        if (model == null) {
            String msg = "Null model parameter";
            throw new IllegalArgumentException(msg);
        }

        VelocityContext context = new VelocityContext(model);

        StringWriter stringWriter = new StringWriter(1024);

        try {
            Template template = null;

            String charset = clickServlet.clickApp.getCharset();
            if (charset != null) {
                template = clickServlet.clickApp.getTemplate(templatePath, charset);
            } else {
                template = clickServlet.clickApp.getTemplate(templatePath);
            }

            if (template == null) {
                String msg =
                    "Template not found for template path: " + templatePath;
                throw new IllegalArgumentException(msg);
            }

            template.merge(context, stringWriter);

        } catch (Exception e) {
            String msg = "Error occured rendering template: "
                         + templatePath;
            clickServlet.logger.error(msg, e);

            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

}
