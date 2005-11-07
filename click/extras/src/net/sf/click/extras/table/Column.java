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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * Provides the Column table data &lt;td&gt; and table header &lt;th&gt;
 * renderer.
 *
 * @see Decorator
 * @see Table
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Column implements Serializable {

    private static final long serialVersionUID = -4573607961095404555L;

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

    /** The cached column property method. */
    protected Method propertyMethod;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a table column with the given property name. The table header
     * title will be set as the capitalized property name.
     *
     * @param name the name of the property to render
     */
    public Column(String name) {
        this.name = name;
        this.headerTitle = StringUtils.capitalize(name);
    }

    /**
     * Create a Column with no name defined, <b>please note</b> the
     * columns's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a column instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
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
     * Set the table data &lt;td&gt; CSS class
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
     * Set the table data &lt;td&gt; CSS style
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
     * Set the table header &lt;th&gt; CSS class
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
     * Set the table header &lt;th&gt; CSS style
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
     * Set the table header &lt;th&gt; title
     *
     * @param title the table header title
     */
    public void setHeaderTitle(String title) {
        headerTitle = title;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the column table data &lt;td&gt; element to the given buffer using
     * the passed row object.
     *
     * @param row the row object to render
     * @param buffer the string buffer to render to
     * @param context the request context
     */
    public void renderTableData(Object row, HtmlStringBuffer buffer,
            Context context) {

        if (getMessageFormat() == null && getFormat() != null) {
            Locale locale = context.getLocale();
            setMessageFormat(new MessageFormat(getFormat(), locale));
        }

        buffer.elementStart("td");
        buffer.appendAttribute("class", getDataClass());
        buffer.appendAttribute("style", getDataStyle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.closeTag();

        if (getDecorator() != null) {
            buffer.append(getDecorator().render(row, context));

        } else {
            Object columnValue = getProperty(row);
            if (columnValue != null) {
                if (getAutolink() && renderLink(columnValue, buffer)) {
                    // Has been rendered

                } else if (getMessageFormat() != null) {
                    Object[] args = new Object[] { columnValue };
                    buffer.append(getMessageFormat().format(args));

                } else {
                    buffer.append(columnValue);
                }
            }
        }

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
        buffer.append(getHeaderTitle());
        buffer.elementEnd("th");
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the named column property value from the given row object.
     *
     * @param row the row object to obtain the property from
     * @return the named row object property value
     * @throws RuntimeException if an error occured obtaining the property
     */
    protected Object getProperty(Object row) {
        try {
            if (propertyMethod == null) {
                String methodName = ClickUtils.toGetterName(name);
                propertyMethod = row.getClass().getMethod(methodName, null);
            }

            return propertyMethod.invoke(row, null);

        } catch (Exception e) {
            throw new RuntimeException(e);
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
            if (valueStr.indexOf('@') != -1 && !valueStr.startsWith("@") &&
                !valueStr.endsWith("@")) {

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
