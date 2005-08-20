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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.control.ActionLink;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a HTML Table control: &lt;table&gt;.
 * <p/>
 * The Table control provides a HTML &lt;table&gt; control with
 * <a href="http://sourceforge.net/projects/dispaytag">DisplayTag</a>
 * like functionality. The design on the Table control has been informed by
 * the excellent DisplayTag library, with the aim of making this control easy to
 * learn for DisplayTag users.
 * <p/>
 * The Table control automatically deploys the table CSS style sheet to the
 * click application directory <tt>/click/table.css</tt>. To import the style
 * sheet simpley reference the method {@link Table#getHtmlImports()}. For
 * example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$table.htmlImports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="blue">$table</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * The table CSS style sheet is adapted from the DisplayTag <tt>screen.css</tt>
 * style sheet and includes the styles:
 * <ul style="margin-top:0.5em;">
 *  <li>mars</li>
 *  <li>isi</li>
 *  <li>its</li>
 *  <li>report</li>
 *  <li>simple</li>
 * </ul>
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
 * @version $Id$
 */
public class Table implements Control {


    // -------------------------------------------------------------- Constants

    /**
     * The Click Extras messages bundle name: &nbsp; <tt>click-extras</tt>
     */
    protected static final String CONTROL_MESSAGES = "click-extras";

    /**
     * The click table properties bundle name: &nbsp; <tt>click-table</tt>
     */
    protected static final String TABLE_PROPERTIES = "click-table";

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
        return context;
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        this.context = context;
        if (pagingLink != null) {
            pagingLink.setContext(context);
        }
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

        Locale locale = getContext().getRequest().getLocale();

        ResourceBundle bundle =
            ResourceBundle.getBundle(CONTROL_MESSAGES, locale);

        return bundle.getString(name);
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
     * @return the list of table rows
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
        int page = pagingLink.getValueInteger().intValue();
        
        // Range sanity check
        page = Math.min(page, getRowList().size() -1);
        page = Math.max(page, 0);
        
        setPageNumber(page);
        
        return true;
    }

    /**
     * Process any Table paging control requests.
     *
     * @see Control#onProcess()
     */
    public boolean onProcess() {
        if (pagingLink != null) {
            pagingLink.onProcess();
        }
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
        
        int firstRow = getFirstRow();
        int lastRow = getLastRow();
                
        for (int i = firstRow; i < lastRow; i++) {
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

    /**
     * Render the table banner detailing number of rows and number displayed.
     * <p/>
     * See the <tt>/click-extras.properies</tt> for the HTML templates:
     * <tt>table-page-banner</tt> and <tt>table-page-banner-nolinks</tt>
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderTableBanner(StringBuffer buffer) {
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
    protected void renderPagingControls(StringBuffer buffer) {
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
