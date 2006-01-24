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
import java.util.HashMap;
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
 * Provides a Action Link control: &nbsp; &lt;a href=""&gt;&lt;/a&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <a href='' title='ActionLink Control'>Action Link</a>
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
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class ActionLink implements Control {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 8469056846426557956L;

    /** The action link parameter name: &nbsp; <tt>actionLink</tt>. */
    public static final String ACTION_LINK = "actionLink";

    /** The value parameter name: &nbsp; <tt>value</tt>. */
    public static final String VALUE = "value";

    // ----------------------------------------------------- Instance Variables

    /** The link attributes map. */
    protected Map attributes;

    /** The button is clicked. */
    protected boolean clicked;

    /** The context. */
    protected Context context;

    /** The Field disabled value. */
    protected boolean disabled;

    /** The link display label. */
    protected String label;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /** The link localized messages Map. */
    protected Map messages;

    /** The link name. */
    protected String name;

    /** The parent localized messages map. */
    protected Map parentMessages;

    /** The link title attribute, which acts as a tooltip help message. */
    protected String title;

    /** The processed link value. */
    protected String value;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an ActionLink for the given name.
     *
     * @param name the action link name
     * @throws IllegalArgumentException if the name is null
     */
    public ActionLink(String name) {
        setName(name);
    }

    /**
     * Create an ActionLink for the given name and label.
     *
     * @param name the action link name
     * @param label the action link label
     * @throws IllegalArgumentException if the name is null
     */
    public ActionLink(String name, String label) {
        setName(name);
        setLabel(label);
    }

    /**
     * Create an ActionLink for the given name, listener object and listener
     * method.
     *
     * @param name the action link name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionLink(String name, Object listener, String method) {
        setName(name);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an ActionLink for the given name, label, listener object and
     * listener method.
     *
     * @param name the action link name
     * @param label the action link label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionLink(String name, String label, Object listener,
            String method) {

        setName(name);
        setLabel(label);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an ActionLink with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public ActionLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the link HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of link HTML attribute
     * @return the link HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the link attribute with the given attribute name and value. You would
     * generally use attributes if you were creating the entire ActionLink
     * programatically and rendering it with the {@link #toString()} method.
     * <p/>
     * For example given the ActionLink:
     *
     * <pre class="codeJava">
     * ActionLink addLink = <span class="kw">new</span> ActionLink(<span class="red">"addLink"</span>, <span class="st">"Add"</span>);
     * addLink.setAttribute(<span class="st">"class"</span>, <span class="st">"table"</span>); </pre>
     *
     * And the page template:
     * <pre class="codeHtml">
     * $<span class="red">addLink</span> </pre>
     *
     * Will render the HTML as:
     * <pre class="codeHtml">
     * &lt;a href="..?actionLink=<span class="red">addLink</span>" <span class="st">class</span>=<span class="st">"table"</span>&gt;<span class="st">Add</span>&lt;/a&gt; </pre>
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (attributes == null) {
            attributes = new HashMap(5);
        }

        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }

    /**
     * Return the ActionLink attributes Map.
     *
     * @return the ActionLink attributes Map
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the ActionLink has attributes or false otherwise.
     *
     * @return true if the ActionLink has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the ActionLink was clicked, otherwise returns false.
     *
     * @return true if the ActionLink was clicked, otherwise returns false.
     */
    public boolean isClicked() {
        return clicked;
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
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;
    }

    /**
     * Return true if the ActionLink is a disabled.
     *
     * @return true if the ActionLink is a disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Set the disabled flag.
     *
     * @param disabled the disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the ActionLink anchor &lt;a&gt; tag href attribute for the
     * given value. This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @param value the ActionLink value parameter
     * @return the ActionLink HTML href attribute
     */
    public String getHref(Object value) {
        String uri = getContext().getRequest().getRequestURI();

        StringBuffer buffer =
            new StringBuffer(uri.length() + getName().length() + 40);

        buffer.append(uri);
        buffer.append("?");
        buffer.append(ACTION_LINK);
        buffer.append("=");
        buffer.append(getName());
        if (value != null) {
            buffer.append("&amp;");
            buffer.append(VALUE);
            buffer.append("=");
            buffer.append(value);
        }

        return getContext().getResponse().encodeURL(buffer.toString());
    }

    /**
     * Return the ActionLink anchor &lt;a&gt; tag href attribute value.
     *
     * @return the ActionLink anchor &lt;a&gt; tag HTML href attribute value
     */
    public String getHref() {
        return getHref(getValue());
    }

    /**
     * This method returns null.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return null
     */
    public String getHtmlImports() {
        return null;
    }

    /**
     * Return the "id" attribute value if defined, or the ActionLink name
     * otherwise.
     *
     * @see net.sf.click.Control#getId()
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
     * Return the label for the ActionLink.
     * <p/>
     * If the label value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".label"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value still cannot be found then the ActinLink name will be converted
     * into a label using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     * For examle given a <tt>OrderPage</tt> with the properties file
     * <tt>OrderPage.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">checkout</span>.label=<span class="red">Checkout</span>
     * <span class="st">checkout</span>.title=<span class="red">Proceed to Checkout</span> </pre>
     *
     * The page ActionLink code:
     * <pre class="codeJava">
     * <span class="kw">public class</span> OrderPage <span class="kw">extends</span> Page {
     *     ActionLink checkoutLink = <span class="kw">new</span> ActionLink(<span class="st">"checkout"</span>);
     *     ..
     * } </pre>
     *
     * Will render the ActionLink label and title properties as:
     * <pre class="codeHtml">
     * &lt;a href="order-page.htm?actionLink=<span class="st">checkout</span>" title="<span class="red">Proceed to Checkout</span>"&gt;<span class="red">Checkout</span>&lt;/a&gt; </pre>
     *
     * When a label value is not set, or defined in any properties files, then
     * its value will be created from the Fields name.
     * <p/>
     * For example given the ActionLink code:
     *
     * <pre class="codeJava">
     * ActionLink nameField = <span class="kw">new</span> ActionLink(<span class="st">"deleteItem"</span>);  </pre>
     *
     * Will render the ActionLink label as:
     * <pre class="codeHtml">
     * &lt;a href="order-page.htm?actionLink=<span class="st">deleteItem</span>"&gt;<span class="red">Delete Item</span>&lt;/a&gt; </pre>
     *
     * Note the ActionLink label can include raw HTML to render other elements.
     * <p/>
     * For example the configured label:
     *
     * <pre class="codeConfig">
     * <span class="st">edit</span>.label=<span class="red">&lt;img src="images/edit.png" title="Edit Item"/&gt;</span> </pre>
     *
     * Will render the ActionLink label as:
     * <pre class="codeHtml">
     * &lt;a href="..?actionLink=<span class="st">edit</span>"&gt;<span class="red">&lt;img src="images/edit.png" title="Edit Item"/&gt;</span>&lt;/a&gt; </pre>
     *
     * @return the label for the ActionLink
     */
    public String getLabel() {
        if (label == null) {
            label = getMessage(getName() + ".label");
        }
        if (label == null) {
            label = ClickUtils.toLabel(getName());
        }
        return label;
    }

    /**
     * Set the label for the ActionLink.
     *
     * @see #getLabel()
     *
     * @param label the label for the ActionLink
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

    /**
     * Return the localized message for the given key, or null if not found.
     * <p/>
     * This method will attempt to lookup the localized message in the
     * parentMessages, which by default represents the Page's resource bundle.
     * <p/>
     * If the message was not found, the this method will attempt to look up the
     * value in the <tt>/click-control.properties</tt> message properties file.
     * <p/>
     * If still not found, this method will return null.
     *
     * @param name the name of the message resource
     * @return the named localized message, or null if not found
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String message = null;

        if (getParentMessages() != null &&
            getParentMessages().containsKey(name)) {

            message = (String) getParentMessages().get(name);
        }

        if (message == null && getMessages().containsKey(name)) {
            message = (String) getMessages().get(name);
        }

        return message;
    }

    /**
     * Return a Map of localized messages for the ActionLink.
     *
     * @return a Map of localized messages for the ActionLink
     * @throws IllegalStateException if the context for the link has not be set
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
     * @see net.sf.click.Control#getName()
     *
     * @return the name of the control
     */
    public String getName() {
        return name;
    }

    /**
     * @see net.sf.click.Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
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
     * Return the 'title' attribute, or null if not defined. The title
     * attribute acts like tooltip message over the link.
     * <p/>
     * If the title value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".title"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * <p/>
     * For examle given a <tt>ItemsPage</tt> with the properties file
     * <tt>ItemPage.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">edit</span>.label=<span class="red">Edit</span>
     * <span class="st">edit</span>.title=<span class="red">Edit Item</span> </pre>
     *
     * The page ActionLink code:
     * <pre class="codeJava">
     * <span class="kw">public class</span> ItemsPage <span class="kw">extends</span> Page {
     *     ActionLink editLink = <span class="kw">new</span> ActionLink(<span class="st">"edit"</span>);
     *     ..
     * } </pre>
     *
     * Will render the ActionLink label and title properties as:
     * <pre class="codeHtml">
     * &lt;a href="items-page.htm?actionLink=<span class="st">edit</span>" title="<span class="red">Edit Item</span>"&gt;<span class="red">Edit</span>&lt;/a&gt; </pre>
     *
     * @return the 'title' attribute tooltip message
     */
    public String getTitle() {
        if (title == null) {
            title = getMessage(getName() + ".title");
        }
        return title;
    }

    /**
     * Set the 'title' attribute tooltip message.
     *
     * @see #getTitle()
     *
     * @param value the 'title' attribute tooltip message
     */
    public void setTitle(String value) {
        title = value;
    }

    /**
     * Returns the ActionLink value if the action link was processed and has
     * a value, or null otherwise.
     *
     * @return the ActionLink value if the ActionLink was processed
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the action link <tt>Double</tt> value if the action link was
     * processed and has a value, or null otherwise.
     *
     * @return the action link <tt>Double</tt> value if the action link was processed
     */
    public Double getValueDouble() {
        if (getValue() != null) {
            return Double.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionLink <tt>Integer</tt> value if the ActionLink was
     * processed and has a value, or null otherwise.
     *
     * @return the ActionLink <tt>Integer</tt> value if the action link was processed
     */
    public Integer getValueInteger() {
        if (getValue() != null) {
            return Integer.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionLink <tt>Long</tt> value if the ActionLink was
     * processed and has a value, or null otherwise.
     *
     * @return the ActionLink <tt>Long</tt> value if the action link was processed
     */
    public Long getValueLong() {
        if (getValue() != null) {
            return Long.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Set the ActionLink value.
     *
     * @param value the ActionLink value
     */
    public void setValue(String value) {
        this.value = value;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     * @throws IOException if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
    }

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionLink was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        clicked =
            getName().equals(getContext().getRequestParameter(ACTION_LINK));

        if (clicked) {
            setValue(getContext().getRequestParameter(VALUE));

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);

            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    /**
     * Return the HTML rendered anchor link string. This method
     * will render the entire anchor link including the tags, the label and
     * any attributes, see {@link #setAttribute(String, String)} for an
     * example.
     *
     * @see Object#toString()
     *
     * @return the HTML rendered anchor link string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("a");

        buffer.append(" href=\"");
        buffer.append(getHref());
        buffer.append("\"");
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("title", getTitle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        buffer.closeTag();

        buffer.append(getLabel());

        buffer.elementEnd("a");

        return buffer.toString();
    }
}
