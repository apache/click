/*
 * Copyright 2004-2005 Malcolm A. Edgar
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
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.MessagesMap;

/**
 * A simple implementation of a Panel, that will render a basic panel.  If
 * the template is provided, it will return that value via the toString()
 * method.  If none provided, it will attempt to locate and use the default name
 * of "BasicPanel.htm".
 * <p/>
 * NOTE: If no template is provided, no output will be rendered, effectively
 * making this Panel a passthrough to the Page.
 *
 * @author Phil Barnes
 */
public class BasicPanel implements Panel {

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

    /** The "name" of this panel (context key). */
    protected String name;

    /** The page this panel is associated with. */
    protected Page page;

    /** The list of sub panels. */
    protected List panels;

    /** The control's parent. */
    protected Object parent;

    /** The template this panel is tied to for rendering. */
    protected String template;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a basic panel with the given id.
     *
     * @param id the panel id
     */
    public BasicPanel(String id) {
        // null template will be replaced with a default template name - see
        // BasicPanel(String id, String name, String template, Context context)
        this(id, ClickUtils.toName(id), null);
    }


    /**
     * Create a basic panel with the given id and Velocity template.
     *
     * @param id the panel id
     * @param template the Velocity template
     */
    public BasicPanel(String id, String template) {
        this(id, ClickUtils.toName(id), template);
    }

    /**
     * Constructor to specify the id and name of this Panel, as well as the
     * template to use in rendering the Panel.
     *
     * @param id the panel id
     * @param name the panel name
     * @param template the Velocity template
     */
    public BasicPanel(String id, String name, String template) {
        setId(id);
        setName(name);
        if (template != null) {
            setTemplate(template);
        } else {
            setTemplate(getDefaultTemplateName());
        }
    }

    /**
     * Default no-args constructor used to deploy panel resources.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for resource deployment and are not
     * intended for general use. </div>
     */
    public BasicPanel() {
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
        // assert not null
        if (panel == null) {
            throw new IllegalArgumentException(
                    "Attempted to add a 'null panel' during addPanel()");
        }
        // ensure that the panels list has been initialized
        if (panels == null) {
            panels = new ArrayList();
        }
        panels.add(panel);
        // add the panel to the model of the page
        addModel(panel.getName(), panel);
    }

    /**
     * A 'pass-through' method to add the control to the page model. The control
     * will be added to the pages model using the controls name as the key. The
     * Controls context property will also be set, as per Page.addControl()
     *
     * @param control the control to add
     * @throws IllegalArgumentException if the control is null
     */
    protected void addControl(Control control) {
        // TODO: add null check
        if (getPage() != null) {
            getPage().addControl(control);
        } else {
            // temporary storage for controls until the page is set
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
            // temporary storage for model objects until page is set
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
     * The page this panel is associated to.
     *
     * @return the page for this panel
     */
    protected Page getPage() {
        return this.page;
    }

    /**
     * Set the page this panel is associated to.  This method will set the
     * sub-panels pages as well
     *
     * @see Panel#setPage(Page)
     *
     * @param page the page associated with this panel
     */
    public void setPage(Page page) {
        this.page = page;
        if (!model.isEmpty()) {
            page.getModel().putAll(model);
        }
        if (!controls.isEmpty()) {
            for (int i = 0; i < controls.size(); i++) {
                Control control = (Control) controls.get(i);
                page.addControl(control);
            }
        }
        // set the page on all the sub-panels
        if (getPanels() != null && !getPanels().isEmpty()) {
            List panels = getPanels();
            for (int i = 0; i < panels.size(); i++) {
                Panel panel = (Panel) panels.get(i);
                panel.setPage(page);
            }
        }
    }

    /**
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
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
     * @see Panel#getLabel()
     *
     * @return the internationalized label associated with this control
     */
    public String getLabel() {
        // TODO: add localization logic, as done with fields
        return label == null ? name : label;
    }

    /**
     * @see Panel#setLabel(String)
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
     * Returns true if there are any sub-panels associated to this panel.
     *
     * @return true if there are sub-panels
     */
    public boolean hasPanels() {
        return (panels != null && !panels.isEmpty());
    }

    /**
     * Returns the list of sub-panels associated with this panel, if any.
     *
     * @return the list of sub-panels, if any
     */
    public List getPanels() {
        // TODO: lazy load this
        return panels;
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
        // TODO: setPage()
        this.parent = parent;
    }

    /**
     * The template associated with this Panel.  If null, the simple class name
     * plus the default extension (".htm") will be used.
     *
     * @see Panel#getTemplate()
     *
     * @return the path/name of the template to use when rendering this panel
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the Velocity template to be used for this Panel.
     *
     * @param template the Velocity template to render
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
     * Return the template path.
     * <p/>
     * Overridden toString to return the name of the template associated with
     * this panel, see {@link #getTemplate()}.
     *
     * @return the template associated with this panel
     */
    public String toString() {
        // TODO: Question: Should this not be done in favor of rendering the
        // panel template implicitly?  i.e. #parse($somePanelName.template)
        // A potential issue might be in debugger evaluation of the toString
        // method.

        return getTemplate();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the default template name which is the short classname and the
     * template extension (htm).
     *
     * @return the short class name + the default template extension (.htm)
     */
    protected String getDefaultTemplateName() {
        String classname = getClass().getName();
        int index = classname.lastIndexOf('.');
        return classname.substring(index + 1) + ".htm";
    }

}
