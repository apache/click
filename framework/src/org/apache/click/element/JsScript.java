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

import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Provides a HEAD element for including <tt>inline</tt> JavaScript using the
 * &lt;script&gt; tag.
 * <p/>
 * Example usage:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHeadElements() {
 *         // We use lazy loading to ensure the JS is only added the
 *         // first time this method is called.
 *         if (headElements == null) {
 *             // Get the head elements from the super implementation
 *             headElements = super.getHeadElements();
 *
 *             JsScript jsScript = new JsScript("alert('Hello World!);");
 *             headElements.add(jsScript);
 *         }
 *         return headElements;
 *     }
 * } </pre>
 *
 * The <tt>jsScript</tt> instance will be rendered as follows:
 *
 * <pre class="prettyprint">
 * &lt;script type="text/javascript"&gt;
 * alert('Hello World');
 * &lt;/script&gt; </pre>
 *
 * Below is an example showing how to render inline Javascript from a
 * Velocity template.
 * <p/>
 * First we create a Velocity template <tt>(/js/mycorp-template.js)</tt> which
 * contains the variable <tt>$divId</tt> that must be replaced at runtime with
 * the real Div ID attribute:
 *
 * <pre class="prettyprint">
 * hide = function() {
 *     var div = document.getElementById('$divId');
 *     div.style.display = "none";
 * } </pre>
 *
 * Next is the Page implementation:
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHeadElements() {
 *         // We use lazy loading to ensure the JS is only added the
 *         // first time this method is called.
 *         if (headElements == null) {
 *             // Get the head elements from the super implementation
 *             headElements = super.getHeadElements();
 *
 *             // Create a default template model to pass to the template
 *             Map model = ClickUtils.createTemplateModel(this, getContext());
 *
 *             // Add the id of the div to hide
 *             model.put("divId", "myDiv");
 *
 *             // Specify the path to the JavaScript Velocity template
 *             String jsTemplate = "/js/mycorp-template.js";
 *
 *             // Render the template providing it with the model
 *             String template = getContext().renderTemplate(jsTemplate, model);
 *
 *             // Create the inline JavaScript for the given template
 *             JsScript jsScript = new JsScript(template);
 *             headElements.add(jsScript);
 *         }
 *         return headElements;
 *     }
 * } </pre>
 *
 * The <tt>jsScript</tt> instance will render as follows (assuming the context
 * path is <tt>myApp</tt>):
 *
 * <pre class="prettyprint">
 * &lt;script type="text/javascript"&gt;
 *     hide = function() {
 *         var div = document.getElementById('myDiv');
 *         div.style.display = "none";
 *     }
 * &lt;/style&gt; </pre>
 *
 * <h3>Character data (CDATA) support</h3>
 *
 * Sometimes it is necessary to wrap <tt>inline</tt> {@link JsScript} in CDATA
 * tags. Two use cases are common for doing this:
 * <ul>
 * <li>For XML parsing: When using Ajax one often send back partial
 * XML snippets to the browser, which is parsed as valid XML. However the XML
 * parser will throw an error if the script contains reserved XML characters
 * such as '&amp;', '&lt;' and '&gt;'. For these situations it is recommended
 * to wrap the script content inside CDATA tags.
 * </li>
 * <li>XHTML validation: if you want to validate your site using an XHTML
 * validator e.g: <a target="_blank" href="http://validator.w3.org/">http://validator.w3.org/</a>.</li>
 * </ul>
 *
 * To wrap the JavaScript content in CDATA tags, set
 * {@link #setCharacterData(boolean)} to true. Below is shown how the JavaScript
 * content would be rendered:
 *
 * <pre class="codeHtml">
 * &lt;script type="text/javascript"&gt;
 *  <span style="color:#3F7F5F">/&lowast;&lt;![CDATA[&lowast;/</span>
 *
 *  if(x &lt; y) alert('Hello');
 *
 *  <span style="color:#3F7F5F">/&lowast;]]&gt;&lowast;/</span>
 * &lt;/script&gt; </pre>
 *
 * Notice the CDATA tags are commented out which ensures older browsers that
 * don't understand the CDATA tag, will ignore it and only process the actual
 * content.
 * <p/>
 * For an overview of XHTML validation and CDATA tags please see
 * <a target="_blank" href="http://javascript.about.com/library/blxhtml.htm">http://javascript.about.com/library/blxhtml.htm</a>.
 *
 * @author Bob Schellink
 */
public class JsScript extends ResourceElement {

    // -------------------------------------------------------------- Variables

    /** A buffer holding the inline JavaScript content. */
    private HtmlStringBuffer content = new HtmlStringBuffer();

    /**
     * Indicates if the JsScript's content should be wrapped in a CDATA tag.
     */
    private boolean characterData = false;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new inline JavaScript element.
     */
    public JsScript() {
        this(null);
    }

    /**
     * Construct a new inline JavaScript element with the given content.
     *
     * @param content the JavaScript content
     */
    public JsScript(String content) {
        if (content != null) {
            this.content.append(content);
        }
        setAttribute("type", "text/javascript");
    }

    // ------------------------------------------------------ Public Properties

    /**
     * Returns the JavaScript HTML tag: &lt;script&gt;.
     *
     * @return the JavaScript HTML tag: &lt;script&gt;
     */
    public String getTag() {
        return "script";
    }

    /**
     * Return the JavaScript content buffer.
     *
     * @return the JavaScript content buffer
     */
    public HtmlStringBuffer getContent() {
        return content;
    }

    /**
     * Set the JavaScript content buffer.
     *
     * @param content the new content buffer
     */
    public void setContent(HtmlStringBuffer content) {
        this.content = content;
    }

    /**
     * Return true if the JsScript's content should be wrapped in CDATA tags,
     * false otherwise.
     *
     * @return true if the JsScript's content should be wrapped in CDATA tags,
     * false otherwise
     */
    public boolean isCharacterData() {
        return characterData;
    }

    /**
     * Sets whether the JsScript's content should be wrapped in CDATA tags or not.
     *
     * @param characterData true indicates that the JsScript's content should be
     * wrapped in CDATA tags, false otherwise
     */
    public void setCharacterData(boolean characterData) {
        this.characterData = characterData;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Append the given JavaScript string to the content buffer.
     *
     * @param content the JavaScript string to append to the content
     * buffer
     * @return the JavaScript content buffer
     */
    public HtmlStringBuffer append(String content) {
        return this.content.append(content);
    }

    /**
     * Render the HTML representation of the JsScript element to the specified
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

        renderContent(buffer);

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
        if (!(o instanceof JsScript)) {
            return false;
        }

        //3. Cast the argument to the correct type.
        JsScript that = (JsScript) o;

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

    // ------------------------------------------------------ Protected Methods

    /**
     * Render this JsScript content to the specified buffer.
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        buffer.append(getContent());
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Render the CDATA tag prefix to the specified buffer if
     * {@link #isCharacterData()} returns true. The default value is
     * <tt>/&lowast;&lt;![CDATA[&lowast;/</tt>.
     *
     * @param buffer buffer to append the conditional comment prefix
     */
    void renderCharacterDataPrefix(HtmlStringBuffer buffer) {
        // Wrap character data in CDATA block
        if (isCharacterData()) {
            buffer.append("/*<![CDATA[*/ ");
        }
    }

    /**
     * Render the CDATA tag suffix to the specified buffer if
     * {@link #isCharacterData()} returns true. The default value is
     * <tt>/&lowast;]]&gt;&lowast;/</tt>.
     *
     * @param buffer buffer to append the conditional comment prefix
     */
    void renderCharacterDataSuffix(HtmlStringBuffer buffer) {
        if (isCharacterData()) {
            buffer.append(" /*]]>*/");
        }
    }
}
