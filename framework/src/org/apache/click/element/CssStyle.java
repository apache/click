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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Bob Schellink
 */
public class CssStyle extends ResourceElement {

     // -------------------------------------------------------------- Variables

    /** A buffer holding the inline CSS content. */
    private HtmlStringBuffer content = new HtmlStringBuffer();

    /**
     * Indicates if the HtmlHeader's content should be wrapped in a CDATA tag.
     * <b>Note:</b> this property only applies to HtmlHeader imports which contain
     * <tt>inline</tt> content.
     */
    private boolean characterData = false;

    // ------------------------------------------------------------ Constructor

    /**
     * Construct a new CSS Style element.
     */
    public CssStyle() {
        this(null);
    }

    /**
     * Construct a new CSS Style element with the given content.
     *
     * @param content the CSS content
     */
    public CssStyle(String content) {
        if (content != null) {
            this.content.append(content);
        }
        setAttribute("type", "text/css");
        setAttribute("rel", "stylesheet");
    }

    // ------------------------------------------------------ Public properties

    /**
     * Returns the Css HTML tag: &lt;style&gt;.
     *
     * @return the Css HTML tag: &lt;style&gt;
     */
    public String getTag() {
        return "style";
    }

    /**
     * Return the CSS content buffer.
     *
     * @return the CSS content buffer
     */
    public HtmlStringBuffer getContent() {
        return content;
    }

    /**
     * Set the CSS content buffer.
     *
     * @param content the new content buffer
     */
    public void setContent(HtmlStringBuffer content) {
        this.content = content;
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

    // --------------------------------------------------------- Public Methods

    /**
     * Append the given CSS string to the content buffer.
     *
     * @param content the CSS string to append to the content buffer
     */
    public void append(String content) {
        this.content.append(content);
    }

    /**
     * Render the HTML representation of the CSS to the specified buffer.
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
        if (!(o instanceof CssStyle)) {
            return false;
        }

        //3. Cast the argument to the correct type.
        CssStyle that = (CssStyle) o;

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
            int hash = getContent().toString().hashCode();
            setId(Integer.toString(hash));
        }
    }
}
