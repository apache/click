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
package net.sf.click.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a HTML Table control: &lt;table&gt;
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Table implements Control {

    // ----------------------------------------------------- Instance Variables

    /** The Table attributes Map. */
    protected Map attributes;

    /** The list of Table Columns. */
    protected List columns;

    /** The request context. */
    protected Context context;

    /** The control name. */
    protected String name;

    /** The Table rows. */
    protected List rows;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an action link for the given name.
     *
     * @param name the table name
     * @throws IllegalArgumentException if the name is null
     */
    public Table(String name) {
        setName(name);
    }
    // ------------------------------------------------------ Public Attributes

    /**
     * Return the Table HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of table HTML attribute
     * @return the Table HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the Tables with the given HTML attribute name and value. These
     * attributes will be rendered as HTML attributes, for example:
     *
     * <pre class="codeJava">
     * Table table = new Table("Username");
     * table.setAttribute("<span class="blue">class</span>", "<span class="red">login</span>"); </pre>
     *
     * HTML output:
     * <pre class="codeHtml">
     * &lt;input type='text' name='username' value='' <span class="blue">class</span>='<span class="red">login</span>'/&gt; </pre>
     *
     * If there is an existing named attribute in the Table it will be replaced
     * with the new value. If the given attribute value is null, any existing
     * attribute will be removed.
     *
     * @param name the name of the table HTML attribute
     * @param value the value of the table HTML attribute
     * @throws IllegalArgumentException if attribute name is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (attributes == null) {
            attributes = new HashMap(5);
        }

        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }

    /**
     * Return the Table attributes Map.
     *
     * @return the table attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the Table has attributes or false otherwise.
     *
     * @return true if the Table has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public void addColumn(Column column) {
        if (column == null) {
            throw new IllegalArgumentException("Null column parameter");
        }
        getColumns().add(column);
    }

    public List getColumns() {
        if (columns == null) {
            columns = new ArrayList();
        }
        return columns;
    }

    /**
     * @see Control#getContext()
     */
    public Context getContext() {
        return null;
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * @see Control#setListener(Object, String)
     */
    public void setListener(Object listener, String method) {
        // Does nothing
    }

    /**
     * @see net.sf.click.Control#getId()
     */
    public String getId() {
        return getName() + "-table";
    }

    /**
     * @see Control#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see Control#setName(String)
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
    }

    /**
     * Return the list of table rows.
     *
     * @return the list of table rows
     */
    public List getRows() {
        if (rows == null) {
            rows = new ArrayList();
        }
        return rows;
    }

    /**
     * Return the list of table rows.
     *
     * @return the list of table rows
     */
    public void setRows(List rows) {
        this.rows = rows;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * @see Control#onProcess()
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * Return a HTML rendered Table string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        // Render table start.
        buffer.append("<table id='");
        buffer.append(getId());
        buffer.append("'");
        renderAttributes(buffer);
        buffer.append(">\n");

        // Render table header row.
        buffer.append(" <tr>\n  ");

        final List tableColumns = getColumns();
        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            column.renderTableHeader(buffer);
        }

        buffer.append("\n </tr>\n");

        // Render table rows.
        final List tableRows = getRows();
        for (int i = 0; i < tableRows.size(); i++) {
            Object row = tableRows.get(i);
            buffer.append(" <tr>\n  ");

            for (int j = 0; j < tableColumns.size(); j++) {
                Column column = (Column) tableColumns.get(j);
                column.renderTableData(row, buffer);
            }

            buffer.append("\n </tr>\n");
        }

        // Render table end.
        buffer.append("</table>\n");

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the table HTML attributes to the string buffer, except for
     * the attribute "id".
     *
     * @param buffer the StringBuffer to render the HTML attributes to
     */
    protected void renderAttributes(StringBuffer buffer) {
        if (hasAttributes()) {
            ClickUtils.renderAttributes(getAttributes(), buffer);
        }
    }

}
