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
 * Provides a Reset control: &nbsp; &lt;input type='reset'&gt;.
 *
 * <table class='htmlHeader'>
 * <tr>
 * <td><input type='reset' value='Reset' title='Reset Control'/></td>
 * </tr>
 * </table>
 *
 * The Reset control provides input type reset field. The Reset button is
 * uses to reset the any changes a user may have made to a forms values. The
 * Reset button performs no server side processing.
 * <p/>
 * The example below illustrates adding a Reset button to a Form, and shows
 * how it will be rendered as HTML.
 * <div class="code">
 * // Java code
 * Reset reset = new Reset("Reset");
 * reset.setTitle("Undo changes");
 * form.add(reset);
 *
 * &lt;-- HTML output --&gt;
 * &lt;input type='reset' name='reset' value='Reset' title='Undo changes'&gt;
 * </div>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Button
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class Reset extends Button {

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Reset button with the given value.
     * <p/>
     * The field name will be Java property representation of the given value.
     *
     * @param value the button value
     */
    public Reset(String value) {
        super(value);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the input type: '<tt>reset</tt>'.
     *
     * @return the input type: '<tt>reset</tt>'
     */
    public String getType() {
        return "reset";
    }
}
