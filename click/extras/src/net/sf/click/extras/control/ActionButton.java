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
package net.sf.click.extras.control;

import org.apache.commons.lang.StringUtils;

import net.sf.click.control.Button;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a ActionButton control: &nbsp; &lt;input type="button"/&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr><td>
 * <input type='button' value='Action Button' title='ActionButton Control'/>
 * </td></tr>
 * </table>
 *
 * The ActionButton provides equivalent functionality to the
 * {@link net.sf.click.control.ActionLink} control, where you can attach
 * action listeners to the button. When the button is clicked it will make a
 * JavaScript GET request. This request is then processed by the ActionButton
 * and its listener is invoked.
 * <p/>
 * Example usage of the ActionButton:
 *
 * <pre class="codeJava">
 * ActionButton actionButton = <span class="kw">new</span> ActionButton(<span class="st">"button"</span>);
 * actionButton.setListener(<span class="kw">this</span>, <span class="st">"onButtonClick"</span>);
 * addControl(actionButton); </pre>
 *
 * @see net.sf.click.control.ActionLink
 *
 * @author Malcolm Edgar
 */
public class ActionButton extends Button {

    private static final long serialVersionUID = 1L;

    /** The action button parameter name: &nbsp; <tt>actionButton</tt>. */
    public static final String ACTION_BUTTON = "actionButton";

    /** The value parameter name: &nbsp; <tt>value</tt>. */
    public static final String VALUE = "value";

    // ----------------------------------------------------- Instance Variables

    /** The button is clicked. */
    protected boolean clicked;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an ActionButton for the given name.
     *
     * @param name the action button name
     * @throws IllegalArgumentException if the name is null
     */

    public ActionButton(String name) {
        super(name);
    }

    /**
     * Create an ActionButton for the given name and label.
     *
     * @param name the action button name
     * @param label the action button label
     * @throws IllegalArgumentException if the name is null
     */
    public ActionButton(String name, String label) {
        super(name, label);
    }

    /**
     * Create an ActionButton for the given name, listener object and listener
     * method.
     *
     * @param name the action button name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionButton(String name, Object listener, String method) {
        setName(name);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an ActionButton for the given name, label, listener object and
     * listener method.
     *
     * @param name the action button name
     * @param label the action button label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionButton(String name, String label, Object listener,
            String method) {

        setName(name);
        setLabel(label);
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an ActionButton with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public ActionButton() {
        super();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns true if the ActionButton was clicked, otherwise returns false.
     *
     * @return true if the ActionButton was clicked, otherwise returns false.
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Return the ActionButton onclick attribute for the given value.
     * This method will encode the URL with the session ID if required using
     * <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @param value the ActionButton value parameter
     * @return the ActionButton JavaScript href attribute
     */
    public String getOnClick(Object value) {
        String uri = getContext().getRequest().getRequestURI();

        StringBuffer buffer =
            new StringBuffer(uri.length() + getName().length() + 40);

        buffer.append(uri);
        buffer.append("?");
        buffer.append(ACTION_BUTTON);
        buffer.append("=");
        buffer.append(getName());
        if (value != null) {
            buffer.append("&");
            buffer.append(VALUE);
            buffer.append("=");
            buffer.append(ClickUtils.encodeUrl(value, getContext()));
        }

        return "javascript:document.location.href='"
               + getContext().getResponse().encodeURL(buffer.toString())
               + "'";
    }

    /**
     * Return the ActionButton anchor &lt;a&gt; tag href attribute value.
     *
     * @return the ActionButton anchor &lt;a&gt; tag HTML href attribute value
     */
    public String getOnClick() {
        return getOnClick(getValueObject());
    }

    /**
     * Returns the ActionButton <tt>Double</tt> value if the action button was
     * processed and has a value, or null otherwise.
     *
     * @return the action button <tt>Double</tt> value if the action button was processed
     */
    public Double getValueDouble() {
        if (getValue() != null) {
            return Double.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionButton <tt>Integer</tt> value if the action button was
     * processed and has a value, or null otherwise.
     *
     * @return the ActionButton <tt>Integer</tt> value if the action button was processed
     */
    public Integer getValueInteger() {
        if (getValue() != null) {
            return Integer.valueOf(getValue());
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionButton <tt>Long</tt> value if the action button was
     * processed and has a value, or null otherwise.
     *
     * @return the ActionButton <tt>Long</tt> value if the action button was processed
     */
    public Long getValueLong() {
        if (getValue() != null) {
            return Long.valueOf(getValue());
        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionButton was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        clicked =
            getName().equals(getContext().getRequestParameter(ACTION_BUTTON));

        if (clicked) {
            setValue(getContext().getRequestParameter(VALUE));

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);

            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    /**
     * Return a HTML rendered Button string. Note the button label is rendered
     * as the HTML "value" attribute.
     *
     * @see Object#toString()
     *
     * @return a HTML rendered Button string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(40);

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getLabel());
        buffer.appendAttribute("title", getTitle());

        String onClickAction = " \"onclick=\"" + getOnClick() + "\"";
        buffer.append(onClickAction);

        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();

        return buffer.toString();
    }
}
