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

import net.sf.click.control.TextField;

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
 * the control listener if the email format is valid.
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class EmailField extends TextField {

    private static final long serialVersionUID = 7526447883893011813L;

    // ----------------------------------------------------------- Constructors

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
        size = 30;
    }

    /**
     * Create an Email Field with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * The default email field size is 30 characters.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public EmailField() {
        super();
        setSize(30);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the EmailField submission. If the Email value is valid the
     * controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>email-format-error</li>
     * <li>field-maxlength-error</li>
     * <li>field-minlength-error</li>
     * <li>field-required-error</li>
     * </ul></blockquote>
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        value = getRequestValue();

        if (!validate()) {
            return true;
        }

        int length = value.length();
        if (length > 0) {
            if (getMinLength() > 0 && length < getMinLength()) {
                Object[] args = new Object[] {
                    getErrorLabel(), new Integer(getMinLength())
                };
                setError(getMessage("field-minlength-error", args));
                return true;
            }

            if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[] {
                    getErrorLabel(), new Integer(getMaxLength())
                };
                setError(getMessage("field-maxlength-error", args));
                return true;
            }

            int index = value.indexOf("@");
            if (index < 1 || index == length - 1) {
                setError(getMessage("email-format-error", getErrorLabel()));
                return true;
            }
            if (!Character.isLetterOrDigit(value.charAt(0))) {
                setError(getMessage("email-format-error", getErrorLabel()));
                return true;
            }
            if (!Character.isLetterOrDigit(value.charAt(length - 1))) {
                setError(getMessage("email-format-error", getErrorLabel()));
                return true;
            }

            return invokeListener();

        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getErrorLabel()));
            }
        }

        return true;
    }

}
