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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
 * <p/>
 * The Table control provides a HTML &lt;table&gt; control with
 * <a href="http://sourceforge.net/projects/dispaytag">DisplayTag</a>
 * like functionality. The design of the Table control has been informed by
 * the excellent DisplayTag library, with the aim of making this control easy to
 * learn for DisplayTag users.
 * <p/>
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
 *  <li>report</li>
 *  <li>simple</li>
 * </ul>
 *
 * To use one of these CSS styles set the table <span class="st">"class"</span>
 * attribute. For examle:
 *
 * <pre class="codeJava">
 * <span class="kw">public</span> CustomersPage() {
 *     Table table = <span class="kw">new</span> Table(<span class="st">"table"</span>);
 *     table.setAttribute(<span class="st">"class"</span>, <span class="st">"simple"</span>);
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

    private static final long serialVersionUID = 8465121273938579765L;

    // -------------------------------------------------------------- Constants

    /**
     * The table.css style sheet import link.
     */
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

    /** The list of Table controls. */
    protected final List controlList = new ArrayList();
 
    /** The Field localized messages Map. */
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

    /** The Table paging action link. */
    protected ActionLink pagingLink;

    /** The parent localized messages map. */
    protected Map parentMessages;

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
     * The show Table banner flag detailing number of rows and rows displayed.
     */
    protected boolean showBanner;

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
     * Create a Table with no name defined, <b>please note</b> the control's
     * name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
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
        if (pagingLink != null) {
            pagingLink.setContext(context);
        }
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
        controlList.add(control);
    }

    /**
     * Return the list of Controls added to the table. Note table paging control
     * will not be returned in this list.
     *
     * @return the list of table controls
     */
    public List getControls() {
        return controlList;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/table.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

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

        if (getParentMessages() != null && getParentMessages().containsKey(name))
        {
            message = (String) getParentMessages().get(name);
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
     * Return a Map of localized messages for the Field.
     *
     * @return a Map of localized messages for the Field
     * @throws IllegalStateException if the context for the Field has not be set
     */
    public Map getMessages() {
         if (messages == null) {
             if (getContext() != null) {
                 Locale locale = getContext().getLocale();
                 messages = new MessagesMap(Field.CONTROL_MESSAGES, locale);

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

        return (int) Math.ceil((double)rowList.size() / (double) getPageSize());
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

        if (pageSize > 0) {
            pagingLink = new ActionLink("paging");
            pagingLink.setListener(this, "onPagingClick");
        }
    }

    /**
     * @see Control#getParentMessages()
     *
     * @return the localization <tt>Map</tt> of the Control's parent
     */
    public Map getParentMessages() {
        return parentMessages;
    }

    /**
     * @see Control#setParentMessages(Map)
     *
     * @param messages the parent's the localized messages <tt>Map</tt>
     */
    public void setParentMessages(Map messages) {
        parentMessages = messages;
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
     * Return the list of table rows.
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

    // --------------------------------------------------------- Public Methods

    /**
     * Handle the previous table page control click.
     *
     * @return true
     */
    public boolean onPagingClick() {
        setPageNumber(pagingLink.getValueInteger().intValue());

        return true;
    }

    /**
     * Deploy the <tt>table.css</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     * @throws IOException if a I/O error occurs
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        ClickUtils.deployFile
            (servletContext, "/net/sf/click/control/table.css", "click");

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
        if (pagingLink != null) {
            pagingLink.onProcess();
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

        // Render table start.
        buffer.elementStart("table");
        buffer.appendAttribute("id", getId());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.closeTag();
        buffer.append("\n");

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

        // Range sanity check
        int pageNumber = Math.min(getPageNumber(), getRowList().size() -1);
        pageNumber = Math.max(pageNumber, 0);
        setPageNumber(pageNumber);

        int firstRow = getFirstRow();
        int lastRow = getLastRow();

        for (int i = firstRow; i < lastRow; i++) {
            Object row = tableRows.get(i);

            if ((i+1) % 2 == 0) {
                buffer.append("<tr class=\"even\">\n");
            } else {
                buffer.append("<tr class=\"odd\">\n");
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

        renderTableBanner(buffer);
        renderPagingControls(buffer);

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
     * Render the table banner detailing number of rows and number displayed.
     * <p/>
     * See the <tt>/click-extras.properies</tt> for the HTML templates:
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
     * See the <tt>/click-extras.properies</tt> for the HTML templates:
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

            if (getPageNumber() > 0) {
                pagingLink.setLabel(firstLabel);
                pagingLink.setValue(String.valueOf(0));
                pagingLink.setAttribute("title", firstTitle);
                firstLabel = pagingLink.toString();

                pagingLink.setLabel(previousLabel);
                pagingLink.setValue(String.valueOf(getPageNumber() - 1));
                pagingLink.setAttribute("title", previousTitle);
                previousLabel = pagingLink.toString();
            }

            StringBuffer pagesBuffer = new StringBuffer(getNumberPages() * 70);
            for (int i = 0; i < getNumberPages(); i++) {
                String pageNumber = String.valueOf(i + 1);
                if (i == getPageNumber()) {
                    pagesBuffer.append("<strong>" + pageNumber + "</strong>");
                } else {
                    pagingLink.setLabel(pageNumber);
                    pagingLink.setValue(String.valueOf(i));
                    pagingLink.setAttribute("title", gotoTitle + " " + pageNumber);
                    pagesBuffer.append(pagingLink.toString());
                }

                if (i < getNumberPages() - 1) {
                    pagesBuffer.append(", ");
                }
            }
            String pageLinks = pagesBuffer.toString();

            if (getPageNumber() < getNumberPages() - 1) {
                pagingLink.setLabel(nextLabel);
                pagingLink.setValue(String.valueOf(getPageNumber() + 1));
                pagingLink.setAttribute("title", nextTitle);
                nextLabel = pagingLink.toString();

                pagingLink.setLabel(lastLabel);
                pagingLink.setValue(String.valueOf(getNumberPages() - 1));
                pagingLink.setAttribute("title", lastTitle);
                lastLabel = pagingLink.toString();
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
}
