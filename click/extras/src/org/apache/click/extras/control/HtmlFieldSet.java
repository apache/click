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
package org.apache.click.extras.control;

import org.apache.click.Control;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an HTML FieldSet container to create composite based fieldsets:
 * &nbsp; &lt;fieldset&gt;.
 * <p/>
 * {@link org.apache.click.control.FieldSet} uses an HTML Table to layout its fields
 * and controls. HtmlFieldSet on the other hand does not provide a layout and
 * instead relies on the developer to compose a layout programmatically.
 * <p/>
 * This allows developers to provide a more flexible and CSS friendly layout.
 * <p/>
 * You can read more about programmatic layout
 * <a href="../../../../../../user-guide/html/ch03s07.html#programmatic-layout">here</a>.
 * <p/>
 * Also see this <a href="http://click.avoka.com/click-examples/form/contact-details.htm">example</a>
 * of how HtmlFieldSet is used to provide a custom layout.
 */
public class HtmlFieldSet extends FieldSet {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Constructors -----------------------------------------------------------

    /**
     * Create a HtmlFieldSet with the given name.
     *
     * @param name the fieldset name element value
     */
    public HtmlFieldSet(String name) {
        super(name);
    }

    /**
     * Create a HtmlFieldSet with the given name and legend.
     *
     * @param name the fieldset name
     * @param legend the fieldset legend element value
     */
    public HtmlFieldSet(String name, String legend) {
        super(name, legend);
    }

    /**
     * Create a HtmlFieldSet with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public HtmlFieldSet() {
    }

    // Public Methods ---------------------------------------------------------

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param field the field to add to the fieldSet
     * @param width the width of the field in table columns
     * @return the field added to this fieldSet
     * @throws UnsupportedOperationException if invoked
     */
    @Override
    public Field add(Field field, int width) {
        throw new UnsupportedOperationException("Not supported by HtmlFieldSet.");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param control the control to add to the fieldSet
     * @param width the width of the control in table columns
     * @return the control added to this fieldSet
     * @throws UnsupportedOperationException if invoked
     */
    @Override
    public Control add(Control control, int width) {
        throw new UnsupportedOperationException("Not supported by HtmlFieldSet.");
    }

    /**
     * Render the HTML representation of the FieldSet.
     * <p/>
     * The size of buffer is determined by {@link #getControlSizeEst()}.
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {

        if (getShowBorder()) {
            renderTagBegin(getTag(), buffer);
            buffer.closeTag();
            if (hasControls()) {
                buffer.append("\n");
            }

            renderContent(buffer);

            renderTagEnd(getTag(), buffer);
        } else {
            renderChildren(buffer);
        }
    }

    /**
     * Returns the HTML representation of the FieldSet.
     * <p/>
     * The rendering of the FieldSet is delegated to
     * {@link #render(org.apache.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @see Object#toString()
     *
     * @return the HTML representation of this control
     */
    @Override
    public String toString() {
        return super.toString();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * @see org.apache.click.control.AbstractControl#renderTagBegin(java.lang.String, org.apache.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    @Override
    protected void renderTagBegin(String tagName, HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        String id = getId();
        if (id != null) {
            buffer.appendAttribute("id", id);
        }

        appendAttributes(buffer);
    }

    /**
     * @see org.apache.click.control.AbstractControl#renderTagEnd(java.lang.String, org.apache.click.util.HtmlStringBuffer).
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
    @Override
    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd(tagName);
    }

    /**
     * @see org.apache.click.control.AbstractContainer#renderContent(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        String fsLegend = getLegend();
        if (fsLegend != null && fsLegend.length() > 0) {
            buffer.elementStart("legend");
            if (hasLegendAttributes()) {
                Object legendId = getLegendAttributes().get("id");
                if (legendId != null) {
                    buffer.appendAttribute("id", legendId);
                }
                buffer.appendAttributes(getLegendAttributes());
            }
            buffer.closeTag();
            buffer.append(fsLegend);
            buffer.elementEnd("legend");
            buffer.append("\n");
        }
        renderChildren(buffer);
    }

    /**
     * Render this fieldset children to the specified buffer.
     *
     * @see #getControls()
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = getControls().get(i);

                int before = buffer.length();
                control.render(buffer);

                int after = buffer.length();
                if (before != after) {
                    buffer.append("\n");
                }
            }
        }
    }
}
