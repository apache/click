/*
 * Copyright 2004-2007 Malcolm A. Edgar
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

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Abstract Link control: &nbsp; &lt;a href=""&gt;&lt;/a&gt;.
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/links.html#h-12.2">A Links</a>
 *
 * @see ActionLink
 * @see PageLink
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public abstract class AbstractLink extends AbstractControl {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The Field disabled value. */
    protected boolean disabled;

    /**
     * The image src path attribute.  If the image src is defined then a
     * <tt>&lt;img/&gt;</tt> element will rendered inside the anchor link when
     * using the AbstractLink {@link #toString()} method.
     * <p/>
     * If the image src value is prefixed with '/' then the request context path
     * will be prefixed to the src value when rendered by the control.
     */
    protected String imageSrc;

    /** The link display label. */
    protected String label;

    /** The link parameters map. */
    protected Map parameters;

    /** The link 'tabindex' attribute. */
    protected int tabindex;

    /** The link title attribute, which acts as a tooltip help message. */
    protected String title;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an AbstractLink for the given name.
     *
     * @param name the page link name
     * @throws IllegalArgumentException if the name is null
     */
    public AbstractLink(String name) {
        setName(name);
    }

    /**
     * Create an AbstractLink with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public AbstractLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return true if the AbstractLink is a disabled.  If the link is disabled
     * it will be rendered as &lt;span&gt; element with a HTML class attribute
     * of "disabled".
     *
     * @return true if the AbstractLink is a disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Set the disabled flag. If the link is disabled it will be rendered as
     * &lt;span&gt; element with a HTML class attribute of "disabled".
     *
     * @param disabled the disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the AbstractLink anchor &lt;a&gt; tag href attribute.
     * This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @return the AbstractLink HTML href attribute
     */
    public abstract String getHref();

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
     * Return the image src path attribute. If the image src is defined then a
     * <tt>&lt;img/&gt;</tt> element will rendered inside the anchor link when
     * using the AbstractLink {@link #toString()} method.
     * <p/>
     * If the src value is prefixed with '/' then the request context path will
     * be prefixed to the src value when rendered by the control.
     *
     * @return the image src path attribute
     */
    public String getImageSrc() {
        return imageSrc;
    }

    /**
     * Set the image src path attribute. If the src value is prefixed with
     * '/' then the request context path will be prefixed to the src value when
     * rendered by the control.
     *
     * @param src the image src path attribute
     */
    public void setImageSrc(String src) {
        this.imageSrc = src;
    }

    /**
     * Return the "id" attribute value if defined, or null otherwise.
     *
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes()) {
            return getAttribute("id");
        } else {
            return null;
        }
    }

    /**
     * Return the label for the AbstractLink.
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
     * Will render the AbstractLink label and title properties as:
     * <pre class="codeHtml">
     * &lt;a href=".." title="<span class="red">Proceed to Checkout</span>"&gt;<span class="red">Checkout</span>&lt;/a&gt; </pre>
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
     * &lt;a href=".."&gt;<span class="red">Delete Item</span>&lt;/a&gt; </pre>
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
     * &lt;a href=".."&gt;<span class="red">&lt;img src="images/edit.png" title="Edit Item"/&gt;</span>&lt;/a&gt; </pre>
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
     * Return the link request parameter value for the given name, or null if
     * the parameter value does not exist.
     *
     * @param name the name of request parameter
     * @return the link request parameter value
     */
    public String getParameter(String name) {
        if (hasParameters()) {
            return (String) getParameters().get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the link parameter with the given parameter name and value. You would
     * generally use parameter if you were creating the entire AbstractLink
     * programatically and rendering it with the {@link #toString()} method.
     * <p/>
     * For example given the ActionLink:
     *
     * <pre class="codeJava">
     * PageLink editLink = <span class="kw">new</span> PageLink(<span class="st">"editLink"</span>, EditCustomer.<span class="kw">class</span>);
     * editLink.setLabel(<span class="st">"Edit Customer"</span>);
     * editLink.setParameter(<span class="st">"customerId"</span>, customerId); </pre>
     *
     * And the page template:
     * <pre class="codeHtml">
     * $<span class="red">editLink</span> </pre>
     *
     * Will render the HTML as:
     * <pre class="codeHtml">
     * &lt;a href="/mycorp/edit-customer.htm?<span class="st">customerId</span>=<span class="red">13490</span>"&gt;<span class="st">Edit Customer</span>&lt;/a&gt; </pre>
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setParameter(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getParameters().put(name, value);
        } else {
            getParameters().remove(name);
        }
    }

    /**
     * Return the AbstractLink parameters Map.
     *
     * @return the AbstractLink parameters Map
     */
    public Map getParameters() {
        if (parameters == null) {
            parameters = new HashMap(4);
        }
        return parameters;
    }

    /**
     * Return true if the AbstractLink has parameters or false otherwise.
     *
     * @return true if the AbstractLink has parameters on false otherwise
     */
    public boolean hasParameters() {
        if (parameters != null && !parameters.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the link "tabindex" attribute value.
     *
     * @return the link "tabindex" attribute value
     */
    public int getTabIndex() {
        return tabindex;
    }

    /**
     * Set the link "tabindex" attribute value.
     *
     * @param tabindex the link "tabindex" attribute value
     */
    public void setTabIndex(int tabindex) {
        this.tabindex = tabindex;
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
     * &lt;a href=".." title="<span class="red">Edit Item</span>"&gt;<span class="red">Edit</span>&lt;/a&gt; </pre>
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

    // --------------------------------------------------------- Public Methods

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
    }

    /**
     * This method does nothing. Subclasses may override this method to perform
     * additional initialization.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
    }

    /**
     * This method does nothing. Subclasses may override this method to perform
     * clean up any resources.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * Return the HTML rendered anchor link string. This method
     * will render the entire anchor link including the tags, the label and
     * any attributes, see {@link #setAttribute(String, String)} for an
     * example.
     * <p/>
     * If the image src is defined then a <tt>&lt;img/&gt;</tt> element will
     * rendered inside the anchor link instead of the label property.
     * <p/>
     * This method invokes the abstract {@link #getHref()} method.
     *
     * @see Object#toString()
     *
     * @return the HTML rendered anchor link string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        if (isDisabled()) {

            buffer.elementStart("span");

            buffer.appendAttribute("class", "disabled");

            if (hasStyles()) {
                buffer.appendStyleAttributes(getStyles());
            }

            buffer.closeTag();

            buffer.append(getLabel());

            buffer.elementEnd("span");

        } else {
            buffer.elementStart("a");

            buffer.appendAttribute("href", getHref());
            buffer.appendAttribute("id", getId());
            buffer.appendAttribute("title", getTitle());
            if (getTabIndex() > 0) {
                buffer.appendAttribute("tabindex", getTabIndex());
            }
            if (hasAttributes()) {
                buffer.appendAttributes(getAttributes());
            }
            if (isDisabled()) {
                buffer.appendAttributeDisabled();
            }
            if (hasStyles()) {
                buffer.appendStyleAttributes(getStyles());
            }

            buffer.closeTag();

            if (StringUtils.isBlank(getImageSrc())) {
                buffer.append(getLabel());

            } else {
                buffer.elementStart("img");
                buffer.appendAttribute("border", "0");
                buffer.appendAttribute("class", "link");

                if (getTitle() != null) {
                    buffer.appendAttribute("alt", getTitle());
                } else {
                    buffer.appendAttribute("alt", getLabel());
                }

                String src = getImageSrc();
                if (StringUtils.isNotBlank(src)) {
                    if (src.charAt(0) == '/') {
                        src = getContext().getRequest().getContextPath() + src;
                    }
                    buffer.appendAttribute("src", src);
                }

                buffer.elementEnd();
            }

            buffer.elementEnd("a");
        }

        return buffer.toString();
    }
}
