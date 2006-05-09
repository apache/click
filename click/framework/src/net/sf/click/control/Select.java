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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.click.util.HtmlStringBuffer;
import ognl.Ognl;

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
 *     <span class="kw">private</span> Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *     <span class="kw">private</span> Select genderSelect = <span class="kw">new</span> Select(<span class="st">"Gender"</span>);
 *
 *     <span class="kw">public</span> GenderPage() {
 *         genderSelect.setRequired(<span class="kw">true</span>);
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"U"</span>, <span class="st">""</span>);
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"M"</span>, <span class="st">"Male"</span>));
 *         genderSelect.add(<span class="kw">new</span> Option(<span class="st">"F"</span>, <span class="st">"Female"</span>));
 *         form.add(genderSelect);
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>));
 *
 *         addControl(form);
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
 *     <span class="kw">private</span> Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *     <span class="kw">private</span> Select locationSelect = <span class="kw">new</span> Select(<span class="st">"location"</span>);
 *
 *     <span class="kw">public</span> LocationPage() {
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
 *
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>));
 *
 *         addControl(form);
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
 * <tr align='left'><td colspan='2'><input type='submit' value='  OK  '/></td></tr>
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
 * @see Option
 * @see OptionGroup
 *
 * @author Malcolm Edgar
 */
public class Select extends Field {

    private static final long serialVersionUID = 1L;

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the name of the static JavaScript function to call</li>
     * <li>2 - is the localized error message</li>
     * <li>3 - is the default Select option value</li>
     * </ul>
     */
    protected final static String VALIDATE_SELECT_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   if (!{1}(''{0}'', ''{3}'')) '{'\n"
        + "      return ''{2}|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

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
     * Create a Select field with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public Select() {
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
            String msg = "option parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().add(option);
        if (getOptionList().size() == 1) {
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
     * Add the given option value to the Select. This covenience method will
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
        getOptionList().add(new Option(value));
        if (getOptionList().size() == 1) {
            setInitialValue();
        }
    }

    /**
     * Add the given Option/OptionGroup collection to the Select.
     *
     * @param options the collection of Option/OptionGroup objects to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Collection options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        getOptionList().addAll(options);
        setInitialValue();
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
        for (int i = 0; i < options.length; i++) {
            String value = options[i];
            getOptionList().add(new Option(value, value));
        }
        setInitialValue();
    }

    /**
     * Add the given collection of objects to the Select, creating new Option
     * instances based on the object properties specified by value and label.
     *
     * <pre class="codeJava">
     * Select select = <span class="kw">new</span> Select(<span class="st">"type"</span>, <span class="st">"Type:"</span>);
     * select.addAll(getCustomerService().getCustomerTypes(), <span class="st">"id"</span>, <span class="st">"name"</span>);
     * form.add(select); </pre>
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
 
        Map ognlContext = new HashMap();

        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object object = i.next();

            try {
                Object valueResult = Ognl.getValue(value, ognlContext, object);
                Object labelResult = Ognl.getValue(label, ognlContext, object);

                Option option = new Option(valueResult, labelResult.toString());
                getOptionList().add(option);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        setInitialValue();
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
     * Set the list of selected values.
     *
     * @param multipleValues the list of selected values
     */
    public void setMultipleValues(List multipleValues) {
        this.multipleValues = multipleValues;
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

    /**
     * Return the Select JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        if (isRequired()) {
            Object[] args = new Object[4];
            args[0] = getId();
            args[1] = "validateSelect";
            args[2] = getMessage("field-required-error", getErrorLabel());

            if (!getOptionList().isEmpty()) {
                Option option = (Option) getOptionList().get(0);
                args[3] = option.getValue();
            } else {
                args[3] = "";
            }

            return MessageFormat.format(VALIDATE_SELECT_FUNCTION, args);

        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the {@link #value} or
     * {@link #multipleValues} property if defined in the request.
     */
    public void bindRequestValue() {

        // Page developer has not initialized options
        if (getOptionList().isEmpty()) {
            return;
        }

        // Process single item select case, do the easy one first.
        if (!isMultiple()) {
            // Load the selected item.
            this.value = getContext().getRequestParameter(getName());

        // Process the multiple item select case.
        } else {

            // Load the selected items.
            this.multipleValues = new ArrayList();

            // TODO: resolve multiple values when multipart/form-data

            String[] values =
                getContext().getRequest().getParameterValues(getName());

            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    multipleValues.add(values[i]);
                }
            }
        }
    }

    /**
     * Return a HTML rendered Select string.
     *
     * @return a HTML rendered Select string
     */
    public String toString() {

        int bufferSize = 50;
        if (!getOptionList().isEmpty()) {
            bufferSize = bufferSize + (optionList.size() * 48);
        }
        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        buffer.elementStart("select");

        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("size", getSize());
        buffer.appendAttribute("title", getTitle());
        if (isMultiple()) {
            buffer.appendAttribute("multiple", "multiple");
        }
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        if (isReadonly()) {
            buffer.appendAttributeReadonly();
        }
        if (!isValid()) {
            buffer.appendAttribute("class", "error");
        }
        buffer.closeTag();

        if (!getOptionList().isEmpty()) {
            for (int i = 0, size = getOptionList().size(); i < size; i++) {
                Object object = getOptionList().get(i);

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

        buffer.elementEnd("select");

        return buffer.toString();
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
    public void validate() {
        setError(null);

        if (isRequired()) {
            if (isMultiple()) {
                if (getMultipleValues().isEmpty()) {
                    setErrorMessage("select-error");
                }

            } else {
                Option firstOption = (Option) getOptionList().get(0);

                if (firstOption.getValue().equals(getValue())) {
                    setErrorMessage("select-error");
                }
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

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
            }
        }
    }
}
