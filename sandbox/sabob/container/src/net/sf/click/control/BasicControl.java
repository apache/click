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

import javax.servlet.ServletContext;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.ContainerUtils;
import net.sf.click.util.ControlUtils;

/**
 *
 * @author Bob Schellink
 */
public class BasicControl extends AbstractControl {

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    public BasicControl() {
    }

    public BasicControl(String name) {
        if (name != null) {
            setName(name);
        }
    }

    public BasicControl(String name, String id) {
        this(name);
        setId(id);
    }

    public String getTag() {
        return null;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        //If control has a parent control, it cannot change its name.
        //To change a fields name, it must be removed from its parent first,
        //and reattached. The reason for this is that controls added to parents
        //are added to a hashMap keyed on their name. So changing the field's 
        //name while its inside the map, will make it irretrievable.
        if (getParent() == null) {
            this.name = name;
        } else {
            throw new IllegalStateException("You cannot change the name of "
                + "a control that has a parent. To change the name, first remove "
                + "the control from its parent, change its name, then add it " 
                + "again.");
        }
    }

    // TODO should onProcess and invokeListener be seperated???
    // This enables processing and request binding to be completed for all controls
    // before invoking control listeners
    public boolean onProcess() {
        return invokeListener();
    }

    public void onDeploy(ServletContext servletContext) {
    }

    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

    public String getHtmlImports() {
        return null;
    }

    protected boolean invokeListener() {
        if (listener != null && listenerMethod != null) {
            return ClickUtils.invokeListener(listener, listenerMethod);

        } else {
            return true;
        }
    }

    public void onRender() {
    }

    public void onInit() {
    }

    public void onDestroy() {
    }

    public String getId() {
        return ContainerUtils.getAbsoluteId(this);
    }

    protected String _getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            return getName();
        }
    }

    public void render(HtmlStringBuffer buffer) {
        renderTagStart(getTag(), buffer);
        renderTagEnd(getTag(), buffer);
    }

    public String toString() {
        if (getTag() == null) {
            return "";
        }
        HtmlStringBuffer buffer = new HtmlStringBuffer(getContainerSizeEst());
        render(buffer);
        return buffer.toString();
    }

    // --------------------------------------------protected methods

    /**
     * Will render the element and common attributes. The element will not 
     * be closed, so one can add more attributes as needed.
     */
    protected void renderTagStart(String elementName,
      HtmlStringBuffer buffer) {
        if (elementName == null) {
            throw new IllegalStateException("Element cannot be null");
        }

        buffer.elementStart(elementName);

        buffer.appendAttribute("name", getName());
        if (hasAttributes() && getAttributes().containsKey("id")) {
            buffer.appendAttribute("id", getAttribute("id"));
        } else {
            ContainerUtils.appendAbsoluteId(this, buffer);
        }

        appendAttributes(buffer);
    }

    protected void renderTagEnd(String elementName, HtmlStringBuffer buffer) {
        buffer.elementEnd();
    }

    protected int getContainerSizeEst() {
        int size = 0;
        if (getTag() != null && hasAttributes()) {
            //length of the markup -> </> == 3
            //1 * tag.length()
            size += 3 + getTag().length();
            //using 20 as an estimate
            size += 20 * getAttributes().size();
        }
        return size;
    }

    protected int getDepth() {
        return ControlUtils.getDepth(this);
    }

    public static void main(String[] args) {
        // HR with name and id
        BasicControl hr = new BasicControl("name") {
            public String getTag() {
                return "hr";
            }
        };
        hr.setStyle("border", "solid blue 1px");
        System.out.println(hr);
        
        // HR without name and id
        hr = new BasicControl() {
            public String getTag() {
                return "hr";
            }
        };
        hr.setStyle("border", "solid blue 1px");
        System.out.println(hr);
    }
}
