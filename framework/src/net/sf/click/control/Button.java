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
package net.sf.click.control;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Button control: &nbsp; &lt;input type='button'/&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <input type='button' value='Button' title='Button Control'/>
 * </td></tr>
 * </table>
 *
 * The Button control is used to render a JavaScript enabled button which can
 * perform client side logic. The Button control provides no servier side
 * processing. If server side processing is required use {@link Submit} instead.
 *
 * <h3>Button Example</h3>
 *
 * The example below adds a back button to a form, which when clicked returns
 * to the previous page.
 *
 * <pre class="codeJava">
 * Button backButton = <span class="kw">new</span> Button(<span class="st">"back"</span>, <span class="st">" &lt Back "</span>);
 * backButton.setOnClick(<span class="st">"history.back();"</span>);
 * backButton.setTitle(<span class="st">"Return to previous page"</span>);
 * form.add(backButton); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;input type='button' name='back' value=' &lt Back ' onclick='history.back();'
 *        title='Return to previous page'/&gt; </pre>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Reset
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class Button extends Field {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a button with the given name.
     *
     * @param name the button name
     */
    public Button(String name) {
        super(name);
    }

    /**
     * Create a button with the given name and label. The button label is
     * rendered as the HTML "value" attribute.
     *
     * @param name the button name
     * @param label the button label
     */
    public Button(String name, String label) {
        setName(name);
        setLabel(label);
    }

    /**
     * Create a button with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Button() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the button's html tag: <tt>input</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    public String getTag() {
        return "input";
    }

    /**
     * Returns the button onclick attribute value, or null if not defined.
     *
     * @return the button onclick attribute value, or null if not defined.
     */
    public String getOnClick() {
        if (attributes != null) {
            return (String) attributes.get("onclick");
        } else {
            return null;
        }
    }

    /**
     * Sets the button onclick attribute value.
     *
     * @param value the onclick attribute value.
     */
    public void setOnClick(String value) {
        setAttribute("onclick", value);
    }

    /**
     * Return the input type: '<tt>button</tt>'.
     *
     * @return the input type: '<tt>button</tt>'
     */
    public String getType() {
        return "button";
    }

    // -------------------------------------------------------- Public Methods

    /**
     * Returns true, as buttons perform no server side logic.
     *
     * @see net.sf.click.control.Field#onProcess()
     *
     * @return true
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * This method does nothing. Subclasses may override this method to perform
     * pre rendering logic.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
    }

    /**
     * @see AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    public int getControlSizeEst() {
        return 40;
    }

    /**
     * Render the HTML representation of the Button. Note the button label is
     * rendered as the HTML "value" attribute.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        buffer.elementStart(getTag());

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getLabel());
        buffer.appendAttribute("title", getTitle());
        if (getTabIndex() > 0) {
            buffer.appendAttribute("tabindex", getTabIndex());
        }

        appendAttributes(buffer);

        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();
    }
}
