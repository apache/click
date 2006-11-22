/*
 * Copyright 2004-2005 Malcolm A. Edgar
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Please also see {@link Control#getHtmlImports()}.
 *
 * @see Format
 *
 * @author Malcolm Edgar
 */
public class PageImports {

    /** The cached page imports value. */
    protected String cachedPageImports;

    /** The set of included imports. */
    protected Set includeSet;

    /** The page instance. */
    protected final Page page;

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
     * Return the Page's set of control HTML head imports.
     *
     * @see Object#toString()
     *
     * @return the Page's set of control HTML head imports
     */
    public String toString() {
        if (cachedPageImports != null) {
            return cachedPageImports;
        }

        if (!page.hasControls()) {
            return "";
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(80);
        includeSet = null;

        for (int i = 0; i < page.getControls().size(); i++) {
            Control control = (Control) page.getControls().get(i);

            processControl(control, buffer);
        }

        cachedPageImports = buffer.toString();

        return cachedPageImports;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Process the given control adding its imports to the buffer and processing
     * any child controls it may contain.
     *
     * @param control the control to add
     * @param buffer the import lines buffer
     */
    protected void processControl(Control control, HtmlStringBuffer buffer) {
        addImport(control.getHtmlImports(), buffer);

        if (control instanceof Form) {
            Form form = (Form) control;
            List controls = form.getFieldList();
            for (int i = 0, size = controls.size(); i < size; i++) {
                processControl((Control) controls.get(i), buffer);
            }

        } else if (control instanceof FieldSet) {
            FieldSet fieldSet = (FieldSet) control;
            List controls = fieldSet.getFieldList();
            for (int i = 0, size = controls.size(); i < size; i++) {
                processControl((Control) controls.get(i), buffer);
            }

        } else if (control instanceof Panel) {
            Panel panel = (Panel) control;
            if (panel.hasControls()) {
                List controls = panel.getControls();
                for (int i = 0, size = controls.size(); i < size; i++) {
                    processControl((Control) controls.get(i), buffer);
                }
            }

        } else if (control instanceof Table) {
            Table table = (Table) control;
            if (table.hasControls()) {
                List controls = table.getControls();
                for (int i = 0, size = controls.size(); i < size; i++) {
                    processControl((Control) controls.get(i), buffer);
                }
            }
        }
    }

    /**
     * Add the HTML imports value to the string buffer, ensuring the same import
     * line is not repeatedly added.
     *
     * @param value the control HTML header imports value
     * @param buffer the string buffer to append the import lines to
     */
    protected void addImport(String value, HtmlStringBuffer buffer) {
        if (value == null || value.length() == 0) {
            return;
        }

        String[] lines = StringUtils.split(value, '\n');

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (!getIncludeSet().contains(line)) {
                buffer.append(line);
                buffer.append('\n');
                getIncludeSet().add(line);
            }
        }
    }

    /**
     * Return the set of included HTML control imports. This set will not be
     * filled until the {@link #toString()} method has been invoked.
     *
     * @return the set of included HTML control imports
     */
    protected Set getIncludeSet() {
        if (includeSet == null) {
            includeSet = new HashSet();
        }
        return includeSet;
    }
}
