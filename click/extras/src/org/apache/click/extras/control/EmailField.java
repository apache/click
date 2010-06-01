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

import java.text.MessageFormat;
import java.util.List;
import org.apache.click.Context;

import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;

/**
 * Provides a Email Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Email Field</td>
 * <td><input type='text' size='30' value='medgar@mycorp.com' title='EmailField Control'/></td>
 * </tr>
 * </table>
 *
 * EmailField will validate the email when the control is processed and invoke
 * the control listener if defined.
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The EmailField control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.js</tt></li>
 * </ul>
 *
 * To import this EmailField file simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 * <p/>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class EmailField extends TextField {

    private static final long serialVersionUID = 1L;

    // Constants --------------------------------------------------------------

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the minimum length</li>
     * <li>3 - is the maximum length</li>
     * <li>4 - is the localized error message for required validation</li>
     * <li>5 - is the localized error message for minimum length validation</li>
     * <li>6 - is the localized error message for maximum length validation</li>
     * <li>7 - is the localized error message for format validation</li>
     * </ul>
     */
    protected final static String VALIDATE_EMAILFIELD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateEmailField(\n"
        + "         ''{0}'',{1}, {2}, {3}, [''{4}'',''{5}'',''{6}'', ''{7}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Constructors -----------------------------------------------------------

    /**
     * Construct an Email Field with the given name. The default email field
     * size is 30 characters.
     *
     * @param name the name of the field
     */
    public EmailField(String name) {
        super(name);
        setSize(30);
    }

    /**
     * Construct an Email Field with the given name and label.
     * The default email field size is 30 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public EmailField(String name, String label) {
        super(name, label);
        setSize(30);
    }

    /**
     * Construct an Email Field with the given name and required status.
     * The default email field size is 30 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public EmailField(String name, boolean required) {
        this(name);
        setRequired(required);
    }

    /**
     * Construct an Email Field with the given name, label and required status.
     * The default email field size is 30 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public EmailField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Construct the Email Field with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public EmailField(String name, String label, int size) {
        super(name, label);
        setSize(size);
    }

    /**
     * Construct the Email Field with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public EmailField(String name, String label, int size, boolean required) {
        super(name, label, required);
        setSize(size);
    }

    /**
     * Create an Email Field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public EmailField() {
        super();
        setSize(30);
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Returns the EmailField HTML head imports statements for the
     * <tt>click/extras-control.js</tt> resource.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML head import statements for the control
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
        Object[] args = new Object[8];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = String.valueOf(getMinLength());
        args[3] = String.valueOf(getMaxLength());
        args[4] = getMessage("field-required-error", getErrorLabel());
        args[5] = getMessage("field-minlength-error",
                getErrorLabel(), String.valueOf(getMinLength()));
        args[6] = getMessage("field-maxlength-error",
                getErrorLabel(), String.valueOf(getMaxLength()));
        args[7] = getMessage("email-format-error", getErrorLabel());

        return MessageFormat.format(VALIDATE_EMAILFIELD_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Process the EmailField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-maxlength-error</li>
     *       <li>field-minlength-error</li>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/org/apache/click/extras/control/EmailField.properties
     *     <ul>
     *       <li>email-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    @Override
    public void validate() {
        setError(null);

        super.validate();

        if (isValid() && getValue().length() > 0) {
            String value = getValue();
            int length = value.length();

            int atIndex = value.indexOf("@");
            if (atIndex < 1 || atIndex == length - 1) {
                setErrorMessage("email-format-error");
                return;
            }

            int dotIndex = value.lastIndexOf(".");
            if (dotIndex == -1
                || dotIndex < atIndex
                || dotIndex == length - 1) {
                setErrorMessage("email-format-error");
                return;
            }

            if (!Character.isLetterOrDigit(value.charAt(0))) {
                setErrorMessage("email-format-error");
                return;
            }

            if (!Character.isLetterOrDigit(value.charAt(length - 1))) {
                setErrorMessage("email-format-error");
            }
        }
    }

}
