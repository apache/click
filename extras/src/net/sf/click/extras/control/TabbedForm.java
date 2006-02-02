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

    /** The form HTML background color, default value: &nbsp; "#eed" */
    protected String backgroundColor = "#EFEFEF";

    /** The tab sheet height HTML attribute value. */
    protected String tabHeight = "";

    /** The list of FieldSet tab sheets. */
    protected List tabSheets = new ArrayList();

    /** The tab sheet width HTML attribute value. */
    protected String tabWidth = "";

    /**
     * The path of the tabbed form Velocity template to render: &nbsp;
     * <tt>"/net/sf/click/extras/control/TabbedForm.htm"</tt>
     */
    protected String template = "/net/sf/click/extras/control/TabbedForm.htm";

    // ---------------------------------------------------------- Constructors

    /**
     * Create a new tabbed form instance with the given name.
     *
     * @param name the name of the form
     */
    public TabbedForm(String name) {
        super(name);
        setErrorsStyle("");
        setButtonStyle("");
    }

    /**
     * Create a new tabbed form instance.
     */
    public TabbedForm() {
        setErrorsStyle("");
        setButtonStyle("");
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
        tabSheet.setShowBorder(false);
        getTabSheets().add(tabSheet);
        add(tabSheet);
    }

    /**
     * Return the form HTML background color.
     *
     * @return the form HTML background color
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set the form HTML background color.
     *
     * @param value the form HTML background color
     */
    public void setBackgroundColor(String value) {
        this.backgroundColor = value;
    }

    public String getTabHeight() {
        return tabHeight;
    }

    public void setTabHeight(String value) {
        this.tabHeight = value;
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
     * Return the tab sheet number for the given field name, indexed from 1.
     * If the field is not found this method will return 1.
     *
     * @param fieldName the name of the form field
     * @return the tab sheet number for the given field (indexed from 1)
     */
    public int getTabSheetNumber(String fieldName) {
        for (int i = 0; i < getTabSheets().size(); i++) {
            FieldSet fieldSet = (FieldSet) getTabSheets().get(i);
            if (fieldSet.getFields().containsKey(fieldName)) {
                return i + 1;
            }
        }
        return 1;
    }

    public String getTabWidth() {
        return tabWidth;
    }

    public void setTabWidth(String value) {
        this.tabWidth = value;
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

        return getContext().renderTemplate(getTemplate(), model);
    }

}
