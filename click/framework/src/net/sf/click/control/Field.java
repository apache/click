/*
 * Copyright 2004-2007 Malcolm A. Edgar
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

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides an abstract form Field control. Field controls are contained by
 * the {@link Form} control which will orchestrate the processing and
 * rendering of the contained fields. All Form field controls must extend this
 * abstract class.
 *
 * <h3>Field Processing</h3>
 *
 * <h4>Post Requests</h4>
 *
 * When processing POST requests forms typically invoke the {@link #onProcess()}
 * method on all its fields. The Field <tt>onProcess()</tt> method is used
 * to bind the fields request value, validate the submission and invoke any
 * control listener method. If the <tt>onProcess()</tt> method returns true
 * the form will continue processing fields, otherwise the form will abort
 * further processing.
 * <p/>
 * The body of the Field <tt>onProcess()</tt> method is detailed below.
 *
 * <pre class="codeJava">
 * <span class="kw">public boolean</span> onProcess() {
 *     bindRequestValue();
 *
 *     <span class="kw">if</span> (getValidate()) {
 *         validate();
 *     }
 *
 *     <span class="kw">return</span> invokeListener();
 * } </pre>
 *
 * The Field methods called by <tt>onProcess()</tt> include:
 *
 * <dl>
 * <dt>{@link #bindRequestValue()}</dt>
 * <dd>This method will bind the HTTP request value to the Field's value.
 * </dd>
 * <dt>{@link #getValidate()}</dt>
 * <dd>This method will return true if the Field should validate itself. This
 * value is generally inherited from the parent Form, however the Field can
 * override this value and specify whether it should be validated.
 * </dd>
 * <dt>{@link #validate()}</dt>
 * <dd>This method will validate the submitted Field value. If the submitted
 * value is not valid this method should set the Field {@link #error} property,
 * which can be rendered by the Form.
 * </dd>
 * <dt>{@link #invokeListener()}</dt>
 * <dd>This method will invoke any Control listener method which has be defined
 * for the Field. If no listener is defined this method will return null.
 * </dd>
 * </dl>
 *
 * Field subclasses generally only have to override the <tt>validate()</tt>
 * method, and possibly the <tt>bindRequestValue()</tt> method, to provide their
 * own behaviour.
 *
 * <h4>Get Requests</h4>
 *
 * When processing GET requests a Page's Form will typically perform no
 * processing and simply render itself and its Fields.
 *
 * <h3>Rendering</h3>
 *
 * Field subclasses must override the <tt>Object.toString()</tt> method to
 * enable themselves to be rendered as HTML. With the increasing use of AJAX
 * Field should render themselves as valid XHTML, so that they may be parsed
 * correctly and used as the <tt>innerHtml</tt> in the DOM.
 * <p/>
 * When a Form object renders a Field using autolayout, it renders the
 * Field in a table row using the Field's {@link #label} attribute, its
 * {@link #error} attribute if defined, and the Fields <tt>toString()</tt>
 * method.
 * <p/>
 * To assist with rendering valid HTML Field subclasses can use the
 * {@link net.sf.click.util.HtmlStringBuffer} class.
 *
 * <h3>Message Resources</h3>
 *
 * Fields support a hierarchy of resource bundles for displaying validation
 * error messages and display messages. These localized messages can be accessed
 * through the methods:
 *
 * <ul>
 * <li>{@link #getMessage(String)}</li>
 * <li>{@link #getMessage(String, Object)}</li>
 * <li>{@link #getMessage(String, Object[])}</li>
 * <li>{@link #getMessages()}</li>
 * <li>{@link #setErrorMessage(String)}</li>
 * <li>{@link #setErrorMessage(String, Object)}</li>
 * </ul>
 *
 * The order in which localized messages are resolve is:
 * <dl>
 * <dt style="font-weight:bold">Page scope messages</dt>
 * <dd>Message lookups are first resolved to the Pages message bundle if it
 * exists. For example a <tt>Login</tt> page may define the message properties:
 *
 * <pre class="codeConfig">
 * /com/mycorp/page/Login.properties </pre>
 *
 * If you want messages to be used across your entire application this is where
 * to place them.
 * </dd>
 *
 * <dt style="font-weight:bold;margin-top:1em;">Global page scope messages</dt>
 * <dd>Next message lookups are resolved to the global pages message bundle if it
 * exists.
 *
 * <pre class="codeConfig">
 * /click-page.properties </pre>
 *
 * If you want messages to be used across your entire application this is where
 * to place them.
 * </dd>
 *
 * <dt style="font-weight:bold">Control scope messages</dt>
 * <dd>Next message lookups are resolved to the Control message bundle if it
 * exists. For example a <tt>CustomTextField</tt> control may define the
 * message properties:
 *
 * <pre class="codeConfig">
 * /com/mycorp/control/CustomTextField.properties </pre>
 * </dd>
 *
 * <dt style="font-weight:bold">Global control scope messages</dt>
 * <dd>Finally message lookups are resolved to the global application control
 * message bundle if the message has not already found. The global control
 * properties file is:
 *
 * <pre class="codeConfig">
 * /click-control.properties </pre>
 *
 * You can modify these properties by copying this file into your applications
 * root class path and editing these properties.
 * <p/>
 * Note when customizing the message properties you must include all the
 * properties, not just the ones you want to override.
 * </dd>
 * </dl>
 *
 * @author Malcolm Edgar
 */
public abstract class Field extends AbstractControl {

    // ----------------------------------------------------- Instance Variables

    /** The Field disabled value. */
    protected boolean disabled;

    /** The Field error message. */
    protected String error;

    /** The request focus flag. */
    protected boolean focus;

    /** The parent Form. */
    protected Form form;

    /** The Field help text. */
    protected String help;

    /** The Field label. */
    protected String label;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /** The Field is readonly flag. */
    protected boolean readonly;

    /** The Field is required flag. */
    protected boolean required;

    /** The Field 'tabindex' attribute. */
    protected int tabindex;

    /** The Field 'title' attribute, which acts as a tooltip help message. */
    protected String title;

    /**
     * The validate Field value <tt>onProcess()</tt> invokation flag.
     */
    protected Boolean validate;

    /** The Field value. */
    protected String value;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new Field object.
     */
    public Field() {
    }

    /**
     * Construct the Field with the given name.
     *
     * @param name the name of the Field
     */
    public Field(String name) {
        setName(name);
    }

    /**
     * Construct the Field with the given name and label.
     *
     * @param name the name of the Field
     * @param label the label of the Field
     */
    public Field(String name, String label) {
        setName(name);
        setLabel(label);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return true if the Field is a disabled. The Field will also be disabled
     * if the parent Form is disabled.
     * <p/>
     * <b>Important Note</b>: disabled fields will not submit their values in
     * a HTML form POST. This may cause validation issues in a form submission.
     * Please note this is a HTML limitation and is not due to Click.
     *
     * @return true if the Field is a disabled
     */
    public boolean isDisabled() {
        if (getForm() != null && getForm().isDisabled()) {
            return true;
        } else {
            return disabled;
        }
    }

    /**
     * Set the Field disabled flag.
     * <p/>
     * <b>Important Note</b>: disabled fields will not submit their values in
     * a HTML form POST. This may cause validation issues in a form submission.
     * Please note this is a HTML limitation and is not due to Click.
     *
     * @param disabled the Field disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the validation error message if the Field is not valid, or null
     * if valid.
     *
     * @return the Field validation error message, or null if valid
     */
    public String getError() {
        return error;
    }

    /**
     * Set the Field validation error message. If the error message is not null
     * the Field is invalid, otherwise it is valid.
     *
     * @param error the validation error message
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Return true if the field has requested focus.
     *
     * @return true if the field has requested focus
     */
    public boolean getFocus() {
        return focus;
    }

    /**
     * Set the Field request focus flag.
     *
     * @param focus the request focus flag
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * Return the Field focus JavaScript.
     *
     * @return the Field focus JavaScript
     */
    public String getFocusJavaScript() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(32);

        buffer.append("setFocus('");
        buffer.append(getId());
        buffer.append("');");

        return buffer.toString();
    }

    /**
     * Return the parent Form containing the Field.
     *
     * @return the parent Form containing the Field
     */
    public Form getForm() {
        return form;
    }

    /**
     * Set the Field's the parent <tt>Form</tt>.
     *
     * @param form Field's parent <tt>Form</tt>
     */
    public void setForm(Form form) {
        this.form = form;
    }

    /**
     * Return the field help text.
     * <p/>
     * If the help value is null, this method will attempt to find a
     * localized help message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".help"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * <p/>
     * For examle given a <tt>CustomerPage</tt> with the properties file
     * <tt>CustomerPage.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">name</span>.label=<span class="red">Customer Name</span>
     * <span class="st">name</span>.help=<span class="red">Full name or Business name</span> </pre>
     *
     * The page TextField code:
     * <pre class="codeJava">
     * <span class="kw">public class</span> CustomerPage <span class="kw">extends</span> Page {
     *     TextField nameField = <span class="kw">new</span> TextField(<span class="st">"name"</span>);
     *     ..
     * } </pre>
     *
     * Will render the TextField label and title properties as:
     * <pre class="codeHtml">
     * &lt;td&gt;&lt;label&gt;<span class="red">Customer Name</span>&lt;/label&gt;&lt;/td&gt;
     * &lt;td&gt;&lt;input type="text" name="<span class="st">name</span>"/&gt; &lt;span="<span class="red">Full name or Business name</span>"/&gt;/td&gt; </pre>
     *
     * How the help text is rendered is depends upon the Field subclass.
     *
     * @return the help text of the Field
     */
    public String getHelp() {
        if (help == null) {
            help = getMessage(getName() + ".help");

            if (help != null) {
                if (help.indexOf("$context") != -1) {
                    help = StringUtils.replace(help, "$context", getContext().getRequest().getContextPath());

                } else if (help.indexOf("${context}") != -1) {
                    help = StringUtils.replace(help, "${context}", getContext().getRequest().getContextPath());
                }
            }
        }
        return help;
    }

    /**
     * Set the Field help text.
     *
     * @param help the help text of the Field
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * Return the HTML head element import string. This method returns null.
     * <p/>
     * Override this method to specify JavaScript and CSS includes for the
     * HTML head element. This value will rendered by the Form
     * {@link Form#getHtmlImports()} method.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return null value
     */
    public String getHtmlImports() {
        return null;
    }

    /**
     * Return true if the Field type is hidden (&lt;input type="hidden"/&gt;) or
     * false otherwise. By default this method returns false.
     *
     * @return false
     */
    public boolean isHidden() {
        return false;
    }
 
    /**
     * Return the Form and Field id appended: &nbsp; "<tt>form-field</tt>"
     * <p/>
     * Use the field the "id" attribute value if defined, or the name otherwise.
     *
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");

        } else {
            String formId = (getForm() != null) ? getForm().getId() + "_" : "";

            String id = formId + getName();

            if (id.indexOf('/') != -1) {
                id = id.replace('/', '_');
            }
            if (id.indexOf(' ') != -1) {
                id = id.replace(' ', '_');
            }
            if (id.indexOf('<') != -1) {
                id = id.replace('<', '_');
            }
            if (id.indexOf('>') != -1) {
                id = id.replace('>', '_');
            }

            return id;
        }
    }

    /**
     * Return the field display label.
     * <p/>
     * If the label value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".label"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key.
     * If a value still cannot be found then the Field name will be converted
     * into a label using the method: {@link ClickUtils#toLabel(String)}
     * <p/>
     * For examle given a <tt>CustomerPage</tt> with the properties file
     * <tt>CustomerPage.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">name</span>.label=<span class="red">Customer Name</span>
     * <span class="st">name</span>.title=<span class="red">Full name or Business name</span> </pre>
     *
     * The page TextField code:
     * <pre class="codeJava">
     * <span class="kw">public class</span> CustomerPage <span class="kw">extends</span> Page {
     *     TextField nameField = <span class="kw">new</span> TextField(<span class="st">"name"</span>);
     *     ..
     * } </pre>
     *
     * Will render the TextField label and title properties as:
     * <pre class="codeHtml">
     * &lt;td&gt;&lt;label&gt;<span class="red">Customer Name</span>&lt;/label&gt;&lt;/td&gt;
     * &lt;td&gt;&lt;input type="text" name="<span class="st">name</span>" title="<span class="red">Full name or Business name</span>"/&gt;&lt;/td&gt; </pre>
     *
     * When a label value is not set, or defined in any properties files, then
     * its value will be created from the Fields name.
     * <p/>
     * For example given the TextField code:
     *
     * <pre class="codeJava">
     * TextField nameField = <span class="kw">new</span> TextField(<span class="st">"faxNumber"</span>);  </pre>
     *
     * Will render the TextField label as:
     * <pre class="codeHtml">
     * &lt;td&gt;&lt;label&gt;<span class="red">Fax Number</span>&lt;/label&gt;&lt;/td&gt;
     * &lt;td&gt;&lt;input type="text" name="<span class="st">faxNumber</span>"/&gt;&lt;/td&gt; </pre>
     *
     * @return the display label of the Field
     */
    public String getLabel() {
        if (label == null) {
            label = getMessage(getName() + ".label");
        }
        if (label == null) {
            label = ClickUtils.toLabel(getName());
        }
        return label;
    }

    /**
     * Set the Field display caption.
     *
     * @param label the display label of the Field
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The callback listener will only be called during processing if the Field
     * value is valid. If the field has validation errors the listener will not
     * be called.
     *
     * @see net.sf.click.Control#getName()
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

    /**
     * Return true if the Field is a readonly. The Field will also be readonly
     * if the parent Form is readonly.
     *
     * @return true if the Field is a readonly
     */
    public boolean isReadonly() {
        if (getForm() != null && getForm().isReadonly()) {
            return true;
        } else {
            return readonly;
        }
    }
 
    /**
     * Set the Field readonly flag.
     *
     * @param readonly the Field readonly flag
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * Return true if the Field's value is required.
     *
     * @return true if the Field's value is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Set the Field required status.
     *
     * @param required set the Field required status
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Return the field "tabindex" attribute value.
     *
     * @return the field "tabindex" attribute value
     */
    public int getTabIndex() {
        return tabindex;
    }

    /**
     * Set the field "tabindex" attribute value.
     *
     * @param tabindex the field "tabindex" attribute value
     */
    public void setTabIndex(int tabindex) {
        this.tabindex = tabindex;
    }

    /**
     * Return the field CSS "text-align" style, or null if not defined.
     *
     * @return the field CSS "text-align" style, or null if not defined.
     */
    public String getTextAlign() {
        return getStyle("text-align");
    }

    /**
     * Set the field CSS horizontal "text-align" style.
     *
     * @param align the CSS "text-align" value: <tt>["left", "right", "center"]</tt>
     */
    public void setTextAlign(String align) {
        setStyle("text-align", align);
    }

    /**
     * Return the 'title' attribute, or null if not defined. The title
     * attribute acts like tooltip message over the Field.
     * <p/>
     * If the title value is null, this method will attempt to find a
     * localized label message in the parent messages using the key:
     * <blockquote>
     * <tt>getName() + ".title"</tt>
     * </blockquote>
     * If not found then the message will be looked up in the
     * <tt>/click-control.properties</tt> file using the same key. If still
     * not found the title will be left as null and will not be rendered.
     * <p/>
     * For examle given a <tt>CustomerPage</tt> with the properties file
     * <tt>CustomerPage.properties</tt>:
     *
     * <pre class="codeConfig">
     * <span class="st">name</span>.label=<span class="red">Customer Name</span>
     * <span class="st">name</span>.title=<span class="red">Full name or Business name</span> </pre>
     *
     * The page TextField code:
     * <pre class="codeJava">
     * <span class="kw">public class</span> CustomerPage <span class="kw">extends</span> Page {
     *     TextField nameField = <span class="kw">new</span> TextField(<span class="st">"name"</span>);
     *     ..
     * } </pre>
     *
     * Will render the TextField label and title properties as:
     * <pre class="codeHtml">
     * &lt;td&gt;&lt;label&gt;<span class="red">Customer Name</span>&lt;/label&gt;&lt;/td&gt;
     * &lt;td&gt;&lt;input type="text" name="<span class="st">name</span>" title="<span class="red">Full name or Business name</span>"/&gt;&lt;/td&gt; </pre>
     *
     * @return the 'title' attribute tooltip message
     */
    public String getTitle() {
        if (title == null) {
            title = getMessage(getName() + ".title");
        }
        return title;
    }

    /**
     * Set the 'title' attribute tooltip message.
     *
     * @param value the 'title' attribute tooltip message
     */
    public void setTitle(String value) {
        title = value;
    }

    /**
     * Return true if the Field should validate itself when being processed.
     * <p/>
     * If the validate attribute for the Field is not explicity set, this
     * method will return the validation status of its parent Form, see
     * {@link Form#getValidate()}. If the Field validate attribute is not set
     * and the parent Form is not set this method will return true.
     * <p/>
     * This method is called by the {@link #onProcess()} method to determine
     * whether the the Field {@link #validate()} method should be invoked.
     *
     * @return true if the Field should validate itself when being processed.
     */
    public boolean getValidate() {
        if (validate != null) {
            return validate.booleanValue();

        } else if (getForm() != null) {
            return getForm().getValidate();

        } else {
            return true;
        }
    }

    /**
     * Set the validate Field value when being processed flag.
     *
     * @param validate the field value when processed
     */
    public void setValidate(boolean validate) {
        this.validate = Boolean.valueOf(validate);
    }

    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        return null;
    }

    /**
     * Return true if the Field is valid after being processed, or false
     * otherwise. If the Field has no error message after
     * {@link net.sf.click.Control#onProcess()} has been invoked it is considered to be
     * valid.
     *
     * @return true if the Field is valid after being processed
     */
    public boolean isValid() {
        return (error == null);
    }

    /**
     * Return the Field value.
     *
     * @return the Field value
     */
    public String getValue() {
        return (value != null) ? value : "";
    }

    /**
     * Set the Field value.
     *
     * @param value the Field value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Return the object representation of the Field value. This method will
     * return a string value, or null if the string value is null or is zero
     * length.
     * <p/>
     * Specialized object field subclasses should override this method to
     * return a non string object. For examples a <tt>DoubleField</tt> would
     * return a <tt>Double</tt> value instead.
     *
     * @return the object representation of the Field value
     */
    public Object getValueObject() {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    /**
     * Set the value of the field using the given object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            value = object.toString();
        }
    }

    /**
     * Return the width CSS "width" style, or null if not defined.
     *
     * @return the CSS "width" style attribute, or null if not defined
     */
    public String getWidth() {
        return getStyle("width");
    }

    /**
     * Set the the CSS "width" style attribute.
     *
     * @param value the CSS "width" style attribute
     */
    public void setWidth(String value) {
        setStyle("width", value);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method binds the submitted request value to the Field's value.
     */
    public void bindRequestValue() {
        setValue(getRequestValue());
    }

    /**
     * This method does nothing. Subclasses may override this method to deploy
     * static web resources.
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
    }

    /**
     * This method does nothing. Subclasses may override this method to perform
     * additional initialization.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
    }

    /**
     * This method processes the page request returning true to continue
     * processing or false otherwise. The Field <tt>onProcess()</tt> method is
     * typically invoked by the Form <tt>onProcess()</tt> method when
     * processing POST request.
     * <p/>
     * This method will bind the Field request parameter value to the field,
     * validate the sumission and invoke its callback listener if defined.
     * The code of this method is provided below:
     *
     * <pre class="codeJava">
     * <span class="kw">public boolean</span> onProcess() {
     *     bindRequestValue();
     *
     *     <span class="kw">if</span> (getValidate()) {
     *         validate();
     *     }
     *
     *     <span class="kw">return</span> invokeListener();
     * } </pre>
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        bindRequestValue();

        if (getValidate()) {
            validate();
        }

        return invokeListener();
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
     * This method does nothing. Subclasses may override this method to perform
     * clean up any resources.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * The validate method is invoked by <tt>onProcess()</tt> to validate
     * the request submission. Field subclasses should override this method
     * to implement request validation logic.
     * <p/>
     * If the field determines that the submission is invalid it should set
     * {@link #error} property with the error message.
     */
    public void validate() {
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return a normalised label for display in error messages.
     *
     * @return a normalized label for error message display
     */
    protected String getErrorLabel() {
        String label = getLabel().trim();
        label = (label.endsWith(":"))
                ? label.substring(0, label.length() - 1) : label;
        return label;
    }

    /**
     * Set the error with the a label formatted message specified by the given
     * message bundle key. The message will be formatted the field label using
     * {@link #getErrorLabel()}.
     *
     * @param key the key of the localized message bundle string
     */
    protected void setErrorMessage(String key) {
        setError(getMessage(key, getErrorLabel()));
    }

    /**
     * Set the error with the a label and value formatted message specified by
     * the given message bundle key. The message will be formatted the field
     * label {0} using {@link #getErrorLabel()} and the given value {1}.
     *
     * @param key the key of the localized message bundle string
     * @param value the value to format in the message
     */
    protected void setErrorMessage(String key, Object value) {
        Object[] args = new Object[] {
            getErrorLabel(), value
        };
        setError(getMessage(key, args));
    }

    /**
     * Set the error with the a label and value formatted message specified by
     * the given message bundle key. The message will be formatted the field
     * label {0} using {@link #getErrorLabel()} and the given value {1}.
     *
     * @param key the key of the localized message bundle string
     * @param value the value to format in the message
     */
    protected void setErrorMessage(String key, int value) {
        Object[] args = new Object[] {
            getErrorLabel(), new Integer(value)
        };
        setError(getMessage(key, args));
    }

    /**
     * Set the error with the a label and value formatted message specified by
     * the given message bundle key. The message will be formatted the field
     * label {0} using {@link #getErrorLabel()} and the given value {1}.
     *
     * @param key the key of the localized message bundle string
     * @param value the value to format in the message
     */
    protected void setErrorMessage(String key, double value) {
        Object[] args = new Object[] {
            getErrorLabel(), new Double(value)
        };
        setError(getMessage(key, args));
    }

    /**
     * Perform a action listener callback if a listener object and listener
     * method is defined, otherwise returns true.
     *
     * @see ClickUtils#invokeListener(Object, String)
     *
     * @return true if the invoked listener returns true, or if not listener
     * is defined
     */
    protected boolean invokeListener() {
        if (listener != null && listenerMethod != null) {
            return ClickUtils.invokeListener(listener, listenerMethod);

        } else {
            return true;
        }
    }

    /**
     * Return the field's value from the request.
     *
     * @return the field's value from the request
     */
    protected String getRequestValue() {
        String value = getContext().getRequestParameter(getName());
        if (value != null) {
            return value.trim();
        } else {
            return "";
        }
    }

}
