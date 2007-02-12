/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Submit control: &nbsp; &lt;input type='submit'&gt;.
 *
 * <table class='htmlHeader cellspacing='6'>
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

    private static final long serialVersionUID = 1L;

    // ----------------------------------------------------- Instance Variables

    /** The button is clicked. */
    protected boolean clicked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a Submit button with the given name.
     *
     * @param name the button name
     */
    public Submit(String name) {
        super(name);
    }

    /**
     * Create a Submit button with the given name and label.
     *
     * @param name the button name
     * @param label the button display label
     */
    public Submit(String name, String label) {
        super(name, label);
    }

    /**
     * Create a Submit button with the given name, listener object and
     * listener method.
     *
     * @param name the button name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method
     * is blank
     */
    public Submit(String name, Object listener, String method) {
        super(name);

        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create a Submit button with the given name, label, listener object and
     * listener method.
     *
     * @param name the button name
     * @param label the button display label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if listener is null or if the method
     * is blank
     */
    public Submit(String name, String label, Object listener, String method) {
        super(name, label);

        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an Submit button with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Submit() {
        super();
    }

    // ------------------------------------------------------ Public Attributes


    /**
     * Return true if client side JavaScript form validation will be cancelled
     * by pressing this button.
     *
     * @return true if button will cancel JavaScript form validation
     */
    public boolean getCancelJavaScriptValidation() {
        String value = getAttribute("onclick");

        return "form.onsubmit=null;".equals(value);
    }

    /**
     * Set whether client side JavaScript form validation will be cancelled
     * by pressing this button.
     *
     * @param cancel the cancel JavaScript form validation flag
     */
    public void setCancelJavaScriptValidation(boolean cancel) {
        if (cancel) {
            setAttribute("onclick", "form.onsubmit=null;");
        } else {
            setAttribute("onclick", null);
        }
    }

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
     * Bind the request submission, setting the {@link Field#value} and
     * {@link #clicked} properties if defined.
     */
    public void bindRequestValue() {
        this.value = getContext().getRequestParameter(getName());

        if (value != null) {
            this.clicked = getLabel().equals(value);
        }
    }

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
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        if (getContext() == null) {
            String msg = "context is not defined for field: " + getName();
            throw new IllegalStateException(msg);
        }

        bindRequestValue();

        if (isClicked()) {
            return invokeListener();

        } else {
            return true;
        }
    }

}
