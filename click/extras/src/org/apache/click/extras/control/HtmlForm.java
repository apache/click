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

import java.util.List;
import org.apache.click.Control;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an HTML Form control to create composite based forms:
 * &nbsp; &lt;form method='post'&gt;.
 * <p/>
 * {@link org.apache.click.control.Form} uses an HTML Table to layout its fields and
 * controls. HtmlForm on the other hand does not provide a layout and instead
 * relies on the developer to compose a layout programmatically.
 * <p/>
 * This allows developers to provide a more flexible and CSS friendly layout.
 * <p/>
 * You can read more about programmatic layout
 * <a href="../../../../../../user-guide/html/ch03s07.html#programmatic-layout">here</a>.
 * <p/>
 * Also see this <a href="http://click.avoka.com/click-examples/form/contact-details.htm">example</a>
 * of how HtmlForm is used to provide a custom layout.
 * <p/>
 * <b>Please note</b>, for most cases {@link Form} is the better option as
 * it provides automatic layout and error reporting.
 */
public class HtmlForm extends Form {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /**
     * Create an html form with the given name.
     *
     * @param name the name of the form
     */
    public HtmlForm(String name) {
        super(name);
    }

    /**
     * Create an html form with no name.
     */
    public HtmlForm() {
    }

    // Public Methods ---------------------------------------------------------

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param field the field to add to the form
     * @param width the width of the field in table columns
     * @return the field added to this form
     * @throws UnsupportedOperationException if invoked
     */
    @Override
    public Field add(Field field, int width) {
        throw new UnsupportedOperationException("Not supported by HtmlForm.");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param control the control to add to the form
     * @param width the width of the control in table columns
     * @return the control added to this form
     * @throws UnsupportedOperationException if invoked
     */
    @Override
    public Control add(Control control, int width) {
        throw new UnsupportedOperationException("Not supported by HtmlForm.");
    }

    /**
     * Render the HTML representation of the form and all its child
     * controls to the specified buffer.
     *
     * @see org.apache.click.control.AbstractControl#render(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {

        //If tag is set, render it
        if (getTag() != null) {
            renderTagBegin(getTag(), buffer);
            buffer.closeTag();
            if (hasControls()) {
                buffer.append("\n");
            }
            renderContent(buffer);
            renderTagEnd(getTag(), buffer);

        } else {

            //render only content because no tag is specified
            renderContent(buffer);
        }
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

        buffer.appendAttribute("method", getMethod());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("action", getActionURL());
        buffer.appendAttribute("enctype", getEnctype());

        appendAttributes(buffer);
    }

    /**
     * @see org.apache.click.control.AbstractContainer#renderContent(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    @Override
    protected void renderContent(HtmlStringBuffer buffer) {
        // Render hidden fields
        List<Field> fields = ContainerUtils.getInputFields(this);
        for (Field field : fields) {
            if (field.isHidden()) {
                field.render(buffer);
                buffer.append("\n");
            }
        }
        renderChildren(buffer);
    }

    /**
     * @see org.apache.click.control.AbstractContainer#renderChildren(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    @Override
    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = getControls().get(i);

                // Don't render hidden fields again.
                if (control instanceof Field) {
                    Field field = (Field) control;
                    if (field.isHidden()) {
                        continue;
                    }
                }
                int before = buffer.length();
                control.render(buffer);
                int after = buffer.length();
                if (before != after) {
                    buffer.append("\n");
                }
            }
        }
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @param formFields the list of form fields
     * @return the estimated rendered control size in characters
     */
    @Override
    protected int getFormSizeEst(List<Field> formFields) {
        return 400 + (getControls().size() * 350);
    }
}
