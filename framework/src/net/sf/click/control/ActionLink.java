/**
 * Copyright 2004 Malcolm A. Edgar
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

import java.util.HashMap;
import java.util.Map;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a Action Link control: &nbsp; &lt;a href=""&gt;&lt;/a&gt;.
 * <p/>
 * <table class='form'><tr><td>
 * <a href='' title='ActionLink Control'>ActionLink</a>
 * </td></tr></table>
 * <p/>
 * This control can render the "href" URL attribute using 
 * {@link #getHref()}, or the entire ActionLink anchor tag using 
 * {@link #toString()}.
 * <p/>
 * ActionLink support invoking control listeners. An example of using ActionLink 
 * to call a logout method is illustrated below:<blockquote><pre>
 * public class MyPage extends Page {
 *
 *     public void onInit() {
 *         ActionLink link = new ActionLink("<font color="blue">logoutLink</font>");
 *         link.setListener(this, "<b>onLogoutClick</b>");
 *         addControl(link);
 *     }
 *
 *     public boolean <b>onLogoutClick</b>() {
 *         if (getContext().getSessionAttribute("user") != null) {
 *            getContext().getSession().invalidate();
 *         }
 *         setForward("logout");
 * 
 *         return false;
 *     }
 * }</pre></blockquote>
 * The corresponding template code is below. Note href is evaluated by Velocity
 * to {@link #getHref()}:<blockquote><pre>
 * &lt;a href="$<font color="blue">logoutLink</font>.href" title="Click to Logout"&gt;Logout&lt;/a&gt;
 * </pre></blockquote>
 * ActionLink can also support a value parameter which is accessable
 * using {@link #getValue()}. For example a products table could include rows
 * of products, each with a get product details ActionLink and add product
 * ActionLink. The ActionLinks include the product's id as a parameter to
 * the {@link #getHref(Object)} method, which is then available when the
 * control is processed:<pre>
 * &lt;table&gt;
 * #foreach( $product in $productList )
 *   &lt;tr&gt;
 *    &lt;td&gt;
 *      $product.name
 *    &lt;/td&gt;
 *    &lt;td&gt;
 *      &lt;a href="$<font color="blue">detailsLink</font>.getHref($product.id)" title="Get product information"&gt;Details&lt;/a&gt;
 *    &lt;/td&gt;
 *    &lt;td&gt;
 *      &lt;a href="$<font color="blue">addLink</font>.getHref($product.id)" title="Add to basket"&gt;Add&lt;/a&gt;
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 * #end
 * &lt;/table&gt;</pre>
 * The corresponding Page class for this template is:<blockquote><pre>
 * public class ProductsPage extends Page {
 *
 *     ActionLink addLink;
 *     ActionLink detailsLink;
 *
 *     public void onInit() {
 *         addLink = new ActionLink("<font color="blue">addLink</font>");
 *         addLink.setListener(this, "<b>onAddClick</b>");
 *         addControl(addLink);
 *
 *         detailsLink = new ActionLink("<font color="blue">detailsLink</font>");
 *         detailsLink.setListener(this, "<b>onDetailsClick</b>");
 *         addControl(detailsLink);
 *     }
 *
 *     public boolean <b>onAddClick</b>() {
 *         // Get the product clicked on by the user
 *         Integer productId = addLink.getValueInteger();
 *         Product product = ProductDatabase.getProduct(productId);
 *
 *         // Add product to basket
 *         List basket = (List) getContext().getSessionAttribute("basket");
 *         basket.add(product);
 * 
 *         return true;
 *     }
 *
 *     public boolean <b>onDetailsClick</b>() {
 *         // Get the product clicked on by the user
 *         Integer productId = detailsLink.getValueInteger();
 *         Product product = ProductDatabase.getProduct(productId);
 *
 *         // Store the product in the request and display in the details page
 *         getContext().setRequestAttribute("product", product);
 *         setForward("productDetails.html");
 * 
 *         return false;
 *     }
 *
 *     public void onGet() {
 *         // Display the list of available products
 *         List productList = ProductDatabase.getProducts();
 *         addModel("productList", productList);
 *     }
 * }</pre></blockquote>
 * 
 * <p/>
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

    /** The action link parameter name: &nbsp; <tt>actionLink</tt> */
    public static final String ACTION_LINK = "actionLink";

    /** The value parameter name: &nbsp; <tt>value</tt> */
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

    /** The link name. */
    protected String name;
    
    /** The link label. */
    protected String label;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /** The processed link value. */
    protected String value;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an action link for the given name.
     *
     * @param name the action link name
     * @throws IllegalArgumentException if the name.
     */
    public ActionLink(String name) {
        setName(name);
    }

    // --------------------------------------------------------- Public Methods
    
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
     * For example:
     * <blockquote><pre>
     * // Java code
     * ActionLink addLink = new ActionLink("<font color="blue">addLink</font>");
     * addLink.setLabel("Add");
     * addLink.setAttribute("title", "Add Product to Cart");
     * addLink.setAttribute("class", "table");
     * 
     * &lt;-- Page template --&gt;
     * $<font color="blue">addLink</font>
     *
     * &lt;-- HTML output --&gt;
     * &lt;a href='actionLink=addLink' class='table' title='Add Product to Cart'&gt;Add&lt;/a&gt;
     * </pre></blockquote>
     *
     * @param name
     * @param value
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
     * Returns true if the ActionLink was clicked, otherwise returns false.
     *
     * @return true if the ActionLink was clicked, otherwise returns false.
     */
    public boolean isClicked() {
        return clicked;
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
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;
    }
    
    /**
     * Return HTML rendering string "disabled " if the ActionLink is disabled 
     * or a blank string otherwise.
     * 
     * @see #isDisabled()
     *
     * @return HTML rendering string for the ActionLink disabled status
     */
    public String getDisabled() {
        return (disabled) ? " disabled" : "";
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
     * Return the label for the ActionLink.
     * 
     * @return the label for the ActionLink
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label for the ActionLink.
     * 
     * @param label the label for the ActionLink
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see net.sf.click.Control#setName(String)
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
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
        if (value != null) {
            return Double.valueOf(value);
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
        if (value != null) {
            return Integer.valueOf(value);
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
        if (value != null) {
            return Long.valueOf(value);
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

    /**
     * @see net.sf.click.Control#setListener(Object, String)
     */
    public void setListener(Object target, String methodName) {
        listener = target;
        listenerMethod = methodName;
    }

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionLink was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        clicked = getName().equals(getContext().getRequest().getParameter(ACTION_LINK));

        if (clicked) {
            value = getContext().getRequest().getParameter(VALUE);

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
     * Return the ActionLink anchor &lt;a&gt; tag href attribute for the
     * given value.
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

        return buffer.toString();
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
     * Return the HTML rendered anchor link string. This method
     * will render the entire anchor link including the tags, the label and
     * any attributes, see {@link #setAttribute(String, String)} for an
     * example.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(100);
        
        buffer.append("<a href='");
        buffer.append(getHref());
        buffer.append("'");
        ClickUtils.renderAttributes(attributes, buffer); 
        buffer.append(getDisabled());
        buffer.append(">");
        buffer.append(getLabel());
        buffer.append("</a>");
        
        return buffer.toString();
    }
}
