/*
 * Copyright 2004 Malcolm A. Edgar
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
 * <p/>
 * <table class='form'><tr>
 * <td>Password Field</td>
 * <td><input type='password' value='password' title='PasswordField Control'/></td>
 * </tr></table>
 * <p/>
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

    /**
     * Construct the Password Field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public PasswordField(String label) {
        super(label);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns the 'password' input field type.
     *
     * @return 'password'
     */
    public String getType() {
        return "password";
    }
}
