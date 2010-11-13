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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.click.Context;

import org.apache.click.Control;
import org.apache.click.control.Container;
import org.apache.click.control.Field;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an abstract convenience class that implements Container and extend Field.
 * <p/>
 * This control is only rarely used or necessary. The primary use case is for
 * creating custom Containers that will be treated as {@link org.apache.click.control.Field}
 * instances by the {@link org.apache.click.control.Form}.
 * <p/>
 * Here is an example of a FieldBorder which wraps Fields in a
 * <tt>&lt;div&gt;</tt> element. The FieldBorder container can be passed to the
 * Form and will be treated as a normal Field.
 *
 * <pre class="prettyprint">
 * public class FieldBorder extends AbstractContainerField {
 *     public FieldBorder(String name) {
 *         super(name);
 *     }
 *
 *     public String getTag() {
 *         return "div";
 *     }
 *
 *     public Control add(Field field) {
 *         return getContainer().add(field);
 *     }
 * } </pre>
 *
 * If you need to bind a request parameter to this field value, please see
 * {@link #bindRequestValue()}.
 */
public abstract class AbstractContainerField extends Field implements Container {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

    /** The list of controls. */
    protected List<Control> controls;

    /** The map of controls keyed by field name. */
    protected Map<String, Control> controlMap;

    // Constructors -----------------------------------------------------------

    /**
     * Create an AbstractContainerField with no name defined.
     */
    public AbstractContainerField() {
    }

    /**
     * Create an AbstractContainerField with the given name.
     *
     * @param name the ContainerField name
     */
    public AbstractContainerField(String name) {
        super(name);
    }

    /**
     * Construct an AbstractContainerField with the given name and label.
     *
     * @param name the name of the Field
     * @param label the label of the Field
     */
    public AbstractContainerField(String name, String label) {
        super(name, label);
    }

    // Public methods ---------------------------------------------------------

    /**
     * @see org.apache.click.control.Container#add(org.apache.click.Control).
     *
     * <b>Please note</b>: if the container contains a control with the same name
     * as the given control, that control will be
     * {@link #replace(org.apache.click.Control, org.apache.click.Control) replaced}
     * by the given control. If a control has no name defined it cannot be replaced.
     *
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null
     */
    public Control add(Control control) {
        return insert(control, getControls().size());
    }

    /**
     * Add the control to the container at the specified index, and return the
     * added instance.
     * <p/>
     * <b>Please note</b>: if the container contains a control with the same name
     * as the given control, that control will be
     * {@link #replace(org.apache.click.Control, org.apache.click.Control) replaced}
     * by the given control. If a control has no name defined it cannot be replaced.
     *
     * @see org.apache.click.control.Container#insert(org.apache.click.Control, int).
     *
     * @param control the control to add to the container and return
     * @param index the index at which the control is to be inserted
     * @return the control that was added to the container
     *
     * @throws IllegalArgumentException if the control is null or if the control
     * and container is the same instance
     */
    public Control insert(Control control, int index) {
        // Check if panel already contains the control
        String controlName = control.getName();
        if (controlName != null) {
            // Check if container already contains the control
            Control currentControl = getControlMap().get(control.getName());

            // If container already contains the control do a replace
            if (currentControl != null) {

                // Current control and new control are referencing the same object
                // so we exit early
                if (currentControl == control) {
                    return control;
                }

                // If the two controls are different objects, we remove the current
                // control and add the given control
                return replace(currentControl, control);
            }
        }

        return ContainerUtils.insert(this, control, index, getControlMap());
    }

    /**
     * @see org.apache.click.control.Container#remove(org.apache.click.Control)
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     */
    public boolean remove(Control control) {
        return ContainerUtils.remove(this, control, getControlMap());
    }

    /**
     * Replace the control in the container at the specified index, and return
     * the newly added control.
     *
     * @see org.apache.click.control.Container#replace(org.apache.click.Control, org.apache.click.Control)
     *
     * @param currentControl the control currently contained in the container
     * @param newControl the control to replace the current control contained in
     * the container
     * @return the new control that replaced the current control
     *
     * @deprecated this method was used for stateful pages, which have been deprecated
     *
     * @throws IllegalArgumentException if the currentControl or newControl is
     * null
     * @throws IllegalStateException if the currentControl is not contained in
     * the container
     */
    public Control replace(Control currentControl, Control newControl) {
        int controlIndex = getControls().indexOf(currentControl);
        return ContainerUtils.replace(this, currentControl, newControl,
            controlIndex, getControlMap());
    }

    /**
     * Return the internal container instance.
     *
     * @deprecated the internal container instance was removed,
     * AbstractContainerField can be used without accessing the internal
     * container
     *
     * @return the internal container instance
     */
    public Container getContainer() {
        return this;
    }

    /**
     * @see org.apache.click.control.Container#getControls()
     *
     * @return the sequential list of controls held by the container
     */
    public List<Control> getControls() {
        if (controls == null) {
            controls = new ArrayList<Control>();
        }
        return controls;
    }

    /**
     * @see org.apache.click.control.Container#getControl(java.lang.String)
     *
     * @param controlName the name of the control to get from the container
     * @return the named control from the container if found or null otherwise
     */
    public Control getControl(String controlName) {
        if (hasControls()) {
            return getControlMap().get(controlName);
        }
        return null;
    }

    /**
     * @see org.apache.click.control.Container#contains(org.apache.click.Control)
     *
     * @param control the control whose presence in this container is to be tested
     * @return true if the container contains the specified control
     */
    public boolean contains(Control control) {
        return getControls().contains(control);
    }

    /**
     * Returns true if this container has existing controls, false otherwise.
     *
     * @see org.apache.click.control.AbstractContainer#hasControls()
     *
     * @return true if the container has existing controls, false otherwise.
     */
    public boolean hasControls() {
        return (controls != null) && !controls.isEmpty();
    }

    /**
     * Set the parent of the Field.
     *
     * @see org.apache.click.Control#setParent(Object)
     *
     * @param parent the parent of the Control
     * @throws IllegalArgumentException if the given parent instance is
     * referencing <tt>this</tt> object: <tt>if (parent == this)</tt>
     */
    @Override
    public void setParent(Object parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Cannot set parent to itself");
        }
        this.parent = parent;
    }

    /**
     * This method does nothing by default.
     * <p/>
     * Subclasses can override this method to binds the submitted request
     * value to the Field value. For example:
     * <p/>
     * <pre class="prettyprint">
     * public CoolField extends AbstractContainerField {
     *
     *     public CoolField(String name) {
     *         super(name);
     *     }
     *
     *     public void bindRequestValue() {
     *         setValue(getRequestValue());
     *     }
     * } </pre>
     *
     * @see org.apache.click.control.Field#getRequestValue()
     */
    @Override
    public void bindRequestValue() {
    }

    /**
     * @see org.apache.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    @Override
    public boolean onProcess() {
        boolean performProcessing = true;

        if (isDisabled()) {
            Context context = getContext();

            // Switch off disabled property if field has an incoming request
            // parameter.
            if (context.hasRequestParameter(getName())) {
                setDisabled(false);

            } else {
                // If field is disabled skip processing and validation
                performProcessing = false;
            }
        }

        if (performProcessing) {
            bindRequestValue();

            if (getValidate()) {
                validate();
            }
            dispatchActionEvent();
        }

        boolean continueProcessing = true;

        if (hasControls()) {
            for (Control control : getControls()) {
                if (!control.onProcess()) {
                    continueProcessing = false;
                }
            }
        }
        return continueProcessing;
    }

    /**
     * @see org.apache.click.Control#onDestroy()
     */
    @Override
    public void onDestroy() {
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = getControls().get(i);
                try {
                    control.onDestroy();
                } catch (Throwable t) {
                    ClickUtils.getLogService().error("onDestroy error", t);
                }
            }
        }
    }

    /**
     * @see org.apache.click.Control#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = getControls().get(i);
                control.onInit();
            }
        }
    }

    /**
     * @see org.apache.click.Control#onRender()
     */
    @Override
    public void onRender() {
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = getControls().get(i);
                control.onRender();
            }
        }
    }

    /**
     * By default render the container and all its child controls to the
     * specified buffer.
     * <p/>
     * If {@link org.apache.click.control.AbstractControl#getTag()} returns null,
     * this method will render only its child controls.
     * <p/>
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
            buffer.append("\n");

        } else {

            //render only content because no tag is specified
            if (hasControls()) {
                renderContent(buffer);
            }
        }
    }

    /**
     * Returns the HTML representation of this control.
     * <p/>
     * This method delegates the rendering to the method
     * {@link #render(org.apache.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @return the HTML representation of this control
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render the Container tag and common attributes including {@link #getId() id},
     * <tt>class</tt> and <tt>style</tt>. The {@link #getName() name} attribute
     * is <em>not</em> rendered by this container.
     *
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
     * @see org.apache.click.control.AbstractControl#renderTagEnd(java.lang.String, org.apache.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
    @Override
    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd(tagName);
    }

    /**
     * Render this container content to the specified buffer.
     *
     * @see org.apache.click.control.AbstractContainer#renderContent(org.apache.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

    /**
     * Render this container children to the specified buffer.
     *
     * @see org.apache.click.control.AbstractContainer#renderChildren(org.apache.click.util.HtmlStringBuffer)
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

    /**
     * Return the map of controls where each map's key / value pair will consist
     * of the control name and instance.
     *
     * @see org.apache.click.control.AbstractContainer#getControlMap()
     *
     * @return the map of controls
     */
    protected Map<String, Control> getControlMap() {
        if (controlMap == null) {
            controlMap = new HashMap<String, Control>();
        }
        return controlMap;
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    @Override
    protected int getControlSizeEst() {
        int size = 20;

        if (getTag() != null && hasAttributes()) {
            size += 20 * getAttributes().size();
        }

        if (hasControls()) {
            size += getControls().size() * size;
        }

        return size;
    }
}
