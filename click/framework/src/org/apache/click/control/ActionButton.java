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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.click.Context;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

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
 * {@link org.apache.click.control.ActionLink} control, where you can attach
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
 * the Form will not invoke the ActionButton's <tt>onProcess()</tt> method.
 *
 * @see org.apache.click.control.ActionLink
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

    /** The button parameters map. */
    protected Map<String, Object> parameters;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an ActionButton for the given name.
     * <p/>
     * Please note the name 'actionButton' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @param name the action button name
     * @throws IllegalArgumentException if the name is null
     */
    public ActionButton(String name) {
        super(name);
    }

    /**
     * Create an ActionButton for the given name and label.
     * <p/>
     * Please note the name 'actionButton' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
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
     * <p/>
     * Please note the name 'actionButton' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
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
     * <p/>
     * Please note the name 'actionButton' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
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

    // --------------------------------------------------------- Public Attributes

    /**
     * Returns true if the ActionButton was clicked, otherwise returns false.
     *
     * @return true if the ActionButton was clicked, otherwise returns false.
     */
    public boolean isClicked() {
        return clicked;
    }

    /**
     * Set the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     * <p/>
     * Please note the name 'actionButton' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @see org.apache.click.Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    @Override
    public void setName(String name) {
        if (ACTION_BUTTON.equals(name)) {
            String msg = "Invalid name '" + ACTION_BUTTON + "'. This name is "
                + "reserved for use as a control request parameter name";
            throw new IllegalArgumentException(msg);
        }
        super.setName(name);
    }

    /**
     * Set the parent of the ActionButton.
     *
     * @see org.apache.click.Control#setParent(Object)
     *
     * @param parent the parent of the Control
     * @throws IllegalStateException if {@link #name} is not defined
     * @throws IllegalArgumentException if the given parent instance is
     * referencing <tt>this</tt> object: <tt>if (parent == this)</tt>
     */
    @Override
    public void setParent(Object parent) {
        if (parent == this) {
            throw new IllegalArgumentException("Cannot set parent to itself");
        }
        if (getName() == null) {
            String msg = "ActionButton name not defined.";
            throw new IllegalArgumentException(msg);
        }
        this.parent = parent;
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
        Context context = getContext();
        String uri = ClickUtils.getRequestURI(context.getRequest());

        HtmlStringBuffer buffer =
            new HtmlStringBuffer(uri.length() + getName().length() + 40);

        buffer.append(uri);
        buffer.append("?");
        buffer.append(ACTION_BUTTON);
        buffer.append("=");
        buffer.append(getName());

        if (value != null) {
            buffer.append("&amp;");
            buffer.append(VALUE);
            buffer.append("=");
            buffer.append(ClickUtils.encodeUrl(value, context));
        }

        if (hasParameters()) {
            for (String name : getParameters().keySet()) {
                if (!name.equals(ACTION_BUTTON) && !name.equals(VALUE)) {
                    Object paramValue = getParameters().get(name);
                    String encodedValue
                        = ClickUtils.encodeUrl(paramValue, context);
                    buffer.append("&amp;");
                    buffer.append(name);
                    buffer.append("=");
                    buffer.append(encodedValue);
                }
            }
        }

        return "javascript:document.location.href='"
               + context.getResponse().encodeURL(buffer.toString())
               + "';";
    }

    /**
     * Return the ActionButton anchor &lt;a&gt; tag href attribute value.
     *
     * @return the ActionButton anchor &lt;a&gt; tag HTML href attribute value
     */
    @Override
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
            Object value = getParameters().get(name);
            return (value == null ? null : value.toString());
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
    public Map<String, Object> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<String, Object>(4);
        }
        return parameters;
    }

    /**
     * Set the ActionButton parameter map.
     *
     * @param parameters the button parameter map
     */
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    /**
     * Defines a button parameter that will have its value bound to a matching
     * request parameter. {@link #setParameter(java.lang.String, java.lang.String) setParameter}
     * implicitly defines a parameter as well.
     * <p/>
     * <b>Please note:</b> parameters need only be defined for Ajax requests.
     * For non-Ajax requests, <tt>all</tt> incoming request parameters
     * are bound, whether they are defined or not. This behavior may change in a
     * future release.
     * <p/>
     * <b>Also note:</b> button parameters are bound to request parameters
     * during the {@link #onProcess()} event, so button parameters must be defined
     * in the Page constructor or <tt>onInit()</tt> event.
     *
     * @param name the name of the parameter to define
     */
    public void defineParameter(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        Map<String, Object> localParameters = getParameters();
        if (!localParameters.containsKey(name)) {
            localParameters.put(name, null);
        }
    }

    /**
     * Return true if the ActionButton has parameters or false otherwise.
     *
     * @return true if the ActionButton has parameters on false otherwise
     */
    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    @Override
    public boolean isAjaxTarget(Context context) {
        String id = getId();
        if (id != null) {
            return context.getRequestParameter(id) != null;
        } else {
            String name = getName();
            if (name != null) {
                return name.equals(context.getRequestParameter(ActionButton.ACTION_BUTTON));
            }
        }
        return false;
    }

    /**
     * Returns the ActionButton value if the action link was processed and has
     * a value, or null otherwise.
     *
     * @return the ActionButton value if the ActionButton was processed
     */
    @Override
    public String getValue() {
        if (hasParameters()) {
            return getParameter(VALUE);
        } else {
            return null;
        }
    }

    /**
     * Returns the ActionButton <tt>Double</tt> value if the action button was
     * processed and has a value, or null otherwise.
     *
     * @return the action button <tt>Double</tt> value if the action button was processed
     *
     * @throws NumberFormatException if the value cannot be parsed into a Double
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
     *
     * @throws NumberFormatException if the value cannot be parsed into an Integer
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
     *
     * @throws NumberFormatException if the value cannot be parsed into a Long
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
    @Override
    public void setValue(String value) {
        getParameters().put(VALUE, value);
    }

    /**
     * Return the value of the ActionButton.
     *
     * @return the value of the ActionButton
     */
    @Override
    public Object getValueObject() {
        return getParameters().get(VALUE);
    }

    /**
     * Set the value of the field using the given object.
     *
     * @param object the object value to set
     */
    @Override
    public void setValueObject(Object object) {
        if (object != null) {
            setValue(object.toString());
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method binds the submitted request value to the ActionButton's
     * value.
     */
    @Override
    public void bindRequestValue() {
        Context context = getContext();
        if (context.isMultipartRequest()) {
            return;
        }

        clicked = getName().equals(context.getRequestParameter(ACTION_BUTTON));

        if (clicked) {
            String localValue = context.getRequestParameter(VALUE);
            if (localValue != null) {
                setValue(localValue);
            }
            bindRequestParameters(context);
        }
    }

    /**
     * This method will set the {@link #isClicked()} property to true if the
     * ActionButton was clicked, and if an action callback listener was set
     * this will be invoked.
     *
     * @see org.apache.click.control.Field#onProcess()
     *
     * @return true to continue Page event processing or false otherwise
     */
    @Override
    public boolean onProcess() {
        if (isDisabled()) {
            Context context = getContext();

            // Switch off disabled property if control has incoming request
            // parameter. Normally this means the field was enabled via JS
            if (context.hasRequestParameter(getName())) {
                setDisabled(false);
            } else {
                // If field is disabled skip process event
                return true;
            }
        }

        bindRequestValue();

        if (isClicked()) {
            dispatchActionEvent();
        }
        return true;
    }

    /**
     * Render the HTML representation of the ActionButton. Note the button label
     * is rendered as the HTML "value" attribute.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
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

        String onClickAction = " onclick=\"" + getOnClick() + "\"";
        buffer.append(onClickAction);

        appendAttributes(buffer);

        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }

        buffer.elementEnd();
    }

    // Protected Methods ------------------------------------------------------

    /**
     * This method binds the submitted request parameters to the buttons
     * parameters.
     * <p/>
     * For non-Ajax requests this method will bind <tt>all</tt> incoming request
     * parameters to the link. For Ajax requests this method will only bind
     * the parameters already defined on the link.
     *
     * @param context the request context
     */
    @SuppressWarnings("unchecked")
    protected void bindRequestParameters(Context context) {
        HttpServletRequest request = context.getRequest();

        Set<String> parameterNames = null;

        if (context.isAjaxRequest()) {
            parameterNames = getParameters().keySet();
        } else {
            parameterNames = request.getParameterMap().keySet();
        }

        for (String param : parameterNames) {
            String[] values = request.getParameterValues(param);
            // Do not process request parameters that return null values. Null
            // values are only returned if the request parameter is not present.
            // A null value can only occur for Ajax requests which processes
            // parameters defined on the button, not the incoming parameters.
            // The reason for not processing the null value is because it would
            // nullify the parameter that was set during onInit
            if (values == null) {
                continue;
            }

            if (values.length == 1) {
                getParameters().put(param, values[0]);
            } else {
                getParameters().put(param, values);
            }
        }
    }
}
