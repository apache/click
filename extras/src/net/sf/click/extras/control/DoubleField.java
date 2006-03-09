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

import java.text.NumberFormat;
import java.text.ParseException;
import net.sf.click.control.TextField;
import java.util.Locale;

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
            value = object.toString();
        }
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
