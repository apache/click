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

import java.util.List;
import java.util.Map;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an abstract convenience class that implements {@link Container} and
 * extend {@link Field}.
 * <p/>
 * An internal {@link Container} implementation is used to delegate the
 * container methods to. The container can be accessed through
 * {@link getContainer()}.
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
 *
 * @author Bob Schellink
 */
public abstract class AbstractContainerField extends Field implements Container {

    // -------------------------------------------------------- Instance Variables

    /** Internal container instance. */
    private AbstractContainer container = new InnerContainerField();

    // ------------------------------------------------------ Constructorrs

    /**
     * Create a ContainerField with no name defined.
     */
    public AbstractContainerField() {
    }

    /**
     * Create a ContainerField with the given name.
     *
     * @param name the ContainerField name
     */
    public AbstractContainerField(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public methods

    /**
     * @see net.sf.click.control.Container#addControl(net.sf.click.Control).
     */
    public Control addControl(Control control) {
        return container.addControl(control);
    }

    /**
     * @see net.sf.click.control.Container#addControl(int, net.sf.click.Control).
     */
    public Control addControl(int index, Control control) {
        return container.addControl(index, control);
    }

    /**
     * @see net.sf.click.control.Container#removeControl(net.sf.click.Control).
     */
    public boolean removeControl(Control control) {
        return container.removeControl(control);
    }

    /**
     * @see net.sf.click.control.Container#getControls()}.
     */
    public List getControls() {
        return container.getControls();
    }

    /**
     * @see net.sf.click.control.Container#getControl(java.lang.String)
     */
    public Control getControl(String controlName) {
        return container.getControl(controlName);
    }

    /**
     * @see net.sf.click.control.Container#contains(net.sf.click.Control)
     */
    public boolean contains(Control control) {
        return container.contains(control);
    }

    /**
     * Returns true if this container has existing controls, false otherwise.
     *
     * @see AbstractContainer#hasControls()
     *
     * @return true if the container has existing controls, false otherwise.
     */
    public boolean hasControls() {
        return container.hasControls();
    }

    /**
     * @see net.sf.click.Control#onProcess()}.
     */
    public boolean onProcess() {
        return container.onProcess();
    }

    /**
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
        container.onDestroy();
    }

    /**
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        container.onInit();
    }

    /**
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        container.onRender();
    }

    /**
     * Returns the HTML representation of this control.
     * <p/>
     * This method delegates the rendering to the method
     * {@link #render(net.sf.click.util.HtmlStringBuffer)}. The size of buffer
     * is determined by {@link #getControlSizeEst()}.
     *
     * @return the HTML representation of this control
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    /**
     * Render the container and all its child controls to the specified buffer.
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
            buffer.append("\n");

        } else {

            //render only content because no tag is specified
            if (hasControls()) {
                renderContent(buffer);
            }
        }
    }

    //-------------------------------------------- protected methods

    /**
     * Return the container instance for this field.
     *
     * @return the container instance
     */
    protected AbstractContainer getContainer() {
        return container;
    }

    /**
     * @see AbstractControl#renderTagEnd(java.lang.String, net.sf.click.util.HtmlStringBuffer)}.
     */
    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd(tagName);
    }

    /**
     * Render this container content to the specified buffer.
     *
     * @see AbstractContainer#renderContent(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

    /**
     * Render this container children to the specified buffer.
     * 
     * @see AbstractContainer#renderChildren(net.sf.click.util.HtmlStringBuffer)
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
     * @see AbstractContainer#getControlMap()
     *
     * @return the map of controls
     */
    protected Map getControlMap() {
        return container.getControlMap();
    }

    /**
     * @see AbstractControl#getControlSizeEst() 
     */
    protected int getControlSizeEst() {
        return container.getControlSizeEst();
    }

    // -------------------------------------------------------- Inner Class

    /**
     * Inner class providing the container implementation for
     * AbstractContainerField.
     * <p/>
     * Note this class delegates certain methods to AbstractContainerField, so
     * that the Container implementation manipulates certain state on the
     * AbstractContainerField instance.
     */
    class InnerContainerField extends AbstractContainer {

        // -------------------------------------------------------- Constants

        private static final long serialVersionUID = 1L;

        // -------------------------------------------------------- Public Methods

        /**
         * Return the AbstractContainerField html tag.
         * 
         * @return the AbstractContainerField html tag
         */
        public String getTag() {
            return AbstractContainerField.this.getTag();
        }

        /**
         * Sets the AbstractContainerField parent.
         * 
         * @param parent the parent of the AbstractContainerField
         */
        public void setParent(Object parent) {
            AbstractContainerField.this.setParent(parent);
        }

        /**
         * Sets the AbstractContainerField name.
         *
         * @param name the name of the AbstractContainerField
         */
        public void setName(String name) {
            AbstractContainerField.this.setName(name);
        }

        /**
         * Sets the listener of the AbstractContainerField.
         * 
         * @param listener the listener object with the named method to invoke
         * @param method the name of the method to invoke
         */
        public void setListener(Object listener, String method) {
            AbstractContainerField.this.setListener(listener, method);
        }

        /**
         * Return the parent of the AbstractContainerField.
         * 
         * @return the parent of the AbstractContainerField
         */
        public Object getParent() {
            return AbstractContainerField.this.getParent();
        }

        /**
         * Return the name of the AbstractContainerField.
         * 
         * @return the name of the AbstractContainerField
         */
        public String getName() {
            return AbstractContainerField.this.getName();
        }

        /**
         * Return the messages of the AbstractContainerField.
         * 
         * @return the message of the AbstractContainerField
         */
        public Map getMessages() {
            return AbstractContainerField.this.getMessages();
        }

        /**
         * Return the id of the AbstractContainerField.
         * 
         * @return the id of the AbstractContainerField
         */
        public String getId() {
            return AbstractContainerField.this.getId();
        }

        /**
         * Return the html imports of the AbstractContainerField.
         * 
         * @return the html imports of the AbstractContainerField
         */
        public String getHtmlImports() {
            return AbstractContainerField.this.getHtmlImports();
        }

        /**
         * Return the Context of the AbstractContainerField.
         * 
         * @return the Context of the AbstractContainerField
         */
        public Context getContext() {
            return AbstractContainerField.this.getContext();
        }
    }
}
