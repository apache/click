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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.util.ClickUtils;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a Form control: &nbsp; &lt;form method='POST'&gt;.
 *
 * <table class='htmlHeader' cellspacing='12'>
 * <tr>
 * <td>
 *
 * <table class='fields'>
 * <tr>
 * <td align='left'><label>Username</label><span class="red">*</span></td>
 * <td align='left'><input type='text' name='username' value='' size='20' maxlength='20' /></td>
 * </tr>
 * <tr>
 * <td align='left'><label>Password</label><span class="red">*</span></td>
 * <td align='left'><input type='password' name='password' value='' size='20' maxlength='20' /></td>
 * </tr>
 * </table>
 * <table class="buttons">
 * <tr><td>
 * <input type='submit' name='ok' value='  OK  '/>&nbsp;<input type='submit' name='cancel' value=' Cancel '/>
 * </td></tr>
 * </table>
 *
 * </td>
 * </tr>
 * </table>
 *
 * When a Form is processed it will process its {@link Field} controls
 * in the order they were added to the form, and then it will process the
 * {@link Button} controls in the added order. Once all the Fields have been
 * processed the form will invoke its action listener if defined.
 * <p/>
 * The example below illustrates a Form being used in a login Page.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> Login <span class="kw">extends</span> Page {
 *
 *     Form form;
 *     TextField usernameField;
 *     PasswordField passwordField;
 *
 *     <span class="kw">public void</span> onInit() {
 *         form = <span class="kw">new</span> Form(<span class="st">"form"</span>, getContext());
 *         addControl(form);
 *
 *         usernameField = <span class="kw">new</span> TextField(<span class="st">"Username"</span>);
 *         usernameField.setMaxLength(20);
 *         usernameField.setMinLength(5);
 *         usernameField.setRequired(<span class="kw">true</span>);
 *         form.add(usernameField);
 *
 *         passwordField = <span class="kw">new</span> PasswordField(<span class="st">"Password"</span>);
 *         passwordField.setMaxLength(20);
 *         passwordField.setMinLength(5);
 *         passwordField.setRequired(<span class="kw">true</span>);
 *         form.add(passwordField);
 *
 *         Submit okButton = <span class="kw">new</span> Submit(<span class="st">"  OK  "</span>);
 *         okButton.setListener(<span class="kw">this</span>, <span class="st">"onOkClick"</span>);
 *         form.add(okButton);
 *
 *         Submit cancelButton = <span class="kw">new</span> Submit(<span class="st">" Cancel "</span>);
 *         cancelButton.setListener(<span class="kw">this</span>, <span class="st">"onCancelClick"</span>);
 *         form.add(cancelButton);
 *     }
 *
 *     <span class="kw">public boolean</span> onOkClick() {
 *         <span class="kw">if</span> (form.isValid()) {
 *             String username = usernameField.getValue();
 *             String password = passwordField.getValue();
 *
 *             User user = UserDAO.findByPK(username);
 *
 *             <span class="kw">if</span> (user != <span class="kw">null</span> && user.getPassword().equals(password)) {
 *                 getContext().setSessionAttribute(<span class="st">"user"</span>, user);
 *                 setRedirect(<span class="st">"home.htm"</span>);
 *             }
 *             <span class="kw">else</span> {
 *                  form.setError(getMessage(<span class="st">"authentication-error"</span>));
 *             }
 *         }
 *         <span class="kw">return true</span>;
 *     }
 *
 *     <span class="kw">public boolean</span> onCancelClick() {
 *         setRedirect(<span class="st">"index.htm"</span>);
 *         <span class="kw">return false</span>;
 *     }
 * } </pre>
 *
 * The forms corresponding template code is below. Note the form automatically
 * renders itself when Velocity invokes its {@link #toString()} method.
 *
 * <pre class="codeHtml">
 * <span class="blue">$form</span> </pre>
 *
 * If a Form has been posted and processed, if it has an {@link #error} defined or
 * any of its Fields hava validation errors they will be automatically
 * rendered, and the {@link #isValid()} method will return false.
 *
 * <a name="form-layout"><h3>Form Layout</h3></a>
 *
 * <a name="auto-layout"><h4>Auto Layout</h4></a>
 *
 * If you include a form variable in your template the form will be
 * automatically layed out and rendered. Auto layout, form and field rendering
 * options include:
 *
 * <table style="margin-left: 1em;" cellpadding="3">
 * <tr>
 * <td>{@link #buttonAlign}</td> <td>button alignment: &nbsp; <tt>["left", "center", "right"]</tt></td>
 * </tr><tr>
 * <td>{@link #columns}</td> <td>number of form table columns, the default value number is 1</td>
 * </tr><tr>
 * <td>{@link #errorsAlign}</td> <td>validation error messages alignment: &nbsp; <tt>["left", "center", "right"]</tt></td>
 * </tr><tr>
 * <td>{@link #errorsPosition}</td> <td>validation error messages position: &nbsp; <tt>["top", "middle", "bottom"]</tt></td>
 * </tr><tr>
 * <td>{@link #labelAlign}</td> <td>field label alignment: &nbsp; <tt>["left", "center", "right"]</tt></td>
 * </tr><tr>
 * <td>{@link #labelsPosition}</td> <td>label position relative to field: &nbsp; <tt>["left", "top"]</tt></td>
 * </tr><tr>
 * <td>click/form.css</td> <td>form CSS styles, located under web root directory</td>
 * </tr><tr>
 * <td>/click-control.properties</td> <td>form and field messages and HTML, located under classpath</td>
 * </tr>
 * </table>
 *
 * <a name="manual-layout"><h4>Manual Layout</h4></a>
 *
 * You can also manually layout the Form in the page template specifying
 * the fields using the named field notation:
 *
 * <pre class="codeHtml">
 * $form.{@link #fields}.usernameField </pre>
 *
 * Whenever including your own Form markup in a page template or Velocity macro
 * always specify:
 * <ul style="margin-top: 0.5em;">
 *  <li><span class="maroon">method</span>
 *      - the form submission method <tt>["POST" | "GET"]</tt></li>
 *  <li><span class="maroon">name</span>
 *      - the name of your form, important when using JavaScript</li>
 *  <li><span class="maroon">action</span>
 *      - directs the Page where the form should be submitted to</li>
 *  <li><span class="maroon">form_name</span>
 *      - include a hidden field which specifies the {@link #name} of the Form </li>
 * </ul>
 * The hidden field is used by Click to determine which form was posted on
 * a page which may contain multiple forms.
 * <p/>
 * An example of a manually layed out Login form is provided below:
 *
 * <pre class="codeHtml">
 * &lt;form <span class="maroon">method</span>="<span class="blue">$form.post</span>" <span class="maroon">name</span>="<span class="blue">$form.name</span>" <span class="maroon">action</span>="<span class="blue">$request.requestURI</span>"&gt;
 *   &lt;input type="hidden" name="<span class="maroon">form_name</span>" value="<span class="blue">$form.name</span>"/&gt;
 *
 *   &lt;table style="margin: 1em;"&gt;
 *
 *     <span class="red">#if</span> (<span class="blue">$form.error</span>)
 *     &lt;tr&gt;
 *       &lt;td colspan="2" style="color: red;"&gt; <span class="blue">$form.error</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *     <span class="red">#end</span>
 *     <span class="red">#if</span> (<span class="blue">$form.fields.usernameField.error</span>)
 *     &lt;tr&gt;
 *       &lt;td colspan="2" style="color: red;"&gt; <span class="blue">$form.fields.usernameField.error</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *     <span class="red">#end</span>
 *     <span class="red">#if</span> (<span class="blue">$form.fields.passwordField.error</span>)
 *     &lt;tr&gt;
 *       &lt;td colspan="2" style="color: red;"&gt; <span class="blue">$form.fields.passwordField.error</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *     <span class="red">#end</span>
 *
 *     &lt;tr&gt;
 *       &lt;td&gt; Username: &lt;/td&gt;
 *       &lt;td&gt; <span class="blue">$form.fields.usernameField</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *     &lt;tr&gt;
 *       &lt;td&gt; Password: &lt;/td&gt;
 *       &lt;td&gt; <span class="blue">$form.fields.passwordField</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *
 *     &lt;tr&gt;
 *       &lt;td&gt;
 *         <span class="blue">$form.fields.okSubmit</span>
 *         <span class="blue">$form.fields.cancelSubmit</span>
 *       &lt;/td&gt;
 *     &lt;/tr&gt;
 *
 *   &lt;/table&gt;
 *
 * &lt;form&gt; </pre>
 *
 * As you can see in this example most of the code and markup is generic and
 * could be reused. This is where Velocity Macros come in.
 *
 * <a name="velocity-macros"><h4>Velocity Macros</h4></a>
 *
 * Velocity Macros
 * (<a target="topic" href="../../../../../velocity/user-guide.html#Velocimacros">velocimacros</a>)
 * are a great way to encapsulate customized forms.
 * <p/>
 * To create a generic form layout you can use the Form {@link #fieldList} and
 * {@link #buttonList} properties within a Velocity macro.
 * <p/>
 * The example below provides a generic <span class="green">writeForm()</span>
 * macro which you could use through out an application. This Velocity macro code
 * would be contained in a macro file, e.g. <tt>macro.vm</tt>.
 *
 * <pre class="codeHtml"> <span class="red">#*</span> Custom Form Macro Code <span class="red">*#</span>
 * <span class="red">#macro</span>( <span class="green">writeForm</span>[<span class="blue">$form</span>] )
 *
 * &lt;form method="<span class="blue">$form.post</span>" name="<span class="blue">$form.name</span>" action="<span class="blue">$request.requestURI</span>"&gt;
 *
 *  <span class="red">#foreach</span> (<span class="blue">$field</span> <span class="red">in</span> <span class="blue">$form.fieldList</span>)
 *    <span class="red">#if</span> (<span class="blue">$field.hidden</span>) <span class="blue">$field</span> <span class="red">#end</span>
 *  <span class="red">#end</span>
 *
 * &lt;table width="100%"&gt;
 *
 * <span class="red">#if</span> (<span class="blue">$form.error</span>)
 *   &lt;tr&gt;
 *     &lt;td colspan="2" style="color: red;"&gt; <span class="blue">$form.error</span> &lt;/td&gt;
 *   &lt;/tr&gt;
 * <span class="red">#end</span>
 *
 * <span class="red">#foreach</span> (<span class="blue">$field</span> <span class="red">in</span> <span class="blue">$form.fieldList</span>)
 *   <span class="red">#if</span> (!<span class="blue">$field.hidden</span>)
 *     <span class="red">#if</span> (!<span class="blue">$field.valid</span>)
 *     &lt;tr&gt;
 *       &lt;td colspan="2"&gt; <span class="blue">$field.error</span> &lt;/td&gt;
 *     &lt;/tr&gt;
 *     <span class="red">#end</span>
 *
 *   &lt;tr&gt;
 *     &lt;td&gt; <span class="blue">$field.label</span>: &lt;/td&gt;&lt;td&gt; <span class="blue">$field</span> &lt;/td&gt;
 *   &lt;/tr&gt;
 *   <span class="red">#end</span>
 * <span class="red">#end</span>
 *
 *  &lt;tr&gt;
 *    &lt;td colspan="2"&gt;
 *    <span class="red">#foreach</span> (<span class="blue">$button</span> <span class="red">in </span><span class="blue">$form.buttonList</span>)
 *      <span class="blue">$button</span> &amp;nbsp;
 *    <span class="red">#end</span>
 *    &lt;/td&gt;
 *  &lt;/tr&gt;
 *
 * &lt;/table&gt;
 * &lt;/form&gt;
 *
 * <span class="red">#end</span> </pre>
 *
 * You would then call this macro in your Page template passing it your
 * <span class="blue">form</span> object:
 *
 * <pre class="codeHtml"> <span class="red">#</span><span class="green">writeForm</span>(<span class="blue">$form</span>) </pre>
 *
 * At render time Velocity will execute the macro using the given form and render
 * the results to the response output stream.
 *
 * <h4>Configuring Macros</h4>
 *
 * To configure your application to use your macros you can:
 * <ul>
 *  <li>
 *   Put your macros if a file called <span class="st"><tt>macro.vm</tt></span>
 *   in your applications root directory.
 *  </li>
 *  <li>
 *   Put your macros in the auto deployed
 *   <span class="st"><tt>click/VM_global_macro.vm</tt></span> file.
 *  </li>
 *  <li>
 *   Create a custom named macro file and reference it in a
 *   <span class="st"><tt>WEB-INF/velocity.properties</tt></span>
 *   file under the property named
 *   <tt>velocimacro.library</tt>. See configuration topic
 *   <a target="topic" href="../../../../../configuration.html#velocity-properties">Velocity Properties</a>
 *   for more info.
 *  </li>
 * </ul>
 *
 * <p>&nbsp;<p/>
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

    // ------------------------------------------------------- Static Variables

    /**
     * The form name parameter for multiple forms: &nbsp; <tt>"form_name"</tt>
     */
    public static final String FORM_NAME = "form_name";

    /** The errors and labels on top form layout constant: &nbsp; <tt>"top"</tt> */
    public static final String TOP = "top";

    /** The errors in middle form layout constant: &nbsp; <tt>"middle"</tt> */
    public static final String MIDDLE = "middle";

    /** The errors on bottom form layout constant: &nbsp; <tt>"top"</tt> */
    public static final String BOTTOM = "bottom";

    /** The labels of left form layout contant: &nbsp; <tt>"left"</tt> */
    public static final String LEFT = "left";

    /** The HTTP content type header for multipart forms. */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /** The errors-header resource property */
    protected static String errorsHeader = "";

    /** The errors-footer resource property. */
    protected static String errorsFooter = "";

    /** The errors-prefix resource property. */
    protected static String errorsPrefix = "";

    /** The errors-suffix resource property. */
    protected static String errorsSuffix = "";

    /** The label-required-prefix resource property. */
    protected static String labelRequiredPrefix = "";

    /** The label-required-suffix resource property. */
    protected static String labelRequiredSuffix = "";

    protected static final String HTML_IMPORTS =
        "<link rel='stylesheet' type='text/css' href='$/click/control.css' title='style'>\n" +
        "<script type='text/javascript' src='$/click/control.js'></script>\n" +
        "<script type='text/javascript' src='$/click/calendar-en.js'></script>\n";

    static {
        ResourceBundle bundle =
            ResourceBundle.getBundle(Field.CONTROL_MESSAGES);

        errorsHeader = bundle.getString("errors-header");
        errorsFooter = bundle.getString("errors-footer");
        errorsPrefix = bundle.getString("errors-prefix");
        errorsSuffix = bundle.getString("errors-suffix");
        labelRequiredPrefix = bundle.getString("label-required-prefix");
        labelRequiredSuffix = bundle.getString("label-required-suffix");
    }

    // ----------------------------------------------------- Instance Variables

    /** The form attributes map. */
    protected Map attributes;

    /** The button align, default value is "<tt>left</tt>" */
    protected String buttonAlign = LEFT;

    /** The ordered list of button values. */
    protected final List buttonList = new ArrayList(5);

    /**
     * The number of form layout table columns, default value: <tt>1</tt>.
     * <p/>
     * This property is used to layout the number of table columns the form is
     * rendered in using a flow layout style.
     */
    protected int columns = 1;

    /** The form context. */
    protected Context context;

    /** The form disabled value. */
    protected boolean disabled;

    /** The form "enctype" attribute. */
    protected String enctype;

    /** The form level error message. */
    protected String error;

    /** The errors block align, default value is <tt>"left"</tt> */
    protected String errorsAlign = LEFT;

    /**
     * The form errors position <tt>["top", "middle", "bottom"]</tt> default value:
     * &nbsp; <tt>"middle"</tt>
     */
    protected String errorsPosition = MIDDLE;

    /** The ordered list of field values, excluding buttons */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The label align, default value is <tt>"left"</tt> */
    protected String labelAlign = LEFT;

    /**
     * The form labels position <tt>["left", "top"]</tt> default value: &nbsp; <tt>"left"</tt>
     */
    protected String labelsPosition = LEFT;

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

    /** The form is readonly flag. */
    protected boolean readonly;

    /** The form validate fields when processing flag. */
    protected boolean validate = true;

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

        HiddenField nameField = new HiddenField(FORM_NAME, String.class);
        nameField.setValue(name);
        add(nameField);
    }

    // ------------------------------------------------------ Public Attributes

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
        if (fields.containsKey(field.getName()) && !(field instanceof Label)) {
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
            System.err.println(field);
        }
    }

    /**
     * Remove the named field from the form.
     *
     * @param name the name of the field to remove from the form
     */
    public void removeField(String name) {
        remove(getField(name));
    }

    /**
     * Remove the list of named fields from the form.
     *
     * @param fieldNames the list of field names to remove from the form
     */
    public void removeFields(List fieldNames) {
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.size(); i++) {
                removeField(fieldNames.get(i).toString());
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
     * Return the form attributes Map.
     *
     * @return the form attributes Map.
     */
    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap(5);
        }
        return attributes;
    }

    /**
     * Return true if the form has attributes or false otherwise.
     *
     * @return true if the form has attributes on false otherwise
     */
    public boolean hasAttributes() {
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the buttons &lt;td&gt; HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>".
     *
     * @return the field label HTML horizontal alignment
     */
    public String getButtonAlign() {
        return buttonAlign;
    }

    /**
     * Set the button &lt;td&gt; HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>". Note the given align is not
     * validated.
     *
     * @param align the field label HTML horizontal alignment
     */
    public void setButtonAlign(String align) {
        buttonAlign = align;
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
     * Return true if the form is a disabled.
     *
     * @return true if the form is a disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Set the form disabled flag
     *
     * @param disabled the form disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the number of form layout table columns. This property is used
     * to layout the number of table columns the form is rendered in.
     *
     * @return the number of form layout table columns
     */
    public int getColumns() {
        return columns;
    }


    /**
     * Set the number of form layout table columns. This property is used
     * to layout the number of table columns the form is rendered in.
     *
     * @param columns the number of form layout table columns
     */
    public void setColumns(int columns) {
       this.columns = columns;
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
     * Return the form "enctype" attribute value, or null if not defined.
     *
     * @return the form "enctype" attribute value, or null if not defined
     */
    public String getEnctype() {
        if (enctype == null) {
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (!field.isHidden() && (field instanceof FileField)) {
                    enctype = MULTIPART_FORM_DATA;
                    break;
                }
            }
        }
        return enctype;
    }

    /**
     * Set the form "enctype" attribute value.
     *
     * @param enctype the form "enctype" attribute value, or null if not defined
     */
    public void setEnctype(String enctype) {
        this.enctype = enctype;
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
     * Return the errors block HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>".
     *
     * @return the errors block HTML horizontal alignment
     */
    public String getErrorsAlign() {
        return errorsAlign;
    }

    /**
     * Set the errors block HTML horizontal alignment: "<tt>left</tt>",
     * "<tt>center</tt>", "<tt>right</tt>". Note the given align is not
     * validated.
     *
     * @param align the errors block HTML horizontal alignment
     */
    public void setErrorsAlign(String align) {
        errorsAlign = align;
    }

    /**
     * Return the form errors position <tt>["top", "middle", "bottom"]</tt>.
     *
     * @return form errors position
     */
    public String getErrorsPosition() {
        return errorsPosition;
    }

    /**
     * Set the form errors position <tt>["top", "middle", "bottom"]</tt>.
     *
     * @param position the form errors position
     */
    public void setErrorsPosition(String position) {
        if (TOP.equals(position) ||
            MIDDLE.equals(position) ||
            BOTTOM.equals(position)) {

            errorsPosition = position;

        } else {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
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
     * Return the HTML head import statements for the CSS stylesheet
     * (<tt>click/control.css</tt>) and JavaScript (<tt>click/control.js</tt>)
     * files.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String path = context.getRequest().getContextPath();

        return StringUtils.replace(HTML_IMPORTS, "$", path);
    }

    /**
     * Return the "id" attribute value if defined, or the Form name otherwise.
     *
     * @see net.sf.click.Control#getId()
     */
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            return getName();
        }
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
     * Return the form labels position <tt>["left", "top"]</tt>.
     *
     * @return form labels position
     */
    public String getLabelsPosition() {
        return labelsPosition;
    }

    /**
     * Set the form labels position <tt>["left", "top"]</tt>.
     *
     * @param position the form labels position
     */
    public void setLabelsPosition(String position) {
        if (LEFT.equals(position) || TOP.equals(position)) {
            labelsPosition = position;
        } else {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
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
     * Return true if the form is a readonly.
     *
     * @return true if the form is a readonly
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Set the form readonly flag
     *
     * @param readonly the form readonly flag
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * Return true if the fields are valid and there is no form level error,
     * otherwise return false.
     *
     * @return true if the fields are valid and there is no form level error
     */
    public boolean isValid() {
        if (getError() != null) {
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
     * Return true if the Form's fields should validate themselves when being
     * processed.
     *
     * @return true if the form fields should perform validation when being
     * processed
     */
    public boolean getValidate() {
        return validate;
    }

    /**
     * Set the Form's field validation flag, telling the Fields to validate
     * themselves when their <tt>onProcess()</tt> method is invoked.
     *
     * @param validate the Form's field validation flag
     */
    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    // --------------------------------------------------------- Public Methods

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

            // If "multipart/form-data" request load form data FileItem into
            // context
            if (FileUploadBase.isMultipartContent(request)) {
                FileField fileField = null;
                for (int i = 0, size = fieldList.size(); i < size; i++) {
                    Field field = (Field) fieldList.get(i);
                    if (!field.isHidden() && (field instanceof FileField)) {
                        fileField = (FileField) field;
                        break;
                    }
                }

                FileUploadBase fileUpload = null;
                if (fileField != null) {
                    fileUpload = fileField.getFileUpload();
                    if (fileUpload.getFileItemFactory() == null) {
                        FileItemFactory fif = new DefaultFileItemFactory();
                        fileUpload.setFileItemFactory(fif);
                    }
                } else {
                    String msg = "No FileField defined for POST " +
                                 "Content-type 'multipart' request";
                    throw new RuntimeException(msg);
                }

                try {
                    List itemsList = fileUpload.parseRequest(request);

                    Map itemsMap = new HashMap(itemsList.size());
                    for (int i = 0; i < itemsList.size(); i++) {
                        FileItem fileItem = (FileItem) itemsList.get(i);
                        itemsMap.put(fileItem.getFieldName(), fileItem);
                    }

                    getContext().setMultiPartFormData(itemsMap);

                } catch (FileUploadException fue) {
                    throw new RuntimeException(fue);
                }
            }

            // If a form name is defined, but does not match this form exit.
            String formName = getContext().getRequestParameter(FORM_NAME);
            if (formName == null || !formName.equals(getName())) {
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
        int bufferSize =
            400 + (fieldList.size() * 350) + (buttonList.size() * 50);

        StringBuffer buffer = new StringBuffer(bufferSize);

        HttpServletRequest request = getContext().getRequest();
        HttpServletResponse response = getContext().getResponse();
        String actionURL = response.encodeURL(request.getRequestURI());

        buffer.append("<form method='");
        buffer.append(getMethod());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("' action='");
        buffer.append(actionURL);
        if (getEnctype() != null) {
            buffer.append("' enctype='");
            buffer.append(getEnctype());
        }
        buffer.append("'");
        if (hasAttributes()) {
            ClickUtils.renderAttributes(getAttributes(), buffer);
        }
        buffer.append(">\n");

        int hiddenCount = 0;
        Field fieldWithError = null;

        buffer.append("<table class='form' id='");
        buffer.append(getId());
        buffer.append("-form'>\n");

        // Render fields, errors and buttons
        if (TOP.equals(getErrorsPosition())) {
            fieldWithError = renderErrors(buffer, process);
            hiddenCount = renderFields(buffer);
            renderButtons(buffer);

        } else if (MIDDLE.equals(getErrorsPosition())) {
            hiddenCount = renderFields(buffer);
            fieldWithError = renderErrors(buffer, process);
            renderButtons(buffer);

        } else if (BOTTOM.equals(getErrorsPosition())) {
            hiddenCount = renderFields(buffer);
            renderButtons(buffer);
            fieldWithError = renderErrors(buffer, process);

        } else {
            String msg = "Invalid errorsPositon:" + getErrorsPosition();
            throw new IllegalArgumentException(msg);
        }
        buffer.append("</table>\n");

        // Render hidden fields
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

        // Set field focus
        if (fieldWithError != null) {
            buffer.append("<script type='text/javascript'><!--\n");
            buffer.append("document.forms['");
            buffer.append(getName());
            buffer.append("'].elements['");
            buffer.append(fieldWithError.getName());
            buffer.append("'].focus();\n");
            buffer.append("//--></script>\n");

        } else {
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (field.getFocus() && !field.isHidden() && !field.isDisabled()) {
                    buffer.append("<script type='text/javascript'><!--\n");
                    buffer.append("document.forms['");
                    buffer.append(getName());
                    buffer.append("'].elements['");
                    buffer.append(field.getName());
                    buffer.append("'].focus();\n");
                    buffer.append("//--></script>\n");
                    break;
                }
            }
        }

        return buffer.toString();
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the non hidden Form Fields to the string buffer and return a
     * count of hidden fields.
     *
     * @param buffer the StringBuffer to render to
     * @return the number of hidden Fields
     */
    protected int renderFields(StringBuffer buffer) {
        int hiddenCount = 0;

        buffer.append("<tr><td>\n");
        buffer.append("<table class='fields' id='");
        buffer.append(getId());
        buffer.append("-fields'>\n");

        int column = 1;

        for (int i = 0, size = fieldList.size(); i < size; i++) {

            Field field = (Field) fieldList.get(i);

            if (!field.isHidden()) {

                if (column == 1) {
                    buffer.append("<tr class='fields'>\n");
                }

                if (field instanceof Label) {
                    buffer.append("<td class='fields' colspan='2' align='");
                    buffer.append(getLabelAlign());
                    buffer.append("'");
                    if (field.hasAttributes()) {
                        ClickUtils.renderAttributes
                            (field.getAttributes(), buffer);
                    }
                    buffer.append(">");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else {
                    // Write out label
                    if (LEFT.equals(getLabelsPosition())) {
                        buffer.append("<td class='fields' align='");
                        buffer.append(getLabelAlign());
                        buffer.append("'>");
                    } else {
                        buffer.append("<td class='fields' valign='top'>");
                    }

                    if (field.isRequired()) {
                        buffer.append(labelRequiredPrefix);
                    }
                    buffer.append("<label");
                    buffer.append(field.getDisabled());
                    buffer.append(">");
                    buffer.append(field.getLabel());
                    buffer.append("</label>");
                    if (field.isRequired()){
                        buffer.append(labelRequiredSuffix);
                    }

                    if (LEFT.equals(getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align='left'>");
                    } else {
                        buffer.append("<br>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                }

                if (column == columns) {
                    buffer.append("</tr>\n");
                    column = 1;
                } else {
                    column++;
                }

            } else {
                hiddenCount++;
            }

        }
        buffer.append("</table>\n");
        buffer.append("</td></tr>\n");

        return hiddenCount;
    }

    /**
     * Render the form errors to the given buffer is form processed and
     * return the first field with an error if processed.
     *
     * @param buffer the string buffer to render the errors to
     * @param processed the flag indicating whether has been processed
     * @return the first field with an error if the form is being processed
     */
    protected Field renderErrors(StringBuffer buffer, boolean processed) {

        Field fieldWithError = null;
        if (processed && !isValid()) {

            String headerTest = errorsHeader.toLowerCase() +
                                errorsPrefix.toLowerCase();
            boolean useErrorsHeader =
                (((headerTest.indexOf("<ul") > -1) ||
                  (headerTest.indexOf("<ol") > -1)) &&
                  (headerTest.indexOf("<li") > -1));

            if (useErrorsHeader) {
                buffer.append(errorsHeader);
                buffer.append("\n");
            } else {
                buffer.append("<tr><td align='");
                buffer.append(getErrorsAlign());
                buffer.append("'>\n");
                buffer.append("<table class='errors' id='");
                buffer.append(getId());
                buffer.append("-errors'>\n");
            }

            if (getError() != null) {
                if (useErrorsHeader) {
                    buffer.append(errorsPrefix);
                } else {
                    buffer.append("<tr class='errors'>");
                    buffer.append("<td class='errors' align='");
                    buffer.append(getErrorsAlign());
                    buffer.append("'>\n");
                }
                buffer.append("<span class='error'>");
                buffer.append(getError());
                buffer.append("</span>\n");
                if (useErrorsHeader) {
                    buffer.append(errorsSuffix);
                    buffer.append("\n");
                } else {
                    buffer.append("</td></tr>\n");
                }
            }

            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (!field.isValid() && !field.isHidden()) {
                    if (fieldWithError == null && !field.isDisabled()) {
                        fieldWithError = field;
                    }
                    if (useErrorsHeader) {
                        buffer.append(errorsPrefix);
                    } else {
                        buffer.append("<tr class='errors'>");
                        buffer.append("<td class='errors' align='");
                        buffer.append(getErrorsAlign());
                        buffer.append("'>");
                    }

                    buffer.append("<a class='error'");
                    buffer.append(" href='javascript:document.");
                    buffer.append(getName());
                    buffer.append(".");
                    buffer.append(field.getName());
                    buffer.append(".focus();'>");
                    buffer.append(field.getError());
                    buffer.append("</a>");

                    if (useErrorsHeader) {
                        buffer.append(errorsSuffix);
                        buffer.append("\n");
                    } else {
                        buffer.append("</td></tr>\n");
                    }
                }
            }

            if (useErrorsHeader) {
                buffer.append(errorsFooter);
                buffer.append("\n");
            } else {
                buffer.append("</table>\n");
                buffer.append("</td></tr>\n");
            }
        }

        return fieldWithError;
    }

    /**
     * Render the Form Buttons to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderButtons(StringBuffer buffer) {
        if (!buttonList.isEmpty()) {
            buffer.append("<tr><td align='");
            buffer.append(getButtonAlign());
            buffer.append("'>\n");
            buffer.append("<table class='buttons' id='");
            buffer.append(getId());
            buffer.append("-buttons'>\n");
            buffer.append("<tr class='buttons'><td class='buttons'>");
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                buffer.append(button);
                if (i <= size - 1) {
                    buffer.append("&nbsp;");
                }
            }
            buffer.append("</td></tr>\n");
            buffer.append("</table>\n");
            buffer.append("</td></tr>\n");
        }
    }
}
