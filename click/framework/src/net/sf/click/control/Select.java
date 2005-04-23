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
package net.sf.click.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
 * <h4>Single Item Select</h4>
 * A single item Select, will only allow users to select one item from the list.
 * By default the Select {@link #multiple} item property is false.
 * <p/>
 * If a Select is required, an item after the first in the list must be selected
 * for the Field to be valid. This forces the user to make an active selection.
 * <p/>
 * An example of a single item Select is provided below along with the
 * rendered HTML.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> GenderPage <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     Select genderSelect;
 *
 *     <span class="kw">public void</span> onInit() {
 *         form = <span class="kw">new</span> Form(<span class="st">"form"</span>, getContext());
 *         addControl(form);
 *
 *         genderSelect = <span class="kw">new</span> Select(<span class="st">"Gender"</span>);
 *         genderSelect.setRequired(<span class="kw">true</span>);
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"U"</span>, <span class="st">""</span>);
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"M"</span>, <span class="st">"Male"</span>));
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"F"</span>, <span class="st">"Female"</span>));
 *         form.add(genderSelect);
 *     }
 *
 *     <span class="kw">public void</span> onPost() {
 *         <span class="kw">if</span> (form.isValid()) {
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
 * <input type='submit' value='Submit'/>
 * </td></tr>
 * </table>
 * </td></tr>
 * </table>
 *
 * Note how {@link Option} items are added to the Select. In this
 * example the "U" option will not be a valid selection, as it is the first
 * item in the option list.
 *
 * <h4>Multiple Item Select</h4>
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
 * <pre class="codeJava">
 * <span class="kw">public class</span> LocationPage <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     Select locationSelect;
 *
 *     <span class="kw">public void</span> onInit() {
 *         form = <span class="kw">new</span> Form(<span class="st">"form"</span>, getContext());
 *         addControl(form);
 *
 *         locationSelect = <span class="st">new</span> Select(<span class="st">"Location"</span>);
 *         locationSelect.setMutliple(<span class="kw">true</span>);
 *         locationSelect.setRequired(<span class="kw">true</span>);
 *         locationSelect.setSize(7);
 *         locationSelect.add(<span class="st">"QLD"</span>);
 *         locationSelect.add(<span class="st">"NSW"</span>);
 *         locationSelect.add(<span class="st">"NT"</span>);
 *         locationSelect.add(<span class="st">"SA"</span>);
 *         locationSelect.add(<span class="st">"TAS"</span>);
 *         locationSelect.add(<span class="st">"VIC"</span>);
 *         locationSelect.add(<span class="st">"WA"</span>);
 *         form.add(locationSelect);
 *     }
 *
 *     <span class="kw">public void</span> onPost() {
 *         <span class="kw">if</span> (form.isValid()) {
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
 * <td align='left'><select name='location'size='7' multiple ><option value='QLD'>QLD</option><option value='NSW'>NSW</option><option value='NT'>NT</option><option value='SA'>SA</option><option value='TAS'>TAS</option><option value='VIC'>VIC</option><option value='WA'>WA</option></select></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'><input type='submit' value='Submit'/></td></tr>
 * </table>
 * </td></tr>
 * </table>
 *
 * Note is this example the {@link #add(String)} method is used to an an Option
 * item to the Select.
 *
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.6">SELECT</a>
 *
 * @see Select.Option
 * @see Select.OptionGroup
 *
 * @author Malcolm Edgar
 */
public class Select extends Field {

    // ----------------------------------------------------- Instance Variables

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
    protected List multipleValues;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Select field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public Select(String label) {
        super(label);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the given Option to the Select.
     *
     * @param option the Option value to add
     * @throws IllegalArgumentException if option is null
     */
    public void add(Option option) {
        if (option == null) {
            throw new IllegalArgumentException("option parameter cannot be null");
        }
        getOptionList().add(option);
    }

    /**
     * Add the given OptionGroup to the Select.
     *
     * @param optionGroup the OptionGroup value to add
     * @throws IllegalArgumentException if optionGroup is null
     */
    public void add(OptionGroup optionGroup) {
        if (optionGroup == null) {
            throw new IllegalArgumentException("optionGroup parameter cannot be null");
        }
        getOptionList().add(optionGroup);
    }

    /**
     * Add the given option value to the Select. This covenience method will
     * create a new {@link Option} with the given value and add it to the
     * Select. The new Option display label will be the same as its value.
     *
     * @param value the option value to add
     * @throws IllegalArgumentException if the value is null
     */
    public void add(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value parameter cannot be null");
        }
        getOptionList().add(new Option(value));
    }

    /**
     * Add the given Option/OptionGroup collection to the Select.
     *
     * @param options the collection of Option/OptionGroup objects to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Collection options) {
        if (options == null) {
            throw new IllegalArgumentException("options parameter cannot be null");
        }
        if (optionList == null) {
            optionList = new ArrayList(options.size());
        }
        optionList.addAll(options);
    }

    /**
     * Add the given list of Option/OptionGroup objects to the Select.
     *
     * @param options the list of Option/OptionGroup objects to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(List options) {
        if (options == null) {
            throw new IllegalArgumentException("options parameter cannot be null");
        }
        if (optionList == null) {
            optionList = new ArrayList(options.size());
        }
        for (int i = 0, size = options.size(); i < size; i++) {
            optionList.add(options.get(i));
        }
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
            throw new IllegalArgumentException("options parameter cannot be null");
        }
        if (optionList == null) {
            optionList = new ArrayList(options.length);
        }
        for (int i = 0; i < options.length; i++) {
            String value = options[i];
            optionList.add(new Option(value, value));
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
     * @return the list of selected values
     */
    public List getMultipleValues() {
        if (multipleValues != null) {
            return multipleValues;

        } else {
            return Collections.EMPTY_LIST;
        }
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
     * Set the Option list.
     *
     * @param options the Option list
     */
    public void setOptionList(List options) {
        optionList = options;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the Select submission.
     * <p/>
     * If a Select is {@link #required} then the user must select a value
     * other than the first value is the list, otherwise the Select will
     * have a validation error. If the Select is not required then no
     * validation errors will occur.
     * <p/>
     * If the Select is valid, an item(s) is selected, and a Control listener is
     * defined then the listener method will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>select-error</li>
     * </ul></blockquote>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        // Page developer has not initialized options
        if (optionList == null || optionList.isEmpty()) {
            return true;
        }

        // Process single item select case, do the easy one first.
        if (!isMultiple()) {
            // Load the selected item.
            value = getContext().getRequest().getParameter(getName());

            if (value != null) {
                Option firstOption = (Option) optionList.get(0);

                if (isRequired() && firstOption.getValue().equals(value)) {
                    setError(getMessage("select-error", getLabel()));
                    return true;

                } else {
                    return invokeListener();
                }

            } else {
                return true;
            }

        // Process the multiple item select case.
        } else {

            // Load the selected items.
            multipleValues = new ArrayList();
            String[] values = getContext().getRequest().getParameterValues(getName());
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    multipleValues.add(values[i]);
                }
            }

            if (isRequired()) {
                if (multipleValues.isEmpty()) {
                    setError(getMessage("select-error", getLabel()));
                    return true;

                } else {
                    return invokeListener();
                }

            } else {
                if (multipleValues.isEmpty()) {
                    return true;

                } else {
                    return invokeListener();
                }
            }
        }
    }

    /**
     * Return a HTML rendered Select string.
     *
     * @see Object#toString()
     */
    public String toString() {

        int bufferSize = 50;
        if (optionList != null) {
            bufferSize = bufferSize + (optionList.size() * 48);
        }
        StringBuffer buffer = new StringBuffer(bufferSize);

        buffer.append("<select name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("'");
        buffer.append(getDisabled());
        buffer.append(" size='");
        buffer.append(getSize());
        buffer.append("'");
        if (getTitle() != null) {
            buffer.append(" title='");
            buffer.append(getTitle());
            buffer.append("' ");
        }

        renderAttributes(buffer);

        if (isMultiple()) {
            buffer.append(" multiple");
        }
        if (isValid()) {
            buffer.append(">");
        } else {
            buffer.append(" class='error'>");
        }

        if (optionList != null) {
            for (int i = 0, size = optionList.size(); i < size; i++) {
                Object object = optionList.get(i);

                if (object instanceof Option) {
                    Option option = (Option) object;
                    buffer.append(option.renderHTML(this));

                } else if (object instanceof OptionGroup) {
                    OptionGroup optionGroup = (OptionGroup) object;
                    buffer.append(optionGroup.renderHTML(this));

                } else {
                    String msg = "Select option class not instance of Option"
                        + " or OptionGroup: " + object.getClass().getName();
                    throw new IllegalArgumentException(msg);
                }
            }
        }
        buffer.append("</select>");

        return buffer.toString();
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * Provides a select Option element: &nbsp; &lt;option&gt;&lt;/option&gt;.
     * <p/>
     * The Option class uses an immutable design so Option instances can be
     * shared by multiple Pages in the multi-threaded Servlet environment.
     * This enables Option instances to be cached as static variables.
     * <p/>
     * The example below caches Select Option and OptionGroup instances in a
     * static List.
     *
     * <pre class="codeJava">
     * <span class="kw">public class</span> InvestmentSelect <span class="kw">extends</span> Select {
     *
     *     <span class="kw">static final</span> List INVESTMENT_OPTIONS = <span class="kw">new</span> ArrayList();
     *
     *     <span class="kw">static</span> {
     *         Select.OptionGroup property = <span class="kw">new</span> Select.OptionGroup(<span class="st">"Property"</span>);
     *         property.add(<span class="kw">new</span> Select.Option(<span class="st">"Commerical Property"</span>, <span class="st">"Commercial"</span>));
     *         property.add(<span class="kw">new</span> Select.Option(<span class="st">"Residential Property"</span>, <span class="st">"Residential"</span>));
     *         INVESTMENT_OPTIONS.add(property);
     *
     *         Select.OptionGroup securities = <span class="kw">new</span> Select.OptionGroup(<span class="st">"Securities"</span>);
     *         securities.add(<span class="kw">new</span> Select.Option(<span class="st">"Bonds"</span>));
     *         securities.add(<span class="kw">new</span> Select.Option(<span class="st">"Options"</span>));
     *         securities.add(<span class="kw">new</span> Select.Option(<span class="st">"Stocks"</span>));
     *         INVESTMENT_OPTIONS.add(securities);
     *     }
     *
     *     <span class="kw">public</span> InvestmentSelect(String label) {
     *         <span class="kw">super</span>(label);
     *         setOptionList(INVESTMENT_OPTIONS);
     *     }
     * }
     *
     * <span class="kw">public class</span> InvestmentsPage <span class="kw">extends</span> Page {
     *
     *     Form form;
     *     Select investmentsSelect;
     *
     *     <span class="kw">public void</span> onInit() {
     *         form = new Form(<span class="st">"form"</span>, getContext());
     *         addControl(form);
     *
     *         investmentsSelect = <span class="kw">new</span> InvestmentsSelect(<span class="st">"Investments"</span>);
     *         investmentsSelect.setMutliple(<span class="kw">true</span>);
     *         investmentsSelect(7);
     *         form.add(investmentsSelect);
     *     }
     *
     *     ..
     * } </pre>
     *
     * Rendered HTML:
     * <table class="htmlExample"><tr><td>
     * <table class='form'><tr>
     * <td align='left'><label >Investments</label></td>
     * <td align='left'><select name='investments' size='7' multiple><optgroup label='Property'><option value='Commerical Property'>Commercial</option><option value='Residential Property'>Residential</option></optgroup><optgroup label='Securities'><option value='Bonds'>Bonds</option><option selected value='Options'>Options</option><option value='Stocks'>Stocks</option></optgroup></select></td>
     * </tr>
     * <tr><td colspan='2'>&nbsp;</td></tr>
     * <tr align='left'><td colspan='2'>
     * <input type='submit' value='Submit'/>
     * </td></tr>
     * </table>
     * </td></tr></table>
     *
     * See also the W3C HTML reference:
     * <a title="W3C HTML 4.01 Specification"
     *    href="../../../../../html/interact/forms.html#h-17.6">OPTION</a>
     *
     * @see Select
     * @see Select.OptionGroup
     *
     * @author Malcolm Edgar
     */
    public static class Option {

        // ------------------------------------------------- Instance Variables

        /** The Options display label */
        protected final String label;

        /** The Option value. */
        protected final String value;

        // ------------------------------------------------------- Constructors

        /**
         * Create an Option with the given value and display label.
         *
         * @param value the Option value
         * @param label the Option display label
         */
        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }

        /**
         * Create an Option with the given value. The value will also be used
         * for the display label.
         *
         * @param value the Option value and display label
         */
        public Option(String value) {
            this(value, value);
        }

        // -------------------------------------------------- Public Attributes

        /**
         * Return the Option display label.
         *
         * @return the Option display label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Return the Option value.
         *
         * @return the Option value
         */
        public String getValue() {
            return value;
        }

        // ----------------------------------------------------- Public Methods

        /**
         * Return a HTML rendered Option string.
         *
         * @param select the parent Select
         * @return rendered HTML Option string
         */
        public String renderHTML(Select select) {
            StringBuffer buffer = new StringBuffer(48);

            if (select.isMultiple()) {

                if (!select.getMultipleValues().isEmpty()) {

                    // Search through selection list for matching value
                    List values = select.getMultipleValues();
                    boolean found = false;
                    for (int i = 0, size = values.size(); i < size; i++) {
                        String value = values.get(i).toString();
                        if (getValue().equals(value)) {
                            buffer.append("<option selected value='");
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        buffer.append("<option value='");
                    }

                } else {
                    buffer.append("<option value='");
                }

            } else {
                if (getValue().equals(select.getValue())) {
                    buffer.append("<option selected value='");
                } else {
                    buffer.append("<option value='");
                }
            }

            buffer.append(getValue());
            buffer.append("'>");
            buffer.append(getLabel());
            buffer.append("</option>");

            return buffer.toString();
        }
    }

    /**
     * Provides a select Option Group element: &nbsp; &lt;optgroup&gt;&lt;/optgroup&gt;.
     * <p/>
     * The OptionGroup class uses an immutable design so Option instances can be
     * shared by multiple Pages in the multi-threaded Servlet environment.
     * This enables OptionGroup instances to be cached as static variables.
     * <p/>
     * For an OptionGroup code example see the {@link Select.Option} Javadoc example.
     * <p/>
     * See also the W3C HTML reference:
     * <a title="W3C HTML 4.01 Specification"
     *    href="../../../../../html/interact/forms.html#h-17.6">OPTGROUP</a>
     *
     * @see Select
     * @see Select.Option
     *
     * @author Malcolm Edgar
     */
    public static class OptionGroup {

        // ------------------------------------------------- Instance Variables

        /** The groups child Option/OptGroup objects. */
        protected List children = new ArrayList();

        /** The label for the OptionGroup. */
        protected final String label;

        // ------------------------------------------------------- Constructors

        /**
         * Create an OptionGroup with the given display label.
         *
         * @param label the display label for the OptionGroup
         */
        public OptionGroup(String label) {
            this.label = label;
        }

        // -------------------------------------------------- Public Attributes

        /**
         * Add the given Option or OptionGroup object to this group.
         *
         * @param object the Option or OptionGroup to add
         */
        public void add(Object object) {
            getChildren().add(object);
        }

        /**
         * Return the OptionGroup children.
         *
         * @return the OptionGroup children
         */
        public List getChildren() {
            return children;
        }

        /**
         * Return the display label.
         *
         * @return the display label
         */
        public String getLabel() {
            return label;
        }

        // ----------------------------------------------------- Public Methods

        /**
         * Return a HTML rendered OptionGroup string.
         *
         * @param select the parent Select
         * @return a rendered HTML OptionGroup string
         */
        public String renderHTML(Select select) {
            StringBuffer buffer = new StringBuffer(64);

            buffer.append("<optgroup label='");
            buffer.append(getLabel());
            buffer.append("'>");

            List list = getChildren();
            for (int i = 0, size = list.size(); i < size; i++) {
                Object object = list.get(i);

                if (object instanceof Option) {
                    Option option = (Option) object;
                    buffer.append(option.renderHTML(select));

                } else if (object instanceof OptionGroup) {
                    OptionGroup optionGroup = (OptionGroup) object;
                    buffer.append(optionGroup.renderHTML(select));

                } else {
                    String msg = "Select option class not instance of Option"
                        + " or OptionGroup: " + object.getClass().getName();
                    throw new IllegalArgumentException(msg);
                }
            }

            buffer.append("</optgroup>");

            return buffer.toString();
        }
    }
}
