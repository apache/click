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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.Format;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.SessionMap;

public class Panel extends AbstractContainer {

    protected String template;

    /** A temporary storage for model objects until the Page is set. */
    protected Map model;// = new HashMap();

    // ----------------------------------------------------------- Constructors
        
    public Panel(String name) {
        super(name);
    }
    
    public Panel(String name, String id) {
        super(name);
        setId(id);
    }
    
    public Panel() {
    }
    
    // ------------------------------------------------------------- Properties

    public void addModel(String name, Object value) {
        if (name == null) {
            String msg = "Cannot add null parameter name to " + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "Cannot add null " + name + " parameter " + "to " + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (getModel().containsKey(name)) {
            String msg = getClass().getName() + " model already contains " + "value named " + name;
            throw new IllegalArgumentException(msg);
        } else {
            getModel().put(name, value);
        }
    }

    public Map getModel() {
        if (model == null) {
            model = new HashMap();
        }
        return model;
    }

    public String getTemplate() {
        return template;
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }

    public void render(HtmlStringBuffer buffer) {
        if (getTemplate() != null) {
            buffer.append(getContext().renderTemplate(getTemplate(), createTemplateModel()));
        } else {
            super.render(buffer);
        }
    }

    protected Map createTemplateModel() {

        final HttpServletRequest request = getContext().getRequest();

        final Page page = ClickUtils.getParentPage(this);

        final Map renderModel = new HashMap(page.getModel());

        if (hasAttributes()) {
            renderModel.put("attributes", getAttributes());
        } else {
            renderModel.put("attributes", Collections.EMPTY_MAP);
        }

        renderModel.put("this", this);

        renderModel.put("context", request.getContextPath());

        Format format = page.getFormat();
        if (format != null) {
            renderModel.put("format", format);
        }

        Map messages = new HashMap(getMessages());
        messages.putAll(page.getMessages());
        renderModel.put("messages", messages);

        renderModel.put("request", request);

        renderModel.put("response", getContext().getResponse());

        renderModel.put("session", new SessionMap(request.getSession(false)));

        return renderModel;
    }
}
