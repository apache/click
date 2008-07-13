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
package net.sf.click.util;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.Container;
import net.sf.click.control.Table;

import net.sf.click.service.LogService;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a utility object for rendering a Page's HTML header imports and its
 * control HTML header imports.
 * <p/>
 * A <tt>PageImports</tt> instance is automatically added to the Velocity Context
 * for Velocity templates, or as a request attribute for JSP pages using the key
 * name "<span class="blue">imports</span>".
 * <p/>
 * To use the <tt>PageImports</tt> object simply reference it your page header
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
 * Please also see {@link Page#getHtmlImports()} and
 * {@link Control#getHtmlImports()}.
 *
 * @see Format
 *
 * @author Malcolm Edgar
 */
public class PageImports {

    /** The page imports initialized flag. */
    protected boolean initialize = false;

    /** The list of CSS import lines. */
    protected List cssImports = new ArrayList();

    /** The list of JS import lines. */
    protected List jsImports = new ArrayList();

    /** The list of JS script block lines. */
    protected List jsScripts = new ArrayList();

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
     * Return a HTML string of all the page's HTML imports, including:
     * CSS imports, JS imports and JS script blocks.
     *
     * @return a HTML string of all the page's HTML imports, including:
     * CSS imports, JS imports and JS script blocks.
     */
    protected String getAllIncludes() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(80 * cssImports.size()
            + jsImports.size() + jsScripts.size());

        for (int i = 0; i  < cssImports.size(); i++) {
            String line = cssImports.get(i).toString();
            buffer.append(line);
            buffer.append('\n');
        }
        for (int i = 0; i  < jsImports.size(); i++) {
            String line = jsImports.get(i).toString();
            buffer.append(line);
            buffer.append('\n');
        }
        for (int i = 0; i  < jsScripts.size(); i++) {
            String line = jsScripts.get(i).toString();
            buffer.append(line);
            buffer.append('\n');
        }

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML CSS imports.
     *
     * @return a HTML string of all the page's HTML CSS imports.
     */
    protected String getCssImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(80 * cssImports.size());

        for (int i = 0; i  < cssImports.size(); i++) {
            String line = cssImports.get(i).toString();
            buffer.append(line);
            if (i < cssImports.size() - 1) {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML JS imports and scripts.
     *
     * @return a HTML string of all the page's HTML JS imports and scripts.
     */
    protected String getJsImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(80 * jsImports.size());

        for (int i = 0; i  < jsImports.size(); i++) {
            String line = jsImports.get(i).toString();
            buffer.append(line);
            if (i < jsImports.size() - 1 || !jsScripts.isEmpty()) {
                buffer.append('\n');
            }
        }

        for (int i = 0; i  < jsScripts.size(); i++) {
            String line = jsScripts.get(i).toString();
            buffer.append(line);
            if (i < jsScripts.size() - 1) {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    /**
     * Process the Page's set of control HTML head imports.
     */
    protected void processPageControls() {
        if (initialize) {
            return;
        }

        initialize = true;

        if (page.hasControls()) {
            for (int i = 0; i < page.getControls().size(); i++) {
                Control control = (Control) page.getControls().get(i);

                processControl(control);
            }
        }

        processLine(page.getHtmlImports());
    }

    /**
     * Process the given control HTML imports.
     *
     * @param control the control to process
     */
    protected void processControl(Control control) {
        processLine(control.getHtmlImports());

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
     * Process the given control HTML import line.
     *
     * @param value the HTML import line to process
     */
    protected void processLine(String value) {
        if (value == null || value.length() == 0) {
            return;
        }

        String[] lines = StringUtils.split(value, '\n');

        for (int i = 0; i  < lines.length; i++) {
            String line = lines[i].trim().toLowerCase();
            if (line.startsWith("<link") && line.indexOf("text/css") != -1) {
                addToList(lines[i], cssImports);

            } else if (line.startsWith("<style") && line.indexOf("text/css") != -1) {
                addToList(lines[i], cssImports);

            } else if (line.startsWith("<script")) {
                if (line.indexOf(" src=") != -1) {
                    addToList(lines[i], jsImports);

                } else {
                    addToList(lines[i], jsScripts);

                }
            } else {
                throw new IllegalArgumentException("Unknown include type: " + lines[i]);
            }
        }
    }

    /**
     * Add the given string item to the list if it is not already present.
     *
     * @param item the line item to add
     * @param list the list to add the item to
     */
    protected void addToList(String item, List list) {
        item = item.trim();

        boolean found = false;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(item)) {
                found = true;
                break;
            }
        }

        if (!found) {
            list.add(item);
        }
    }

    // -------------------------------------------------------- Internal Classes

    /**
     * This class enables lazy, on demand importing for {@link #getAllIncludes()}.
     */
    class Imports {
        public String toString() {
            return PageImports.this.getAllIncludes();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getJsImports()}.
     */
    class JsImports {
        public String toString() {
            return PageImports.this.getJsImports();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getCssImports()}.
     */
    class CssImports {
        public String toString() {
            return PageImports.this.getCssImports();
        }
    }
}
