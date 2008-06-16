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
import net.sf.click.util.PageImports;

/**
 * Provides a default implementation of the {@link Container} interface,
 * to make it easier for developers to implement their own containers.
 * <p/>
 * Subclasses are expected to at least override {@link #getTag()}
 * to differentiate the container. However some containers does not map cleanly
 * to a html <em>tag</em>, in which case you can override
 * {@link #render(net.sf.click.util.HtmlStringBuffer)} for complete control
 * over the output.
 * <p/>
 * Below is an example of creating a new container:
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
            control.setParent(null);
        }

        String controlName = control.getName();

        if (controlName != null) {
            getControlMap().remove(controlName);
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
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logEvent(control, "onProcess");
                }
            }
        }

        registerListener();
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
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logEvent(control, "onDestroy");
                }
            }
        }
    }

   /**
    * @see net.sf.click.Control#onInit()
    */
    public void onInit() {
        if (hasControls()) {
            for (int i = 0, size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                control.onInit();
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logEvent(control, "onInit");
                }
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
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logEvent(control, "onRender");
                }
            }
        }
    }

   /**
    * @see net.sf.click.control.AbstractControl#onHtmlImports(net.sf.click.util.PageImports)
    *
    * @param pageImports the PageImports instance to add imports to
    */
    public void onHtmlImports(PageImports pageImports) {
        if (hasControls()) {
            for(Iterator it = getControls().iterator(); it.hasNext(); ) {
                Control control = (Control) it.next();

                if (control instanceof AbstractControl) {
                    AbstractControl abstractControl = (AbstractControl) control;
                    abstractControl.onHtmlImports(pageImports);

                    if (ClickUtils.getLogService().isTraceEnabled()) {
                       logEvent(control, "onHtmlImports");
                    }
                }
            }
        }
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

    //-------------------------------------------- protected methods

    /**
     * Return the map of controls where each map's key / value pair will consist
     * of the control name and instance.
     * <p/>
     * Controls added to the container that did not specify a {@link #name},
     * will not be included in the returned map.
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
     * @see AbstractControl#getControlSizeEst().
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
//                if (control instanceof Container) {
//                    ((Container) control).render(buffer);
//                } else if (control instanceof AbstractControl) {
//                    ((AbstractControl) control).render(buffer);
//                } else {
//                    buffer.append(control.toString());
//                }
                int after = buffer.length();
                if (before != after) {
                    buffer.append("\n");
                }
            }
        }
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Log the event being processed for the specified control.
     *
     * TODO. Not sure if this method is useful. Logging out life cycles for
     * every container will be very noisy.
     *
     * @param control the control which event is logged
     * @param event the current event being processed
     */
    private void logEvent(Control control, String event) {
        String controlClassName = control.getClass().getName();
        controlClassName = controlClassName.substring(controlClassName.
            lastIndexOf('.') + 1);
        String msg = "   invoked: '" + control.getName() + "' "
            + controlClassName + "." + event + "()";
        ClickUtils.getLogService().trace(msg);
    }
}
