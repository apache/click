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
 * the control listener if defined.
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class EmailField extends TextField {

    private static final long serialVersionUID = 1L;

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
     * Process the EmailField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-maxlenght-error</li>
     *       <li>field-minlength-error</li>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/net/sf/click/extras/control/EmailField.properties
     *     <ul>
     *       <li>email-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        super.validate();

        if (isValid() && isRequired()) {
            String value = getValue();
            int length = value.length();

            int index = value.indexOf("@");
            if (index < 1 || index == length - 1) {
                setErrorMessage("email-format-error");
                return;
            }

            if (!Character.isLetterOrDigit(value.charAt(0))) {
                setErrorMessage("email-format-error");
                return;
            }

            if (!Character.isLetterOrDigit(value.charAt(length - 1))) {
                setErrorMessage("email-format-error");
                return;
            }
        }
    }

}
