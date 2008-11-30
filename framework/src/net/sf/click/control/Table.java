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
package net.sf.click.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

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
 *     <span class="kw">public</span> ActionLink deleteLink = <span class="kw">new</span> ActionLink(<span class="st">"Delete"</span>, <span class="kw">this</span>, <span class="st">"onDeleteClick"</span>);
 *
 *     <span class="kw">public</span> CustomersPage() {
 *         table.setClass(Table.CLASS_ITS);
 *         table.setPageSize(4);
 *         table.setShowBanner(<span class="kw">true</span>);
 *         table.setSortable(<span class="kw">true</span>);
 *
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"id"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"name"</span>));
 *
 *         Column column = <span class="kw">new</span> Column(<span class="st">"email"</span>);
 *         column.setAutolink(<span class="kw">true</span>);
 *         table.addColumn(column);
 *
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"investments"</span>));
 *
 *         column = <span class="kw">new</span> Column(<span class="st">"Action"</span>);
 *         column.setDecorator(<span class="kw">new</span> LinkDecorator(table, deleteLink, <span class="st">"id"</span>));
 *         column.setSortable(<span class="kw">false</span>);
 *         table.addColumn(column);
 *     }
 *
 *     public boolean onDeleteClick() {
 *         Integer id = deleteLink.getValueInteger();
 *         getCustomerService().deleteCustomer(id);
 *         return <span class="kw">true</span>;
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
 * To import the Table CSS styles and any control JavaScript simply reference <span class="blue">$cssImports</span>
 * and <span class="blue">$jsImports</span> in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$cssImports</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$table</span>
 * &lt;/body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsImports</span></pre>
 *
 * The table CSS style sheet is adapted from the DisplayTag <tt>screen.css</tt>
 * style sheet and includes the styles:
 * <ul style="margin-top:0.5em;">
 *  <li>blue1</li>
 *  <li>blue2</li>
 *  <li>complex</li>
 *  <li>isi</li>
 *  <li>its</li>
 *  <li>mars</li>
 *  <li>nocol</li>
 *  <li>orange1</li>
 *  <li>orange2</li>
 *  <li>report</li>
 *  <li>simple</li>
 * </ul>
 *
 * To use one of these CSS styles set the table <span class="st">"class"</span>
 * attribute. For example in a page constructor:
 *
 * <pre class="codeJava">
 * <span class="kw">public</span> LineItemsPage() {
 *     Table table = <span class="kw">new</span> Table(<span class="st">"table"</span>);
 *     table.setClass(Table.CLASS_SIMPLE);
 *     ..
 * } </pre>
 *
 * An alternative method of specifying the table class to use globally for your
 * application is to define a <tt>table-default-class</tt> message property
 * in your applications <tt>click-pages.properties</tt> file. For example:
 *
 * <pre class="codeConfig">
 * table-default-class=blue2
 * </pre>
 *
 * <a name="paging-and-sorting"></a>
 * <h4>Paging and Sorting</h4>
 *
 * Table provides out-of-the-box paging and sorting.
 * <p/>
 * To enable Paging set the table's page size: {@link #setPageSize(int)} and
 * make the Table Banner visible: {@link #setShowBanner(boolean)}.
 * <p/>
 * To enable/disable sorting use {@link #setSortable(boolean)}.
 * <p/>
 * You can also set parameters on links generated by the Table through
 * the Table's {@link #getControlLink() controlLink}.
 * <p/>
 * One often needs to add extra parameters on the Table links in order to
 * preserve state when navigating between pages or sorting columns.
 * <p/>
 * For example:
 *
 * <pre class="prettyprint">
 * public CompanyPage extends BorderPage {
 *
 *      // companyId is the criteria used to limit Table rows to clients from
 *      // the specified company
 *      public String companyId;
 *
 *      public Table table = new Table();
 *
 *      public onInit() {
 *          // companyId could be selected from a drop down list or similar means.
 *          // Set the companyId on the table's controlLink. If you view the
 *          // output rendered by Table you will note the companyId parameter
 *          // is rendered for each Paging and Sorting link.
 *          table.getControlLink().setParameter("companyId", companyId);
 *      }
 *
 *      ...
 *
 *      public void onRender() {
 *          // Select only clients for the specified companyId
 *          List rowList = getCompanyDao().getCompanyClients(companyId);
 *          table.setRowList(rowList);
 *      }
 * } </pre>
 *
 * Table supports rendering different paginators through the method
 * {@link #setPaginator(net.sf.click.control.Renderable)}. The default Table
 * Paginator is {@link TablePaginator}.
 *
 * <a name="row-attributes"></a>
 * <h4>Row Attributes</h4>
 *
 * Sometimes it is useful to add HTML attributes on individual rows. For these
 * cases one can override the method {@link #addRowAttributes(java.util.Map, java.lang.Object, int)}.
 * <p/>
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
public class Table extends AbstractControl {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    private static final Set DARK_STYLES;

    static {
        DARK_STYLES = new HashSet();
        DARK_STYLES.add("isi");
        DARK_STYLES.add("nocol");
        DARK_STYLES.add("report");
    }

    /**
     * The table.css style sheet import link with a light contract sortable icon.
     */
    public static final String TABLE_IMPORTS_LIGHT =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/table{1}.css\"/>\n"
        + "<style type=\"text/css\"> th.sortable a '{'background: url({0}/click/column-sortable-light{1}.gif) center right no-repeat;'}' th.ascending a '{'background: url({0}/click/column-ascending-light{1}.gif) center right no-repeat;'}' th.descending a '{'background: url({0}/click/column-descending-light{1}.gif) center right no-repeat;'}' </style>\n";

    /**
     * The table.css style sheet import link with a dark contract sortable icon.
     */
    public static final String TABLE_IMPORTS_DARK =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/table{1}.css\"/>\n"
        + "<style type=\"text/css\"> th.sortable a '{'background: url({0}/click/column-sortable-dark{1}.gif) center right no-repeat;'}' th.ascending a '{'background: url({0}/click/column-ascending-dark{1}.gif) center right no-repeat;'}' th.descending a '{'background: url({0}/click/column-descending-dark{1}.gif) center right no-repeat;'}' </style>\n";

    /** The attached style pagination banner position. */
    public static final int PAGINATOR_ATTACHED = 1;

    /** The detached style pagination banner position. */
    public static final int PAGINATOR_DETACHED = 2;

    /** The attached style pagination banner position. */
    public static final int PAGINATOR_INLINE = 3;

    /** The table top pagination banner position. */
    public static final int POSITION_TOP = 1;

    /** The table bottom pagination banner position. */
    public static final int POSITION_BOTTOM = 2;

    /** The table top and bottom pagination banner position. */
    public static final int POSITION_BOTH = 3;

    /** The control ActionLink page number parameter name: <tt>"ascending"</tt>. */
    public static final String ASCENDING = "ascending";

    /** The control ActionLink sorted column parameter name: <tt>"column"</tt>. */
    public static final String COLUMN = "column";

    /** The control ActionLink page number parameter name: <tt>"page"</tt>. */
    public static final String PAGE = "page";

    /** The control ActionLink sort number parameter name: <tt>"sort"</tt>. */
    public static final String SORT = "sort";

    /** The table CSS style: <tt>"blue1"</tt>. */
    public static final String CLASS_BLUE1 = "blue1";

    /** The table CSS style: <tt>"blue2"</tt>. */
    public static final String CLASS_BLUE2 = "blue2";

    /** The table CSS style: <tt>"complex"</tt>. */
    public static final String CLASS_COMPLEX = "complex";

    /** The table CSS style: <tt>"isi"</tt>. */
    public static final String CLASS_ISI = "isi";

    /** The table CSS style: <tt>"its"</tt>. */
    public static final String CLASS_ITS = "its";

    /** The table CSS style: <tt>"mars"</tt>. */
    public static final String CLASS_MARS = "mars";

    /** The table CSS style: <tt>"nocol"</tt>. */
    public static final String CLASS_NOCOL = "nocol";

    /** The table CSS style: <tt>"orange1"</tt>. */
    public static final String CLASS_ORANGE1 = "orange1";

    /** The table CSS style: <tt>"orange2"</tt>. */
    public static final String CLASS_ORANGE2 = "orange2";

    /** The table CSS style: <tt>"report"</tt>. */
    public static final String CLASS_REPORT = "report";

    /** The table CSS style: <tt>"simple"</tt>. */
    public static final String CLASS_SIMPLE = "simple";

    /** The array of pre-defined table CSS class styles. */
    public static final String[] CLASS_STYLES = {
        Table.CLASS_BLUE1, Table.CLASS_BLUE2, Table.CLASS_COMPLEX,
        Table.CLASS_ISI, Table.CLASS_ITS, Table.CLASS_MARS, Table.CLASS_NOCOL,
        Table.CLASS_ORANGE1, Table.CLASS_ORANGE2, Table.CLASS_REPORT,
        Table.CLASS_SIMPLE
    };

    // ----------------------------------------------------- Instance Variables

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

    /** The table paging and sorting control action link. */
    protected ActionLink controlLink;

    /** The list of table controls. */
    protected List controlList;

    /** The table HTML &lt;td&gt; height attribute. */
    protected String height;

    /**
     * The table rows set 'hover' CSS class on mouseover events flag. By default
     * hoverRows is false.
     */
    protected boolean hoverRows;

    /**
     * Flag indicating if <tt>rowList</tt> is nullified when
     * <tt>onDestroy()</tt> is invoked, default is true. This flag only applies
     * to <tt>stateful</tt> pages.
     * <p/>
     * @see #setNullifyRowListOnDestroy(boolean)
     */
    protected boolean nullifyRowListOnDestroy = true;

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

    /** The paginator used to render the table pagination controls. */
    protected Renderable paginator;

    /**
     * The paginator attachment style:
     * <tt>[ PAGINATOR_ATTACHED | PAGINATOR_DEATTACHED | PAGINATOR_INLINE ]</tt>.
     * The default paginator attachment type is <tt>PAGINATOR_ATTACHED</tt>.
     */
    protected int paginatorAttachment = PAGINATOR_ATTACHED;

    /**
     * The default column render id attribute status. The default value is
     * false.
     */
    protected boolean renderId;

    /**
     * The list Table rows. Please note the rowList is cleared in table
     * {@link #onDestroy()} method at the end of each request.
     */
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

    /** The row list is sorted status. By default sorted is false. */
    protected boolean sorted = false;

    /** The rows list is sorted in ascending order. */
    protected boolean sortedAscending = true;

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
     * Return the table's html tag: <tt>table</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
     public String getTag() {
        return "table";
     }

    /**
     * Set the parent of the Table.
     *
     * @see net.sf.click.Control#setParent(Object)
     *
     * @param parent the parent of the Table
     * @throws IllegalStateException if {@link #name} is not defined
     * @throws IllegalArgumentException if the given parent instance is
     * referencing <tt>this</tt> object: <tt>if (parent == this)</tt>
     */
    public void setParent(Object parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Cannot set parent to itself");
        }
        if (getName() == null) {
            throw new IllegalArgumentException("Table name is not defined");
        }
        this.parent = parent;
    }

    /**
     * Return the Table pagination banner position. Banner position values:
     * <tt>[ POSITION_TOP | POSITION_BOTTOM | POSITION_BOTH ]</tt>.
     * The default banner position is <tt>POSITION_BOTTOM</tt>.
     *
     * @return the table pagination banner position
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
     * Set the HTML class attribute.
     * <p/>
     * <b>Note:</b> this method will replace the existing <tt>"class"</tt>
     * attribute value.
     * <p/>
     * Predefined table CSS classes include:
     * <ul>
     *  <li>complex</li>
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
     * @return the added column
     * @throws IllegalArgumentException if the table already contains a column
     * with the same name, or the column name is not defined
     */
    public Column addColumn(Column column) {
        if (column == null) {
            String msg = "column parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (StringUtils.isBlank(column.getName())) {
            throw new IllegalArgumentException("Column name is not defined");
        }
        if (getColumns().containsKey(column.getName())) {
            String msg =
                "Table already contains column named: " + column.getName();
            throw new IllegalArgumentException(msg);
        }

        getColumns().put(column.getName(), column);
        getColumnList().add(column);
        column.setTable(this);

        return column;
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
     * Return true if the Table will nullify the <tt>rowList</tt> when the
     * <tt>onDestroy()</tt> method is invoked.
     *
     * @return true if the rowList is nullified when onDestroy is invoked
     */
    public boolean getNullifyRowListOnDestroy() {
        return nullifyRowListOnDestroy;
    }

    /**
     * Set the flag to nullify the <tt>rowList</tt> when the <tt>onDestroy()</tt>
     * method is invoked.
     * <p/>
     * This option only applies to <tt>stateful</tt> pages.
     * <p/>
     * If this option is false, the rowList will be persisted between requests.
     * If this option is true (<tt>the default</tt>), the rowList must be set
     * each request.
     *
     * @param value the flag value to nullify the table rowList when onDestroy
     * is called
     */
    public void setNullifyRowListOnDestroy(boolean value) {
        nullifyRowListOnDestroy = value;
    }

    /**
     * Return the Column for the given name.
     *
     * @param name the name of the column to return
     * @return the Column for the given name
     */
    public Column getColumn(String name) {
        return (Column) columns.get(name);
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
     * Add the given Control to the table. The control will be processed when
     * the Table is processed.
     *
     * @param control the Control to add to the table
     * @return the added control
     */
    public Control addControl(Control control) {
        return add(control);
    }

    /**
     * Add the given Control to the table. The control will be processed when
     * the Table is processed.
     *
     * @param control the Control to add to the table
     * @return the added control
     */
    public Control add(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        // Note: set parent first since setParent might veto further processing
        control.setParent(this);

        getControls().add(control);

        return control;
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
     * <p/>
     * <b>Note</b> you can set parameters on the returned ActionLink in order
     * to preserve state when paging or sorting columns. A common use case is
     * to filter out Table rows on specified criteria. See
     * <a href="#paging-and-sorting">here</a> for an example.
     *
     * @return the table paging and sorting control action link
     */
    public ActionLink getControlLink() {
        if (controlLink == null) {
            controlLink = new ActionLink();
        }
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
     * @param hoverRows specify whether class 'hover' rows attribute is rendered (default false).
     */
    public void setHoverRows(boolean hoverRows) {
        this.hoverRows = hoverRows;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/table.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        // Flag indicating which import style to return
        boolean useDarkStyle = false;
        if (hasAttribute("class")) {

            String styleClasses = getAttribute("class");

            StringTokenizer tokens = new StringTokenizer(styleClasses, " ");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (DARK_STYLES.contains(token)) {
                    useDarkStyle = true;
                    break;
                }
            }
        }

        if (useDarkStyle) {
            buffer.append(ClickUtils.createHtmlImport(TABLE_IMPORTS_DARK, getContext()));

        } else {
            buffer.append(ClickUtils.createHtmlImport(TABLE_IMPORTS_LIGHT, getContext()));
        }

        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                String htmlImports = control.getHtmlImports();
                if (htmlImports != null) {
                    buffer.append(htmlImports);
                }
            }
        }

        return buffer.toString();
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
     * @see Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name) {
        super.setName(name);
        getControlLink().setName(getName() + "-controlLink");
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

        if (getRowList().isEmpty()) {
            return 1;
        }

        double value = (double) getRowList().size() / (double) getPageSize();

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
     * Return the paginator for rendering the table pagination.
     *
     * @return the table paginator
     */
    public Renderable getPaginator() {
        if (paginator == null) {
            paginator = new TablePaginator(this);
        }
        return paginator;
    }

    /**
     * Set the paginator for rendering the table pagination controls.
     *
     * @param value the table paginator to set
     */
    public void setPaginator(Renderable value) {
        paginator = value;
    }

    /**
     * Return the paginator attachment style. Renderable attachment style values:
     * <tt>[ PAGINATOR_ATTACHED | PAGINATOR_DEATTACHED | PAGINATOR_INLINE ]</tt>.
     * The default paginator attachment type is <tt>PAGINATOR_ATTACHED</tt>.
     *
     * @return the paginator attachment style
     */
    public int getPaginatorAttachment() {
        return paginatorAttachment;
    }

    /**
     * Set Table pagination attachment style. Renderable attachment style values:
     * <tt>[ PAGINATOR_ATTACHED | PAGINATOR_DEATTACHED | PAGINATOR_INLINE ]</tt>.
     *
     * @param value the table pagination attachment style
     */
    public void setPaginatorAttachment(int value) {
        paginatorAttachment = value;
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
     * Returns the column render id attribute status. The default value
     * is false.
     *
     * @return the column render id attribute status, default is false
     */
    public boolean getRenderId() {
        return renderId;
    }

    /**
     * Set the column render id attribute status.
     *
     * @param renderId set the column render id attribute status
     */
    public void setRenderId(boolean renderId) {
        this.renderId = renderId;
    }

    /**
     * Return the list of table rows. Please note the rowList is cleared in
     * table {@link #onDestroy()} method at the end of each request.
     *
     * @return the list of table rows
     */
    public List getRowList() {
        if (rowList == null) {
            rowList = new ArrayList(0);
        }

        return rowList;
    }

    /**
     * Set the list of table rows. Each row can either be a value object
     * (JavaBean) or an instance of a <tt>Map</tt>.
     * <p/>
     * Please note the rowList is cleared in table {@link #onDestroy()} method
     * at the end of each request.
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
     * columns are not sortable.
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
     * Return true if the sort order is ascending.
     *
     * @return true if the sort order is ascending
     */
    public boolean isSortedAscending() {
        return sortedAscending;
    }

    /**
     * Set the ascending sort order status.
     *
     * @param value the ascending sort order status
     */
    public void setSortedAscending(boolean value) {
        sortedAscending = value;
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
     * Deploy the <tt>table.css</tt> and column sorting icon files to the
     * <tt>click</tt> web directory when the application is initialized.
     *
     * @see Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        String[] files = new String[] {
                "/net/sf/click/control/column-sortable-dark.gif",
                "/net/sf/click/control/column-sortable-light.gif",
                "/net/sf/click/control/column-ascending-dark.gif",
                "/net/sf/click/control/column-ascending-light.gif",
                "/net/sf/click/control/column-descending-dark.gif",
                "/net/sf/click/control/column-descending-light.gif",
                "/net/sf/click/control/table.css"
            };

        for (int i = 0; i < files.length; i++) {
            ClickUtils.deployFile(servletContext,
                                  files[i],
                                  "click");
        }
    }

    /**
     * Initialize the controls contained in the Table.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        super.onInit();
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            control.onInit();
        }
    }

    /**
     * Perform any pre rendering logic.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            control.onRender();
        }
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
        Context context = getContext();
        getControlLink().onProcess();

        if (getControlLink().isClicked()) {
            String page = context.getRequestParameter(PAGE);
            if (NumberUtils.isNumber(page)) {
                setPageNumber(Integer.parseInt(page));
            } else {
                setPageNumber(0);
            }

            String column = context.getRequestParameter(COLUMN);
            if (column != null) {
                setSortedColumn(column);
            }

            String ascending = context.getRequestParameter(ASCENDING);
            if (ascending != null) {
                setSortedAscending("true".equals(ascending));
            }

            // Flip sorting order
            if ("true".equals(context.getRequestParameter(SORT))) {
                setSortedAscending(!isSortedAscending());
            }
        }

        boolean continueProcessing = true;
        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            continueProcessing = control.onProcess();
            if (!continueProcessing) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method will clear the <tt>rowList</tt>, if the property
     * <tt>clearRowListOnDestroy</tt> is true, set the sorted flag to false and
     * will invoke the onDestroy() method of any child controls.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
        sorted = false;

        for (int i = 0, size = getControls().size(); i < size; i++) {
            Control control = (Control) getControls().get(i);
            try {
                control.onDestroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (getNullifyRowListOnDestroy()) {
            setRowList(null);
        }
    }

    /**
     * @see AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    public int getControlSizeEst() {
        int bufferSize = 0;
        if (getPageSize() > 0) {
            bufferSize = (getColumnList().size() * 60) * (getPageSize() + 1) + 1792;
        } else {
            bufferSize = (getColumnList().size() * 60) * (getRowList().size() + 1);
        }
        return bufferSize;
    }

    /**
     * Render the HTML representation of the Table.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        // Range sanity check.
        int pageNumber = Math.min(getPageNumber(), getRowList().size() - 1);
        pageNumber = Math.max(pageNumber, 0);
        setPageNumber(pageNumber);

        // If attached table top paginator configured render it.
        if (getPaginatorAttachment() == PAGINATOR_ATTACHED) {
            if (getBannerPosition() == POSITION_TOP || getBannerPosition() == POSITION_BOTH) {
                renderPaginator(buffer);
                if (buffer.length() > 0) {
                    buffer.append("\n");
                }
            }
        }

        // Render table start.
        buffer.elementStart(getTag());
        buffer.appendAttribute("id", getId());

        // If table class not specified, look for message property
        // 'table-default-class' in the global click-page.properties messages bundle.
        if (!hasAttribute("class") && getPage() != null) {
            Map messages = getPage().getMessages();
            if (messages.containsKey("table-default-class")) {
                String htmlClass = (String) messages.get("table-default-class");
                setAttribute("class", htmlClass);
            }
        }

        appendAttributes(buffer);

        buffer.appendAttribute("height", getHeight());
        buffer.appendAttribute("width", getWidth());

        buffer.closeTag();
        buffer.append("\n");

        // Render table header
        renderHeaderRow(buffer);

        sortRowList();

        // Render table body
        renderBodyRows(buffer);

        // Render table footer
        renderFooterRow(buffer);

        // Render table end.
        buffer.elementEnd(getTag());
        buffer.append("\n");

        // If attached table bottom paginator configured render it.
        if (getPaginatorAttachment() == PAGINATOR_ATTACHED) {
            if (getBannerPosition() == POSITION_BOTTOM || getBannerPosition() == POSITION_BOTH) {
                renderPaginator(buffer);
            }
        }
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
            column.renderTableHeader(buffer, getContext());
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

        // If inline table top paginator configured render it.
        if (getPaginatorAttachment() == PAGINATOR_INLINE) {
            if (getBannerPosition() == POSITION_TOP || getBannerPosition() == POSITION_BOTH) {

                buffer.append("<tr class=\"paging-inline\">\n");
                buffer.append("<td class=\"paging-inline\" colspan=\"");
                buffer.append(getColumnList().size());
                buffer.append("\">");

                renderPaginator(buffer);

                buffer.append("</td></tr>\n");
            }
        }

        int firstRow = getFirstRow();
        int lastRow = getLastRow();

        if (lastRow == 0) {
            renderBodyNoRows(buffer);
        } else {
            final List tableRows = getRowList();

            Map rowAttributes = new HashMap(3);

            for (int i = firstRow; i < lastRow; i++) {
                Object row = getRowList().get(i);

                buffer.append("<tr");

                // Calculate if row is odd or even
                boolean even = (i + 1) % 2 == 0;
                String hoverClass = null;
                if (even) {
                    hoverClass = "even";
                } else {
                    hoverClass = "odd";
                }

                // Empty the row attributes
                rowAttributes.clear();

                // Allow user to add row attributes
                addRowAttributes(rowAttributes, row, i);

                if (!rowAttributes.isEmpty()) {
                    // Append id attribute if it was set
                    buffer.appendAttribute("id", rowAttributes.get("id"));

                    // Remove class attribute and append hoverClass to the value
                    String cls = (String) rowAttributes.remove("class");

                    // Open class attribute
                    buffer.append(" class=\"");
                    if (cls != null) {
                        buffer.append(cls).append(" ");
                    }
                    buffer.append(hoverClass);

                    // Close class attribute
                    buffer.append("\"");

                    // Render other attributes set by user.
                    buffer.appendAttributes(rowAttributes);
                } else {
                    // If attributes was not set by user, render hoverClass
                    // attribute
                    buffer.append(" class=\"").append(hoverClass).append("\"");
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

        // If inline table bottom paginator configured render it.
        if (getPaginatorAttachment() == PAGINATOR_INLINE) {
            if (getBannerPosition() == POSITION_BOTTOM || getBannerPosition() == POSITION_BOTH) {

                buffer.append("\n<tr class=\"paging-inline\">\n");
                buffer.append("<td class=\"paging-inline\" colspan=\"");
                buffer.append(getColumnList().size());
                buffer.append("\">");

                renderPaginator(buffer);

                buffer.append("</td>\n</tr>\n");
            }
        }

        buffer.append("</tbody>");
    }

    /**
     * Override this method to set HTML attributes for each Table row.
     * <p/>
     * For example:
     *
     * <pre class="prettyprint">
     * public CompanyPage extends BorderPage {
     *
     *     public void onInit() {
     *         table = new Table() {
     *             public void addRowAttributes(Map attributes, Object domain, int rowIndex) {
     *                 Customer customer = (Customer) customer;
     *                 if (customer.isDisabled()) {
     *                     // Set the row class to disabled. CSS can then be used
     *                     // to set disabled rows background to a different color.
     *                     attributes.put("class", "disabled");
     *                 }
     *                 attributes.put("onclick", "alert('you clicked on row "
     *                     + rowIndex + "')");
     *             }
     *         };
     *     }
     * } </pre>
     *
     * <b>Please note</b> that in order to enable alternate background colors
     * for rows, Click will automatically add a CSS <tt>class</tt> attribute
     * to each row with a value of either <tt>odd</tt> or <tt>even</tt>. You are
     * free to add other CSS class attributes as illustrated in the example
     * above.
     *
     * @param attributes the row attributes
     * @param row the domain object currently being rendered
     * @param rowIndex the rows index
     */
    protected void addRowAttributes(Map attributes, Object row, int rowIndex) {
    }

    /**
     * Render the table header footer row. This method is designed to be
     * overridden by Table subclasses which include a custom footer row.
     * <p/>
     * By default this method does not render a table footer.
     * <p/>
     * An example:
     * <pre class="prettyprint">
     * private Table table;
     *
     * public void onInit() {
     *     table = new Table("table") {
     *         public void renderFooterRow(HtmlStringBuffer buffer) {
     *             double totalHoldings = getCustomerService().getTotalHoldings(customers);
     *             renderTotalHoldingsFooter(buffer);
     *         };
     *     }
     *     addControl(table);
     *     ...
     * }
     *
     * ...
     *
     * public void renderTotalHoldingsFooter(HtmlStringBuffer buffer,) {
     *     double total = 0;
     *     for (int i = 0; i < table.getRowList().size(); i++) {
     *         Customer customer = (Customer) table.getRowList().get(i);
     *         if (customer.getHoldings() != null) {
     *             total += customer.getHoldings().doubleValue();
     *         }
     *     }
     *
     *     String format = "&lt;b&gt;Total Holdings&lt;/b&gt;: &nbsp; ${0,number,#,##0.00}";
     *     String totalDisplay = MessageFormat.format(format, new Object[] { new Double(total) });
     *
     *     buffer.append("&lt;foot&gt;&lt;tr&gt;&lt;td colspan='4' style='text-align:right'&gt");
     *     buffer.append(totalDisplay);
     *     buffer.append("&lt/td&gt&lt/tr&gt&lt/tfoot&gt");
     * }
     * </pre>
     *
     * @param buffer the StringBuffer to render the footer row in
     */
    protected void renderFooterRow(HtmlStringBuffer buffer) {
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
            column.renderTableData(row, buffer, getContext(), rowIndex);
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
     * Render the table pagination display.
     *
     * @param buffer the StringBuffer to render the pagination display to
     */
    protected void renderPaginator(HtmlStringBuffer buffer) {
        getPaginator().render(buffer);
    }

    /**
     * Render the table banner detailing number of rows and number displayed.
     * <p/>
     * See the <tt>/click-controls.properies</tt> for the HTML templates:
     * <tt>table-page-banner</tt> and <tt>table-page-banner-nolinks</tt>
     *
     * @deprecated use {@link #renderPaginator(HtmlStringBuffer)} instead, this
     * method is provided to support backward compatibility older Click 1.4
     * customized tables. In these scenarios please override {@link #renderPaginator(HtmlStringBuffer)}
     * method to invoke {@link #renderTableBanner(HtmlStringBuffer)} and {@link #renderPagingControls(HtmlStringBuffer)}.
     *
     * @param buffer the StringBuffer to render the paging controls to
     */
    protected void renderTableBanner(HtmlStringBuffer buffer) {
        if (getShowBanner()) {
            String totalRows = String.valueOf(getRowList().size());

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
     * @deprecated use {@link #renderPaginator(HtmlStringBuffer)} instead, this
     * method is provided to support backward compatibility older Click 1.4
     * customized tables. In these scenarios please override {@link #renderPaginator(HtmlStringBuffer)}
     * method to invoke {@link #renderTableBanner(HtmlStringBuffer)} and {@link #renderPagingControls(HtmlStringBuffer)}.
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

            AbstractLink link = getControlLink();
            if (getSortedColumn() != null) {
                link.setParameter(SORT, null);
                link.setParameter(COLUMN, getSortedColumn());
                link.setParameter(ASCENDING, String.valueOf(isSortedAscending()));
            } else {
                link.setParameter(SORT, null);
                link.setParameter(COLUMN, null);
                link.setParameter(ASCENDING, null);
            }

            if (getPageNumber() > 0) {
                link.setLabel(firstLabel);
                link.setParameter(PAGE, String.valueOf(0));
                link.setAttribute("title", firstTitle);
                link.setId("control-first");
                firstLabel = link.toString();

                link.setLabel(previousLabel);
                link.setParameter(PAGE, String.valueOf(getPageNumber() - 1));
                link.setId("control-previous");
                link.setAttribute("title", previousTitle);
                previousLabel = link.toString();
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
                    link.setLabel(pageNumber);
                    link.setParameter(PAGE, String.valueOf(i));
                    link.setAttribute("title", gotoTitle + " " + pageNumber);
                    link.setId("control-" + pageNumber);
                    pagesBuffer.append(link.toString());
                }

                if (i < upperBound - 1) {
                    pagesBuffer.append(", ");
                }
            }
            String pageLinks = pagesBuffer.toString();

            if (getPageNumber() < getNumberPages() - 1) {
                link.setLabel(nextLabel);
                link.setParameter(PAGE, String.valueOf(getPageNumber() + 1));
                link.setAttribute("title", nextTitle);
                link.setId("control-next");
                nextLabel = link.toString();

                link.setLabel(lastLabel);
                link.setParameter(PAGE, String.valueOf(getNumberPages() - 1));
                link.setAttribute("title", lastTitle);
                link.setId("control-last");
                lastLabel = link.toString();
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

            Column column = (Column) getColumns().get(getSortedColumn());

            Collections.sort(getRowList(), column.getComparator());

            setSorted(true);
        }
    }

}
