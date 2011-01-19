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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.click.Context;
import org.apache.click.control.Field;
import org.apache.click.control.Option;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.click.util.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a check list control. This is an implementation of the Checklist
 * from <a href="http://c82.net/article.php?ID=25">Check it don't select it</a>
 * <p/>
 * A check list is a more user friendly substitution for
 * multiple-select-boxes. It is a scrollable div which has many select-boxes.
 *
 * <h3><a name="checklist-example"></a>CheckList Examples</h3>
 *
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public void onInit() {
 *
 *         CheckList checkList = new ChecList("languages");
 *
 *         checkList.add(new Option("001", "Java"));
 *         checkList.add(new Option("002", "Ruby"));
 *         checkList.add(new Option("003", "Perl"));
 *
 *         // Set the Java as a selected option
 *         checkList.addSelectedValue("001");
 *     }
 * } </pre>
 *
 * Unless you use a <a href="#dataprovider">DataProvider</a>, remember to always
 * populate the CheckList option list before it is processed. Do not populate the
 * option list in a Page's onRender() method.
 *
 * <h3><a name="dataprovider"></a>DataProvider</h3>
 * A common issue new Click users face is which page event (onInit or onRender)
 * to populate the CheckList {@link #getOptionList() optionList} in. To alleviate
 * this problem you can set a
 * {@link #setDataProvider(org.apache.click.dataprovider.DataProvider) dataProvider}
 * which allows the CheckList to fetch data when needed. This is
 * particularly useful if retrieving CheckList data is expensive e.g. loading
 * from a database.
 * <p/>
 * Below is a simple example:
 *
 * <pre class="prettyprint">
 * public class LanguagePage extends Page {
 *
 *     public Form form = new Form();
 *
 *     private Select languageCheckList = new CheckList("languages");
 *
 *     public LanguagePage() {
 *
 *         // Set a DataProvider which "getData" method will be called to
 *         // populate the optionList. The "getData" method is only called when
 *         // the optionList data is needed
 *         languageCheckList.setDataProvider(new DataProvider() {
 *             public List getData() {
 *                 List options = new ArrayList();
 *                 options.add(new Option("001", "Java"));
 *                 options.add(new Option("002", "Ruby"));
 *                 options.add(new Option("003", "Perl"));
 *                 return options;
 *             }
 *         });
 *
 *         form.add(languageCheckList);
 *
 *         form.add(new Submit("ok", "  OK  "));
 *     }
 * } </pre>
 *
 * CheckList also supports a scrollable mode. To make the CheckList scrollable,
 * set the height of the CheckList through {@link #setHeight(String)}.
 * <p/>
 * <b>Note</b> when setting the height it is recommended that the CheckList
 * should not be sortable, because of browser incompatibilities.
 * <p/>
 * The CheckList is also sortable by drag-drop if the
 * {@link #setSortable(boolean)} property is set to true. In this case the
 * method {@link #getSortorder()} returns the keys of all the options whether
 * they where selected or not in the order provided by the user.
 * <p/>
 * Sortable is provided by scriptaculous which is only supported on IE6, FF and
 * Safari1.2 and higher. This control is only tested on IE6 and FF on Windows.
 * With IE the text of the dragged element has a black-outline which does not
 * look good. To turn this off define an explicit back-ground color for the
 * &lt;li&gt; elements. Typically you will do this in a style: .listClass li
 * {background-color: #xxx}, where the listClass is set through
 * {@link #addStyleClass(String)}.
 * <p/>
 * If a select is required at least one item must be
 * selected so that the input is valid. Other validations are not done.
 * <p/>
 * The Control listener will be invoked in any case whether the CheckList is valid or not.
 * <p/>
 * The values of the CheckList are provided by
 * {@link org.apache.click.control.Option} instances. To populate the CheckList with
 * Options, use the add methods.
 * <p/>
 * The label of the Option is shown to the user and the value is what is
 * provided in the {@link #getSelectedValues()} and {@link #getSortorder()}
 * Lists.
 * <p/>
 * The selected values can be retrieved from {@link #getSelectedValues()}. The
 * get/setValue() property is not supported.
 * <p/>
 * The select uses the /click/checklist/checklist.css style. By providing a style which
 * extends this style the appearance of the list can be changed.
 * <p/>
 * To set the additional style class use {@link #addStyleClass(String)}.
 * This will append the given class to the default class of this control.
 * Alternatively {@link #setStyle(String, String)} can be used to set the style
 * of the outer div.
 * <p/>
 * For an example please look at the click-examples and the at the above blog.
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The CheckList control makes use of the following resources
 * (which Click automatically deploys to the application directories,
 * <tt>/click/checklist</tt> and <tt>/click/prototype</tt>):
 *
 * <ul>
 * <li><tt>click/checklist/checklist.css</tt></li>
 * <li><tt>click/checklist/checklist.js</tt></li>
 * <li><tt>click/prototype/builder.js</tt></li>
 * <li><tt>click/prototype/controls.js</tt></li>
 * <li><tt>click/prototype/dragdrop.js</tt></li>
 * <li><tt>click/prototype/effects.js</tt></li>
 * <li><tt>click/prototype/prototype.js</tt></li>
 * <li><tt>click/prototype/slider.js</tt></li>
 * </ul>
 *
 * To import these CheckList files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 *
 * @see org.apache.click.control.Option
 */
public class CheckList extends Field {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /** The style class which is always set on this element (checkList). */
    protected static final String STYLE_CLASS = "checkList";

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the name of the checkbox</li>
     * <li>2 - is the id of the form</li>
     * <li>3 - is the Field required status</li>
     * <li>4 - is the localized error message for required validation</li>
     * </ul>
     */
    protected final static String VALIDATE_CHECKLIST_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateCheckList(''{1}'', ''{2}'', {3}, [''{4}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Instance Variables -----------------------------------------------------

    /** The select data provider. */
    protected DataProvider<Option> dataProvider;

    /** The height if null not scrollable. */
    protected String height;

    /** The Select Option list. */
    protected List<Option> optionList;

    /** If sortable by drag and drop. */
    protected boolean sortable;

    /**
     * The key of the values in the order they are present (only set when
     * sortable).
     */
    protected List<String> sortorder;

    /** The selected values. */
    protected List<String> selectedValues;

    // Constructors -----------------------------------------------------------

    /**
     * Create a CheckList field with the given name.
     *
     * @param name the name of the field
     */
    public CheckList(String name) {
        super(name);
    }

    /**
     * Create a CheckList field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public CheckList(String name, String label) {
        super(name, label);
    }

    /**
     * Create a CheckList field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public CheckList(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Create a CheckList field with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public CheckList(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Create a CheckList field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CheckList() {
    }

    // Public Attributes ------------------------------------------------------

    /**
     * @see org.apache.click.control.AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    @Override
    public String getTag() {
        return "div";
    }

    /**
     * Add the given Option.
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
     * Add the given option value. This convenience method will create a new
     * {@link Option} with the given value and add it to the CheckList. The new
     * Option display label will be the same as its value.
     *
     * @param value the option value to add
     * @throws IllegalArgumentException if the value is null
     */
    public void add(String value) {
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(new Option(value));
    }

    /**
     * Add the given Option/String/Number/Boolean to the CheckList.
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
                + "CheckList.addAll(Collection, String, String) instead.";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Add the given Option/String/Number/Boolean collection to the CheckList.
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
     * Add the given Map of option values and labels to the CheckList. The Map
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
     * Add the given array of string options to the Select option list. <p/> The
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
        for (String option : options) {
            getOptionList().add(new Option(option, option));
        }
    }

    /**
     * Add the given collection of objects to the CheckList, creating new Option
     * instances based on the object properties specified by optionValueProperty
     * and optionLabelProperty.
     *
     * <pre class="prettyprint">
     *   CheckList list = new CheckList("type", "Type:");
     *   list.addAll(getCustomerService().getCustomerTypes(), "id", "name");
     *   form.add(select); </pre>
     *
     * For example given the Collection of CustomerType <tt>objects</tt>,
     * <tt>optionValueProperty</tt> "id" and <tt>optionLabelProperty</tt> "name",
     * the <tt>id</tt> and <tt>name</tt> properties of each CustomerType will be
     * retrieved. For each CustomerType in the Collection a new
     * {@link org.apache.click.control.Option} instance is created and its
     * <tt>value</tt> and <tt>label</tt> is set to the <tt>id</tt> and
     * <tt>name</tt> retrieved from the CustomerType instance.
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
     * Return the CheckList optionList DataProvider.
     *
     * @return the CheckList optionList DataProvider
     */
    public DataProvider<Option> getDataProvider() {
        return dataProvider;
    }

    /**
     * Set the CheckList option list DataProvider. The dataProvider must return
     * a list containing Option values.
     * <p/>
     * Example usage:
     *
     * <pre class="prettyprint">
     * CheckList checkList = new CheckList("languages");
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
     * @param dataProvider the CheckList option list DataProvider
     */
    public void setDataProvider(DataProvider<Option> dataProvider) {
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
     * Adds the given style-value pair to the style attribute of the outer div.
     * Does not check whether the style is already set.
     * <p/>
     * Typically used for the width:
     *
     * <pre class="codeJava">
     * list.addStyle(<span class="st">"&quot;width: 100%; height: 25em&quot;"</span>); </pre>
     *
     * @param style the style name:value pair without a ending ;
     *
     * @deprecated use @{link #setStyle(String, String)}
     */
    public void addStyle(String style) {
        if (StringUtils.isBlank(style)) {
            throw new IllegalArgumentException("The style is empty");
        }
        style = style.trim();

        if (style.charAt(style.length() - 1) == ';') {
            style = style + ";";
        }

        String old = getAttribute("style");
        if (old == null || (old = old.trim()).length() == 0) {
            setAttribute("style", style);
        } else {
            if (old.charAt(old.length() - 1) != ';') {
                old = old + ';';
            }
            old = old + style;
            setAttribute("style", old);
        }
    }

    /**
     * Return the Field focus JavaScript.
     *
     * @return the Field focus JavaScript
     */
    @Override
    public String getFocusJavaScript() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append("setFocus('");
        buffer.append(getName() + "_0");
        buffer.append("');");

        return buffer.toString();
    }

    /**
     * The css height attribute-value.
     * If null no height is set and the CheckList is not scrollable.
     *
     * @param height one of css height values (ie 40px) or null.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * The css-height attribute.
     *
     * @return Returns the height or null.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Set the given html class. The class will be set on the select list
     * together with the {@link #STYLE_CLASS}. Ie class="checkList my-class"
     * where my-class is the set class. The default value is null.
     *
     * @deprecated use {@link #addStyleClass(String)} instead
     *
     * @param clazz the class to set or null
     */
    public void setHtmlClass(String clazz) {
        addStyleClass(clazz);
    }

    /**
     * The html class to set on this control.
     *
     * @see #setHtmlClass(String)
     *
     * @deprecated use {@link #getAttribute(String)} instead
     *
     * @return the class or null (default null)
     */
    public String getHtmlClass() {
        return getAttribute("class");
    }

    /**
     * Return the CheckList HEAD elements to be included in the page.
     * The following resources are returned:
     * <ul>
     * <li><tt>click/checklist/checklist.css</tt></li>
     * <li><tt>click/checklist/checklist.js</tt></li>
     * <li><tt>click/prototype/builder.js</tt></li>
     * <li><tt>click/prototype/controls.js</tt></li>
     * <li><tt>click/prototype/dragdrop.js</tt></li>
     * <li><tt>click/prototype/effects.js</tt></li>
     * <li><tt>click/prototype/prototype.js</tt></li>
     * <li><tt>click/prototype/slider.js</tt></li>
     * </ul>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML head import statements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements = super.getHeadElements();

            headElements.add(new CssImport("/click/checklist/checklist.css",
                versionIndicator));
            headElements.add(new JsImport("/click/checklist/checklist.js",
                versionIndicator));

            if (isSortable()) {
                headElements.add(new JsImport("/click/prototype/prototype.js",
                    versionIndicator));
                headElements.add(new JsImport("/click/prototype/builder.js",
                    versionIndicator));
                headElements.add(new JsImport("/click/prototype/effects.js",
                    versionIndicator));
                headElements.add(new JsImport("/click/prototype/dragdrop.js",
                    versionIndicator));
                headElements.add(new JsImport("/click/prototype/controls.js",
                    versionIndicator));
                headElements.add(new JsImport("/click/prototype/slider.js",
                    versionIndicator));
            }
        }

        String checkListId = getId();
        JsScript script = new JsScript();
        script.setId(checkListId + "-js-setup");

        if (!headElements.contains(script)) {
            script.setExecuteOnDomReady(true);

            HtmlStringBuffer buffer = new HtmlStringBuffer(50);

            if (isSortable()) {
                if (getHeight() != null) {
                    buffer.append("Position.includeScrollOffset = true;\n");
                }
                // Script to execute
                buffer.append("Sortable.create('");
                buffer.append(StringEscapeUtils.escapeJavaScript(checkListId));
                buffer.append("-ul'");

                if (getHeight() != null) {
                    buffer.append(", { scroll : '");
                    buffer.append(StringEscapeUtils.escapeJavaScript(checkListId));
                    buffer.append("'}");
                }
                buffer.append(");");

            } else {
                buffer.append("initChecklist('");
                buffer.append(StringEscapeUtils.escapeJavaScript(checkListId));
                buffer.append("-ul');\n");
            }
            script.setContent(buffer.toString());
            headElements.add(script);
        }

        return headElements;
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
     * Set the Option list. Note: if the CheckList is sortable
     * than the List <b>must be fully modifiable</b>, because
     * it will be sorted according to the order chosen by the
     * user.
     *
     * @param options a list of Option objects
     */
    public void setOptionList(List<Option> options) {
        optionList = options;
    }
    /**
     * Whether the list should be drag-drop sortable. This is supported by
     * scriptaculous. Note when the list also has a size than this might not work
     * on different browsers.
     *
     * @param sortable default is false.
     */
    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * Whether the list is also sortable.
     *
     * @return Returns the sortable.
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * A list of the values transmitted in the order they are present in the
     * list. This is only available if the list is sortable
     *
     * @return Returns list of strings of the option values.
     */
    public List<String> getSortorder() {
        return sortorder;
    }

    /**
     * Return the list of selected values as a <tt>List</tt> of Strings. The
     * returned List will contain the values of the Options selected.
     *
     * @deprecated use {@link #getSelectedValues()} instead
     *
     * @return a list of Strings
     */
    public List<String> getValues() {
        return getSelectedValues();
    }

    /**
     * Return the list of selected values as a <tt>List</tt> of Strings. The
     * returned List will contain the values of the Options selected.
     *
     * @return the list of selected values
     */
    public List<String> getSelectedValues() {
        if (selectedValues != null) {
            return selectedValues;

        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Set the list of selected values. The specified values must be Strings and
     * match the values of the Options.
     *
     * @deprecated use {@link #setSelectedValues(List)} instead
     *
     * @param values a list of strings or null
     */
    public void setValues(List<String> values) {
        this.selectedValues = values;
    }

    /**
     * Set the list of selected values. The specified values must be Strings and
     * match the values of the Options.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * CheckList checkList = new CheckList("checkList");
     *
     * public void onInit() {
     *     List options = new ArrayList();
     *     options.add(new Option("1", "Option 1");
     *     options.add(new Option("2", "Option 2");
     *     options.add(new Option("3", "Option 3");
     *     checkList.setOptionList(options);
     *     ...
     * }
     *
     * public void onRender() {
     *     // Preselect some Options.
     *     List selected = new ArrayList();
     *     selected.add("1"));
     *     selected.add("3");
     *     checkList.setSelectedValues(selected);
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
     * to set the selected values of the CheckList. The given object parameter
     * must be a <tt>java.util.List</tt> of Strings, otherwise it is ignored.
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
     * Return the CheckList JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[5];
        args[0] = getId();
        args[1] = getName();
        args[2] = getForm().getId();
        args[3] = String.valueOf(isRequired());
        args[4] = getMessage("field-required-error", getErrorLabel());

        return MessageFormat.format(VALIDATE_CHECKLIST_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Bind the request submission, setting the {@link #selectedValues} and
     * sort order if the checkList is sortable.
     */
    @Override
    public void bindRequestValue() {

        Context context = getContext();

        // Page developer has not initialized options, which are required
        // to support sorting
        if (getOptionList().isEmpty()) {
            return;
        }

        // Load the selected items.
        List<String> localSelectedValues = new ArrayList<String>();

        String[] parameterValues = context.getRequestParameterValues(getName());

        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                localSelectedValues.add(parameterValue);
            }
        }

        if (isSortable()) {
            String[] orderParameterValues = context.getRequest().getParameterValues(
                    getName() + "_order");
            if (orderParameterValues != null) {
                this.sortorder = new ArrayList<String>(orderParameterValues.length);
                for (String orderParameterValue : orderParameterValues) {
                    if (orderParameterValue != null) {
                        sortorder.add(orderParameterValue);
                    }
                }
                sortOptions(orderParameterValues);
            }
        }
        setSelectedValues(localSelectedValues);
    }

    /**
     * Process the request Context setting the CheckList selectedValues if
     * selected and invoking the control's listener if defined.
     *
     * @return true to continue Page event processing, false otherwise
     */
    @Override
    public boolean onProcess() {
        if (isDisabled()) {
            Context context = getContext();

            // Switch off disabled property if control has incoming request
            // parameter. Normally this means the field was enabled via JS
            if (context.hasRequestParameter(getName())) {
                setDisabled(false);
            } else {
                // If field is disabled skip process event
                return true;
            }
        }

        // In Html an unchecked CheckList does not submit it's name/value so we
        // always validate and dispatch registered events
        bindRequestValue();

        if (getValidate()) {
            validate();
        }

        dispatchActionEvent();

        return true;
    }

    /**
     * Sorts the current Options List. This method is called
     * in {@link #bindRequestValue()} when the CheckList
     * is sortable.
     *
     * @param order values in the order to sort the list.
     */
    protected void sortOptions(String[] order) {
        final List<Option> options = getOptionList();
        final List<Option> orderList = new ArrayList<Option>(options.size());

        for (int i = 0, size = order.length; i < size; i++) {
            String orderValue = order[i];
            if (orderValue != null) {
                int oI = -1;
                for (int j = 0, jSize = options.size(); j < jSize; j++) {
                    Option optT = options.get(j);
                    if (orderValue.equals(optT.getValue())) {
                        oI = j;
                    }
                }
                if (oI != -1) {
                    orderList.add(options.remove(oI));
                }
            }
        }
        options.addAll(0, orderList);
    }

    /**
     * @see org.apache.click.control.AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    @Override
    public int getControlSizeEst() {
        int bufferSize = 50;
        if (!getOptionList().isEmpty()) {
            bufferSize = bufferSize + (optionList.size() * 48);
        }
        return bufferSize;
    }

    /**
     * Render the HTML representation of the CheckList.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        final boolean sortable = isSortable();

        // the div element
        buffer.elementStart(getTag());

        buffer.appendAttribute("id", getId());

        // style class
        addStyleClass(STYLE_CLASS);

        if (isValid()) {
            removeStyleClass("error");
        } else {
            addStyleClass("error");
        }

        // set the style
        setStyle("height", getHeight());

        if (sortable && getHeight() == null) {
            setStyle("overflow", "hidden");
        } else {
            setStyle("overflow", "auto");
        }

        appendAttributes(buffer);

        buffer.closeTag();

        // the ul tag
        buffer.elementStart("ul");
        buffer.append(" id=\"").append(getId()).append("-ul\"");
        buffer.closeTag();

        // the options
        List<Option> optionsList = getOptionList();
        if (!optionsList.isEmpty()) {
            int i = -1;
            for (Option option : optionsList) {
                i++;

                buffer.append("<li>");
                if (sortable) {
                    buffer.elementStart("div");
                    buffer.appendAttribute("style", "cursor:move;");
                } else {
                    buffer.elementStart("label");
                    buffer.append(" for=\"").append(getName()).append('_').append(i).append("\"");
                }
                buffer.appendAttribute("class", "checkListLabel");
                buffer.closeTag();

                buffer.append("<input type=\"checkbox\" ");
                buffer.appendAttributeEscaped("value", option.getValue());
                buffer.append(" id=\"").append(getName()).append('_').append(i).append("\"");
                buffer.appendAttribute("name", getName());

                if (sortable) {
                    buffer.appendAttribute("style", "cursor:default;");
                }

                // set checked status
                boolean checked = false;
                List<String> values = getSelectedValues();
                for (int k = 0; k < values.size(); k++) {
                    if (String.valueOf(values.get(k)).equals(option.getValue())) {
                        checked = true;
                    }
                }

                if (checked) {
                    buffer.appendAttribute("checked", "checked");
                }
                if (isReadonly() || isDisabled()) {
                    buffer.appendAttributeDisabled();
                }
                buffer.elementEnd();
                buffer.appendEscaped(option.getLabel());

                if (sortable) {
                    buffer.append("</div>");
                } else {
                    buffer.append("</label>");
                }

                if (checked && (isReadonly() || isDisabled())) {
                    buffer.elementStart("input");
                    buffer.appendAttribute("type", "hidden");
                    buffer.appendAttribute("name", getName());
                    buffer.appendAttributeEscaped("value", option.getValue());
                    buffer.elementEnd();
                }

                // hiddenfield if sortable

                if (sortable) {
                    buffer.append("<input type=\"hidden\"");
                    buffer.appendAttribute("name", getName() + "_order");
                    buffer.appendAttributeEscaped("value", option.getValue());
                    buffer.elementEnd();
                }

                buffer.append("</li>");
            }
        }
        buffer.append("</ul>");
        buffer.elementEnd(getTag());
    }

    /**
     * Validate the CheckList request submission.
     * <p/>
     * If a CheckList is {@link #required} then the user must select a value,
     * otherwise the Select will have a validation error. If the Select is not
     * required then no validation errors will occur.
     * <p/>
     * A field error message is displayed if a validation error occurs. These
     * messages are defined in the resource bundle: <blockquote>
     *
     * <pre class="codeConfig>
     *  /click-control.properties </pre>
     *
     * </blockquote> <p/> Error message bundle key names include: <blockquote>
     * <ul>
     * <li>select-error</li>
     * </ul>
     * </blockquote>
     */
    @Override
    public void validate() {
        if (isRequired()) {
            if (getSelectedValues().isEmpty()) {
                setErrorMessage("select-error");
            }
        }
    }

}
