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
package net.sf.click.examples.control;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.click.control.TextArea;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a HTML Rich TextArea editor control using the
 * <a href="http://developer.yahoo.com/yui/editor/">YUI editor</a>.
 * <p/>
 * To utilize this control in your application include <tt>YUI editor</tt>
 * JavaScript libraries in the web apps root directory.
 *
 * @see TextArea
 *
 * @author Malcolm Edgar
 */
public class RichTextArea extends TextArea {

    private static final long serialVersionUID = 1L;

    /** The YUI editor JavaScript import. */
    protected static final String HTML_IMPORTS =
        "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/yui/fonts/fonts-min.css\"/>\n"
        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/yui/editor/skins/sam/simpleeditor.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/yui/yahoo-dom-event/yahoo-dom-event.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/yui/element/element-beta-min.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/yui/container/container_core-min.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/yui/editor/simpleeditor-min.js\"></script>\n";

    /**
     * The textarea YUI editor theme [<tt>yui-skin-sam</tt>].
     */
    protected String theme = "yui-skin-sam";

    /**
     * The textarea YUI editor configuration. Default values are:
     * <tt>height: '300px', width: '530px', dompath: true, focusAtStart: true,
     * handleSubmit: true, titlebar:'Rich Editor'</tt>.
     */
    private String config = "height: '200px', width: '600px',"
        + "dompath: true, focusAtStart: true, handleSubmit: true, titlebar:'Rich Editor'";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a rich TextArea control with the given name.
     *
     * @param name the name of the control
     */
    public RichTextArea(String name) {
        super(name);
    }

    /**
     * Default no-args constructor used to deploy control resources.
     */
    public RichTextArea() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the textarea YUI editor configuration.
     *
     * @return the textarea YUI editor configuration
     */
    public String getConfig() {
        return config;
    }

    /**
     * Set the textarea YUI editor configuration.
     *
     * @param the textarea YUI editor configuration
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * Return the textarea YUI editor theme.
     *
     * @return the textarea YUI editor theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Return the JavaScript include: &nbsp; {@link #HTML_IMPORTS}, and YUI
     * editor JavaScript initialization code.
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        String[] args = { getContext().getRequest().getContextPath() };
        buffer.append(MessageFormat.format(HTML_IMPORTS, args));

        Map model = new HashMap();
        model.put("id", getId());
        model.put("config", getConfig());
        renderTemplate(buffer, model);

        return buffer.toString();
    }

    /**
     * Render the HTML representation of the RichTextArea.
     * <p/>
     * This method wraps the <tt>textarea</tt> in a <tt>&lt;span&gt;</tt> element
     * which is used to specify the {@link #getTheme()} of the textarea.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart("span");
        buffer.appendAttribute("class", getTheme());
        buffer.closeTag();
        buffer.append("\n");
        super.render(buffer);
        buffer.elementEnd("span");
    }

    // -------------------------------------------------------- Protected Methods

    /**
     * Render a Velocity template for the given data model.
     *
     * @param buffer the specified buffer to render the template output to
     * @param model the model data to merge with the template
     */
    protected void renderTemplate(HtmlStringBuffer buffer, Map model) {
        buffer.append(getContext().renderTemplate(RichTextArea.class, model));
    }

}
