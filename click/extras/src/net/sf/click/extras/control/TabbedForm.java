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
package net.sf.click.extras.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;

/**
 * TODO: work in progress
 *
 * @author Malcolm Edgar
 */
public class TabbedForm extends Form {

    private static final long serialVersionUID = -5131480863117157372L;

    /** The list of FieldSet tab sheets. */
    protected List tabSheets = new ArrayList();

    /** The Velocity template override path. */
    protected String template;

    // ---------------------------------------------------------- Constructors

    /**
     * Create a new tabbed form instance with the given name.
     *
     * @param name the name of the form
     */
    public TabbedForm(String name) {
        super(name);
    }

    /**
     * Create a new tabbed form instance.
     */
    public TabbedForm() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add the given FieldSet tab sheet to the form.
     *
     * @param tabSheet the FieldSet tab sheet to add
     */
    public void addTabSheet(FieldSet tabSheet) {
        if (tabSheet == null) {
            throw new IllegalArgumentException("Null tabSeet parameter");
        }
        getTabSheets().add(tabSheet);
        add(tabSheet);
    }

    /**
     * Return list FieldSet tab sheets.
     *
     * @return list FieldSet tab sheets
     */
    public List getTabSheets() {
        return tabSheets;
    }

    /**
     * Return the Velocity override path. If the returned value is not null
     * this template will be used to render tabbed form.
     *
     * @return the override Velocity template path
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set the override Velocity template path. If this value is not null
     * this template will be used to render the tabbed form.
     *
     * @param template the Velocity template to render
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the HTML string representation of the form. The form will
     * be rendered using the classpath template:
     *
     * <pre class="codeConfig">
     * /net/sf/click/extras/control/TabbedForm.htm </pre>
     * 
     * TODO: discuss template override
     *
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        Map model = new HashMap();
        model.put("form", this);
        
        if (getTemplate() != null) {
            return getContext().renderTemplate(getTemplate(), model);
            
        } else {
            return getContext().renderTemplate(getClass(), model);
        }
    }

}
