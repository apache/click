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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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
 *
 * <h3>ActionButton Example</h3>
 *
 * Example usage of the ActionButton:
 *
 * <pre class="codeJava">
 * ActionButton actionButton = <span class="kw">new</span> ActionButton(<span class="st">"button"</span>);
 * actionButton.setListener(<span class="kw">this</span>, <span class="st">"onButtonClick"</span>);
 * addControl(actionButton); </pre>
 *
 * <b>Please Note</b> do not add ActionButton instances to the Form object, as
 * the GET request it generates will never be processed by the Form, and in turn
 * the Form will invoke the ActionButton's <tt>onProcess()</tt> method.
 *
 * @see net.sf.click.control.ActionLink
 *
 * @author Malcolm Edgar
 */
public class ActionButton extends Button {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The action button parameter name: &nbsp; <tt>actionButton</tt>. */
    public static final String ACTION_BUTTON = "actionButton";

    /** The value parameter name: &nbsp; <tt>value</tt>. */
    public static final String VALUE = "value";

    // ----------------------------------------------------- Instance Variables

    /** The button is clicked. */
    protected boolean clicked;


    /** The link parameters map. */
    protected Map parameters;

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
     * Create an ActionButton for the given listener object and listener method.
     *
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public ActionButton(Object listener, String method) {
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
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
     * Create an ActionButton with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
        if (getContext() == null) {
            throw new IllegalStateException("context is not defined");
        }

        String uri = getContext().getRequest().getRequestURI();

        HtmlStringBuffer buffer =
            new HtmlStringBuffer(uri.length() + getName().length() + 40);

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

        if (hasParameters()) {
            Iterator i = getParameters().keySet().iterator();
            while (i.hasNext()) {
                String name = i.next().toString();
                if (!name.equals(ACTION_BUTTON) && !name.equals(VALUE)) {
                    Object paramValue = getParameters().get(name);
                    String encodedValue
                        = ClickUtils.encodeUrl(paramValue, getContext());
                    buffer.append("&");
                    buffer.append(name);
                    buffer.append("=");
                    buffer.append(encodedValue);
                }
            }
        }

        return "javascript:document.location.href='"
               + getContext().getResponse().encodeURL(buffer.toString())
               + "';";
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
     * Return the button request parameter value for the given name, or null if
     * the parameter value does not exist.
     *
     * @param name the name of request parameter
     * @return the button request parameter value
     */
    public String getParameter(String name) {
        if (hasParameters()) {
            return (String) getParameters().get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the button parameter with the given parameter name and value.
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setParameter(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (value != null) {
            getParameters().put(name, value);
        } else {
            getParameters().remove(name);
        }
    }

    /**
     * Return the ActionButton parameters Map.
     *
     * @return the ActionButton parameters Map
     */
    public Map getParameters() {
        if (parameters == null) {
            parameters = new HashMap(4);
        }
        return parameters;
    }

    /**
     * Return true if the ActionButton has parameters or false otherwise.
     *
     * @return true if the ActionButton has parameters on false otherwise
     */
    public boolean hasParameters() {
        if (parameters != null && !parameters.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the ActionButton value if the action link was processed and has
     * a value, or null otherwise.
     *
     * @return the ActionButton value if the ActionButton was processed
     */
    public String getValue() {
        if (hasParameters()) {
            return (String) getParameters().get(VALUE);
        } else {
            return null;
        }
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

    /**
     * Set the ActionButton value.
     *
     * @param value the ActionButton value
     */
    public void setValue(String value) {
        getParameters().put(VALUE, value);
    }

    /**
     * Return the value of the ActionButton.
     *
     * @return the value of the ActionButton
     */
    public Object getValueObject() {
        return getParameters().get(VALUE);
    }

    /**
     * Set the value of the field using the given object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            setValue(object.toString());
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
        if (getContext() == null) {
            throw new IllegalStateException("context is not defined");
        }

        clicked =
            getName().equals(getContext().getRequestParameter(ACTION_BUTTON));

        if (clicked) {
            HttpServletRequest request = getContext().getRequest();

            Enumeration paramNames = request.getParameterNames();

            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement().toString();
                String value = request.getParameter(name);
                getParameters().put(name, value);
            }

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

        String onClickAction = " onclick=\"" + getOnClick() + "\"";
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
