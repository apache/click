/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import net.sf.click.control.TextField;

/**
 * Provides a Long Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Long Field</td>
 * <td><input type='text' value='93019382701' title='LongField Control'/></td>
 * </tr>
 * </table>
 *
 * LongField will validate the number when the control is processed and invoke
 * the control listener if defined.
 * <p/>
 * The LongField uses a JavaScript onKeyPress() integerFilter() method to prevent
 * users from entering invalid characters. To enable number key filtering
 * reference the {@link net.sf.click.util.PageImports} object in your page
 * header section. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class LongField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The maximum field value. */
    protected long maxvalue = Long.MAX_VALUE;

    /** The minimum field value. */
    protected long minvalue = Long.MIN_VALUE;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a LongField field with the given name.
     *
     * @param name the name of the field
     */
    public LongField(String name) {
        super(name);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a LongField field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public LongField(String name, boolean required) {
        super(name, required);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a LongField field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public LongField(String name, String label) {
        super(name, label);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a LongField field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public LongField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Create a LongField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public LongField() {
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the field Long value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Long value
     */
    public Long getLong() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            try {
                return Long.valueOf(value);

            } catch (NumberFormatException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Return the field Integer value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Integer value
     */
    public Integer getInteger() {
        Long value = getLong();
        if (value != null) {
            return new Integer(value.intValue());
        } else {
            return null;
        }
    }

    /**
     * Return the maximum valid long field value.
     *
     * @return the maximum valid long field value
     */
    public long getMaxValue() {
        return maxvalue;
    }

    /**
     * Set the maximum valid long field value.
     *
     * @param value the maximum valid long field value
     */
    public void setMaxValue(int value) {
        maxvalue = value;
    }

    /**
     * Return the minimum valid long field value.
     *
     * @return the minimum valid long field value
     */
    public long getMinValue() {
        return minvalue;
    }

    /**
     * Set the miminum valid long field value.
     *
     * @param value the miminum valid long field value
     */
    public void setMinValue(long value) {
        minvalue = value;
    }

    /**
     * Return the <tt>Long.class</tt>.
     *
     * @see net.sf.click.control.Field#getValueClass()
     *
     * @return the <tt>Long.class</tt>
     */
    public Class getValueClass() {
        return Long.class;
    }

    /**
     * Return the field Long value, or null if value was empty or a parsing
     * error occured.
     *
     * @see net.sf.click.control.Field#getValueObject()
     *
     * @return the Long object representation of the Field value
     */
    public Object getValueObject() {
        return getLong();
    }

    /**
     * Set the long value of the field using the given object.
     *
     * @see net.sf.click.control.Field#setValueObject(Object)
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            value = object.toString();
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Validate the LongField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-required-error</li>
     *       <li>number-maxvalue-error</li>
     *       <li>number-minvalue-error</li>
     *     </ul>
     *   </li>
     *   <li>/net/sf/click/extras/control/LongField.properties
     *     <ul>
     *       <li>long-format-error</li>
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
                long longValue = Long.parseLong(value);

                if (longValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error", maxvalue);

                } else if (longValue < minvalue) {
                    setErrorMessage("number-minvalue-error", minvalue);
                }

            } catch (NumberFormatException nfe) {
                setError(getMessage("long-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
}
