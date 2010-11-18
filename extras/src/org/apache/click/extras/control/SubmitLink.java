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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Form;
import org.apache.click.element.Element;
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
 *         // Get the submit script for the given form id
 *         String submitScript = submitLink.getSubmitScript(form.getId());
 *
 *         // Add a confirmation popup message
 *         scriptLink.setOnClick("var confirm = window.confirm('Are you sure?'); if (confirm) "
 *             + submitScript + " else return false;");
 *     }
 * } </pre>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * When SubmitLink is added to a Form it makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li>/click/extras-control.js</li>
 * </ul>
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
            return attributes.get("onclick");
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
     * Set the parameter prefix that is applied to the SubmitLink parameters.
     *
     * @param prefix the parameter prefix
     */
    public void setParameterPrefix(String prefix) {
        this.parameterPrefix = prefix;
    }

    /**
     * Return the parameter prefix that is applied to the SubmitLink parameters.
     *
     * @return the parameter prefix that is applied to the SubmitLink parameters.
     */
    public String getParameterPrefix() {
        if (!hasParentForm()) {
            return "";
        }
        if (parameterPrefix == null) {
            parameterPrefix = "_";
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
     * Return the JavaScript that submits the Form with the given formId.
     * <p/>
     * The script returned by this method is:
     *
     * <pre class="prettyprint">
     * "return Click.submitLinkAction(this, 'formId');" </pre>
     *
     * The <tt>Click.submitLinkAction</tt> function takes as parameters a reference
     * to the SubmitLink and the id of the Form to submit. (The
     * Click.submitLinkAction is defined in <tt>/click/extras-control.js</tt>)
     *
     * @param formId the id of the Form to submit
     *
     * @return the JavaScript that submits the Form with the given formId
     *
     * @throws IllegalStateException if the form id is null
     */
    public String getSubmitScript(String formId) {
        if (formId == null) {
            throw new IllegalStateException("formId cannot be null.");
        }

        HtmlStringBuffer buffer = new HtmlStringBuffer(60);
        buffer.append("return");
        if (getForm().isJavaScriptValidation()) {
            buffer.append(" on_");
            buffer.append(getForm().getId());
            buffer.append("_submit() &&");
        }
        buffer.append(" Click.submitLinkAction(this, '");
        buffer.append(formId);
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
    @Override
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
            if (StringUtils.isNotBlank(prefix)) {
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
                String localName = i.next().toString();
                if (!localName.equals(ACTION_LINK) && !localName.equals(VALUE)) {
                    Object paramValue = getParameters().get(localName);
                    if (paramValue instanceof String[]) {
                        String[] paramValues = (String[]) paramValue;
                        for (int j = 0; j < paramValues.length; j++) {
                            renderParameter(localName, paramValues[j], prefix,
                                buffer, context);
                        }
                    } else {
                        renderParameter(localName, paramValue, prefix, buffer,
                            context);
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
    @Override
    public void bindRequestValue() {
        Context context = getContext();

        clicked = getName().equals(context.getRequestParameter(ACTION_LINK));

        if (clicked) {
            // SubmitLink parameters are prefixed when included inside a Form
            String prefix = getParameterPrefix();

            HttpServletRequest request = context.getRequest();
            Set<String> parameterNames = request.getParameterMap().keySet();

            boolean hasParentForm = hasParentForm();

            for (String param : parameterNames) {
                String[] values = request.getParameterValues(param);
                if (values == null) {
                    continue;
                }

                if (hasParentForm) {
                    // Only bind parameters that start with the prefix
                    if (param.startsWith(prefix)) {

                        // Remove prefix from parameters
                        String key = param.substring(prefix.length());
                        if (values.length == 1) {
                            getParameters().put(key, values[0]);
                        } else {
                            getParameters().put(key, values);
                        }
                    }

                } else {
                    if (values.length == 1) {
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
     * to be included in the page. The list of resources are:
     * <p/>
     * <ul>
     * <li>click/extras-control.js</li>
     * </ul>
     *
     * @return the list of HEAD elements to be included in the page
     */
    @Override
    public List<Element> getHeadElements() {
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
    @Override
    public void render(HtmlStringBuffer buffer) {

        // Check that the link is attached to a Form and the onClick attribute
        // has not been set
        Form localForm = getForm();
        if (localForm != null && getOnClick() == null) {
            setOnClick(getSubmitScript(localForm.getId()));
        }

        super.render(buffer);
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Render the specified name and value pair to the buffer.
     *
     * @param name the parameter name
     * @param value the parameter value
     * @param prefix the parameter prefix value
     * @param buffer the buffer to render the parameter to
     * @param context the request context
     */
    private void renderParameter(String name, Object value, String prefix,
        HtmlStringBuffer buffer, Context context) {

        // Don't render null values
        if (value == null) {
            return;
        }

        String encodedValue = ClickUtils.encodeUrl(value, context);
        buffer.append("&amp;");
        if (StringUtils.isNotBlank(prefix)) {
            // Parameters are prefixed when SubmitLink is included
            // inside a Form
            buffer.append(prefix);
        }
        buffer.append(name);
        buffer.append("=");
        buffer.append(encodedValue);
    }
}
