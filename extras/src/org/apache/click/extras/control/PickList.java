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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.click.control.Field;
import org.apache.click.control.Option;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.Format;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.click.util.PropertyUtils;

/**
 * Provides a twin multiple Select box control to select items.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <table width="400" class="picklist">
 * <tr>
 *   <th>Languages</th>
 *   <td></td>
 *   <th>Selected</th>
 * </tr>
 * <tr>
 *   <td width="50%">
 *     <select size="8" style="width:100%;" multiple>
 *       <option>Ruby</option>
 *       <option>Perl</option>
 *     </select>
 *   </td>
 *   <td>
 *     <input type="button" value="&gt;" style="width:60px;"/><br>
 *     <input type="button" value="&lt;" style="width:60px;"/><br>
 *     <input type="button" value="&gt;&gt;" style="width:60px;"/><br>
 *     <input type="button" value="&lt;&lt;" style="width:60px;"/>
 *   </td>
 *   <td width="50%">
 *     <select size="8" style="width:100%;" multiple>
 *       <option>Java</option>
 *     </select>
 *   </td>
 * </tr>
 * </table>
 * </td></tr></table>
 *
 * The values of the <code>PickList</code> are provided by <code>Option</code>
 * objects like for a <code>Select</code>.
 *
 * <h3>PickList Examples</h3>
 *
 * The following code shows the previous rendering example:
 *
 * <pre class="codeJava">
 * PickList pickList = <span class="kw">new</span> PickList(<span class="st">"languages"</span>);
 * pickList.setHeaderLabel(<span class="st">"Languages"</span>, <span class="st">"Selected"</span>);
 *
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"001"</span>, <span class="st">"Java"</span>));
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"002"</span>, <span class="st">"Ruby"</span>));
 * pickList.add(<span class="kw">new</span> Option(<span class="st">"003"</span>, <span class="st">"Perl"</span>));
 *
 * pickList.addSelectedValue(<span class="st">"001"</span>); </pre>
 *
 * The selected values can be retrieved from {@link #getSelectedValues()}.
 *
 * <pre class="codeJava">
 * Set selectedValues = pickList.getSelectedValues();
 *
 * <span class="kw">for</span> (Iterator i = selectedValues.iterator(); i.hasNext();){
 *     String value = (String) i.next();
 *     ...
 * } </pre>
 *
 * @author Naoki Takezoe
 */
public class PickList extends Field {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------- Constants

    /** The <tt>Palette.js</tt> imports statement. */
    public static final String HTML_IMPORTS =
        "<script type=\"text/javascript\" src=\"{0}/click/extras-control{1}.js\"></script>\n";

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message for required validation</li>
     * </ul>
     */
    protected final static String VALIDATE_PICKLIST_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validatePickList(\n"
        + "         ''{0}'',{1}, [''{2}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // ----------------------------------------------------- Instance Variables

    /**
     * The list height. The default height is 8.
     */
    protected int height = 8;

    /**
     * The Option list.
     */
    protected List optionList;

    /**
     * The label text for the selected list.
     */
    protected String selectedLabel;

    /**
     * The selected values.
     */
    protected List selectedValues;

    /**
     * The component size (width) in pixels. The default size is 400px.
     */
    protected int size = 400;

    /**
     * The label text for the unselected list.
     */
    protected String unselectedLabel;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a PickList field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public PickList(String name, String label) {
        super(name, label);
    }

    /**
     * Create a PickList field with the given name.
     *
     * @param name the name of the field
     */
    public PickList(String name) {
        super(name);
    }

    /**
     * Create a PickList with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public PickList() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the given Option to the PickList.
     *
     * @param option the Option value to add
     * @throws IllegalArgumentException if option is null
     */
    public void add(Option option) {
        if (option == null) {
            String msg = "option parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(option);
    }

    /**
     * Add the given Option collection to the PickList.
     *
     * @param options the collection of Option objects to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Collection options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().addAll(options);
    }

    /**
     * Add the given Map of option values and labels to the PickList. The Map
     * entry key will be used as the option value and the Map entry value will
     * be used as the option label.
     * <p/>
     * It is recommended that <tt>LinkedHashMap</tt> is used as the Map
     * parameter to maintain the order of the option vales.
     *
     * @param options the Map of option values and labels to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Map options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (Iterator i = options.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Option option = new Option(entry.getKey().toString(), entry
                    .getValue().toString());
            getOptionList().add(option);
        }
    }

    /**
     * Add the given array of string options to the PickList. <p/> The
     * options array string value will be used for the {@link Option#value} and
     * {@link Option#label}.
     *
     * @param options the array of option values to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(String[] options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < options.length; i++) {
            String value = options[i];
            getOptionList().add(new Option(value, value));
        }
    }

    /**
     * Add the given collection of objects to the PickList, creating new Option
     * instances based on the object properties specified by value and label.
     *
     * <pre class="prettyprint">
     *   PickList list = new PickList("type", "Type:");
     *   list.addAll(getCustomerService().getCustomerTypes(), "id", "name);
     *   form.add(list); </pre>
     *
     * For example given the Collection of CustomerType <tt>objects</tt>,
     * <tt>value</tt> "id" and <tt>label</tt> "name", the <tt>id</tt> and
     * <tt>name</tt> properties of each CustomerType will be retrieved. For each
     * CustomerType in the Collection a new {@link org.apache.click.control.Option}
     * instance is created and its <tt>value</tt> and <tt>label</tt> is set to
     * the <tt>value</tt> and <tt>label</tt> retrieved from the CustomerType
     * instance.
     *
     * @param objects the collection of objects to render as options
     * @param value the name of the object property to render as the Option value
     * @param label the name of the object property to render as the Option label
     * @throws IllegalArgumentException if options, value or label parameter is null
     */
    public void addAll(Collection objects, String value, String label) {
        if (objects == null) {
            String msg = "objects parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (label == null) {
            String msg = "label parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }

        if (objects.isEmpty()) {
            return;
        }

        Map cache = new HashMap();

        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object object = i.next();

            try {
                Object valueResult = PropertyUtils.getValue(object, value, cache);
                Object labelResult = PropertyUtils.getValue(object, label, cache);

                Option option = null;

                if (labelResult != null) {
                    option = new Option(valueResult, labelResult.toString());
                } else {
                    option = new Option(valueResult.toString());
                }

                getOptionList().add(option);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Set the header label text for the selected list and the unselected list.
     * The specified text is displayed at the top of the list.
     *
     * @param unselectedLabel the label text for the unselected list
     * @param selectedLabel the label text for the selected list
     */
    public void setHeaderLabel(String unselectedLabel, String selectedLabel) {
        this.unselectedLabel = unselectedLabel;
        this.selectedLabel = selectedLabel;
    }

    /**
     * Return the Option list.
     *
     * @return the Option list
     */
    public List getOptionList() {
        if (optionList == null) {
            optionList = new ArrayList();
        }
        return optionList;
    }

    /**
     * Return the list height.
     *
     * @return the list height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the list height.
     *
     * @param  height the list height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Return the HTML head import statements for the JavaScript
     * (<tt>click/extras-control.js</tt>) file.
     *
     * @see org.apache.click.Control#getHtmlImports()
     *
     * @return the HTML head import statements for the JavaScript file
     */
    public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
    }

    /**
     * The PickList selected values will be derived from the given collection of
     * objects, based on the object properties specified by value.
     * <p/>
     * Example usage:
     * <pre class="prettyprint">
     *   PickList list = new PickList("type", "Type:");
     *
     *   // Fill the PickList with product types
     *   list.addAll(getCustomerService().getProductTypes(), "id", "name");
     *
     *   // Set the PickList selected values to the list of products of the
     *   // current customer
     *   list.setSelectedValues(getCustomer().getProductTypes(), "id");
     *   form.add(list); </pre>
     *
     * For example given the Collection of ProductType <tt>objects</tt> and the
     * <tt>value</tt> "id", the <tt>id</tt> property of each ProductType will
     * be retrieved and added to the PickList {@link #selectedValues}.
     *
     * @param objects the collection of objects to render selected values
     * @param value the name of the object property to render as the Option value
     * @throws IllegalArgumentException if options or value parameter is null
     */
    public void setSelectedValues(Collection objects, String value) {
        if (objects == null) {
            String msg = "objects parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }

        if (objects.isEmpty()) {
            return;
        }

        Map cache = new HashMap();

        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object object = i.next();

            try {
                Object valueResult = PropertyUtils.getValue(object, value, cache);

                if (valueResult != null) {
                    addSelectedValue(valueResult.toString());
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Add the selected value.
     *
     * @param value the selected value
     * @throws IllegalArgumentException if the value is null
     */
    public void addSelectedValue(String value) {
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getSelectedValues().add(value);
    }

    /**
     * Return selected values.
     *
     * @return selected values
     */
    public List getSelectedValues() {
        if (selectedValues == null) {
            selectedValues = new ArrayList();
        }
        return selectedValues;
    }

    /**
     * Set the component size.
     *
     * @param  size the component size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the component size (width) in pixels.
     *
     * @return the component size
     */
    public int getSize() {
        return size;
    }

    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        Object[] args = new Object[3];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("field-required-error", getErrorLabel());
        return MessageFormat.format(VALIDATE_PICKLIST_FUNCTION, args);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the {@link #selectedValues}
     * property if defined in the request.
     */
    public void bindRequestValue() {

        // Page developer has not initialized options
        if (getOptionList().isEmpty()) {
            return;
        }

        // Load the selected items.
        this.selectedValues = new ArrayList();

        String[] values =
            getContext().getRequest().getParameterValues(getName());

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                selectedValues.add(values[i]);
            }
        }
    }

    /**
     * Deploy the <tt>extras-control.js</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see org.apache.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/org/apache/click/extras/control/extras-control.js",
                              "click");
    }

    /**
     * Validate the PickList request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>org.apache.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>field-required-error</li>
     * </ul></blockquote>
     */
    public void validate() {
        setError(null);
        List value = getSelectedValues();

        if (value.size() > 0) {
            return;

        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }

    /**
     * Render the HTML representation of the PickList.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        List optionList     = getOptionList();
        List selectedValues = getSelectedValues();
        List options        = new ArrayList();

        for (int i = 0; i < optionList.size(); i++) {
            Option option = (Option) optionList.get(i);
            Map map = new HashMap();
            map.put("option", option);
            map.put("selected", new Boolean(selectedValues.contains(option.getValue())));
            options.add(map);
        }

        Map model = new HashMap();

        model.put("id", getId());
        model.put("name", getName());
        model.put("options", options);
        model.put("selectedLabel", selectedLabel);
        model.put("unselectedLabel", unselectedLabel);
        model.put("format", new Format());
        model.put("size", new Integer(getSize()));
        model.put("height", new Integer(getHeight()));
        model.put("valid", new Boolean(isValid()));
        model.put("disabled", new Boolean(isDisabled()));
        model.put("readOnly", new Boolean(isReadonly()));

        renderTemplate(buffer, model);
    }

    /**
     * Return a HTML rendered PickList string.
     *
     * @return a HTML rendered PickList string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(2250);
        render(buffer);
        return buffer.toString();
    }

    // -------------------------------------------------------- Protected Methods

    /**
     * Render a Velocity template for the given data model.
     *
     * @param buffer the specified buffer to render the template output to
     * @param model the model data to merge with the template
     */
    protected void renderTemplate(HtmlStringBuffer buffer, Map model) {
        buffer.append(getContext().renderTemplate(PickList.class, model));
    }

}
