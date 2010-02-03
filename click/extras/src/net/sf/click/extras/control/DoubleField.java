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
package net.sf.click.extras.control;

import net.sf.click.control.Field;
import net.sf.click.control.Form;
import net.sf.click.control.TextField;

/**
 * Provides a Double Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Double Field</td>
 * <td><input type='text' value='3.541' title='DoubleField Control'/></td>
 * </tr>
 * </table>
 *
 * DoubleField will validate the number when the control is processed and invoke
 * the control listener if there is no parsing error.
 * <p/>
 * The DoubleField uses a JavaScript onKeyPress() doubleFilter() method to prevent
 * users from entering invalid characters. To enable number key filtering
 * reference the {@link Form#getHtmlImports()} object in the page template.
 * For example:
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
public class DoubleField extends TextField {

    private static final long serialVersionUID = -6326214893690121356L;

    /** The maximum field value. */
    protected double maxvalue = Double.MAX_VALUE;

    /** The minimum field value. */
    protected double minvalue = Double.MIN_VALUE;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a DoubleField with the given name.
     *
     * @param name the name of the field
     */
    public DoubleField(String name) {
        super(name);
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
    }

    /**
     * Construct a DoubleField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public DoubleField(String name, String label) {
        super(name, label);
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
    }

    /**
     * Construct a DoubleField with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public DoubleField(String name, boolean required) {
        this(name);
        setRequired(required);
    }

    /**
     * Create a DoubleField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public DoubleField() {
        super();
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the field Double value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Double value
     */
    public Double getDouble() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            try {
                return Double.valueOf(value);

            } catch (NumberFormatException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Return the field Float value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Float value
     */
    public Float getFloat() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            try {
                return Float.valueOf(value);

            } catch (NumberFormatException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Return the maximum valid double field value.
     *
     * @return the maximum valid double field value
     */
    public double getMaxValue() {
        return maxvalue;
    }

    /**
     * Set the maximum valid double field value.
     *
     * @param value the maximum valid double field value
     */
    public void setMaxValue(double value) {
        maxvalue = value ;
    }

    /**
     * Set the miminum valid double field value.
     *
     * @param value the miminum valid double field value
     */
    public void setMinValue(double value) {
        minvalue = value;
    }

    /**
     * Return the minimum valid double field value.
     *
     * @return the minimum valid double field value.
     */
    public double getMinValue() {
        return minvalue;
    }

    /**
     * Return the <tt>Double.class</tt>.
     *
     * @see Field#getValueClass()
     *
     * @return the <tt>Double.class</tt>
     */
    public Class getValueClass() {
        return Double.class;
    }

    /**
     * Return the field Double value, or null if value was empty or a parsing
     * error occured.
     *
     * @see Field#getValueObject()
     */
    public Object getValueObject() {
        return getDouble();
    }

    /**
     * Set the double value of the field using the given object.
     *
     * @see Field#setValueObject(Object)
     */
    public void setValueObject(Object object) {
        if (object != null) {
            value = object.toString();
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the DoubleField submission. If the Double value can be parsed
     * the controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>double-format-error</li>
     * <li>field-required-error</li>
     * <li>number-maxvalue-error</li>
     * <li>number-minvalue-error</li>
     * </ul></blockquote>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        value = getRequestValue();

        if (!validate()) {
            return true;
        }

        int length = value.length();
        if (length > 0) {
            try {
                double doubleValue = Double.parseDouble(value);

                if (maxvalue != Double.MAX_VALUE && doubleValue > maxvalue) {
                    Object[] args = new Object[] { getErrorLabel(), new Double(maxvalue) };
                    setError(getMessage("number-maxvalue-error", args));
                    return true;
                }

                if (minvalue != Double.MIN_VALUE && doubleValue < minvalue) {
                    Object[] args = new Object[] { getErrorLabel(), new Double(minvalue) };
                    setError(getMessage("number-minvalue-error", args));
                    return true;
                }

                return invokeListener();

            } catch (NumberFormatException nfe) {
                setError(getMessage("double-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getErrorLabel()));
            }
        }

        return true;
    }
}