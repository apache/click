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

/**
 *
 * @author Bob Schellink
 */
public class ResourceElement extends Element {

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

    /** The Internet Explorer conditional comment to wrap the HtmlHeader import with. */
    private String conditionalComment;

    /**
     * Indicates if Click should ensure the import is unique, default value is
     * <tt>false</tt>. <b>Note:</b> subclasses of HtmlHeader have different rules to
     * determine if unique should be true or false.
     */
    private boolean unique = false;

    // ------------------------------------------------------ Public properties

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

    // --------------------------------------------------------- Public methods

    public void render(HtmlStringBuffer buffer) {
        renderConditionalCommentPrefix(buffer);

        if (getTag() == null) {
            return;
        }
        renderTagBegin(getTag(), buffer);
        renderTagEnd(getTag(), buffer);

        renderConditionalCommentSuffix(buffer);
    }

    // ------------------------------------------------ Package Private Methods

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
