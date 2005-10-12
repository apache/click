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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides an abstract form Field control.
 * <p/>
 * The Form control acts a container for Field control instances. When a Form
 * is processed it will inturn process all the fields in contains. All Form
 * field controls must extend this abstract class.
 * <p/>
 * Localizable field messages and error messages are defined in the resource
 * bundle:
 *
 * <pre class="codeConfig">
 * /click-control.properties </pre>
 *
 * You can modify these properties by copying this file into your applications
 * root class path and editing these properties.
 * <p/>
 * <span style="font-weight: bolder">Note</span> when customizing
 * the message properties you must include all the properties, not just the
 * ones you want to override, otherwise MissingResourceExceptions may be
 * thrown.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public abstract class Field implements Control {

    // -------------------------------------------------------------- Constants

    /**
     * The control package messages bundle name: &nbsp; <tt>click-control</tt>
     */
    protected static final String CONTROL_MESSAGES = "click-control";

    // ----------------------------------------------------- Instance Variables

    /** The Field attributes Map. */
    protected Map attributes;

    /** The Page request Context. */
    protected Context context;

    /** The Field disabled value. */
    protected boolean disabled;

    /** The Field error message. */
    protected String error;

    /** The request focus flag. */
    protected boolean focus;

    /** The parent Form. */
    protected Form form;

    /** The Field label. */
    protected String label;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /** The Field name. */
    protected String name;

    /** The Field is readonly flag. */
    protected boolean readonly;

    /** The Field is required flag. */
    protected boolean required;

    /** The Field 'title' attribute, which acts as a tooltip help message. */
    protected String title;

    /** The Field value. */
    protected String value;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new Field object.
     */
    public Field() {
    }

    /**
     * Construct the Field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the Field
     */
    public Field(String label) {
        setLabel(label);
        setName(ClickUtils.toName(label));
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
     * Return the Field HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of field HTML attribute
     * @return the Field HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the Fields with the given HTML attribute name and value. These
     * attributes will be rendered as HTML attributes, for example:
     *
     * <pre class="codeJava">
     * TextField textField = new TextField("Username");
     * textField.setAttribute("<span class="blue">class</span>", "<span class="red">login</span>"); </pre>
     *
     * HTML output:
     * <pre class="codeHtml">
     * &lt;input type='text' name='username' value='' <span class="blue">class</span>='<span class="red">login</span>'/&gt; </pre>
     *
     * If there is an existing named attribute in the Field it will be replaced
     * with the new value. If the given attribute value is null, any existing
     * attribute will be removed.
     *
     * @param name the name of the field HTML attribute
     * @param value the value of the field HTML attribute
     * @throws IllegalArgumentException if attribute name is null
     */
    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        if (attributes == null) {
            attributes = new HashMap(5);
        }

        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }

    /**
     * Return the Field attributes Map.
     *
     * @return the field attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the Field has attributes or false otherwise.
     *
     * @return true if the Field has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see Control#getContext()
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (label != null && label.startsWith("msg:")) {
            String key = label.substring(4);
            this.label = getMessage(key);
        }
    }

    /**
     * Return HTML rendering string " disabled" if the Field is disabled or a
     * blank string otherwise.
     *
     * @see #isDisabled()
     *
     * @return HTML rendering string for the Fields disabled status
     */
    public String getDisabled() {
        return (isDisabled()) ? " disabled='disabled'" : "";
    }

    /**
     * Return true if the Field is a disabled. The Field will also be disabled
     * if the parent Form is disabled.
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
     * Set the Field disabled flag
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
     * Set the Field request focus flag
     *
     * @param focus the request focus flag
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
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
     * @param form Field's parent <tt>Form</tt>.
     */
    public void setForm(Form form) {
        this.form = form;
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
     */
    public String getId() {
        String formId = (getForm() != null) ? getForm().getId() + "-" : "";

        if (hasAttributes() && getAttributes().containsKey("id")) {
            return formId + getAttribute("id");
        } else {
            return formId + getName();
        }
    }

    /**
     * Return the display caption label.
     *
     * @return the display label of the Field
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the Field display caption.
     * <p/>
     * If the given label value is prefixed with <tt>"msg:"</tt> then the label will be
     * considered to be a localized message key. This value will be replaced
     * with the localized message string for the given key when the Field's context
     * is set.
     * <p/>
     * For examle:
     *
     * <pre class="codeJava">
     * TextField nameField = <span class="kw">new</span> TextField(<span class="st">"name"</span>);
     * nameField.setLabel(<span class="st">"msg:name.label"</span>);
     *
     * <span class="cm">// Or alternatively</span>
     * TextField nameField = <span class="kw">new</span> TextField(<span class="st">"name"</span>, <span class="st">"msg:name.label"</span>); </pre>
     *
     * Will replace the Field label with the localized message
     * <span class="red">Customer Name</span> for the key
     * <span class="st">name.label</span>  when the context is set.
     * This message would be defined in the <tt>/click-control.properties</tt> file:
     *
     * <pre class="codeConfig">
     * <span class="st">name.label</span>=<span class="red">Customer Name</span> </pre>
     *
     * @param label the display label of the Field.
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
     */
    public void setListener(Object target, String methodName) {
        listener = target;
        listenerMethod = methodName;
    }

    /**
     * Return the package resource bundle message for the named resource key
     * and the context's request locale.
     *
     * @param name resource name of the message
     * @return the named localized message for the package
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        Locale locale = getContext().getLocale();

        ResourceBundle bundle =
            ResourceBundle.getBundle(CONTROL_MESSAGES, locale);

        return bundle.getString(name);
    }

    /**
     * Return the formatted package message for the given resource name
     * and message format argument and for the context request locale.
     *
     * @param name resource name of the message
     * @param arg the message argument to format
     * @return the named localized message for the package
     */
    public String getMessage(String name, Object arg) {
        Object[] args = new Object[] { arg };
        return getMessage(name, args);
    }

    /**
     * Return the formatted package message for the given resource name and
     * message format arguments and for the context request locale.
     *
     * @param name resource name of the message
     * @param args the message arguments to format
     * @return the named localized message for the package
     */
     public String getMessage(String name, Object[] args) {
        if (args == null) {
            throw new IllegalArgumentException("Null args parameter");
        }
        String value = getMessage(name);

        return MessageFormat.format(value, args);
    }

    /**
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        return name;
    }
 
    /**
     * @see net.sf.click.Control#setName(String)
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
    }

    /**
     * Return HTML rendering string " readonly" if the Field is readonly or a
     * blank string otherwise.
     *
     * @see #isDisabled()
     *
     * @return HTML rendering string for the Fields readonly status
     */
    public String getReadonly() {
        return (isReadonly()) ? " readonly='readonly'" : "";
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
     * Set the Field readonly flag
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
     * Return the 'title' attribute, or null if not defined. The title
     * attribute acts like tooltip message over the Field.
     *
     * @return the 'title' attribute tooltip message
     */
    public String getTitle() {
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
     * The Field inherits its validate status from its parent Form, see
     * {@link Form#getValidate()}.
     *
     * @return true if the Field should validate itself when being processed.
     */
    public boolean validate() {
        if (getForm() != null) {
            return getForm().getValidate();
        } else {
            return true;
        }
    }

    /**
     * Return true if the Field is valid after being processed, or false
     * otherwise. If the Field has no error message after
     * {@link Control#onProcess()} has been invoked it is considered to be
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
    public void setValue(Object value) {
        if (value != null) {
            this.value = value.toString();
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Perform a action listener callback if a listener object and listener
     * method is defined.
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

    /**
     * Render the field HTML attributes to the string buffer, except for
     * the attribute "id".
     *
     * @param buffer the StringBuffer to render the HTML attributes to
     */
    protected void renderAttributes(StringBuffer buffer) {
        if (hasAttributes()) {
            ClickUtils.renderAttributes(getAttributes(), buffer);
        }
    }
}
