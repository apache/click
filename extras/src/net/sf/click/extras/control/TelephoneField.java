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
 * Provides a Telephone Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Telephone Field</td>
 * <td><input type='text' size='20' value='(02) 9283 0321' title='TelephoneField Control'/></td>
 * </tr>
 * </table>
 *
 * TelephoneField will validate the telephone number when the control is
 * processed and invoke the control listener if defined.
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class TelephoneField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the TelephoneField with the given name. The default text field
     * size is 20 characters.
     *
     * @param name the name of the field
     */
    public TelephoneField(String name) {
        super(name);
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    /**
     * Construct the TelephoneField with the given name and required status.
     * The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public TelephoneField(String name, boolean required) {
        super(name);
        setRequired(required);
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    /**
     * Construct the TelephoneField with the given name and label. The default
     * text field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public TelephoneField(String name, String label) {
        super(name, label);
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    /**
     * Construct the TelephoneField with the given name, label and required
     * status. The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public TelephoneField(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    /**
     * Construct the TelephoneField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public TelephoneField(String name, String label, int size) {
        super(name, label);
        setSize(size);
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    /**
     * Create a TelephoneField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public TelephoneField() {
        setAttribute("onKeyPress", "javascript:return noLetterFilter(event);");
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Validate the TelephoneField request submission.
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
     *   <li>/net/sf/click/extras/control/TelephoneField.properties
     *     <ul>
     *       <li>telephone-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        setError(null);

        super.validate();

        if (isValid()) {
            String value = getValue();
            for (int i = 0; i < value.length(); i++) {
                char aChar = value.charAt(i);
                if (Character.isLetter(aChar)) {
                    setErrorMessage("telephone-format-error");
                    return;
                }
            }
        }
    }

}
