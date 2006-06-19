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

import java.util.Iterator;
import java.util.Map;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Page Link control: &nbsp; &lt;a href=""&gt;&lt;/a&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <a href='' title='PageLink Control'>Page Link</a>
 * </td></tr>
 * </table>
 *
 * This control can render the "href" URL attribute using
 * {@link #getHref()}, or the entire ActionLink anchor tag using
 * {@link #toString()}.
 * <p/>
 * ActionLink support invoking control listeners. An example of using ActionLink
 * to call a logout method is illustrated below:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> MyPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> MyPage() {
 *         ActionLink link = <span class="kw">new</span> ActionLink(<span class="st">"logoutLink"</span>);
 *         link.setListener(<span class="kw">this</span>, <span class="st">"onLogoutClick"</span>);
 *         addControl(link);
 *     }
 *
 *     <span class="kw">public boolean</span> onLogoutClick() {
 *         <span class="kw">if</span> (getContext().hasSession()) {
 *            getContext().getSession().invalidate();
 *         }
 *         setForward(<span class="st">"logout"</span>);
 *
 *         <span class="kw">return false</span>;
 *     }
 * } </pre>
 *
 * The corresponding template code is below. Note href is evaluated by Velocity
 * to {@link #getHref()}:
 *
 * <pre class="codeHtml">
 * &lt;a href="<span class="blue">$logoutLink</span>.href" title="Click to Logout"&gt;Logout&lt;/a&gt; </pre>
 *
 * ActionLink can also support a value parameter which is accessable
 * using {@link #getValue()}.
 * <p/>
 * For example a products table could include rows
 * of products, each with a get product details ActionLink and add product
 * ActionLink. The ActionLinks include the product's id as a parameter to
 * the {@link #getHref(Object)} method, which is then available when the
 * control is processed:
 *
 * <pre class="codeHtml">
 * &lt;table&gt;
 * <span class="red">#foreach</span> (<span class="blue">$product</span> <span class="red">in</span> <span class="blue">$productList</span>)
 *   &lt;tr&gt;
 *    &lt;td&gt;
 *      $product.name
 *    &lt;/td&gt;
 *    &lt;td&gt;
 *      &lt;a href="<span class="blue">$detailsLink</span>.getHref(<span class="blue">$product</span>.id)" title="Get product information"&gt;Details&lt;/a&gt;
 *    &lt;/td&gt;
 *    &lt;td&gt;
 *      &lt;a href="<span class="blue">$addLink</span>.getHref(<span class="blue">$product</span>.id)" title="Add to basket"&gt;Add&lt;/a&gt;
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 * <span class="red">#end</span>
 * &lt;/table&gt; </pre>
 *
 * The corresponding Page class for this template is:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> ProductsPage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">private</span> ActionLink addLink;
 *     <span class="kw">private</span> ActionLink detailsLink;
 *
 *     <span class="kw">public</span> ProductsPage() {
 *         addLink = <span class="kw">new</span> ActionLink(<span class="st">"addLink"</span>, <span class="kw">this</span>, <span class="st">"onAddClick"</span>);
 *         addControl(addLink);
 *
 *         detailsLink = <span class="kw">new</span> ActionLink(<span class="st">"detailsLink"</span>, <span class="kw">this</span>, <span class="st">"onDetailsClick"</span>);
 *         addControl(detailsLink);
 *     }
 *
 *     <span class="kw">public boolean</span> onAddClick() {
 *         <span class="cm">// Get the product clicked on by the user</span>
 *         Integer productId = addLink.getValueInteger();
 *         Product product = ProductDAO.getProduct(productId);
 *
 *         <span class="cm">// Add product to basket</span>
 *         List basket = (List) getContext().getSessionAttribute(<span class="st">"basket"</span>);
 *         basket.add(product);
 *
 *         <span class="kw">return true</span>;
 *     }
 *
 *     <span class="kw">public boolean</span> onDetailsClick() {
 *         <span class="cm">// Get the product clicked on by the user</span>
 *         Integer productId = detailsLink.getValueInteger();
 *         Product product = ProductDAO.getProduct(productId);
 *
 *         <span class="cm">// Store the product in the request and display in the details page</span>
 *         getContext().setRequestAttribute(<span class="st">"product"</span>, product);
 *         setForward(<span class="st">"productDetails.html"</span>);
 *
 *         <span class="kw">return false</span>;
 *     }
 *
 *     <span class="kw">public void</span> onGet() {
 *         <span class="cm">// Display the list of available products</span>
 *         List productList = ProductDAO.getProducts();
 *         addModel("<span class="blue">productList</span>", productList);
 *     }
 * } </pre>
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/links.html#h-12.2">A Links</a>
 *
 * @see AbstractLink
 * @see ActionLink
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class PageLink extends AbstractLink {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The target page class. */
    protected Class pageClass;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an PageLink for the given name.
     *
     * @param name the page link name
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(String name) {
        setName(name);
    }

    /**
     * Create an PageLink for the given name and target Page class.
     *
     * @param name the page link name
     * @param targetPage the target page class
     * @throws IllegalArgumentException if the name is null
     */
    public PageLink(String name, Class targetPage) {
        setName(name);
        if (targetPage == null) {
            throw new IllegalArgumentException("Null targetPage parameter");
        }
        pageClass = targetPage;
    }

    /**
     * Create an PageLink with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public PageLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the PageLink anchor &lt;a&gt; tag href attribute.
     * This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @return the PageLink HTML href attribute
     */
    public String getHref() {
        if (getPageClass() == null) {
            throw new IllegalStateException("target pageClass is not defined");
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append(getContext().getRequest().getContextPath());

        String pagePage = getContext().getPagePath(getPageClass());

        buffer.append(pagePage);

        if (hasParameters()) {
            buffer.append("?");

            Iterator i = getParameters().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = entry.getKey().toString();
                String value = entry.getValue().toString();

                buffer.append(name);
                buffer.append("=");
                buffer.append(value);
                if (i.hasNext()) {
                    buffer.append("&");
                }
            }
        }

        return getContext().getResponse().encodeURL(buffer.toString());
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * Return the target Page class.
     *
     * @return the target Page class
     */
    public Class getPageClass() {
        return pageClass;
    }

    /**
     * Set the target Page class. The page link href URL attribute will be
     * to the path of the target page.
     *
     * @param targetPage the target Page class
     */
    public void setPageClass(Class targetPage) {
        pageClass = targetPage;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method will return true.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

}
