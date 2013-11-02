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
package org.apache.click.control;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.click.dataprovider.DataProvider;
import org.apache.click.service.ConfigService;
import org.apache.click.service.PropertyService;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a Select control: &nbsp; &lt;select&gt;&lt;/select&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Select</td>
 * <td>
 * <select title='Select Control'>
 * <option value='Option 1'>Option 1</option>
 * <option value='Option 2'>Option 2</option>
 * <option value='Option 3'>Option 3</option>
 * </select>
 * </td>
 * </tr>
 * </table>
 *
 * The Control listener will be invoked if the Select is valid and an item(s) is
 * selected by the user.
 *
 * <h3>Select Examples</h3>
 *
 * <h4><a name="single-select-example"></a>Single Item Select</h4>
 * A single item Select, will only allow users to select one item from the list.
 * By default the Select {@link #multiple} item property is false.
 * <p/>
 * If a Select is required, an item after the first in the list must be selected
 * for the Field to be valid. This forces the user to make an active selection.
 *
 * An example of a single item Select is provided below along with the
 * rendered HTML.
 *
 * <pre class="prettyprint">
 * public class GenderPage extends Page {
 *
 *     public Form form = new Form();
 *
 *     private Select genderSelect = new Select("Gender");
 *
 *     public GenderPage() {
 *         genderSelect.setRequired(true);
 *         genderSelect.add(new Option("U", "");
 *         genderSelect.add(new Option("M", "Male"));
 *         genderSelect.add(new Option("F", "Female"));
 *         form.add(genderSelect);
 *
 *         form.add(new Submit("ok", "  OK  "));
 *     }
 *
 *     public void onPost() {
 *         if (form.isValid()) {
 *             String gender = genderSelect.getValue();
 *             ..
 *         }
 *     }
 * } </pre>
 *
 * Rendered HTML:
 * <table class="htmlExample"><tr><td>
 * <table class='form'><tr>
 * <td align='left'><label >Gender</label><font color="red">*</font></td>
 * <td align='left'><select name='gender'size='1'><option value='U'></option><option value='M'>Male</option><option value='F'>Female</option></select></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'>
 * <input type='submit' value='  OK  '/>
 * </td></tr>
 * </table>
 * </td></tr>
 * </table>
 *
 * Note how {@link Option} items are added to the Select. In this
 * example the "U" option will not be a valid selection, as it is the first
 * item in the option list.
 *
 * <h4><a name="multiple-select-example"></a>Multiple Item Select</h4>
 * A multiple item Select, will allow users to select multiple items from the list.
 * By default the Select {@link #multiple} item property is false, and must be
 * enabled for multiple item selects.
 * <p/>
 * If multiple item Select is required, the user must select an item(s) in
 * the list for the Field to be valid. A valid selection can include any item
 * including the first item.
 * <p/>
 * An example of a single item Select is provided below along with the
 * rendered HTML.
 *
 * <pre class="prettyprint">
 * public class LocationPage extends Page {
 *
 *     public Form form = new Form();
 *
 *     private Select locationSelect = new Select("location");
 *
 *     public LocationPage() {
 *         locationSelect.setMultiple(true);
 *         locationSelect.setRequired(true);
 *         locationSelect.setSize(7);
 *         locationSelect.add("QLD");
 *         locationSelect.add("NSW");
 *         locationSelect.add("NT");
 *         locationSelect.add("SA");
 *         locationSelect.add("TAS");
 *         locationSelect.add("VIC");
 *         locationSelect.add("WA");
 *         form.add(locationSelect);
 *
 *         form.add(new Submit("ok", "  OK  "));
 *     }
 *
 *     public void onPost() {
 *         if (form.isValid()) {
 *             String location = locationSelect.getValue();
 *             ..
 *         }
 *     }
 * } </pre>
 *
 * Rendered HTML:
 * <table class="htmlExample"><tr><td>
 * <table class='form'>
 * <tr>
 * <td align='left'><label >Location</label><font color="red">*</font></td>
 * <td align='left'><select name='location' size='7' multiple ><option value='QLD'>QLD</option><option value='NSW'>NSW</option><option value='NT'>NT</option><option value='SA'>SA</option><option value='TAS'>TAS</option><option value='VIC'>VIC</option><option value='WA'>WA</option></select></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'><input type='submit' value='  OK  '/></td></tr>
 * </table>
 * </td></tr>
 * </table>
 *
 * Note in this example the {@link #add(String)} method is used to add an Option
 * item to the Select.
 *
 * <h3><a name="required-behaviour"></a>Required Behaviour</h3>
 *
 * When a Select control's required property is set to true, then the user has
 * to select a value other than the first value in the option list. The  first
 * value represents a non-selection by the user. In the example below an
 * Empty Option is set as the first value in the option list.
 *
 * <pre class="prettyprint">
 * public MyPage extends Page {
 *     ..
 *
 *     private Select mySelect;
 *
 *     public MyPage() {
 *         mySelect = new Select("mySelect");
 *         mySelect.setRequired(true);
 *
 *         ..
 *     }
 *
 *     public void onInit() {
 *         mySelect.add(Option.EMPTY_OPTION);
 *         List&lt;Customer&gt; customerList = customerDao.getCustomerList();
 *         mySelect.addAll(customerList, "id", "name");
 *     }
 *
 *     ..
 * } </pre>
 *
 * Unless you use a <a href="#dataprovider">DataProvider</a>, remember to always
 * populate the Select option list before it is processed. Do not populate the
 * option list in a Page's onRender() method.
 *
 * <h3><a name="readonly-behaviour"></a>Readonly Behaviour</h3>
 *
 * Note the &lt;select&gt; HTML element does not support the "readonly" attribute.
 * To provide readonly style behaviour, the Select control will render the
 * "disabled" attribute when it is readonly to give the appearance of a
 * readonly field, and will render a hidden field of the same name so that its
 * value will be submitted with the form.
 *
 * <h3><a name="dataprovider"></a>DataProvider</h3>
 * A common issue new Click users face is which page event (onInit or onRender)
 * to populate the Select {@link #getOptionList() optionList} in. To alleviate
 * this problem you can set a
 * {@link #setDataProvider(org.apache.click.dataprovider.DataProvider) dataProvider}
 * which allows the Select to fetch data when needed. This is
 * particularly useful if retrieving Select data is expensive e.g. loading
 * from a database.
 * <p/>
 * Below is a simple example:
 *
 * <pre class="prettyprint">
 * public class GenderPage extends Page {
 *
 *     public Form form = new Form();
 *
 *     private Select genderSelect = new Select("Gender");
 *
 *     public GenderPage() {
 *
 *         // Set the Select default "non-selection" option
 *         genderSelect.setDefaultOption(new Option("U", "");
 *
 *         // Set a DataProvider which "getData" method will be called to populate the
 *         // optionList. The "getData" method is only called when the optionList
 *         // data is needed
 *         genderSelect.setDataProvider(new DataProvider() {
 *             public List getData() {
 *                 List options = new ArrayList();
 *                 options.add(new Option("M", "Male"));
 *                 options.add(new Option("F", "Female"));
 *                 return options;
 *             }
 *         });
 *
 *         form.add(genderSelect);
 *
 *         form.add(new Submit("ok", "  OK  "));
 *     }
 *
 *     public void onPost() {
 *         if (form.isValid()) {
 *             String gender = genderSelect.getValue();
 *             ..
 *         }
 *     }
 * } </pre>
 *
 * <h3><a name="default-value"></a>Specify the default selected value</h3>
 *
 * If you need to set the selected value to something other than the first
 * option, set the Select {@link #setValue(java.lang.String) value} to the
 * option value you want to select:
 *
 * <pre class="prettyprint">
 * public MyPage extends Page {
 *     private Select mySelect;
 *
 *     public MyPage() {
 *         mySelect = new Select("mySelect");
 *         mySelect.add("YES");
 *         mySelect.add("NO");
 *
 *         // If you want NO to be selected by default, set the value to "NO"
 *         mySelect.setValue("NO");
 *     }
 * } </pre>
 *
 * See also the W3C HTML reference:
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.6">SELECT</a>
 *
 * @see Option
 * @see OptionGroup
 */
public class Select extends Field {

    private static final long serialVersionUID = 1L;

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message</li>
     * <li>3 - is the default Select option value</li>
     * </ul>
     */
    protected final static String VALIDATE_SELECT_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateSelect(''{0}'', ''{3}'', {1}, [''{2}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Instance Variables -----------------------------------------------------

    /** The multiple options selectable flag. The default value is false. */
    protected boolean multiple;

    /** The Select Option/OptionGroup list. */
    protected List optionList;

    /** The Select display size in rows. The default size is one. */
    protected int size = 1;

    /**
     * The multiple selected values. This list will only be populated if
     * {@link #multiple} is true.
     */
    protected List<String> selectedValues;

    /** The select data provider. */
    @SuppressWarnings("unchecked")
    protected DataProvider dataProvider;

    /**
     * The default option will be the first option added to the Select.
     * This property is often used when populating the Select from a
     * {@link #setDataProvider(org.apache.click.dataprovider.DataProvider)}, where
     * the DataProvider does not return a sensible default option e.g. an
     * empty ("") option.
     */
    protected Option defaultOption;

    /** The column property service. */
    protected PropertyService propertyService;

    // Constructors -----------------------------------------------------------

    /**
     * Create a Select field with the given name.
     *
     * @param name the name of the field
     */
    public Select(String name) {
        super(name);
    }

    /**
     * Create a Select field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public Select(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Select field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public Select(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Create a Select field with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public Select(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Create a Select field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Select() {
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Return the select's html tag: <tt>select</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    @Override
    public String getTag() {
        return "select";
    }

    /**
     * Add the given Option to the Select.
     *
     * @param option the Option value to add
     * @throws IllegalArgumentException if option is null
     */
    public void add(Option option) {
        if (option == null) {
            String msg = "option parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        List optionList = getOptionList();
        optionList.add(option);
        if (optionList.size() == 1) {
            setInitialValue();
        }
    }

    /**
     * Add the given OptionGroup to the Select.
     *
     * @param optionGroup the OptionGroup value to add
     * @throws IllegalArgumentException if optionGroup is null
     */
    public void add(OptionGroup optionGroup) {
        if (optionGroup == null) {
            String msg = "optionGroup parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(optionGroup);
    }

    /**
     * Add the given option value to the Select. This convenience method will
     * create a new {@link Option} with the given value and add it to the
     * Select. The new Option display label will be the same as its value.
     *
     * @param value the option value to add
     * @throws IllegalArgumentException if the value is null
     */
    public void add(String value) {
        if (value == null) {
            String msg = "value parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        List optionList = getOptionList();
        optionList.add(new Option(value));
        if (optionList.size() == 1) {
            setInitialValue();
        }
    }

    /**
     * Add the given Option/OptionGroup/String/Number/Boolean to the Select.
     *
     * @param option one of either Option/OptionGroup/String/Number/Boolean
     *     to add
     * @throws IllegalArgumentException if option is null, or the option
     *     is an unsupported class
     */
    public void add(Object option) {
        List optionList = getOptionList();
        if (option instanceof Option) {
            optionList.add(option);

        } else if (option instanceof OptionGroup) {
            optionList.add(option);

        } else if (option instanceof String) {
            optionList.add(new Option(option.toString()));

        } else if (option instanceof Number) {
            optionList.add(new Option(option.toString()));

        } else if (option instanceof Boolean) {
            optionList.add(new Option(option.toString()));

        } else {
            String message = "Unsupported Option class "
                + option.getClass().getName() + ". Must be one of "
                + "Option, OptionGroup, String, Number or Boolean";
            throw new IllegalArgumentException(message);
        }

        if (optionList.size() == 1) {
            setInitialValue();
        }
    }

    /**
     * Add the given Option/OptionGroup/String/Number/Boolean collection to the
     * Select.
     *
     * @param options the collection of Option/OptionGroup/String/Number/Boolean
     *     objects to add
     * @throws IllegalArgumentException if options is null, or the collection
     *     contains an unsupported class
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
     * Add the given Map of option values and labels to the Select.
     * The Map entry key will be used as the option value and the Map entry
     * value will be used as the option label.
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
            Option option = new Option(entry.getKey().toString(),
                                       entry.getValue().toString());
            getOptionList().add(option);
        }
        setInitialValue();
    }

    /**
     * Add the given array of string options to the Select option list.
     * <p/>
     * The options array string value will be used for the {@link Option#value}
     * and {@link Option#label}.
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
        setInitialValue();
    }

    /**
     * Add the given collection of objects to the Select, creating new Option
     * instances based on the object properties specified by optionValueProperty
     * and optionLabelProperty. If the optionLabelProperty is null, the
     * optionValueProperty will be used as both the value and label of the
     * options.
     * <p/>
     * The collection objects can either be POJOs (plain old java objects) or
     * {@link java.util.Map} instances.
     * <p/>
     * Example usage:
     *
     * <pre class="prettyprint">
     * Select select = new Select("type", "Type:");
     * select.addAll(getCustomerService().getCustomerTypes(), "id", "name");
     * form.add(select); </pre>
     *
     * This method will iterate over all customerTypes and for each customerType
     * create a new Option, setting the option value to the customerType
     * <tt>"id"</tt>, and the option label to the customerType <tt>"name"</tt>.
     *
     * @param objects the collection of objects to render as options
     * @param optionValueProperty the name of the object property to render as
     * the Option value
     * @param optionLabelProperty the name of the object property to render as
     * the Option label
     * @throws IllegalArgumentException if objects or optionValueProperty
     * parameter is null
     */
    public void addAll(Collection objects, String optionValueProperty,
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

        Map methodCache = new HashMap();

        if (propertyService == null) {
            ServletContext sc = getContext().getServletContext();
            ConfigService configService = ClickUtils.getConfigService(sc);
            propertyService = configService.getPropertyService();
        }

        for (Object object : objects) {
            try {
                Object valueResult =
                    propertyService.getValue(object,
                                             optionValueProperty,
                                             methodCache);

                // Default labelResult to valueResult
                Object labelResult = valueResult;

                // If optionLabelProperty is specified, lookup the labelResult
                // from the object
                if (optionLabelProperty != null) {
                    labelResult =
                        propertyService.getValue(object,
                                                 optionLabelProperty,
                                                 methodCache);
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

        setInitialValue();
    }

    /**
     * Return the select option list DataProvider.
     *
     * @return the select option list DataProvider
     */
    @SuppressWarnings("unchecked")
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Set the select option list DataProvider. The dataProvider can return any
     * mixture of Option and OptionGroup values.
     * <p/>
     * Example usage:
     *
     * <pre class="prettyprint">
     * Select select = new Select("name", "Name");
     *
     * // Set the Select default "non-selection" option
     * select.setDefaultOption(new Option("U", ""));
     *
     * select.setDataProvider(new DataProvider() {
     *     public List getData() {
     *         List options = new ArrayList();
     *         options.add(new Option("M", "Male"));
     *         options.add(new Option("F", "Female"));
     *         return options;
     *     }
     * }); </pre>
     *
     * @param dataProvider the select option list DataProvider
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
     * Return the number of Select display rows.
     *
     * @return the number of Select display rows
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the number of the Select display rows.
     *
     * @param rows the Select display size in rows.
     */
    public void setSize(int rows) {
        size = rows;
    }

    /**
     * Return true if multiple options can be selected.
     *
     * @return true if multiple options can be selected
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * Set the multiple options can be selected flag.
     *
     * @param value the multiple options can be selected flag
     */
    public void setMultiple(boolean value) {
        multiple = value;
    }

    /**
     * Return the list of selected values.
     *
     * @deprecated use {@link #getSelectedValues()} instead, this method will
     * be removed in subsequent releases
     *
     * @return the list of selected values
     */
    public List<String> getMultipleValues() {
        return getSelectedValues();
    }

    /**
     * Return the list of selected values.
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
     * Set the list of selected values.
     *
     * @deprecated use {@link #getSelectedValues()} instead, this method will
     * be removed in subsequent releases
     *
     * @param multipleValues the list of selected values
     */
    public void setMultipleValues(List<String> multipleValues) {
        this.selectedValues = multipleValues;
    }

    /**
     * Set the list of selected values.
     *
     * @param multipleValues the list of selected values
     */
    public void setSelectedValues(List<String> multipleValues) {
        this.selectedValues = multipleValues;
    }

    /**
     * Set the Select default option. The default option will be the first option
     * added to the Select {@link #getOptionList() optionList}.
     * <p/>
     * <b>Please note</b>: this property is used in conjunction with the Select
     * {@link #setDataProvider(org.apache.click.dataprovider.DataProvider) dataProvider},
     * where the DataProvider does not return a sensible default, non-selecting
     * option. For example if the DataProvider returns a list of Authors from the
     * database, the list won't include a default empty ("") option to choose
     * from. By setting the defaultOption property, the Select will add this
     * Option as the first option of the Select {@link #getOptionList() optionList}.
     * <p/>
     * In addition, if the Select is {@link #setRequired(boolean) required},
     * the defaultOption is used to check whether the Select is valid or not.
     * In other words, if the user's selected value equals the defaultOption value,
     * the Select won't be valid since no selection was made by the user.
     * <p/>
     * Example usage:
     * <pre class="prettyprint">
     * public void onInit() {
     *     authorSelect.setDefaultOption(Option.EMPTY_OPTION);
     *
     *     authorSelect.setDataProvider(new DataProvider() {
     *         public List getData() {
     *             List options = new ArrayList();
     *             List<Author> authors = getAuthorDao().getAuthors();
     *             for (Author author : authors) {
     *                 options.add(new Option(author.getId(), author.getName()));
     *             }
     *             return options;
     *         }
     *     });
     *     form.add(authorSelect);
     * } </pre>
     *
     * @param option the Select default option
     */
    public void setDefaultOption(Option option) {
        this.defaultOption = option;
    }

    /**
     * Return the Select default option or null if no default option is set.
     *
     * @see #setDefaultOption(org.apache.click.control.Option)
     *
     * @return the Select default option or null if no default option is set
     */
    public Option getDefaultOption() {
        return defaultOption;
    }

    /**
     * Return the Option list.
     *
     * @return the Option list
     */
    public List getOptionList() {
        if (optionList == null) {

            Option defaultOption = getDefaultOption();

            DataProvider dp = getDataProvider();

            if (dp != null) {
                Iterable iterableData = dp.getData();

                if (iterableData instanceof List) {
                    // Set optionList to data
                    List listData = (List) iterableData;
                    if (defaultOption != null) {
                        // Insert default option as first option
                        listData.add(0, defaultOption);
                    }
                    setOptionList(listData);

                } else {
                    // Create and populate the optionList from the Iterable data
                    optionList = new ArrayList();

                    if (iterableData != null) {

                        if (defaultOption != null) {
                            optionList.add(defaultOption);
                        }

                        // Populate optionList from options
                        for (Object option : iterableData) {
                            if (option instanceof Option || option instanceof OptionGroup) {
                                optionList.add(option);
                            } else {
                                String msg = "Select option class not instance of Option"
                                + " or OptionGroup: " + option.getClass().getName();
                                throw new IllegalArgumentException(msg);
                            }
                        }
                    }
                }
            } else {
                // Create empty list
                optionList = new ArrayList();
            }
        }
        return optionList;
    }

    /**
     * Set the Option list.
     *
     * @param options the Option list
     */
    public void setOptionList(List options) {
        optionList = options;
        if (optionList != null) {
            setInitialValue();
        }
    }

    /**
     * Return the Select JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[4];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = getMessage("select-error", getErrorLabel());

        List optionList = getOptionList();
        if (!optionList.isEmpty()) {
            Option option = (Option) optionList.get(0);
            args[3] = option.getValue();
        } else {
            args[3] = "";
        }

        return MessageFormat.format(VALIDATE_SELECT_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Bind the request submission, setting the {@link #value} or
     * {@link #selectedValues} property if defined in the request.
     */
    @Override
    public void bindRequestValue() {

        List<String> localSelectedValues = new ArrayList<String>(5);

        // Process single item select case.
        if (!isMultiple()) {
            // Load the selected item.
            value = getContext().getRequestParameter(getName());
            localSelectedValues.add(value);

        // Process the multiple item select case.
        } else {

            // Load the selected items.
            String[] parameterValues =
                getContext().getRequest().getParameterValues(getName());

            if (parameterValues != null) {
                for (String parameterValue : parameterValues) {
                    localSelectedValues.add(parameterValue);
                }
            }
        }

        setSelectedValues(localSelectedValues);
    }

    /**
     * Return the Select state. The following state is returned, depending on
     * whether {@link #isMultiple()} is <tt>true</tt> or <tt>false</tt>:
     * <ul>
     * <li>{@link #getValue()} if {@link #isMultiple()} is <tt>false</tt></li>
     * <li>{@link #getSelectedValues()} if {@link #isMultiple()} is <tt>true</tt></li>
     * </ul>
     *
     * @return the Select state
     */
    @Override
    public Object getState() {
        if (isMultiple()) {
            List selectedState = getSelectedValues();
            if (selectedState.isEmpty()) {
                return null;
            } else {
                return selectedState.toArray(new String[0]);
            }
        } else {
            return super.getState();
        }
    }

    /**
     * Set the Select state.
     *
     * @param state the Select state to set
     */
    @Override
    public void setState(Object state) {
        if (state == null) {
            return;
        }

        List<String> localSelectedState = null;

        if (state instanceof String) {
            localSelectedState = new ArrayList<String>(1);
            String selectState = (String) state;
            setValue(selectState);
            localSelectedState.add(selectState);
        } else {
            String[] selectState = (String[]) state;
            localSelectedState = new ArrayList<String>(selectState.length);
            for (String val : selectState) {
            localSelectedState.add(val);
        }
        }
        setSelectedValues(localSelectedState);
    }

    /**
     * This method invokes {@link #getOptionList()} to ensure exceptions thrown
     * while retrieving options will be handled by the error page.
     *
     * @see org.apache.click.Control#onRender()
     */
    @Override
    public void onRender() {
        getOptionList();
    }

    /**
     * @see AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    @Override
    public int getControlSizeEst() {
        int bufferSize = 50;
        List optionList = getOptionList();
        if (!optionList.isEmpty()) {
            bufferSize = bufferSize + (optionList.size() * 48);
        }
        return bufferSize;
    }

    /**
     * Render the HTML representation of the Select.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());

        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("size", getSize());
        buffer.appendAttribute("title", getTitle());
        if (isValid()) {
            removeStyleClass("error");
        } else {
            addStyleClass("error");
        }
        if (getTabIndex() > 0) {
            buffer.appendAttribute("tabindex", getTabIndex());
        }
        if (isMultiple()) {
            buffer.appendAttribute("multiple", "multiple");
        }
        if (getFocus()) {
            buffer.appendAttribute("autofocus", "autofocus");
        }

        appendAttributes(buffer);

        if (isDisabled() || isReadonly()) {
            buffer.appendAttributeDisabled();
        }

        buffer.closeTag();

        List optionList = getOptionList();

        if (!optionList.isEmpty()) {
            for (int i = 0, listSize = optionList.size(); i < listSize; i++) {
                Object object = optionList.get(i);

                if (object instanceof Option) {
                    Option option = (Option) object;
                    option.render(this, buffer);

                } else if (object instanceof OptionGroup) {
                    OptionGroup optionGroup = (OptionGroup) object;
                    optionGroup.render(this, buffer);

                } else {
                    String msg = "Select option class not instance of Option"
                        + " or OptionGroup: " + object.getClass().getName();
                    throw new IllegalArgumentException(msg);
                }
            }
        }

        buffer.elementEnd(getTag());

        if (getHelp() != null) {
            buffer.append(getHelp());
        }

        // select element does not support "readonly" element, so as a work around
        // we make the field "disabled" and render a hidden field to submit its value
        if (isReadonly()) {
            buffer.elementStart("input");
            buffer.appendAttribute("type", "hidden");
            buffer.appendAttribute("name", getName());
            buffer.appendAttributeEscaped("value", getValue());
            buffer.elementEnd();
        }
    }

    /**
     * Validate the Select request submission.
     * <p/>
     * If a Select is {@link #required} then the user must select a value
     * other than the first value is the list, otherwise the Select will
     * have a validation error. If the Select is not required then no
     * validation errors will occur.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>select-error</li>
     * </ul></blockquote>
     */
    @Override
    public void validate() {
        setError(null);

        if (isRequired()) {
            if (isMultiple()) {
                if (getSelectedValues().isEmpty()) {
                    setErrorMessage("select-error");
                }

            } else {
                // TODO: if only one item present is this a select error

                if (getValue().length() == 0) {
                    setErrorMessage("select-error");

                } else {
                    String defaultValue = getDefaultOptionValue();

                    // if no defaultValue is present, lookup value from OptionList
                    if (defaultValue == null) {
                        List optionList = getOptionList();

                        if (optionList.isEmpty()) {
                            String msg =
                                "Mandatory Select field " + getName()
                                + " has no options to validate the request"
                                + " against. Solutions are to either set the"
                                + " Select defaultOption(), use a DataProvider"
                                + " or set the optionList in the Page onInit()"
                                + " page event";
                            throw new RuntimeException(msg);
                        }

                        Object firstEntry = optionList.get(0);
                        if (firstEntry instanceof Option) {
                            Option option = (Option) firstEntry;
                            defaultValue = option.getValue();
                        }
                    }

                    if (defaultValue == null) {
                        defaultValue = "";
                    }

                    if (defaultValue.equals(getValue())) {
                        setErrorMessage("select-error");
                    }
                }
            }
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Return the Select {@link #getDefaultOption() defaultOption} value, or
     * null if no defaultOption is set.
     *
     * @see #getDefaultOption()
     * @see #setDefaultOption(org.apache.click.control.Option)
     *
     * @return the Select defaultOption value, or null if no defaultOption is set
     */
    protected String getDefaultOptionValue() {
        Option defaultOption = getDefaultOption();
        if (defaultOption != null) {
            return defaultOption.getValue();
        }
        return null;
    }

    /**
     * Set the initial select option value.
     */
    protected void setInitialValue() {
        if ((getValue().length() == 0) && !getOptionList().isEmpty()) {
            Object object = getOptionList().get(0);

            if (object instanceof String) {
                setValue(object.toString());

            } else if (object instanceof Option) {
                Option option = (Option) object;
                setValue(option.getValue());

            } else if (object instanceof OptionGroup) {
                OptionGroup optionGroup = (OptionGroup) object;

                if (!optionGroup.getChildren().isEmpty()) {
                    Object child = optionGroup.getChildren().get(0);

                    if (child instanceof Option) {
                        Option option = (Option) child;
                        setValue(option.getValue());

                    } else if (child instanceof OptionGroup) {
                        OptionGroup childOptionGroup = (OptionGroup) child;

                        if (!childOptionGroup.getChildren().isEmpty()) {
                            Object cogChild = childOptionGroup.getChildren().get(0);

                            if (cogChild instanceof Option) {
                                Option option = (Option) cogChild;
                                setValue(option.getValue());
                            }
                        }
                    }
                }
            }
        }
    }
}
