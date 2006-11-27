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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.MessagesMap;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a HTML Table control: &lt;table&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='table.png' title='Table'/>
 * </td>
 * </tr>
 * </table>
 *
 * The Table control provides a HTML &lt;table&gt; control with
 * <a href="http://sourceforge.net/projects/displaytag">DisplayTag</a>
 * like functionality. The design of the Table control has been informed by
 * the excellent DisplayTag library.
 *
 * <h3>Table Example</h3>
 *
 * An example Table usage is provided below:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomersPage <span class="kw">extends</span> BorderPage {
 *
 *     <span class="kw">public</span> Table table = <span class="kw">new</span> Table();
 *
 *     <span class="kw">public</span> CustomersPage() {
 *         table.setClass(<span class="st">"its"</span>);
 *         table.setPageSize(4);
 *
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"id"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"name"</span>));
 *
 *         Column column = <span class="kw">new</span> Column(<span class="st">"email"</span>);
 *         column.setAutolink(<span class="kw">true</span>);
 *         table.addColumn(column);
 *
 *         column = <span class="kw">new</span> Column(<span class="st">"age"</span>);
 *         column.setTextAlign(<span class="st">"center"</span>);
 *         table.addColumn(column);
 *
 *         column = <span class="kw">new</span> Column(<span class="st">"holdings"</span>);
 *         column.setFormat(<span class="st">"${0,number,#,##0.00}"</span>);
 *         column.setTextAlign(<span class="st">"right"</span>);
 *         table.addColumn(column);
 *     }
 *
 *     <span class="kw">public void</span> onRender() {
 *         List customers = getCustomerService().getCustomersSortedByName();
 *         table.setRowList(customers);
 *     }
 * } </pre>
 *
 * <h4>Table Styles</h4>
 *
 * The Table control automatically deploys the table CSS style sheet
 * (<tt>table.css</tt>) to the application directory <tt>/click</tt>.
 * To import the style sheet simply reference the
 * {@link net.sf.click.util.PageImports} object. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$table</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * The table CSS style sheet is adapted from the DisplayTag <tt>screen.css</tt>
 * style sheet and includes the styles:
 * <ul style="margin-top:0.5em;">
 *  <li>isi</li>
 *  <li>its</li>
 *  <li>mars</li>
 *  <li>nocol</li>
 *  <li>report</li>
 *  <li>simple</li>
 * </ul>
 *
 * To use one of these CSS styles set the table <span class="st">"class"</span>
 * attribute. For examle:
 *
 * <pre class="codeJava">
 * <span class="kw">public</span> LineItemsPage() {
 *     Table table = <span class="kw">new</span> Table(<span class="st">"table"</span>);
 *     table.setClass(<span class="st">"simple"</span>);
 *     ..
 * } </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../../html/struct/tables.html">Tables</a>
 * and the W3C CSS reference
 * <a title="W3C CSS 2.1 Specification"
 *    href="../../../../../../css2/tables.html">Tables</a>.
 *
 * @see Column
 * @see Decorator
 *
 * @author Malcolm Edgar
 */
public class Table implements Control {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /**
     * The table.css style sheet import link.
     */
    public static final String TABLE_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"$/click/table.css\">\n";

    /** The table top banner position. */
    public static final int POSITION_TOP = 1;

    /** The table bottom banner position. */
    public static final int POSITION_BOTTOM = 2;

    /** The table top and bottom banner. */
    public static final int POSITION_BOTH = 3;

    /** The control ActionLink page number parameter name: <tt>"page"</tt>. */
    public static final String PAGE = "page";

    /** The control ActionLink sorted column parameter name: <tt>"column"</tt>. */
    public static final String COLUMN = "column";

    // ----------------------------------------------------- Instance Variables

    /** The table HTML attributes Map. */
    protected Map attributes;

    /**
     * The table pagination banner position:
     * <tt>[ POSITION_TOP | POSITION_BOTTOM | POSITION_BOTH ]</tt>.
     * The default position is <tt>POSITION_BOTTOM</tt>.
     */
    protected int bannerPosition = POSITION_BOTTOM;

    /** The map of table columns keyed by column name. */
    protected Map columns = new HashMap();

    /** The list of table Columns. */
    protected List columnList = new ArrayList();

    /** The request context. */
    protected transient Context context;

    /** The table paging and sorting control action link. */
    protected ActionLink controlLink = new ActionLink("control");

    /** The list of table controls. */
    protected List controlList;

    /** The table HTML &lt;td&gt; height attribute. */
    protected String height;

    /**
     * The table rows set 'hover' CSS class on mouseover events flag. By default
     * hoverRows is false.
     */
    protected boolean hoverRows;

    /** The table localized messages Map. */
    protected Map messages;

    /** The control name. */
    protected String name;

    /**
     * The currently displayed page number. The page number is zero indexed,
     * i.e. the page number of the first page is 0.
     */
    protected int pageNumber;

    /**
     * The maximum page size in rows. A value of 0 means there is no maximum
     * page size.
     */
    protected int pageSize;

    /** The control's parent. */
    protected Object parent;

    /**
     * The total number of rows in the query, if 0 rowCount is undefined. Row
     * count is generally populated with a <tt>SELECT COUNT(*) FROM ..</tt>
     * query and is used to determine the number of pages which can be
     * displayed.
     */
    // TODO: need to consider than passed lists will not be indexed from the
    // start of the query, ie Page 50, wont have 50 pages worth of empty data
    // in the provided list
    protected int rowCount;

    /** The list Table rows. */
    protected List rowList;

    /**
     * The show table banner flag detailing number of rows and rows
     * displayed.
     */
    protected boolean showBanner;

    /**
     * The default column are sortable status. By default columnsSortable is
     * false.
     */
    protected boolean sortable = false;

    /** The row list is sorted status. */
    protected boolean sorted;

    /** The name of the sorted column. */
    protected String sortedColumn;

    /** The table HTML &lt;td&gt; width attribute. */
    protected String width;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an Table for the given name.
     *
     * @param name the table name
     * @throws IllegalArgumentException if the name is null
     */
    public Table(String name) {
        setName(name);
    }

    /**
     * Create a Table with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Table() {
        super();
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
     * Set the Table with the given HTML attribute name and value. These
     * attributes will be rendered as HTML attributes, for example:
     *
     * <pre class="codeJava">
     * Table table = new Table("customer");
     * table.setAttribute("<span class="blue">class</span>", "<span class="red">simple</span>"); </pre>
     *
     * HTML output:
     * <pre class="codeHtml">
     * &lt;table id='customer-table' <span class="blue">class</span>='<span class="red">simple</span>'&gt;
     *   ..
     * &lt;/table&gt; </pre>
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
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;
    }

    /**
     * Return true if the Table has attributes or false otherwise.
     *
     * @return true if the Table has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null) {
            return !getAttributes().isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Return the Table pagination banner position. Banner position values:
     * <tt>[ POSITION_TOP | POSITION_BOTTOM | POSITION_BOTH ]</tt>.
     * The default banner position is <tt>POSITION_BOTTOM</tt>.
     *
     * @return the table pagination banner position.
     */
    public int getBannerPosition() {
        return bannerPosition;
    }

    /**
     * Set Table pagination banner position. Banner position values:
     * <tt>[ POSITION_TOP | POSITION_BOTTOM | POSITION_BOTH ]</tt>.
     *
     * @param value the table pagination banner position
     */
    public void setBannerPosition(int value) {
        bannerPosition = value;
    }

    /**
     * Set the HTML class attribute. Predefined table CSS classes include:
     * <ul>
     *  <li>isi</li>
     *  <li>its</li>
     *  <li>mars</li>
     *  <li>nocol</li>
     *  <li>report</li>
     *  <li>simple</li>
     * </ul>
     * 
     * @param value the HTML class attribute
     */
    public void setClass(String value) {
        setAttribute("class", value);
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
            String msg = "column parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (getColumns().containsKey(column.getName())) {
            String msg =
                "Table already contains column named: " + column.getName();
            throw new IllegalArgumentException(msg);
        }

        getColumns().put(column.getName(), column);
        getColumnList().add(column);
        column.setTable(this);
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

    /**
     * Return the list of table columns.
     *
     * @return the list of table columns
     */
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
     *
     * @return the Page request Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see Control#setContext(Context)
     *
     * @param context the Page request Context
     */
    public void setContext(Context context) {
        this.context = context;
        controlLink.setContext(context);
    }

    /**
     * Add the given Control to the table. The control will be processed when
     * the Table is processed.
     *
     * @param control the Control to add to the table
     */
    public void addControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        getControls().add(control);
    }

    /**
     * Return the list of Controls added to the table. Note table paging control
     * will not be returned in this list.
     *
     * @return the list of table controls
     */
    public List getControls() {
        if (controlList == null) {
            controlList = new ArrayList();
        }
        return controlList;
    }

    /**
     * Return true if the table has any controls defined.
     *
     * @return true if the table has any controls defined
     */
    public boolean hasControls() {
        return (controlList == null) ? false : !controlList.isEmpty();
    }

    /**
     * Return the table paging and sorting control action link.
     *
     * @return the table paging and sorting control action link
     */
    protected ActionLink getControlLink() {
        return controlLink;
    }

    /**
     * Return the table HTML &lt;td&gt; height attribute.
     *
     * @return the table HTML &lt;td&gt; height attribute
     */
    public String getHeight() {
        return height;
    }
    /**
     * Set the table HTML &lt;td&gt; height attribute.
     *
     * @param value the table HTML &lt;td&gt; height attribute
     */
    public void setHeight(String value) {
        height = value;
    }

    /**
     * Return true if the table row (&lt;tr&gt;) elements should have the
     * class="hover" attribute set on JavaScript mouseover events. This class
     * can be used to define mouse over :hover CSS pseudo classes to create
     * table row highlite effects.
     *
     * @return true if table rows elements will have the class 'hover' attribute
     * set on JavaScript mouseover events
     */
    public boolean getHoverRows() {
        return hoverRows;
    }

    /**
     * Set whether the table row (&lt;tr&gt;) elements should have the
     * class="hover" attribute set on JavaScript mouseover events. This class
     * can be used to define mouse over :hover CSS pseudo classes to create
     * table row highlite effects. For example:
     *
     * <pre class="codeHtml">
     * hover:hover { color: navy } </pre>
     *
     * @param hooverRows specify whether class 'hover' rows attribute is rendered (default false).
     */
    public void setHoverRows(boolean hooverRows) {
        this.hoverRows = hooverRows;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/table.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet
     */
    public String getHtmlImports() {
        String path = getContext().getRequest().getContextPath();

        return StringUtils.replace(TABLE_IMPORTS, "$", path);
    }

    /**
     * Return the "id" attribute value if defined, or the table name otherwise.
     *
     * @see Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            return getName();
        }
    }

    /**
     * @see Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        // Does nothing
    }

    /**
     * Return the package resource bundle message for the named resource key
     * and the context's request locale.
     *
     * @param name resource name of the message
     * @return the named localized message for the package
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String message = null;

        Map parentMessages = ClickUtils.getParentMessages(this);
        if (parentMessages.containsKey(name)) {

            message = (String) parentMessages.get(name);
        }

        if (message == null && getMessages().containsKey(name)) {
            message = (String) getMessages().get(name);
        }

        return message;
    }

    /**
     * Return the formatted package message for the given resource name and
     * message format arguments and for the context request locale.
     *
     * @param name resource name of the message
     * @param args the message arguments to format
     * @return the named localized message for the package
     */
    public String getMessage(String name, Object[] args) {
        if (args == null) {
            throw new IllegalArgumentException("Null args parameter");
        }
        String value = getMessage(name);

        return MessageFormat.format(value, args);
    }

    /**
     * Return a Map of localized messages for the Control.
     * The returned message-bundle is {@link Field#CONTROL_MESSAGES}.
     *
     * @return a Map of localized messages for the Control
     * @throws IllegalStateException if the context for the Field has not been set
     */
    public Map getMessages() {
        if (messages == null) {
            if (getContext() != null) {
                messages =
                    new MessagesMap(this, CONTROL_MESSAGES, getContext());

            } else {
                String msg = "Cannot initialize messages as context not set";
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }

    /**
     * @see Control#getName()
     *
     * @return the name of the control
     */
    public String getName() {
        return name;
    }

    /**
     * @see Control#setName(String)
     *
     * @param name of the control
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
    }

    /**
     * Return the number of pages to display.
     *
     * @return the number of pages to display
     */
    public int getNumberPages() {
        if (getPageSize() == 0) {
            return 1;
        }

        if (rowList == null || rowList.isEmpty()) {
            return 1;
        }

        double value = (double) rowList.size() / (double) getPageSize();

        return (int) Math.ceil(value);
    }

    /**
     * Return the currently displayed page number. The page number is zero
     * indexed, i.e. the page number of the first page is 0.
     *
     * @return the currently displayed page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Set the currently displayed page number. The page number is zero
     * indexed, i.e. the page number of the first page is 0.
     *
     * @param pageNumber set the currently displayed page number
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Return the maximum page size in rows. A page size of 0
     * means there is no maximum page size.
     *
     * @return the maximum page size in rows
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Set the maximum page size in rows. A page size of 0
     * means there is no maximum page size.
     *
     * @param pageSize the maximum page size in rows
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @see Control#getParent()
     *
     * @return the Control's parent
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @see Control#setParent(Object)
     *
     * @param parent the parent of the Control
     */
    public void setParent(Object parent) {
        this.parent = parent;
    }

    /**
     * Return total number of rows in the query, if 0 rowCount is undefined. Row
     * count is generally populated with a <tt>SELECT COUNT(*) FROM ..</tt>
     * query and is used to determine the number of pages which can be
     * displayed.
     *
     * @return the total number of rows in the quer]y, or 0 if undefined
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Set the total number of rows in the query, if 0 rowCount is undefined. Row
     * count is generally populated with a <tt>SELECT COUNT(*) FROM ..</tt>
     * query and is used to determine the number of pages which can be
     * displayed.
     *
     * @param rowCount the total number of rows in the quer]y, or 0 if undefined
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
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
     * Set the list of table rows.
     *
     * @param rowList the list of table rows to set
     */
    public void setRowList(List rowList) {
        this.rowList = rowList;
    }

    /**
     * Return the show Table banner flag detailing number of rows and rows
     * displayed.
     *
     * @return the show Table banner flag
     */
    public boolean getShowBanner() {
        return showBanner;
    }

    /**
     * Set the show Table banner flag detailing number of rows and rows
     * displayed.
     *
     * @param showBanner the show Table banner flag
     */
    public void setShowBanner(boolean showBanner) {
        this.showBanner = showBanner;
    }

    /**
     * Return the table default column are sortable status. By default table
     * columns are sortable.
     *
     * @return the table default column are sortable status
     */
    public boolean getSortable() {
        return sortable;
    }

    /**
     * Set the table default column are sortable status.
     *
     * @param sortable the table default column are sortable status
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * Return the sorted status of the table row list.
     *
     * @return the sorted table row list status
     */
    public boolean isSorted() {
        return sorted;
    }

    /**
     * Set the sorted status of the table row list.
     *
     * @param value the sorted status to set
     */
    public void setSorted(boolean value) {
        sorted = value;
    }

    /**
     * Return the name of the sorted column, or null if not defined.
     *
     * @return the name of the sorted column, or null if not defined
     */
    public String getSortedColumn() {
        return sortedColumn;
    }

    /**
     * Set the name of the sorted column, or null if not defined.
     *
     * @param value the the name of the sorted column
     */
    public void setSortedColumn(String value) {
        sortedColumn = value;
    }

    /**
     * Return the table HTML &lt;td&gt; width attribute.
     *
     * @return the table HTML &lt;td&gt; width attribute
     */
    public String getWidth() {
        return width;
    }
    /**
     * Set the table HTML &lt;td&gt; width attribute.
     *
     * @param value the table HTML &lt;td&gt; width attribute
     */
    public void setWidth(String value) {
        width = value;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the <tt>table.css</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/control/table.css",
                              "click");
    }

    /**
     * Process any Table paging control requests, and process any added Table
     * Controls.
     *
     * @see Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        controlLink.onProcess();

        String page = getContext().getRequestParameter(PAGE);
        if (page != null) {
            setPageNumber(Integer.parseInt(page));
        }

        String column = getContext().getRequestParameter(COLUMN);
        if (column != null) {
            setSortedColumn(column);
        }

        boolean continueProcessing = true;
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            control.setContext(getContext());
            continueProcessing = control.onProcess();
            if (!continueProcessing) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return a HTML rendered Table string.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered Table string
     */
    public String toString() {
        int bufferSize =
            (getColumnList().size() * 60) * (getRowList().size() + 1);

        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        if (getBannerPosition() == POSITION_TOP
            || getBannerPosition() == POSITION_BOTH) {

            renderTableBanner(buffer);
            renderPagingControls(buffer);
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
        }

        // Render table start.
        buffer.elementStart("table");
        buffer.appendAttribute("id", getId());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.appendAttribute("height", getHeight());
        buffer.appendAttribute("width", getWidth());
        buffer.closeTag();
        buffer.append("\n");

        renderHeaderRow(buffer);

        sortRowList();
        renderBodyRows(buffer);

        // Render table end.
        buffer.append("</table>\n");

        if (getBannerPosition() == POSITION_BOTTOM
            || getBannerPosition() == POSITION_BOTH) {

            renderTableBanner(buffer);
            renderPagingControls(buffer);
        }

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the index of the first row to display. Index starts from 0.
     *
     * @return the index of the first row to display
     */
    protected int getFirstRow() {
        int firstRow = 0;

        if (getPageSize() > 0) {
            if (getPageNumber() > 0) {
                firstRow = getPageSize() * getPageNumber();
            }
        }

        return firstRow;
    }

    /**
     * Return the index of the last row to diplay. Index starts from 0.
     *
     * @return the index of the last row to display
     */
    protected int getLastRow() {
        int numbRows = getRowList().size();
        int lastRow = numbRows;

        if (getPageSize() > 0) {
            lastRow = getFirstRow() + getPageSize();
            lastRow = Math.min(lastRow, numbRows);
        }
        return lastRow;
    }

    /**
     * Render the table header row of column names.
     *
     * @param buffer the StringBuffer to render the header row in
     */
    protected void renderHeaderRow(HtmlStringBuffer buffer) {
        buffer.append("<thead>\n<tr>\n");

        final List tableColumns = getColumnList();
        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            column.renderTableHeader(buffer, context);
            if (j < tableColumns.size() - 1) {
                buffer.append("\n");
            }
        }

        buffer.append("</tr></thead>\n");
    }

    /**
     * Render the table body rows for each of the rows in <tt>getRowList</tt>.
     *
     * @param buffer the StringBuffer to render the table body rows in
     */
    protected void renderBodyRows(HtmlStringBuffer buffer) {
        buffer.append("<tbody>\n");

        // Range sanity check
        int pageNumber = Math.min(getPageNumber(), getRowList().size() - 1);
        pageNumber = Math.max(pageNumber, 0);
        setPageNumber(pageNumber);

        int firstRow = getFirstRow();
        int lastRow = getLastRow();

        if (lastRow == 0) {
            renderBodyNoRows(buffer);
        } else {
            final List tableRows = getRowList();

            for (int i = firstRow; i < lastRow; i++) {
                boolean even = (i + 1) % 2 == 0;
                if (even) {
                    buffer.append("<tr class=\"even\"");
                } else {
                    buffer.append("<tr class=\"odd\"");
                }

                if (getHoverRows()) {
                    buffer.append(" onmouseover=\"this.className='hover';\"");
                    buffer.append(" onmouseout=\"this.className='");
                    if (even) {
                        buffer.append("even");
                    } else {
                        buffer.append("odd");
                    }
                    buffer.append("';\"");
                }

                buffer.append(">\n");

                renderBodyRowColumns(buffer, i);

                buffer.append("</tr>");
                if (i < tableRows.size() - 1) {
                    buffer.append("\n");
                }
            }
        }

        buffer.append("</tbody>");
    }

    /**
     * Render the current table body row cells.
     *
     * @param buffer the StringBuffer to render the table row cells in
     * @param rowIndex the 0-based index in tableRows to render
     */
    protected void renderBodyRowColumns(HtmlStringBuffer buffer, int rowIndex) {
        Object row = getRowList().get(rowIndex);

        final List tableColumns = getColumnList();

        for (int j = 0; j < tableColumns.size(); j++) {
            Column column = (Column) tableColumns.get(j);
            column.renderTableData(row, buffer, context, rowIndex);
            if (j < tableColumns.size() - 1) {
                buffer.append("\n");
            }
        }
    }

    /**
     * Render the table body content if no rows are in the row list.
     *
     * @param buffer the StringBuffer to render the no row message to
     */
    protected void renderBodyNoRows(HtmlStringBuffer buffer) {
        buffer.append("<tr class=\"odd\"><td colspan=\"");
        buffer.append(getColumns().size());
        buffer.append("\" class=\"error\">");
        buffer.append(getMessage("table-no-rows-found"));
        buffer.append("</td></tr>\n");
    }

    /**
     * Render the table banner detailing number of rows and number displayed.
     * <p/>
     * See the <tt>/click-controls.properies</tt> for the HTML templates:
     * <tt>table-page-banner</tt> and <tt>table-page-banner-nolinks</tt>
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderTableBanner(HtmlStringBuffer buffer) {
        if (getShowBanner()) {
            String totalRows = null;
            if (getRowCount() > 0) {
                totalRows = String.valueOf(getRowCount());
            } else {
                totalRows = String.valueOf(getRowList().size());
            }

            String firstRow = null;
            if (getRowList().isEmpty()) {
                firstRow = String.valueOf(0);
            } else {
                firstRow = String.valueOf(getFirstRow() + 1);
            }

            String lastRow = null;
            if (getRowList().isEmpty()) {
                lastRow = String.valueOf(0);
            } else {
                lastRow = String.valueOf(getLastRow());
            }

            String[] args = { totalRows, firstRow, lastRow};

            if (getPageSize() > 0) {
                buffer.append(getMessage("table-page-banner", args));
            } else {
                buffer.append(getMessage("table-page-banner-nolinks", args));
            }
        }
    }

    /**
     * Render the table paging action link controls.
     * <p/>
     * See the <tt>/click-controls.properies</tt> for the HTML templates:
     * <tt>table-page-links</tt> and <tt>table-page-links-nobanner</tt>
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderPagingControls(HtmlStringBuffer buffer) {
        if (getPageSize() > 0) {
            String firstLabel = getMessage("table-first-label");
            String firstTitle = getMessage("table-first-title");
            String previousLabel = getMessage("table-previous-label");
            String previousTitle = getMessage("table-previous-title");
            String nextLabel = getMessage("table-next-label");
            String nextTitle = getMessage("table-next-title");
            String lastLabel = getMessage("table-last-label");
            String lastTitle = getMessage("table-last-title");
            String gotoTitle = getMessage("table-goto-title");

            if (getSortedColumn() != null) {
                controlLink.setParameter(COLUMN, getSortedColumn());
            } else {
                controlLink.setParameter(COLUMN, null);
            }

            if (getPageNumber() > 0) {
                controlLink.setLabel(firstLabel);
                controlLink.setParameter(PAGE, String.valueOf(0));
                controlLink.setAttribute("title", firstTitle);
                controlLink.setId("control-first");
                firstLabel = controlLink.toString();

                controlLink.setLabel(previousLabel);
                controlLink.setParameter(PAGE, String.valueOf(getPageNumber() - 1));
                controlLink.setId("control-previous");
                controlLink.setAttribute("title", previousTitle);
                previousLabel = controlLink.toString();
            }

            HtmlStringBuffer pagesBuffer =
                new HtmlStringBuffer(getNumberPages() * 70);

            // Create sliding window of paging links
            int lowerBound = Math.max(0, getPageNumber() - 5);
            int upperBound = Math.min(lowerBound + 10, getNumberPages());
            if (upperBound - lowerBound < 10) {
                lowerBound = Math.max(upperBound - 10, 0);
            }

            for (int i = lowerBound; i < upperBound; i++) {
                String pageNumber = String.valueOf(i + 1);
                if (i == getPageNumber()) {
                    pagesBuffer.append("<strong>" + pageNumber + "</strong>");

                } else {
                    controlLink.setLabel(pageNumber);
                    controlLink.setParameter(PAGE, String.valueOf(i));
                    controlLink.setAttribute("title", gotoTitle + " " + pageNumber);
                    controlLink.setId("control-" + pageNumber);
                    pagesBuffer.append(controlLink.toString());
                }

                if (i < upperBound - 1) {
                    pagesBuffer.append(", ");
                }
            }
            String pageLinks = pagesBuffer.toString();

            if (getPageNumber() < getNumberPages() - 1) {
                controlLink.setLabel(nextLabel);
                controlLink.setParameter(PAGE, String.valueOf(getPageNumber() + 1));
                controlLink.setAttribute("title", nextTitle);
                controlLink.setId("control-next");
                nextLabel = controlLink.toString();

                controlLink.setLabel(lastLabel);
                controlLink.setParameter(PAGE, String.valueOf(getNumberPages() - 1));
                controlLink.setAttribute("title", lastTitle);
                controlLink.setId("control-last");
                lastLabel = controlLink.toString();
            }

            String[] args =
                { firstLabel, previousLabel, pageLinks, nextLabel, lastLabel };

            if (getShowBanner()) {
                buffer.append(getMessage("table-page-links", args));
            } else {
                buffer.append(getMessage("table-page-links-nobanner", args));
            }
        }
    }

    /**
     * The default row list sorting method, which will sort the row list based
     * on the selected column if the row list is not already sorted.
     */
    protected void sortRowList() {
        if (!isSorted() && StringUtils.isNotBlank(getSortedColumn())) {

            final Column column = (Column) getColumns().get(getSortedColumn());

            Collections.sort(getRowList(), new Comparator() {
                public int compare(Object row1, Object row2) {

                    Object obj1 = column.getProperty(row1);
                    Object obj2 = column.getProperty(row2);

                    if (obj1 instanceof Comparable
                        && obj2 instanceof Comparable) {

                        return ((Comparable) obj1).compareTo(obj2);

                    } else if (obj1 instanceof Boolean
                               && obj2 instanceof Boolean) {

                        boolean bool1 = ((Boolean) obj1).booleanValue();
                        boolean bool2 = ((Boolean) obj2).booleanValue();

                        if (bool1 == bool2) {
                            return 0;

                        } else if (bool1 && !bool2) {
                            return 1;

                        } else {
                            return -1;
                        }

                    } else if (obj1 != null && obj2 == null) {

                        return +1;

                    } else if (obj1 == null && obj2 != null) {

                        return -1;

                    } else {
                        return 0;
                    }
                }
            });

            setSorted(true);
        }
    }

}
