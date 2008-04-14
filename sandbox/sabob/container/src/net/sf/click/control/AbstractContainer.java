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

import net.sf.click.util.ContainerUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.click.control.Container;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

public abstract class AbstractContainer extends AbstractControl implements Container {

    private static final long serialVersionUID = 1L;

    /** The list of controls. */
    private List controls;

    /** The map of controls keyed by field name. */
    private Map controlMap;

    // ------------------------------------------------------ Constructorrs

    public AbstractContainer() {
    }

    public AbstractContainer(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public methods

    public Control addControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }

        // Check if container already contains the control
        if (getControlMap().containsKey(control.getName())) {

            throw new IllegalArgumentException(
                "Container already contains control named: " + control.getName());
        }

        // Check if control already has parent
        if (control.getParent() != null) {

            // TODO perhaps throw exception instead of removing from parent
            if (control.getParent() instanceof Page) {
                throw new IllegalArgumentException("This control already has a"
                    + " parent Page.");

            } else {
                //remove control from parent
                ((Container) control.getParent()).removeControl(control);
            }
        }

        getControls().add(control);
        control.setParent(this);

        String name = control.getName();
        if (name != null) {
            getControlMap().put(name, control);
        }
        return control;
    }

    public boolean removeControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Control cannot be null");
        }

        boolean contains = getControls().remove(control);

        if (contains) {
            control.setParent(null);
        }

        String name = control.getName();

        if (name != null) {
            getControlMap().remove(name);
        }

        return contains;
    }

    public Control getControl(String controlName) {
        if (hasControls()) {
            return (Control) getControlMap().get(controlName);
        }
        return null;
    }

    public boolean contains(Control control) {
        return getControls().contains(control);
    }

    public List getControls() {
        if (controls == null) {
            controls = new ArrayList();
        }
        return controls;
    }

    public boolean hasControls() {
        return (controls == null) ? false : !controls.isEmpty();
    }

    public boolean onProcess() {

        if (hasControls()) {
            for (Iterator it = getControls().iterator(); it.hasNext(); ) {
                Control control = (Control) it.next();
                boolean continueProcessing = control.onProcess();
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logLifeCycle(control, "onProcess");
                }
                if (!continueProcessing) {
                    return false;
                }
            }
        }

        //TODO should invokeListener have its own callback? Then all controls can be
        //processed first, and invokeListener can be called afterwards once all
        //request values are available. This ensures that when a listener fires,
        //all controls have been bound to their request value.
        return invokeListener();
    }

    public String getHtmlImportsAll() {
        return ContainerUtils.getHtmlImportsAll(this);
    }

    public void onDestroy() {
        if (hasControls()) {
            for (int i = 0,  size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                try {
                    control.onDestroy();
                } catch (Throwable t) {
                    ClickUtils.getLogService().error("", t);
                }
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logLifeCycle(control, "onDestroy");
                }
            }
        }
    }

    public void onInit() {
        if (hasControls()) {
            for (int i = 0,  size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                control.onInit();
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logLifeCycle(control, "onInit");
                }
            }
        }
    }

    public void onRender() {
        if (hasControls()) {
            for (int i = 0,  size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                control.onRender();
                if (ClickUtils.getLogService().isTraceEnabled()) {
                    logLifeCycle(control, "onRender");
                }
            }
        }
    }

    /**
     * NOTE: #toString delegates to #render for major performance boost by 
     * rendering from the same buffer.
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

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

    protected Map getControlMap() {
        if (controlMap == null) {
            controlMap = new HashMap();
        }
        return controlMap;
    }

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

    protected void renderTagEnd(String tagName, HtmlStringBuffer buffer) {
        buffer.elementEnd(tagName);
    }

    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

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
//                if (control instanceof Container) {
//                    ((Container) control).render(buffer);
//                } else if (control instanceof BasicControl) {
//                    ((BasicControl) control).render(buffer);
//                } else {
//                    buffer.append(control.toString());
//                }
            }
        }
    }

    protected void logLifeCycle(Control control, String phase) {
        String controlClassName = control.getClass().getName();
        controlClassName = controlClassName.substring(controlClassName.lastIndexOf('.')
            + 1);
        String msg = "   invoked: '" + control.getName() + "' " +
            controlClassName + "." + phase + "()";
        ClickUtils.getLogService().trace(msg);
    }

    // -------------------------------------------------------- Private methods

    public static void main(String[] args) {
        AbstractContainer div = new AbstractContainer("name") {
            public String getTag() {
                return "div";
            }
        };
        div.setStyle("border", "solid blue 1px");
        System.out.println(div);
    }
}
