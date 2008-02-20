/*
 * Copyright 2004-2007 Malcolm A. Edgar
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
package net.sf.click.extras.control;

/**
 * Provides a Integer Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Integer Field</td>
 * <td><input type='text' value='101' title='IntegerField Control'/></td>
 * </tr>
 * </table>
 *
 * IntegerField will validate the number when the control is processed and invoke
 * the control listener if defined.
 * <p/>
 * The IntegerField uses a JavaScript onkeypress() integerFilter() method to prevent
 * users from entering invalid characters. To enable number key filtering
 * reference <tt class="blue">$jsImports</tt> and <tt class="blue">$cssImports</tt> in your page
 * template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$cssImports</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$form</span>
 * &lt;/body&gt;
 * &lt;/html&gt; 
 * <span class="blue">$jsImports</span> </pre>
 *
 * The InteferField has left justified horizontal text alignment,
 * {@link #setTextAlign(String)}.
 * <p/>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class IntegerField extends NumberField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a IntegerField field with the given name.
     *
     * @param name the name of the field
     */
    public IntegerField(String name) {
        super(name);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        setTextAlign("left");
    }

    /**
     * Construct a IntegerField field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public IntegerField(String name, boolean required) {
        super(name, required);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        setTextAlign("left");
    }

    /**
     * Construct a IntegerField field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public IntegerField(String name, String label) {
        super(name, label);
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        setTextAlign("left");
    }

    /**
     * Construct a IntegerField field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public IntegerField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Create a IntegerField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public IntegerField() {
        setAttribute("onkeypress", "javascript:return integerFilter(event);");
        setTextAlign("left");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the field Integer value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Integer value
     */
    public Integer getInteger() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            try {
                return Integer.valueOf(value);

            } catch (NumberFormatException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Set the Integer value of the field.
     *
     * @param integerValue the field integer value to set
     */
    public void setInteger(Integer integerValue) {
        if (integerValue != null) {
            setValue(integerValue.toString());
        } else {
            setValue(null);
        }
    }

    /**
     * Return the field Long value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Long value
     */
    public Long getLong() {
        Integer value = getInteger();
        if (value != null) {
            return new Long(value.longValue());
        } else {
            return null;
        }
    }

    /**
     * Return the field Integer value, or null if value was empty or a parsing
     * error occured.
     *
     * @see net.sf.click.control.Field#getValueObject()
     *
     * @return the Integer object representation of the Field value
     */
    public Object getValueObject() {
        return getInteger();
    }

    /**
     * Set the integer value of the field using the given object.
     *
     * @see net.sf.click.control.Field#setValueObject(Object)
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            setValue(object.toString());

        } else {
            setValue(null);
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Validate the IntegerField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-required-error</li>
     *       <li>number-format-error</li>
     *       <li>number-maxvalue-error</li>
     *       <li>number-minvalue-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        setError(null);

        String value = getValue();

        int length = value.length();
        if (length > 0) {
            try {
                int intValue = Integer.parseInt(value);

                if (intValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error", maxvalue);

                } else if (intValue < minvalue) {
                    setErrorMessage("number-minvalue-error", minvalue);
                }

            } catch (NumberFormatException nfe) {
                setError(getMessage("number-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
}
