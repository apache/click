/*
 * Copyright 2005-2006 Malcolm A. Edgar
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
 * The Radio control is generally used within a RadioGroup, for an code example
 * pleasse see the {@link net.sf.click.control.RadioGroup} Javadoc example.
 * When used with a RadioGroup the Radio control will derrive its name from the
 * parent RadioGroup, if the Radio's name is not defined.
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

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The field checked value. */
    protected boolean checked;

    // ----------------------------------------------------------- Constructors

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

    /**
     * Create an Radio field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Radio() {
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
     * Return the Radio field id attribute.
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");

        } else {
            String formId = (getForm() != null) ? getForm().getId() + "_" : "";

            String id = formId + getName() + "_" + getValue();

            if (id.indexOf('/') != -1) {
                id = id.replace('/', '_');
            }
            if (id.indexOf(' ') != -1) {
                id = id.replace(' ', '_');
            }

            return id;
        }
    }

    /**
     * Return the name of the Radio field. If the Radio name attribute has not
     * been explicitly set, this method will return its parent RadioGroup's
     * name if defined.
     *
     * @return the name of the control
     */
    public String getName() {
        if (super.getName() != null) {
            return super.getName();

        } else {
            Object parent = getParent();
            if (parent instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup) parent;
                return radioGroup.getName();

            } else {
                return null;
            }
        }
    }

    /**
     * Return the input type: 'radio'.
     *
     * @return the input type: 'radio'
     */
    public String getType() {
        return "radio";
    }

    /**
     * Set the radio value, setting the checked status if given value is the
     * same as the radio field value.
     *
     * @see Field#setValue(String)
     *
     * @param value the Field value
     */
    public void setValue(String value) {
        setChecked(getValue().equals(value));
        super.setValue(value);
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
        if (getContext() == null) {
            throw new IllegalStateException("context is not defined");
        }

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
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("title", getTitle());
        if (isChecked()) {
            buffer.appendAttribute("checked", "checked");
        }
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled() || isReadonly()) {
            buffer.appendAttributeDisabled();
        }
        if (isReadonly()) {
            buffer.appendAttributeReadonly();
        }
        if (!isValid()) {
            buffer.appendAttribute("class", "error");
        }
        if (hasStyles()) {
            buffer.appendStyleAttributes(getStyles());
        }

        buffer.closeTag();

        if (label != null) {
            buffer.appendEscaped(label);
        } else {
            buffer.appendEscaped(getValue());
        }

        buffer.elementEnd("input");

        // radio element does not support "readonly" element, so as a work around
        // we make the field "disabled" and render a hidden field to submit its value
        if (isReadonly() && isChecked()) {
            buffer.elementStart("input");
            buffer.appendAttribute("type", "hidden");
            buffer.appendAttribute("name", getName());
            buffer.appendAttribute("value", getValue());
            buffer.elementEnd();
        }

        return buffer.toString();
    }
}
