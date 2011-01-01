/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.cayenne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.click.Context;

import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides Cayenne data aware tabbed Form control: &nbsp; &lt;form method='POST'&gt;.
 * <p/>
 * This control provides tab capabilities to the standard CayenneForm.
 * For more information and examples please see:
 * <ul>
 * <li>{@link CayenneForm}</li>
 * <li>{@link org.apache.click.extras.control.TabbedForm}</li>
 * </ul>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * In addition to <a href="../../../../../../click-api/org/apache/click/control/Form.html#resources">Form's resources</a>,
 * the TabbedCayenneForm control makes use of the following resources (which Click
 * automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.css</tt></li>
 * </ul>
 *
 * To import these TabbedCayenneForm files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 */
public class TabbedCayenneForm extends CayenneForm {

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

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
     * <tt>"/org/apache/click/extras/control/TabbedForm.htm"</tt>.
     */
    protected String template = "/org/apache/click/extras/control/TabbedForm.htm";

    // Constructors -----------------------------------------------------------

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

    /**
     * Create a Tabbed Cayenne Form with the given <tt>DataObject</tt> class.
     *
     * @param dataClass the <tt>DataObject</tt> class
     */
    public TabbedCayenneForm(Class dataClass) {
        super(dataClass);
        setErrorsStyle("");
        setButtonStyle("");
    }

    /**
     * Create a Tabbed Cayenne Form with no name or dataObjectClass.
     * <p/>
     * <b>Important Note</b> the form's name and dataObjectClass must be defined
     * before it is valid.
     */
    public TabbedCayenneForm() {
        setErrorsStyle("");
        setButtonStyle("");
    }

    // Properties ------------------------------------------------------------

    /**
     * Add the given FieldSet tab sheet to the form.
     *
     * @param tabSheet the FieldSet tab sheet to add
     */
    public void addTabSheet(FieldSet tabSheet) {
        if (tabSheet == null) {
            throw new IllegalArgumentException("Null tabSheet parameter");
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
     * Return the TabbedCayenneForm HTML HEAD elements for the following resources:
     *
     * <ul>
     * <li><tt>click/extras-control.js</tt></li>
     * <li><tt>click/extras-control.css</tt></li>
     * </ul>
     *
     * Additionally all the {@link org.apache.click.control.Form#getHeadElements()
     * Form import statements} are also returned.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the control list of HEAD elements to be included in the page
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new CssImport("/click/extras-control.css", versionIndicator));
            headElements.add(new JsImport("/click/extras-control.js", versionIndicator));
        }
        return headElements;
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

    // Public Methods ---------------------------------------------------------

    /**
     * Process the Form request. In addition to the normal Form
     * <tt>onProcess()</tt> processing, if the Form is invalid this method
     * will display the tab sheet with the first field error.
     *
     * @return true to continue Page event processing or false otherwise
     */
    @Override
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
     * Render the HTML representation of the form.
     * <p/>
     * The form will be rendered using the classpath template:
     *
     * <pre class="codeConfig">
     * /org/apache/click/extras/control/TabbedForm.htm </pre>
     *
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        applyMetaData();

        Map model = new HashMap();
        model.put("form", this);

        buffer.append(getContext().renderTemplate(getTemplate(), model));
    }

    @Override
    protected void renderValidationJavaScript(HtmlStringBuffer buffer, List<Field> formFields) {

        // Render JavaScript form validation code
        if (isJavaScriptValidation()) {
            List<String> functionNames = new ArrayList<String>();

            buffer.append("<script type=\"text/javascript\"><!--\n");

            // Render field validation functions & build list of function names
            for (Field field : formFields) {
                String fieldJS = field.getValidationJavaScript();
                if (fieldJS != null) {
                    buffer.append(fieldJS);

                    StringTokenizer tokenizer = new StringTokenizer(fieldJS);
                    tokenizer.nextToken();
                    functionNames.add(tokenizer.nextToken());
                }
            }

            if (!functionNames.isEmpty()) {
                buffer.append("function on_");
                buffer.append(getId());
                buffer.append("_submit() {\n");
                buffer.append("   var msgs = new Array(");
                buffer.append(functionNames.size());
                buffer.append(");\n");
                for (int i = 0; i < functionNames.size(); i++) {
                    buffer.append("   msgs[");
                    buffer.append(i);
                    buffer.append("] = ");
                    buffer.append(functionNames.get(i).toString());
                    buffer.append(";\n");
                }
                buffer.append("   return Click.validateTabbedForm(msgs, '");
                buffer.append(getId());
                buffer.append("', '");
                buffer.append(getErrorsAlign());
                buffer.append("', ");
                if (getErrorsStyle() == null) {
                    buffer.append("null");
                } else {
                    buffer.append("'" + getErrorsStyle() + "'");
                }
                buffer.append(");\n");
                buffer.append("}\n");

            } else {
                buffer.append("function on_");
                buffer.append(getId());
                buffer.append("_submit() { return true; }\n");
            }
            buffer.append("//--></script>\n");
        }
    }
}
