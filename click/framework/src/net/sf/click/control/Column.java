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
import net.sf.click.util.PropertyUtils;

/**
 * Provides the Column table data &lt;td&gt; and table header &lt;th&gt;
 * renderer.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr><td>
 *
 * <table id="table" class="isi">
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
 * </td></tr></table>
 *
 * <p/>
 *
 * The Column object provide column definitions for the {@link Table} object.
 *
 * <h3>Column Options</h3>
 *
 * The Column class supports a number of rendering options which include:
 *
 * <ul>
 * <li>{@link #autolink} - the option to automatically render href links
 *      for email and URL column values</li>
 * <li>{@link #attributes} - the CSS style attributes for the table data cell</li>
 * <li>{@link #dataClass} - the CSS class for the table data cell</li>
 * <li>{@link #dataStyles} - the CSS styles for the table data cell</li>
 * <li>{@link #decorator} - the custom column value renderer</li>
 * <li>{@link #format} - the <tt>MessageFormat</tt> pattern rendering
 *      the column value</li>
 * <li>{@link #headerClass} - the CSS class for the table header cell</li>
 * <li>{@link #headerStyles} - the CSS styles for the table header cell</li>
 * <li>{@link #headerTitle} - the table header cell value to render</li>
 * <li>{@link #sortable} - the table column sortable property</li>
 * <li>{@link #width} - the table cell width property</li>
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
 * table.setClass(<span class="st">"isi"</span>);
 *
 * Column idColumn = <span class="kw">new</span> Column(<span class="st">"purchaseId"</span>, <span class="st">"ID"</span>);
 * idColumn.setFormat(<span class="st">"{0,number,#,###}"</span>);
 * table.addColumn(idColumn);
 *
 * Column priceColumn = <span class="kw">new</span> Column(<span class="st">"purchasePrice"</span>, <span class="st">"Price"</span>);
 * priceColumn.setFormat(<span class="st">"{0,number,currency}"</span>);
 * priceColumn.setTextAlign(<span class="st">"right"</span>);
 * table.addColumn(priceColumn);
 *
 * Column dateColumn = <span class="kw">new</span> Column(<span class="st">"purchaseDate"</span>, <span class="st">"Date"</span>);
 * dateColumn.setFormat(<span class="st">"{0,date,dd MMM yyyy}"</span>);
 * table.addColumn(dateColumn);
 *
 * Column orderIdColumn = <span class="kw">new</span> Column(<span class="st">"order.id"</span>, <span class="st">"Order ID"</span>);
 * table.addColumn(orderIdColumn);  </pre>
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
    protected Map attributes;

    /**
     * The automatically hyperlink column URL and email address values flag,
     * default value is <tt>false</tt>.
     */
    protected boolean autolink;

    /** The column table data &lt;td&gt; CSS class attribute. */
    protected String dataClass;

    /** The Map of column table data &lt;td&gt; CSS style attributes. */
    protected Map dataStyles;

    /** The column row decorator. */
    protected Decorator decorator;

    /** The escape HTML characters flag. The default value is true. */
    protected boolean escapeHtml = true;

    /** The column message format pattern. */
    protected String format;

    /** The CSS class attribute of the column header. */
    protected String headerClass;

    /** The Map of column table header &lt;th&gt; CSS style attributes. */
    protected Map headerStyles;

    /** The title of the column header. */
    protected String headerTitle;

    /**
     * The maximum column length. If maxLength is greater than 0 and the column
     * data string length is greater than maxLength, the rendered value will be
     * truncated with an eclipse(...).
     * <p/>
     * Autolinked email or URL values will not be constrained.
     * <p/>
     * The default value is 0.
     */
    protected int maxLength;

    /**
     * The optional MessageFormat used to render the column table cell value.
     */
    protected MessageFormat messageFormat;

    /** The property name of the row object to render. */
    protected String name;

    /** The method cached for rendering column values. */
    protected Map methodCache;

    /** The column sortable status. The default value is false. */
    protected Boolean sortable;

    /** The parent Table. */
    protected Table table;

    /** The column HTML &lt;td&gt; width attribute. */
    protected String width;

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
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;
    }

    /**
     * Return true if the Column has attributes or false otherwise.
     *
     * @return true if the column has attributes on false otherwise
     */
    public boolean hasAttributes() {
        return (attributes != null && !getAttributes().isEmpty());
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
     * @param name the CSS style name
     * @return the table data CSS style for the given name
     */
    public String getDataStyle(String name) {
        if (hasDataStyles()) {
            return (String) getDataStyles().get(name);

        } else {
            return null;
        }
    }

    /**
     * Set the table data &lt;td&gt; CSS style name and value pair.
     *
     * @param name the CSS style name
     * @param value the CSS style value
     */
    public void setDataStyle(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getDataStyles().put(name, value);
        } else {
            getDataStyles().remove(name);
        }
    }

    /**
     * Return true if table data &lt;td&gt; CSS styles are defined.
     *
     * @return true if table data &lt;td&gt; CSS styles are defined
     */
    public boolean hasDataStyles() {
        return (dataStyles != null && !dataStyles.isEmpty());
    }

    /**
     * Return the Map of table data &lt;td&gt; CSS styles.
     *
     * @return the Map of table data &lt;td&gt; CSS styles
     */
    public Map getDataStyles() {
        if (dataStyles == null) {
            dataStyles = new HashMap();
        }
        return dataStyles;
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
     * Set the row column message format pattern. For example:
     *
     * <pre class="javaClass">
     * Column idColumn = <span class="kw">new</span> Column(<span class="st">"purchaseId"</span>, <span class="st">"ID"</span>);
     * idColumn.setFormat(<span class="st">"{0,number,#,###}"</span>);
     *
     * Column priceColumn = <span class="kw">new</span> Column(<span class="st">"purchasePrice"</span>, <span class="st">"Price"</span>);
     * priceColumn.setFormat(<span class="st">"{0,number,currency}"</span>);
     *
     * Column dateColumn = <span class="kw">new</span> Column(<span class="st">"purchaseDate"</span>, <span class="st">"Date"</span>);
     * dateColumn.setFormat(<span class="st">"{0,date,dd MMM yyyy}"</span>); </pre>
     *
     * <h4>MesssageFormat Patterns</h4>
     *
     * <table border='1' cellspacing='0' cellpadding='3'>
     *  <tr bgcolor="#ccccff">
     *   <th id="ft">Format Type
     *   <th id="fs">Format Style
     *   <th id="sc">Subformat Created
     *  <tr bgcolor="#ffffff">
     *   <td bgcolor="#eeeeff" headers="ft" rowspan=5><code>number</code>
     *   <td headers="fs"><i>(none)</i>
     *   <td headers="sc"><code>NumberFormat.getInstance(getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>integer</code>
     *   <td headers="sc"><code>NumberFormat.getIntegerInstance(getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>currency</code>
     *   <td headers="sc"><code>NumberFormat.getCurrencyInstance(getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>percent</code>
     *   <td headers="sc"><code>NumberFormat.getPercentInstance(getLocale())</code>
     *  <tr>
     *   <td headers="fs"><i>SubformatPattern</i>
     *   <td headers="sc"><code>new DecimalFormat(subformatPattern, new DecimalFormatSymbols(getLocale()))</code>
     *  <tr>
     *   <td bgcolor="#eeeeff" headers="ft" rowspan=6><code>date</code>
     *   <td headers="fs"><i>(none)</i>
     *   <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>short</code>
     *   <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.SHORT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>medium</code>
     *   <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>long</code>
     *   <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.LONG, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>full</code>
     *   <td headers="sc"><code>DateFormat.getDateInstance(DateFormat.FULL, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><i>SubformatPattern</i>
     *   <td headers="sc"><code>new SimpleDateFormat(subformatPattern, getLocale())</code>
     *  <tr>
     *   <td bgcolor="#eeeeff" headers="ft" rowspan=6><code>time</code>
     *   <td headers="fs"><i>(none)</i>
     *   <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.DEFAULT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>short</code>
     *   <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.SHORT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>medium</code>
     *   <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.DEFAULT, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>long</code>
     *   <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.LONG, getLocale())</code>
     *  <tr>
     *   <td headers="fs"><code>full</code>
     *   <td headers="sc"><code>DateFormat.getTimeInstance(DateFormat.FULL, getLocale())</code>
     *   <tr>
     *   <td headers="fs"><i>SubformatPattern</i>
     *   <td headers="sc"><code>new SimpleDateFormat(subformatPattern, getLocale())</code>
     *  <tr>
     *   <td bgcolor="#eeeeff" headers="ft"><code>choice</code>
     *   <td headers="fs"><i>SubformatPattern</i>
     *   <td headers="sc"><code>new ChoiceFormat(subformatPattern)</code>
     * </table>
     *
     * <h4>DecimalFormat Pattern Characters</h4>
     *
     *  <table border='1' cellspacing='0' cellpadding='3'>
     *      <tr bgcolor="#ccccff">
     *           <th align=left>Symbol
     *           <th align=left>Location
     *           <th align=left>Localized?
     *           <th align=left>Meaning
     *      <tr valign=top>
     *           <td><code>0</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Digit
     *      <tr valign=top bgcolor="#eeeeff">
     *           <td><code>#</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Digit, zero shows as absent
     *      <tr valign=top>
     *           <td><code>.</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Decimal separator or monetary decimal separator
     *      <tr valign=top bgcolor="#eeeeff">
     *           <td><code>-</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Minus sign
     *      <tr valign=top>
     *           <td><code>,</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Grouping separator
     *      <tr valign=top bgcolor="#eeeeff">
     *           <td><code>E</code>
     *           <td>Number
     *           <td>Yes
     *           <td>Separates mantissa and exponent in scientific notation.
     *               <em>Need not be quoted in prefix or suffix.</em>
     *      <tr valign=top>
     *           <td><code>;</code>
     *           <td>Subpattern boundary
     *           <td>Yes
     *           <td>Separates positive and negative subpatterns
     *      <tr valign=top bgcolor="#eeeeff">
     *           <td><code>%</code>
     *           <td>Prefix or suffix
     *           <td>Yes
     *           <td>Multiply by 100 and show as percentage
     *      <tr valign=top>
     *           <td><code>&#92;u2030</code>
     *           <td>Prefix or suffix
     *           <td>Yes
     *           <td>Multiply by 1000 and show as per mille
     *      <tr valign=top bgcolor="#eeeeff">
     *           <td><code>&#164;</code> (<code>&#92;u00A4</code>)
     *           <td>Prefix or suffix
     *           <td>No
     *           <td>Currency sign, replaced by currency symbol.  If
     *               doubled, replaced by international currency symbol.
     *               If present in a pattern, the monetary decimal separator
     *               is used instead of the decimal separator.
     *      <tr valign=top>
     *           <td><code>'</code>
     *           <td>Prefix or suffix
     *           <td>No
     *           <td>Used to quote special characters in a prefix or suffix,
     *               for example, <code>"'#'#"</code> formats 123 to
     *               <code>"#123"</code>.  To create a single quote
     *               itself, use two in a row: <code>"# o''clock"</code>.
     *  </table>
     *
     * <h4>SimpleDateFormat Pattern Characters</h4>
     *
     *  <table border="1" cellspacing="0" cellpadding="3">
     *  <tr bgcolor="#ccccff">
     *           <th align=left>Letter
     *           <th align=left>Date or Time Component
     *           <th align=left>Presentation
     *           <th align=left>Examples
     *       <tr>
     *           <td><code>G</code>
     *           <td>Era designator
     *           <td>Text
     *           <td><code>AD</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>y</code>
     *           <td>Year
     *           <td>Year
     *           <td><code>1996</code>; <code>96</code>
     *       <tr>
     *           <td><code>M</code>
     *           <td>Month in year
     *           <td>Month
     *           <td><code>July</code>; <code>Jul</code>; <code>07</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>w</code>
     *           <td>Week in year
     *           <td>Number
     *           <td><code>27</code>
     *       <tr>
     *           <td><code>W</code>
     *           <td>Week in month
     *           <td>Number
     *           <td><code>2</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>D</code>
     *           <td>Day in year
     *           <td>Number
     *           <td><code>189</code>
     *       <tr>
     *           <td><code>d</code>
     *           <td>Day in month
     *           <td>Number
     *           <td><code>10</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>F</code>
     *           <td>Day of week in month
     *           <td>Number
     *           <td><code>2</code>
     *       <tr>
     *           <td><code>E</code>
     *           <td>Day in week
     *           <td>Text
     *           <td><code>Tuesday</code>; <code>Tue</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>a</code>
     *           <td>Am/pm marker
     *           <td>Text
     *           <td><code>PM</code>
     *       <tr>
     *           <td><code>H</code>
     *           <td>Hour in day (0-23)
     *           <td>Number
     *           <td><code>0</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>k</code>
     *           <td>Hour in day (1-24)
     *           <td>Number
     *           <td><code>24</code>
     *       <tr>
     *           <td><code>K</code>
     *           <td>Hour in am/pm (0-11)
     *           <td>Number
     *           <td><code>0</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>h</code>
     *           <td>Hour in am/pm (1-12)
     *           <td>Number
     *           <td><code>12</code>
     *       <tr>
     *           <td><code>m</code>
     *           <td>Minute in hour
     *           <td>Number
     *           <td><code>30</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>s</code>
     *           <td>Second in minute
     *           <td>Number
     *           <td><code>55</code>
     *       <tr>
     *           <td><code>S</code>
     *           <td>Millisecond
     *           <td>Number
     *           <td><code>978</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>z</code>
     *           <td>Time zone
     *           <td>General time zone
     *           <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>
     *       <tr>
     *           <td><code>Z</code>
     *           <td>Time zone
     *           <td>RFC 822 time zone
     *           <td><code>-0800</code>
     *   </table>
     *
     * @param pattern the message format pattern
     */
    public void setFormat(String pattern) {
        this.format = pattern;
    }

    /**
     * The maximum column length. If maxLength is greater than 0 and the column
     * data string length is greater than maxLength, the rendered value will be
     * truncated with an eclipse(...).
     *
     * @return the maximum column length, or 0 if not defined
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Set the maximum column length. If maxLength is greater than 0 and the
     * column data string length is greater than maxLength, the rendered value
     * will be truncated with an eclipse(...).
     *
     * @param value the maximum column length
     */
    public void setMaxLength(int value) {
        maxLength = value;
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
     * @param name the CSS style name
     * @return the table header CSS style value for the given name
     */
    public String getHeaderStyle(String name) {
        if (hasHeaderStyles()) {
            return (String) getHeaderStyles().get(name);

        } else {
            return null;
        }
    }

    /**
     * Set the table header &lt;th&gt; CSS style name and value pair.
     *
     * @param name the CSS style name
     * @param value the CSS style value
     */
    public void setHeaderStyle(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getHeaderStyles().put(name, value);
        } else {
            getHeaderStyles().remove(name);
        }
    }

    /**
     * Return true if table header &lt;th&gt; CSS styles are defined.
     *
     * @return true if table header &lt;th&gt; CSS styles are defined
     */
    public boolean hasHeaderStyles() {
        return (headerStyles != null && !headerStyles.isEmpty());
    }

    /**
     * Return the Map of table header &lt;th&gt; CSS styles.
     *
     * @return the Map of table header &lt;th&gt; CSS styles
     */
    public Map getHeaderStyles() {
        if (headerStyles == null) {
            headerStyles = new HashMap();
        }
        return headerStyles;
    }

    /**
     * Return the table header &lt;th&gt; title.
     * <p/>
     * If the header title value is null, this method will attempt to find a
     * localized message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".headerTitle"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value still cannot be found then the Column name will be converted
     * into a header title using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     *
     * @return the table header title
     */
    public String getHeaderTitle() {
        if (headerTitle == null) {
            headerTitle = getTable().getMessage(getName() + ".headerTitle");
        }
        if (headerTitle == null) {
            headerTitle = ClickUtils.toLabel(getName());
        }
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

    /**
     * Return the column sortable status. If the column sortable status is not
     * defined the value will be inherited from the
     * {@link Table#sortable} property.
     *
     * @return the column sortable status
     */
    public boolean getSortable() {
        if (sortable == null) {
            if (getTable() != null) {
                return getTable().getSortable();
            } else {
                return false;
            }

        } else {
            return sortable.booleanValue();
        }
    }

    /**
     * Set the column sortable status.
     *
     * @param value the column sortable status
     */
    public void setSortable(boolean value) {
        sortable = Boolean.valueOf(value);
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
     * Set the column CSS "text-align" style for the header &lt;th&gt; and
     * data &lt;td&gt; elements.
     *
     * @param align the CSS "text-align" value: <tt>["left", "right", "center"]</tt>
     */
    public void setTextAlign(String align) {
        //setHeaderStyle("text-align", align);
        setDataStyle("text-align", align);
    }

    /**
     * Set the column CSS "vertical-align" style for the header &lt;th&gt; and
     * data &lt;td&gt; elements.
     *
     * @param align the CSS "vertical-align" value
     */
    public void setVerticalAlign(String align) {
        setHeaderStyle("vertical-align", align);
        setDataStyle("vertical-align", align);
    }

    /**
     * Return the column HTML &lt;td&gt; width attribute.
     *
     * @return the column HTML &lt;td&gt; width attribute
     */
    public String getWidth() {
        return width;
    }
    /**
     * Set the column HTML &lt;td&gt; width attribute.
     *
     * @param value the column HTML &lt;td&gt; width attribute
     */
    public void setWidth(String value) {
        width = value;
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
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (hasDataStyles()) {
            buffer.appendStyleAttributes(getDataStyles());
        }
        buffer.appendAttribute("width", getWidth());
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

        boolean isSortable = getSortable() && !getTable().getRowList().isEmpty();

        if (isSortable) {
            boolean sortedColumn = getName().equals(getTable().getSortedColumn());

            String classValue = (getHeaderClass() != null) ? getHeaderClass() : "";
            classValue += (sortedColumn) ? " sorted" : "sortable";
            buffer.appendAttribute("class", classValue);

        } else {
            buffer.appendAttribute("class", getHeaderClass());
        }

        if (hasHeaderStyles()) {
            buffer.appendStyleAttributes(getHeaderStyles());
        }

        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }

        buffer.closeTag();

        if (isSortable) {
            ActionLink controlLink = getTable().getControlLink();

            if (controlLink.getContext() == null) {
                controlLink.setContext(getTable().getContext());
            }

            controlLink.setId("control-" + getName());
            controlLink.setParameter(Table.COLUMN, getName());
            controlLink.setParameter(Table.PAGE, String.valueOf(getTable().getPageNumber()));
            controlLink.setLabel(getHeaderTitle());

            buffer.append(controlLink);

        } else {
            if (getEscapeHtml()) {
                buffer.appendEscaped(getHeaderTitle());
            } else {
                buffer.append(getHeaderTitle());
            }
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

                    String value = getMessageFormat().format(args);

                    if (getMaxLength() > 0) {
                        value = ClickUtils.limitLength(value, getMaxLength());
                    }

                    if (getEscapeHtml()) {
                        buffer.appendEscaped(value);
                    } else {
                        buffer.append(value);
                    }

                } else {
                    String value = columnValue.toString();

                    if (getMaxLength() > 0) {
                        value = ClickUtils.limitLength(value, getMaxLength());
                    }

                    if (getEscapeHtml()) {
                        buffer.appendEscaped(value);
                    } else {
                        buffer.append(value);
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
     * Object property values can also be specified using an path expression.
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
            if (methodCache == null) {
                methodCache = new HashMap();
            }

            return PropertyUtils.getValue(row, name, methodCache);
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
