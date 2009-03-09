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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Provides a HEAD entry for importing <tt>inline</tt> Javascript through the
 * &lt;script&gt; element.
 * <p/>
 * Example usage:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHtmlHeaders() {
 *         // We use lazy loading to ensure the JS is only added the
 *         // first time this method is called.
 *         if (htmlHeaders == null) {
 *             // Get the header entries from the super implementation
 *             htmlHeaders = super.getHtmlHeaders();
 *
 *             Javascript javascript = new Javascript("alert('Hello World!);");
 *             htmlHeaders.add(javascript);
 *         }
 *         return htmlHeaders;
 *     }
 * } </pre>
 *
 * The <tt>javascript</tt> instance will render the output:
 *
 * <pre class="prettyprint">
 * &lt;script type="text/javascript"&gt;
 * alert('Hello World');
 * &lt;/script&gt;
 * </pre>
 *
 * Below is an example showing how to create inline Javascript from a
 * Velocity template.
 * <p/>
 * First we create a Velocity template <tt>(/js/mycorp-template.js)</tt> which
 * contains the variable <tt>$divId</tt> that must be replaced at runtime by
 * the real Div ID attribute:
 *
 * <pre class="prettyprint">
 * hide = function() {
 *     var div = document.getElementById('$divId');
 *     div.style.display = "none";
 * }
 * </pre>
 *
 * Next is the Page implementation:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHtmlHeaders() {
 *         // We use lazy loading to ensure the JS is only added the
 *         // first time this method is called.
 *         if (htmlHeaders == null) {
 *             // Get the header entries from the super implementation
 *             htmlHeaders = super.getHtmlHeaders();
 *
 *             // Create a default template model to pass to the template
 *             Map model = ClickUtils.createTemplateModel(this, getContext());
 *
 *             // Add the id of the div to hide
 *             model.put("divId", "myDiv");
 *
 *             // Specify the path to JAvascript Velocity template
 *             String jsTemplate = "/js/mycorp-template.js";
 *
 *             // Render the template providing it with the model
 *             String template = getContext().renderTemplate(jsTemplate, model);
 *
 *             // Create the inline Javascript for the given template
 *             Javascript content = new Javascript(template);
 *             htmlHeaders.add(content);
 *         }
 *         return htmlHeaders;
 *     }
 * } </pre>
 *
 * The <tt>javascript</tt> above will render as follows (assuming the context
 * path is <tt>myApp</tt>):
 *
 * <pre class="prettyprint">
 * &lt;script type="text/javascript"&gt;
 *     hide = function() {
 *         var div = document.getElementById('myDiv');
 *         div.style.display = "none";
 *     }
 * &lt;/style&gt;
 * </pre>
 *
 * @author Bob Schellink
 */
public class Javascript extends HtmlHeader {

    // -------------------------------------------------------------- Constants

    /** A buffer holding the inline CSS content. */
    private HtmlStringBuffer content = new HtmlStringBuffer();

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new inline Javascript element.
     */
    public Javascript() {
        this(null);
    }

    /**
     * Construct a new inline Javascript element with the given content.
     *
     * @param content the Javascript content
     */
    public Javascript(String content) {
        if (content != null) {
            this.content.append(content);
        }
        setAttribute("type", "text/javascript");
    }

    // ------------------------------------------------------ Public Properties

    /**
     * Returns the Javascript HTML tag: &lt;script&gt;.
     *
     * @return the Javascript HTML tag: &lt;script&gt;
     */
    public String getTag() {
        return "script";
    }

    /**
     * Return the Javascript content buffer.
     *
     * @return the Javascript content buffer
     */
    public HtmlStringBuffer getContent() {
        return content;
    }

    /**
     * Set the Javascript content buffer.
     *
     * @param content the new content buffer
     */
    public void setContent(HtmlStringBuffer content) {
        this.content = content;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Append the given Javascript string to the content buffer.
     *
     * @param content the Javascript string to append to the content
     * buffer
     */
    public void append(String content) {
        this.content.append(content);
    }

    /**
     * Render the HTML representation of the JavaScript to the specified
     * buffer.
     *
     * @param buffer the buffer to render output to
     */
    public void render(HtmlStringBuffer buffer) {

        // Render IE conditional comment if conditional comment was set
        renderConditionalCommentPrefix(buffer);

        buffer.elementStart(getTag());

        buffer.appendAttribute("id", getId());
        appendAttributes(buffer);

        buffer.closeTag();

        // Render CDATA tag if necessary
        renderCharacterDataPrefix(buffer);

        buffer.append(getContent());

        renderCharacterDataSuffix(buffer);

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
        if (!isUnique()) {
            return super.equals(o);
        }

        //1. Use the == operator to check if the argument is a reference to this object.
        if (o == this) {
            return true;
        }

        //2. Use the instanceof operator to check if the argument is of the correct type.
        if (!(o instanceof Javascript)) {
            return false;
        }

        //3. Cast the argument to the correct type.
        Javascript that = (Javascript) o;

        String id = getId();
        String thatId = that.getId();
        return id == null ? thatId == null : id.equals(thatId);
    }

    /**
     * @see java.lang.Object#hashCode()
     *
     * @return a hash code value for this object
     */
    public int hashCode() {
        if (!isUnique()) {
            return super.hashCode();
        }
        return new HashCodeBuilder(17, 37).append(getId()).toHashCode();
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * @see HtmlHeader#setUnique(boolean)
     *
     * @deprecated use {@link #setId(java.lang.String)} instead
     *
     * @param unique sets whether the HtmlHeader import should be unique or not
     */
    void setUnique(boolean unique) {
        super.setUnique(unique);

        // If CSS is unique and ID is not defined, derive the ID from the content
        if (unique && StringUtils.isBlank(getId()) && getContent().length() > 0) {
            int hash = Math.abs(getContent().toString().hashCode());
            setId(Integer.toString(hash));
        }
    }
}
