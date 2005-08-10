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
package net.sf.click.extras.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a HTML Table control: &lt;table&gt;.
 * <p/>
 * <b>PLEASE NOTE</b>: the Table control is undergoing preliminary development
 * and is subject to significant change
 *
 * @see Column
 * @see Decorator
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Table implements Control {

    // -------------------------------------------------------------- Constants

    /**
     * The click table properties bundle name: &nbsp; <tt>click-table</tt>
     */
    protected static final String TABLE_PROPERTIES = "click-table";

    protected static final String TABLE_IMPORTS =
        "<link rel='stylesheet' type='text/css' href='$/click/table.css' title='style'>\n";

    // ----------------------------------------------------- Instance Variables

    /** The Table attributes Map. */
    protected Map attributes = new HashMap();

    /** The map of Table Columns keyed by column name. */
    protected Map columns = new HashMap();

    /** The list of Table Columns. */
    protected List columnList = new ArrayList();

    /** The request context. */
    protected Context context;

    /** The control name. */
    protected String name;

    /** The list Table rows. */
    protected List rowList;

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
        return (String) getAttributes().get(name);
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

        if (value != null) {
            getAttributes().put(name, value);
        } else {
            getAttributes().remove(name);
        }
    }

    /**
     * Return the Table attributes Map.
     *
     * @return the table attributes Map.
     */
    public Map getAttributes() {
        return attributes;
    }

    /**
     * Return true if the Table has attributes or false otherwise.
     *
     * @return true if the Table has attributes on false otherwise
     */
    public boolean hasAttributes() {
        return !getAttributes().isEmpty();
    }

    /**
     * Add the column to the table. The column will be added to the
     * {@link #columns} Map using its name.
     *
     * @param column the column to add to the table
     * @throws IllegalArgumentException if the table already contains a column
     * with the same name
     */
    public void addColumn(Column column) {
        if (column == null) {
            throw new IllegalArgumentException("column parameter cannot be null");
        }
        if (getColumns().containsKey(column.getName())) {
            throw new IllegalArgumentException
                ("Table already contains column named: " + column.getName());
        }

        getColumns().put(column.getName(), column);
        getColumnList().add(column);
    }

    /**
     * Remove the given Column from the table.
     *
     * @param column the column to remove from the table
     */
    public void removeColumn(Column column) {
        if (column != null && getColumns().containsKey(column.getName())) {
            getColumns().remove(column.getName());
            getColumnList().remove(column);
        }
    }

    /**
     * Remove the named colum from the Table.
     *
     * @param name the name of the column to remove from the table
     */
    public void removeColumn(String name) {
        Column column = (Column) getColumns().get(name);
        removeColumn(column);
    }

    /**
     * Remove the list of named columns from the table.
     *
     * @param columnNames the list of column names to remove from the table
     */
    public void removeColumns(List columnNames) {
        if (columnNames != null) {
            for (int i = 0; i < columnNames.size(); i++) {
                removeColumn(columnNames.get(i).toString());
            }
        }
    }

    public List getColumnList() {
        return columnList;
    }

    /**
     * Return the Map of table Columns, keyed on column name.
     *
     * @return the Map of table Columns, keyed on column name
     */
    public Map getColumns() {
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
     * Return the HTML head import statements for the CSS stylesheet
     * (<tt>click/control.css</tt>) and JavaScript (<tt>click/control.js</tt>)
     * files.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(TABLE_IMPORTS, "$", path);
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
     * Return the table property for the named resource key and the context's
     * request locale.
     *
     * @param name resource name of the property
     * @return the named localized property for the table
     */
    public String getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        Locale locale = getContext().getRequest().getLocale();

        ResourceBundle bundle =
            ResourceBundle.getBundle(TABLE_PROPERTIES, locale);

        return bundle.getString(name);
    }

    /**
     * Return the list of table rows.
     *
     * @return the list of table rows
     */
    public List getRowList() {
        if (rowList == null) {
            rowList = new ArrayList();
        }
        return rowList;
    }

    /**
     * Return the list of table rows.
     *
     * @return the list of table rows
     */
    public void setRowList(List rowList) {
        this.rowList = rowList;
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
        buffer.append("<thead>\n<tr>\n");

        final List tableColumns = getColumnList();
        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            column.renderTableHeader(buffer, context);
            if (j < tableColumns.size() -1) {
                buffer.append("\n");
            }
        }

        buffer.append("</tr></thead>\n");

        // Render table rows.
        buffer.append("<tbody>\n");

        final List tableRows = getRowList();
        for (int i = 0; i < tableRows.size(); i++) {
            Object row = tableRows.get(i);

            if ((i+1) % 2 == 0) {
                buffer.append("<tr class='even'>\n");
            } else {
                buffer.append("<tr class='odd'>\n");
            }

            for (int j = 0; j < tableColumns.size(); j++) {
                Column column = (Column) tableColumns.get(j);
                column.renderTableData(row, buffer, context);
                if (j < tableColumns.size() -1) {
                    buffer.append("\n");
                }
            }

            buffer.append("</tr>");
            if (i < tableRows.size() -1) {
                buffer.append("\n");
            }
        }

        // Render table end.
        buffer.append("</tbody></table>\n");

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
