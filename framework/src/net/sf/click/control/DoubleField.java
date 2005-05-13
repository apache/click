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
 * reference the method {@link Form#getHtmlImports()} in the page template
 * (imports click/form.js file). For example.
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$form.htmlImports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="blue">$form</span>
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

    /** The maximum field value. */
    protected double maxvalue = Double.MAX_VALUE;

    /** The minimum field value. */
    protected double minvalue = Double.MIN_VALUE;

    /**
     * Construct the Double field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public DoubleField(String label) {
        super(label);
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
                    Object[] args = new Object[] { getLabel(), new Double(maxvalue) };
                    setError(getMessage("number-maxvalue-error", args));
                    return true;
                }

                if (minvalue != Double.MIN_VALUE && doubleValue < minvalue) {
                    Object[] args = new Object[] { getLabel(), new Double(minvalue) };
                    setError(getMessage("number-minvalue-error", args));
                    return true;
                }

                return invokeListener();

            } catch (NumberFormatException nfe) {
                setError(getMessage("double-format-error", getLabel()));
            }
        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getLabel()));
            }
        }

        return true;
    }
}
