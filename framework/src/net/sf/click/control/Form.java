/*
 * Copyright 2004 Malcolm A. Edgar
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

/**
 * Provides a Form control: &nbsp; &lt;form&gt;.
 * <p/>
 * <form method='POST' name='form' action='Form.html'>
 * <input name='form_name' type='hidden' value='form'/>
 * <table class='form'>
 * <tr>
 * <td align='left'><label>Username</label><font color="red">*</font></td>
 * <td align='left'><input type='text' name='username' value='' size='20'   maxlength='20' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label  >Password</label><font color="red">*</font></td>
 * <td align='left'><input type='password' name='password' value='' size='20'   maxlength='20' /></td>
 * </tr>
 * <tr><td colspan='2'>&nbsp;</td></tr>
 * <tr align='left'><td colspan='2'>
 * <input type='submit' name='ok' value='  OK  '/><input type='submit' name='cancel' value=' Cancel '/></td>
 * </tr></table></form>
 * <p/>
 * When a Form is processed it will process its {@link Field} controls 
 * in the order they were added to the form, and then it will process the 
 * {@link Button} controls in the added order. Once all the Fields have been 
 * processed the form will invoke its action listener if defined.
 * <p/>
 * The example below illustrates a Form being used in a login Page.<blockquote><pre>
 * public class Login extends Page {
 * 
 *     Form form;
 *     TextField usernameField;
 *     PasswordField passwordField;
 * 
 *     public void onInit() {
 *         form = new Form("<font color='blue'>form</font>", getContext());
 *         addControl(form);
 * 
 *         usernameField = new TextField("Username");
 *         usernameField.setMaxLength(20);
 *         usernameField.setMinLength(5);
 *         usernameField.setRequired(true);
 *         form.add(usernameField);
 * 
 *         passwordField = new PasswordField("Password");
 *         passwordField.setMaxLength(20);
 *         passwordField.setMinLength(5);
 *         passwordField.setRequired(true);
 *         form.add(passwordField);
 * 
 *         Submit okButton = new Submit("  OK  ");
 *         okButton.setListener(this, "onOkClicked");
 *         form.add(okButton);
 * 
 *         Submit cancelButton = new Submit(" Cancel ");
 *         cancelButton.setListener(this, "onCancelClicked");
 *         form.add(cancelButton);
 *     }
 * 
 *     public boolean onOkClicked() {
 *         if (form.isValid()) {
 *             String username = usernameField.getValue();
 *             String password = passwordField.getValue();
 * 
 *             User user = UserDatabase.getUser(username);
 * 
 *             if (user != null && user.getPassword().equals(password)) {
 *                 getContext().setSessionAttribute("user", user);
 *                 setRedirect("home.htm");
 *             } 
 *             else {
 *                 String msg = "The system could not log you on.&lt;br&gt; Make sure "
 *                     + "your Username and password is correct, then try again.";        
 *                 form.setError(msg);           
 *             }
 *         }
 *         return true;
 *     }
 * 
 *     public boolean onCancelClicked() {
 *         setRedirect("index.htm");
 *         return false;
 *     }
 * }</pre></blockquote>
 * The corresponding template code is below. The form will render itself using
 * its {@link #toString()} method.<blockquote><pre>
 * <font color="blue">$form</font></pre></blockquote>
 *
 * If a Form has been posted and processed, if it has an {@link #error} defined or 
 * any of its Fields hava validation errors they will be automatically 
 * rendered, and the {@link #isValid()} method will return false.
 * <p/>
 * Alternatively you can layout the Form in the page template specifying
 * the fields using the named field notation:<blockquote><pre>
 * $form.{@link #fields}.usernameField</pre></blockquote>
 * 
 * Or you can use the Form {@link #fieldList} and {@link #buttonList} properties
 * to layout a generic form.<blockquote><pre>
 * &lt;form method="POST"&gt;
 * &lt;input type="hidden" name="form_name" value="form"/&gt;
 * &lt;table width="100%"&gt;
 * <font color="red">#foreach (</font><font color="blue">$field</font> <font color="red">in </font><font color="blue">$form.fieldList</font><font color="red">)</font>
 *   <font color="red">#if( !</font><font color="blue">$field.isValid()</font> <font color="red">)</font>
 *   &lt;tr&gt;
 *     &lt;td colspan="2"&gt; <font color="blue">$field.error</font> &lt;/td&gt;
 *   &lt;/tr&gt;
 *   <font color="red">#end</font>
 *   &lt;tr&gt;
 *     &lt;td&gt; <font color="blue">$field.label</font> &lt;/td&gt; &lt;td&gt; $<font color="blue">field</font> &lt;/td&gt;
 *   &lt;/tr&gt;
 * <font color="red">#end</font>
 *  &lt;tr&gt;
 *    &lt;td colspan="2"&gt;
 *    <font color="red">#foreach (</font><font color="blue">$button</font> <font color="red">in </font><font color="blue">$form.buttonList</font><font color="red">)</font>
 *      <font color="blue">$button</font> &amp;nbsp;
 *    <font color="red">#end</font> 
 *    &lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/table&gt;
 * &lt;/form&gt;</pre></blockquote>
 * 
 * Whenever including your own Form markup in a page template or Velocity macro, 
 * always specify the form {@link #method} and include a hidden field which 
 * specifies the {@link #name} of the Form, for example:<blockquote><pre>
 * &lt;form method="<font color="blue">POST</font>"&gt;
 * &lt;input type="<font color="blue">hidden</font>" name="<font color="blue">form_name</font>" value="<font color="blue">searchForm</font>"/&gt;
 * </pre></blockquote>
 * 
 * The form_name hidden field is used to ensure the correct form is processed
 * in a Page which may have multiple forms.
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification" 
 *    href="../../../../../html/interact/forms.html#h-17.3">FORM</a>
 * 
 * @see Field
 * @see Submit
 *
 * @author Malcolm Edgar
 */
public class Form implements Control {

    /**
     * The form name parameter for multiple forms: &nbsp; <tt>"form_name"</tt>
     */
    public static final String FORM_NAME = "form_name";   
    
    /** The label on left form layout. */
    public static final int LABEL_ON_LEFT = 10;
    
    /** The label on top form layout. */
    public static final int LABEL_ON_TOP = 11;
    
    /** The required field label prefix. */
    protected static String labelRequiredPrefix = "";
    
    /** The required field label postfix. */
    protected static String labelRequiredPostfix = "";
    
    static {
        ResourceBundle bundle = 
            ResourceBundle.getBundle(Field.PACKAGE_MESSAGES);
        
        labelRequiredPrefix = bundle.getString("label-required-prefix");
        labelRequiredPostfix = bundle.getString("label-required-postfix");
    }

    // ----------------------------------------------------- Instance Variables
    
    /** The form attributes map. */
    protected Map attributes;
    
    /** The button align, default value is "<tt>left</tt>" */
    protected String buttonAlign = "left";

    /** The ordered list of button values. */
    protected final List buttonList = new ArrayList(5);

    /** The form context. */
    protected Context context;

    /** The form level error message. */
    protected String error;

    /** The ordered list of field values, excluding buttons */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The JavaScript enabled flag, defaults value is true. */
    protected boolean jsEnabled = true;

    /** The label align, default value is "<tt>left</tt>" */
    protected String labelAlign = "left";
    
    /** 
     * The form layout <tt>[LABEL_ON_LEFT, LABEL_ON_TOP]</tt> default value: 
     * &nbsp; <tt>LABEL_ON_LEFT</tt>
     */
    protected int layout = LABEL_ON_LEFT;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;
    
    /** 
     * The form method <tt>["POST, "GET"]</tt>, default value: &nbsp; 
     * <tt>POST</tt> 
     */
    protected String method = "POST";

    /** The form name. */
    protected String name;
    
    // ----------------------------------------------------------- Constructors

    /**
     * Create a form with the given name and context.
     *
     * @param name the name of the form
     * @param context the form command context
     * @throws IllegalArgumentException if the form name or command is null.
     */
    public Form(String name, Context context) {
        setName(name);
        setContext(context);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Add the field to the form, and set the fields form property. The field 
     * will be added to the {@link #fields} Map using its name. 
     * <p/>
     * Button instances will also be added to the {@link #buttonList} while
     * all others field types will also be added to the {@link #fieldList}.
     *
     * @param field the field to add to the form.
     * @throws IllegalArgumentException if the form already contains a field
     * or button with the same name as the added field
     */
    public void add(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field parameter cannot be null");
        }
        if (fields.containsKey(field.getName())) {
            throw new IllegalArgumentException
                ("Form already contains field named: " + field.getName());
        }
        
        if (field instanceof Button) {
            buttonList.add(field);
        } else {
            fieldList.add(field);
        }
        fields.put(field.getName(), field);
        field.setForm(this);
        field.setContext(getContext());
    }
    
    /**
     * Remove the given field from the form.
     * 
     * @param field the field to remove from the form
     */
    public void remove(Field field) {
        if (field != null && fields.containsKey(field.getName())) {
            field.setForm(null);
            fields.remove(field.getName());
            if (field instanceof Button) {
                buttonList.remove(field);
            } else {
                fieldList.remove(field);
            }
        }
    }
    
    /**
     * Return the link HTML attribute with the given name, or null if the
     * attribute does not exist.
     * 
     * @param name the name of link HTML attribute
     * @return the link HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * Set the form HTML attribute with the given attribute name and value..
     *
     * @param name the name of the form HTML attribute
     * @param value the value of the form HTML attribute
     * @throws IllegalArgumentException if name parameter is null
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
     * Return the ordered list of {@link Button}s.
     *
     * @return the ordered list of {@link Button}s.
     */
    public List getButtonList() {
        return buttonList;
    }

    /**
     * Return the button HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>".
     *
     * @return the button HTML horizontal alignment
     */
    public String getButtonAlign() {
        return buttonAlign;
    }

    /**
     * Set the button HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>". Note the given align is not
     * validated.
     *
     * @param align the button HTML horizontal alignment
     */
    public void setButtonAlign(String align) {
        buttonAlign = align;
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
    }

    /**
     * Return the form level error message.
     *
     * @return the form level error message
     */
    public String getError() {
        return error;
    }

    /**
     * Set the form level validation error message. If the error message is not
     * null the form is invalid.
     *
     * @param error the validation error message
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Return the named field if contained in the form, or null if not found.
     *
     * @param name the name of the field
     * @return the named field if contained in the form
     */
    public Field getField(String name) {
        return (Field) fields.get(name);
    }

    /**
     * Return the List of form fields, ordered in addition order to the form.
     *
     * @return the ordered List of form fields
     */
    public List getFieldList() {
        return fieldList;
    }

    /**
     * Return the Map of form fields, keyed on field name.
     *
     * @return the Map of form fields, keyed on field name
     */
    public Map getFields() {
        return fields;
    }

    /**
     * Return the field value for the named field, or null if the field is not
     * found.
     *
     * @param name the name of the field
     * @return the field value for the named field
     */
    public String getFieldValue(String name) {
        Field field = getField(name);
        if (field != null) {
            return field.getValue();
        } else {
            return null;
        }
    }

    /**
     * Return the HTML head import statement for the CSS stylesheet 
     * click/form.css. If JavaScript is enabled a import statement for 
     * click/form.js will also be included.
     *
     * @see #jsEnabled
     *
     * @return the HTML head import statements for the form stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String contextPath = context.getRequest().getContextPath();

        if (jsEnabled) {

            return "<link rel='stylesheet' type='text/css' href='"
                   + contextPath + "/click/form.css' title='style'/>\n"
                   + "<script language='JavaScript' type='text/javascript' "
                   + "src='" + contextPath + "/click/form.js'/>";

        } else {

            return "<link rel='stylesheet' type='text/css' href='"
                   + contextPath + "/click/form.css' title='style'/>";

        }
    }

    /**
     * Return true if JavaScript is enabled.
     *
     * @return true if JavaScript is enabled
     */
    public boolean getJSEnabled() {
        return jsEnabled;
    }

    /**
     * Set whether JavaScript is enabled.
     *
     * @param enabled sets whether JavaScript is enabled
     */
    public void setJSEnabled(boolean enabled) {
        jsEnabled = enabled;
    }

    /**
     * Return the field label HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>".
     *
     * @return the field label HTML horizontal alignment
     */
    public String getLabelAlign() {
        return labelAlign;
    }

    /**
     * Set the field label HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>". Note the given align is not
     * validated.
     *
     * @param align the field label HTML horizontal alignment
     */
    public void setLabelAlign(String align) {
        labelAlign = align;
    }
  
    /**
     * Return the form layout <tt>[LABEL_ON_LEFT, LABEL_ON_TOP]</tt>.
     * 
     * @return layout the form layout
     */
    public int getLayout() {
        return layout;
    }
    
    /**
     * Set the form layout <tt>[LABEL_ON_LEFT, LABEL_ON_TOP]</tt>.
     * 
     * @param layout the form layout
     */
    public void setLayout(int layout) {
        if (layout != LABEL_ON_LEFT && layout != LABEL_ON_TOP) {
            throw new IllegalArgumentException("Invalid layout: " + layout);
        }
        this.layout = layout;
    }

    /**
     * The callback listener will only be called during processing if the field
     * value is valid. If the field has validation errors the listener will not
     * be called.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     */
    public void setListener(Object target, String methodName) {
        listener = target;
        listenerMethod = methodName;
    }
 
    /**
     * Return the form method <tt>["POST" | "GET"]</tt>
     * 
     * @return the form method
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Set the form method <tt>["POST" | "GET"]</tt>
     * 
     * @param value the form method
     */
    public void setMethod(String value) {
        method = value;
    }

    /**
     * Return the name of the form.
     *
     * @see net.sf.click.Control#getName()
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the form.
     * 
     * @see net.sf.click.Control#setName(String)
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;
    }
    
    /**
     * Return true if the fields are valid and there is no form level error,
     * otherwise return false.
     *
     * @return true if the fields are valid and there is no form level error
     */
    public boolean isValid() {
        if (error != null) {
            return false;
        }
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);
            if (!field.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Process the Form when the Context request method is the same as the Forms,
     * by default "POST" method.
     * <p/>
     * The Forms processing order is:<ol>
     * <li>All {@link Field} controls in the order they were added</li>
     * <li>All {@link Button} controls in the order they were added</li>
     * <li>Invoke the Forms listener if defined</li>
     * </ol>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        HttpServletRequest request = getContext().getRequest();

        if (request.getMethod().equalsIgnoreCase(getMethod())) {
            // If a form name is defined, but does not match this form exit.
            String formName = request.getParameter(FORM_NAME);
            if (formName != null && !formName.equals(name)) {
                return true;
            }

            boolean continueProcessing = true;
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                continueProcessing = field.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                continueProcessing = button.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);
            }
        }
        
        return true;
    }
    
    /**
     * Return the HTML string representation of the form.
     * <p/>
     * If the form contains errors after processing, these errors will be 
     * rendered.
     *
     * @see Object#toString()
     */
    public String toString() {
        final boolean process = 
            context.getRequest().getMethod().equalsIgnoreCase(getMethod());

        // Estimate the size of the string buffer
        int bufferSize = 0;
        if (process) {
            if (jsEnabled) {
                bufferSize = 300 + (fieldList.size() * 700)
                    + (buttonList.size() * 100);
            } else {
                bufferSize = 300 + (fieldList.size() * 400)
                    + (buttonList.size() * 100);
            }
        } else {
            if (jsEnabled) {
                bufferSize = 300 + (fieldList.size() * 500)
                    + (buttonList.size() * 100);
            } else {
                bufferSize = 300 + (fieldList.size() * 400)
                    + (buttonList.size() * 100);
            }
        }
        StringBuffer buffer = new StringBuffer(bufferSize);

        int hiddenCount = 0;

        buffer.append("<form method='");
        buffer.append(getMethod());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' action='");
        buffer.append(context.getRequest().getRequestURI());
        buffer.append("'");
        ClickUtils.renderAttributes(attributes, buffer);
        buffer.append(">\n");
        
        buffer.append("<input name='form_name' type='hidden' value='");
        buffer.append(getName());
        buffer.append("'/>\n");

        buffer.append("<table class='form'>\n");
        
        for (int i = 0, size = fieldList.size(); i < size; i++) {

            Field field = (Field) fieldList.get(i);

            if (!field.isHidden()) {
                buffer.append("<tr>\n");
                
                // Write out label
                if (layout == LABEL_ON_LEFT) {
                    buffer.append("<td align='");
                    buffer.append(labelAlign);
                    buffer.append("'>");
                } else if (layout == LABEL_ON_TOP) {
                    buffer.append("<td valign='top'>");
                }
                
                if (field.isRequired()) {
                    buffer.append(labelRequiredPrefix);
                }
                buffer.append("<label ");
                buffer.append(field.getDisabled());
                buffer.append(">");
                buffer.append(field.getLabel());
                buffer.append("</label>");
                if (field.isRequired()){
                    buffer.append(labelRequiredPostfix);
                } 
                
                if (layout == LABEL_ON_LEFT) {
                    buffer.append("</td>\n");
                    buffer.append("<td align='left'>");
                } else if (layout == LABEL_ON_TOP) {
                    buffer.append("<br>");
                }
                
                // Write out field
                buffer.append(field);
                buffer.append("</td>\n");
                buffer.append("</tr>\n");

            } else {
                hiddenCount++;
            }
        }

        boolean foundError = false;
        if (process) {

            if (error != null) {
                foundError = true;
                buffer.append("<tr><td colspan='2'><span class='error'>");
                buffer.append(error);
                buffer.append("</span></td></tr>\n");
            }

            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (!field.isValid()) {
                    foundError = true;
                    buffer.append("<tr><td colspan='2'>");
                    if (jsEnabled) {
                        buffer.append("<a class='error'");
                        buffer.append(" href='javascript:document.");
                        buffer.append(getName());
                        buffer.append(".");
                        buffer.append(field.getName());
                        buffer.append(".focus();'>");
                        buffer.append(field.getError());
                        buffer.append("</a>");
                    } else {
                        buffer.append("<span class='error'>");
                        buffer.append(field.getError());
                        buffer.append("</span>");
                    }
                    buffer.append("</td></tr>\n");
                }
            }
        }

        buffer.append("<tr><td colspan='2'>&nbsp;</td></tr>\n");

        buffer.append("<tr align='");
        buffer.append(buttonAlign);
        buffer.append("'><td colspan='2'>");
        if (buttonList.isEmpty()) {
            buffer.append("<input type='submit' value='Submit'/>");
        } else {
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                buffer.append(button);
            }
        }
        buffer.append("</td></tr>\n");
        
        buffer.append("</table>\n");

        if (hiddenCount > 0) {
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (field.isHidden()) {
                    buffer.append(field);
                    buffer.append("\n");
                    hiddenCount--;
                    if (hiddenCount == 0) {
                        break;
                    }
                }
            }
        }

        buffer.append("</form>\n");

        return buffer.toString();
    }    
}

