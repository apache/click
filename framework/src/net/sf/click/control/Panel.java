/*
 * Copyright 2004-2006 Malcolm A. Edgar
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.Format;
import net.sf.click.util.MessagesMap;
import net.sf.click.util.SessionMap;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Panel control for creating customized layout sections within a page.
 *
 * <table style='margin-bottom: 1.25em'>
 * <tr>
 * <td>
 * <fieldset>
 * <legend>Panel</legend>
 * <i>My panel content.</i>
 * </fieldset>
 * </td>
 * </tr>
 * </table>
 *
 * The Panel class uses a Velocity template for rendering any model data or
 * controls you have added to the Page. For example to create a simple
 * Panel we could have the following <tt>panel-template.htm</tt>
 *
 * <pre class="codeHtml">
 * &lt;fieldset&gt;
 *   &lt;legend class="title"&gt; <span class="st">$messages.heading</span> &lt;/legend&gt;
 *   <span class="st">$messages.content</span>
 * &lt;/fieldset&gt; </pre>
 *
 * Then in our page class we would include the Panel. With the
 * <span class="st"><tt>$messages.heading</tt></span> and
 * <span class="st"><tt>$messages.content</tt></span> values defined in the
 * Pages properties file.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> WelcomePage <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> Panel panel = <span class="kw">new</span> Panel(<span class="st">"panel"</span>, <span class="st">"/panel-template.htm"</span>);
 * } </pre>
 *
 * <p/>
 * In our <tt>WelcomePage</tt> template <tt>welcome.htm</tt> would simply
 * reference our panel control:
 *
 * <pre class="codeHtml"> <span class="st">$panel</span> </pre>
 *
 * The Panel template would then be merged with the template model and rendered
 * in the page as:
 *
 * <fieldset style="margin:2em;width:550px;">
 * <legend><b>Welcome</b></legend>
 * Welcome to <a href="#">MyCorp</a>.
 * <p/>
 * MyCorp is your telecommuting office portal. Its just like being there at the
 * office!
 * </fieldset>
 *
 * Panel rendering is performed using the {@link #toString()} method, and the
 * template model is created using {@link #createTemplateModel()}.
 *
 * <h3>Template Model</h3>
 *
 * To render the panel's template, a model is created which is merged with
 * the Velocity template.  This model will include the pages model values,
 * plus any Panel defined model values, with the Panels values overriding any
 * Page defined values. In addition a number of values are automatically added
 * model. These values include:
 * <ul>
 * <li>attributes - the panel HTML attributes map</li>
 * <li>context - the Servlet context path, e.g. /mycorp</li>
 * <li>format - the page {@link Format} object for formatting the display of objects</li>
 * <li>this - a reference to this panel object</li>
 * <li>messages - the panel messages bundle</li>
 * <li>request - the servlet request</li>
 * <li>response - the servlet request</li>
 * <li>session - the {@link SessionMap} adaptor for the users HttpSession</li>
 * </ul>
 *
 * @author Phil Barnes
 * @author Malcolm Edgar
 */
public class Panel implements Control {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The Panel attributes Map. */
    protected Map attributes;

    /** A request context. */
    protected transient Context context;

    /** The list of panel controls. */
    protected List controls;

    /** The "identifier" for this panel (CSS id for rendering). */
    protected String id;

    /** The (localized) label of this panel. */
    protected String label;

    /** The map of localized messages for this panel. */
    protected Map messages;

    /** A temporary storage for model objects until the Page is set. */
    protected Map model = new HashMap();

    /** The name of the control. */
    protected String name;

    /** The list of sub panels. */
    protected List panels = new ArrayList(5);

    /** The control's parent. */
    protected Object parent;

    /** The path of the Velocity template to render. */
    protected String template;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Panel with the given name.
     *
     * @param name the name of the panel
     */
    public Panel(String name) {
        setName(name);
    }

    /**
     * Create a Panel with the given name and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template
     */
    public Panel(String name, String template) {
        setName(name);
        setTemplate(template);
    }

    /**
     * Create a Panel with the given name, id attribute and template path.
     *
     * @param name the name of the panel
     * @param template the Velocity template path
     * @param id the id HTML attribute value
     */
    public Panel(String name, String id, String template) {
        setName(name);
        setTemplate(template);
        setId(id);
    }

    /**
     * Create a Panel with no name or template defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Panel() {
    }

    // ------------------------------------------------------------- Properties

    /**
     * Return the Panel HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of panel HTML attribute
     * @return the Panel HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the Panels with the given HTML attribute name and value. These
     * attributes will be rendered as HTML attributes.
     * <p/>
     * If there is an existing named attribute in the Panel it will be replaced
     * with the new value. If the given attribute value is null, any existing
     * attribute will be removed.
     *
     * @param name the name of the field HTML attribute
     * @param value the value of the field HTML attribute
     * @throws IllegalArgumentException if attribute name is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (attributes == null) {
            attributes = new HashMap(5);
        }

        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }

    /**
     * Return the Panel attributes Map.
     *
     * @return the panel attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the Panel has attributes or false otherwise.
     *
     * @return true if the Panel has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add the control to the panel. The control will be added to the panels model
     * using the controls name as the key. The Controls context property will
     * be set if the context is available. The Controls parent property will
     * also be set to the page instance.
     *
     * @param control the control to add
     * @throws IllegalArgumentException if the control is null, or if the name
     *      of the control is not defined
     */
    public void addControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        if (StringUtils.isBlank(control.getName())) {
            throw new IllegalArgumentException("Control name not defined");
        }

        getControls().add(control);
        addModel(control.getName(), control);

        if (control instanceof Panel) {
            getPanels().add(control);
        }

        control.setParent(this);

        if (getContext() != null) {
            control.setContext(getContext());
        }
    }

    /**
     * Return the list of page Controls.
     *
     * @return the list of page Controls
     */
    public List getControls() {
        if (controls == null) {
            controls = new ArrayList();
        }
        return controls;
    }

    /**
     * Return true if the page has any controls defined.
     *
     * @return true if the page has any controls defined
     */
    public boolean hasControls() {
        return (controls == null) ? false : !controls.isEmpty();
    }

    /**
     * @see net.sf.click.Control#getContext()
     *
     * @return the Page request Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see net.sf.click.Control#setContext(Context)
     *
     * @param context the Page request Context
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = (Control) getControls().get(i);
                control.setContext(context);
            }
        }
    }

    /**
     * Return the panel id value. If no id attribute is defined then this method
     * will return the panel name.
     *
     * @see net.sf.click.Control#getId()
     *
     * @return the panel HTML id attribute value
     */
    public String getId() {
        if (id != null) {
            return id;

        } else {
            String id = getName();

            if (id.indexOf('/') != -1) {
                id = id.replace('/', '_');
            }
            if (id.indexOf(' ') != -1) {
                id = id.replace(' ', '_');
            }
            if (id.indexOf('<') != -1) {
                id = id.replace('<', '_');
            }
            if (id.indexOf('>') != -1) {
                id = id.replace('>', '_');
            }

            return id;
        }
    }

    /**
     * Set the id for this panel.  This is the identifier that will be assigned
     * to the 'id' tag for this panel's model.
     *
     * @param id the id attribute for this panel
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method returns null.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return null
     */
    public String getHtmlImports() {
        return null;
    }

    /**
     * Return the panel display label.
     * <p/>
     * If the label value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".label"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value still cannot be found then the Panel name will be converted
     * into a label using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     * Typically the label property is used as a header for a particular panel.
     * For example:
     *
     * <pre class="codeHtml">
     *  &lt;div id="$panel.id"&gt;
     *      &lt;h1&gt;$panel.label&lt;/h1&gt;
     *      ## content here
     *  &lt;/div&gt; </pre>
     *
     * @return the internationalized label associated with this control
     */
    public String getLabel() {
        if (label == null) {
            label = getMessage(getName() + ".label");
        }
        if (label == null) {
            label = ClickUtils.toLabel(getName());
        }
        return label;
    }

    /**
     * Set the Panel display caption.
     *
     * @param label the display label of the Panel
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * This method does nothing.
     *
     * @see Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
    }

    /**
     * Return a Map of localized messages for the Field.
     *
     * @return a Map of localized messages for the Field
     * @throws IllegalStateException if the context for the Field has not be set
     */
    public Map getMessages() {
        if (messages == null) {
            if (getContext() != null) {
                messages =
                    new MessagesMap(this, CONTROL_MESSAGES, getContext());

            } else {
                String msg = "Cannot initialize messages as context not set";
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }

    /**
     * Return the localized message for the given key, or null if not found.
     * <p/>
     * This method will attempt to lookup for the localized message in the
     * parent, which by default represents the Page's resource bundle.
     * <p/>
     * If the message was not found, the this method will attempt to look up the
     * value in the fields class properties file and then finally in the global
     * controls <tt>/click-control.properties</tt> message properties file.
     * <p/>
     * If still not found, this method will return null.
     *
     * @param name the name of the message resource
     * @return the named localized message, or null if not found
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String message = null;

        Map parentMessages = ClickUtils.getParentMessages(this);
        if (parentMessages.containsKey(name)) {

            message = (String) parentMessages.get(name);
        }

        if (message == null && getMessages().containsKey(name)) {
            message = (String) getMessages().get(name);
        }

        return message;
    }

    /**
     * Add the named object value to the Panels model map.
     *
     * @param name the key name of the object to add
     * @param value the object to add
     * @throws IllegalArgumentException if the name or value parameters are
     * null, or if there is already a named value in the model
     */
    public void addModel(String name, Object value) {
        if (name == null) {
            String msg = "Cannot add null parameter name to "
                         + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "Cannot add null " + name + " parameter "
                         + "to " + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (getModel().containsKey(name)) {
            String msg = getClass().getName() + " model already contains "
                         + "value named " + name;
            throw new IllegalArgumentException(msg);
        } else {
            getModel().put(name, value);
        }
    }

    /**
     * Return the panels model map. The model is used populate the
     * Velocity Context with is merged with the panel template before rendering.
     *
     * @return the Page's model map
     */
    public Map getModel() {
        return model;
    }

    /**
     * Adds a 'sub-panel' to this panel.  This is useful for 'panels of panels',
     * in which each Panel will be rendered recursively, allowing advanced
     * layout functionality.
     *
     * @deprecated this method will be removed in Click RC1, please use
     * addControl() instead
     *
     * @param panel the pannel to add
     */
    public void addPanel(Panel panel) {
        addControl(panel);
    }

    /**
     * Return the list of sub panels associated with this panel. Do not
     * add sub panels using this method, use {@link #addPanel(Panel)} instead.
     *
     * @return the list of sub-panels, if any
     */
    public List getPanels() {
        if (panels == null) {
            panels = new ArrayList();
        }
        return panels;
    }

    /**
     * @see Panel#getName()
     *
     * @return the name of this panel, to be used to uniquely identify it in the
     * model context
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for this panel.  This is used to uniquely identify the panel
     * in the model context.
     *
     * @see Control#setName(String)
     *
     * @param name the name of this control
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see Control#getParent()
     *
     * @return the Control's parent
     */
    public Object getParent() {
        return parent;
    }

    /**
     * @see Control#setParent(Object)
     *
     * @param parent the parent of the Control
     */
    public void setParent(Object parent) {
        this.parent = parent;
    }

    /**
     * Return the path of the Velocity template to render.
     *
     * @return the path of the Velocity template to render
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the path of the Velocity template to render.
     *
     * @param template the path of the Velocity template to render
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method does nothing and can be overridden by subclasses.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
    }

    /**
     * Process the request and invoke the <tt>onProcess()</tt> method of any
     * child controls.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true or false to abort further processing
     */
    public boolean onProcess() {
        if (hasControls()) {
            List controls = getControls();
            for (int i = 0; i < controls.size(); i++) {
                Control control = (Control) controls.get(i);
                if (!control.onProcess()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return the HTML string representation of the Panel. The panel will be
     * rendered by merging the Velocity {@link #template} with the template
     * model. The template model is created using {@link #createTemplateModel()}.
     * <p/>
     * If a Panel template is not defined, a template based on the classes
     * name will be loaded. For more details please see {@link Context#renderTemplate(Class, Map)}.
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        Context context = getContext();

        if (getTemplate() != null) {
            return context.renderTemplate(getTemplate(), createTemplateModel());

        } else {
            return context.renderTemplate(getClass(), createTemplateModel());
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Create a model to merge with the Velocity template. The model will
     * include the pages model values, plus any Panel defined model values, and
     * a number of automatically added model values. Note panel model values
     * will override any page defined model values.
     * <p/>
     * The following values automatically added to the Model:
     * <ul>
     * <li>attributes - the panel HTML attributes map</li>
     * <li>context - the Servlet context path, e.g. /mycorp</li>
     * <li>format - the page {@link Format} object for formatting the display of objects</li>
     * <li>this - a reference to this panel</li>
     * <li>messages - the panel messages bundle</li>
     * <li>request - the servlet request</li>
     * <li>response - the servlet request</li>
     * <li>session - the {@link SessionMap} adaptor for the users HttpSession</li>
     * </ul>
     *
     * @return a new model to merge with the Velocity template.
     */
    protected Map createTemplateModel() {

        final HttpServletRequest request = getContext().getRequest();

        final Page page = ClickUtils.getParentPage(this);

        final Map renderModel = new HashMap(page.getModel());

        renderModel.putAll(getModel());

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
