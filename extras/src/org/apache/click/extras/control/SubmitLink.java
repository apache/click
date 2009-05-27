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
package org.apache.click.extras.control;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Form;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a Submit Link control, &nbsp; &lt;a href=""&gt;&lt;/a&gt;, that can
 * submit a {@link org.apache.click.control.Form Form}.
 * <p/>
 * SubmitLink can be added to a Form and it will submit the Form when clicked.
 * All SubmitLink parameters will be submitted to the server as hidden fields.
 * <p/>
 * <b>Please note:</b> SubmitLink uses a <tt>JavaScript</tt> function to submit
 * the Form. This JavaScript function also creates <tt>hidden fields</tt>
 * for each SubmitLink parameter, to ensure the link's parameters are
 * submitted. See {@link #getSubmitScript(java.lang.String)} for more details on
 * the JavaScript used to submit the Form.
 * <p/>
 * <b>Also note:</b> if SubmitLink is <tt>not</tt> added to a Form, it behaves
 * like an {@link org.apache.click.control.ActionLink ActionLink} control.
 * <p/>
 * Here is an example:
 * <pre class="prettyprint">
 * public class MyPage extends Page {
 *
 *     public void onInit() {
 *
 *         // Create a Form control
 *         Form form = new Form("form");
 *         addControl(form);
 *
 *         // Add a SubmitLink to the Form
 *         SubmitLink link = new SubmitLink("link");
 *         form.add(link);
 *     }
 * } </pre>
 *
 * <h3>Custom Popup Message</h3>
 *
 * The SubmitLink uses the <tt>"onclick"</tt> event handler to submit the Form.
 * <p/>
 * If you would like to customize the <tt>"onclick"</tt> event handler, for
 * example to show a confirmation popup message, you can retrieve the link's
 * submit script through the {@link #getSubmitScript(java.lang.String)} method.
 * <p/>
 * Here is an example of providing a confirmation popup message before submitting
 * the Form:
 *
 * <pre class="prettyprint">
 * public MyPage extends Page {
 *
 *     public void onInit() {
 *         Form form = new Form("form");
 *         SubmitLink link = new SubmitLink("link", "Delete");
 *         form.add(link);
 *
 *         // Get the submit script for the given form name
 *         String submitScript = submitLink.getSubmitScript(form.getName());
 *
 *         // Add a confirmation popup message
 *         scriptLink.setOnClick("var confirm = window.confirm('Are you sure?'); if (confirm) "
 *             + submitScript + " else return false;");
 *     }
 * } </pre>
 *
 * <h3>JavaScript and CSS Dependencies</h3>
 *
 * When SubmitLink is added to a Form it will include the following resources:
 * <ul>
 * <li>/click/extras-control.js</li>
 * </ul>
 *
 * @author Bob Schellink
 */
public class SubmitLink extends ActionLink {

    // -------------------------------------------------------------- Variables

    /** The SubmitLink parent Form. */
    private Form form;

    /**
     * A parameter prefix to differentiate the link parameters from
     * From parameters.
     */
    private String parameterPrefix;

    // ----------------------------------------------------------- Constructors

    /**
     * Create an SubmitLink for the given name.
     * <p/>
     * Please note the name 'actionLink' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @param name the action link name
     * @throws IllegalArgumentException if the name is null
     */
    public SubmitLink(String name) {
        setName(name);
    }

    /**
     * Create an SubmitLink for the given name and label.
     * <p/>
     * Please note the name 'actionLink' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @param name the action link name
     * @param label the action link label
     * @throws IllegalArgumentException if the name is null
     */
    public SubmitLink(String name, String label) {
        setName(name);
        setLabel(label);
    }

    /**
     * Create an SubmitLink for the given listener object and listener
     * method.
     *
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public SubmitLink(Object listener, String method) {
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("Blank listener method");
        }
        setListener(listener, method);
    }

    /**
     * Create an SubmitLink for the given name, listener object and listener
     * method.
     * <p/>
     * Please note the name 'actionLink' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @param name the action link name
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public SubmitLink(String name, Object listener, String method) {
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
     * Create an SubmitLink for the given name, label, listener object and
     * listener method.
     * <p/>
     * Please note the name 'actionLink' is reserved as a control request
     * parameter name and cannot be used as the name of the control.
     *
     * @param name the action link name
     * @param label the action link label
     * @param listener the listener target object
     * @param method the listener method to call
     * @throws IllegalArgumentException if the name, listener or method is null
     * or if the method is blank
     */
    public SubmitLink(String name, String label, Object listener,
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
     * Create an SubmitLink with no name defined. <b>Please note</b> the
     * control's name must be defined before it is valid.
     */
    public SubmitLink() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the parent Form or null if no Form is available.
     *
     * @return the parent Form or null if no Form is available
     */
    public Form getForm() {
        if (form == null) {
            form = ContainerUtils.findForm(this);
        }
        return form;
    }

    /**
     * Set the SubmitLink Form.
     *
     * @param form the SubmitLink Form
     */
    public void setForm(Form form) {
        this.form = form;
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
     * @see org.apache.click.control.AbstractLink#getParameter(java.lang.String)
     *
     * @param name the name of request parameter
     * @return the link request parameter value
     */
    public String getParameter(String name) {
        if (hasParameters()) {
            Object value = getParameters().get(name);

            if (value instanceof String) {
                return (String) value;
            }

            if (value instanceof String[]) {
                String[] array = (String[]) value;
                if (array.length >= 1) {
                    return array[0];
                } else {
                    return null;
                }
            }

            return (value == null ? null : value.toString());
        } else {
            return null;
        }
    }

    /**
     * Return the link request parameter values for the given name, or null if
     * the parameter values does not exist.
     *
     * @param name the name of request parameter
     * @return the link request parameter values
     */
    public String[] getParameterValues(String name) {
        if (hasParameters()) {
            Object values = getParameters().get(name);
            if (values instanceof String) {
                return new String[] { values.toString() };
            }
            if (values instanceof String[]) {
                return (String[]) values;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Set the link parameter with the given parameter name and values. If the
     * values are null, the parameter will be removed from the {@link #parameters}.
     *
     * @see org.apache.click.control.AbstractLink#setParameter(java.lang.String, java.lang.String)
     *
     * @param name the attribute name
     * @param values the attribute values
     * @throws IllegalArgumentException if name parameter is null
     */
    public void setParameterValues(String name, String[] values) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (values != null) {
            getParameters().put(name, values);
        } else {
            getParameters().remove(name);
        }
    }

    /**
     * Set the paramter prefix that is applied to the SubmitLink parameters.
     *
     * @param prefix the parameter prefix
     */
    public void setParameterPrefix(String prefix) {
        this.parameterPrefix = prefix;
    }

    /**
     * Return the paramter prefix that is applied to the SubmitLink parameters.
     *
     * @return the paramter prefix that is applied to the SubmitLink parameters.
     */
    public String getParameterPrefix() {
        if (!hasParentForm()) {
            return "";
        }
        if (parameterPrefix == null) {
            parameterPrefix = getName() + '_';
        }
        return parameterPrefix;
    }

    /**
     * Return true if SubmitLink has a parent Form control, false otherwise.
     *
     * @return true if SubmitLink has a parent Form control, false otherwise.
     */
    public boolean hasParentForm() {
        return getForm() != null;
    }

    /**
     * Return the JavaScript that submits the Form with the given formName.
     * <p/>
     * The script returned by this method is:
     *
     * <pre class="prettyprint">
     * "return Click.submitLinkAction(this, 'formName');" </pre>
     *
     * The <tt>Click.submitLinkAction</tt> function takes as parameters a reference
     * to the SubmitLink and the name of the Form to submit. (The
     * Click.submitLinkAction is defined in <tt>/click/extras-control.js</tt>)
     *
     * @param formName the name of the Form to submit
     *
     * @return the JavaScript that submits the Form with the given formName
     *
     * @throws IllegalStateException if the form name is null
     */
    public String getSubmitScript(String formName) {
        if (formName == null) {
            throw new IllegalStateException("formName cannot be null.");
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(60);
        buffer.append("return Click.submitLinkAction(this, '");
        buffer.append(formName);
        buffer.append("');");
        return buffer.toString();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the SubmitLink anchor &lt;a&gt; tag href attribute for the
     * given value. This method will encode the URL with the session ID
     * if required using <tt>HttpServletResponse.encodeURL()</tt>.
     *
     * @param value the SubmitLink value parameter
     * @return the SubmitLink HTML href attribute
     */
    public String getHref(Object value) {
        Context context = getContext();
        String uri = ClickUtils.getRequestURI(context.getRequest());

        HtmlStringBuffer buffer =
                new HtmlStringBuffer(uri.length() + getName().length() + 40);

        String prefix = getParameterPrefix();

        buffer.append(uri);
        buffer.append("?");
        buffer.append(ACTION_LINK);
        buffer.append("=");
        buffer.append(getName());
        if (value != null) {
            buffer.append("&amp;");
            if (hasParentForm()) {
                // Value parameter is prefixed when SubmitLink is included
                // inside a Form
                buffer.append(prefix);
            }
            buffer.append(VALUE);
            buffer.append("=");
            buffer.append(ClickUtils.encodeUrl(value, context));
        }

        if (hasParameters()) {
            Iterator i = getParameters().keySet().iterator();
            while (i.hasNext()) {
                String name = i.next().toString();
                if (!name.equals(ACTION_LINK) && !name.equals(VALUE)) {
                    Object paramValue = getParameters().get(name);
                    if (paramValue instanceof String[]) {
                        String[] paramValues = (String[]) paramValue;
                        for (int j = 0; j < paramValues.length; j++) {
                            renderParameter(name, paramValues[j], buffer, context);
                        }
                    } else {
                        renderParameter(name, paramValue, buffer, context);
                    }
                }
            }
        }

        return context.getResponse().encodeURL(buffer.toString());
    }

    /**
     * This method binds the submitted request value to the SubmitLink's
     * value.
     */
    public void bindRequestValue() {
        Context context = getContext();

        clicked = getName().equals(context.getRequestParameter(ACTION_LINK));

        if (clicked) {
            // SubmitLink parameters are prefixed when included inside a Form
            String prefix = getParameterPrefix();

            HttpServletRequest request = context.getRequest();
            Enumeration paramNames = request.getParameterNames();

            boolean hasParentForm = hasParentForm();

            while (paramNames.hasMoreElements()) {
                String param = paramNames.nextElement().toString();
                String[] values = request.getParameterValues(param);

                if (hasParentForm) {
                    // Only bind parameters that start with the prefix
                    if (param.startsWith(prefix)) {

                        // Remove prefix from parameters
                        String key = param.substring(prefix.length());
                        if (values != null && values.length == 1) {
                            getParameters().put(key, values[0]);
                        } else {
                            getParameters().put(key, values);
                        }
                    }

                } else {
                    if (values != null && values.length == 1) {
                        getParameters().put(param, values[0]);
                    } else {
                        getParameters().put(param, values);
                    }
                }
            }
        }
    }

    /**
     * Return the list of HEAD {@link org.apache.click.element.Element elements}
     * to be included in the page.
     * <p/>
     * The list of resources returned are:
     * <ul>
     * <li>the template "/click/extras-control.js"</li>
     * </ul>
     *
     * @return the list of HEAD elements to be included in the page
     */
    public List getHeadElements() {
        if (getForm() == null) {
            return super.getHeadElements();
        }

        if (headElements == null) {
            headElements = super.getHeadElements();

            String versionIndicator = ClickUtils.getResourceVersionIndicator(getContext());

            JsImport jsImport = new JsImport("/click/extras-control.js",
                versionIndicator);
            headElements.add(jsImport);
        }
        return headElements;
    }

    /**
     * Render the HTML representation of the SubmitLink.
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {

        // Check that the link is attached to a Form and the onClick attribute
        // has not been set
        Form form = getForm();
        if (form != null && getOnClick() == null) {
            setOnClick(getSubmitScript(form.getName()));
        }

        super.render(buffer);
    }

    /**
     * Deploy the <tt>extras-control.js</tt> file to the <tt>click</tt> web
     * directory when the application is initialized.
     *
     * @see org.apache.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext,
                              "/org/apache/click/extras/control/extras-control.js",
                              "click");
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Render the specified name and value pair to the buffer.
     *
     * @param name the parameter name
     * @param value the parameter value
     * @param buffer the buffer to render the parameter to
     * @param context the request context
     */
    private void renderParameter(String name, Object value,
        HtmlStringBuffer buffer, Context context) {

        String encodedValue = ClickUtils.encodeUrl(value, context);
        buffer.append("&amp;");
        if (hasParentForm()) {
            // Parameters are prefixed when SubmitLink is included
            // inside a Form
            buffer.append(getParameterPrefix());
        }
        buffer.append(name);
        buffer.append("=");
        buffer.append(encodedValue);
    }
}
