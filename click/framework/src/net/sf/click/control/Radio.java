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

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Radio control: &nbsp; &lt;input type='radio'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td><input type='radio' name='header' value='Radio Control'>Radio</input></td>
 * </tr>
 * </table>
 *
 * For an Radio code example see the {@link net.sf.click.control.RadioGroup}
 * Javadoc example.
 *
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see RadioGroup
 *
 * @author Malcolm Edgar
 */
public class Radio extends Field {

    private static final long serialVersionUID = 6352053148711861658L;

    // ----------------------------------------------------- Instance Variables

    /** The field checked value. */
    protected boolean checked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a radio field.
     */
    public Radio() {
    }

    /**
     * Create a radio field with the given value.
     *
     * @param value the label of the field
     */
    public Radio(String value) {
        setValue(value);
    }

    /**
     * Create a radio field with the given value and label.
     *
     * @param value the label of the field
     * @param label the name of the field
     */
    public Radio(String value, String label) {
        setValue(value);
        setLabel(label);
    }

    /**
     * Create a radio field with the given value, label and name.
     *
     * @param value the label of the field
     * @param label the label of the field
     * @param name the name of the field
     */
    public Radio(String value, String label, String name) {
        setValue(value);
        setLabel(label);
        setName(name);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return true if the radio is checked, or false otherwise.
     *
     * @return true if the radio is checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Set the selected value of the radio.
     *
     * @param value the selected value
     */
    public void setChecked(boolean value) {
        checked = value;
    }

    /**
     * Return the input type: 'radio'.
     *
     * @return the input type: 'radio'
     */
    public String getType() {
        return "radio";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Bind the request submission, setting the Field {@link #checked} property
     * if defined in the request.
     */
    public void bindRequestValue() {
        String value = getRequestValue();

        setChecked(getValue().equals(value));
    }

    /**
     * Process the request Context setting the checked value if selected
     * and invoking the controls listener if defined.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        bindRequestValue();

        if (isChecked()) {
            return invokeListener();

        } else {
            return true;
        }
    }

    /**
     * Return the HTML rendered Radio string.
     *
     * @return the HTML rendered Radio string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("value", getValue());
        buffer.appendAttribute("title", getTitle());
        if (isChecked()) {
            buffer.appendAttribute("checked", "checked");
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

        if (getLabel() != null) {
            buffer.appendEscaped(getLabel());
        } else {
            buffer.appendEscaped(getValue());
        }

        buffer.elementEnd("input");

        return buffer.toString();
    }
}
