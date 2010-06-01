/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import org.apache.click.Context;

import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;

/**
 * Provides a Number Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Number Field</td>
 * <td><input type='text' style="text-align:left"
 *      value='127,500.00' title='NumberField Control'/></td>
 * </tr>
 * </table>
 *
 * NumberField uses a {@link NumberFormat} to format, parse and validate the
 * input text. The number format can either directly be set through
 * {@link #setNumberFormat(NumberFormat)} or by setting the number format
 * pattern with {@link #setPattern(String)}.
 * <p/>
 * When NumberField is validated and the input string can be parsed by the
 * NumberFormat then the string value of this field
 * (@link org.apache.click.control.Field#getValue()} is set to the formatted value
 * of the input.
 * <p/>
 * For example if you define an integer pattern of <tt>"#,##0"</tt> and the
 * users enters "2.54" then the resulting Number is 3. For all such cases the
 * NumberFormat does recognize the input as valid and does <b>not</b> mark the
 * field as invalid.
 * To get the exact string the user entered either turn validation
 * off or call {@link #getValue()}.
 * <p/>
 * When the Number is set through {@link #setNumber(Number)} the value of the field
 * is also set to the formatted number. The number returned from
 * {@link #getNumber()} is then the formatted number. It is not the original Number
 * passed in. To circumvent formatting use setValue().
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The NumberField control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.js</tt></li>
 * </ul>
 *
 * The NumberField uses a JavaScript onkeypress() doubleFilter() method to prevent
 * users from entering invalid characters.  To enable number key filtering
 * reference the variables <tt class="blue">$jsElements</tt> and
 * <tt class="blue">$headElements</tt> in your page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$headElements</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 *
 * <span class="red">$form</span>
 *
 * <span class="blue">$jsElements</span>
 * &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * The NumberField has right justified horizontal text alignment,
 * {@link #setTextAlign(String)}.
 * <p/>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class NumberField extends TextField {

    private static final long serialVersionUID = 1L;

    // Constants --------------------------------------------------------------

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the minimum value</li>
     * <li>3 - is the maximum value</li>
     * <li>4 - is the localized error message for required validation</li>
     * <li>5 - is the localized error message for minimum value validation</li>
     * <li>6 - is the localized error message for maximum value validation</li>
     * </ul>
     */
    protected final static String VALIDATE_NUMBER_FIELD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateNumberField(\n"
        + "         ''{0}'',{1}, {2}, {3}, [''{4}'',''{5}'',''{6}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Instance Variables -----------------------------------------------------

    /** The maximum field value. */
    protected double maxvalue = Double.POSITIVE_INFINITY;

    /** The minimum field value. */
    protected double minvalue = Double.NEGATIVE_INFINITY;

    /** The NumberFormat for formatting the output. */
    protected NumberFormat numberFormat;

    /** The decimal pattern to use for a NumberFormat. */
    protected String pattern;

    // Constructors -----------------------------------------------------------

    /**
     * Construct a NumberField with the given name.
     *
     * @param name the name of the field
     */
    public NumberField(String name) {
        super(name);
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("right");
    }

    /**
     * Construct a NumberField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public NumberField(String name, String label) {
        super(name, label);
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("right");
    }

    /**
     * Construct a NumberField with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public NumberField(String name, boolean required) {
        this(name);
        setRequired(required);
    }

    /**
     * Construct a NumberField with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public NumberField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Construct the NumberField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public NumberField(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the NumberField with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public NumberField(String name, String label, int size, boolean required) {
        this(name, label, required);
        setSize(size);
    }

    /**
     * Create a NumberField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public NumberField() {
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("right");
    }

    // Public Attributes ------------------------------------------------------

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
        maxvalue = value;
    }

    /**
     * Set the minimum valid double field value.
     *
     * @param value the minimum valid double field value
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
     * Return the field Number value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the field Number value
     */
    public Number getNumber() {
        String value = getValue();
        if (value != null && value.length() > 0) {
            try {
                return getNumberFormat().parse(value);

            } catch (ParseException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Set the Number value of the field.
     *
     * @param number the field number value to set
     */
    public void setNumber(Number number) {
        if (number != null) {
            setValue(getNumberFormat().format(number));
        } else {
            setValue(null);
        }
    }

    /**
     * Return the NumberFormat for formatting/parsing the field value.
     * If no NumberFormat has been set before, the NumberFormat for the
     * requests locale is used. If this format is a DecimalNumberFormat the
     * {@link #pattern} is applied to it.
     * <p/>
     * This method is used through-out this class to obtain the NumberFormat.
     * By overriding this method full control is given onto which
     * NumberFormat is used for formatting, parsing and validating.
     *
     * @return the NumberFormat to format/parse the field's value
     */
    public NumberFormat getNumberFormat() {
        if (numberFormat == null) {

            Locale locale = getContext().getLocale();
            numberFormat = NumberFormat.getInstance(locale);

            if (getPattern() != null
                && numberFormat instanceof DecimalFormat) {

                ((DecimalFormat) numberFormat).applyPattern(getPattern());
            }
        }

        return numberFormat;
    }

    /**
     * Set the NumberFormat for this field.
     * <p/>
     * By default the format of the request locale is used and the
     * {@link #pattern} is set. If the {@link #getPattern()} is set then
     * the pattern will be applied to the new Format if it a DecimalFormat.
     *
     * @param format the number format
     */
    public void setNumberFormat(NumberFormat format) {
        numberFormat = format;

        if (format instanceof DecimalFormat) {
            if (getPattern() != null) {
                ((DecimalFormat) numberFormat).applyPattern(getPattern());
            }
        }
    }

    /**
     * Return the number pattern used for formatting and parsing.
     *
     * @return the number pattern used for formatting and parsing
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Set the number pattern used for formatting and parsing.
     * <p/>
     * By default the pattern is null and the default number pattern of the
     * context locale is used. If set the pattern will be also applied to an
     * already set NumberFormat.
     *
     * @param pattern the pattern used for formatting and parsing
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;

        if (pattern != null) {
            if (numberFormat instanceof DecimalFormat) {
                ((DecimalFormat) numberFormat).applyPattern(pattern);
            }
        }
    }

    /**
     * Return the field Number value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the Number object representation of the Field value
     */
    @Override
    public Object getValueObject() {
        return getNumber();
    }

    /**
     * Set the Number value of the field using the given object.
     *
     * @param object the object value to set
     */
    @Override
    public void setValueObject(Object object) {
        if (object instanceof Number) {
            setNumber((Number) object);

        } else {
            if (object != null) {
                setValue(object.toString());

            } else {
                setValue(null);
            }
        }
    }

    /**
     * Returns the NumberField HTML HEAD elements for the
     * <tt>click/extras-control.js</tt> resource.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new JsImport("/click/extras-control.js", versionIndicator));
        }
        return headElements;
    }

    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[7];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = String.valueOf(getMinValue());
        args[3] = String.valueOf(getMaxValue());
        args[4] = getMessage("field-required-error", getErrorLabel());
        args[5] = getMessage("number-minvalue-error",
                getErrorLabel(), String.valueOf(getMinValue()));
        args[6] = getMessage("number-maxvalue-error",
                getErrorLabel(), String.valueOf(getMaxValue()));

        return MessageFormat.format(VALIDATE_NUMBER_FIELD_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Validates the NumberField request submission. If the value entered
     * by the user can be parsed by the NumberFormat the string value
     * of this Field ({@link org.apache.click.control.Field#getValue()}) is
     * set to the formatted value of the user input.
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
    @Override
    public void validate() {
        setError(null);

        String value = getValue();

        int length = value.length();
        if (length > 0) {
            try {
                NumberFormat format = getNumberFormat();
                Number number = format.parse(value);

                double doubleValue = number.doubleValue();

                if (doubleValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error",
                                    getNumberFormat().format(maxvalue));

                } else if (doubleValue < minvalue) {
                    setErrorMessage("number-minvalue-error",
                                    getNumberFormat().format(minvalue));

                } else {
                    String formattedValue = format.format(number);
                    setValue(formattedValue);
                }

            } catch (ParseException pe) {
                setError(getMessage("number-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
}
