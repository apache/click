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
 * TODO
 *
 * @author Bob Schellink
 */
public class ContainerField extends Field implements Container {

    // -------------------------------------------------------- Instance Variables
    
    protected AbstractContainer container = new InnerContainerField();

    // ------------------------------------------------------ Constructorrs

    public ContainerField() {
    }

    public ContainerField(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public methods

    public Control addControl(Control control) {
        return container.addControl(control);
    }

    public boolean removeControl(Control control) {
        return container.removeControl(control);
    }

    public Control getControl(String controlName) {
        return container.getControl(controlName);
    }

    public boolean contains(Control control) {
        return container.contains(control);
    }

    public List getControls() {
        return container.getControls();
    }

    public boolean hasControls() {
        return container.hasControls();
    }

    public boolean onProcess() {
        return container.onProcess();
    }

    public String getHtmlImportsAll() {
        return container.getHtmlImportsAll();
    }

    public void onDestroy() {
        container.onDestroy();
    }

    public void onInit() {
        container.onInit();
    }

    public void onRender() {
        container.onRender();
    }

     public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(getControlSizeEst());
        render(buffer);
        return buffer.toString();
    }

    /**
     * @see Control#render(net.sf.click.util.HtmlStringBuffer)
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
            }
        }
    }

    protected Map getControlMap() {
        return container.getControlMap();
    }

    protected int getControlSizeEst() {
        return container.getControlSizeEst();
    }

    // -------------------------------------------------------- Inner Class

    class InnerContainerField extends AbstractContainer {

        public String getTag() {
            return ContainerField.this.getTag();
        }

        public void setParent(Object parent) {
            ContainerField.this.setParent(parent);
        }

        public void setName(String name) {
            ContainerField.this.setName(name);
        }

        public void setListener(Object listener, String method) {
            ContainerField.this.setListener(listener, method);
        }

        public Object getParent() {
            return ContainerField.this.getParent();
        }

        public String getName() {
            return ContainerField.this.getName();
        }

        public Map getMessages() {
            return ContainerField.this.getMessages();
        }

        public String getId() {
            return ContainerField.this.getId();
        }

        public String getHtmlImports() {
            return ContainerField.this.getHtmlImports();
        }

        public Context getContext() {
            return ContainerField.this.getContext();
        }
    }
}
