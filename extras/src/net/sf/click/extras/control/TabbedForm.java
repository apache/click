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
package net.sf.click.extras.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.util.ClickUtils;

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
 * A form delivery details editor example, with separate "Contact Detais", "Delivery Details"
 * and "Payment Details" tab sheets, is provided below. Note how tabsheets
 * are created with {@link FieldSet} control which are added to the form.
 *
 * <pre class="codeJava">
 * <span class="kw">public</span> DeliveryDetailsEditor() {
 *
 *     form.setBackgroundColor(<span class="st">"#F7FFAF"</span>);
 *     form.setTabHeight(<span class="st">"210px"</span>);
 *     form.setTabWidth(<span class="st">"420px"</span>);
 *
 *     <span class="green">// Contact tab sheet</span>
 *
 *     FieldSet contactTabSheet = <span class="kw">new</span> FieldSet(<span class="st">"contactDetails"</span>);
 *     form.addTabSheet(contactTabSheet);
 *
 *     contactTabSheet.add(<span class="kw">new</span> TitleSelect(<span class="st">"title"</span>));
 *     contactTabSheet.add(<span class="kw">new</span> TextField(<span class="st">"firstName"</span>));
 *     contactTabSheet.add(<span class="kw">new</span> TextField(<span class="st">"middleNames"</span>));
 *     contactTabSheet.add(<span class="kw">new</span> TextField(<span class="st">"surname"</span>, <span class="kw">true</span>));
 *     contactTabSheet.add(contactNumber);
 *     contactTabSheet.add(<span class="kw">new</span> EmailField(<span class="st">"email"</span>));
 *
 *     <span class="green">// Delivery tab sheet</span>
 *
 *     FieldSet deliveryTabSheet = <span class="kw">new</span> FieldSet("deliveryDetails");
 *     form.addTabSheet(deliveryTabSheet);
 *
 *     TextArea textArea = <span class="kw">new</span> TextArea(<span class="st">"deliveryAddress"</span>, <span class="kw">true</span>);
 *     textArea.setCols(30);
 *     textArea.setRows(3);
 *     deliveryTabSheet.add(textArea);
 *
 *     deliveryTabSheet.add(<span class="kw">new</span> DateField(<span class="st">"deliveryDate"</span>));
 *
 *     PackagingRadioGroup packaging = <span class="kw">new</span> PackagingRadioGroup(<span class="st">"packaging"</span>);
 *     packaging.setValue(<span class="st">"STD"</span>);
 *     deliveryTabSheet.add(packaging);
 *
 *     deliveryTabSheet.add(telephoneOnDelivery);
 *
 *     <span class="green">// Payment tab sheet</span>
 *
 *     FieldSet paymentTabSheet = <span class="kw">new</span> FieldSet(<span class="st">"paymentDetails"</span>);
 *     form.addTabSheet(paymentTabSheet);
 *
 *     paymentGroup.add(<span class="kw">new</span> Radio(<span class="st">"cod"</span>, <span class="st">"Cash On Delivery"</span>));
 *     paymentGroup.add(<span class="kw">new</span> Radio(<span class="st">"credit"</span>, <span class="st">"Credit Card"</span>));
 *     paymentGroup.setVerticalLayout(false);
 *     paymentTabSheet.add(paymentGroup);
 *
 *     paymentTabSheet.add(cardName);
 *     paymentTabSheet.add(cardNumber);
 *     paymentTabSheet.add(expiry);
 *     expiry.setSize(4);
 *     expiry.setMaxLength(4);
 *
 *     <span class="green">// Buttons</span>
 *
 *     form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"   OK   "</span>,  <span class="kw">this</span>, <span class="st">"onOkClick"</span>));
 *     form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClick"</span>));
 *
 *     addControl(form);
 * } </pre>
 *
 * @author Malcolm Edgar
 */
public class TabbedForm extends Form {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The TabbedForm.css style sheet import link. */
    public static final String HTML_IMPORTS = Form.HTML_IMPORTS
        + "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/extras-control{1}.css\"/>\n";

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
     * Create a new tabbed form instance with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public TabbedForm() {
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
     * Set the number of the tab sheet to display (indexed from 1).
     *
     * @param value the number of the tab sheet to display
     */
    public void setDisplayTab(int value) {
        this.displayTab = value;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet file:
     * <tt>click/TabbedForm.css</tt>.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
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
     * Deploy the <tt>table.css</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/control/extras-control.css",
                              "click");
    }

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
        Map model = new HashMap();
        model.put("form", this);

        return getContext().renderTemplate(getTemplate(), model);
    }

}
