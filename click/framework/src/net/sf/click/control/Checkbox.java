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
 * @author Malcolm
 */
public class Checkbox extends Field {

    // ----------------------------------------------------- Instance Variables

    /** The field checked value. */
    protected boolean checked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a checkbox field with the given label.
     *
     * @param label the label of the field.
     */
    public Checkbox(String label) {
        super(label);
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
     * Return the input type: '<tt>checkbox</tt>'
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
     */
    public String getValue() {
        return String.valueOf(checked);
    }

    /**
     * Set checked value of the field. If the given value is null, the checked
     * value is set to false.
     *
     * @see Field#setValue(Object)
     */
    public void setValue(Object value) {
        if (value != null) {
            checked = Boolean.getBoolean(value.toString());
        } else {
            checked = false;
        }
    }

    /**
     * Process the request Context setting the checked value and invoking
     * the controls listener if defined.
     * <p/>
     * If a checked value is {@link Field#required} and the Checkbox is not
     * checked the error message defined by <tt>not-checked-error</tt>
     * property will be displayed.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        setChecked(getContext().getRequest().getParameter(name) != null);

        if (!validate()) {
            return true;
        }

        if (isRequired() && !isChecked()) {
            error = getMessage("not-checked-error", getLabel());
        }

        return invokeListener();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the HTML rendered Checkbox string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(50);

        buffer.append("<input type='");
        buffer.append(getType());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("'");

        renderAttributes(buffer);

        if (checked) {
            buffer.append("' checked");
        }
        if (getTitle() != null) {
            buffer.append(" 'title='");
            buffer.append(getTitle());
            buffer.append("'");
        }
        buffer.append(getDisabled());
        buffer.append(getReadonly());
        if (isValid()) {
            buffer.append(">");
        } else {
            buffer.append(" class='error'>");
        }

        return buffer.toString();
    }
}
