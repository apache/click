/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;

/**
 * Provides the Column table data &lt;td&gt; and table header &lt;th&gt;
 * renderer.
 *
 * <table id="table" class="simple">
 * <thead>
 * <tr>
 * <th>Id</th>
 * <th>Name</th>
 * <th>Category</th>
 * <th>Action</th></tr></thead>
 * <tbody>
 * <tr class="odd">
 * <td>834501</td>
 * <td>Alison Smart</td>
 * <td>Residential Property</td>
 * <td><a href="#">View</a></td></tr>
 * <tr class="even">
 * <td>238454</td>
 * <td>Angus Robins</td>
 * <td>Bonds</td>
 * <td><a href="#">View</a></td></tr>
 * <tr class="odd">
 * <td>784191</td>
 * <td>Ann Melan</td>
 * <td>Residential Property</td>
 * <td><a href="#">View</a></td></tr></tbody></table>
 *
 * <p/>
 *
 * The Column object provide column definitions for the {@link Table} object.
 *
 * <h3>Rendering Options</h3>
 *
 * The Column class supports a number of rendering options which include:
 *
 * <ul>
 * <li>{@link #dataClass} - the CSS class for the table data cell</li>
 * <li>{@link #dataStyle} - the CSS style for the table data cell</li>
 * <li>{@link #headerClass} - the CSS class for the table header cell</li>
 * <li>{@link #headerStyle} - the CSS style for the table header cell</li>
 * <li>{@link #headerTitle} - the table header cell value to render</li>
 * <li>{@link #format} - the <tt>MessageFormat</tt> pattern rendering
 *      the column value</li>
 * <li>{@link #attributes} - the CSS style attributes for the table data cell</li>
 * <li>{@link #autolink} - the option to automatically render href links
 *      for email and URL column values</li>
 * <li>{@link #decorator} - the custom column value renderer</li>
 * </ul>
 *
 * <h4>Format Pattern</h4>
 *
 * The {@link #format} property which specifies {@link MessageFormat} pattern
 * a is very useful for formatting column values. For example to render
 * formatted number and date values you simply specify:
 *
 * <pre class="codeJava">
 * Table table = <span class="kw">new</span> Table(<span class="st">"table"</span>);
 * table.setStyle(<span class="st">"simple"</span>);
 *
 * Column idColumn = <span class="kw">new</span> Column(<span class="st">"purchaseId"</span>, <span class="st">"ID"</span>);
 * idColumn.setFormat(<span class="st">"{0,number,#,###}"</span>);
 * table.addColumn(idColumn);
 *
 * Column priceColumn = <span class="kw">new</span> Column(<span class="st">"purchasePrice"</span>, <span class="st">"Price"</span>);
 * priceColumn.setFormat(<span class="st">"{0,number,currency}"</span>);
 * priceColumn.setAttribute(<span class="st">"style"</span>, <span class="st">"{text-align:right;}"</span>);
 * table.addColumn(priceColumn);
 *
 * Column dateColumn = <span class="kw">new</span> Column(<span class="st">"purchaseDate"</span>, <span class="st">"Date"</span>);
 * dateColumn.setFormat(<span class="st">"{0,date,dd MMM yyyy}"</span>);
 * table.addColumn(dateColumn); </pre>
 *
 * Column orderIdColumn = <span class="kw">new</span> Column(<span class="st">"order.id"</span>, <span class="st">"Order ID"</span>);
 * table.addColumn(orderIdColumn);
 *
 * <h4>Column Decorators</h4>
 *
 * The support custom column value rendering you can specify a {@link Decorator}
 * class on columns. The decorator <tt>render</tt> method is passed the table
 * row object and the page request Context. Using the table row you can access
 * all the column values enabling you to render a compound value composed of
 * multiple column values. For example:
 *
 * <pre class="codeJava">
 * Column column = <span class="kw">new</span> Column(<span class="st">"email"</span>);
 *
 * column.setDecorator(<span class="kw">new</span> Decorator() {
 *     <span class="kw">public</span> String render(Object row, Context context) {
 *         Person person = (Person) row;
 *         String email = person.getEmail();
 *         String fullName = person.getFullName();
 *         <span class="kw">return</span> <span class="st">"&lt;a href='mailto:"</span> + email + <span class="st">"'&gt;"</span> + fullName + <span class="st">"&lt;/a&gt;"</span>;
 *     }
 * });
 *
 * table.addColumn(column); </pre>

 * The <tt>Context</tt> parameter of the decorator <tt>render()</tt> method enables you to
 * render controls to provid additional functionality. For example:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomerList <span class="kw">extends</span> BorderedPage {
 *
 *     <span class="kw">private</span> Table table = <span class="kw">new</span> Table(<span class="st">"table"</span>);
 *     <span class="kw">private</span> ActionLink viewLink = <span class="kw">new</span> ActionLink(<span class="st">"view"</span>);
 *
 *     <span class="kw">public</span> CustomerList() {
 *
 *         viewLink.setListener(<span class="kw">this</span>, <span class="st">"onViewClick"</span>);
 *         viewLink.setLabel(<span class="st">"View"</span>);
 *         viewLink.setAttribute(<span class="st">"title"</span>, <span class="st">"View customer details"</span>);
 *         table.addControl(viewLink);
 *
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"id"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"name"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"category"</span>));
 *
 *         Column column = <span class="kw">new</span> Column(<span class="st">"Action"</span>);
 *         column.setDecorator(<span class="kw">new</span> Decorator() {
 *             public String render(Object row, Context context) {
 *                 Customer customer = (Customer) row;
 *                 viewLink.setValue(<span class="st">""</span> + customer.getId());
 *                 viewLink.setContext(context);
 *
 *                 return viewLink.toString();
 *             }
 *          });
 *         table.addColumn(column);
 *
 *         addControl(table);
 *     }
 *
 *     <span class="kw">public boolean</span> onViewClick() {
 *         String path = getContext().getPagePath(Logout.class);
 *         setRedirect(path + <span class="st">"?id="</span> + viewLink.getValue());
 *         <span class="kw">return true</span>;
 *     }
 * } </pre>
 *
 *
 * @see Decorator
 * @see Table
 *
 * @author Malcolm Edgar
 */
public class Column implements Serializable {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The Column attributes Map. */
    protected Map attributes = new HashMap();

    /**
     * The automatically hyperlink column URL and email address values flag,
     * default value is <tt>false</tt>.
     */
    protected boolean autolink;

    /** The column table data &lt;td&gt; CSS class attribute. */
    protected String dataClass;

    /** The column table data &lt;td&gt; CSS style attribute. */
    protected String dataStyle;

    /** The column row decorator. */
    protected Decorator decorator;

    /** The escape HTML characters flag. The default value is true. */
    protected boolean escapeHtml = true;

    /** The column message format pattern. */
    protected String format;

    /** The CSS class attribute of the column header. */
    protected String headerClass;

    /** The CSS style attribute of the column header. */
    protected String headerStyle;

    /** The title of the column header. */
    protected String headerTitle;

    /**
     * The optional MessageFormat used to render the column table cell value.
     */
    protected MessageFormat messageFormat;

    /** The property name of the row object to render. */
    protected String name;

    /** The cached OGNL context for rendering column values. */
    protected Map ognlContext = new HashMap();

    /** The parent Table. */
    protected Table table;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a table column with the given property name. The table header
     * title will be set as the capitalized property name.
     *
     * @param name the name of the property to render
     */
    public Column(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
        this.headerTitle = ClickUtils.toLabel(name);
    }

    /**
     * Create a table column with the given property name and header title.
     *
     * @param name the name of the property to render
     * @param title the column header title to render
     */
    public Column(String name, String title) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        if (title == null) {
            throw new IllegalArgumentException("Null title parameter");
        }
        this.name = name;
        this.headerTitle = title;
    }

    /**
     * Create a Column with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Column() {
    }

    // ------------------------------------------------------ Public Properties

    /**
     * Return the Column HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of column HTML attribute
     * @return the Column HTML attribute
     */
    public String getAttribute(String name) {
        return (String) getAttributes().get(name);
    }

    /**
     * Set the Column with the given HTML attribute name and value. These
     * attributes will be rendered as HTML attributes, for example:
     * <p/>
     * If there is an existing named attribute in the Column it will be replaced
     * with the new value. If the given attribute value is null, any existing
     * attribute will be removed.
     *
     * @param name the name of the column HTML attribute
     * @param value the value of the column HTML attribute
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
     * Return the Column attributes Map.
     *
     * @return the column attributes Map.
     */
    public Map getAttributes() {
        return attributes;
    }

    /**
     * Return true if the Column has attributes or false otherwise.
     *
     * @return true if the column has attributes on false otherwise
     */
    public boolean hasAttributes() {
        return !getAttributes().isEmpty();
    }

    /**
     * Return the flag to automatically render HTML hyperlinks for column URL
     * and email addresses values.
     *
     * @return automatically hyperlink column URL and email addresses flag
     */
    public boolean getAutolink() {
        return autolink;
    }

    /**
     * Set the flag to automatically render HTML hyperlinks for column URL
     * and email addresses values.
     *
     * @param autolink the flag to automatically hyperlink column URL and
     * email addresses flag
     */
    public void setAutolink(boolean autolink) {
        this.autolink = autolink;
    }

    /**
     * Return the table data &lt;td&gt; CSS class.
     *
     * @return the table data CSS class
     */
    public String getDataClass() {
        return dataClass;
    }

    /**
     * Set the table data &lt;td&gt; CSS class.
     *
     * @param dataClass the table data CSS class
     */
    public void setDataClass(String dataClass) {
        this.dataClass = dataClass;
    }

    /**
     * Return the table data &lt;td&gt; CSS style.
     *
     * @return the table data CSS style
     */
    public String getDataStyle() {
        return dataStyle;
    }

    /**
     * Set the table data &lt;td&gt; CSS style.
     *
     * @param style the table data CSS style
     */
    public void setDataStyle(String style) {
        dataStyle = style;
    }

    /**
     * Return the row column &lt;td&gt; decorator.
     *
     * @return the row column &lt;td&gt; decorator
     */
    public Decorator getDecorator() {
        return decorator;
    }

    /**
     * Set the row column &lt;td&gt; decorator.
     *
     * @param decorator the row column &lt;td&gt; decorator
     */
    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    /**
     * Return true if the HTML characters will be escaped when rendering the
     * column data. By default this method returns true.
     *
     * @return true if the HTML characters will be escaped when rendered
     */
    public boolean getEscapeHtml() {
        return escapeHtml;
    }

    /**
     * Set the escape HTML characters when rendering column data flag.
     *
     * @param escape the flag to escape HTML characters
     */
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }

    /**
     * Return the row column message format pattern.
     *
     * @return the message row column message format pattern
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the row column message format pattern.
     *
     * @param pattern the message format pattern
     */
    public void setFormat(String pattern) {
        this.format = pattern;
    }

    /**
     * Return the MessageFormat instance used to format the table cell value.
     *
     * @return the MessageFormat instance used to format the table cell value
     */
    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    /**
     * Set the MessageFormat instance used to format the table cell value.
     *
     * @param messageFormat the MessageFormat used to format the table cell
     *  value
     */
    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    /**
     * Return the property name.
     *
     * @return the name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Set the property name.
     *
     * @param name the property name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the table header &lt;th&gt; CSS class.
     *
     * @return the table header CSS class
     */
    public String getHeaderClass() {
        return headerClass;
    }

    /**
     * Set the table header &lt;th&gt; CSS class.
     *
     * @param headerClass the table header CSS class
     */
    public void setHeaderClass(String headerClass) {
        this.headerClass = headerClass;
    }

    /**
     * Return the table header &lt;th&gt; CSS style.
     *
     * @return the table header CSS style
     */
    public String getHeaderStyle() {
        return headerStyle;
    }

    /**
     * Set the table header &lt;th&gt; CSS style.
     *
     * @param style the table header CSS style
     */
    public void setHeaderStyle(String style) {
        headerStyle = style;
    }

    /**
     * Return the table header &lt;th&gt; title.
     *
     * @return the table header title
     */
    public String getHeaderTitle() {
        return headerTitle;
    }

    /**
     * Set the table header &lt;th&gt; title.
     *
     * @param title the table header title
     */
    public void setHeaderTitle(String title) {
        headerTitle = title;
    }

    /**
     * Return the parent Table containing the Column.
     *
     * @return the parent Table containing the Column
     */
    public Table getTable() {
        return table;
    }

    /**
     * Set the Column's the parent <tt>Table</tt>.
     *
     * @param table Column's parent <tt>Table</tt>
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Return the Table and Column id appended: &nbsp; "<tt>table-column</tt>"
     * <p/>
     * Use the field the "id" attribute value if defined, or the name otherwise.
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            String tableId = (getTable() != null)
                                ? getTable().getId() + "-" : "";

            String id = tableId + getName();

            if (id.indexOf('/') != -1) {
                id = id.replace('/', '_');
            }
            if (id.indexOf(' ') != -1) {
                id = id.replace(' ', '_');
            }

            return id;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the column table data &lt;td&gt; element to the given buffer using
     * the passed row object.
     *
     * @param row the row object to render
     * @param buffer the string buffer to render to
     * @param context the request context
     * @param rowIndex the index of the current row within the parent table
     */
    public void renderTableData(Object row, HtmlStringBuffer buffer,
            Context context, int rowIndex) {

        if (getMessageFormat() == null && getFormat() != null) {
            Locale locale = context.getLocale();
            setMessageFormat(new MessageFormat(getFormat(), locale));
        }

        buffer.elementStart("td");
        buffer.appendAttribute("id", getId() + "_" + rowIndex);
        buffer.appendAttribute("class", getDataClass());
        buffer.appendAttribute("style", getDataStyle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.closeTag();

        renderTableDataContent(row, buffer, context, rowIndex);

        buffer.elementEnd("td");
    }

    /**
     * Render the column table header &lt;tr&gt; element to the given buffer.
     *
     * @param buffer the string buffer to render to
     * @param context the request context
     */
    public void renderTableHeader(HtmlStringBuffer buffer, Context context) {
        buffer.elementStart("th");
        buffer.appendAttribute("class", getHeaderClass());
        buffer.appendAttribute("style", getHeaderStyle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.closeTag();

        if (getEscapeHtml()) {
            buffer.appendEscaped(getHeaderTitle());
        } else {
            buffer.append(getHeaderTitle());
        }

        buffer.elementEnd("th");
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the content within the column table data &lt;td&gt; element.
     *
     * @param row the row object to render
     * @param buffer the string buffer to render to
     * @param context the request context
     * @param rowIndex the index of the current row within the parent table
     */
    protected void renderTableDataContent(Object row, HtmlStringBuffer buffer,
            Context context, int rowIndex) {

        if (getDecorator() != null) {
            Object value = getDecorator().render(row, context);
            if (value != null) {
                buffer.append(value);
            }

        } else {
            Object columnValue = getProperty(row);
            if (columnValue != null) {
                if (getAutolink() && renderLink(columnValue, buffer)) {
                    // Has been rendered

                } else if (getMessageFormat() != null) {
                    Object[] args = new Object[] { columnValue };

                    if (getEscapeHtml()) {
                        buffer.appendEscaped(getMessageFormat().format(args));
                    } else {
                        buffer.append(getMessageFormat().format(args));
                    }

                } else {
                    if (getEscapeHtml()) {
                        buffer.appendEscaped(columnValue);
                    } else {
                        buffer.append(columnValue);
                    }
                }
            }
        }
    }

    /**
     * Return the named column property value from the given row object.
     * <p/>
     * If the row object is a <tt>Map</tt> this method will attempt to return
     * the map value for the column {@link #name}.
     * <p/>
     * The row map lookup will be performed using the property name,
     * if a value is not found the property name in uppercase will be used,
     * if a value is still not found the property name in lowercase will be used.
     * If a map value is still not found then this method will return null.
     * <p/>
     * Object property values can also be specified using an OGNL path
     * expression.
     *
     * @param row the row object to obtain the property from
     * @return the named row object property value
     * @throws RuntimeException if an error occured obtaining the property
     */
    protected Object getProperty(Object row) {
        if (row instanceof Map) {
            Map map = (Map) row;

            Object object = map.get(name);
            if (object != null) {
                return object;
            }

            String upperCaseName = name.toUpperCase();
            object = map.get(upperCaseName);
            if (object != null) {
                return object;
            }

            String lowerCaseName = name.toLowerCase();
            object = map.get(lowerCaseName);
            if (object != null) {
                return object;
            }

            return null;

        } else {

            try {
                return Ognl.getValue(name, ognlContext, row);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Render the given table cell value to the buffer as a <tt>mailto:</tt>
     * or <tt>http:</tt> hyperlink, or as an ordinary string if the value is
     * determined not be linkable.
     *
     * @param value the table cell value to render
     * @param buffer the StringBuffer to render to
     * @return a rendered email or web hyperlink if applicable
     */
    protected boolean renderLink(Object value, HtmlStringBuffer buffer) {
        if (value != null) {
            String valueStr = value.toString();

            // If email
            if (valueStr.indexOf('@') != -1
                && !valueStr.startsWith("@")
                && !valueStr.endsWith("@")) {

                buffer.append("<a href=\"mailto:");
                buffer.append(valueStr);
                buffer.append("\">");
                buffer.append(valueStr);
                buffer.append("</a>");
                return true;

            } else if (valueStr.startsWith("http")) {
                int index = valueStr.indexOf("//");
                if (index != -1) {
                    index += 2;
                } else {
                    index = 0;
                }
                buffer.append("<a href=\"");
                buffer.append(valueStr);
                buffer.append("\">");
                buffer.append(valueStr.substring(index));
                buffer.append("</a>");
                return true;

            } else if (valueStr.startsWith("www")) {
                buffer.append("<a href=\"http://");
                buffer.append(valueStr);
                buffer.append("\">");
                buffer.append(valueStr);
                buffer.append("</a>");
                return true;
            }
        }
        return false;
    }

}
