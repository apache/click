/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import net.sf.click.Control;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;


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
 * the control listener if defined.
 * <p/>
 * The IntegerField uses a JavaScript onKeyPress() integerFilter() method to prevent
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
public class IntegerField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constants

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
    protected final static String VALIDATE_NUMERICFIELD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateNumericField(\n"
        + "         ''{0}'',{1}, {2}, {3}, [''{4}'',''{5}'',''{6}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    /**
     * The IntegerField.js imports statement.
     */
    public static final String NUMERICFIELD_IMPORTS =
        "<script type=\"text/javascript\" src=\"$/click/IntegerField.js\"></script>\n";

    // ----------------------------------------------------- Instance Variables

    /** The maximum field value. */
    protected int maxvalue = Integer.MAX_VALUE;

    /** The minimum field value. */
    protected int minvalue = Integer.MIN_VALUE;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a IntegerField field with the given name.
     *
     * @param name the name of the field
     */
    public IntegerField(String name) {
        super(name);
        setAttribute("onKeyPress", "javascript:return integerFilter(event);");
    }

    /**
     * Construct a IntegerField field with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public IntegerField(String name, boolean required) {
        super(name, required);
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
     * Construct a IntegerField field with the given name, label and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public IntegerField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Create a IntegerField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
     * Set the Integer value of the field.
     *
     * @param integerValue the field integer value to set
     */
    public void setInteger(Integer integerValue) {
        if (integerValue != null) {
            setValue(integerValue.toString());
        } else {
            setValue(null);
        }
    }

    /**
     * Return the field Long value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Long value
     */
    public Long getLong() {
        Integer value = getInteger();
        if (value != null) {
            return new Long(value.longValue());
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
        maxvalue = value;
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
     * Return the <tt>Integer.class</tt>.
     *
     * @see net.sf.click.control.Field#getValueClass()
     *
     * @return the <tt>Integer.class</tt>
     */
    public Class getValueClass() {
        return Integer.class;
    }

    /**
     * Return the field Integer value, or null if value was empty or a parsing
     * error occured.
     *
     * @see net.sf.click.control.Field#getValueObject()
     *
     * @return the Integer object representation of the Field value
     */
    public Object getValueObject() {
        return getInteger();
    }

    /**
     * Set the integer value of the field using the given object.
     *
     * @see net.sf.click.control.Field#setValueObject(Object)
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            setValue(object.toString());

        } else {
            setValue(null);
        }
    }
    
    /**
     * Return the HTML head import statements for the IntegerField.js.
     *
     * @return the HTML head import statements for the IntegerField.js
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(NUMERICFIELD_IMPORTS, "$", path);
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
        
        return MessageFormat.format(VALIDATE_NUMERICFIELD_FUNCTION, args);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the <tt>IntegerField.js</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/net/sf/click/extras/control/IntegerField.js",
                              "click");
    }
    
    /**
     * Validate the IntegerField request submission.
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
     *   <li>/net/sf/click/extras/control/IntegerField.properties
     *     <ul>
     *       <li>integer-format-error</li>
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
                int intValue = Integer.parseInt(value);

                if (intValue > maxvalue) {
                    setErrorMessage("number-maxvalue-error", maxvalue);

                } else if (intValue < minvalue) {
                    setErrorMessage("number-minvalue-error", minvalue);
                }

            } catch (NumberFormatException nfe) {
                setError(getMessage("integer-format-error", getErrorLabel()));
            }
        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
}
