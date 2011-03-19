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
import java.util.List;
import java.util.Map;
import org.apache.click.Context;

import org.apache.click.control.Field;
import org.apache.click.control.Option;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.dataprovider.DataProvider;
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
 * objects similar to a <code>Select</code> field.
 *
 * <h3>PickList Examples</h3>
 *
 * The following code shows how the previous example was rendered:
 * <p/>
 *
 * <a name="picklist-example"></a>
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public void onInit() {
 *
 *         PickList pickList = new PickList("languages");
 *         pickList.setHeaderLabel("Languages", "Selected");
 *
 *         pickList.add(new Option("001", "Java"));
 *         pickList.add(new Option("002", "Ruby"));
 *         pickList.add(new Option("003", "Perl"));
 *
 *         // Set the Java as a selected option
 *         pickList.addSelectedValue("001");
 *     }
 * } </pre>
 *
 * Unless you use a <a href="#dataprovider">DataProvider</a>, remember to always
 * populate the PickList option list before it is processed. Do not populate the
 * option list in a Page's onRender() method.
 *
 * <h3><a name="dataprovider"></a>DataProvider</h3>
 * A common issue new Click users face is which page event (onInit or onRender)
 * to populate the PickList {@link #getOptionList() optionList} in. To alleviate
 * this problem you can set a
 * {@link #setDataProvider(org.apache.click.dataprovider.DataProvider) dataProvider}
 * which allows the PickList to fetch data when needed. This is
 * particularly useful if retrieving PickList data is expensive e.g. loading
 * from a database.
 * <p/>
 * Below is a simple example:
 *
 * <pre class="prettyprint">
 * public class LanguagePage extends Page {
 *
 *     public Form form = new Form();
 *
 *     private Select languagePickList = new PickList("languages");
 *
 *     public LanguagePage() {
 *
 *         // Set a DataProvider which "getData" method will be called to
 *         // populate the optionList. The "getData" method is only called when
 *         // the optionList data is needed
 *         languagePickList.setDataProvider(new DataProvider() {
 *             public List getData() {
 *                 List options = new ArrayList();
 *                 options.add(new Option("001", "Java"));
 *                 options.add(new Option("002", "Ruby"));
 *                 options.add(new Option("003", "Perl"));
 *                 return options;
 *             }
 *         });
 *
 *         form.add(languagePickList);
 *
 *         form.add(new Submit("ok", "  OK  "));
 *     }
 * } </pre>
 *
 * <h3><a name="selected-values"></a>Retrieving selected values</h3>
 *
 * The selected values can be retrieved from {@link #getSelectedValues()}.
 *
 * <pre class="prettyprint">
 * public void onInit() {
 *     ...
 *     form.add(pickList);
 *
 *     // Add a submit button with a listener
 *     form.add(new Submit("OK", this, "onSubmitClick"));
 * }
 *
 * public boolean onSubmitClick() {
 *     if (form.isValid()) {
 *         Set selectedValues = languagePickList.getSelectedValues();
 *         for (Object languageValue : selectedValues) {
 *             ...
 *         }
 *     }
 * } </pre>
 *
 * <h3><a name="resources"></a>CSS and JavaScript resources</h3>
 *
 * The PickList control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.js</tt></li>
 * </ul>
 *
 * To import these CheckList files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 */
public class PickList extends Field {

    private static final long serialVersionUID = 1L;

    // Constants --------------------------------------------------------------

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

    // Instance Variables -----------------------------------------------------

    /** The select data provider. */
    protected DataProvider<Option> dataProvider;

    /**
     * The list height. The default height is 8.
     */
    protected int height = 8;

    /**
     * The Option list.
     */
    protected List<Option> optionList;

    /**
     * The label text for the selected list.
     */
    protected String selectedLabel;

    /**
     * The selected values.
     */
    protected List<String> selectedValues;

    /**
     * The component size (width) in pixels. The default size is 400px.
     */
    protected int size = 400;

    /**
     * The label text for the unselected list.
     */
    protected String unselectedLabel;

    // Constructors -----------------------------------------------------------

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

    // Public Attributes ------------------------------------------------------

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
     * Add the given Option/String/Number/Boolean to the PickList.
     *
     * @param option one of either Option/String/Number/Boolean to add
     * @throws IllegalArgumentException if option is null, or the option
     * is an unsupported class
     */
    public void add(Object option) {
        if (option instanceof Option) {
            getOptionList().add((Option) option);

        } else if (option instanceof String) {
            getOptionList().add(new Option(option.toString()));

        } else if (option instanceof Number) {
            getOptionList().add(new Option(option.toString()));

        } else if (option instanceof Boolean) {
            getOptionList().add(new Option(option.toString()));

        } else {
            String message = "Unsupported options class "
                + option.getClass().getName() + ". Please use method "
                + "PickList.addAll(Collection, String, String) instead.";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Add the given Option/String/Number/Boolean collection to the PickList.
     *
     * @param options the collection of Option/String/Number/Boolean
     * objects to add
     * @throws IllegalArgumentException if options is null, or the collection
     * contains an unsupported class
     */
    public void addAll(Collection<?> options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }

        if (!options.isEmpty()) {
            for (Object option : options) {
                add(option);
            }
        }
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
    public void addAll(Map<?, ?> options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (Map.Entry<?, ?> entry : options.entrySet()) {
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
            String option = options[i];
            getOptionList().add(new Option(option, option));
        }
    }

    /**
     * Add the given collection of objects to the PickList, creating new Option
     * instances based on the object properties specified by optionValueProperty
     * and optionLabelProperty.
     *
     * <pre class="prettyprint">
     *   PickList list = new PickList("type", "Type:");
     *   list.addAll(getCustomerService().getCustomerTypes(), "id", "name);
     *   form.add(list); </pre>
     *
     * For example, given a Collection of CustomerType <tt>objects</tt>,
     * <tt>optionValueProperty</tt> "id" and <tt>optionLabelProperty</tt> "name",
     * the <tt>id</tt> and <tt>name</tt> properties of each CustomerType will
     * be retrieved. For each CustomerType in the Collection a new
     * {@link org.apache.click.control.Option} instance is created and its
     * <tt>value</tt> and <tt>label</tt> is set to the <tt>id</tt>
     * and <tt>name</tt> retrieved from the CustomerType instance.
     *
     * @param objects the collection of objects to render as options
     * @param optionValueProperty the name of the object property to render as
     * the Option value
     * @param optionLabelProperty the name of the object property to render as
     * the Option label
     * @throws IllegalArgumentException if objects or optionValueProperty
     * parameter is null
     */
    public void addAll(Collection<?> objects, String optionValueProperty,
        String optionLabelProperty) {
        if (objects == null) {
            String msg = "objects parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        if (optionValueProperty == null) {
            String msg = "optionValueProperty parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }

        if (objects.isEmpty()) {
            return;
        }

        Map<?, ?> methodCache = new HashMap<Object, Object>();

        for (Object object : objects) {
            try {
                Object valueResult = PropertyUtils.getValue(object,
                    optionValueProperty, methodCache);

                // Default labelResult to valueResult
                Object labelResult = valueResult;

                // If optionLabelProperty is specified, lookup the labelResult
                // from the object
                if (optionLabelProperty != null) {
                    labelResult = PropertyUtils.getValue(object,
                        optionLabelProperty, methodCache);
                }

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
     * Return the PickList optionList DataProvider.
     *
     * @return the PickList optionList DataProvider
     */
    public DataProvider<Option> getDataProvider() {
        return dataProvider;
    }

    /**
     * Set the PickList option list DataProvider. The dataProvider must return
     * a list containing Option values.
     * <p/>
     * Example usage:
     *
     * <pre class="prettyprint">
     * PickList pickList = new PickList("languages");
     * pickList.setHeaderLabel("Languages", "Selected");
     *
     * select.setDataProvider(new DataProvider() {
     *     public List getData() {
     *         List options = new ArrayList();
     *         options.add(new Option("001", "Java"));
     *         options.add(new Option("002", "Ruby"));
     *         options.add(new Option("003", "Perl"));
     *         return options;
     *     }
     * }); </pre>
     *
     * @param dataProvider the PickList option list DataProvider
     */
    @SuppressWarnings("unchecked")
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        if (dataProvider != null) {
            if (optionList != null) {
                ClickUtils.getLogService().warn("please note that setting a"
                    + " dataProvider nullifies the optionList");
            }
            setOptionList(null);
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
    public List<Option> getOptionList() {
        if (optionList == null) {

            DataProvider<Option> dp = getDataProvider();

            if (dp != null) {
                Iterable<Option> iterableData = dp.getData();

                if (iterableData instanceof List<?>) {
                    // Set optionList to data
                    setOptionList((List<Option>) iterableData);

                } else {
                    // Create and populate the optionList from the Iterable data
                    optionList = new ArrayList<Option>();

                    if (iterableData != null) {
                        // Populate optionList from options
                        for (Object option : iterableData) {
                            add(option);
                        }
                    }
                }
            } else {
                // Create empty list
                optionList = new ArrayList<Option>();
            }
        }
        return optionList;
    }

    /**
     * Set the Option list.
     *
     * @param options the Option list
     */
    public void setOptionList(List<Option> options) {
        optionList = options;
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
     * Returns the PickList HTML HEAD elements for the
     * <tt>click/extras-control.js</tt> resource.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new JsImport("/click/extras-control.js", versionIndicator));
        }
        return headElements;
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
    public void setSelectedValues(Collection<?> objects, String value) {
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

        Map<?, ?> cache = new HashMap<Object, Object>();

        for (Object object : objects) {
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
     * Add the selected value to the List of {@link #selectedValues}.
     *
     * @param value the selected value to add
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
     * Return the list of selected values as a <tt>List</tt> of Strings. The
     * returned List will contain the values of the Options selected.
     *
     * @return selected values as a List of Strings
     */
    public List<String> getSelectedValues() {
        if (selectedValues == null) {
            selectedValues = new ArrayList<String>();
        }
        return selectedValues;
    }

    /**
     * Set the list of selected values. The specified values must be Strings and
     * match the values of the Options.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * PickList pickList = new PickList("languages");
     *
     * public void onInit() {
     *     pickList.add(new Option("005", "Java"));
     *     pickList.add(new Option("006", "Ruby"));
     *     pickList.add(new Option("007", "Perl"));
     *     ...
     * }
     *
     * public void onRender() {
     *     // Preselect Java and Perl.
     *     List selected = new ArrayList();
     *     selected.add("005");
     *     selected.add("007");
     *     pickList.setSelectedValues(selected);
     * } </pre>
     *
     * @param selectedValues the list of selected string values or null
     */
    public void setSelectedValues(List<String> selectedValues) {
        this.selectedValues = selectedValues;
    }

    /**
     * This method delegates to {@link #getSelectedValues()} to return the
     * selected values as a <tt>java.util.List</tt> of Strings.
     *
     * @see org.apache.click.control.Field#getValueObject()
     * @see #getSelectedValues()
     *
     * @return selected values as a List of Strings
     */
    @Override
    public Object getValueObject() {
        return getSelectedValues();
    }

    /**
     * This method delegates to {@link #setSelectedValues(java.util.List)}
     * to set the selected values of the PickList. The given object
     * parameter must be a <tt>java.util.List</tt> of Strings, otherwise it is
     * ignored.
     * <p/>
     * The List of values match the values of the Options.
     *
     * @see org.apache.click.control.Field#setValueObject(java.lang.Object)
     * @see #setSelectedValues(java.util.List)
     *
     * @param object a List of Strings
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setValueObject(Object object) {
        if (object instanceof List<?>) {
            setSelectedValues((List<String>) object);
        }
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
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[3];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("field-required-error", getErrorLabel());
        return MessageFormat.format(VALIDATE_PICKLIST_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Bind the request submission, setting the {@link #selectedValues}
     * property if defined in the request.
     */
    @Override
    public void bindRequestValue() {

        // Load the selected items.
        List<String> localSelectedValues = new ArrayList<String>();

        String[] parameterValues =
            getContext().getRequest().getParameterValues(getName());

        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                localSelectedValues.add(parameterValue);
            }
        }

        setSelectedValues(localSelectedValues);
    }

    /**
     * Return the PickList state. The following state is returned:
     * <ul>
     * <li>{@link #getSelectedValues()}</li>
     * </ul>
     *
     * @return the PickList state
     */
    @Override
    public Object getState() {
            List selectedState = getSelectedValues();
        if (selectedState.isEmpty()) {
            return null;
        } else {
            return selectedState.toArray(new String[0]);
        }
    }

    /**
     * Set the PickList state.
     *
     * @param state the PickList state to set
     */
    @Override
    public void setState(Object state) {
        if (state == null) {
            return;
        }

        String[] pickListState = (String[]) state;
        List<String> localSelectedState = new ArrayList<String>(pickListState.length);
        for (String val : pickListState) {
            localSelectedState.add(val);
        }
        setSelectedValues(localSelectedState);
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
    @Override
    public void validate() {
        setError(null);
        List<String> values = getSelectedValues();

        if (values.size() > 0) {
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
    @Override
    public void render(HtmlStringBuffer buffer) {
        List<Option> localOptionList      = getOptionList();
        List<String> localSelectedValues  = getSelectedValues();
        List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();

        for (Option option : localOptionList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("option", option);
            map.put("selected", localSelectedValues.contains(option.getValue()));
            options.add(map);
        }

        // Add all attributes to buffer
        HtmlStringBuffer attributesBuffer = new HtmlStringBuffer();

        // Add the CSS class 'picklist' to buffer
        if (hasAttribute("class")) {
            String cssClass = getAttribute("class");
            attributesBuffer.append("class=\"");
            if (cssClass != null) {
                attributesBuffer.append(cssClass).append(" ");
            }
            attributesBuffer.append("picklist\"");
        } else {
            attributesBuffer.appendAttribute("class", "picklist");
        }

        if (hasAttributes()) {
            Map<String, String> localAttributes = getAttributes();
            for (Map.Entry<String, String> entry : localAttributes.entrySet()) {
                String attrName = entry.getKey();
                if (!attrName.equals("id") && !attrName.equals("class")) {
                    attributesBuffer.appendAttributeEscaped(attrName, entry.getValue());
                }
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("id", getId());
        model.put("attributes", attributesBuffer.toString());
        model.put("name", getName());
        model.put("options", options);
        model.put("selectedLabel", selectedLabel);
        model.put("unselectedLabel", unselectedLabel);
        model.put("format", new Format());
        if (getSize() != 0) {
            model.put("size", getSize());
        }
        model.put("height", getHeight());
        model.put("valid", isValid());
        model.put("disabled", isDisabled());
        model.put("readOnly", isReadonly());

        renderTemplate(buffer, model);
    }

    /**
     * Return a HTML rendered PickList string.
     *
     * @return a HTML rendered PickList string
     */
    @Override
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(2250);
        render(buffer);
        return buffer.toString();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render a Velocity template for the given data model.
     *
     * @param buffer the specified buffer to render the template output to
     * @param model the model data to merge with the template
     */
    protected void renderTemplate(HtmlStringBuffer buffer, Map<String, Object> model) {
        buffer.append(getContext().renderTemplate(PickList.class, model));
    }

}
