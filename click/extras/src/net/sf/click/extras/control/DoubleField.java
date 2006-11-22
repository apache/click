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

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

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
 * the control listener if defined.
 * <p/>
 * The DoubleField uses a JavaScript onKeyPress() doubleFilter() method to prevent
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
public class DoubleField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The maximum field value. */
    protected double maxvalue = Double.POSITIVE_INFINITY;

    /** The minimum field value. */
    protected double minvalue = Double.NEGATIVE_INFINITY;

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
     * Construct a DoubleField with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public DoubleField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Create a DoubleField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
                Locale locale = getContext().getLocale();
                NumberFormat format = NumberFormat.getNumberInstance(locale);
                double doubleValue = format.parse(value).doubleValue();

                return new Double(doubleValue);

            } catch (ParseException nfe) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Set the Double value of the field.
     *
     * @param doubleValue the double value to set
     */
    public void setDouble(Double doubleValue) {
        if (doubleValue != null) {
            Locale locale = getContext().getLocale();
            NumberFormat format = NumberFormat.getNumberInstance(locale);

            setValue(format.format(doubleValue.doubleValue()));

        } else {
            setValue(null);
        }
    }

    /**
     * Return the field Float value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Float value
     */
    public Float getFloat() {
        Double value = getDouble();
        if (value != null) {
            return new Float(value.floatValue());
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
     * Return the <tt>Double.class</tt>.
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
     * @return the Double object representation of the Field value
     */
    public Object getValueObject() {
        return getDouble();
    }

    /**
     * Set the double value of the field using the given object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            if (object instanceof Number) {
                Number number = (Number) object;
                Locale locale = getContext().getLocale();
                NumberFormat format = NumberFormat.getNumberInstance(locale);

                setValue(format.format(number.doubleValue()));

            } else {
                setValue(object.toString());
            }
        }
    }

    /**
     * Return the HTML head import statements for the NumberField.js.
     *
     * @return the HTML head import statements for the NumberField.js
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(IntegerField.NUMERICFIELD_IMPORTS,
                                   "$",
                                   path);
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
    public String getValidationJavaScript() {
        Object[] args = new Object[7];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = String.valueOf(getMinValue());
        args[3] = String.valueOf(getMaxValue());
        args[4] = getMessage("field-required-error", getErrorLabel());
        args[5] = getMessage("number-minvalue-error",
                new Object[]{getErrorLabel(), String.valueOf(getMinValue())});
        args[6] = getMessage("number-maxvalue-error",
                new Object[]{getErrorLabel(), String.valueOf(getMaxValue())});

        return MessageFormat.format(IntegerField.VALIDATE_NUMERICFIELD_FUNCTION,
                                    args);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the DoubleField request submission.
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
        setError(null);

        String value = getValue();

        int length = value.length();
        if (length > 0) {
            try {
                Locale locale = getContext().getLocale();
                NumberFormat format = NumberFormat.getNumberInstance(locale);
                double doubleValue = format.parse(value).doubleValue();

                if (doubleValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error", maxvalue);

                } else if (doubleValue < minvalue) {

                    setErrorMessage("number-minvalue-error", minvalue);
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
}
