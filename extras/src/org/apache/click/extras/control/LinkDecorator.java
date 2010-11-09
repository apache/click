/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.click.Context;
import org.apache.click.control.AbstractControl;
import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionButton;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Decorator;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.click.util.PropertyUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a table column AbstractLink and ActionButton Decorator.
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
 *
 * <h3>Multiple parameters</h3>
 * On rare occasions it is useful to add extra parameters or even replace the
 * default parameter.
 * <p/>
 * In such cases you can override the methods
 * {@link #renderActionLink(org.apache.click.util.HtmlStringBuffer, org.apache.click.control.AbstractLink, org.apache.click.Context, java.lang.Object, java.lang.Object) renderActionLink}
 * or {@link #renderActionButton(org.apache.click.util.HtmlStringBuffer, org.apache.click.control.ActionButton, org.apache.click.Context, java.lang.Object, java.lang.Object) renderActionButton}.
 * <p/>
 * In the example below we want to send both the <tt>state</tt> and <tt>postCode</tt>
 * parameters to the <tt>AddressPage</tt>:
 * <pre class="prettyprint">
 * public class SelectPostCode extends BorderPage {
 *
 *     private Table table = new Table("table");
 *
 *     public SelectPostCode() {
 *         ...
 *         PageLink selectPostCode = new PageLink("select", AddressPage.class);
 *         Column action = new Column("action");
 *         String idProperty = "postCode";
 *         LinkDecorator decorator = new LinkDecorator(table, selectPostCode, idProperty) {
 *             protected void renderActionLink(HtmlStringBuffer buffer, AbstractLink link, Context context, Object row, Object value) {
 *                 // We want to add the PostCode "state" as an extra parameter
 *                 // to the action link
 *                 link.setParameter("state", ((PostCode) row).getState());
 *
 *                 // You can manipulate the link parameters as needed, and
 *                 // even remove the default idProperty parameter.
 *                 // Object currentValue = link.getParameters().remove(this.idProperty);
 *
 *                 // NB we invoke the super implementation here for default rendering to continue
 *                 super.renderActionLink(buffer, link, context, row, value);
 *             }
 *         }
 *         action.setDecorator(decorator);
 *         table.addColumn(action);
 *     };
 *
 *     public void onRender() {
 *         table.setRowList(getPostCodeService().getPostCodes());
 *     }
 * } </pre>
 * In the above example the LinkDecorator will extract the idProperty value ("state")
 * for each object in the table.
 *
 * The PageLinks will render as follows:
 * <pre class="prettyprint">
 * &lt;a href="/mycorp/address-page.htm?postCode=6089&state=NSW&gt;Select&lt;/a&gt;
 * </pre>
 *
 * <p/>
 * This class was inspired by Richardo Lecheta's <tt>ViewDecorator</tt> design
 * pattern.
 *
 * @see org.apache.click.control.ActionLink
 * @see org.apache.click.control.PageLink
 */
public class LinkDecorator implements Decorator, Serializable {

    private static final long serialVersionUID = 1L;

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
    protected transient Map<?, ?> methodCache;

    /** An optional parameter name for the {@link #idProperty}. */
    protected String parameterName;

    /**
     * Create a new AbstractLink table column Decorator with the given actionLink
     * and row object identifier property name.
     * <p/>
     * Example usage of this constructor:
     * <pre class="prettyprint">
     * Table table = new Table("table");
     *
     * public void onInit() {
     *     ... // setup other columns
     *     ActionLink selectState = new ActionLink("select");
     *     Column action = new Column("action");
     *     String idProperty = "state";
     *     LinkDecorator decorator = new LinkDecorator(table, selectState, idProperty);
     *     action.setDecorator(decorator);
     *     table.addColumn(action);
     *     ...
     * }
     *
     * public void onRender() {
     *     // Populate the table rows with post code instances
     *     table.setRowList(getPostCodeService().getPostCodes());
     * } </pre>
     *
     * In the above example the LinkDecorator will extract the idProperty value
     * ("state") from each PostCode instance in the table.
     * <p/>
     * The idProperty value will also be used as the <tt>name</tt> of the
     * request parameter. In this example the idProperty value is "state" thus
     * the request parameter name will also be "state".
     * <p/>
     * For the PostCode "NSW" the PageLink will render as follows:
     *
     * <pre class="prettyprint">
     * &lt;a href="/mycorp/postcodes.htm?state=NSW&gt;Select&lt;/a&gt; </pre>
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

        table.add(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * AbstractLinks array and row object identifier property name.
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.AbstractLink, java.lang.String)
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

        table.add(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * AbstractLinks array, <tt>row object identifier</tt> property name and
     * <tt>parameter name</tt>.
     * <p/>
     * When the link is rendered, the <tt>parameter name</tt> is set as the
     * <tt>row object identifier</tt> parameter. For example:
     *
     * <pre class="prettyprint">
     *   // PageLink links to a Page where customers can be edited -> EditCustomerPage
     *   PageLink editLink = new PageLink("edit", EditCustomerPage.class);
     *   AbstractLink[] actions = new AbstractLink[] {editLink};
     *   Column column = new Column("id");
     *   table.addColumn(column);
     *   column.setDecorator(new LinkDecorator(table, actions, "id", "idParam")); </pre>
     *
     * If the table displayed a list of customers, the customer with <tt>id</tt>,
     * <tt>"123"</tt>, will render the following editLink:
     *
     * <pre class="codeHtml">
     * &lt;a href="/mycorp/edit-customer.htm?<span class="red">idParam=123</span>"&gt;edit&lt;/a&gt; </pre>
     *
     * If the <tt>parameter name</tt> was not specified the <tt>row object identifier</tt>
     * parameter will default to the given <tt>idProperty</tt>, in this case <tt>"id"</tt>:
     *
     * <pre class="codeHtml">
     * &lt;a href="/mycorp/edit-customer.htm?<span class="red">id=123</span>"&gt;edit&lt;/a&gt; </pre>
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.AbstractLink, java.lang.String)
     *
     * @param table the table to render the links for
     * @param links the array of AbstractLinks to render
     * @param idProperty the row object identifier property name
     * @param parameterName a parameter name for the row object identifier
     */
    public LinkDecorator(Table table, AbstractLink[] links, String idProperty,
        String parameterName) {

        this(table, links, idProperty);
        this.parameterName = parameterName;
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * ActionButton and row object identifier property name.
     * The default linkSeparator for buttons is <tt>" "</tt>.
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.AbstractLink, java.lang.String)
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

        table.add(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new ActionButton table column Decorator with the given
     * ActionButton array, <tt>row object identifier</tt> property name and
     * <tt>parameter name</tt>.
     * <p/>
     * When the button is rendered, the <tt>parameter name</tt> is set as the
     * <tt>row object identifier</tt> parameter. For example:
     *
     * <pre class="prettyprint">
     *   // PageButton links to a Page where customers can be edited -> EditCustomerPage
     *   PageButton editButton = new PageButton("edit", EditCustomerPage.class);
     *   ActionButton[] actions = new ActionButton[] {editButton};
     *   Column column = new Column("id");
     *   table.addColumn(column);
     *   column.setDecorator(new LinkDecorator(table, actions, "id", "idParam")); </pre>
     *
     * If the table displayed a list of customers, the customer with <tt>id</tt>,
     * <tt>"123"</tt>, will render the following editButton:
     *
     * <pre class="codeHtml">
     * &lt;input onclick="javascript:document.location.href='/mycorp/edit-customer.htm?actionButton=edit&value=123&<span class="red">idParam=123</span>';"/&gt; </pre>
     *
     * If the <tt>parameter name</tt> was not specified the <tt>row object identifier</tt>
     * parameter will default to {@value org.apache.click.control.ActionButton#VALUE}:
     *
     * <pre class="codeHtml">
     * &lt;input onclick="javascript:document.location.href='/mycorp/edit-customer.htm?actionButton=edit&amp;<span class="red">value=123</span>';"/&gt; </pre>
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.ActionButton, java.lang.String)
     *
     * @param table the table to render the buttons for
     * @param buttons the array of ActionButtons to render
     * @param idProperty the row object identifier property name
     * @param parameterName a parameter name for the row object identifier
     */
    public LinkDecorator(Table table, ActionButton[] buttons, String idProperty,
        String parameterName) {

        this(table, buttons, idProperty);
        this.parameterName = parameterName;
    }

    /**
     * Create a new ActionButton table column Decorator with the given
     * ActionButtons array and row object identifier property name.
     * The default linkSeparator for buttons is <tt>" "</tt>.
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.AbstractLink, java.lang.String)
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

        table.add(new LinkDecorator.PageNumberControl(table));
    }

    /**
     * Create a new table column Decorator with the given list of AbstractLinks
     * or ActionButtons and row object identifier property name.
     * The default linkSeparator for buttons are <tt>" "</tt>.
     * <p/>
     * Please note you must provide either AbstractLink objects or ActionButton
     * objects in the controls array, but not a mixture of both.
     *
     * @see LinkDecorator#LinkDecorator(org.apache.click.control.Table, org.apache.click.control.AbstractLink, java.lang.String)
     *
     * @param table the table to render the links for
     * @param controls the list of AbstractLink or ActionButtons to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(Table table, List<? extends AbstractControl> controls, String idProperty) {
        if (table == null) {
            throw new IllegalArgumentException("Null table parameter");
        }
        if (controls == null) {
            throw new IllegalArgumentException("Null controls parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.table = table;
        this.idProperty = idProperty;
        this.linkSeparator = " ";

        if (!controls.isEmpty()) {
            Object object = controls.get(0);

            if (object instanceof AbstractLink) {
                linksArray = new AbstractLink[controls.size()];
                for (int i = 0; i < controls.size(); i++) {
                    Object control = controls.get(i);
                    if (control instanceof AbstractLink) {
                        linksArray[i] = (AbstractLink) control;
                    } else {
                        String msg = "Unsupported control type: " + object.getClass()
                            + ". Must be of type AbstractLink";
                        throw new RuntimeException(msg);
                    }
                }

            } else if (object instanceof ActionButton) {
                buttonsArray = new ActionButton[controls.size()];
                for (int i = 0; i < controls.size(); i++) {
                    Object control = controls.get(i);
                    if (control instanceof ActionButton) {
                        buttonsArray[i] = (ActionButton) control;
                    } else {
                        String msg = "Unsupported control type: " + object.getClass()
                            + ". Must be of type ActionButton";
                        throw new RuntimeException(msg);
                    }
                }

            } else {
                String msg = "Unsupported control type: " + object.getClass()
                    + ". Must be of type AbstractLink or ActionButton";
                throw new RuntimeException(msg);
            }
        }

        table.add(new LinkDecorator.PageNumberControl(table));
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
     * @see Decorator#render(java.lang.Object, org.apache.click.Context)
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
            return "";
        }
    }

    // ----------------------------------------------------- Protected Methods

    /**
     * Render the given row object using the actionLinks array.
     * <p/>
     * This method initializes each link in actionLinks array by invoking
     * {@link #initLink(org.apache.click.control.AbstractLink, org.apache.click.Context, java.lang.Object)}.
     * <p/>
     * This method also renders each link in the array by
     * invoking {@link #renderActionLink(org.apache.click.util.HtmlStringBuffer, org.apache.click.control.AbstractLink, org.apache.click.Context, java.lang.Object, java.lang.Object)}.
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered links for the given row object and request context
     */
    public String renderActionLinks(Object row, Context context) {
        if (methodCache == null) {
            methodCache = new HashMap<Object, Object>();
        }

        Object value = PropertyUtils.getValue(row, idProperty, methodCache);

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        if (linksArray.length == 1) {
            AbstractLink link = linksArray[0];
            initLink(link, context, value);
            renderActionLink(buffer, link, context, row, value);

        } else {

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

                renderActionLink(buffer, link, context, row, value);
            }

        }
        return buffer.toString();
    }

    /**
     * Render the given row object using the actionButtons array.
     * <p/>
     * This method initializes each button in actionButtons array by invoking
     * {@link #initButton(org.apache.click.control.ActionButton, org.apache.click.Context, java.lang.Object)}.
     * <p/>
     * This method also renders each button in the array by
     * invoking {@link #renderActionButton(org.apache.click.util.HtmlStringBuffer, org.apache.click.control.ActionButton, org.apache.click.Context, java.lang.Object, java.lang.Object)}.
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered buttons for the given row object and request context
     */
    public String renderActionButtons(Object row, Context context) {
        if (methodCache == null) {
            methodCache = new HashMap<Object, Object>();
        }

        Object value = PropertyUtils.getValue(row, idProperty, methodCache);

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        if (buttonsArray.length == 1) {
            ActionButton button = buttonsArray[0];
            initButton(button, context, value);
            renderActionButton(buffer, button, context, row, value);

        } else {

            for (int i = 0; i < buttonsArray.length; i++) {
                ActionButton button = buttonsArray[i];
                initButton(button, context, value);

                if (i > 0) {
                    buffer.append(getLinkSeparator());
                }

                renderActionButton(buffer, button, context, row, value);
            }

        }
        return buffer.toString();
    }

    // --------------------------------------------------------- Protected methods

    /**
     * Render the link to the specified buffer.
     * <p/>
     * If this method is overridden to add extra parameters to the link,
     * remember to invoke <tt>super.renderActionLink</tt> so default rendering
     * can continue.
     *
     * @param buffer the specified buffer to render the link output to
     * @param link the link to render
     * @param context the request context
     * @param row the table row being rendered
     * @param value the value of the link
     */
    protected void renderActionLink(HtmlStringBuffer buffer, AbstractLink link,
        Context context, Object row, Object value) {
        link.render(buffer);
    }

    /**
     * Render the button to the specified buffer.
     * <p/>
     * If this method is overridden to add extra parameters to the button,
     * remember to invoke <tt>super.renderActionButton</tt> so default rendering
     * can continue.
     *
     * @see #renderActionLink(org.apache.click.util.HtmlStringBuffer, org.apache.click.control.AbstractLink, org.apache.click.Context, java.lang.Object, java.lang.Object)
     *
     * @param buffer the specified buffer to render the button output to
     * @param button the button to render
     * @param context the request context
     * @param row the table row being rendered
     * @param value the value of the button
     */
    protected void renderActionButton(HtmlStringBuffer buffer,
        ActionButton button, Context context, Object row, Object value) {
        button.render(buffer);
    }

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
            if (parameterName != null) {
                link.setParameter(parameterName, value.toString());
            }

        } else {
            if (value != null) {
                if (parameterName != null) {
                    link.setParameter(parameterName, value.toString());
                } else {
                    link.setParameter(idProperty, value.toString());
                }
            }
        }

        // PageLinks link to other pages; no need to add Table parameters
        if (link instanceof PageLink) {
            // Exit early
            return;
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
        if (parameterName != null) {
            button.setParameter(parameterName, value.toString());
        }

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
     */
    class PageNumberControl extends AbstractControl {

        private static final long serialVersionUID = 1L;

        /** The PageNumberControl's Table instance. */
        final Table table;

        /**
         * Create an PageNumberControl with the specified table.
         *
         * @param table the table instance
         */
        PageNumberControl(Table table) {
            this.table = table;
        }

        /**
         * @see org.apache.click.Control#onProcess()
         */
        @Override
        public boolean onProcess() {
            Context context = getContext();
            if (anyLinkOrButtonClicked()) {
                String pageNumber = context.getRequestParameter(Table.PAGE);

                if (StringUtils.isNotBlank(pageNumber)) {
                    table.setPageNumber(Integer.parseInt(pageNumber));
                }

                String column = context.getRequestParameter(Table.COLUMN);
                if (column != null) {
                    table.setSortedColumn(column);
                }

                String ascending = context.getRequestParameter(Table.ASCENDING);
                if (ascending != null) {
                    table.setSortedAscending("true".equals(ascending));
                }

                // Flip sorting order
                if ("true".equals(context.getRequestParameter(Table.SORT))) {
                    table.setSortedAscending(!table.isSortedAscending());
                }
            }
            return true;
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
            Context context = getContext();
            boolean clicked = false;

            //Loop over all links and check if any was clicked
            if (linksArray != null) {
                for (int i = 0; i < linksArray.length; i++) {
                    AbstractLink link = linksArray[i];
                    if (link instanceof ActionLink) {
                        ActionLink actionLink = (ActionLink) link;

                        String name = actionLink.getName();
                        if (name != null) {
                            clicked = name.equals(context.getRequestParameter(ActionLink.ACTION_LINK));
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
                        clicked = name.equals(context.getRequestParameter(ActionButton.ACTION_BUTTON));
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
