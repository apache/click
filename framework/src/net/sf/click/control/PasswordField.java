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
package net.sf.click.control;

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
 * For an PasswordField code example see the {@link net.sf.click.control.Form}
 * Javadoc example.
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
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
     * Create a PasswordField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
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
    public String getType() {
        return "password";
    }
}
