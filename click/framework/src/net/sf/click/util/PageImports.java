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

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Panel;
import net.sf.click.control.Table;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a utility object for rendering a Page's control HTML header imports.
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
 * You should also follow the performance best practice by importing CSS includes
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
 * Please also see {@link Control#getHtmlImports()}.
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
     * Return a HTML string of all the page's HTML imports, including:
     * CSS imports, JS imports and JS script blocks.
     *
     * @return a HTML string of all the page's HTML imports, including:
     * CSS imports, JS imports and JS script blocks.
     */
    public String getAllIncludes() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer();

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
    public String getCssImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer();

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
    public String getJsImports() {
        processPageControls();

        HtmlStringBuffer buffer = new HtmlStringBuffer();

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

    // ------------------------------------------------------ Protected Methods

    /**
     * Process the Page's set of control HTML head imports.
     */
    protected void processPageControls() {
        if (initialize) {
            return;
        }

        initialize = true;

        if (!page.hasControls()) {
            return;
        }

        for (int i = 0; i < page.getControls().size(); i++) {
            Control control = (Control) page.getControls().get(i);

            processControl(control);
        }
    }

    /**
     * Process the given control HTML imports.
     *
     * @param control the control to process
     */
    protected void processControl(Control control) {
        processLine(control.getHtmlImports());

        if (control instanceof Form) {
            Form form = (Form) control;
            List controls = form.getFieldList();
            for (int i = 0, size = controls.size(); i < size; i++) {
                processControl((Control) controls.get(i));
            }

        } else if (control instanceof FieldSet) {
            FieldSet fieldSet = (FieldSet) control;
            List controls = fieldSet.getFieldList();
            for (int i = 0, size = controls.size(); i < size; i++) {
                processControl((Control) controls.get(i));
            }

        } else if (control instanceof Panel) {
            Panel panel = (Panel) control;
            if (panel.hasControls()) {
                List controls = panel.getControls();
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
}
