/*
 * Copyright 2005 Malcolm A. Edgar
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ognl.Ognl;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a RadioGroup control.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Radio Group</td>
 * <td>
 * <input type='radio' name='header' value='R'>Red </input>
 * <input type='radio' name='header' checked value='G'>Green </input>
 * <input type='radio' name='header' value='B'>Blue</input>
 * </td>
 * </tr>
 * </table>
 *
 * The RadioGroup control provides a Field for containing grouped Radio buttons.
 * Radio controls added to a RadioGroup will have their name set to that of
 * the RadioGroup. This will ensure the buttons will toggle together so that
 * only one button is selected at a time.
 * <p/>
 * The example below illustrates a RadioGroup being added to a form.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> Purchase <span class="kw">extends</span> Page {
 *
 *     <span class="kw">privte</span> Form form  = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *     <span class="kw">private</span> RadioGroup radioGroup = <span class="kw">new</span> RadioGroup(<span class="st">"packaging"</span>);
 *
 *     <span class="kw">public</span> Purchase() {
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"STD"</span>, <span class="st">"Standard "</span>));
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"PRO"</span>, <span class="st">"Protective "</span>));
 *         radioGroup.add(<span class="kw">new</span> Radio(<span class="st">"GFT"</span>, <span class="st">"Gift Wrap "</span>));
 *         radioGroup.setValue(<span class="st">"STD"</span>);
 *         radioGroup.setVerticalLayout(<span class="kw">true</span>);
 *         form.add(radioGroup);
 *
 *         ..
 *
 *         addControl(form);
 *     }
 * } </pre>
 *
 * This radio group field would be render as:
 *
 * <table class='htmlExample'>
 *  <tr>
 *   <td>Packaging</td>
 *   <td>
 *    <input type='radio' name='group' checked value='STD'>Standard</input><br>
 *    <input type='radio' name='group' value='PRO'>Protective</input><br>
 *    <input type='radio' name='group' value='GFT'>Gift Wrap</input>
 *   </td>
 *  </tr>
 * </table>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Radio
 *
 * @author Malcolm Edgar
 */
public class RadioGroup extends Field {

    private static final long serialVersionUID = 1L;

    /**
     * The field validation JavaScript function template. 
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the name of the static JavaScript function to call</li>
     * <li>2 - is the full path name to the radio button</li>
     * <li>3 - is the localized error message</li>
     * <li>4 - is the first radio id to select</li>
     * </ul>
     */
    protected final static String VALIDATE_RADIOGROUP_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   if (!{1}({2})) '{'\n"
        + "      return ''{3}|{4}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // ----------------------------------------------------- Instance Variables

    /** The list of Radio controls. */
    protected List radioList;

    /**
     * The layout is vertical flag (default false). If the layout is vertical
     * each Radio controls is rendered on a new line using the &lt;br&gt;
     * tag.
     */
    protected boolean isVerticalLayout = true;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a RadioGroup with the given name.
     *
     * @param name the name of the field
     */
    public RadioGroup(String name) {
        super(name);
    }

    /**
     * Create a RadioGroup with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public RadioGroup(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Create a RadioGroup with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public RadioGroup(String name, String label) {
        super(name, label);
    }

    /**
     * Create a RadioGroup with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public RadioGroup(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Create a RadioGroup field with no label/name or context defined,
     * <b>please note</b> the control's name and context must be defined before
     * it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public RadioGroup() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the given radio to the radio group. When the radio is added to the
     * group its name is set to that of the radio group and its context is
     * set.
     *
     * @param radio the radio control to add to the radio group
     * @throws IllegalArgumentException if the radio parameter is null
     */
    public void add(Radio radio) {
        if (radio == null) {
            throw new IllegalArgumentException("Null radio parameter");
        }

        radio.setName(getName());
        getRadioList().add(radio);
        if (getContext() != null) {
            radio.setContext(getContext());
        }
    }

    /**
     * Add the given collection Radio item options to the RadioGroup.
     *
     * @param options the collection of Radio items to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Collection options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (Iterator i = options.iterator(); i.hasNext();) {
            Radio radio = (Radio) i.next();
            add(radio);
        }
    }

    /**
     * Add the given Map of radio values and labels to the RadioGroup.
     * The Map entry key will be used as the radio value and the Map entry
     * value will be used as the radio label.
     * <p/>
     * It is recommended that <tt>LinkedHashMap</tt> is used as the Map
     * parameter to maintain the order of the radio items.
     *
     * @param options the Map of radio option values and labels to add
     * @throws IllegalArgumentException if options is null
     */
    public void addAll(Map options) {
        if (options == null) {
            String msg = "options parameter cannot be null";
            throw new IllegalArgumentException(msg);
        }
        for (Iterator i = options.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            Radio radio = new Radio(entry.getKey().toString(),
                                    entry.getValue().toString());
            add(radio);
        }
    }

    /**
     * Add the given collection of objects to the RadioGroup, creating new
     * Radio instances based on the object properties specified by value and
     * label.
     *
     * <pre class="codeJava">
     * RadioGroup radioGroup = <span class="kw">new</span> RadioGroup(<span class="st">"type"</span>, <span class="st">"Type:"</span>);
     * radioGroup.addAll(getCustomerService().getCustomerTypes(), <span class="st">"id"</span>, <span class="st">"name"</span>);
     * form.add(select); </pre>
     *
     * @param objects the collection of objects to render as radio options
     * @param value the name of the object property to render as the Radio value
     * @param label the name of the object property to render as the Radio label
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

                Radio radio = new Radio(valueResult.toString(),
                                        labelResult.toString());
                add(radio);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see Control#setContext(Context)
     *
     * @param context the Page request Context
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (hasRadios()) {
            for (Iterator i = getRadioList().iterator(); i.hasNext();) {
                Object object = i.next();
                if (object instanceof Control) {
                    ((Control) object).setContext(context);
                }
            }
        }
    }

    /**
     * Return the RadioGroup focus JavaScript.
     *
     * @return the RadioGroup focus JavaScript
     */
    public String getFocusJavaScript() {
        String id = "";

        if (!getRadioList().isEmpty()) {
            Radio radio = (Radio) getRadioList().get(0);
            id = radio.getId();
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(32);
        buffer.append("setFocus('");
        buffer.append(id);
        buffer.append("');");

        return buffer.toString();
    }

    /**
     * @see Field#setForm(Form)
     *
     * @param form Field's parent <tt>Form</tt>
     */
    public void setForm(Form form) {
        super.setForm(form);
        if (hasRadios()) {
            for (int i = 0, size = getRadioList().size(); i < size; i++) {
                Radio radio = (Radio) getRadioList().get(i);
                radio.setForm(getForm());
            }
        }
    }

    /**
     * Return true if the radio control layout is vertical.
     *
     * @return true if the radio control layout is vertical
     */
    public boolean isVerticalLayout() {
        return isVerticalLayout;
    }

    /**
     * Set the vertical radio control layout flag.
     *
     * @param vertical the vertical layout flag
     */
    public void setVerticalLayout(boolean vertical) {
        isVerticalLayout = vertical;
    }

    /**
     * Return the list of radio controls.
     *
     * @return the list of radio controls
     */
    public List getRadioList() {
        if (radioList == null) {
            radioList = new ArrayList();
        }
        return radioList;
    }

    /**
     * Return true if RadioGroup has Radio controls, or false otherwise.
     *
     * @return true if RadioGroup has Radio controls, or false otherwise
     */
    public boolean hasRadios() {
        return radioList != null && !radioList.isEmpty();
    }

    /**
     * Return the RadioGroup JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        if (isRequired()) {
            Object[] args = new Object[5];
            args[0] = getId();
            args[1] = "validateRadioGroup";
            args[2] = "document." + getForm().getName() + "." + getName();
            args[3] = getMessage("field-required-error", getErrorLabel());

            if (!getRadioList().isEmpty()) {
                Radio radio = (Radio) getRadioList().get(0);
                args[4] = radio.getId();
            } else {
                args[4] = "";
            }

            return MessageFormat.format(VALIDATE_RADIOGROUP_FUNCTION, args);

        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the request Context setting the checked value and invoking
     * the controls listener if defined.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        bindRequestValue();

        boolean continueProcessing = true;
        for (int i = 0, size = getRadioList().size(); i < size; i++) {
            Radio radio = (Radio) getRadioList().get(i);
            continueProcessing = radio.onProcess();
            if (!continueProcessing) {
                return false;
            }
        }

        if (getValidate()) {
            validate();
        }

        return invokeListener();
    }

    /**
     * Return the HTML rendered RadioGroup string.
     *
     * @see Object#toString()
     *
     * @return the HTML rendered RadioGroup string
     */
    public String toString() {
        final int size = getRadioList().size();

        HtmlStringBuffer buffer = new HtmlStringBuffer(size * 30);

        String value = getValue();

        for (int i = 0; i < size; i++) {
            Radio radio = (Radio) getRadioList().get(i);

            if (value != null && value.length() > 0) {
                if (radio.getValue().equals(value)) {
                    radio.setChecked(true);
                } else {
                    radio.setChecked(false);
                }
            }

            buffer.append(radio.toString());

            if (isVerticalLayout() && (i < size - 1)) {
                buffer.append("<br/>");
            }
        }

        return buffer.toString();
    }

    /**
     * Validate the RadioGroup request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>field-required-error</li>
     * </ul></blockquote>
     */
    public void validate() {
        setError(null);

        if (isRequired() && getValue().length() == 0) {
            setErrorMessage("select-error");
        }
    }
}
