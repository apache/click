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

import java.text.NumberFormat;
import java.text.ParseException;

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
 * The DoubleField uses a JavaScript onkeypress() doubleFilter() method to prevent
 * users from entering invalid characters. To enable number key filtering
 * reference the {@link org.apache.click.util.PageImports} object in your page
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
 * The DoubleField has left justified horizontal text alignment,
 * {@link #setTextAlign(String)}.
 * <p/>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class DoubleField extends NumberField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a DoubleField with the given name.
     *
     * @param name the name of the field
     */
    public DoubleField(String name) {
        super(name);
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("left");
    }

    /**
     * Construct a DoubleField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public DoubleField(String name, String label) {
        super(name, label);
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("left");
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
     * Construct the DoubleField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public DoubleField(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the DoubleField with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public DoubleField(String name, String label, int size, boolean required) {
        this(name, label, required);
        setSize(size);
    }

    /**
     * Create a DoubleField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public DoubleField() {
        setAttribute("onkeypress", "javascript:return doubleFilter(event);");
        setTextAlign("left");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the field Double value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the field Double value
     */
    public Double getDouble() {
        Number number = getNumber();

        if (number != null) {
            return new Double(number.doubleValue());

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
        setValueObject(doubleValue);
    }

    /**
     * Return the field Float value, or null if value was empty or a parsing
     * error occurred.
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
     * Return the field Double value, or null if value was empty or a parsing
     * error occurred.
     *
     * @return the Double object representation of the Field value
     */
    public Object getValueObject() {
        return getDouble();
    }

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
