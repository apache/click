/*
 * Copyright 2004 Malcolm A. Edgar
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

import net.sf.click.Context;

/**
 * Provides a Double Field control: &nbsp; &lt;input type='text'&gt;.
 * <p/>
 * <table class='form'><tr>
 * <td>Double Field</td>
 * <td><input type='text' value='3.541' title='DoubleField Control'/></td>
 * </tr></table>
 * <p/>
 * DoubleField will validate the number when the control is processed and invoke
 * the control listener if there is no parsing error. 
 * <p/>
 * The DoubleField uses a JavaScript onKeyPress() numberFilter method to prevent 
 * users from entering invalid characters. To enable number key filtering
 * reference the method {@link Form#getHtmlImports()} in the page template 
 * (imports form.js file). For example.<blockquote><pre>
 * &lt;html&gt;
 *  &lt;head&gt; 
 *   <font color="blue">$form.getHtmlImports()</font>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   ..
 *  &lt;/body&gt;
 * &lt;/html&gt;</pre>
 * </blockquote>
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification" 
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 * 
 * @author Malcolm Edgar
 */
public class DoubleField extends TextField {

    /** The maximum field value. */
    private double maxvalue = Double.MAX_VALUE;

    /** The minimum field value. */
    private double minvalue = Double.MIN_VALUE;

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

    // --------------------------------------------------------- Public Methods
    
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

    /**
     * Process the DoubleField submission. If the Double value can be parsed
     * the controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs. 
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
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
        Context context = getContext();
        
        value = context.getRequest().getParameter(name);
        if (value != null) {
            value = value.trim();
        } else {
            value = "";
        }

        int length = value.length();
        if (length > 0) {
            try {
                double doubleValue = Double.parseDouble(value);

                if (maxvalue != Double.MAX_VALUE && doubleValue > maxvalue) {
                    Object[] args = new Object[] { getLabel(), new Double(maxvalue) };
                    error = getMessage(context, "number-maxvalue-error", args);
                    return true;
                }
               
                if (minvalue != Double.MIN_VALUE && doubleValue < minvalue) {
                    Object[] args = new Object[] { getLabel(), new Double(minvalue) };
                    error = getMessage(context, "number-minvalue-error", args);
                    return true;
                }

                return invokeListener();

            } catch (NumberFormatException nfe) {
                error = getMessage(context, "double-format-error", getLabel());
            }
        } else {
            if (required) {
                error = getMessage(context, "field-required-error", getLabel());
            }
        }
        
        return true;
    }
}
