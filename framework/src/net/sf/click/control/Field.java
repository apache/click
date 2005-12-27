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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.MessagesMap;

/**
 * Provides an abstract form Field control.
 * <p/>
 * The Form control acts a container for Field control instances. When a Form
 * is processed it will inturn process all the fields it contains. All Form
 * field controls must extend this abstract class.
 * <p/>
 * Field classes provide localizable field messages and error messages
 * defined in the resource bundle:
 *
 * <pre class="codeConfig">
 * /click-control.properties </pre>
 *
 * Access to these message is provided using the {@link #getMessage(String)}
 * method.
 * <p/>
 * You can modify these properties by copying this file into your applications
 * root class path and editing these properties.
 * <p/>
 * <span style="font-weight: bolder">Note</span> when customizing
 * the message properties you must include all the properties, not just the
 * ones you want to override.
 *
 * @author Malcolm Edgar
 */
public abstract class Field implements Control {

    // -------------------------------------------------------------- Constants

    /**
     * The control package messages bundle name: &nbsp; <tt>click-control</tt>
     */
    public static final String CONTROL_MESSAGES = "click-control";

    // ----------------------------------------------------- Instance Variables

    /** The Field attributes Map. */
    protected Map attributes;

    /** The Page request Context. */
    protected Context context;

    /** The global localized control messages map. */
    protected Map controlMessages;

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

    /** The Field localized messages Map. */
    protected Map messages;

    /** The Field name. */
    protected String name;

    /** The parent localized messages map. */
    protected Map parentMessages;

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
     * attributes will be rendered as HTML attributes.
     * <p/>
     * For example the TextField code:
     *
     * <pre class="codeJava">
     * TextField textField = <span class="kw">new</span> TextField("username");
     * textField.setAttribute("<span class="blue">class</span>", "<span class="red">login</span>"); </pre>
     *
     * Will render the HTML:
     * <pre class="codeHtml">
     * &lt;input type="text" name="username" size="20" <span class="blue">class</span>="<span class="red">login</span>"/&gt; </pre>
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
     * Set the Field context value.
     *
     * @see Control#setContext(Context)
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;
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
     * Return the HTML head element import string. This method returns null.
     * <p/>
     * Override this method to specify JavaScript and CSS includes for the
     * HTML head element. This value will rendered by the Form
     * {@link Form#getHtmlImports()} method.
     *
     * @return the HTML Header element include string.
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
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");

        } else {
            String formId = (getForm() != null) ? getForm().getId() + "-" : "";

            return formId + getName();
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
     * Return the localized message for the given key, or null if not found.
     * <p/>
     * This method will attempt to lookup the localized message in the
     * parentMessages, which by default represents the Page's resource bundle.
     * <p/>
     * If the message was not found, the this method will attempt to look up the
     * value in the <tt>/click-control.properties</tt> message properties file.
     * <p/>
     * If still not found, this method will return null.
     *
     * @param name the name of the message resource
     * @return the named localized message, or null if not found
     */
    public String getMessage(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        String message = null;

        if (getParentMessages() != null && getParentMessages().containsKey(name))
        {
            message = (String) getParentMessages().get(name);
        }

        if (message == null && getMessages().containsKey(name)) {
            message = (String) getMessages().get(name);
        }

        if (message == null && getControlMessages().containsKey(name)) {
            message = (String) getControlMessages().get(name);
        }

        return message;
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
      * Return a Map of localized messages for the Field.
      *
      * @return a Map of localized messages for the Field
      * @throws IllegalStateException if the context for the Field has not be set
      */
     public Map getMessages() {
         if (messages == null) {
             if (getContext() != null) {
                 Locale locale = getContext().getLocale();
                 messages = new MessagesMap(getClass().getName(), locale);

             } else {
                 String msg = "Cannot initialize messages as context not set";
                 throw new IllegalStateException(msg);
             }
         }
         return messages;
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
     * @see Control#getParentMessages()
     */
    public Map getParentMessages() {
        return parentMessages;
    }

    /**
     * @see Control#setParentMessages(Map)
     */
    public void setParentMessages(Map messages) {
        parentMessages = messages;
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
    public void setValue(String value) {
        if (value != null) {
            this.value = value.toString();
        }
    }

    /**
     * Return the value object class of the Field. This method returns
     * <tt>String.class</tt>.
     *
     * @return the value object class of the field
     */
    public Class getValueClass() {
        return String.class;
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

    // ---------------------------------------------------------- Public Methods

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Deployable#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return a Map of common localized control messages. This message
     * resouces are loaded from the property file
     * <tt>/click-control.properties</tt>.
     *
     * @return a Map of common localized control messages
     * @throws IllegalStateException if the context for the Field has not be set
     */
    protected Map getControlMessages() {
        if (controlMessages == null) {
            if (getContext() != null) {
                Locale locale = getContext().getLocale();
                controlMessages = new MessagesMap(CONTROL_MESSAGES, locale);

            } else {
                String msg =
                    "Cannot initialize control messages as context not set";
                throw new IllegalStateException(msg);
            }
        }
        return controlMessages;
    }

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
     * Return a normalised label for display in error messages.
     *
     * @return a normalized label for error message display
     */
    protected String getErrorLabel() {
        String label = getLabel().trim();
        label = (label.endsWith(":")) ?
                label.substring(0, label.length() - 1) : label;
        return label;
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
