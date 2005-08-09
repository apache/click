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
package net.sf.click.panel;

import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.Panel;
import net.sf.click.util.ClickUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @version $Id$
 */
public class BasicPanel implements Panel {
    /**
     * The list of sub panels.
     */
    protected List panels;

    /**
     * The page this panel is associated with
     */
    private Page page;

    /**
     * A temporary storage for control objects until the Page is set
     */
    private List controls = new ArrayList();

    /**
     * A temporary storage for model objects until the Page is set
     */
    private Map model = new HashMap();

    /**
     * The template this panel is tied to for rendering
     */
    private String template;

    /**
     * The "name" of this panel (context key)
     */
    private String name;

    /**
     * The (localized) label of this panel
     */
    private String label;

    /**
     * The "identifier" for this panel (id for rendering)
     */
    private String id;

    public BasicPanel(String id) {
        // null template will be replaced with a default template name - see
        // BasicPanel(String id, String name, String template, Context context)
        this(id, ClickUtils.toName(id), null);
    }

    public BasicPanel(String id, String template) {
        this(id, ClickUtils.toName(id), template);
    }

    /**
     * Constructor to specify the id and name of this Panel, as well as the
     * template to use in rendering the Panel.
     *
     * @param id
     * @param name
     * @param template
     */
    public BasicPanel(String id, String name, String template) {
        setId(id);
        setName(name);
        if (template == null) {
            setTemplate(ClickUtils.generateDefaultTemplateName(getClass()));
        } else {
            setTemplate(template);
        }
    }


    /**
     * Adds a 'sub-panel' to this panel.  This is useful for 'panels of panels',
     * in which each Panel will be rendered recursively, allowing advanced
     * layout functionality.  See {@link ListPanel} and {@link TabbedPanel} for
     * examples.
     *
     * @param panel
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
     *                                  null, or if there is already a named
     *                                  value in the model
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
     * @param key
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
     * Set the page this panel is associated to.  This method will set the
     * sub-panels pages as well
     *
     * @param page
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
     * The template associated with this Panel.  If null, the simple class name
     * plus the default extension (".htm") will be used.
     *
     * @return the template assocaited with this panel
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the Velocity template to be used for this Panel.
     *
     * @param template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * The name for this panel.  This is used to uniquely identify the panel
     * in the model context.
     *
     * @return the name for this panel
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name for this panel.  This is used to uniquely identify the panel
     * in the model context.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The id for this panel.  This is the identifier that will be assigned to
     * the 'id' tag for this panel's model.
     *
     * @return the id for this panel
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id for this panel.  This is the identifier that will be assigned
     * to the 'id' tag for this panel's model.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
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
        return panels;
    }

    /**
     * The label associated with this panel.  This can be used to i18n the title
     * associated to this panel.
     *
     * @return the label for this panel
     */
    public String getLabel() {
        return label == null ? name : label;
    }

    /**
     * Set the label associated with this panel.  This can be used to i18n the
     * title associated to this panel.
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Overridden toString to return the name of the template associated with
     * this panel.
     * <p/>
     * TODO: Question: Should this not be done in favor of rendering the panel
     * template implicitly?  i.e. #parse($somePanelName.template) A potential
     * issue might be in debugger evaluation of the toString method.
     *
     * @return the template associated with this panel-see {@link #getTemplate()}
     */
    public String toString() {
        return getTemplate();
    }
}
