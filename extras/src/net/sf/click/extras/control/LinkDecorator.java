/*
 * Copyright 2006 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionButton;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Decorator;
import net.sf.click.control.Table;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.PropertyUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a table column AbstractLink Decorator.
 *
 * <table cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='link-decorator.png' title='LinkDecorator'/>
 * </td>
 * </tr>
 * </table>
 *
 * <h3>LinkDecorator Example</h3>
 *
 * The example below uses a LinkDecorator to render the row ActionLinks in the
 * tables "Action" column.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> EditTable <span class="kw">extends</span> BorderPage {
 *
 *     <span class="kw">public</span> Table table = <span class="kw">new</span> Table();
 *     <span class="kw">public</span> ActionLink editLink = <span class="kw">new</span> ActionLink(<span class="st">"edit"</span>, <span class="st">"Edit"</span>, <span class="kw">this</span>, <span class="st">"onEditClick"</span>);
 *     <span class="kw">public</span> ActionLink deleteLink = <span class="kw">new</span> ActionLink(<span class="st">"delete"</span>, <span class="st">"Delete"</span>, <span class="kw">this</span>, <span class="st">"onDeleteClick"</span>);
 *
 *     public EditTable() {
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"name"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"email"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"holdings"</span>));
 *         table.addColumn(<span class="kw">new</span> Column(<span class="st">"dateJoined"</span>));
 *
 *         Column column = <span class="kw">new</span> Column(<span class="st">"Action"</span>);
 *         ActionLink[] links = <span class="kw">new</span> ActionLink[]{editLink, deleteLink};
 *         column.setDecorator(<span class="kw">new</span> LinkDecorator(table, links, <span class="st">"id"</span>));
 *         table.addColumn(column);
 *
 *         deleteLink.setAttribute(<span class="st">"onclick"</span>, <span class="st">"return window.confirm('Please confirm delete');"</span>);
 *     }
 *
 *     <span class="kw">public boolean</span> onEditClick() {
 *         Integer id = editLink.getValueInteger();
 *         Customer customer = getCustomerService().getCustomer(id);
 *
 *         ..
 *     }
 *
 *     <span class="kw">public boolean</span> onDeleteClick() {
 *         Integer id = deleteLink.getValueInteger();
 *         getCustomerService().deleteCustomer(id);
 *         <span class="kw">return true</span>;
 *     }
 *
 *     <span class="kw">public void</span> onRender() {
 *         List customers = getCustomerService().getCustomersSortedByName(12);
 *         table.setRowList(customers);
 *     }
 * } </pre>
 *
 * The LinkDecorator class automatically supports table paging.
 * <p/>
 * This class was inspired by Richardo Lecheta's <tt>ViewDecorator</tt> design
 * pattern.
 *
 * @see net.sf.click.control.ActionLink
 * @see net.sf.click.control.PageLink
 *
 * @author Malcolm Edgar
 */
public class LinkDecorator implements Decorator {

    /** The row object identifier property. */
    protected String idProperty;

    /** The array of AbstractLinks to render. */
    protected AbstractLink[] linksArray;

    /** The array of ActionButtons to render. */
    protected ActionButton[] buttonsArray;

    /** The link separator string, default value is <tt>" | "</tt>. */
    protected String linkSeparator = " | ";

    /** The table to render the links for. */
    protected Table table;

    /** The method cached for rendering column values. */
    protected Map methodCache;

    /**
     * Create a new AbstractLink table column Decorator with the given actionLink
     * and row object identifier property name.
     *
     * @param table the table to render the links for
     * @param link the AbstractLink to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(Table table, AbstractLink link, String idProperty) {
        if (table == null) {
            throw new IllegalArgumentException("Null table parameter");
        }
        if (link == null) {
            throw new IllegalArgumentException("Null actionLink parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.table = table;
        this.linksArray = new AbstractLink[1];
        this.linksArray[0] = link;
        this.idProperty = idProperty;

        table.addControl(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * AbstractLinks array and row object identifier property name.
     *
     * @param table the table to render the links for
     * @param links the array of AbstractLinks to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(Table table, AbstractLink[] links, String idProperty) {
        if (table == null) {
            throw new IllegalArgumentException("Null table parameter");
        }
        if (links == null) {
            throw new IllegalArgumentException("Null links parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.table = table;
        this.linksArray = links;
        this.idProperty = idProperty;

        table.addControl(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * ActionButton and row object identifier property name.
     * The default linkSeparator for buttons is <tt>" "</tt>.
     *
     * @param table the table to render the links for
     * @param button the ActionButton to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(Table table, ActionButton button, String idProperty) {
        if (table == null) {
            throw new IllegalArgumentException("Null table parameter");
        }
        if (button == null) {
            throw new IllegalArgumentException("Null button parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.table = table;
        this.buttonsArray = new ActionButton[1];
        this.buttonsArray[0] = button;
        this.idProperty = idProperty;
        this.linkSeparator = " ";

        table.addControl(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * ActionButtons array and row object identifier property name.
     * The default linkSeparator for buttons is <tt>" "</tt>.
     *
     * @param table the table to render the links for
     * @param buttons the array of ActionButtons to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(Table table, ActionButton[] buttons, String idProperty) {
        if (table == null) {
            throw new IllegalArgumentException("Null table parameter");
        }
        if (buttons == null) {
            throw new IllegalArgumentException("Null buttons parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.table = table;
        this.buttonsArray = buttons;
        this.idProperty = idProperty;
        this.linkSeparator = " ";

        table.addControl(new LinkDecorator.PageNumberControl(table));
    }

    // ------------------------------------------------------ Public Properties

    /**
     * Return the link separator string. The default value is <tt>" | "</tt>.
     *
     * @return the link separator string.
     */
    public String getLinkSeparator() {
        return linkSeparator;
    }

    /**
     * Set the link separator string with the given value.
     *
     * @param value the link separator string value
     */
    public void setLinkSeparator(String value) {
        this.linkSeparator = value;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the given row object using the links or buttons.
     *
     * @see Decorator#render(java.lang.Object, net.sf.click.Context)
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered links for the given row object and request context
     */
    public String render(Object row, Context context) {

        if (linksArray != null) {
            return renderActionLinks(row, context);

        } else if (buttonsArray != null) {
            return renderActionButtons(row, context);

        } else {
            // Should never occur
            throw new IllegalStateException("No ActionLinks or ActionButtons defined");
        }
    }

    // ----------------------------------------------------- Protected Methods

    /**
     * Render the given row object using the actionLinks array.
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered links for the given row object and request context
     */
    public String renderActionLinks(Object row, Context context) {
        if (methodCache == null) {
            methodCache = new HashMap();
        }

        Object value = PropertyUtils.getValue(row, idProperty, methodCache);

        if (linksArray.length == 1) {
            AbstractLink link = linksArray[0];
            initLink(link, context, value);

            return link.toString();

        } else {
            HtmlStringBuffer buffer = new HtmlStringBuffer();

            for (int i = 0; i < linksArray.length; i++) {
                AbstractLink link = linksArray[i];
                initLink(link, context, value);

                if (i > 0) {
                    if (StringUtils.isBlank(link.getImageSrc())) {
                        buffer.append(getLinkSeparator());
                    } else {
                        buffer.append(" ");
                    }
                }

                buffer.append(link.toString());
            }

            return buffer.toString();
        }
    }

    /**
     * Render the given row object using the actionButtons array.
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered buttons for the given row object and request context
     */
    public String renderActionButtons(Object row, Context context) {
        if (methodCache == null) {
            methodCache = new HashMap();
        }

        Object value = PropertyUtils.getValue(row, idProperty, methodCache);

        if (buttonsArray.length == 1) {
            ActionButton button = buttonsArray[0];
            initButton(button, context, value);

            return button.toString();

        } else {
            HtmlStringBuffer buffer = new HtmlStringBuffer();

            for (int i = 0; i < buttonsArray.length; i++) {
                ActionButton button = buttonsArray[i];
                initButton(button, context, value);

                if (i > 0) {
                    buffer.append(getLinkSeparator());
                }

                buffer.append(button.toString());
            }

            return buffer.toString();
        }
    }

    // --------------------------------------------------------- Protected methods

    /**
     * Initialize the link value and parameters.
     *
     * @param link the link to initialize
     * @param context the request context
     * @param value the value of the link
     */
    protected void initLink(AbstractLink link, Context context, Object value) {
        link.setId(null);

        if (link instanceof ActionLink) {
            ((ActionLink) link).setValueObject(value);

        } else {
            if (value != null) {
                link.setParameter(idProperty, value.toString());
            }
        }

        link.setParameter(Table.PAGE, String.valueOf(table.getPageNumber()));

        if (table.getSortedColumn() != null) {
            link.setParameter(Table.COLUMN, table.getSortedColumn());
            link.setParameter(Table.ASCENDING, Boolean.toString(!table.isSortedAscending()));
            link.setParameter(Table.SORT, Boolean.toString(table.isSorted()));
        }
    }

    /**
     * Initialize the button value and parameters.
     *
     * @param button the button to initialize
     * @param context the request context
     * @param value the value of the button
     */
    protected void initButton(ActionButton button, Context context, Object value) {
        button.setValueObject(value);

        button.setParameter(Table.PAGE, String.valueOf(table.getPageNumber()));

        if (table.getSortedColumn() != null) {
            button.setParameter(Table.COLUMN, table.getSortedColumn());
            button.setParameter(Table.ASCENDING, Boolean.toString(!table.isSortedAscending()));
            button.setParameter(Table.SORT, Boolean.toString(table.isSorted()));
        }
    }

    // --------------------------------------------------------- Inner Classes

    /**
     * Add page number control for setting the table page number.
     *
     * @author Malcolm Edgar
     */
    class PageNumberControl implements Control {

        private static final long serialVersionUID = 1L;

        final Table table;

        PageNumberControl(Table table) {
            this.table = table;
        }

        public Context getContext() {
            return null;
        }

        public void setContext(Context context) {
        }

        public String getHtmlImports() {
            return null;
        }

        public String getId() {
            return null;
        }

        public void setListener(Object listener, String method) {
        }

        public Map getMessages() {
            return null;
        }

        public String getName() {
            return null;
        }

        public void setName(String name) {
        }

        public Object getParent() {
            return null;
        }

        public void setParent(Object parent) {
        }

        public void onDeploy(ServletContext servletContext) {
        }

        public void onInit() {
        }

        public boolean onProcess() {
            if (anyLinkOrButtonClicked()) {
                String pageNumber = table.getContext().getRequestParameter(Table.PAGE);

                if (StringUtils.isNotBlank(pageNumber)) {
                    table.setPageNumber(Integer.parseInt(pageNumber));
                }

                String column = table.getContext().getRequestParameter(Table.COLUMN);
                if (column != null) {
                    table.setSortedColumn(column);
                }

                String ascending = table.getContext().getRequestParameter(Table.ASCENDING);
                if (ascending != null) {
                    table.setSortedAscending("true".equals(ascending));
                }

                // Flip sorting order
                if ("true".equals(table.getContext().getRequestParameter(Table.SORT))) {
                    table.setSortedAscending(!table.isSortedAscending());
                }
            }
            return true;
        }

        public void onRender() {
        }

        public void onDestroy() {
        }

        /**
         * Query if the user clicked on a button or button contained
         * in LinkDecorator. As soon as either a button or link if found
         * that was clicked this method returns.
         *
         * @return true if a button or button of LinkDecorator was clicked,
         * false otherwise
         */
        protected boolean anyLinkOrButtonClicked() {
            boolean clicked = false;

            //Loop over all links and check if any was clicked
            if (linksArray != null) {
                for (int i = 0; i < linksArray.length; i++) {
                    AbstractLink link = linksArray[i];
                    if (link instanceof ActionLink) {
                        ActionLink actionLink = (ActionLink) link;

                        String name = actionLink.getName();
                        if (name != null) {
                            clicked = name.equals(table.getContext().getRequestParameter(ActionLink.ACTION_LINK));
                        } else {
                            throw new IllegalStateException("ActionLink name not defined");
                        }

                        if (clicked) {
                            return clicked;
                        }
                    }
                }
            }

            //Loop over all buttons and check if any was clicked
            if (buttonsArray != null) {
                for (int i = 0; i < buttonsArray.length; i++) {
                    ActionButton button = buttonsArray[i];

                    String name = button.getName();
                    if (name != null) {
                        clicked = name.equals(table.getContext().getRequestParameter(ActionButton.ACTION_BUTTON));
                    } else {
                        throw new IllegalStateException("ActionButton name not defined");
                    }

                    if (clicked) {
                        return clicked;
                    }
                }
            }
            return clicked;
        }
    }
}
