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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.AbstractControl;
import net.sf.click.control.Container;
import net.sf.click.control.CssImport;
import net.sf.click.control.CssInclude;
import net.sf.click.control.JavascriptImport;
import net.sf.click.control.JavascriptInclude;
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

    // -------------------------------------------------------- Variables

    /** The page imports initialized flag. */
    protected boolean initialize = false;

    /** The page instance. */
    protected final Page page;

    /** The list of CSS imports. */
    private List cssImports = new ArrayList();

    /** The list of CSS includes. */
    private List cssIncludes = new ArrayList();

    /** The set of unique CSS imports and includes. */
    private Set cssUniqueSet = new HashSet();

    /** The global CSS include. */
    private CssInclude cssGlobalInclude;

    /** The global Javascript include. */
    private JavascriptInclude javascriptGlobalInclude;

    /** The set of unique Javascript imports and includes. */
    private Set javascriptUniqueSet = new HashSet();

    /** The list of Javascript imports. */
    private List javascriptImports = new ArrayList();

    /** The list of Javascript includes. */
    private List javascriptIncludes = new ArrayList();

    // -------------------------------------------------------- Public Constructors

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
     * @param logger where warnings are logged if keys are replaced
     */
    public void popuplateTemplateModel(Map model, LogService logger) {
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

        pop = model.put("jsImportsTop", new JsImportsTop());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
            + " model contains an object keyed with reserved "
            + "name \"jsImportsTop\". The page model object "
            + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }

        pop = model.put("jsImportsBottom", new JsImportsBottom());
        if (pop != null && !page.isStateful()) {
            String msg = page.getClass().getName() + " on " + page.getPath()
            + " model contains an object keyed with reserved "
            + "name \"jsImportsBottom\". The page model object "
            + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
        }
    }

    /**
     * Populate the specified request with html import keys.
     *
     * @param request the http request to populate
     * @param model the model to populate with html import keys
     * @param logger where warnings are logged if keys are replaced
     */
    public void popuplateRequest(HttpServletRequest request, Map model, LogService logger) {
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

        request.setAttribute("jsImportsTop", new JsImportsTop());
        if (model.containsKey("jsImportsTop")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"jsImportsTop\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

        request.setAttribute("jsImportsBottom", new JsImportsBottom());
        if (model.containsKey("jsImportsBottom")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"jsImportsBottom\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }
    }

    public void add(CssImport cssImport) {
        if (cssUniqueSet.contains(cssImport.getHref())) {
            // Already contain this import source
            return;
        }
        cssUniqueSet.add(cssImport.getHref());
        cssImports.add(cssImport);
    }

    public void add(CssInclude cssInclude) {
        if (cssInclude.isUnique()) {
            if (cssUniqueSet.contains(cssInclude.getInclude().toString())) {
                // Already contain this css include
                return;
            }
            cssUniqueSet.add(cssInclude.getInclude().toString());
        }
        cssIncludes.add(cssInclude);
    }

    public void add(JavascriptImport javaScriptImport) {
        if (javascriptUniqueSet.contains(javaScriptImport.getSource())) {
            // Already contain this import source
            return;
        }
        javascriptUniqueSet.add(javaScriptImport.getSource());
        javascriptImports.add(javaScriptImport);
    }

    public void add(JavascriptInclude javaScriptInclude) {
        if (javaScriptInclude.isUnique()) {
            if (javascriptUniqueSet.contains(javaScriptInclude.getInclude().toString())) {
                // Already contain this JavaScript include
                return;
            }
            javascriptUniqueSet.add(javaScriptInclude.getInclude().toString());
        }
        javascriptIncludes.add(javaScriptInclude);
    }

    public void appendToGlobalScript(String script) {
        getGlobalScript().append(script);
    }

    public JavascriptInclude getGlobalScript() {
        if (javascriptGlobalInclude == null) {
            javascriptGlobalInclude = new JavascriptInclude();
        }
        return javascriptGlobalInclude;
    }

    public void appendToGlobalStyle(String style) {
        getGlobalStyle().append(style);
    }

    public CssInclude getGlobalStyle() {
        if (cssGlobalInclude == null) {
            cssGlobalInclude = new CssInclude();
        }
        return cssGlobalInclude;
    }

    // -------------------------------------------------------- Protected Methods

    /**
     * Return a HTML string of all the page's HTML imports, including:
     * CSS imports, CSS includes, JS imports and JS includes.
     *
     * @return a HTML string of all the page's HTML imports, including:
     * CSS imports, CSS includes, JS imports and JS includes.
     */
    String getAllImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
              80 * javascriptImports.size()
            + 80 * javascriptIncludes.size()
            + 80 * cssImports.size()
            + 80 * cssIncludes.size());

        buffer.append(getCssImports());
        buffer.append(getJavascriptImports());

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML CSS
     * {@link #cssImports imports}, {@link #cssIncludes scripts} and
     * {@link #cssGlobalInclude}.
     *
     * @return only css imports and scripts
     */
    String getCssImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * cssImports.size() + 80 * cssIncludes.size());

        // First include all the imports e.g. <link href="...">
        for (Iterator it = cssImports.iterator(); it.hasNext();) {
            CssImport cssImport = (CssImport) it.next();
            buffer.append(cssImport.toString());
            buffer.append('\n');
        }

        // Then include all the styles e.g. <style>...</style>
        for (Iterator it = cssIncludes.iterator(); it.hasNext();) {
            CssInclude cssInclude = (CssInclude) it.next();
            buffer.append(cssInclude.toString());
            buffer.append('\n');
        }

        // Lastly include the global css include
        if (cssGlobalInclude != null) {
            buffer.append(cssGlobalInclude.toString());
        }

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML JS imports and scripts.
     * <p/>
     * This includes {@link #javascriptImports}, {@link #javascriptIncludes} and
     * {@link #javascriptGlobalInclude}.
     *
     * @return all javascript imports and scripts
     */
    String getJavascriptImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
              80 * javascriptImports.size() + 80 * javascriptIncludes.size());

        // First include all the imports e.g. <script src="...">
        for (Iterator it = javascriptImports.iterator(); it.hasNext();) {
            JavascriptImport javascriptImport = (JavascriptImport) it.next();
            buffer.append(javascriptImport.toString());
            buffer.append('\n');
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = javascriptIncludes.iterator(); it.hasNext();) {
            JavascriptInclude javascriptInclude = (JavascriptInclude) it.next();
            buffer.append(javascriptInclude.toString());
            buffer.append('\n');
        }

        // Lastly include global javascript
        if (javascriptGlobalInclude != null) {
            buffer.append(javascriptGlobalInclude.toString());
        }

        return buffer.toString();
    }

    /**
     * Return a string containing javascript imports and includes which should
     * be included at top of the Page. This includes {@link #javascriptImports},
     * {@link #javascriptIncludes} and {@link #javascriptGlobalInclude}.
     *
     * @return all top javascript imports and scripts
     */
    String getJavascriptImportsTop() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * javascriptImports.size() + 80 * javascriptIncludes.size());

        // First include all the imports e.g. <script src="...">
        for (Iterator it = javascriptImports.iterator(); it.hasNext();) {
            JavascriptImport javascriptImport = (JavascriptImport) it.next();
            if (javascriptImport.getPosition() == JavascriptImport.HEAD) {
                buffer.append(javascriptImport.toString());
                buffer.append('\n');
            }
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = javascriptIncludes.iterator(); it.hasNext();) {
            JavascriptImport javascriptInclude = (JavascriptImport) it.next();
            if (javascriptInclude.getPosition() == JavascriptImport.HEAD) {
                buffer.append(javascriptInclude.toString());
                buffer.append('\n');
            }
        }

        // Lastly include global javascript if targeted for top of Page
        if (javascriptGlobalInclude != null) {
            if (javascriptGlobalInclude.getPosition() == JavascriptInclude.HEAD) {
                buffer.append(javascriptGlobalInclude.toString());
            }
        }

        return buffer.toString();
    }

    /**
     * Return a string containing javascript imports and includes which should
     * be included at bottom of the Page. This includes {@link #javascriptImports},
     * {@link #javascriptIncludes} and {@link #javascriptGlobalInclude}.
     *
     * @return all top javascript imports and scripts
     */
    String getJavascriptImportsBottom() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * javascriptImports.size() + 80 * javascriptIncludes.size());

        // First include all the bottom imports e.g. <script src="...">
        for (Iterator it = javascriptImports.iterator(); it.hasNext();) {
            JavascriptImport javascriptImport = (JavascriptImport) it.next();
            if (javascriptImport.getPosition() == JavascriptImport.BODY) {
                buffer.append(javascriptImport.toString());
                buffer.append('\n');
            }
        }

        
        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = javascriptIncludes.iterator(); it.hasNext();) {
            JavascriptImport javascriptInclude = (JavascriptImport) it.next();
            if (javascriptInclude.getPosition() == JavascriptImport.BODY) {
                buffer.append(javascriptInclude.toString());
                buffer.append('\n');
            }
        }

        // Lastly include global javascript if targeted for top of Page
        if (javascriptGlobalInclude != null) {
            if (javascriptGlobalInclude.getPosition() == JavascriptInclude.BODY) {
                buffer.append(javascriptGlobalInclude.toString());
            }
        }

        return buffer.toString();
    }

    /**
     * Process the Page's set of control HTML head imports.
     */
    void processPageControls() {
        if (initialize) {
            return;
        }

        initialize = true;

        // Allow Page and its Controls to contribute header imports
        page.onHtmlImports(this);

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
    void processControl(Control control) {
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
    void processLine(String value) {
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
                CssInclude cssInclude = asCssInclude(lines[i]);
                cssInclude.setUnique(true);
                add(cssInclude);

            } else if (line.startsWith("<script")) {
                if (line.indexOf(" src=") != -1) {
                    JavascriptImport javascriptImport = asJavascriptImport(lines[i]);
                    add(javascriptImport);

                } else {
                    JavascriptInclude javascriptInclude = asJavascriptInclude(lines[i]);
                    javascriptInclude.setUnique(true);
                    add(javascriptInclude);

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
    void addToList(String item, List list) {
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
            return PageImports.this.getAllImports();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getJavascriptImports()}.
     */
    class JsImports {
        public String toString() {
            return PageImports.this.getJavascriptImports();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getJavascriptImportsTop()}.
     */
    class JsImportsTop {
        public String toString() {
            return PageImports.this.getJavascriptImportsTop();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getJavascriptImportsBottom()}.
     */
    class JsImportsBottom {
        public String toString() {
            return PageImports.this.getJavascriptImportsBottom();
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

    // -------------------------------------------------------- Private Methods

    private CssImport asCssImport(String line) {
        CssImport cssImport = new CssImport();
        setAttributes(cssImport, line);
        return cssImport;
    }

    private CssInclude asCssInclude(String line) {
        CssInclude cssInclude = new CssInclude();
        setAttributes(cssInclude, line);
        cssInclude.append(extractContent(line));
        return cssInclude;
    }

    private JavascriptImport asJavascriptImport(String line) {
        JavascriptImport javascriptInclude = new JavascriptImport();
        setAttributes(javascriptInclude, line);
        return javascriptInclude;
    }

    private JavascriptInclude asJavascriptInclude(String line) {
        JavascriptInclude javascriptInclude = new JavascriptInclude();
        setAttributes(javascriptInclude, line);
        javascriptInclude.append(extractContent(line));
        return javascriptInclude;
    }

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
     * input -> style="moo:ok;param:value" hello="ok"
     * @param line
     * @return
     */
    private void setAttributes(AbstractControl control, String line) {
        // Find index where attributes start -> first space char
        int start = line.indexOf(' ');
        if (start == -1) {
            // If no attributes found, exit early
            return;
        }

        // Find index where attributes end -> closing tag
        int end = line.indexOf("/>");
        if (end == -1) {
            end = line.indexOf(">");
        }
        if (end == -1) {
            throw new IllegalArgumentException(line + " is not a valid css import");
        }

        line = line.substring(start, end);
        StringTokenizer tokens = new StringTokenizer(line, " ");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            StringTokenizer attribute = new StringTokenizer(token, "=");
            String key = attribute.nextToken();
            String value = attribute.nextToken();
            control.setAttribute(key, StringUtils.strip(value, "'\""));
        }
    }
}
