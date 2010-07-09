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
package org.apache.click.control;

/**
 * Provides a Password Field control: &nbsp; &lt;input type='password'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Password Field</td>
 * <td><input type='password' value='password' title='PasswordField Control'/></td>
 * </tr>
 * </table>
 *
 * For an PasswordField code example see the {@link org.apache.click.control.Form}
 * Javadoc example.
 * <p/>
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class PasswordField extends TextField {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a PasswordField with the given name. The default password
     * field size is 20 characters.
     *
     * @param name the name of the field
     */
    public PasswordField(String name) {
        super(name);
    }

    /**
     * Construct a PasswordField with the given name and required status. The
     * default password field size is 20 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public PasswordField(String name, boolean required) {
        super(name, required);
    }

    /**
     * Construct a PasswordField with the given name and label. The default
     * password field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public PasswordField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct a PasswordField with the given name, label and required status.
     * The default password field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public PasswordField(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Construct a PasswordField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public PasswordField(String name, String label, int size) {
        super(name, label, size);
    }

    /**
     * Construct the PasswordField with the given name, label, size and required
     * status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public PasswordField(String name, String label, int size, boolean required) {
        super(name, label, size, required);
    }

    /**
     * Create a PasswordField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public PasswordField() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the input type: '<tt>password</tt>'.
     *
     * @return the input type: '<tt>password</tt>'
     */
    @Override
    public String getType() {
        return "password";
    }
}
