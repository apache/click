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
import org.apache.click.Context;

import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a Checkbox control: &nbsp; &lt;input type='checkbox'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Checkbox</td>
 * <td><input type='checkbox' title='Checkbox Control'/></td>
 * </tr>
 * </table>
 *
 * Checkbox supports the {@link Field#required} property and Control listeners.
 *
 * <p/>
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class Checkbox extends Field {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------- Constants

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message</li>
     * </ul>
     */
    protected final static String VALIDATE_CHECKBOX_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateCheckbox(''{0}'',{1}, [''{2}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    /** The field checked value. */
    protected boolean checked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Checkbox field with the given name.
     *
     * @param name the name of the field
     */
    public Checkbox(String name) {
        super(name);
    }

    /**
     * Create a Checkbox field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public Checkbox(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Checkbox field with the given name and required flag.
     *
     * @param name the name of the field
     * @param required the name required status
     */
    public Checkbox(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Create a Checkbox field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Checkbox() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the checkbox's html tag: <tt>input</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    @Override
    public String getTag() {
        return "input";
    }

    /**
     * Return true if the checkbox is checked, or false otherwise.
     *
     * @return true if the checkbox is checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Set the selected value of the checkbox.
     *
     * @param value the selected value
     */
    public void setChecked(boolean value) {
        checked = value;
    }

    /**
     * Return the input type: '<tt>checkbox</tt>'.
     *
     * @return the input type '<tt>checkbox</tt>'
     */
    public String getType() {
        return "checkbox";
    }

    /**
     * Returns "true" if the checkbox is checked, or false otherwise.
     *
     * @see Field#getValue()
     *
     * @return the Field value
     */
    @Override
    public String getValue() {
        return String.valueOf(checked);
    }

    /**
     * Set checked value of the field. If the given value is null, the checked
     * value is set to false.
     *
     * @see Field#setValue(String)
     *
     * @param value the Field value
     */
    @Override
    public void setValue(String value) {
        checked = Boolean.valueOf(value);
     }

    /**
     * Return the field Boolean value.
     *
     * @see Field#getValueObject()
     *
     * @return the object representation of the Field value
     */
    @Override
    public Object getValueObject() {
        if (checked) {
            return Boolean.TRUE;

        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Set the checked value of the field using the given object.
     *
     * @see Field#setValueObject(Object)
     *
     * @param object the object value to set
     */
    @Override
    public void setValueObject(Object object) {
        if (object != null && object instanceof Boolean) {
            checked = (Boolean) object;
        }
    }

    /**
     * Return the Checkbox JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        if (isRequired()) {
            Object[] args = new Object[3];
            args[0] = getId();
            args[1] = String.valueOf(isRequired());
            args[2] = getMessage("not-checked-error", getErrorLabel());

            return MessageFormat.format(VALIDATE_CHECKBOX_FUNCTION, args);

        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the {@link #checked} property to true if the fields value is
     * submitted.
     */
    @Override
    public void bindRequestValue() {
        setChecked(getContext().getRequestParameter(getName()) != null);
    }

    /**
     * Process the request Context setting the checked value if selected
     * and invoking the control's listener if defined.
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

        // In Html an unchecked Checkbox does not submit it's name/value so we
        // always validate and dispatch registered events
        bindRequestValue();

        if (getValidate()) {
            validate();
        }

        dispatchActionEvent();

        return true;
    }

    /**
     * Render the HTML representation of the Checkbox.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("title", getTitle());
        if (isValid()) {
            removeStyleClass("error");
        } else {
            addStyleClass("error");
        }
        if (getTabIndex() > 0) {
            buffer.appendAttribute("tabindex", getTabIndex());
        }
        if (isChecked()) {
            buffer.appendAttribute("checked", "checked");
        }

        appendAttributes(buffer);

        if (isDisabled() || isReadonly()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();

        if (getHelp() != null) {
            buffer.append(getHelp());
        }

        // checkbox element does not support "readonly" element, so as a work around
        // we make the field "disabled" and render a hidden field to submit its value
        if (isReadonly() && isChecked()) {
            buffer.elementStart("input");
            buffer.appendAttribute("type", "hidden");
            buffer.appendAttribute("name", getName());
            buffer.appendAttributeEscaped("value", getValue());
            buffer.elementEnd();
        }
    }

    /**
     * Validate the Checkbox request submission.
     * <p/>
     * If a checked value is {@link Field#required} and the Checkbox is not
     * checked the error message defined by <tt>not-checked-error</tt>
     * property will be displayed.
     */
    @Override
    public void validate() {
        setError(null);
        if (isRequired() && !isChecked()) {
            setErrorMessage("not-checked-error");
        }
    }
}
