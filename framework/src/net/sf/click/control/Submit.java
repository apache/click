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
 * Provides a Submit control: &nbsp; &lt;input type='submit'&gt;.
 *
 * <table class='htmlHeader'>
 * <tr>
 * <td><input type='submit' value='Submit' title='Submit Control'/></td>
 * </tr>
 * </table>
 *
 * The Submit control supports server side processing and can be used to invoke
 * Control listeners.
 * <p/>
 * For an Submit code example see the {@link net.sf.click.control.Form}
 * Javadoc example.
 * <p/>
 * See also the W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class Submit extends Button {

    // ----------------------------------------------------- Instance Variables

    /** The button is clicked. */
    protected boolean clicked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Submit button with the given value
     * <p/>
     * The field name will be Java property representation of the given value.
     *
     * @param value the button value
     */
    public Submit(String value) {
        super(value);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Returns the true if the submit button was clicked, or false otherwise.
     *
     * @return the true if the submit button was clicked
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Return the input type: '<tt>submit</tt>'.
     *
     * @return the input type: '<tt>submit</tt>'
     */
    public String getType() {
        return "submit";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the submit event and return true to continue event processing.
     * <p/>
     * If the submit button is clicked and a Control listener is defined, the
     * listener method will be invoked and its boolean return value will be
     * returned by this method.
     * <p/>
     * Submit buttons will be processed after all the non Button Form Controls
     * have been processed. Submit buttons will be processed in the order
     * they were added to the Form.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        clicked = value.equals(getContext().getRequest().getParameter(getName()));

        if (clicked) {
            return invokeListener();
        } else {
            return true;
        }
    }
}
