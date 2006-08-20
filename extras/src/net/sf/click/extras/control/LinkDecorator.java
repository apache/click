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

import net.sf.click.Context;
import net.sf.click.control.AbstractLink;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Decorator;
import net.sf.click.control.PageLink;
import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;
import ognl.OgnlException;

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
 * <p/>
 * The example below uses a LinkDecorator to render the row ActionLinks in the
 * tables "Action" column.
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
 *         column.setDecorator(<span class="kw">new</span> LinkDecorator(links, <span class="st">"id"</span>));
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
 *
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

    /** The link separator string, default value is <tt>" | "</tt>. */
    protected String linkSeparator = " | ";

    /** The OGNL context map. */
    protected Map ognlContext;

    /**
     * Create a new AbstractLink table column Decorator with the given actionLink
     * and row object identifier property name.
     *
     * @param link the AbstractLink to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(AbstractLink link, String idProperty) {
        if (link == null) {
            throw new IllegalArgumentException("Null actionLink parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.linksArray = new AbstractLink[1];
        this.linksArray[0] = link;
        this.idProperty = idProperty;
    }

    /**
     * Create a new AbstractLink table column Decorator with the given
     * AbstractLinks array and row object identifier property name.
     *
     * @param links the array of AbstractLinks to render
     * @param idProperty the row object identifier property name
     */
    public LinkDecorator(AbstractLink[] links, String idProperty) {
        if (links == null) {
            throw new IllegalArgumentException("Null links parameter");
        }
        if (idProperty == null) {
            throw new IllegalArgumentException("Null idProperty parameter");
        }
        this.linksArray = links;
        this.idProperty = idProperty;
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
     * Render the given row object using the links.
     *
     * @see Decorator#render(java.lang.Object, net.sf.click.Context)
     *
     * @param row the row object to render
     * @param context the request context
     * @return the rendered links for the given row object and request context
     */
    public String render(Object row, Context context) {
        if (ognlContext == null) {
            ognlContext = new HashMap();
        }

        if (linksArray.length == 1) {
            AbstractLink link = linksArray[0];
            link.setContext(context);

            try {
                Object value = Ognl.getValue(idProperty, ognlContext, row);
                if (link instanceof ActionLink) {
                    ((ActionLink) link).setValueObject(value);

                } else if (link instanceof PageLink) {
                    ((PageLink) link).setParameter(idProperty.toString(),
                                                  value.toString());
                }

            } catch (OgnlException ognle) {
                throw new RuntimeException(ognle);
            }

            return link.toString();

        } else {
            HtmlStringBuffer buffer = new HtmlStringBuffer();

            try {
                Object value = Ognl.getValue(idProperty, ognlContext, row);

                for (int i = 0; i < linksArray.length; i++) {
                    AbstractLink link = linksArray[i];
                    link.setContext(context);

                    if (link instanceof ActionLink) {
                        ((ActionLink) link).setValueObject(value);

                    } else if (link instanceof PageLink) {
                        ((PageLink) link).setParameter(idProperty.toString(),
                                                      value.toString());
                    }

                    if (i > 0) {
                        buffer.append(getLinkSeparator());
                    }

                    buffer.append(link.toString());
                }

            } catch (OgnlException ognle) {
                throw new RuntimeException(ognle);
            }

            return buffer.toString();
        }
    }

}
