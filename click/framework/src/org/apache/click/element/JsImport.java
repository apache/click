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
package org.apache.click.element;

import org.apache.click.Context;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Provides a JavaScript HEAD element for importing <tt>external</tt> JavaScript
 * files using the &lt;script&gt; tag.
 * <p/>
 * Example usage:
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHeadElements() {
 *         // We use lazy loading to ensure the JS import is only added the
 *         // first time this method is called.
 *         if (headElements == null) {
 *             // Get the head elements from the super implementation
 *             headElements = super.getHeadElements();
 *
 *             JsImport jsImport = new JsImport("/js/js-library.js");
 *             headElements.add(jsImport);
 *         }
 *         return headElements;
 *     }
 * } </pre>
 *
 * The <tt>jsImport</tt> instance will be rendered as follows (assuming the context
 * path is <tt>myApp</tt>):
 * <pre class="prettyprint">
 * &lt;script type="text/javascript" href="/myApp/js/js-library.js"&gt;&lt;/script&gt; </pre>
 *
 * @author Bob Schellink
 */
public class JsImport extends ResourceElement {

    // ----------------------------------------------------------- Constructors

    /**
     * Constructs a new JavaScript import element.
     */
    public JsImport() {
        this(null);
    }

    /**
     * Construct a new JavaScript import element with the specified
     * <tt>src</tt> attribute.
     * <p/>
     * <b>Please note</b> if the given <tt>src</tt> begins with a
     * <tt class="wr">"/"</tt> character the src will be prefixed with the web
     * application <tt>context path</tt>.
     *
     * @param src the JavaScript import src attribute
     */
    public JsImport(String src) {
        setSrc(src);
        setAttribute("type", "text/javascript");
    }

    // ------------------------------------------------------ Public properties

    /**
     * Returns the JavaScript import HTML tag: &lt;script&gt;.
     *
     * @return the JavaScript import HTML tag: &lt;script&gt;
     */
    public String getTag() {
        return "script";
    }

    /**
     * This method always return true because a JavaScript import must be unique
     * based on its <tt>src</tt> attribute. In other words the Page HEAD should
     * only contain a single JavaScript import for the specific <tt>src</tt>.
     *
     * @see ResourceElement#isUnique()
     *
     * @return true because JavaScript import must unique based on its
     * <tt>src</tt> attribute
     */
    public boolean isUnique() {
        return true;
    }

    /**
     * Sets the <tt>src</tt> attribute.
     * <p/>
     * If the given <tt>src</tt> begins with a <tt class="wr">"/"</tt> character
     * the sr will be prefixed with the web application <tt>context path</tt>.
     * Note if the given src is already prefixed with the <tt>context path</tt>,
     * Click won't add it a second time.
     *
     * @param src the new src attribute
     */
    public void setSrc(String src) {
        if (src != null) {
            if (src.charAt(0) == '/') {
                Context context = getContext();
                String contextPath = context.getRequest().getContextPath();

                // Guard against adding duplicate context path
                if (!src.startsWith(contextPath + '/')) {
                    HtmlStringBuffer buffer =
                        new HtmlStringBuffer(contextPath.length() + src.length());

                    // Append the context path
                    buffer.append(contextPath);
                    buffer.append(src);
                    src = buffer.toString();
                }
            }
        }
        setAttribute("src", src);
    }

    /**
     * Return the <tt>src</tt> attribute.
     *
     * @return the src attribute
     */
    public String getSrc() {
        return getAttribute("src");
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Render the HTML representation of the JsImport element to the specified
     * buffer.
     *
     * @param buffer the buffer to render output to
     */
    public void render(HtmlStringBuffer buffer) {
        renderConditionalCommentPrefix(buffer);

        buffer.elementStart(getTag());

        buffer.appendAttribute("id", getId());
        appendAttributes(buffer);

        buffer.closeTag();

        buffer.elementEnd(getTag());

        renderConditionalCommentSuffix(buffer);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     *
     * @param o the object with which to compare this instance with
     * @return true if the specified object is the same as this object
     */
    public boolean equals(Object o) {
        //1. Use the == operator to check if the argument is a reference to this object.
        if (o == this) {
            return true;
        }

        //2. Use the instanceof operator to check if the argument is of the correct type.
        if (!(o instanceof JsImport)) {
            return false;
        }

        //3. Cast the argument to the correct type.
        JsImport that = (JsImport) o;

        return getSrc() == null ? that.getSrc() == null
            : getSrc().equals(that.getSrc());
    }

    /**
     * @see java.lang.Object#hashCode()
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getSrc()).toHashCode();
    }
}
