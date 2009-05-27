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
import org.apache.click.element.CssImport;
import org.apache.click.element.CssStyle;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.service.LogService;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a utility object for rendering a Page's HEAD elements and
 * control HEAD elements.
 * <p/>
 * PageImports automatically makes the HEAD elements available to Velocity
 * templates and JSP pages through the following variables:
 * <ul>
 * <li><span class="st">$headElements</span> - this variable includes all HEAD
 * elements except JavaScript elements</li>
 * <li><span class="st">$jsElements</span> - this variable includes only
 * JavaScript elements</li>
 * </ul>
 * By splitting JavaScript elements from other HEAD elements allows you to place
 * JavaScript elements at the bottom of the Page which allows the HTML content
 * to be rendered faster.
 * <p/>
 * To use the HEAD elements simply reference them in your page template. For
 * example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$headElements</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *
 *  <span class="blue">$jsElements</span>
 *  &lt;body&gt;
 * &lt;/html&gt; </pre>
 *
 * Its not always possible to move the JavaScript elements to the bottom of
 * the Page, for example there might be JavaScript scoping issues. In those
 * situations you can simply place the JavaScript elements variable in the Page
 * HEAD section:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$headElements</span>
 *   <span class="blue">$jsElements</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;body&gt;
 * &lt;/html&gt; </pre>
 *
 * <b>Please note: </b>the variables <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> are new in Click 2.1.0. For backwards
 * compatibility the HEAD elements are also available through the following
 * variables:
 *
 * <ul>
 * <li><span class="st">$imports</span> - this variable includes all HEAD
 * elements including JavaScript and Css elements</li>
 * <li><span class="st">$cssImports</span> - this variable includes only Css elements</li>
 * <li><span class="st">$jsImports</span> - this variable includes only Javascript elements</li>
 * </ul>
 *
 * Please also see {@link org.apache.click.Page#getHeadElements()},
 * {@link org.apache.click.Control#getHeadElements()}.
 *
 * @author Malcolm Edgar
 */
public class PageImports {

    /** The page imports initialized flag. */
    protected boolean initialized = false;

    /** The list of head elements. */
    protected List headElements = new ArrayList();

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
     * Add the given Element to the Page HEAD elements.
     *
     * @param element the Element to add
     */
    public void add(Element element) {
        if (element instanceof JsImport) {
            if (jsImports.contains(element)) {
                return;
            }
            jsImports.add(element);

        } else if (element instanceof JsScript) {
            if (((JsScript) element).isUnique()) {
                if (jsScripts.contains(element)) {
                    return;
                }
            }
            jsScripts.add(element);

        } else if (element instanceof CssImport) {
            if (cssImports.contains(element)) {
                return;
            }
            cssImports.add(element);

        } else if (element instanceof CssStyle) {
            if (((CssStyle) element).isUnique()) {
                if (cssStyles.contains(element)) {
                    return;
                }
            }
            cssStyles.add(element);

        } else {
            headElements.add(element);
        }
    }

    /**
     * Add the given HTML import line to the Page HTML imports.
     *
     * @deprecated use the new {@link #add(org.apache.click.element.Element)}
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
                CssStyle cssStyle = asCssStyle(lines[i]);
                setUnique(cssStyle, cssStyle.getContent().toString());
                add(cssStyle);

            } else if (line.startsWith("<script")) {
                if (line.indexOf(" src=") != -1) {
                    JsImport jsImport = asJsImport(lines[i]);
                    add(jsImport);

                } else {
                    JsScript jsScript = asJsScript(lines[i]);
                    setUnique(jsScript, jsScript.getContent().toString());
                    add(jsScript);

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

        Object pop = model.put("headElements", new HeadElements());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"headElements\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        pop = model.put("jsElements", new JsElements());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"jsElements\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        pop = model.put("imports", new Imports());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"imports\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        pop = model.put("cssImports", new CssElements());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
            + " model contains an object keyed with reserved "
            + "name \"cssImports\". The page model object "
            + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        pop = model.put("jsImports", new JsElements());
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

        request.setAttribute("headElements", new HeadElements());
        if (model.containsKey("headElements")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"headElements\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        request.setAttribute("jsElements", new JsElements());
        if (model.containsKey("jeElements")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"jsElements\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        request.setAttribute("imports", new Imports());
        if (model.containsKey("imports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"imports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        request.setAttribute("cssImports", new CssElements());
        if (model.containsKey("cssImports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"cssImports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        // For backwards compatibility
        request.setAttribute("jsImports", new JsElements());
        if (model.containsKey("jsImports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"jsImports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }
    }

    /**
     * Process the HEAD elements of the given list of Controls. You can retrieve
     * the processed HEAD elements through {@link #getHeadElements} and
     * {@link #getJsElements()}.
     * <p/>
     * This method delegates to {@link #processControl(org.apache.click.Control)}
     * to add the given Control's HEAD elements to the Page imports.
     *
     * @param controls the list of Controls which HEAD elements to process
     */
    public void processControls(List controls) {
        for (int i = 0; i < controls.size(); i++) {
            Control control = (Control) controls.get(i);

            // import from getHtmlImports
            addImport(control.getHtmlImports());

            // import from getHeadElement
            processControl(control);
        }
    }

    /**
     * Process the given control HEAD elements. This method will recursively
     * process Containers and all child controls. You can retrieve
     * the processed HEAD elements through {@link #getHeadElements} and
     * {@link #getJsElements()}.
     * <p/>
     * This method delegates to {@link #processHeadElements(java.util.List)}
     * to add the HEAD elements to the Page imports.
     *
     * @param control the control to process
     */
    public void processControl(Control control) {
        processHeadElements(control.getHeadElements());

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
     * Return the list of processed HEAD elements, excluding any JavaScript
     * elements. To retrieve JavaScript elements please see
     * {@link #getJsElements()}.
     *
     * @return the list of processed HEAD elements
     */
    public final List getHeadElements() {
        List result = new ArrayList(headElements);
        result.addAll(cssImports);
        result.addAll(cssStyles);
        return result;
    }

    /**
     * Return the list of processed JavaScript elements.
     *
     * @return the list of processed JavaScript elements
     */
    public final List getJsElements() {
        List result = new ArrayList(jsImports);
        result.addAll(jsScripts);
        return result;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render an HTML representation of all the page's HTML head elements,
     * including: CSS imports, CSS styles, Title and Meta elements.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderHeadElements(HtmlStringBuffer buffer) {
        // First include miscellaneous elements e.g. Title and Meta elements.
        for (Iterator it = headElements.iterator(); it.hasNext();) {
            Element element = (Element) it.next();
            element.render(buffer);
            buffer.append('\n');
        }

        // Next include all CSS imports and styles.
        renderCssElements(buffer);
    }

    /**
     * Render an HTML representation of all the page's HTML imports,
     * including: CSS imports, CSS styles, JS imports and JS scripts.
     *
     * @deprecated rather use {@link #renderHeadElements(org.apache.click.util.HtmlStringBuffer)}
     * and {@link #renderJsElements(org.apache.click.util.HtmlStringBuffer)}
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderAllIncludes(HtmlStringBuffer buffer) {
        renderCssElements(buffer);
        renderJsElements(buffer);
    }

    /**
     * Render an HTML representation of all all the page's HTML CSS
     * {@link #cssImports imports} and {@link #cssStyles styles}.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderCssElements(HtmlStringBuffer buffer) {
        // First include all the imports e.g. <link href="...">
        for (Iterator it = cssImports.iterator(); it.hasNext();) {
            CssImport cssImport = (CssImport) it.next();
            cssImport.render(buffer);
            buffer.append('\n');
        }

        // Then include all the styles e.g. <style>...</style>
        for (Iterator it = cssStyles.iterator(); it.hasNext();) {
            CssStyle cssStyle = (CssStyle) it.next();
            cssStyle.render(buffer);
            buffer.append('\n');
        }
    }

    /**
     * Render an HTML representation of all the page's HTML JavaScript
     * {@link #jsImports imports} and {@link #jsScripts scripts}.
     *
     * @param buffer the specified buffer to render the page's HTML imports to
     */
    protected void renderJsElements(HtmlStringBuffer buffer) {
        // First include all the imports e.g. <script src="...">
        for (Iterator it = jsImports.iterator(); it.hasNext();) {
            JsImport jsImport = (JsImport) it.next();
            jsImport.render(buffer);
            buffer.append('\n');
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = jsScripts.iterator(); it.hasNext();) {
            JsScript jsScript = (JsScript) it.next();
            jsScript.render(buffer);
            buffer.append('\n');
        }
    }

    /**
     * Process the Page's set of control HEAD elements.
     */
    protected void processPageControls() {
        if (isInitialized()) {
            return;
        }

        setInitialized(true);

        if (page.hasControls()) {
            processControls(page.getControls());
        }

        addImport(page.getHtmlImports());

        processHeadElements(page.getHeadElements());
    }

    /**
     * Process the given list of HEAD elements.
     * <p/>
     * This method invokes {@link #add(org.apache.click.element.Element)} for
     * every <tt>Element</tt> entry in the specified list.
     *
     * @param elements the list of HEAD elements to process
     */
    protected void processHeadElements(List elements) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        Iterator it = elements.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (!(item instanceof Element)) {
                throw new IllegalStateException(item.getClass() + " is not"
                    + " of type " + Element.class);
            }
            add((Element) item);
        }
    }

    // ------------------------------------------------------- Internal Classes

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderHeadElements(org.apache.click.util.HtmlStringBuffer)}.
     */
    class HeadElements {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing miscellaneous head and CSS elements
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * cssImports.size()
                + 80 * cssStyles.size()
                + 80 * headElements.size());
            PageImports.this.renderHeadElements(buffer);
            return buffer.toString();
        }
    }

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderAllIncludes(org.apache.click.util.HtmlStringBuffer)}.
     *
     * @deprecated rather use {@link HeadElements} and {@link JsImports}
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
     * {@link #renderJsElements(org.apache.click.util.HtmlStringBuffer)}.
     */
    class JsElements {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing all JavaScript elements
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * jsImports.size() + 80 * jsScripts.size());

            PageImports.this.renderJsElements(buffer);
            return buffer.toString();
        }
    }

    /**
     * This class enables lazy, on demand importing for
     * {@link #renderCssElements(org.apache.click.util.HtmlStringBuffer)}.
     *
     * @deprecated use {@link HeadElements} instead
     */
    class CssElements {

        /**
         * @see java.lang.Object#toString()
         *
         * @return a string representing all CSS elements
         */
        public String toString() {
            processPageControls();
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                80 * cssImports.size() + 80 * cssStyles.size());

            PageImports.this.renderCssElements(buffer);
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
     * Convert the given HTML import line to a {@link CssStyle} instance.
     *
     * @param line the HTML import line to convert to a Css instance
     * @return a Css instance
     */
    private CssStyle asCssStyle(String line) {
        CssStyle cssStyle = new CssStyle();
        copyAttributes(cssStyle, line);
        cssStyle.append(extractCssContent(line));
        return cssStyle;
    }

    /**
     * Convert the given HTML import line to a {@link JavascriptImport} instance.
     *
     * @param line the HTML import line to convert to a JavaScriptImport instance
     * @return a JavascriptImport instance
     */
    private JsImport asJsImport(String line) {
        JsImport jsImport = new JsImport();
        copyAttributes(jsImport, line);
        return jsImport;
    }

    /**
     * Convert the given HTML import line to a {@link Javascript} instance.
     *
     * @param line the HTML import line to convert to a JavaScript instance
     * @return a Javascript instance
     */
    private JsScript asJsScript(String line) {
        JsScript jsScript = new JsScript();
        copyAttributes(jsScript, line);
        jsScript.append(extractJsContent(line, jsScript));
        return jsScript;
    }

    /**
     * Extract the CSS content from the given HTML import line.
     *
     * @param line the HTML import line
     * @return the CSS content contained in the HTML import line
     */
    private String extractCssContent(String line) {
        if (line.endsWith("/>")) {
            // If tag has no content, exit early
            return "";
        }

        // Find index where tag ends
        int start = line.indexOf('>');
        if (start == -1) {
            throw new IllegalArgumentException(line + " is not a valid element");
        }
        int end = line.lastIndexOf("</style>");
        if (end == -1) {
            return "";
        }
        return line.substring(start + 1, end);
    }

    /**
     * Extract the JavaScript content from the given HTML import line.
     *
     * @param line the HTML import line
     * @param jsScript the JsScript to replace the line with
     */
    private String extractJsContent(String line, JsScript jsScript) {
        if (line.endsWith("/>")) {
            // If tag has no content, exit early
            return "";
        }

        // Find index where tag ends
        int start = line.indexOf('>');
        if (start == -1) {
            throw new IllegalArgumentException(line + " is not a valid element");
        }
        int end = line.lastIndexOf("</script>");
        if (end == -1) {
            return "";
        }
        line = line.substring(start + 1, end).trim();

        // Strip addLoadEvent function
        int addLoadEventStart = line.indexOf("addLoadEvent");
        if (addLoadEventStart == 0) {
            start = line.indexOf("{", addLoadEventStart);
            line = line.substring(start + 1);
            end = line.lastIndexOf("});");
            line = line.substring(0, end);
            jsScript.setExecuteOnDomReady(true);
        }

        return line;
    }

    /**
     * Copy all available attributes from HTML import line to the Element
     * instance.
     *
     * @param element the HTML head element to copy the attributes to
     * @param line the HTML import line to copy attributes from
     */
    private void copyAttributes(Element element, String line) {
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
            element.setAttribute(key, StringUtils.strip(value, "'\""));
        }
    }

    /**
     * Ensure the given element and content will be unique.
     *
     * @deprecated use {@link org.apache.click.element.Element#setId(java.lang.String) ID}
     * instead
     *
     * @param element sets whether the HEAD element should be unique or not
     * @param content sets whether the HEAD element should be unique or not
     */
    private void setUnique(Element element, String content) {
        String id = element.getId();
        // If Element is unique and ID is not defined, derive the ID from the
        // content
        if (StringUtils.isBlank(id) && content.length() > 0) {
            int hash = Math.abs(content.hashCode());
            element.setId(Integer.toString(hash));
        }
    }
}
