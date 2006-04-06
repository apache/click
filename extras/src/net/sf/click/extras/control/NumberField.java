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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import net.sf.click.control.TextField;
import net.sf.click.util.MessagesMap;

import java.util.Locale;
import java.util.Map;

/**
 * Provides a Number Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Number Field</td>
 * <td><input type='text' style="text-align:right"
 *      value='3.541' title='NumberField Control'/></td>
 * </tr>
 * </table>
 *
 * NumberField uses a NumberFormat to format, parse and validate the text
 * input. The NumberFormat can either directly be set through
 * {@link #setNumberFormat(NumberFormat)} or alternatively the pattern of
 * the NumberFormat can be set with {@link #setPattern(String)}.
 * <p/>
 * When NumberField is validated and the input string can be parsed by the
 * NumberFormat than the string value of this field
 * (@link net.sf.click.control.Field#getValue()} is set to the formatted value
 * of the input. Ie if
 * you define an integer-pattern of #,##0 and the users enters 2.54 than the
 * resulting Number is 3. For all such cases the NumberFormat does recognize
 * the input as valid and <b>does not mark the NumberField as invalid</b>.
 * To get the exact string the user entered either turn validation
 * off or call {@link #getRequestValue()}.
 * <p/>
 * When the Number is set through {@link #setNumber(Number)} the value of the field
 * is also set to the formated number. The number returned from
 * {@link #getNumber()} is than the formated number. It is not the orginal Number
 * passed in. To circumvent formatting use setValue().
 * <p/>
 * NumberField adds the attribute style="text-align:right", so that the number is
 * by default aligned right in the input-field. You can replace this style by
 * setting the style-attribute to something else (or null).
 * <p/>
 * The NumberField uses a JavaScript onKeyPress() doubleFilter() method to prevent
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
 * @author Christian Essl
 */
public class NumberField extends TextField {

    private static final long serialVersionUID = 1L;

    /** The maximum field value. */
    protected double maxvalue = Double.MAX_VALUE;

    /** The minimum field value. */
    protected double minvalue = Double.MIN_VALUE;

    /** The NumberFormat for formatting the output. */
    protected NumberFormat numberFormat;

    /** The decimal pattern to use for a NumberFormat. */
    protected String pattern;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a NumberField with the given name.
     *
     * @param name the name of the field
     */
    public NumberField(String name) {
        super(name);
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
        setAttribute("style", "text-align:right");
    }

    /**
     * Construct a NumberField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public NumberField(String name, String label) {
        super(name, label);
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
        setAttribute("style", "text-align:right");
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
     * Create a NumberField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public NumberField() {
        super();
        setAttribute("onKeyPress", "javascript:return doubleFilter(event);");
        setAttribute("style", "text-align:right");
    }

    // ------------------------------------------------------ Public Attributes

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
     * Return a Map of localized messages for the Field (uses DoubleField).
     *
     * @return a Map of localized messages for the Field
     * @throws IllegalStateException if the context for the Field has not be set
     */
    public Map getMessages() {
        if (messages == null) {
            if (getContext() != null) {
                messages = new MessagesMap(DoubleField.class.getName(),
                                           getContext());

            } else {
                String msg = "Cannot initialize messages as context not set";
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }

    /**
     * Return the field Number value, or null if value was empty or a parsing
     * error occured.
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
     * Set the Number value.
     *
     * @param number the number value
     */
    public void setNumber(Number number) {
        if (number != null) {
            setValue(getNumberFormat().format(number));
        }
    }

    /**
     * Return the NumberFormat for formating/parsing the field value.
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
            numberFormat = NumberFormat.getInstance(getLocale());
            if (getPattern() != null && numberFormat instanceof DecimalFormat) {
                ((DecimalFormat) numberFormat).applyPattern(getPattern());
            }
        }
        return numberFormat;
    }

    /**
     * The NumberFormat for this TextField. By default the format
     * of the request locale is used and the {@link #pattern}
     * is set. If the {@link #getPattern()} is set than
     * the pattern will be applied to the new Format if it
     * is a DecimalFormat.
     *
     * @param format the number format
     */
    public void setNumberFormat(NumberFormat format) {
        this.numberFormat = format;
        if (format instanceof DecimalFormat
           && getPattern() != null) {
            ((DecimalFormat) format).applyPattern(getPattern());
        }
    }

    /**
     * The pattern used to format and parse the value.
     * @return returns the outputPattern or null if none is set.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * The pattern used to format and parse the value. By default it is
     * null and the pattern of the current locale is used. If set
     * the pattern will be also applied to an already set NumberFormat.
     * @param pattern the pattern to use.
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
     * error occured.
     *
     * @return the Double object representation of the Field value
     */
    public Object getValueObject() {
        return getNumber();
    }

    /**
     * Set the Number value of the field using the given object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            value = object.toString();
        }
    }

    /**
     * Return the <tt>Number.class</tt>.
     *
     * @return the <tt>Number.class</tt>
     */
    public Class getValueClass() {
        return Number.class;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Validates the NumberField request submission. If the value entered
     * by the user can be parsed by the NumberFormat the string value
     * of this Field ({@link net.sf.click.control.Field#getValue()}) is
     * set to the formatted value of the user input.
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
     *   <li>/net/sf/click/extras/control/DoubleField.properties
     *     <ul>
     *       <li>double-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        String value = getValue();

        int length = value.length();
        if (length > 0) {
            try {
                NumberFormat format = getNumberFormat();
                Number number = format.parse(value);

                double doubleValue = number.doubleValue();

                String formattedValue = format.format(number);

                if (doubleValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error",
                                    getNumberFormat().format(maxvalue));

                } else if (doubleValue < minvalue) {
                    setErrorMessage("number-minvalue-error",
                                    getNumberFormat().format(minvalue));
                } else {
                    setValue(formattedValue);
                }

            } catch (ParseException pe) {
                setError(getMessage("double-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Returns the <tt>Locale</tt> that should be used in this control.
     * By default returns the Locale of the current Context.
     *
     * @return the locale that should be used in this control
     * @throws IllegalStateException if not Context is set
     */
    protected Locale getLocale() {
        Locale locale = null;

        if (getContext() != null) {
            locale = getContext().getLocale();
        } else {
            throw new IllegalStateException("No context to get Locale from");
        }

        return locale;
    }

    /**
     * Return the field's value from the request.
     *
     * @return the field's value from the request
     */
    protected String getRequestValue() {
        String value = getContext().getRequestParameter(getName());
        if (value != null) {
            return value.trim();
        } else {
            return "";
        }
    }

}
