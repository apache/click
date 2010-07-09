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
 * Provides a Reset control: &nbsp; &lt;input type='reset'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td><input type='reset' value='Reset' title='Reset Control'/></td>
 * </tr>
 * </table>
 *
 * The Reset control provides input type reset field. The Reset button is
 * uses to reset the any changes a user may have made to a forms values. The
 * Reset button performs no server side processing.
 *
 * <h3>Reset Example</h3>
 *
 * The example below illustrates adding a Reset button to a Form, and shows
 * how it will be rendered as HTML.
 *
 * <pre class="codeJava">
 * Reset reset = <span class="kw">new</span> Reset(<span class="st">"reset"</span>);
 * reset.setTitle(<span class="st">"Undo changes"</span>);
 * form.add(reset); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;input type='reset' name='reset' value='Reset' title='Undo changes'&gt; </pre>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Button
 * @see Submit
 */
public class Reset extends Button {

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Reset button with the given name.
     *
     * @param name the button name
     */
    public Reset(String name) {
        super(name);
    }

    /**
     * Create a Reset button with the given name and label.
     *
     * @param name the button name
     * @param label the button display label
     */
    public Reset(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Reset field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Reset() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the input type: '<tt>reset</tt>'.
     *
     * @return the input type: '<tt>reset</tt>'
     */
    @Override
    public String getType() {
        return "reset";
    }
}
