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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.click.Control;
import org.apache.click.control.Container;
import org.apache.click.control.Field;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an abstract convenience class that implements Container and extend Field.
 * <p/>
 * AbstractContainerField delegates Contain specific actions to an internal
 * Container instance. You can access the Container instance through
 * {@link #getContainer()}.
 * <p/>
 * If you need to bind a request parameter to this fields value, please see
 * {@link #bindRequestValue()}.
 * <p/>
 * Here is an example of a Border Control that can wrap a Button and render
 * a <tt>div</tt> border around it.
 * <pre class="prettyprint">
 * public class ButtonBorder extends AbstractContainerField {
 *     public ButtonBorder(String name) {
 *         super(name);
 *     }
 *
 *     public String getTag() {
 *         return "div";
 *     }
 *
 *     public Control addControl(Button button) {
 *         return getContainer().addControl(button);
 *     }
 * } </pre>
 */
public abstract class AbstractContainerField extends Field implements Container {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The list of controls. */
    protected List controls;

    /** The map of controls keyed by field name. */
    protected Map controlMap;

    // ---------------------------------------------------------- Constructorrs

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

    // --------------------------------------------------------- Public methods

    /**
     * @see org.apache.click.control.Container#add(org.apache.click.Control).
     *
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     */
    public Control add(Control control) {
        return insert(control, getControls().size());
    }

    /**
     * @see org.apache.click.control.Container#insert(org.apache.click.Control, int).
     *
     * @param control the control to add to the container and return
     * @param index the index at which the control is to be inserted
     * @return the control that was added to the container
     */
    public Control insert(Control control, int index) {
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
    public List getControls() {
        if (controls == null) {
            controls = new ArrayList();
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
            return (Control) getControlMap().get(controlName);
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
        return (controls == null) ? false : !controls.isEmpty();
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
    public void setParent(Object parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Cannot set parent to itself");
        }
        this.parent = parent;
    }

    /**
     * Return the HTML head import statements for contained controls.
     *
     * @see org.apache.click.Control#getHtmlImports()
     *
     * @return the HTML includes statements for the contained control stylesheet
     * and JavaScript files
     */
    public String getHtmlImports() {
        if (hasControls()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(0);

            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                String htmlImports = control.getHtmlImports();
                if (htmlImports != null) {
                    buffer.append(htmlImports);
                }
            }
            return buffer.toString();
        }
        return null;
    }

    /**
     * This method does nothing by default.
     * <p/>
     * Subclasses should override this method to binds the submitted request
     * value to the Field's value. For example:
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
     *
     *     // Below is the actual getRequestValue implementation as defined
     *     // in Field. This is done solely to show how to retrieve the
     *     // request parameter based on the fields name.
     *     protected String getRequestValue() {
     *         String value = getContext().getRequestParameter(getName());
     *         if (value != null) {
     *             return value.trim();
     *         } else {
     *             return "";
     *         }
     *     }
     * }
     * </pre>
     *
     * Note you can use method {@link #getRequestValue()} to retrieve the
     * fields value if the request parameter is the fields name.
     */
    public void bindRequestValue() {
    }

    /**
     * @see org.apache.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        boolean continueProcessing = super.onProcess();

        if (hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control control = (Control) it.next();
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
    public void onDestroy() {
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
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
    public void onInit() {
        super.onInit();
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                control.onInit();
            }
        }
    }

    /**
     * @see org.apache.click.Control#onRender()
     */
    public void onRender() {
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
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
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    //------------------------------------------------------- Protected Methods

    /**
     * @see org.apache.click.control.AbstractControl#renderTagEnd(java.lang.String, org.apache.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to close
     * @param buffer the buffer to append the output to
     */
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
                Control control = (Control) getControls().get(i);

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
    protected Map getControlMap() {
        if (controlMap == null) {
            controlMap = new HashMap();
        }
        return controlMap;
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
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
