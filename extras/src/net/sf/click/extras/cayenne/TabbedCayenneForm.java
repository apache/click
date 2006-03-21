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
package net.sf.click.extras.cayenne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.extras.control.TabbedForm;

import org.apache.commons.lang.StringUtils;

/**
 * TODO: doco.
 *
 * @author Malcolm Edgar
 */
public class TabbedCayenneForm extends CayenneForm {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /**
     * The form HTML background color. The default background color is
     * "#EFEFEF".
     */
    protected String backgroundColor = "#EFEFEF";

    /**
     * The tab number to display (indexed from 1). The default tab number is 1.
     */
    protected int displayTab = 1;

    /** The tab sheet height HTML attribute value. */
    protected String tabHeight = "";

    /** The list of FieldSet tab sheets. */
    protected List tabSheets = new ArrayList();

    /** The tab sheet width HTML attribute value. */
    protected String tabWidth = "";

    /**
     * The path of the tabbed form Velocity template to render. The
     * default template path is
     * <tt>"/net/sf/click/extras/control/TabbedForm.htm"</tt>.
     */
    protected String template = "/net/sf/click/extras/control/TabbedForm.htm";

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Tabbed Cayenne Form with the given form name and
     * <tt>DataObject</tt> class.
     *
     * @param name the form name
     * @param dataClass the <tt>DataObject</tt> class
     */
    public TabbedCayenneForm(String name, Class dataClass) {
        super(name, dataClass);
        setErrorsStyle("");
        setButtonStyle("");
    }

    // ------------------------------------------------------------ Properities

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

    /**
     * Return the number of the tab sheet to display (indexed from 1).
     *
     * @return the number of the tab sheet to display
     */
    public int getDisplayTab() {
        return displayTab;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/TabbedForm.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(TabbedForm.TABBED_FORM_IMPORTS, "$", path);
    }

    /**
     * Set the number of the tab sheet to display (indexed from 1).
     *
     * @param value the number of the tab sheet to display
     */
    public void setDisplayTab(int value) {
        this.displayTab = value;
    }

    /**
     * Return the tab sheet height HTML attribute value.
     *
     * @return the tab sheet height attribute value
     */
    public String getTabHeight() {
        return tabHeight;
    }

    /**
     * Set the tab sheet height HTML attribute value.
     *
     * @param value the tab sheet height attribute value
     */
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

    /**
     * Return the tab sheet width HTML attribute value.
     *
     * @return the tab sheet width attribute value
     */
    public String getTabWidth() {
        return tabWidth;
    }

    /**
     * Set the tab sheet width HTML attribute value.
     *
     * @param value the tab sheet width attribute value
     */
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
     * Process the Form request. In addition to the normal Form
     * <tt>onProcess()</tt> processing, if the Form is invalid this method
     * will display the tab sheet with the first field error.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        boolean result = super.onProcess();

        if (!isValid()) {
            List errorFields = getErrorFields();
            if (!errorFields.isEmpty()) {
                Field field = (Field) errorFields.get(0);
                int sheetNumber = getTabSheetNumber(field.getName());
                setDisplayTab(sheetNumber);
            }
        }

        return result;
    }

    /**
     * Return the HTML string representation of the form. The form will
     * be rendered using the classpath template:
     *
     * <pre class="codeConfig">
     * /net/sf/click/extras/control/TabbedForm.htm </pre>
     *
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        applyMetaData();

        Map model = new HashMap();
        model.put("form", this);

        return getContext().renderTemplate(getTemplate(), model);
    }

}
