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
package org.apache.click.extras.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.click.Context;

import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a Tabbed Form control: &nbsp; &lt;form method='post'&gt;.
 *
 * <table class='htmlHeader' cellspacing='10'>
 * <tr>
 * <td>
 * <img align='middle' hspace='2'src='tabbed-form.png' title='TabbedForm'/>
 * </td>
 * </tr>
 * </table>
 *
 * This class provides a JavaScript enabled tab sheet for control. This control
 * is particularly useful for large forms as fields can be grouped into
 * fieldsets and displayed in individual tab sheets.
 * <p/>
 * The rendered field error messages are tab sheet aware so that clicking on
 * a error message link will open the appropriate tab sheet.
 *
 * <h3>TabbedForm Example</h3>
 *
 * A form delivery details editor example, with separate "Contact Details", "Delivery Details"
 * and "Payment Details" tab sheets, is provided below. Note how tabsheets
 * are created with {@link FieldSet} control which are added to the form.
 *
 * <pre class="prettyprint">
 * public DeliveryDetailsEditor() {
 *
 *     form.setBackgroundColor("#F7FFAF");
 *     form.setTabHeight("210px");
 *     form.setTabWidth("420px");
 *
 *     // Contact tab sheet
 *
 *     FieldSet contactTabSheet = new FieldSet("contactDetails");
 *     form.addTabSheet(contactTabSheet);
 *
 *     contactTabSheet.add(new TitleSelect("title"));
 *     contactTabSheet.add(new TextField("firstName"));
 *     contactTabSheet.add(new TextField("middleNames"));
 *     contactTabSheet.add(new TextField("surname", true));
 *     contactTabSheet.add(contactNumber);
 *     contactTabSheet.add(new EmailField("email"));
 *
 *     // Delivery tab sheet
 *
 *     FieldSet deliveryTabSheet = new FieldSet("deliveryDetails");
 *     form.addTabSheet(deliveryTabSheet);
 *
 *     TextArea textArea = new TextArea("deliveryAddress", true);
 *     textArea.setCols(30);
 *     textArea.setRows(3);
 *     deliveryTabSheet.add(textArea);
 *
 *     deliveryTabSheet.add(new DateField("deliveryDate"));
 *
 *     PackagingRadioGroup packaging = new PackagingRadioGroup("packaging");
 *     packaging.setValue("STD");
 *     deliveryTabSheet.add(packaging);
 *
 *     deliveryTabSheet.add(telephoneOnDelivery);
 *
 *     // Payment tab sheet
 *
 *     FieldSet paymentTabSheet = new FieldSet("paymentDetails");
 *     form.addTabSheet(paymentTabSheet);
 *
 *     paymentGroup.add(new Radio("cod", "Cash On Delivery"));
 *     paymentGroup.add(new Radio("credit", "Credit Card"));
 *     paymentGroup.setVerticalLayout(false);
 *     paymentTabSheet.add(paymentGroup);
 *
 *     paymentTabSheet.add(cardName);
 *     paymentTabSheet.add(cardNumber);
 *     paymentTabSheet.add(expiry);
 *     expiry.setSize(4);
 *     expiry.setMaxLength(4);
 *
 *     // Buttons
 *
 *     form.add(new Submit("ok", "   OK   ",  this, "onOkClick"));
 *     form.add(new Submit("cancel", this, "onCancelClick"));
 *
 *     addControl(form);
 * } </pre>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * In addition to <a href="../../../../../../click-api/org/apache/click/control/Form.html#resources">Form's resources</a>,
 * the TabbedForm control makes use of the following resources (which Click
 * automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.css</tt></li>
 * </ul>
 *
 * To import these TabbedForm files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 */
public class TabbedForm extends Form {

    // Constants --------------------------------------------------------------

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
    protected List<FieldSet> tabSheets = new ArrayList<FieldSet>();

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
     * Create a new tabbed form instance with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public TabbedForm() {
        setErrorsStyle("");
        setButtonStyle("");
    }

    // Properities ------------------------------------------------------------

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
     * Set the number of the tab sheet to display (indexed from 1).
     *
     * @param value the number of the tab sheet to display
     */
    public void setDisplayTab(int value) {
        this.displayTab = value;
    }

    /**
     * Return the TabbedForm HTML HEAD elements for the following resources:
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
    public List<FieldSet> getTabSheets() {
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
            FieldSet fieldSet = getTabSheets().get(i);
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
            List<Field> errorFields = getErrorFields();
            if (!errorFields.isEmpty()) {
                Field field = errorFields.get(0);
                int sheetNumber = getTabSheetNumber(field.getName());
                setDisplayTab(sheetNumber);
            }
        }

        return result;
    }

    /**
     * Render the HTML representation of the TabbedForm.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("form", this);

        buffer.append(getContext().renderTemplate(getTemplate(), model));
    }

    /**
     * Return the HTML string representation of the form. The form will
     * be rendered using the classpath template:
     *
     * <pre class="codeConfig">
     * /org/apache/click/extras/control/TabbedForm.htm </pre>
     *
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @return the HTML string representation of the form
     */
    @Override
    public String toString() {
        return super.toString();
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
