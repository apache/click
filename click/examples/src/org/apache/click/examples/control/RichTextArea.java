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
package org.apache.click.examples.control;

import java.util.List;

import org.apache.click.control.TextArea;
import org.apache.click.element.CssImport;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a HTML Rich TextArea editor control using the
 * <a href="http://developer.yahoo.com/yui/editor/">YUI editor</a>.
 * <p/>
 * To utilize this control in your application include <tt>YUI editor</tt>
 * JavaScript libraries in the web apps root directory.
 *
 * @see TextArea
 */
public class RichTextArea extends TextArea {

    private static final long serialVersionUID = 1L;

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
     * @param config the textarea YUI editor configuration
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
     * Return the Control HEAD elements for YUI libraries and YUI
     * editor JavaScript initialization code.
     *
     * @see org.apache.click.control.Field#getHeadElements()
     */
    @Override
    public List getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new CssImport("/yui/fonts/fonts-min.css"));
            headElements.add(new CssImport("/yui/editor/skins/sam/simpleeditor.css"));
            headElements.add(new JsImport("/yui/yahoo-dom-event/yahoo-dom-event.js"));
            headElements.add(new JsImport("/yui/element/element-beta-min.js"));
            headElements.add(new JsImport("/yui/container/container_core-min.js"));
            headElements.add(new JsImport("/yui/editor/simpleeditor-min.js"));
        }

        JsScript script = new JsScript();
        script.setId(getId() + "_js_setup");

        if (!headElements.contains(script)) {
            script.setExecuteOnDomReady(true);

            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("var myConfig = {").append(getConfig()).append("};\n");
            buffer.append("var myEditor = new YAHOO.widget.SimpleEditor('");
            buffer.append(getId()).append("', myConfig);\n");
            buffer.append("if(myConfig.titlebar) {");
            buffer.append(" myEditor._defaultToolbar.titlebar=myConfig.titlebar; }\n");
            buffer.append("myEditor.render();\n");
            script.setContent(buffer.toString());
            headElements.add(script);
        }

        return headElements;
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
    @Override
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart("span");
        buffer.appendAttribute("class", getTheme());
        buffer.closeTag();
        buffer.append("\n");
        super.render(buffer);
        buffer.elementEnd("span");
    }
}
