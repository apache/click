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

import java.util.HashMap;
import java.util.Map;
import org.apache.click.Context;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a base class for rendering the HEAD entries of an HTML page, for
 * example JavaScript (&lt;script&gt;) and Cascading Stylesheets (&lt;link&gt;).
 * <p/>
 * Four HTML import types are supported by default. They are:
 * <ul>
 *   <li>{@link JavascriptImport}, for importing <tt>external</tt> JavaScript
 *   using the &lt;script&gt; element.</li>
 *   <li>{@link Javascript}, for including <tt>inline</tt> JavaScript
 *   using the &lt;script&gt; element.</li>
 *   <li>{@link CssImport}, for importing <tt>external</tt> Stylesheets
 *   using the &lt;link&gt; element.</li>
 *   <li>{@link Css}, for including <tt>inline</tt> Stylesheets using the
 *   &lt;style&gt; element.</li>
 * </ul>
 *
 * <h3>Remove duplicates</h3>
 * Click will ensure that duplicate HtmlHeaders are removed by checking the
 * {@link #isUnique()} property. No matter how many Controls or Pages
 * import the same HtmlHeader, only one will be rendered if
 * {@link #isUnique()} returns <tt>true</tt>.
 * <p/>
 * The rules for defining a unique HtmlHeader is as follows:
 * <ul>
 * <li>{@link JavascriptImport} and {@link CssImport} is unique based on the
 * attributes {@link JavascriptImport#getSrc()} and {@link CssImport#getHref()}
 * respectively.</li>
 * <li>{@link Javascript} and {@link Css} is unique if their HTML
 * {@link #setId(java.lang.String) ID} attribute is set. The HTML
 * spec defines that an element's HTML ID must be unique per page.</li>
 * </ul>
 * For example:
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHtmlHeaders() {
 *         // We use lazy loading to ensure the JavaScript and CSS is only added
 *         // the first time this method is called.
 *         if (htmlHeaders== null) {
 *             // Get the header entries from the super implementation
 *             htmlHeaders = super.getHtmlHeaders();
 *
 *             JavascriptImport jsImport = new JavascriptImport("/js/mylib.js");
 *             // Click will ensure the library "/js/mylib.js" is
 *             // only included once in the Page
 *             htmlHeaders.add(jsImport);
 *
 *             Javascript script = new Javascript("alert('Hello!');");
 *             // Click won't ensure the script is unique because its ID
 *             // attribute is not defined
 *             htmlHeaders.add(script);
 *
 *             script = new Javascript("alert('Hello!');");
 *             script.setId("my-unique-script-id");
 *             // Click will ensure the script is unique because its ID attribute
 *             // is defined. Click will remove other scripts with the same ID
 *             htmlHeaders..add(script);
 *
 *             CssImport cssImport = new CssImport("/css/style.css");
 *             // Click will ensure the library "/css/style.css" is
 *             // only included once in the Page
 *             htmlHeaders.add(cssImport);
 *
 *             Css css = new Css("body { font-weight: bold; }");
 *             css.setId("my-unique-style-id");
 *             // Click will ensure the css is unique because its ID attribute
 *             // is defined. Click will remove other css styles with the same ID
 *             htmlHeaders.add(css);
 *         }
 *         return htmlHeaders;
 *     }
 * } </pre>
 *
 * <h3>Conditional comment support for Internet Explorer</h3>
 *
 * Sometimes it is necessary to provide additional Javascript and CSS for
 * Internet Explorer because it deviates quite often from the standard.
 * <p/>
 * Conditional comments allows you to wrap the import in a special comment which
 * only IE understands, meaning other browsers won't process the HtmlHeader.
 * <p/>
 * You can read more about conditional comments
 * <a target="_blank" href="http://msdn.microsoft.com/en-us/library/ms537512(VS.85).aspx#syntax">here</a>
 * and <a target="_blank" href="http://www.quirksmode.org/css/condcom.html">here</a>
 * <p/>
 * It has to be said that IE7 and up has much better support for CSS, thus
 * conditional comments are mostly used for IE6 and below.
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public List getHtmlHeaders() {
 *         // We use lazy loading to ensure the JavaScript and CSS is only added
 *         // the first time this method is called.
 *         if (htmlHeaders == null) {
 *             // Get the header entries from the super implementation
 *             htmlHeaders = super.getHtmlHeaders();
 *
 *             CssImport cssImport = new CssImport("/css/ie-style.css");
 *             // Use one of the predefined conditional comments to target IE6
 *             // and below
 *             cssImport.setConditionalComment(IE_LESS_THAN_IE7);
 *             htmlHeaders.add(cssImport);
 *
 *             cssImport = new CssImport("/css/ie-style2.css");
 *             // Use a custom predefined conditional comments to target only IE6
 *             cssImport.setConditionalComment("[if IE 6]");
 *             htmlHeaders.add(cssImport);
 *         }
 *         return htmlHeaders;
 *     }
 * } </pre>
 *
 * Click contains some predefined Conditional Comments namely
 * {@link #IF_IE}, {@link #IF_LESS_THAN_IE7} and {@link #IF_IE7}.
 *
 * <h3>Character data (CDATA) support</h3>
 *
 * Sometimes it is necessary to wrap <tt>inline</tt> {@link Javascript} and
 * {link Css} in CDATA tags. Two use cases are common for doing this:
 * <ul>
 * <li>For XML parsing: When using Ajax one often send back partial
 * XML snippets to the browser, which is parsed as valid XML. However the XML
 * parser will throw an error if the script contains special XML characters
 * such as '&amp;', '&lt;' and '&gt;'. For these situations it is recommended
 * to wrap the script content inside CDATA tags.
 * </li>
 * <li>XHTML validation: if you want to validate your site using an XHTML
 * validator e.g: <a target="_blank" href="http://validator.w3.org/">http://validator.w3.org/</a>.</li>
 * </ul>
 *
 * Here is an example of wrapping scripts and styles inside CDATA tags:
 * <pre class="prettyprint">
 * &lt;script type="text/javascript"&gt;
 *  /&lowast;&lt;![CDATA[&lowast;/
 *
 *  if(x &lt; y) alert('Hello');
 *
 *  /&lowast;]]&gt;&lowast;/
 * &lt;/script&gt;
 *
 * &lt;style type="text/css"&gt;
 *  /&lowast;&lt;![CDATA[&lowast;/
 *
 *  div &gt; p {
 *    border: 1px solid black;
 *  }
 *
 *  /&lowast;]]&gt;&lowast;/
 * &lt;/style&gt; </pre>
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
public abstract class HtmlHeader {

    // -------------------------------------------------------------- Constants

    /**
     * A predefined conditional comment to test if browser is IE. Value:
     * <tt>[if IE]</tt>.
     */
    public static final String IF_IE = "[if IE]";

    /**
     * A predefined conditional comment to test if browser is less than IE7.
     * Value: <tt>[if lt IE 7]</tt>.
     */
    public static final String IF_LESS_THAN_IE7 = "[if lt IE 7]";

    /**
     * A predefined conditional comment to test if browser is IE7. Value:
     * <tt>[if IE 7]</tt>.
     */
    public static final String IF_IE7 = "[if IE 7]";

    // -------------------------------------------------------------- Variables

    /** The HtmlHeader attributes Map. */
    private Map attributes;

    /** The Internet Explorer conditional comment to wrap the HtmlHeader import with. */
    private String conditionalComment;

    /**
     * Indicates if Click should ensure the import is unique, default value is
     * <tt>false</tt>. <b>Note:</b> subclasses of HtmlHeader have different rules to
     * determine if unique should be true or false.
     */
    private boolean unique = false;

    /**
     * Indicates if the HtmlHeader's content should be wrapped in a CDATA tag.
     * <b>Note:</b> this property only applies to HtmlHeader imports which contain
     * <tt>inline</tt> content.
     */
    private boolean characterData = false;

    // ------------------------------------------------------ Public properties

    /**
     * Returns the HtmlHeader import HTML tag, the default value is <tt>null</tt>.
     * <p/>
     * Subclasses should override this method and return the correct tag.
     *
     * @return this HtmlHeader import HTML tag
     */
    public String getTag() {
        return null;
    }

    /**
     * Return true if the HtmlHeader's content should be wrapped in CDATA tags,
     * false otherwise.
     *
     * @return true if the HtmlHeader's content should be wrapped in CDATA tags,
     * false otherwise
     */
    public boolean isCharacterData() {
        return characterData;
    }

    /**
     * Sets whether the HtmlHeader's content should be wrapped in CDATA tags or not.
     *
     * @param characterData true indicates that the HtmlHeader's content should be
     * wrapped in CDATA tags, false otherwise
     */
    public void setCharacterData(boolean characterData) {
        this.characterData = characterData;
    }

    /**
     * Return true if the HtmlHeader should be unique, false otherwise. The default
     * value is <tt>true</tt> if the {@link #getId() ID} attribute is defined,
     * false otherwise.
     *
     * @return true if the HtmlHeader should be unique, false otherwise.
     */
    public boolean isUnique() {
        String id = getId();

        // If id is defined, import will be any duplicate import found will be
        // filtered out
        if (StringUtils.isNotBlank(id)) {
            return true;
        }
        return unique;
    }

    /**
     * Return the "id" attribute value or null if no id is defined.
     *
     * @return HTML element identifier attribute "id" value or null if no id
     * is defined
     */
    public String getId() {
        return getAttribute("id");
    }

    /**
     * Set the HTML id attribute for the import with the given value.
     *
     * @param id the element HTML id attribute value to set
     */
    public void setId(String id) {
        if (id != null) {
            setAttribute("id", id);
        } else {
            getAttributes().remove("id");
        }
    }

    /**
     * Return Internal Explorer's <tt>conditional comment</tt> to wrap the HtmlHeader
     * import with.
     *
     * @return Internal Explorer's conditional comment to wrap the HtmlHeader import
     * with.
     */
    public String getConditionalComment() {
        return conditionalComment;
    }

    /**
     * Set Internet Explorer's conditional comment to wrap the HtmlHeader import with.
     *
     * @param conditionalComment Internet Explorer's conditional comment to wrap
     * the HtmlHeader import with
     */
    public void setConditionalComment(String conditionalComment) {
        this.conditionalComment = conditionalComment;
    }

    /**
     * Return the import HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of link HTML attribute
     * @return the link HTML attribute
     */
     public String getAttribute(String name) {
        if (hasAttributes()) {
            return (String) getAttributes().get(name);
        }
        return null;
    }

    /**
     * Set the import attribute with the given attribute name and value.
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getAttributes().put(name, value);
        } else {
            getAttributes().remove(name);
        }
    }

    /**
     * Return the import attributes Map.
     *
     * @return the import attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;
    }

    /**
     * Return true if the import has attributes or false otherwise.
     *
     * @return true if the import has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null) {
            return !attributes.isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Returns true if specified attribute is defined, false otherwise.
     *
     * @param name the specified attribute to check
     * @return true if name is a defined attribute
     */
    public boolean hasAttribute(String name) {
        return hasAttributes() && getAttributes().containsKey(name);
    }

    // --------------------------------------------------------- Public methods

    /**
     * Return the thread local Context.
     *
     * @return the thread local Context
     */
    public Context getContext() {
        return Context.getThreadLocalContext();
    }

    /**
     * Render the HTML representation of the HtmlHeader import to the specified buffer.
     * <p/>
     * If {@link #getTag()} returns null, this method will return an empty
     * string.
     *
     * @param buffer the specified buffer to render the import output to
     */
    public void render(HtmlStringBuffer buffer) {
        renderConditionalCommentPrefix(buffer);

        if (getTag() == null) {
            return;
        }
        renderTagBegin(getTag(), buffer);
        renderTagEnd(getTag(), buffer);

        renderConditionalCommentSuffix(buffer);
    }

    /**
     * Return the HTML string representation of the Html import.
     *
     * @return the HTML string representation of the Html import
     */
    public String toString() {
        if (getTag() == null) {
            return "";
        }
        HtmlStringBuffer buffer = new HtmlStringBuffer(getImportSizeEst());
        render(buffer);
        return buffer.toString();
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Render the specified {@link #getTag() tag} and {@link #getAttributes()}.
     * <p/>
     * <b>Please note:</b> the tag will not be closed by this method. This
     * enables callers of this method to append extra attributes as needed.
     * <p/>
     * For example the result of calling:
     * <pre class="prettyprint">
     * Field field = new TextField("mytext");
     * HtmlStringBuffer buffer = new HtmlStringBuffer();
     * field.renderTagBegin("div", buffer);
     * </pre>
     * will be:
     * <pre class="prettyprint">
     * &lt;div name="mytext" id="mytext"
     * </pre>
     * Note that the tag is not closed.
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    void renderTagBegin(String tagName, HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        buffer.appendAttribute("id", getId());
        appendAttributes(buffer);
    }

    /**
     * Closes the specified {@link #getTag() tag}.
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
    void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd();
    }

    /**
     * Return the estimated rendered import size in characters.
     *
     * @return the estimated rendered import size in characters
     */
    int getImportSizeEst() {
        int size = 0;
        if (getTag() != null && hasAttributes()) {
            //length of the markup -> </> == 3
            //1 * tag.length()
            size += 3 + getTag().length();
            //using 20 as an estimate
            size += 20 * getAttributes().size();
        }
        return size;
    }

    /**
     * Append all the HtmlHeader import attributes to the specified buffer.
     *
     * @param buffer the specified buffer to append all the attributes
     */
    void appendAttributes(HtmlStringBuffer buffer) {
        if (hasAttributes()) {
            buffer.appendAttributes(attributes);
        }
    }

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

    /**
     * Render the {@link #getConditionalComment() conditional comment} prefix
     * to the specified buffer. If the conditional comment is not defined this
     * method won't append to the buffer.
     *
     * @param buffer buffer to append the conditional comment prefix
     */
    void renderConditionalCommentPrefix(HtmlStringBuffer buffer) {
        String conditional = getConditionalComment();

        // Render IE conditional comment
        if (StringUtils.isNotBlank(conditional)) {
            buffer.append("<!--").append(conditional).append(">\n");
        }
    }

    /**
     * Render the {@link #getConditionalComment() conditional comment} suffix
     * to the specified buffer. If the conditional comment is not defined this
     * method won't append to the buffer.
     *
     * @param buffer buffer to append the conditional comment suffix
     */
    void renderConditionalCommentSuffix(HtmlStringBuffer buffer) {
        String conditional = getConditionalComment();

        // Close IE conditional comment
        if (StringUtils.isNotBlank(conditional)) {
            buffer.append("\n<![endif]-->");
        }
    }

    /**
     * This method provides backwards compatibility with the String based
     * HTML imports, to indicate whether Javascript and CSS must be unique
     * or not. The replacement functionality is provided by the html
     * {@link #setId(java.lang.String) ID} attribute.
     * <p/>
     * This property is *not* for public use and will be removed in a future
     * release. This property is only set from PageImports.
     *
     * @deprecated use {@link #setId(java.lang.String)} instead
     *
     * @param unique sets whether the HtmlHeader import should be unique or not
     */
    void setUnique(boolean unique) {
        String id = getId();

        // If id is defined, unique property cannot be set to false
        if (StringUtils.isNotBlank(id) && !unique) {
            throw new IllegalArgumentException("Cannot set unique property"
                + " to 'false' because an 'ID' attribute has been defined"
                + " which indicates the import should be unique.");
        }
        this.unique = unique;
    }
}
