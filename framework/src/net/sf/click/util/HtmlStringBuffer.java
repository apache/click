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
package net.sf.click.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Provides a HTML element StringBuffer for rendering, automatically
 * escaping string values. HtmlStringBuffer is used by Click controls
 * for HTML rendering.
 * <p/>
 * For example the following code:
 * <pre class="codeJava">
 * <span class="kw">public</span> String toString() {
 *     HtmlStringBuffer buffer = <span class="kw">new</span> HtmlStringBuffer();
 *
 *     buffer.elementStart(<span class="st">"input"</span>);
 *     buffer.appendAttribute(<span class="st">"type"</span>, <span class="st">"text"</span>);
 *     buffer.appendAttribute(<span class="st">"name"</span>, getName());
 *     buffer.appendAttribute(<span class="st">"value"</span>, getValue());
 *     buffer.elementEnd();
 *
 *     <span class="kw">return</span> buffer.toString();
 * } </pre>
 *
 * Would render:
 *
 * <pre class="codeHtml">
 * &lt;input type="text" name="address" value="23 Holt's Street"/&gt; </pre>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class HtmlStringBuffer {

    /** JavaScript attribute names. */
    public static final String[] JS_ATTRIBUTES = {
       "onload", "onunload", "onclick", "ondblclick", "onmousedown",
       "onmouseup", "onmouseover", "onmousemove", "onmouseout", "onfocus",
       "onblur", "onkeypress", "onkeydown", "onkeyup", "onsubmit", "onreset",
       "onselect", "onchange"
    };

    /** The underlying StringBuffer. */
    protected final StringBuffer buffer;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new HTML StringBuffer with the specified initial
     * capacity.
     *
     * @param length the initial capacity
     */
    public HtmlStringBuffer(int length) {
        buffer = new StringBuffer(length);
    }

    /**
     * Create a new HTML StringBuffer with an initial capacity of 64
     * characters.
     */
    public HtmlStringBuffer() {
        buffer = new StringBuffer(64);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Append the raw string value of the given object to the buffer.
     *
     * @param value the object value to append
     * @throws IllegalArgumentException if the value is null
     */
    public void append(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null value parameter");
        }
        buffer.append(value);
    }

    /**
     * Append the given value to the buffer and escape its HMTL value.
     *
     * @param value the object value to append
     * @throws IllegalArgumentException if the value is null
     */
    public void appendEscaped(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Null value parameter");
        }
        buffer.append(StringEscapeUtils.escapeHtml(value.toString()));
    }

    /**
     * Append the given attribute name and value to the buffer, if the value
     * is not null.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    appendAttribute(<span class="st">"class"</span>, <span class="st">"required"</span>)  <span class="green">-></span>  <span class="st">class="required"</span> </pre>
     *
     * The attribute value will be HMTL escaped. If the attribute name is a
     * JavaScript event handler the value will not be escaped.
     *
     * @param name the HTML attribute name
     * @param value the object value to append
     * @throws IllegalArgumentException if name is null
     */
    public void appendAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        if (value != null) {
            buffer.append(" ");
            buffer.append(name);
            buffer.append("=\"");
            if (isJavaScriptAttribute(name)) {
                buffer.append(value);
            } else {
                buffer.append(StringEscapeUtils.escapeHtml(value.toString()));
            }
            buffer.append("\"");
        }
    }

    /**
     * Append the given HTML attribute name and value to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    appendAttribute(<span class="st">"size"</span>, 10</span>)  <span class="green">-></span>  <span class="st">size="10"</span> </pre>
     *
     * @param name the HTML attribute name
     * @param value the HTML attribute value
     * @throws IllegalArgumentException if name is null
     */
    public void appendAttribute(String name, int value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        buffer.append(" ");
        buffer.append(name);
        buffer.append("=\"");
        buffer.append(value);
        buffer.append("\"");
    }

    /**
     * Append the HTML "disabled" attribute to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    appendAttributeDisabled()  <span class="green">-></span>  <span class="st">disabled="disabled"</span> </pre>
     */
    public void appendAttributeDisabled() {
        buffer.append(" disabled=\"disabled\"");
    }

    /**
     * Append the HTML "readonly" attribute to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    appendAttributeReadonly()  <span class="green">-></span>  <span class="st">readonly="readonly"</span> </pre>
     */
    public void appendAttributeReadonly() {
        buffer.append(" readonly=\"readonly\"");
    }

    /**
     * Append the given map of attribute names and values to the string buffer.
     *
     * @param attributes the map of attribute names and values
     * @throws IllegalArgumentException if attributes is null
     */
    public void appendAttributes(Map attributes) {
        if (attributes == null) {
            throw new IllegalArgumentException("Null attributes parameter");
        }
        for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
            String name = i.next().toString();
            if (!name.equals("id")) {
                Object value = attributes.get(name);
                appendAttribute(name, value);
            }
        }
    }

    /**
     * Append a HTML element end to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    elementEnd(<span class="st">"textarea"</span>)  <span class="green">-></span>  <span class="st">&lt;/textarea&gt;</span> </pre>
     *
     * @param name the HTML element name to end
     * @throws IllegalArgumentException if name is null
     */
    public void elementEnd(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        buffer.append("</");
        buffer.append(name);
        buffer.append(">");
    }

    /**
     * Append a HTML element end to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    closeTag()  <span class="green">-></span>  <span class="st">&gt;</span> </pre>
     *
     */
    public void closeTag() {
        buffer.append(">");
    }

    /**
     * Append a HTML element end to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    elementEnd()  <span class="green">-></span>  <span class="st">/&gt;</span> </pre>
     *
     */
    public void elementEnd() {
        buffer.append("/>");
    }

    /**
     * Append a HTML element start to the string buffer.
     * <p/>
     * For example:
     * <pre class="javaCode">
     *    elementStart(<span class="st">"input"</span>)  <span class="green">-></span>  <span class="st">&lt;input</span> </pre>
     *
     * @param name the HTML element name to start
     */
    public void elementStart(String name) {
        buffer.append("<");
        buffer.append(name);
    }

    /**
     * Return true if the given attribute name is a JavaScript attribute,
     * or false otherwise.
     *
     * @param name the HTML attribute name to test
     * @return true if the HTML attribute is a JavaScript attribute
     */
    public boolean isJavaScriptAttribute(String name) {
        if (name.length() < 6 || name.length() > 11) {
            return false;
        }

        if (!name.startsWith("on")) {
            return false;
        }

        for (int i = 0; i < JS_ATTRIBUTES.length; i++) {
            if (JS_ATTRIBUTES[i].equalsIgnoreCase(name)) {
                return true;
            }
        }


        return false;
    }

    /**
     * Return the length of the string buffer.
     *
     * @return the length of the string buffer
     */
    public int length() {
        return buffer.length();
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return buffer.toString();
    }

}
