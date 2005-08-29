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

import net.sf.click.util.ClickUtils;

/**
 * Provides a Button control: &nbsp; &lt;input type='button'&gt;.
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
 * <p/>
 * The example below adds a back button to a form, which when clicked returns
 * to the previous page.
 *
 * <pre class="codeJava">
 * Button backButton = <span class="kw">new</span> Button(<span class="st">" &lt Back "</span>);
 * backButton.setOnClick(<span class="st">"history.back();"</span>);
 * backButton.setTitle(<span class="st">"Return to previous page"</span>);
 * form.add(backButton); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;input type='button' name='back' value=' &lt Back ' onclick='history.back();'
 *        title='Return to previous page'&gt; </pre>
 *
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @see Reset
 * @see Submit
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Button extends Field {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = -4565346013990356183L;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a button with the given value.
     * <p/>
     * The field name will be Java property representation of the given value.
     *
     * @param value the button value
     */
    public Button(String value) {
        this.value = value;
        this.name = ClickUtils.toName(value);
    }

    /**
     * Create a button with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public Button() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Returns the button onClick attribute value, or null if not defined.
     *
     * @return the button onClick attribute value, or null if not defined.
     */
    public String getOnClick() {
        if (attributes != null) {
            return (String) attributes.get("onclick");
        } else {
            return null;
        }
    }

    /**
     * Sets the button onClick attribute value.
     *
     * @param value the onClick attribute value.
     */
    public void setOnClick(String value) {
        setAttribute("onclick", value);
    }

    /**
     * Return the input type: '<tt>button</tt>'
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
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        return true;
    }

    /**
     * Return a HTML rendered Button string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(40);

        buffer.append("<input type='");
        buffer.append(getType());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("' value='");
        buffer.append(getValue());
        buffer.append("'");
        if (getTitle() != null) {
            buffer.append(" title='");
            buffer.append(getTitle());
            buffer.append("' ");
        }

        renderAttributes(buffer);

        buffer.append(getDisabled());

        buffer.append(">");

        return buffer.toString();
    }
}
