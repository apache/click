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
 * Provides a Integer Field control: &nbsp; &lt;input type='text'&gt;.
 * <p/>
 * <table class='form'><tr>
 * <td>Integer Field</td>
 * <td><input type='text' value='101' title='IntegerField Control'/></td>
 * </tr></table>
 * <p/>
 * IntegerField will validate the number when the control is processed and invoke
 * the control listener if there is no parsing error. 
 * <p/>
 * The IntegerField uses a JavaScript onKeyPress() numberFilter method to prevent 
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
public class IntegerField extends TextField {

    /** The maximum field value. */
    private int maxvalue = Integer.MAX_VALUE;

    /** The minimum field value. */
    private int minvalue = Integer.MIN_VALUE;

    /**
     * Construct a Integer Field field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public IntegerField(String label) {
        super(label);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the field Integer value, or null if value was empty or a parsing 
     * error occured.
     *
     * @return the field Integer value
     */
    public Integer getInteger() {
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
     * Return the field Long value, or null if value was empty or a parsing 
     * error occured.
     *
     * @return the field Long value
     */
    public Long getLong() {
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
     * Return the maximum valid integer field value.
     *
     * @return the maximum valid integer field value
     */
    public int getMaxValue() {
        return maxvalue;
    }

    /**
     * Set the maximum valid integer field value.
     *
     * @param value the maximum valid integer field value
     */
    public void setMaxValue(int value) {
        maxvalue = value ;
    }

    /**
     * Return the minimum valid integer field value.
     *
     * @return the minimum valid integer field value
     */
    public int getMinValue() {
        return minvalue;
    }
    
    /**
     * Set the miminum valid integer field value.
     *
     * @param value the miminum valid integer field value
     */
    public void setMinValue(int value) {
        minvalue = value;
    }

    /**
     * Process the IntegerField submission. If the Integer value can be parsed
     * the controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs. 
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>field-required-error</li>
     * <li>integer-format-error</li>
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
                int intValue = Integer.parseInt(value);
                if (intValue > maxvalue) {
                    Object[] args = new Object[] { getLabel(), new Integer(maxvalue) };
                    error = getMessage(context, "number-maxvalue-error", args);
                    return true;
                }
                if (intValue < minvalue) {
                    Object[] args = new Object[] { getLabel(), new Integer(minvalue) };
                    error = getMessage(context, "number-minvalue-error", args);
                    return true;
                }

                return invokeListener();

            } catch (NumberFormatException nfe) {
                error = getMessage(context, "integer-format-error", getLabel());
            }
        } else {
            if (required) {
                error = getMessage(context, "field-required-error", getLabel());
            }
        }
        
        return true;
    }
}
