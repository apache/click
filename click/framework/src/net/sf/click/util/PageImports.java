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
import java.util.Iterator;
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

    /** The buffer of CSS import lines. */
    protected HtmlStringBuffer cssBuffer = new HtmlStringBuffer();

    /** The list of JS import lines. */
    protected List jsImports = new ArrayList();

    /** 
     * TODO jsScript is probably redundant.
     * The list of JS script block lines.
     */
    protected List jsScripts = new ArrayList();

    /** The buffer of JS script block lines. */
    protected HtmlStringBuffer jsBuffer = new HtmlStringBuffer();

    /** The list of JS import lines to be included at bottom of html page. */
    protected List jsImportsBottom = new ArrayList();

    /** The buffer of JS script block lines to be included at bottom of html page.*/
    protected HtmlStringBuffer jsBottomBuffer = new HtmlStringBuffer();

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

    /**
     * Add the specified css script to the <em>css</em> imports.
     * <p/>
     * <b>Please note</b> Click will automatically enclose the scripts inside a
     * &lt;style&gt; element, so no need to do this yourself.
     * <p/>
     * The css will be made available in your template under the key
     * <em>cssImports</em>.
     *
     * @param script the css to add
     */
    public void addCssScript(String script) {
        cssBuffer.append(script);
        cssBuffer.append('\n');
    }

    /**
     * Add the specified css import to the <em>cssImports</em> imports.
     * <p/>
     * The css import will be made available in your template under the key
     * <em>cssImports</em>.
     *
     * @param cssImport the css import to add
     */
    public void addCssImport(String cssImport) {
        processLine(cssImport, cssImports);
    }

    /**
     * Add the specified javascript import to the <em>jsImports</em> list.
     * <p/>
     * The javascript imports will be made available in your template under the
     * keys <em>jsImports</em> and <em>jsImportsTop</em>.
     *
     * @param jsImport the javascript import to add
     */
    public void addJsImport(String jsImport) {
        processLine(jsImport, jsImports);
    }

    /**
     * Add the specified javascript script to the <em>jsScripts</em> list.
     * <p/>
     * <b>Please note</b> Click will automatically enclose the scripts inside a
     * &lt;srcipt&gt; element, so no need to do this yourself.
     * <p/>
     * The javascript will be made available in your template under the
     * keys <em>jsImports</em> and <em>jsImportsTop</em>.
     *
     * @param script the javascript script to add
     */
    public void addJsScript(String script) {
        jsBuffer.append(script);
        jsBuffer.append('\n');
    }

    /**
     * Add the specified javascript import to the <em>jsImportsBottom</em> list.
     * <p/>
     * The javascript import will be made available in your template under the
     * keys <em>jsImportsBottom</em> and <em>jsImports</em>.
     *
     * @param jsImport the javascript import to add
     */
    public void addJsImportAtBottom(String jsImport) {
        processLine(jsImport, jsImportsBottom);
    }

    /**
     * Add the specified javascript to the <em>jsBottom</em> buffer.
     * <p/>
     * <b>Please note</b> Click will automatically enclose the scripts inside a
     * &lt;srcipt&gt; element, so no need to do this yourself.
     * <p/>
     * The javascript will be made available in your template under the
     * keys <em>jsImportsBottom</em> and <em>jsImports</em>.
     *
     * @param script the javascript script to add
     */
    public void addJsScriptAtBottom(String script) {
        jsBottomBuffer.append(script);
        jsBottomBuffer.append('\n');
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

        HtmlStringBuffer buffer = new HtmlStringBuffer(
              80 * jsImports.size()
            + 80 * jsImportsBottom.size()
            + 80 * jsScripts.size()
            + jsBuffer.length()
            + jsBottomBuffer.length()
            + 80 * cssImports.size()
            + cssBuffer.length());

        buffer.append(getCssImports());
        buffer.append(getJsImports());

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML CSS
     * {@link #cssImports imports} and {@link #cssBuffer scripts}.
     *
     * @return only css imports and scripts
     */
    protected String getCssImports() {
        processPageControls();

        // If cssImports is empty, return only cssScripts.
        if (cssImports.size() == 0) {
            return cssBuffer.toString();
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * cssImports.size() + cssBuffer.length());

        for (Iterator it = cssImports.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Lastly include the css buffers e.g. styles not contained
        // inside a <style> element
        wrapInStyle(buffer, cssBuffer.toString());

        return buffer.toString();
    }

    /**
     * Return a HTML string of all the page's HTML JS imports and scripts.
     * <p/>
     * This includes {@link #jsImports}, {@link #jsImportsBottom},
     * {@link #jsScripts}, {@link #jsBuffer} and {@link #jsBottomBuffer}.
     *
     * @return all javascript imports and scripts
     */
    protected String getJsImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
              80 * jsImports.size()
            + 80 * jsImportsBottom.size()
            + 80 * jsScripts.size()
            + 80 * jsBuffer.length()
            + 80 * jsBottomBuffer.length());

        // First include all the imports e.g. <script src="...">
        for (Iterator it = jsImports.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        for (Iterator it = jsImportsBottom.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = jsScripts.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Lastly include all javascript buffers e.g. scripts not contained
        // inside a <script> element
        String[] scripts = new String[] {jsBuffer.toString(),
            jsBottomBuffer.toString()};
        wrapInScript(buffer, scripts);

        return buffer.toString();
    }

    /**
     * Return a string containing {@link #jsImports}, {@link #jsScripts} and
     * {@link #jsBuffer}.
     *
     * @return all top javascript imports and scripts
     */
    protected String getJsImportsTop() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * jsImports.size() + 80 * jsScripts.size() + jsBuffer.length());

        // First include all the imports e.g. <script src="...">
        for (Iterator it = jsImports.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Then include all the scripts e.g. <script>...</script>
        for (Iterator it = jsScripts.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Lastly include top javascript buffers e.g. scripts not contained
        // inside a <script> element
        wrapInScript(buffer, jsBuffer.toString());

        return buffer.toString();
    }

    /**
     * Return a string containing {@link #jsImportsBottom} and {@link #jsBottomBuffer}.
     *
     * @return all bottom javascript imports and scripts
     */
    protected String getJsImportsBottom() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer(
            80 * jsImportsBottom.size() + jsBottomBuffer.length());

        // First include all the bottom imports e.g. <script src="...">
        for (Iterator it = jsImportsBottom.iterator(); it.hasNext();) {
            String line = it.next().toString();
            buffer.append(line);
            buffer.append('\n');
        }

        // Then include bottom javascript buffers e.g. scripts not contained
        // inside a <script> element
        wrapInScript(buffer, jsBottomBuffer.toString());

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

    /**
     * Add the new value to the specified list of values.
     *
     * @param value new value to add
     * @param list the list to add the new value to
     */
    protected void processLine(String value, List list) {
        if (value == null || value.length() == 0) {
            return;
        }

        String[] lines = StringUtils.split(value, '\n');

        for (int i = 0; i  < lines.length; i++) {
            addToList(lines[i], list);
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
     * This class enables lazy, on demand importing for {@link #getJsImportsTop()}.
     */
    class JsImportsTop {
        public String toString() {
            return PageImports.this.getJsImportsTop();
        }
    }

    /**
     * This class enables lazy, on demand importing for {@link #getJsImportsBottom()}.
     */
    class JsImportsBottom {
        public String toString() {
            return PageImports.this.getJsImportsBottom();
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

    /**
     * Wrap the specified text in a script element and append it to the buffer.
     *
     * @param buffer buffer to append wrapped script to
     * @param text the text to wrap in a script element
     */
    private void wrapInScript(HtmlStringBuffer buffer, String text) {
        wrapInScript(buffer, new String[] {text});
    }

    /**
     * Wrap the specified scripts in a script element and append it to the buffer.
     *
     * @param buffer buffer to append wrapped script to
     * @param scripts the scripts to wrap in a script element
     */
    private void wrapInScript(HtmlStringBuffer buffer, String[] scripts) {
        buffer.elementStart("script");
        // TODO should users be able to set <script> attributes e.g. charset?
        buffer.appendAttribute("type", "text/javascript");
        buffer.closeTag();
        for (int i = 0; i < scripts.length; i++) {
            buffer.append(scripts[i]);
        }
        buffer.elementEnd("script");
    }

    /**
     * Wrap the specified text in a style element and append it to the buffer.
     *
     * @param buffer buffer to append wrapped style to
     * @param text the text to wrap in a style element
     */
    private void wrapInStyle(HtmlStringBuffer buffer, String text) {
        buffer.elementStart("style");
        // TODO should users be able to set <style> attributes e.g. media?
        buffer.appendAttribute("type", "text/css");
        buffer.closeTag();
        buffer.append(text);
        buffer.elementEnd("style");
    }
}
