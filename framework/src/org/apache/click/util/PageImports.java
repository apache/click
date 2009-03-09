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
package org.apache.click.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.Control;
import org.apache.click.Page;
import org.apache.click.control.Container;
import org.apache.click.control.Table;
import org.apache.click.service.LogService;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a utility object for rendering a Page's HTML header imports and its
 * control HTML header imports.
 * <p/>
 * A PageImports instance is automatically added to the Velocity Context
 * for Velocity templates, or as a request attribute for JSP pages using the key
 * name "<span class="blue">imports</span>".
 *
 * <h3>PageImports Examples</h3>
 *
 * To use the PageImports object simply reference it your page header
 * section. For example:
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;body&gt;
 * &lt;/html&gt; </pre>
 *
 * "<span class="blue">imports</span>" include all javascript and stylesheet
 * imports.
 * <p/>
 * PageImports also provides a way of including the javascript and stylesheet
 * separately using the key names "<span class="blue">cssImports</span>" and
 * "<span class="blue">jsImports</span>".
 * <p/>
 * You should follow the performance best practice by importing CSS includes
 * in the head section, then include the JS imports after the html body.
 * For example:
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$cssImports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *   &lt;br/&gt;
 *   <span class="red">$table</span>
 *  &lt;body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsImports</span>
 * </pre>
 *
 * Please also see {@link org.apache.click.Page#getHtmlHeaders()},
 * {@link org.apache.click.Control#getHtmlHeaders()},
 * {@link org.apache.click.Page#getHtmlImports()} and
 * {@link org.apache.click.Control#getHtmlImports()}.
 *
 * @author Malcolm Edgar
 */
public class PageImports {

    /** The page imports initialized flag. */
    protected boolean initialized = false;

    /** The list of CSS import lines. */
    protected List cssImports = new ArrayList();

    /** The list of JS import lines. */
    protected List jsImports = new ArrayList();

    /** The list of JS script block lines. */
    protected List jsScripts = new ArrayList();

    /** The list of CSS styles. */
    protected List cssStyles = new ArrayList();

    /** The page instance. */
    protected final Page page;

    // ------------------------------------------------------------ Constructor

    /**
     * Create a page control HTML includes object.
     *
     * @param page the page to provide HTML includes for
     */
    public PageImports(Page page) {
        this.page = page;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add the given HtmlHeader to the Page HTML imports.
     *
     * @param htmlHeader the HtmlHeader to add
     */
    public void add(HtmlHeader htmlHeader) {
        if (htmlHeader instanceof JavascriptImport) {
            if (jsImports.contains(htmlHeader)) {
                return;
            }
            jsImports.add(htmlHeader);

        } else if (htmlHeader instanceof Javascript) {
            if (((Javascript) htmlHeader).isUnique()) {
                if (jsScripts.contains(htmlHeader)) {
                    return;
                }
            }
            jsScripts.add(htmlHeader);

        } else if (htmlHeader instanceof CssImport) {
            if (cssImports.contains(htmlHeader)) {
                return;
            }
            cssImports.add(htmlHeader);

        } else if (htmlHeader instanceof Css) {
            if (((Css) htmlHeader).isUnique()) {
                if (cssStyles.contains(htmlHeader)) {
                    return;
                }
            }
            cssStyles.add(htmlHeader);

        } else {
            throw new IllegalArgumentException(htmlHeader.getClass().getName()
                + " is not a supported type.");
        }
    }

    /**
     * Add the given HTML import line to the Page HTML imports.
     *
     * @deprecated use the new {@link #add(org.apache.click.util.HtmlHeader)}
     * instead
     *
     * @param value the HTML import line to add
     */
    public void addImport(String value) {
        if (value == null || value.length() == 0) {
            return;
        }

        String[] lines = StringUtils.split(value, '\n');

        for (int i = 0; i  < lines.length; i++) {
            String line = lines[i].trim().toLowerCase();
            if (line.startsWith("<link") && line.indexOf("text/css") != -1) {
                CssImport cssImport = asCssImport(lines[i]);
                add(cssImport);

            } else if (line.startsWith("<style") && line.indexOf("text/css") != -1) {
                Css css = asCss(lines[i]);
                css.setUnique(true);
                add(css);

            } else if (line.startsWith("<script")) {
                if (line.indexOf(" src=") != -1) {
                    JavascriptImport javascriptImport = asJavascriptImport(lines[i]);
                    add(javascriptImport);

                } else {
                    Javascript javascript = asJavascript(lines[i]);
                    javascript.setUnique(true);
                    add(javascript);

                }
            } else {
                throw new IllegalArgumentException("Unknown include type: " + lines[i]);
            }
        }
    }

    /**
     * Return true if the page imports have been initialized.
     *
     * @return true if the page imports have been initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Set whether the page imports have been initialized.
     *
     * @param initialized the page imports have been initialized flag
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Populate the specified model with html import keys.
     *
     * @param model the model to populate with html import keys
     */
    public void popuplateTemplateModel(Map model) {
        LogService logger = ClickUtils.getLogService();
        Object pop = model.put("imports", new Imports());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"imports\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        pop = model.put("cssImports", new CssImports());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
            + " model contains an object keyed with reserved "
            + "name \"cssImports\". The page model object "
            + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        pop = model.put("jsImports", new JsImports());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
            + " model contains an object keyed with reserved "
            + "name \"jsImports\". The page model object "
            + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }
    }

    /**
     * Populate the specified request with html import keys.
     *
     * @param request the http request to populate
     * @param model the model to populate with html import keys
     */
    public void popuplateRequest(HttpServletRequest request, Map model) {
        LogService logger = ClickUtils.getLogService();
        request.setAttribute("imports", new Imports());
        if (model.containsKey("imports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"imports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        request.setAttribute("cssImports", new CssImports());
        if (model.containsKey("cssImports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"cssImports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        request.setAttribute("jsImports", new JsImports());
        if (model.containsKey("jsImports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"jsImports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render an HTML representation of all the page's HTML imports,
     * including: CSS imports, CSS styles, JS imports and JS scripts.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderAllIncludes(HtmlStringBuffer buffer) {
        renderCssImports(buffer);
        renderJsImports(buffer);
    }

    /**
     * Render an HTML representation of all all the page's HTML CSS
     * {@link #cssImports imports} and {@link #cssStyles styles}.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderCssImports(HtmlStringBuffer buffer) {
        // First include all the imports e.g. <link href="...">
        for (Iterator it = cssImports.iterator(); it.hasNext();) {
            CssImport cssImport = (CssImport) it.next();
            cssImport.render(buffer);
            buffer.append('\n');
        }

        // Then include all the styles e.g. <style>...</style>
        for (Iterator it = cssStyles.iterator(); it.hasNext();) {
            Css cssInclude = (Css) it.next();
            cssInclude.render(buffer);
            buffer.append('\n');
        }
    }

    /**
     * Render an HTML representation of all the page's HTML JavaScript
     * {@link #jsImports imports} and {@link #jsScripts scripts}.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderJsImports(HtmlStringBuffer buffer) {
        // First include all the imports e.g. <script src="...">
        for (Iterator it = jsImports.iterator(); it.hasNext();) {
            JavascriptImport javascriptImport = (JavascriptImport) it.next();
            javascriptImport.render(buffer);
            buffer.append('\n');
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = jsScripts.iterator(); it.hasNext();) {
            Javascript javascriptInclude = (Javascript) it.next();
            javascriptInclude.render(buffer);
            buffer.append('\n');
        }
    }

    /**
     * Process the Page's set of control HTML head imports.
     */
    protected void processPageControls() {
        if (isInitialized()) {
            return;
        }

        setInitialized(true);

        if (page.hasControls()) {
            for (int i = 0; i < page.getControls().size(); i++) {
                Control control = (Control) page.getControls().get(i);

                // import from getHtmlImports
                addImport(control.getHtmlImports());

                // import from getHtmlHeaders
                processControl(control);
            }
        }

        addImport(page.getHtmlImports());

        processHtmlHeaders(page.getHtmlHeaders());
    }

    /**
     * Process the given control HTML headers. This method will recursively
     * process Containers and all child controls.
     * <p/>
     * This method delegates to {@link #processHtmlHeaders(java.util.List)}
     * to add the HTML headers to the Page imports.
     *
     * @param control the control to process
     */
    protected void processControl(Control control) {
        processHtmlHeaders(control.getHtmlHeaders());

        if (control instanceof Container) {
            Container container = (Container) control;
            if (container.hasControls()) {
                List controls = container.getControls();
                for (int i = 0, size = controls.size(); i < size; i++) {
                    processControl((Control) controls.get(i));
                }
            }

        } else if (control instanceof Table) {
            Table table = (Table) control;
            if (table.hasControls()) {
                List controls = table.getControls();
                for (int i = 0, size = controls.size(); i < size; i++) {
                    processControl((Control) controls.get(i));
                }
            }
        }
    }

    /**
     * Process the given list of HTML headers.
     * <p/>
     * This method invokes {@link #add(org.apache.click.util.HtmlHeader)} for
     * every <tt>HtmlHeader</tt> entry in the specified list.
     *
     * @param htmlHeaders the list of HTML headers to process
     */
    protected void processHtmlHeaders(List htmlHeaders) {
        if (htmlHeaders == null || htmlHeaders.isEmpty()) {
            return;
        }

        Iterator it = htmlHeaders.iterator();
        while (it.hasNext()) {
            add((HtmlHeader) it.next());
        }
    }

    // ------------------------------------------------------- Internal Classes

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderAllIncludes(org.apache.click.util.HtmlStringBuffer)}.
     */
    class Imports {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing all includes
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * jsImports.size()
                + 80 * jsScripts.size()
                + 80 * cssImports.size()
                + 80 * cssStyles.size());
            PageImports.this.renderAllIncludes(buffer);
            return buffer.toString();
        }
    }

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderJsImports(org.apache.click.util.HtmlStringBuffer)}.
     */
    class JsImports {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing all JavaScript imports
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * jsImports.size() + 80 * jsScripts.size());

            PageImports.this.renderJsImports(buffer);
            return buffer.toString();
        }
    }

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderCssImports(org.apache.click.util.HtmlStringBuffer)}.
     */
    class CssImports {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing all CSS imports
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * cssImports.size() + 80 * cssStyles.size());

            PageImports.this.renderCssImports(buffer);
            return buffer.toString();
        }
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Convert the given HTML import line to a {@link CssImport} instance.
     *
     * @param line the HTML import line to convert to a CssImport instance
     * @return a CssImport instance
     */
    private CssImport asCssImport(String line) {
        CssImport cssImport = new CssImport();
        copyAttributes(cssImport, line);
        return cssImport;
    }

    /**
     * Convert the given HTML import line to a {@link Css} instance.
     *
     * @param line the HTML import line to convert to a Css instance
     * @return a Css instance
     */
    private Css asCss(String line) {
        Css css = new Css();
        copyAttributes(css, line);
        css.append(extractContent(line));
        return css;
    }

    /**
     * Convert the given HTML import line to a {@link JavascriptImport} instance.
     *
     * @param line the HTML import line to convert to a JavaScriptImport instance
     * @return a JavascriptImport instance
     */
    private JavascriptImport asJavascriptImport(String line) {
        JavascriptImport javascriptImport = new JavascriptImport();
        copyAttributes(javascriptImport, line);
        return javascriptImport;
    }

    /**
     * Convert the given HTML import line to a {@link Javascript} instance.
     *
     * @param line the HTML import line to convert to a JavaScript instance
     * @return a Javascript instance
     */
    private Javascript asJavascript(String line) {
        Javascript javascript = new Javascript();
        copyAttributes(javascript, line);
        javascript.append(extractContent(line));
        return javascript;
    }

    /**
     * Extract the JavaScript or CSS content from the given HTML import line.
     *
     * @param line the HTML import line
     * @return the JavaScript or CSS content contained in the HTML import line
     */
    private String extractContent(String line) {
        if (line.endsWith("/>")) {
            // If tag has no content, exit early
            return "";
        }

        // Find index where tag ends
        int start = line.indexOf('>');
        if (start == -1) {
            throw new IllegalArgumentException(line + " is not a valid element");
        }
        int end = line.indexOf('<', start);
        if (end == -1) {
            return "";
        }
        return line.substring(start + 1, end);
    }

    /**
     * Copy all available attributes from HTML import line to the HtmlHeader
     * instance.
     *
     * @param htmlHeader the HTML header to copy the attributes to
     * @param line the HTML import line to copy attributes from
     */
    private void copyAttributes(HtmlHeader htmlHeader, String line) {
        // Find index where attributes start: the first space
        int start = line.indexOf(' ');
        if (start == -1) {
            // If no attributes found, exit early
            return;
        }

        // Find index where attributes end: closing tag
        int end = line.indexOf("/>");
        if (end == -1) {
            end = line.indexOf(">");
        }
        if (end == -1) {
            throw new IllegalArgumentException(line + " is not a valid HTML import");
        }

        line = line.substring(start, end);
        StringTokenizer tokens = new StringTokenizer(line, " ");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            StringTokenizer attribute = new StringTokenizer(token, "=");
            String key = attribute.nextToken();
            String value = attribute.nextToken();
            htmlHeader.setAttribute(key, StringUtils.strip(value, "'\""));
        }
    }
}
