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

import org.apache.click.control.TextField;

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
 * The TelephoneField uses a JavaScript onkeypress() noLetterFilter() method to prevent
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
 * <p/>
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class TelephoneField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the TelephoneField with the given name. The default text field
     * size is 20 characters and the minimum valid length is 10 characters.
     *
     * @param name the name of the field
     */
    public TelephoneField(String name) {
        super(name);
        setAttribute("onkeypress", "javascript:return noLetterFilter(event);");
        setMinLength(10);
    }

    /**
     * Construct the TelephoneField with the given name and required status.
     * The default text field size is 20 characters and the minimum valid length
     * is 10 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public TelephoneField(String name, boolean required) {
        this(name);
        setRequired(required);
    }

    /**
     * Construct the TelephoneField with the given name and label. The default
     * text field size is 20 characters and the minimum valid length is 10
     * characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public TelephoneField(String name, String label) {
        super(name, label);
        setAttribute("onkeypress", "javascript:return noLetterFilter(event);");
        setMinLength(10);
    }

    /**
     * Construct the TelephoneField with the given name, label and required
     * status. The default text field size is 20 characters and the minimum valid
     * length is 10 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public TelephoneField(String name, String label, boolean required) {
        this(name, label);
        setRequired(required);
    }

    /**
     * Construct the TelephoneField with the given name, label and size.
     * The default the minimum valid length is 10 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public TelephoneField(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the TelephoneField with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public TelephoneField(String name, String label, int size, boolean required) {
        this(name, label, required);
        setSize(size);
    }

    /**
     * Create a TelephoneField with no name defined. The default the minimum
     * valid length is 10 characters.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public TelephoneField() {
        setAttribute("onkeypress", "javascript:return noLetterFilter(event);");
        setMinLength(10);
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
     *   <li>/org/apache/click/extras/control/TelephoneField.properties
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
            int digitCount = 0;
            String value = getValue();
            for (int i = 0; i < value.length(); i++) {
                char aChar = value.charAt(i);
                if (Character.isLetter(aChar)) {
                    setErrorMessage("telephone-format-error");
                    return;
                }
                if (Character.isDigit(aChar)) {
                    digitCount++;
                }
            }

            if (digitCount > 0
                && getMinLength() > 0
                && digitCount < getMinLength()) {

                setErrorMessage("field-minlength-error", getMinLength());
                return;
            }
        }
    }

}
