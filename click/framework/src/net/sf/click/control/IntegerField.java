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
 * the control listener if there is no parsing error.
 * <p/>
 * The IntegerField uses a JavaScript onKeyPress() integerFilter() method to prevent
 * users from entering invalid characters. To enable number key filtering
 * reference the method {@link Form#getHtmlImports()} in the page template
 * (imports click/form.js file). For example:
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
 * @version $Id$
 */
public class IntegerField extends TextField {

    private static final long serialVersionUID = -2239992411551673682L;

    // ----------------------------------------------------- Instance Variables

    /** The maximum field value. */
    protected int maxvalue = Integer.MAX_VALUE;

    /** The minimum field value. */
    protected int minvalue = Integer.MIN_VALUE;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a IntegerField field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public IntegerField(String label) {
        super(label);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a IntegerField field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public IntegerField(String name, String label) {
        super(name, label);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a IntegerField field with the given label and required status.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     * @param required the field required status
     */
    public IntegerField(String label, boolean required) {
        this(label);
        setRequired(required);
    }

    /**
     * Create a IntegerField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public IntegerField() {
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
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

    // --------------------------------------------------------- Public Methods

    /**
     * Process the IntegerField submission. If the Integer value can be parsed
     * the controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
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
        value = getRequestValue();

        if (!validate()) {
            return true;
        }

        int length = value.length();
        if (length > 0) {
            try {
                int intValue = Integer.parseInt(value);
                if (intValue > maxvalue) {
                    Object[] args = new Object[] { getLabel(), new Integer(maxvalue) };
                    setError(getMessage("number-maxvalue-error", args));
                    return true;
                }
                if (intValue < minvalue) {
                    Object[] args = new Object[] { getLabel(), new Integer(minvalue) };
                    setError(getMessage("number-minvalue-error", args));
                    return true;
                }

                return invokeListener();

            } catch (NumberFormatException nfe) {
                setError(getMessage("integer-format-error", getLabel()));
            }
        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getLabel()));
            }
        }

        return true;
    }
}
