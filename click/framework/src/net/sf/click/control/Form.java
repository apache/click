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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
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
 *     <span class="kw">private</span> Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 *
 *     <span class="kw">public</span> Login() {
 *         form.add(<span class="kw">new</span> TextField(<span class="st">"username"</span>, <span class="kw">true</span>));
 *         form.add(<span class="kw">new</span> PasswordField(<span class="st">"password"</span>, <span class="kw">true</span>));
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>, <span class="kw">this</span>, <span class="st">"onOkClick"</span>));
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClick"</span>));
 *
 *         addControl(form);
 *     }
 *
 *     <span class="kw">public boolean</span> onOkClick() {
 *         <span class="kw">if</span> (form.isValid()) {
 *             User user = new User();
 *             form.copyTo(user);
 *
 *             <span class="kw">if</span> (UserDOA.isAuthenticatedUser(user)) {
 *                 getContext().setSessionAttribute(<span class="st">"user"</span>, user);
 *                 setRedirect(<span class="st">"home.htm"</span>);
 *             }
 *             <span class="kw">else</span> {
 *                 form.setError(getMessage(<span class="st">"authentication-error"</span>));
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
 * <h3>HTML Imports</h3>
 *
 * The Form control automatically deploys the control CSS style sheet
 * (<tt>control.css</tt>) and JavaScript file (<tt>control.js</tt>) to
 * the application directory <tt>/click</tt>.
 * To import these files and any form control imports simpley reference the
 * {@link net.sf.click.util.PageImports} object. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * <a name="form-layout"><h3>Data Binding</h3></a>
 *
 * To bind value objects to a forms fields use the copy methods:
 * <ul>
 * <li>data object &nbsp; -> &nbsp; form fields  &nbsp; &nbsp; &nbsp;
 * {@link #copyFrom(Object)}</li>
 * <li>form fields &nbsp; -> &nbsp; data object  &nbsp; &nbsp; &nbsp;
 * {@link #copyTo(Object)}</li>
 * </ul>
 * To debug the data binding being performed, use the Click application mode to
 * "<tt>debug</tt>" or use the debug copy methods.
 *
 * <a name="form-layout"><h3>Form Layout</h3></a>
 * The Form control supports rendering using automatic and manual layout
 * techniques.
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
 * <td>{@link #buttonStyle}</td> <td>button &lt;td&gt; "style" attribute value</td>
 * </tr><tr>
 * <td>{@link #columns}</td> <td>number of form table columns, the default value number is 1</td>
 * </tr><tr>
 * <td>{@link #errorsAlign}</td> <td>validation error messages alignment: &nbsp; <tt>["left", "center", "right"]</tt></td>
 * </tr><tr>
 * <td>{@link #errorsPosition}</td> <td>validation error messages position: &nbsp; <tt>["top", "middle", "bottom"]</tt></td>
 * </tr><tr>
 * <td>{@link #errorsStyle}</td> <td>errors &lt;td&gt; "style" attribute value</td>
 * </tr><tr>
 * <td>{@link #fieldStyle}</td> <td>field &lt;td&gt; "style" attribute value</td>
 * </tr><tr>
 * <td>{@link #labelAlign}</td> <td>field label alignment: &nbsp; <tt>["left", "center", "right"]</tt></td>
 * </tr><tr>
 * <td>{@link #labelsPosition}</td> <td>label position relative to field: &nbsp; <tt>["left", "top"]</tt></td>
 * </tr><tr>
 * <td>{@link #labelStyle}</td> <td>label &lt;td&gt; "style" attribute value</td>
 * </tr><tr>
 * <td>click/control.css</td> <td>control CSS styles, automatically deployed to the <tt>click</tt> web directory</td>
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
 * &lt;form <span class="maroon">method</span>="<span class="blue">$form.method</span>" <span class="maroon">name</span>="<span class="blue">$form.name</span>" <span class="maroon">action</span>="<span class="blue">$form.actionURL</span>"&gt;
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
 * &lt;form method="<span class="blue">$form.method</span>" name="<span class="blue">$form.name</span>" action="<span class="blue">$form.actionURL</span>"&gt;
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

    private static final long serialVersionUID = -8288409197675214969L;

    /** The align left, form layout contant: &nbsp; <tt>"left"</tt>. */
    public static final String ALIGN_LEFT = "left";

    /** The align center, form layout contant: &nbsp; <tt>"center"</tt>. */
    public static final String ALIGN_CENTER = "center";

    /** The align right, form layout contant: &nbsp; <tt>"right"</tt>. */
    public static final String ALIGN_RIGHT = "right";

    /**
     * The form name parameter for multiple forms: &nbsp; <tt>"form_name"</tt>.
     */
    public static final String FORM_NAME = "form_name";

    /** The HTTP content type header for multipart forms. */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * The position top, errors and labels form layout constant: &nbsp;
     * <tt>"top"</tt>.
     */
    public static final String POSITION_TOP = "top";

    /**
     * The position middle, errors in middle form layout constant: &nbsp;
     * <tt>"middle"</tt>.
     */
    public static final String POSITION_MIDDLE = "middle";

    /**
     * The position bottom, errors on bottom form layout constant: &nbsp;
     * <tt>"top"</tt>.
     */
    public static final String POSITION_BOTTOM = "bottom";

    /**
     * The position left, labels of left form layout contant: &nbsp;
     * <tt>"left"</tt>.
     */
    public static final String POSITION_LEFT = "left";

    /** The errors-header resource property. */
    protected static String ERRORS_HEADER = "";

    /** The errors-footer resource property. */
    protected static String ERRORS_FOOTER = "";

    /** The errors-prefix resource property. */
    protected static String ERRORS_PREFIX = "";

    /** The errors-suffix resource property. */
    protected static String ERRORS_SUFFIX = "";

    /** The label-required-prefix resource property. */
    protected static String LABEL_REQUIRED_PREFIX = "";

    /** The label-required-suffix resource property. */
    protected static String LABEL_REQUIRED_SUFFIX = "";

    /** The HTML imports statements. */
    protected static final String HTML_IMPORTS =
        "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/click/control.css\" title=\"style\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control.js\"></script>\n";

    /**
     * The submit check reserved request parameter prefix: &nbsp;
     * <tt>SUBMIT_CHECK_</tt>.
     */
    protected static final String SUBMIT_CHECK = "SUBMIT_CHECK_";

    static {
        ResourceBundle bundle =
            ResourceBundle.getBundle(Field.CONTROL_MESSAGES);

        ERRORS_HEADER = bundle.getString("errors-header");
        ERRORS_FOOTER = bundle.getString("errors-footer");
        ERRORS_PREFIX = bundle.getString("errors-prefix");
        ERRORS_SUFFIX = bundle.getString("errors-suffix");
        LABEL_REQUIRED_PREFIX = bundle.getString("label-required-prefix");
        LABEL_REQUIRED_SUFFIX = bundle.getString("label-required-suffix");
    }

    // ----------------------------------------------------- Instance Variables

    /** The form attributes map. */
    protected Map attributes;

    /** The button align, default value is "<tt>left</tt>". */
    protected String buttonAlign = ALIGN_LEFT;

    /** The ordered list of button values. */
    protected final List buttonList = new ArrayList(5);

    /** The button &lt;td&gt; "style" attribute value. */
    protected String buttonStyle;

    /**
     * The number of form layout table columns, default value: <tt>1</tt>.
     * <p/>
     * This property is used to layout the number of table columns the form
     * is rendered in using a flow layout style.
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

    /** The errors block align, default value is <tt>"left"</tt>. */
    protected String errorsAlign = ALIGN_LEFT;

    /**
     * The form errors position <tt>["top", "middle", "bottom"]</tt> default
     * value: &nbsp; <tt>"middle"</tt>.
     */
    protected String errorsPosition = POSITION_MIDDLE;

    /** The error &lt;td&gt; "style" attribute value. */
    protected String errorsStyle;

    /** The ordered list of field values, excluding buttons. */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

    /** The field &lt;td&gt; "style" attribute value. */
    protected String fieldStyle;

    /** The label align, default value is <tt>"left"</tt>. */
    protected String labelAlign = ALIGN_LEFT;

    /**
     * The form labels position <tt>["left", "top"]</tt> default value: &nbsp;
     * <tt>"left"</tt>.
     */
    protected String labelsPosition = POSITION_LEFT;

    /** The field required label labelprefix. */
    protected String labelRequiredPrefix;

    /** The field required label suffix. */
    protected String labelRequiredSuffix;

    /** The label &lt;td&gt; "style" attribute value. */
    protected String labelStyle;

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /**
     * The form method <tt>["POST, "GET"]</tt>, default value: &nbsp;
     * <tt>POST</tt>.
     */
    protected String method = "POST";

    /** The form name. */
    protected String name;

    /** The parent localized messages map. */
    protected Map parentMessages;

    /** The form is readonly flag. */
    protected boolean readonly;

    /** The form validate fields when processing flag. */
    protected boolean validate = true;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a form with the given name.
     *
     * @param name the name of the form
     * @throws IllegalArgumentException if the form name is null
     */
    public Form(String name) {
        setName(name);
    }

    /**
     * Create an form with no name or context defined, <b>please note</b> the
     * form's name and context must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;"> No-args constructors are
     * provided for Java Bean tools support and are not intended for general
     * use. If you create a control instance using a no-args constructor you
     * must define its name before adding it to its parent. </div>
     */
    public Form() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add the field to the form, and set the fields form property. The field
     * will be added to the {@link #fields} Map using its name.
     * <p/>
     * Button instances will also be added to the {@link #buttonList} while all
     * others field types will also be added to the {@link #fieldList}.
     *
     * @param field the field to add to the form
     * @throws IllegalArgumentException if the form already contains a field or
     *  button with the same name, or if the field name is not defined
     */
    public void add(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("field parameter cannot be null");
        }
        if (StringUtils.isBlank(field.getName())) {
            throw new IllegalArgumentException("Field name not defined");
        }
        if (getFields().containsKey(field.getName())
            && !(field instanceof Label)) {

            throw new IllegalArgumentException(
                    "Form already contains field named: " + field.getName());
        }

        if (field instanceof Button) {
            getButtonList().add(field);
        } else {
            getFieldList().add(field);
        }
        getFields().put(field.getName(), field);
        field.setForm(this);

        if (getContext() != null) {
            field.setContext(getContext());
        }
        if (getParentMessages() != null) {
            field.setParentMessages(getParentMessages());
        }
    }

    /**
     * Remove the given field from the form.
     *
     * @param field the field to remove from the form
     */
    public void remove(Field field) {
        if (field != null && getFields().containsKey(field.getName())) {
            field.setForm(null);
            getFields().remove(field.getName());
            if (field instanceof Button) {
                getButtonList().remove(field);
            } else {
                getFieldList().remove(field);
            }
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
     * Return the form "action" attribute URL value. The action URL will be
     * encode by the response to ensure it includes the Session ID if required.
     *
     * @return the form "action" attribute URL value.
     */
    public String getActionURL() {
        HttpServletRequest request = getContext().getRequest();
        HttpServletResponse response = getContext().getResponse();
        String actionURL = response.encodeURL(request.getRequestURI());
        return actionURL;
    }

    /**
     * Return the form HTML attribute with the given name, or null if the
     * attribute does not exist.
     *
     * @param name the name of form HTML attribute
     * @return the form HTML attribute
     */
    public String getAttribute(String name) {
        if (attributes != null) {
            return (String) attributes.get(name);
        } else {
            return null;
        }
    }

    /**
     * Set the form HTML attribute with the given attribute name and value.
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
     * @return the form attributes Map
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
     * "<tt>center</tt>", "<tt>right</tt>".
     * Note the given align is not validated.
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
     * Return the button &lt;td&gt; "style" attribute value.
     *
     * @return the button &lt;td&gt; "style" attribute value
     */
    public String getButtonStyle() {
        return buttonStyle;
    }

    /**
     * Set the button &lt;td&gt; "style" attribute value.
     *
     * @param value the button &lt;td&gt; "style" attribute value
     */
    public void setButtonStyle(String value) {
        this.buttonStyle = value;
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
     * Set the form disabled flag.
     *
     * @param disabled the form disabled flag
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Return the number of form layout table columns. This property is used to
     * layout the number of table columns the form is rendered in.
     *
     * @return the number of form layout table columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Set the number of form layout table columns. This property is used to
     * layout the number of table columns the form is rendered in.
     *
     * @param columns the number of form layout table columns
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * @see Control#getContext()
     *
     * @return the Page request Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see Control#setContext(Context)
     *
     * @param context the Page request Context
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        for (Iterator i = getFields().values().iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            field.setContext(context);
        }
    }

    /**
     * Return the form "enctype" attribute value, or null if not defined.
     *
     * @return the form "enctype" attribute value, or null if not defined
     */
    public String getEnctype() {
        if (enctype == null) {
            List fieldList = ClickUtils.getFormFields(this);
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
     * "<tt>center</tt>", "<tt>right</tt>".
     * Note the given align is not validated.
     *
     * @param align the errors block HTML horizontal alignment
     */
    public void setErrorsAlign(String align) {
        errorsAlign = align;
    }

    /**
     * Return a list of form fields which are not valid, not hidden and not
     * disabled.
     *
     * @return list of form fields which are not valid, not hidden and not
     *  disabled
     */
    public List getErrorFields() {
        List list = new ArrayList();

        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);

            if (field instanceof FieldSet) {
                FieldSet fieldSet = (FieldSet) field;

                for (int j = 0; j < fieldSet.getFieldList().size(); j++) {
                    Field fieldSetField =
                        (Field) fieldSet.getFieldList().get(j);

                    if (!fieldSetField.isValid()
                        && !fieldSetField.isHidden()
                        && !fieldSetField.isDisabled()) {

                        list.add(fieldSetField);
                    }
                }

            } else if (!field.isValid()
                       && !field.isHidden()
                       && !field.isDisabled()) {

                list.add(field);
            }
        }

        return list;
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
        if (POSITION_TOP.equals(position)
            || POSITION_MIDDLE.equals(position)
            || POSITION_BOTTOM.equals(position)) {

            errorsPosition = position;

        } else {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    /**
     * Return the error &lt;td&gt; "style" attribute value.
     *
     * @return the error &lt;td&gt; "style" attribute value
     */
    public String getErrorsStyle() {
        return errorsStyle;
    }

    /**
     * Set the errors &lt;td&gt; "style" attribute value.
     *
     * @param value the errors &lt;td&gt; "style" attribute value
     */
    public void setErrorsStyle(String value) {
        this.errorsStyle = value;
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
            for (Iterator i = getFields().values().iterator(); i.hasNext();) {
                field = (Field) i.next();
                if (field instanceof FieldSet) {
                    FieldSet fieldSet = (FieldSet) field;
                    if (fieldSet.getField(name) != null) {
                        return fieldSet.getField(name).getValue();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Return the field &lt;td&gt; "style" attribute value.
     *
     * @return the field &lt;td&gt; "style" attribute value
     */
    public String getFieldStyle() {
        return fieldStyle;
    }

    /**
     * Set the field &lt;td&gt; "style" attribute value.
     *
     * @param value the field &lt;td&gt; "style" attribute value
     */
    public void setFieldStyle(String value) {
        this.fieldStyle = value;
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet
     * (<tt>click/control.css</tt>) and JavaScript
     * (<tt>click/control.js</tt>) files.
     *
     * @see Control#getHtmlImports()
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        String[] args = { getContext().getRequest().getContextPath() };

        return MessageFormat.format(HTML_IMPORTS, args);
    }

    /**
     * Return the HTML head imports for the form and all its controls.
     *
     * {@link Control#getHtmlImports()}
     *
     * @return all the HTML head imports for the form and all its controls
     */
    public String getHtmlImportsAll() {
        StringBuffer buffer = new StringBuffer(200);

        buffer.append(getHtmlImports());

        Set includeSet = null;

        List list = ClickUtils.getFormFields(this);
        for (int i = 0, size = list.size(); i < size; i++) {
            if (includeSet == null) {
                includeSet = new HashSet();
            }

            Field field = (Field) list.get(i);

            String include = field.getHtmlImports();
            if (!includeSet.contains(include)) {
                if (include != null) {
                    buffer.append(include);
                    includeSet.add(include);
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Return the "id" attribute value if defined, or the Form name otherwise.
     *
     * @see net.sf.click.Control#getId()
     *
     * @return HTML element identifier attribute "id" value
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
     * "<tt>center</tt>", "<tt>right</tt>".
     * Note the given align is not validated.
     *
     * @param align
     *            the field label HTML horizontal alignment
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
        if (POSITION_LEFT.equals(position) || POSITION_TOP.equals(position)) {
            labelsPosition = position;
        } else {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    /**
     * Return the field required label prefix. If the value is null it will
     * be initialized with the <tt>label-required-prefix</tt> resource property.
     *
     * @return the field required label prefix
     */
    public String getLabelRequiredPrefix() {
        if (labelRequiredPrefix == null) {
            labelRequiredPrefix = LABEL_REQUIRED_PREFIX;
        }
        return labelRequiredPrefix;
    }

    /**
     * Set the field required label prefix.
     *
     * @param value the field required label prefix
     */
    public void setLabelRequiredPrefix(String value) {
        this.labelRequiredPrefix = value;
    }

    /**
     * Return the field required label suffix. If the value is null it will
     * be initialized with the <tt>label-required-suffix</tt> resource property.
     *
     * @return the field required label suffix
     */
    public String getLabelRequiredSuffix() {
        if (labelRequiredSuffix == null) {
            labelRequiredSuffix = LABEL_REQUIRED_SUFFIX;
        }
        return labelRequiredSuffix;
    }

    /**
     * Set the field required label suffix.
     *
     * @param value the field required label suffix
     */
    public void setLabelRequiredSuffix(String value) {
        this.labelRequiredSuffix = value;
    }

    /**
     * Return the label &lt;td&gt; "style" attribute value.
     *
     * @return the label &lt;td&gt; "style" attribute value
     */
    public String getLabelStyle() {
        return labelStyle;
    }

    /**
     * Set the label &lt;td&gt; "style" attribute value.
     *
     * @param value the label &lt;td&gt; "style" attribute value
     */
    public void setLabelStyle(String value) {
        this.labelStyle = value;
    }

    /**
     * The callback listener will only be called during processing if the field
     * value is valid. If the field has validation errors the listener will not
     * be called.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method) {
        this.listener = listener;
        this.listenerMethod = method;
    }

    /**
     * Return the form method <tt>["POST" | "GET"]</tt>.
     *
     * @return the form method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the form method <tt>["POST" | "GET"]</tt>.
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
     *
     * @return the name of the control
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the form.
     *
     * @see net.sf.click.Control#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }
        this.name = name;

        HiddenField nameField = (HiddenField) getField(FORM_NAME);
        if (nameField == null) {
            nameField = new HiddenField(FORM_NAME, String.class);
            add(nameField);
        }
        nameField.setValue(name);
    }

    /**
     * @see Control#getParentMessages()
     *
     * @return the localization <tt>Map</tt> of the Control's parent
     */
    public Map getParentMessages() {
        return parentMessages;
    }

    /**
     * @see Control#setParentMessages(Map)
     *
     * @param messages the parent's the localized messages <tt>Map</tt>
     */
    public void setParentMessages(Map messages) {
        parentMessages = messages;
        for (Iterator i = getFields().values().iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            field.setParentMessages(parentMessages);
        }
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
     * Set the form readonly flag.
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

        for (Iterator i = getFields().values().iterator(); i.hasNext();) {
            Field field = (Field) i.next();
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
     *  processed
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
     * Copy the given object's attributes into the Form's field values. In other
     * words automatically populate Forms field values with the given objects
     * attributes.
     * <p/>
     * The following example populates the Form field with Customer objects
     * attributes:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onGet() {
     *     Long customerId = ..
     *     Customer customer = CustomerDAO.findByPK(customerId);
     *     form.copyFrom(customer);
     *  }
     * </pre>
     *
     * @param object the object to obtain attribute values from
     * @throws IllegalArgumentException if the object parameter is null
     */
    public void copyFrom(Object object) {
        ClickUtils.copyObjectToForm(object, this, false);
    }

    /**
     * Copy the given object's attributes into the Form's field values. In other
     * words automatically populate Forms field values with the given objects
     * attributes. If the debug parameter is true, debugging messages will be
     * logged.
     *
     * @param object the object to obtain attribute values from
     * @param debug log debug statements when populating the form
     * @throws IllegalArgumentException if the object parameter is null
     */
    public void copyFrom(Object object, boolean debug) {
        ClickUtils.copyObjectToForm(object, this, debug);
    }

    /**
     * Copy the Form's field values into the given object's attributes. In other
     * words automatically populate Object attributes with the Forms field
     * values.
     * <p/>
     * The following example populates the Customer object atributes with the
     * Form's field values:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onPost() {
     *      <span class="kw">if</span> (form.isValid()) {
     *         Customer customer = <span class="kw">new</span> Customer();
     *         form.copyTo(customer);
     *         ..
     *      }
     *      <span class="kw">return true</span>;
     *  }
     * </pre>
     *
     * @param object the object to populate with field values
     * @throws IllegalArgumentException if the object parameter is null
     */
    public void copyTo(Object object) {
        ClickUtils.copyFormToObject(this, object, false);
    }

    /**
     * Copy the Form's field values into the given object's attributes. In other
     * words automatically populate Object attributes with the Forms field
     * values. If the debug parameter is true, debugging messages will be
     * logged.
     *
     * @param object the object to populate with field values
     * @param debug log debug statements when populating the object
     * @throws IllegalArgumentException if the object parameter is null
     */
    public void copyTo(Object object, boolean debug) {
        ClickUtils.copyFormToObject(this, object, debug);
    }

    /**
     * This method does nothing.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     *
     * @param servletContext the servlet context
     * @throws IOException if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
    }

    /**
     * Process the Form when the request method is the same as the Form's
     * method. The default Form method is "POST".
     * <p/>
     * The Forms processing order is:
     * <ol>
     * <li>All {@link Field} controls in the order they were added</li>
     * <li>All {@link Button} controls in the order they were added</li>
     * <li>Invoke the Forms listener if defined</li>
     * </ol>
     * <p/>
     * If the request is a Content-type <tt>"multipart"</tt> POST (i.e. a
     * file upload request), then the form will determine whether the multi part
     * data has been loaded into the request <tt>Context</tt>. &nbsp; If not
     * loaded, the Form will search for a contained <tt>FileUpload</tt>
     * control and use this control to process the <tt>"multipart"</tt> data.
     * This data will then be loaded into the context using
     * {@link Context#setMultiPartFormData(Map)}.
     *
     * @see Context#getRequestParameter(String)
     * @see Context#getMultiPartFormData()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {
        HttpServletRequest request = getContext().getRequest();

        if (request.getMethod().equalsIgnoreCase(getMethod())) {

            // If "multipart/form-data" request and not already loaded then
            // load form data FileItem into context
            if (getContext().isMultipartRequest()
                && getContext().getMultiPartFormData() == Collections.EMPTY_MAP) {

                FileField fileField = null;
                List fieldList = ClickUtils.getFormFields(this);
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
                        FileItemFactory fif = new DiskFileItemFactory();
                        fileUpload.setFileItemFactory(fif);
                    }
                } else {
                    String msg = "No FileField defined for POST "
                                 + "Content-type 'multipart' request";
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
            for (int i = 0, size = getFieldList().size(); i < size; i++) {
                Field field = (Field) getFieldList().get(i);
                if (!field.getName().startsWith(SUBMIT_CHECK)) {
                    continueProcessing = field.onProcess();
                    if (!continueProcessing) {
                        return false;
                    }
                }
            }
            for (int i = 0, size = getButtonList().size(); i < size; i++) {
                Button button = (Button) getButtonList().get(i);
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
     * Perform a form submission check ensuring the user has not replayed the
     * form submission by using the browser back button. If the form submit
     * is valid this method will return true, otherwise set the page to
     * redirect to the given redirectPath and return false.
     * <p/>
     * This method will add a token to the user's session and a hidden field
     * to the form to validate future submits.
     * <p/>
     * Form submit checks should be performed before the pages controls are
     * processed in the Page onSecurityCheck method. For example:
     *
     * <pre class="codeJava">
     * <span class="kw">public class</span> Order <span class="kw">extends</span> Page {
     *     ..
     *
     *     <span class="kw">public boolean</span> onSecurityCheck() {
     *         <span class="kw">return</span> form.onSubmitCheck(<span class="kw">this</span>, <span class="st">"/invalid-submit.html"</span>);
     *     }
     * } </pre>
     *
     * Form submit checks should generally be combined with the Post-Redirect
     * pattern which provides a better user experience when pages are refreshed.
     *
     * @param page the page invoking the Form submit check
     * @param redirectPath the path to redirect invalid submissions to
     * @return true if the form submit is OK or false otherwise
     */
    public boolean onSubmitCheck(Page page, String redirectPath) {
        if (page == null) {
            throw new IllegalArgumentException("Null page parameter");
        }
        if (redirectPath == null) {
            throw new IllegalArgumentException("Null redirectPath parameter");
        }

        if (performSubmitCheck()) {
            return true;

        } else {
            page.setRedirect(redirectPath);

            return false;
        }
    }

    /**
     * Perform a form submission check ensuring the user has not replayed the
     * form submission by using the browser back button. If the form submit
     * is valid this method will return true, otherwise the given listener
     * object and method will be invoked.
     * <p/>
     * This method will add a token to the users session and a hidden field
     * to the form to validate future submit's.
     * <p/>
     * Form submit checks should be performed before the pages controls are
     * processed in the Page onSecurityCheck method. For example:
     *
     * <pre class="codeJava">
     * <span class="kw">public class</span> Order <span class="kw">extends</span> Page {
     *     ..
     *
     *     <span class="kw">public boolean</span> onSecurityCheck() {
     *         <span class="kw">return</span> form.onSubmitCheck(<span class="kw">this</span>, <span class="st">"onInvalidSubmit"</span>);
     *     }
     *
     *     <span class="kw">public boolean</span> onInvalidSubmit() {
     *        getContext().setRequestAttribute(<span class="st">"invalidPath"</span>, getPath());
     *        setForward(<span class="st">"invalid-submit.htm"</span>);
     *        <span class="kw">return false</span>;
     *     }
     * } </pre>
     *
     * Form submit checks should generally be combined with the Post-Redirect
     * pattern which provides a better user experience when pages are refreshed.
     *
     * @param submitListener the listener object to call when an invalid submit
     *      occurs
     * @param submitListenerMethod the listener method to invoke when an
     *      invalid submit occurs
     * @return true if the form submit is valid, or the return value of the
     *      listener method otherwise
     */
    public boolean onSubmitCheck(Object submitListener,
            String submitListenerMethod) {

        if (submitListener == null) {
            throw new IllegalArgumentException("Null submitListener parameter");
        }
        if (submitListenerMethod == null) {
            String msg = "Null submitListenerMethod parameter";
            throw new IllegalArgumentException(msg);
        }

        if (performSubmitCheck()) {
            return true;

        } else {
            return ClickUtils.invokeListener(submitListener, submitListenerMethod);
        }
    }

    /**
     * Return the HTML string representation of the form.
     * <p/>
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @return the HTML string representation of the form
     */
    public String toString() {
        final boolean process =
            context.getRequest().getMethod().equalsIgnoreCase(getMethod());

        // Estimate the size of the string buffer
        int bufferSize =
            400 + (getFieldList().size() * 350) + (getButtonList().size() * 50);

        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        buffer.elementStart("form");

        buffer.appendAttribute("method", getMethod());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("action", getActionURL());
        buffer.appendAttribute("enctype", getEnctype());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        buffer.closeTag();
        buffer.append("\n");

        int hiddenCount = 0;
        Field fieldWithError = null;

        buffer.append("<table class=\"form\" id=\"");
        buffer.append(getId());
        buffer.append("-form\">\n");

        // Render fields, errors and buttons
        if (POSITION_TOP.equals(getErrorsPosition())) {
            fieldWithError = renderErrors(buffer, process);
            hiddenCount = renderFields(buffer);
            renderButtons(buffer);

        } else if (POSITION_MIDDLE.equals(getErrorsPosition())) {
            hiddenCount = renderFields(buffer);
            fieldWithError = renderErrors(buffer, process);
            renderButtons(buffer);

        } else if (POSITION_BOTTOM.equals(getErrorsPosition())) {
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
            for (int i = 0, size = getFieldList().size(); i < size; i++) {
                Field field = (Field) getFieldList().get(i);
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

        buffer.elementEnd("form");
        buffer.append("\n");

        // Set field focus
        if (fieldWithError != null) {
            buffer.append("<script type=\"text/javascript\"><!--\n");
            buffer.append("document.forms['");
            buffer.append(getName());
            buffer.append("'].elements['");
            buffer.append(fieldWithError.getName());
            buffer.append("'].focus();\n");
            buffer.append("//--></script>\n");

        } else {
            for (int i = 0, size = getFieldList().size(); i < size; i++) {
                Field field = (Field) getFieldList().get(i);

                if (field instanceof FieldSet) {
                    FieldSet fieldSet = (FieldSet) field;
                    for (int j = 0; j < fieldSet.getFieldList().size(); j++) {
                        Field fieldSetField =
                            (Field) fieldSet.getFieldList().get(j);

                        if (fieldSetField.getFocus()
                            && !fieldSetField.isHidden()
                            && !fieldSetField.isDisabled()) {

                            buffer.append("<script type=\"text/javascript\"><!--\n");
                            buffer.append("document.forms['");
                            buffer.append(getName());
                            buffer.append("'].elements['");
                            buffer.append(fieldSetField.getName());
                            buffer.append("'].focus();\n");
                            buffer.append("//--></script>\n");
                            break;
                        }
                    }

                } else if (field.getFocus()
                           && !field.isHidden()
                           && !field.isDisabled()) {

                    buffer.append("<script type=\"text/javascript\"><!--\n");
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
     * Perform a back button submit check, returning true if the request is
     * valid or false otherwise. This method will add a submit check token
     * to the form as a hidden field, and to the session.
     *
     * @return true if the submit is OK or false otherwise
     */
    protected boolean performSubmitCheck() {

        final HttpServletRequest request = getContext().getRequest();
        final String submitTokenName =
            SUBMIT_CHECK + getContext().getResourcePath();

        boolean isValidSubmit = true;

        // If not this form exit
        String formName = getContext().getRequestParameter(FORM_NAME);

        // Only test if submit for this form
        if (!getContext().isForward()
            && request.getMethod().equalsIgnoreCase(getMethod())
            && getName().equals(formName)) {

            Long sessionTime =
                (Long) getContext().getSessionAttribute(submitTokenName);

            if (sessionTime != null) {
                String value = request.getParameter(submitTokenName);
                Long formTime = Long.valueOf(value);
                isValidSubmit = formTime.equals(sessionTime);
            }
        }

        // Save state info to form and session
        final Long time = new Long(System.currentTimeMillis());
        HiddenField field = new HiddenField(submitTokenName, Long.class);
        field.setValueObject(time);
        add(field);

        getContext().setSessionAttribute(submitTokenName, time);

        if (isValidSubmit) {
            return true;

        } else {
            return false;
        }
    }

    /**
     * Render the non hidden Form Fields to the string buffer and return a count
     * of hidden fields.
     *
     * @param buffer the StringBuffer to render to
     * @return the number of hidden Fields
     */
    protected int renderFields(HtmlStringBuffer buffer) {
        if (getFieldList().size() == 1 && getFields().containsKey("form_name")) {
            return 1;
        }

        int hiddenCount = 0;

        buffer.append("<tr><td>\n");
        buffer.append("<table class=\"fields\" id=\"");
        buffer.append(getId());
        buffer.append("-fields\">\n");

        int column = 1;

        for (int i = 0, size = getFieldList().size(); i < size; i++) {

            Field field = (Field) getFieldList().get(i);

            if (!field.isHidden()) {

                if (column == 1) {
                    buffer.append("<tr class=\"fields\">\n");
                }

                if (field instanceof FieldSet) {
                    buffer.append("<td class=\"fields\" colspan=\"2\" align=\"");
                    buffer.append(getLabelAlign());
                    buffer.append("\">\n");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else if (field instanceof Label) {
                    buffer.append("<td class=\"fields\" colspan=\"2\" align=\"");
                    buffer.append(getLabelAlign());
                    buffer.append("\"");
                    if (field.hasAttributes()) {
                        buffer.appendAttributes(field.getAttributes());
                    }
                    buffer.append(">");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else {
                    // Write out label
                    if (POSITION_LEFT.equals(getLabelsPosition())) {
                        buffer.append("<td class=\"fields\"");
                        buffer.appendAttribute("align", getLabelAlign());
                        buffer.appendAttribute("style", getLabelStyle());
                        buffer.append(">");
                    } else {
                        buffer.append("<td class=\"fields\" valign=\"top\"");
                        buffer.appendAttribute("style", getLabelStyle());
                        buffer.append(">");
                    }

                    if (field.isRequired()) {
                        buffer.append(getLabelRequiredPrefix());
                    }
                    buffer.append("<label");
                    if (field.isDisabled()) {
                        buffer.append(" disabled=\"disabled\"");
                    }
                    if (field.getError() != null) {
                        buffer.append(" class=\"error\"");
                    }
                    buffer.append(">");
                    buffer.append(field.getLabel());
                    buffer.append("</label>");
                    if (field.isRequired()) {
                        buffer.append(getLabelRequiredSuffix());
                    }

                    if (POSITION_LEFT.equals(getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align=\"left\"");
                        buffer.appendAttribute("style", getFieldStyle());
                        buffer.append(">");
                    } else {
                        buffer.append("<br>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                }

                if (column == getColumns()) {
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
     * Render the form errors to the given buffer is form processed and return
     * the first field with an error if processed.
     *
     * @param buffer the string buffer to render the errors to
     * @param processed the flag indicating whether has been processed
     * @return the first field with an error if the form is being processed
     */
    protected Field renderErrors(HtmlStringBuffer buffer, boolean processed) {

        Field fieldWithError = null;
        if (processed && !isValid()) {

            String headerTest =
                ERRORS_HEADER.toLowerCase()  + ERRORS_PREFIX.toLowerCase();

            boolean useErrorsHeader =
                (((headerTest.indexOf("<ul") > -1)
                 || (headerTest.indexOf("<ol") > -1))
                 && (headerTest.indexOf("<li") > -1));

            if (useErrorsHeader) {
                buffer.append(ERRORS_HEADER);
                buffer.append("\n");
            } else {
                buffer.append("<tr><td align=\"");
                buffer.append(getErrorsAlign());
                buffer.append("\">\n");
                buffer.append("<table class=\"errors\" id=\"");
                buffer.append(getId());
                buffer.append("-errors\">\n");
            }

            if (getError() != null) {
                if (useErrorsHeader) {
                    buffer.append(ERRORS_PREFIX);
                } else {
                    buffer.append("<tr class=\"errors\">");
                    buffer.append("<td class=\"errors\"");
                    buffer.appendAttribute("align", getErrorsAlign());
                    buffer.appendAttribute("style", getErrorsStyle());
                    buffer.append(">\n");
                }
                buffer.append("<span class=\"error\">");
                buffer.append(getError());
                buffer.append("</span>\n");
                if (useErrorsHeader) {
                    buffer.append(ERRORS_SUFFIX);
                    buffer.append("\n");
                } else {
                    buffer.append("</td></tr>\n");
                }
            }

            List errorFieldList = getErrorFields();

            for (int i = 0, size = errorFieldList.size(); i < size; i++) {
                Field field = (Field) errorFieldList.get(i);

                if (fieldWithError == null && !field.isDisabled()) {
                    fieldWithError = field;
                }
                if (useErrorsHeader) {
                    buffer.append(ERRORS_PREFIX);
                } else {
                    buffer.append("<tr class=\"errors\">");
                    buffer.append("<td class=\"errors\"");
                    buffer.appendAttribute("align", getErrorsAlign());
                    buffer.appendAttribute("style", getErrorsStyle());
                    buffer.append(">");
                }

                buffer.append("<a class=\"error\"");
                buffer.append(" href=\"javascript:document.");
                buffer.append(getName());
                buffer.append(".");
                buffer.append(field.getName());
                buffer.append(".focus();\">");
                buffer.append(field.getError());
                buffer.append("</a>");

                if (useErrorsHeader) {
                    buffer.append(ERRORS_SUFFIX);
                    buffer.append("\n");
                } else {
                    buffer.append("</td></tr>\n");
                }
            }

            if (useErrorsHeader) {
                buffer.append(ERRORS_FOOTER);
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
    protected void renderButtons(HtmlStringBuffer buffer) {
        if (!buttonList.isEmpty()) {
            buffer.append("<tr><td");
            buffer.appendAttribute("align", getButtonAlign());
            buffer.append(">\n");
            buffer.append("<table class=\"buttons\" id=\"");
            buffer.append(getId());
            buffer.append("-buttons\">\n");
            buffer.append("<tr class=\"buttons\"><td class=\"buttons\"");
            buffer.appendAttribute("style", getButtonStyle());
            buffer.closeTag();
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                Button button = (Button) buttonList.get(i);
                buffer.append(button);
                if (i <= size - 1) {
                    buffer.append(" ");
                }
            }
            buffer.append("</td></tr>\n");
            buffer.append("</table>\n");
            buffer.append("</td></tr>\n");
        }
    }

}
