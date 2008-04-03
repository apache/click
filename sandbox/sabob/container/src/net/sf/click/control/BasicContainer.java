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
import java.util.List;
import java.util.Map;
import net.sf.click.Container;
import net.sf.click.Control;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.HtmlStringBuffer;

public class BasicContainer extends BasicControl implements Container {

    private static final long serialVersionUID = 1L;

    /** The list of panel controls. */
    protected List controlList;

    /** The map of controls keyed by field name. */
    protected Map controls;

    // ------------------------------------------------------ Constructorrs

    public BasicContainer() {
    }

    public BasicContainer(String name) {
        super(name);
    }

    public BasicContainer(String name, String id) {
        super(name, id);
    }

    // ------------------------------------------------------ Public methods

    public boolean removeControl(Control control) {
        return remove(control);
    }

    public Control addControl(Control control) {
        return add(control);
    }

    public Control getControl(String controlName) {
        if (hasControls()) {
            return (Control) getControlMap().get(controlName);
        }
        return null;
    }

    //TODO. what if control does not have a name
    public boolean contains(String controlName) {
        return getControlMap().containsKey(controlName);
    }

    public List getControls() {
        if (controlList == null) {
            controlList = new ArrayList();
        }
        return controlList;
    }

    public boolean hasControls() {
        return (controlList == null) ? false : !controlList.isEmpty();
    }

    public boolean onProcess() {

        if (hasControls()) {
            List controls = getControls();
            for (int i = 0; i < controls.size(); i++) {
                Control control = (Control) controls.get(i);
                boolean continueProcessing = control.onProcess();
                if (ClickLogger.getInstance().isTraceEnabled()) {
                    logLifeCycle(control, "onProcess");
                }
                if (!continueProcessing) {
                    return false;
                }
            }
        }

        //TODO should not invokeListener have its own callback? Then all controls can be
        //processed, and invokeListener can afterwards be called. This ensures that when
        //a listener fires, all controls are already filled with their data.
        return invokeListener();
    }

    public String getAllHtmlImports() {
        return ContainerUtils.getAllHtmlImports(this);
    }

    public void onDestroy() {
        if (hasControls()) {
            for (int i = 0,  size = getControls().size(); i < size; i++) {
                Control control = (Control) getControls().get(i);
                control.onDestroy();
                if (ClickLogger.getInstance().isTraceEnabled()) {
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
                if (ClickLogger.getInstance().isTraceEnabled()) {
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
                if (ClickLogger.getInstance().isTraceEnabled()) {
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
        HtmlStringBuffer buffer = new HtmlStringBuffer(getContainerSizeEst());
        render(buffer);
        return buffer.toString();
    }

    /**
     * NOTE: #render enables major performance boost by rendering from the same
     * buffer.
     *
     * @param buffer
     */
    public void render(HtmlStringBuffer buffer) {

        //If tag is set, render it
        if (getTag() != null) {
            renderTagStart(getTag(), buffer);
            buffer.closeTag();
            renderContent(buffer);
            if (!hasControls()) {
                buffer.append("\n");
            }

            renderElementEnd(getTag(), buffer);
            buffer.append("\n");

        } else {

            //render only content because no tag is specified
            if (hasControls()) {
                renderContent(buffer);
            }
        }
    }

    //-------------------------------------------- protected methods

    protected boolean remove(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }

        boolean wasRemoved = getControls().remove(control);

        if (wasRemoved) {
            control.setParent(null);
        }

        String name = control.getName();

        if (name != null) {
            getControls().remove(name);
        }

        return wasRemoved;
    }

    protected Control add(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        //TODO Should all controls have names?

        //Test if control already has parent
        if (control.getParent() != null) {
            //remove control from parent
            boolean wasRemoved = ((Container) control.getParent()).removeControl(control);
        }

        getControls().add(control);
        control.setParent(this);

        String name = control.getName();
        if (name != null) {
            getControlMap().put(name, control);
        }
        return control;
    }

    protected Map getControlMap() {
        if (controls == null) {
            controls = new HashMap();
        }
        return controls;
    }

    protected int getContainerSizeEst() {
        int size = 20;

        if (getTag() != null && hasAttributes()) {
            size += 20 * getAttributes().size();
        }

        if (hasControls()) {
            size += 400 + (getControls().size() * 200);
        }

        return size;
    }

    protected void renderElementEnd(String elementName, HtmlStringBuffer buffer) {
        buffer.elementEnd(elementName);
    }

    protected void renderContent(HtmlStringBuffer buffer) {
        renderChildren(buffer);
    }

    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = (Control) getControls().get(i);
                if (control instanceof Container) {
                    ((Container) control).render(buffer);
                } else if (control instanceof BasicControl) {
                    ((BasicControl) control).render(buffer);
                } else {
                    buffer.append(control.toString());
                }
            }
        }
    }

    // -------------------------------------------------------- Private methods

    private void logLifeCycle(Control control, String phase) {
        String controlClassName = control.getClass().getName();
        controlClassName = controlClassName.substring(controlClassName.lastIndexOf('.') +
            1);
        String msg = "   invoked: '" + control.getName() + "' " +
            controlClassName + "." + phase + "()";
        ClickLogger.getInstance().trace(msg);
    }

    public static void main(String[] args) {
        BasicContainer div = new BasicContainer("name") {
            public String getTag() {
                return "div";
            }
        };
        div.setStyle("border", "solid blue 1px");
        System.out.println(div);
    }
}
