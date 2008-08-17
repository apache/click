/*
 * Copyright 2004-2008 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import java.util.Iterator;
import java.util.List;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.Container;
import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.service.FileUploadService;
import net.sf.click.util.ContainerUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an HTML Form control to create composite based forms:
 * &nbsp; &lt;form method='post'&gt;.
 * <p/>
 * {@link net.sf.click.control.Form} uses an HTML Table to layout its fields and
 * controls. HtmlForm on the other hand does not provide a layout and instead
 * relies on the developer to compose a layout programmatically.
 * <p/>
 * This allows developers to provide a more flexible and CSS friendly layout.
 * <p/>
 * See this <a href="http://www.avoka.com/click-examples/form/contact-details.htm">example</a>
 * of how HtmlForm is used to provide a custom layout.
 * <p/>
 * <b>Please note</b>, for most cases {@link Form} is the better option as
 * it provides automatic layout and error reporting.
 *
 * @author Bob Schellink
 */
public class HtmlForm extends Form {

    // -------------------------------------------------------------- Constants

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

    // --------------------------------------------------------- Public Methods

    /**
     * @see net.sf.click.control.Container#add(net.sf.click.Control).
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, the container
     * already contains a control with the same name, or if the control's parent
     * is a Page
     */
    public Control add(Control control) {
        return insert(control, getControls().size());
    }

    /**
     * @see net.sf.click.control.Container#insert(net.sf.click.Control, int)
     *
     * @param control the control to add to the container
     * @param index the index at which the control is to be inserted
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, the container
     * already contains a control with the same name, or if the control's parent
     * is a Page
     * @throws IndexOutOfBoundsException if index is out of range
     * <tt>(index &lt; 0 || index &gt; getControls().size())</tt>
     */
    public Control insert(Control control, int index) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        if (control == this) {
            throw new IllegalArgumentException("Cannot add container to itself");
        }
        int size = getControls().size();
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
                + size);
        }

        // Check if container already contains the control
        if (getControlMap().containsKey(control.getName())) {
            throw new IllegalArgumentException(
                "Container already contains control named: " + control.getName());
        }

        // Check if control already has parent
        // If parent references *this* container, there is no need to remove it
        Object parentControl = control.getParent();
        if (parentControl != null && parentControl != this) {

            // TODO perhaps throw exception instead of removing from parent
            if (parentControl instanceof Page) {
                throw new IllegalArgumentException("This control's parent is"
                    + " already set to a Page.");

            } else if (parentControl instanceof Container) {
                //remove control from parent
                ((Container) parentControl).remove(control);
            }
        }

        getControls().add(index, control);
        control.setParent(this);

        String controlName = control.getName();
        if (controlName != null) {
            getControlMap().put(controlName, control);
        }

        if (control instanceof Field) {
            ((Field) control).setForm(this);
        }

        return control;
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param field the field to add to the form
     * @param width the width of the field in table columns
     * @return the field added to this form
     * @throws UnsupportedOperationException if invoked
     */
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
    public Control add(Control control, int width) {
        throw new UnsupportedOperationException("Not supported by HtmlForm.");
    }

    /**
     * @see net.sf.click.control.Container#remove(net.sf.click.Control).
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     * @throws IllegalArgumentException if the control is null
     */
    public boolean remove(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }

        boolean contains = getControls().remove(control);

        if (contains) {
            control.setParent(null);
        }

        String controlName = control.getName();

        if (controlName != null) {
            getControlMap().remove(controlName);
        }

        if (control instanceof Field) {
            ((Field) control).setForm(null);
        }

        return contains;
    }

    /**
     * Return the ordered list of form fields, excluding buttons.
     * <p/>
     * The order of the fields is the same order they were added to the form.
     *
     * @return the ordered List of form fields, excluding buttons
     */
    public List getFieldList() {
        return ContainerUtils.getFieldsAndLabels(this);
    }

    /**
     * Return the ordered list of {@link net.sf.click.control.Button}s.
     * <p/>
     * The order of the buttons is the same order they were added to the form.
     *
     * @return the ordered list of {@link net.sf.click.control.Button}s.
     */
    public List getButtonList() {
        return ContainerUtils.getButtons(this);
    }

    /**
     * @see net.sf.click.Control#onProcess().
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {

        if (getValidate()) {
            validate();

            // If a POST error occurred exit early.
            if (hasPostError()) {
                // Remove exception to ensure other forms on Page do not
                // validate twice for same error.
                getContext().getRequest().removeAttribute(
                    FileUploadService.UPLOAD_EXCEPTION);

                return true;
            }
        }

        boolean continueProcessing = true;

        if (isFormSubmission()) {

            if (hasControls()) {
                for (Iterator it = getControls().iterator(); it.hasNext();) {
                    Control control = (Control) it.next();
                    String controlName = control.getName();
                    if (controlName == null || !controlName.startsWith(SUBMIT_CHECK)) {

                        if (!control.onProcess()) {
                            continueProcessing = false;
                        }
                    }
                }
            }

            registerActionEvent();
        }

        return continueProcessing;
    }

    /**
     * Render the HTML representation of the form and all its child
     * controls to the specified buffer.
     *
     * @see net.sf.click.control.AbstractControl#render(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the specified buffer to render the control's output to
     */
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

    // ------------------------------------------------------ Protected Methods

    /**
     * @see net.sf.click.control.AbstractControl#renderTagBegin(java.lang.String, net.sf.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
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
     * @see net.sf.click.control.AbstractContainer#renderContent(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        // Render hidden fields
        List fields = ContainerUtils.getFields(this);
        for (Iterator it = fields.iterator(); it.hasNext();) {
            Field field = (Field) it.next();
            if (field.isHidden()) {
                field.render(buffer);
                buffer.append("\n");
            }
        }
        renderChildren(buffer);
    }

    /**
     * @see net.sf.click.control.AbstractContainer#renderChildren(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = (Control) getControls().get(i);

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
     * @see net.sf.click.control.AbstractControl#getControlSizeEst()
     *
     * @param formFields the list of form fields
     * @return the estimated rendered control size in characters
     */
    protected int getFormSizeEst(List formFields) {
        return 400 + (getControls().size() * 350);
    }
}
