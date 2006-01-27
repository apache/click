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

import net.sf.click.util.HtmlStringBuffer;

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
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class Checkbox extends Field {

    private static final long serialVersionUID = -6767031397352259579L;

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
     * Create a Checkbox field with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public Checkbox() {
    }

    // ------------------------------------------------------ Public Attributes

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
    public void setValue(String value) {
        checked = Boolean.valueOf(value).booleanValue();;
     }

    /**
     * Return the <tt>Boolean.class</tt>.
     *
     * @see Field#getValueClass()
     *
     * @return the <tt>Boolean.class</tt>
     */
    public Class getValueClass() {
        return Boolean.class;
    }

    /**
     * Return the field Boolean value.
     *
     * @see Field#getValueObject()
     *
     * @return the object representation of the Field value
     */
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
    public void setValueObject(Object object) {
        if (object != null && object instanceof Boolean) {
            checked = ((Boolean) object).booleanValue();
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * TODO: doco
     */
    public void bindRequestValue() {
        setChecked(getContext().getRequestParameter(getName()) != null);
    }

    /**
     * Return the HTML rendered Checkbox string.
     *
     * @see Object#toString()
     *
     * @return the HTML rendered Checkbox string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
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

        buffer.elementEnd();

        return buffer.toString();
    }

    /**
     * Validate the Checkbox request submission.
     * <p/>
     * If a checked value is {@link Field#required} and the Checkbox is not
     * checked the error message defined by <tt>not-checked-error</tt>
     * property will be displayed.
     *
     * @see net.sf.click.control.Field#validate()
     */
    public void validate() {
        if (isRequired() && !isChecked()) {
            setErrorMessage("not-checked-error");
        }
    }
}
