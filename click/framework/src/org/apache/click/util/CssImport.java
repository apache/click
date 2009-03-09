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
package org.apache.click.util;

import org.apache.click.Context;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Provides a HEAD entry for importing <tt>external</tt> Cascading Stylesheets
 * through the &lt;link&gt; element.
 * <p/>
 * Example usage:
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHtmlHeaders() {
 *         // We use lazy loading to ensure the CSS import is only added the
 *         // first time this method is called.
 *         if (htmlHeaders == null) {
 *             // Get the header entries from the super implementation
 *             htmlHeaders = super.getHtmlHeaders();
 *
 *             CssImport cssImport = new CssImport("/css/style.css");
 *             htmlHeaders.add(cssImport);
 *         }
 *         return htmlHeaders;
 *     }
 * } </pre>
 *
 * The <tt>cssImport</tt> above will be rendered as follows (assuming the context
 * path is <tt>myApp</tt>):
 * <pre class="prettyprint">
 * &lt;link type="text/css" rel="stylesheet" href="/myApp/css/style.css"/&gt; </pre>
 *
 * @author Bob Schellink
 */
public class CssImport extends HtmlHeader {

    // ----------------------------------------------------------- Constructors

    /**
     * Constructs a new CssImport link.
     */
    public CssImport() {
        this(null);
    }

    /**
     * Construct a new CssImport link with the specified <tt>href</tt> attribute.
     * <p/>
     * <b>Please note</b> if the given <tt>href</tt> begins with a <tt class="wr">"/"</tt>
     * character the href will be prefixed with the web application
     * <tt>context path</tt>.
     *
     * @param href the CSS link href attribute
     */
    public CssImport(String href) {
        setHref(href);
        setAttribute("type", "text/css");
        setAttribute("rel", "stylesheet");
    }

    // ------------------------------------------------------ Public Properties

    /**
     * Returns the Css import HTML tag: &lt;link&gt;.
     *
     * @return the Css import HTML tag: &lt;link&gt;
     */
    public String getTag() {
        return "link";
    }

    /**
     * This method always return true because CSS import must be unique based on
     * its <tt>href</tt> attribute. In other words the Page HEAD should only
     * contain a single CSS import for the specific <tt>href</tt>.
     *
     * @see HtmlHeader#isUnique()
     *
     * @return true because CSS import must unique based on its <tt>href</tt>
     * attribute
     */
    public boolean isUnique() {
        return true;
    }

    /**
     * Sets the <tt>href</tt> attribute.
     * <p/>
     * If the given <tt>href</tt> begins with a <tt class="wr">"/"</tt> character
     * the href will be prefixed with the web applications <tt>context path</tt>.
     * Note if the given href is already prefixed with the <tt>context path</tt>,
     * Click won't add it a second time.
     *
     * @param href the new href attribute
     */
    public void setHref(String href) {
        if (href != null) {
            if (href.charAt(0) == '/') {
                Context context = getContext();
                String contextPath = context.getRequest().getContextPath();

                // Guard against adding duplicate context path
                if (!href.startsWith(contextPath + '/')) {
                    HtmlStringBuffer buffer =
                        new HtmlStringBuffer(contextPath.length() + href.length());

                    // Append the context path
                    buffer.append(contextPath);
                    buffer.append(href);
                    href = buffer.toString();
                }
            }
        }
        setAttribute("href", href);
    }

    /**
     * Return the <tt>href</tt> attribute.
     *
     * @return the href attribute
     */
    public String getHref() {
        return getAttribute("href");
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the HTML representation of the CSS import to the specified buffer.
     *
     * @param buffer the buffer to render output to
     */
    public void render(HtmlStringBuffer buffer) {
        renderConditionalCommentPrefix(buffer);

        buffer.elementStart(getTag());

        buffer.appendAttribute("id", getId());
        appendAttributes(buffer);

        buffer.elementEnd();

        renderConditionalCommentSuffix(buffer);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     *
     * @param o the object with which to compare this instance with
     * @return true if the specified object is the same as this object
     */
    public boolean equals(Object o) {
        if (getHref() == null) {
            throw new IllegalStateException("'href' attribute is not defined.");
        }

        //1. Use the == operator to check if the argument is a reference to this object.
        if (o == this) {
            return true;
        }

        //2. Use the instanceof operator to check if the argument is of the correct type.
        if (!(o instanceof CssImport)) {
            return false;
        }

        //3. Cast the argument to the correct type.
        CssImport that = (CssImport) o;

        return getHref() == null ? that.getHref() == null
            : getHref().equals(that.getHref());
    }

    /**
     * @see java.lang.Object#hashCode()
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getHref()).toHashCode();
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * This operation is not supported because CSS imports is always unique
     * based on their <tt>href</tt> attribute.
     *
     * @see HtmlHeader#setUnique(boolean)
     *
     * @param unique sets whether the Css import should be unique or not
     */
    void setUnique(boolean unique) {
        throw new UnsupportedOperationException("CssImport is always"
            + " unique based on the 'href' attribute");
    }
}
