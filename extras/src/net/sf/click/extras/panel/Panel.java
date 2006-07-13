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
package net.sf.click.extras.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.MessagesMap;

/**
 * TODO:
 *
 * Provides for a "Panel" which is simply a template and adds pass through
 * methods to the Page for adding Control and Model objects.
 * <p/>
 * The Panel class allows a Click applications to customize the output of
 * sections on a page, by providing a "template" to layout controls in multiple
 * locations on the page.
 * <p/>
 * The default template name will be the 'simple name' of the class (the class
 * name without the package information) plus the default template extension
 * (currently ".htm").  It is important to remember that since panels contain
 * their own templates, panels must be evaluated using Velocity's #parse()
 * directive.
 * <p/>
 * When a Panel is instantiated, it should automatically add all the information
 * required to render the Panel to the Model (via addModel) and pass controls
 * directly to the Page (via addControl).
 * <p/>
 *
 *
 * A simple implementation of a Panel, that will render a basic panel.  If
 * the template is provided, it will return that value via the toString()
 * method.  If none provided, it will attempt to locate and use the default name
 * of "Panel.htm".
 * <p/>
 * NOTE: If no template is provided, no output will be rendered, effectively
 * making this Panel a passthrough to the Page.
 *
 * @author Phil Barnes
 * @author Malcolm Edgar
 */
public class Panel implements Control {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** A request context. */
    protected transient Context context;

    /** A temporary storage for control objects until the Page is set. */
    protected List controls = new ArrayList();

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

    /** The page this panel is associated with. */
    protected Page page;

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
     * Adds a 'sub-panel' to this panel.  This is useful for 'panels of panels',
     * in which each Panel will be rendered recursively, allowing advanced
     * layout functionality.  See {@link ListPanel} and {@link TabbedPanel} for
     * examples.
     *
     * @param panel the pannel to add
     */
    public void addPanel(Panel panel) {
        if (panel == null) {
            throw new IllegalArgumentException("Null panel parameter");
        }

        getPanels().add(panel);
        addControl(panel);
    }

    /**
     * Return the list of sub panels associated with this panel. Do not
     * add sub panels using this method, use {@link #addPanel(Panel)} instead.
     *
     * @return the list of sub-panels, if any
     */
    public List getPanels() {
        return panels;
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
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (id == null) {
            id = getName();

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
        }
        return id;
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
     * Returns text label assocaited with this panel.  This should be an already
     * internationalized label set via setLabel().  Typically this is used as a
     * header for a particular panel.
     *
     * For example:
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
     * TODO:
     *
     * Set the internationalized label associated with this panel.  This method
     * is necessary to ensure proper internationalization of text for a panel.
     *
     * @param label the internationalized label for this panel
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

        if (parent instanceof Page) {
            setPage((Page) parent);
        }
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
     * This method returns true.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * Return the HTML string representation of the Panel.
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        Context context = getContext();

        if (getTemplate() != null) {
            return context.renderTemplate(getTemplate(), getPage().getModel());

        } else {
            return context.renderTemplate(getClass(), getPage().getModel());
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * A 'pass-through' method to add the control to the page model. The control
     * will be added to the pages model using the controls name as the key. The
     * Controls context property will also be set, as per Page.addControl()
     *
     * @param control the control to add
     * @throws IllegalArgumentException if the control is null
     */
    protected void addControl(Control control) {
        if (getPage() != null) {
            getPage().addControl(control);

        } else {
            controls.add(control);
        }
    }

    /**
     * A 'pass-through' method to add the object to the page model. The object
     * will be added to the pages model using the given name as the key.
     *
     * @param name  the key name of the object to add
     * @param value the object to add
     * @throws IllegalArgumentException if the name or value parameters are
     *      null, or if there is already a named value in the model
     */
    protected void addModel(String name, Object value) {
        if (getPage() != null) {
            getPage().addModel(name, value);

        } else {
            model.put(name, value);
        }
    }

    /**
     * Allows removal of a model object in the Pages or panel model map,
     * depending on whether the page has been set yet or not for this Panel.
     *
     * @param key the key of the page model value to remove
     */
    protected void removeModel(String key) {
        if (getPage() != null) {
            getPage().getModel().remove(key);

        } else {
            model.remove(key);
        }
    }

    /**
     * The page this panel is associated to.
     *
     * @return the page for this panel
     */
    protected Page getPage() {
        return this.page;
    }

    /**
     * TODO:
     * Sets the page associated with this panel.  This call is necessary to
     * ensure any associated model or control elements that are added to the
     * panel are ultimately added to the page, so that the rendering of the page
     * has all available information necessary to render.
     *
     * Set the page this panel is associated to.  This method will set the
     * sub-panels pages as well
     *
     * @param page the page associated with this panel
     */
    protected void setPage(Page page) {
        this.page = page;

        if (!controls.isEmpty()) {
            for (int i = 0; i < controls.size(); i++) {
                Control control = (Control) controls.get(i);

                if (!page.getModel().containsKey(control.getName())) {
                    page.addControl(control);
                }
            }
        }

        if (!model.isEmpty()) {
            for (Iterator i = model.keySet().iterator(); i.hasNext();) {
                String key = i.next().toString();
                Object value = model.get(key);

                if (!page.getModel().containsKey(key)) {
                    page.getModel().put(key, value);
                }
            }
        }

        if (!getPanels().isEmpty()) {
            List panels = getPanels();
            for (int i = 0; i < panels.size(); i++) {
                Panel panel = (Panel) panels.get(i);
                panel.setPage(page);
            }
        }
    }

}
