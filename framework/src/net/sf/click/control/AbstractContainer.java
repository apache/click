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
package net.sf.click.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import org.apache.commons.lang.ClassUtils;

/**
 * Provides a default implementation of the {@link Container} interface,
 * to make it easier for developers to implement their own containers.
 * <p/>
 * Subclasses can override {@link #getTag()} to return a specific HTML element.
 * <p/>
 * The following example shows how to create an HTML <tt>div</tt> element:
 *
 * <pre class="prettyprint">
 * public class Div extends AbstractContainer {
 *
 *     public String getTag() {
 *         // Return the HTML tag
 *         return "div";
 *     }
 * }
 * </pre>
 *
 * @author Bob Schellink
 */
public abstract class AbstractContainer extends AbstractControl implements
    Container {

    // -------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------- Instance Variables

    /** The list of controls. */
    protected List controls;

    /** The map of controls keyed by field name. */
    protected Map controlMap;

    // ------------------------------------------------------ Constructorrs

    /**
     * Create a container with no name defined.
     */
    public AbstractContainer() {
    }

    /**
     * Create a container with the given name.
     *
     * @param name the container name
     */
    public AbstractContainer(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public methods

    /**
     * @see net.sf.click.control.Container#add(net.sf.click.Control).
     *
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null or the container
     * already contains a control with the same name
     */
    public Control add(Control control) {
        return insert(control, getControls().size());
    }

    /**
     * Add the control to the container at the specified index, and return the
     * added instance.
     * <p/>
     * <b>Please note</b> if the specified control already has a parent assigned,
     * it will automatically be removed from that parent and inserted into this
     * container.
     *
     * @see net.sf.click.control.Container#insert(net.sf.click.Control, int)
     *
     * @param control the control to add to the container
     * @param index the index at which the control is to be inserted
     * @return the control that was added to the container
     *
     * @throws IllegalArgumentException if the control is null or if the control
     * and container is the same instance or the container already contains
     * a control with the same name or if a Field name is not defined
     *
     * @throws IndexOutOfBoundsException if index is out of range
     * <tt>(index &lt; 0 || index &gt; getControls().size())</tt>
     */
    public Control insert(Control control, int index) {

        // Pre conditions start
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
        if (control instanceof Field) {
            Field field = (Field) control;
            // Check if container already contains the field. Labels can share
            // names though.
            if (getControlMap().containsKey(field.getName())
                && !(field instanceof Label)) {
                String msg = "Container already contains field named: " + field.getName();
                throw new IllegalArgumentException(msg);
            }
        } else {
            // Check if container already contains the control
            if (getControlMap().containsKey(control.getName())) {
                throw new IllegalArgumentException(
                    "Container already contains control named: " + control.getName());
            }
        }
        // Pre conditions end

        // Check if control already has parent
        // If parent references *this* container, there is no need to remove it
        Object currentParent = control.getParent();
        if (currentParent != null && currentParent != this) {

            // Remove control from parent Page or Container
            if (currentParent instanceof Page) {
                ((Page) currentParent).removeControl(control);

            } else if (currentParent instanceof Container) {
                ((Container) currentParent).remove(control);
            }

            // Create warning message to users that the parent has been reset
            logParentReset(control, currentParent);
        }

        // Note: set parent first since setParent might veto further processing
        control.setParent(this);
        getControls().add(index, control);

        String controlName = control.getName();
        if (controlName != null) {
            getControlMap().put(controlName, control);
        }
        return control;
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
            // Only nullify if this container is parent. This check is for the
            // case where a Control has two parents e.g. Page and Form.
            // NOTE the current #insert logic does not allow Controls to have
            // two parents so this check might be redundant.
            if (control.getParent() == this) {
                control.setParent(null);
            }

            String controlName = control.getName();

            if (controlName != null) {
                getControlMap().remove(controlName);
            }
        }

        return contains;
    }

    /**
     * @see net.sf.click.control.Container#getControls().
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
     * @see net.sf.click.control.Container#getControl(java.lang.String)
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
     * @see net.sf.click.control.Container#contains(net.sf.click.Control)
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
     * @return true if the container has existing controls, false otherwise.
     */
    public boolean hasControls() {
        return (controls == null) ? false : !controls.isEmpty();
    }

    /**
     * Return the map of controls where each map's key / value pair will consist
     * of the control name and instance.
     * <p/>
     * Controls added to the container that did not specify a {@link #name},
     * will not be included in the returned map.
     *
     * @return the map of controls
     */
    public Map getControlMap() {
        if (controlMap == null) {
            controlMap = new HashMap();
        }
        return controlMap;
    }

    /**
     * @see AbstractControl#getControlSizeEst().
     *
     * @return the estimated rendered control size in characters
     */
    public int getControlSizeEst() {
        int size = 20;

        if (getTag() != null && hasAttributes()) {
            size += 20 * getAttributes().size();
        }

        if (hasControls()) {
            size += getControls().size() * size;
        }

        return size;
    }

    /**
     * @see net.sf.click.Control#onProcess().
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {

        boolean continueProcessing = true;

        if (hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control control = (Control) it.next();
                if (!control.onProcess()) {
                    continueProcessing = false;
                }
            }
        }

        registerActionEvent();

        return continueProcessing;
    }

    /**
     * @see net.sf.click.Control#onDestroy()
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
    * @see net.sf.click.Control#onInit()
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
    * @see net.sf.click.Control#onRender()
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
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML includes statements for the container and child Controls,
     * or null if no includes are available
     */
    public String getHtmlImports() {
        if (hasControls()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(512);
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
     * Render the HTML representation of the container and all its child
     * controls to the specified buffer.
     * <p/>
     * If {@link #getTag()} returns null, this method will render only its
     * child controls.
     * <p/>
     * @see AbstractControl#render(net.sf.click.util.HtmlStringBuffer)
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

    /**
     * Returns the HTML representation of this control.
     * <p/>
     * This method delegates the rendering to the method
     * {@link #render(net.sf.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @see Object#toString()
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * @see AbstractControl#renderTagEnd(java.lang.String, net.sf.click.util.HtmlStringBuffer).
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
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

    /**
     * Render this container children to the specified buffer.
     *
     * @see #getControls()
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

    // -------------------------------------------------------- Private Methods

    /**
     * Log a warning that the parent of the specified control will be set to
     * this container.
     *
     * @param control the control which parent is being reset
     * @param currentParent the control current parent
     */
    private void logParentReset(Control control, Object currentParent) {
        HtmlStringBuffer message = new HtmlStringBuffer();

        message.append("Changed ");
        message.append(ClassUtils.getShortClassName(control.getClass()));
        String controlId = control.getId();
        if (controlId != null) {
            message.append("[");
            message.append(controlId);
            message.append("]");
        } else {
            message.append("#");
            message.append(control.hashCode());
        }
        message.append(" parent from ");

        if (currentParent instanceof Page) {
            message.append(ClassUtils.getShortClassName(currentParent.getClass()));

        } else if (currentParent instanceof Container) {
            Container parentContainer = (Container) currentParent;

            message.append(ClassUtils.getShortClassName(parentContainer.getClass()));
            String parentId = parentContainer.getId();
            if (parentId != null) {
                message.append("[");
                message.append(parentId);
                message.append("]");
            } else {
                message.append("#");
                message.append(parentContainer.hashCode());
            }
        }

        message.append(" to ");
        message.append(ClassUtils.getShortClassName(this.getClass()));
        String id = this.getId();
        if (id != null) {
            message.append("[");
            message.append(id);
            message.append("]");
        } else {
            message.append("#");
            message.append(this.hashCode());
        }

        ClickUtils.getLogService().warn(message);
    }
}
