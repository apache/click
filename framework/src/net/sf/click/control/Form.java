/*
 * Copyright 2004-2008 Malcolm A. Edgar
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import net.sf.click.Control;
import net.sf.click.service.FileUploadService;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ContainerUtils;
import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a Form control: &nbsp; &lt;form method='post'&gt;.
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
 *
 * <h3>Form Example</h3>
 *
 * The example below illustrates a Form being used in a login Page.
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> Login <span class="kw">extends</span> Page {
 *
 *     <span class="kw">public</span> Form form = <span class="kw">new</span> Form();
 *
 *     <span class="kw">public</span> Login() {
 *         form.add(<span class="kw">new</span> TextField(<span class="st">"username"</span>, <span class="kw">true</span>));
 *         form.add(<span class="kw">new</span> PasswordField(<span class="st">"password"</span>, <span class="kw">true</span>));
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">"  OK  "</span>, <span class="kw">this</span>, <span class="st">"onOkClick"</span>));
 *         form.add(<span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClick"</span>));
 *     }
 *
 *     <span class="kw">public boolean</span> onOkClick() {
 *         <span class="kw">if</span> (form.isValid()) {
 *             User user = new User();
 *             form.copyTo(user);
 *
 *             <span class="kw">if</span> (getUserService().isAuthenticatedUser(user)) {
 *                 getContext().setSessionAttribute(<span class="st">"user"</span>, user);
 *                 setRedirect(HomePage.<span class="kw">class</span>);
 *             }
 *             <span class="kw">else</span> {
 *                 form.setError(getMessage(<span class="st">"authentication-error"</span>));
 *             }
 *         }
 *         <span class="kw">return true</span>;
 *     }
 *
 *     <span class="kw">public boolean</span> onCancelClick() {
 *         setRedirect(WelcomePage.<span class="kw">class</span>);
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
 * any of its Fields have validation errors they will be automatically
 * rendered, and the {@link #isValid()} method will return false.
 *
 * <a name="data-binding"><h3>Data Binding</h3></a>
 *
 * To bind value objects to a forms fields use the copy methods:
 * <ul>
 * <li>value object &nbsp; -> &nbsp; form fields  &nbsp; &nbsp; &nbsp;
 * {@link #copyFrom(Object)}</li>
 * <li>form fields &nbsp; -> &nbsp; value object  &nbsp; &nbsp; &nbsp;
 * {@link #copyTo(Object)}</li>
 * </ul>
 * To debug the data binding being performed, use the Click application mode to
 * "<tt>debug</tt>" or use the debug copy methods.
 * <p/>
 * Binding of nested data objects is supported using the
 * <a target="blank" href="http://www.ognl.org">OGNL</a> library. To use
 * nested objects in your form, simply specify the object path as the Field
 * name. Note in the object path you exclude the root object, so the path
 * <tt>customer.address.state</tt> is specified as <tt>address.state</tt>.
 * <p/>
 * For example:
 *
 * <pre class="codeJava">
 * <span class="cm">// The customer.address.state field</span>
 * TextField stateField = <span class="kw">new</span> TextField(<span class="st">"address.state"</span>);
 * form.add(stateField);
 * ..
 *
 * <span class="cm">// Loads the customer address state into the form stateField</span>
 * Customer customer = getCustomer();
 * form.copyFrom(customer);
 * ..
 *
 * <span class="cm">// Copies form stateField value into the customer address state</span>
 * Customer customer = <span class="kw">new</span> Customer();
 * form.copyTo(customer); </pre>
 *
 * When populating an object from a form post Click will automatically create
 * any null nested objects so their properties can be set. To do this Click
 * uses the no-args constructor of the nested objects class.
 * <p/>
 * {@link #copyTo(Object)} and {@link #copyFrom(Object)} also supports
 * <tt>java.util.Map</tt> as an argument. Examples of using
 * <tt>java.util.Map</tt> are shown in the respective method descriptions.
 *
 * <a name="form-validation"><h3>Form Validation</h3></a>
 *
 * The Form control supports automatic field validation. By default when a POST
 * request is made the form will validate the field values. To disable
 * automatic validation set {@link #setValidate(boolean)} to false.
 * <p/>
 * <b>JavaScript Validation</b>
 * <p/>
 * The Form control also supports client side JavaScript validation. By default
 * JavaScript validation is not enabled. To enable JavaScript validation set
 * {@link #setJavaScriptValidation(boolean)} to true. For example:
 *
 * <pre class="codeJava">
 * Form form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
 * form.setJavaScriptValidation(<span class="kw">true</span>);
 *
 * <span class="cm">// Add form fields</span>
 * ..
 *
 * form.add(<span class="kw">new</span> Submit(<span class="st">"ok"</span>, <span class="st">" OK "</span>, <span class="kw">this</span>, <span class="st">"onOkClicked"</span>);
 *
 * Submit cancel = <span class="kw">new</span> Submit(<span class="st">"cancel"</span>, <span class="st">"Cancel"</span>, <span class="kw">this</span>, <span class="st">"onCancelClicked"</span>);
 * cancel.setCancelJavaScriptValidation(<span class="kw">true</span>);
 *
 * addControl(form); </pre>
 *
 * Please note in that is this example the cancel submit button has
 * {@link Submit#setCancelJavaScriptValidation(boolean)} set to true. This
 * prevents JavaScript form validation being performed the cancel button is clicked.
 *
 * <h3>CSS and JavaScript Imports</h3>
 *
 * The Form control automatically deploys the control CSS style sheet
 * (<tt>control.css</tt>) and JavaScript file (<tt>control.js</tt>) to
 * the application directory <tt>/click</tt>.
 * To import these files and any form control imports simply reference
 * <span class="blue">$cssImports</span> and <span class="blue">$jsImports</span>
 * in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$cssImports</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$form</span>
 * &lt;/body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsImports</span></pre>
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
 * $form.{@link #getFields fields}.usernameField </pre>
 *
 * Whenever including your own Form markup in a page template or Velocity macro
 * always specify:
 * <ul style="margin-top: 0.5em;">
 *  <li><span class="maroon">method</span>
 *      - the form submission method <tt>["post" | "get"]</tt></li>
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
 * Alternatively you can use the Form {@link #startTag()} and {@link #endTag()}
 * methods to render this information.
 * <p/>
 * An example of a manually layed out Login form is provided below:
 *
 * <pre class="codeHtml">
 * <span class="blue">$form.startTag()</span>
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
 * <span class="blue">$form.endTag()</span> </pre>
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
 * To create a generic form layout you can use the Form {@link #getFieldList()} and
 * {@link #getButtonList()} properties within a Velocity macro.
 * <p/>
 * The example below provides a generic <span class="green">writeForm()</span>
 * macro which you could use through out an application. This Velocity macro code
 * would be contained in a macro file, e.g. <tt>macro.vm</tt>.
 *
 * <pre class="codeHtml"> <span class="red">#*</span> Custom Form Macro Code <span class="red">*#</span>
 * <span class="red">#macro</span>( <span class="green">writeForm</span>[<span class="blue">$form</span>] )
 *
 * <span class="blue">$form.startTag()</span>
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
 *
 * <span class="blue">$form.endTag()</span>
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
 * <h3>Preventing Accidental Form Posts</h3>
 *
 * Users may accidentally make multiple form submissions by refreshing a page
 * or by pressing the back button.
 * <p/>
 * To prevent multiple form posts from page refreshes use the Post
 * Redirect pattern. With this pattern once the user has posted a form you
 * redirect to another page. If the user then presses the refresh button, they
 * will making a GET request on the current page. Please see the
 * <a target="blank" href="http://www.theserverside.com/articles/content/RedirectAfterPost/article.html">Redirect After Post</a>
 * article for more information on this topic.
 * <p/>
 * To prevent multiple form posts from use of the browser back button use one
 * of the Form {@link #onSubmitCheck(Page, String)} methods. For example:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> Purchase <span class="kw">extends</span> Page {
 *     ..
 *
 *     <span class="kw">public boolean</span> onSecurityCheck() {
 *         <span class="kw">return</span> form.onSubmitCheck(<span class="kw">this</span>, <span class="st">"/invalid-submit.html"</span>);
 *     }
 * } </pre>
 *
 * The form submit check methods store a special token in the users session
 * and in a hidden field in the form to ensure a form post isn't replayed.
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
public class Form extends BasicForm {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The align left, form layout constant: &nbsp; <tt>"left"</tt>. */
    public static final String ALIGN_LEFT = "left";

    /** The align center, form layout constant: &nbsp; <tt>"center"</tt>. */
    public static final String ALIGN_CENTER = "center";

    /** The align right, form layout constant: &nbsp; <tt>"right"</tt>. */
    public static final String ALIGN_RIGHT = "right";

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
     * The position left, labels of left form layout constant: &nbsp;
     * <tt>"left"</tt>.
     */
    public static final String POSITION_LEFT = "left";

    /** The Form set field focus JavaScript. */
    protected static final String FOCUS_JAVASCRIPT =
        "<script type=\"text/javascript\"><!--\n"
        + "var field = document.getElementById('$id');\n"
        + "if (field && field.focus && field.type != 'hidden' && field.disabled != true) { field.focus(); };\n"
        + "//--></script>\n";

    // ----------------------------------------------------- Instance Variables

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
     * is rendered with using a flow layout style.
     */
    protected int columns = 1;

    /**
     * The default field size, default value: <tt>0</tt>.
     * <p/>
     * If the form default field size is greater than 0, when fields are added
     * to the form the field's size will be set to the default value.
     */
    protected int defaultFieldSize;

    /** The errors block align, default value is <tt>"left"</tt>. */
    protected String errorsAlign = ALIGN_LEFT;

    /**
     * The form errors position <tt>["top", "middle", "bottom"]</tt> default
     * value: &nbsp; <tt>"top"</tt>.
     */
    protected String errorsPosition = POSITION_TOP;

    /** The error &lt;td&gt; "style" attribute value. */
    protected String errorsStyle;

    /** The field &lt;td&gt; "style" attribute value. */
    protected String fieldStyle;

    /** The map of field width values. */
    protected Map fieldWidths = new HashMap();

    /**
     * The JavaScript client side form fields validation flag. By default
     * JavaScript validation is not enabled.
     */
    protected boolean javaScriptValidation;

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

    /** The field not required label labelprefix. */
    protected String labelNotRequiredPrefix;

    /** The field not required label suffix. */
    protected String labelNotRequiredSuffix;

    /** The label &lt;td&gt; "style" attribute value. */
    protected String labelStyle;

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
     * Create a form with no name.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public Form() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Add a Field or FieldSet to the container and return the added instance.
     * <p/>
     * <b>Please note</b> only {@link Field} and {@link FieldSet} instances can
     * be added to a Form. Trying to add any other control will throw an
     * exception. This restriction allows Form to automatically layout its
     * controls. If you want to add other controls such as tables,
     * rather use {@link BasicForm}.
     * <p/>
     * The Fields inside a FieldSet will be laid out by the Form.
     * <p/>
     * Fields will be added to {@link #getFields() fields} using their name.
     * <p/>
     * Buttons will be added to {@link #getButtonList() buttonList} while
     * all others field types will be added to {@link #getFieldList() fieldList}.
     *
     * @see Container#add(net.sf.click.Control)
     *
     * @param control the control to add to the container and return
     * @return the control that was added to the container
     * @throws IllegalArgumentException if the control is null, the Field's name
     * is not defined, the container already contains a control with the same
     * name, if the control's parent is a Page or if the control is neither a
     * Field nor FieldSet
     */
    public Control add(Control control) {
         if (control == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }
        if (control instanceof Field) {
            Field field = (Field) control;
            if (StringUtils.isBlank(field.getName())) {
               String msg = "Field name not defined: " + field.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
            if (getControlMap().containsKey(field.getName())
                && !(field instanceof Label)) {

                throw new IllegalArgumentException(
                    "Form already contains field named: " + field.getName());
            }

            if (field instanceof Button) {
                getButtonList().add(field);
            } else {
                getControls().add(field);
            }

            getControlMap().put(field.getName(), field);

            field.setForm(this);

            field.setParent(this);

            if (getDefaultFieldSize() > 0) {
                if (field instanceof TextField) {
                    ((TextField) field).setSize(getDefaultFieldSize());

                } else if (field instanceof FileField) {
                    ((FileField) field).setSize(getDefaultFieldSize());

                } else if (field instanceof TextArea) {
                    ((TextArea) field).setCols(getDefaultFieldSize());
                }
            }
        } else if (control instanceof FieldSet) {
            FieldSet fieldSet = (FieldSet) control;
            super.add(getControls().size(), fieldSet);
            fieldSet.setForm(this);
        } else {
            throw new IllegalArgumentException("Only fields and FieldSets are"
                + " allowed on this Form");
        }

        return control;
    }

    /**
     * This method is not supported by Form.
     *
     * @param index the index at which the control is to be inserted
     * @param control the control to add to the container
     * @return the control that was added to the container
     * @throws UnsupportedOperationException if invoked
     */
    public Control add(int index, Control control) {
        throw new UnsupportedOperationException("This method is not supported"
            + " by Form. Please use add(Control) instead.");
    }

    /**
     * Add the field to the form, and set the fields form property. The field
     * will be added to {@link #getFields() fields} using its name.
     * <p/>
     * Button instances will be add to {@link #getButtonList() buttonList} while
     * all others field types will be added to the
     * {@link #getFieldList() fieldList}.
     *
     * @see #add(net.sf.click.Control)
     *
     * @param field the field to add to the form
     * @return the field added to this form
     * @throws IllegalArgumentException if the field is null, the field name
     * is not defined, the form already contains a control with the same name
     * or if the field's parent is a Page
     */
    public Field add(Field field) {
        add((Control) field);
        return field;
    }

    /**
     * Add the field to the form and specify the field's width in columns. The
     * field will be added to {@link #getFields() fields} using its name.
     * <p/>
     * Note Button or HiddenFields types are not valid arguments for this method.
     *
     * @param field the field to add to the form
     * @param width the width of the field in table columns
     * @return the field added to this form
     * @throws IllegalArgumentException if the field is null, field's name is
     * not defined, field is a Button or HiddenField, the form already contains
     * a control with the same name, if the field's parent is a Page or the
     * width &lt; 1
     */
    public Field add(Field field, int width) {
        if (field == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }
        if (field instanceof Button || field instanceof HiddenField) {
            String msg = "Not valid a valid field type: " + field.getClass().getName();
            throw new IllegalArgumentException(msg);
        }
        if (width < 1) {
            throw new IllegalArgumentException("Invalid field width: " + width);
        }

        add(field);
        getFieldWidths().put(field.getName(), new Integer(width));
        return field;
    }

    /**
     * Add the fieldSet to the form and specify the fieldSet's width in columns.
     * <p/>
     *
     * @param fieldSet the fieldSet to add to the form
     * @param width the width of the fieldSet in table columns
     * @return the fieldSet added to this form
     * @throws IllegalArgumentException if the fieldSet is null, the form
     * already contains a control with the same name, if the fieldSet's parent
     * is a Page or the width &lt;
     */
    public FieldSet add(FieldSet fieldSet, int width) {
        if (fieldSet == null) {
            throw new IllegalArgumentException("FieldSet parameter cannot be null");
        }
        if (width < 1) {
            throw new IllegalArgumentException("Invalid field width: " + width);
        }

        add(fieldSet);

        if (fieldSet.getName() != null) {
            getFieldWidths().put(fieldSet.getName(), new Integer(width));
        }
        return fieldSet;
    }

    /**
     * @see Container#remove(net.sf.click.Control)
     *
     * @param control the control to remove from the container
     * @return true if the control was removed from the container
     * @throws IllegalArgumentException if the control is null
     */
    public boolean remove(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }

        if (control instanceof Field) {
            Field field = (Field) control;

            boolean contains = false;

            if (getControlMap().containsKey(field.getName())) {
                field.setForm(null);
                if (field.getParent() == this) {
                    field.setParent(null);
                }

                getControlMap().remove(field.getName());
                getFieldWidths().remove(field.getName());

                if (field instanceof Button) {
                    contains = getButtonList().remove(field);
                } else {
                    contains = getControls().remove(field);
                }
            }

            return contains;
        } else if (control instanceof FieldSet) {
            FieldSet fieldSet = (FieldSet) control;
            boolean contains = super.remove(fieldSet);

            fieldSet.setForm(null);

            return contains;
        } else {
            return false;
        }
    }

    /**
     * Remove the named field from the form.
     *
     * @param name the name of the field to remove from the form
     * @throws IllegalArgumentException if the field is null
     */
    public void removeField(String name) {
        remove(getField(name));
    }

    /**
     * Remove the list of named fields from the form.
     *
     * @param fieldNames the list of field names to remove from the form
     * @throws IllegalArgumentException if any of the fields is null
     */
    public void removeFields(List fieldNames) {
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.size(); i++) {
                removeField(fieldNames.get(i).toString());
            }
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
     * <p/>
     * The order of the buttons is the same order they were added to the form.
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
     * Return the number of form layout table columns. This property is used to
     * layout the number of table columns the form is rendered with.
     *
     * @return the number of form layout table columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Set the number of form layout table columns. This property is used to
     * layout the number of table columns the form is rendered with.
     *
     * @param columns the number of form layout table columns
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * Return the form default field size. If the form default field size is
     * greater than 0, when fields are added to the form the field's size will
     * be set to the default value.
     *
     * @return the form default field size
     */
    public int getDefaultFieldSize() {
        return defaultFieldSize;
    }

    /**
     * Return the form default field size. If the form default field size is
     * greater than 0, when fields are added to the form the field's size will
     * be set to the default value.
     *
     * @param size the default field size
     */
    public void setDefaultFieldSize(int size) {
        this.defaultFieldSize = size;
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
     * Return the named field if contained in the form or one of the form's
     * fieldsets, or null if not found.
     *
     * @param name the name of the field
     * @return the named field if contained in the form
     * @throws IllegalStateException if a non-field control is found with the
     * specified name
     */
    public Field getField(String name) {
        return super.getField(name);
    }

    /**
     * Return the ordered list of form {@link Field}s as well as any
     * {@link FieldSet} containers.
     * <p/>
     * The order of the fields is the same order they were added to the form.
     *
     * @return the ordered List of form fields
     */
    public List getFieldList() {
        return getControls();
    }

    /**
     * Return a Map of form fields and fieldsets, keyed on field name.
     *
     * @return the Map of form fields and fieldsets, keyed on field name
     */
    public Map getFields() {
        return getControlMap();
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
     * Return the map of field width values, keyed on field name.
     *
     * @return the map of field width values, keyed on field name
     */
    public Map getFieldWidths() {
        return fieldWidths;
    }

    /**
     * Return the HTML head imports for the form and all its controls.
     *
     * {@link net.sf.click.Control#getHtmlImports()}
     *
     * @deprecated this method is not very useful and has been deprecated
     *
     * @return all the HTML head imports for the form and all its controls
     */
    public String getHtmlImportsAll() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(200);

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
     * Return true if JavaScript client side form validation is enabled.
     *
     * @return true if JavaScript client side form validation is enabled
     */
    public boolean getJavaScriptValidation() {
        return javaScriptValidation;
    }

    /**
     * Set the JavaScript client side form validation flag.
     *
     * @param validate the JavaScript client side validation flag
     */
    public void setJavaScriptValidation(boolean validate) {
        javaScriptValidation = validate;
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
        if (POSITION_LEFT.equals(position) || POSITION_TOP.equals(position)) {
            labelsPosition = position;
        } else {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
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
        super.setListener(listener, method);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * @see BasicForm#copyFrom(java.lang.Object)
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
     * attributes. copyFrom also supports <tt>java.util.Map</tt> as an argument.
     * <p/>
     * If the debug parameter is true, debugging messages will be
     * logged.
     *
     * @see BasicForm#copyFrom(java.lang.Object)
     *
     * @param object the object to obtain attribute values from
     * @param debug log debug statements when populating the form
     * @throws IllegalArgumentException if the object parameter is null
     */
    public void copyFrom(Object object, boolean debug) {
        ClickUtils.copyObjectToForm(object, this, debug);
    }

    /**
     * @see BasicForm#copyTo(java.lang.Object)
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
     * values. copyTo also supports <tt>java.util.Map</tt> as an argument.
     * <p/>
     * If the debug parameter is true, debugging messages will be
     * logged.
     *
     * @see BasicForm#copyTo(java.lang.Object)
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
     */
    public void onDeploy(ServletContext servletContext) {
    }

    /**
     * Initialize the fields, fieldSet and buttons contained in the Form.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        super.onInit();

        for (int i = 0, size = getButtonList().size(); i < size; i++) {
            Button button = (Button) getButtonList().get(i);
            button.onInit();
        }
    }

    /**
     * Process the Form when the request method is the same as the Form's
     * method. The default Form method is "post".
     * <p/>
     * The Forms processing order is:
     * <ol>
     * <li>All {@link Field} controls in the order they were added</li>
     * <li>All {@link Button} controls in the order they were added</li>
     * <li>Invoke the Forms listener if defined</li>
     * </ol>
     *
     * @see net.sf.click.Context#getRequestParameter(String)
     * @see net.sf.click.Context#getFileItemMap()
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess() {

        if (getValidate()) {
            validate();

            // If a POST error occurred exit early.
            if (hasPostError()) {
                // Remove exception to ensure other forms on Page do not
                // validate twice for same error.
                getContext().getRequest().removeAttribute(
                    FileUploadService.UPLOAD_EXCEPTION);

                return true;
            }
        }

        if (isFormSubmission()) {

            boolean continueProcessing = true;
            for (Iterator it = getControls().iterator(); it.hasNext();) {
                Control control = (Control) it.next();
                if (control.getName() != null
                    && !control.getName().startsWith(SUBMIT_CHECK)) {

                    continueProcessing = control.onProcess();
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

            return invokeListener();
        }

        return true;
    }

    /**
     * Perform any pre rendering logic.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        super.onRender();

        for (int i = 0, size = getButtonList().size(); i < size; i++) {
            Button button = (Button) getButtonList().get(i);
            button.onRender();
        }
    }

    /**
     * Destroy the fields and buttons contained in the Form and clear any form
     * error message.
     *
     * @see net.sf.click.Control#onDestroy()
     */
    public void onDestroy() {
        super.onDestroy();

        for (int i = 0, size = getButtonList().size(); i < size; i++) {
            Button button = (Button) getButtonList().get(i);
            try {
                button.onDestroy();
            } catch (Throwable t) {
                ClickUtils.getLogService().error("onDestroy error", t);
            }
        }

        setError(null);
    }

    /**
     * Return the rendered opening form tag and all the forms hidden fields.
     *
     * @return the rendered form start tag and the forms hidden fields
     */
    public String startTag() {
        List formFields = ClickUtils.getFormFields(this);

        int bufferSize = getFormSizeEst(formFields);

        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

        renderHeader(buffer, formFields);

        return buffer.toString();
    }

    /**
     * Return the rendered form end tag and JavaScript for field focus
     * and validation.
     *
     * @return the rendered form end tag
     */
    public String endTag() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        List formFields = ClickUtils.getFormFields(this);

        renderTagEnd(formFields, buffer);

        return buffer.toString();
    }

    /**
     * Convenience method that allows the form to render the specified fieldSet,
     * and layout its fields.
     *
     * @param buffer the buffer to render to
     * @param fieldSet the fieldSet to render
     */
    public void renderFieldSet(HtmlStringBuffer buffer, FieldSet fieldSet) {
        if (fieldSet.getShowBorder()) {
            // Render the FieldSet
            buffer.elementStart(fieldSet.getTag());

            String id = fieldSet.getId();

            if (id != null) {
                buffer.appendAttribute("id", id);
            }

            fieldSet.appendAttributes(buffer);

            buffer.closeTag();
            buffer.append("\n");

            if (fieldSet.getLegend().length() > 0) {
                buffer.elementStart("legend");
                if (fieldSet.hasLegendAttributes()) {
                    Object legendId = fieldSet.getLegendAttributes().get("id");
                    if (legendId != null) {
                        buffer.appendAttribute("id", legendId);
                    }
                    buffer.appendAttributes(fieldSet.getLegendAttributes());
                }

                buffer.closeTag();
                buffer.append(fieldSet.getLegend());
                buffer.elementEnd("legend");
                buffer.append("\n");
            }
        }

        renderFieldSetFields(buffer, fieldSet);

        if (fieldSet.getShowBorder()) {
            buffer.elementEnd(fieldSet.getTag());
            buffer.append("\n");
        }
    }

    /**
     * Render the HTML representation of the Form.
     * <p/>
     * If the form contains errors after processing, these errors will be
     * rendered.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        final boolean process =
            getContext().getRequest().getMethod().equalsIgnoreCase(getMethod());

        List formFields = ClickUtils.getFormFields(this);

        renderHeader(buffer, formFields);

        buffer.append("<table class=\"form\" id=\"");
        buffer.append(getId());
        buffer.append("-form\">\n");

        // Render fields, errors and buttons
        if (POSITION_TOP.equals(getErrorsPosition())) {
            renderErrors(buffer, process);
            renderFields(buffer);
            renderButtons(buffer);

        } else if (POSITION_MIDDLE.equals(getErrorsPosition())) {
            renderFields(buffer);
            renderErrors(buffer, process);
            renderButtons(buffer);

        } else if (POSITION_BOTTOM.equals(getErrorsPosition())) {
            renderFields(buffer);
            renderButtons(buffer);
            renderErrors(buffer, process);

        } else {
            String msg = "Invalid errorsPositon:" + getErrorsPosition();
            throw new IllegalArgumentException(msg);
        }

        buffer.append("</table>\n");

        renderTagEnd(formFields, buffer);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the estimated rendered form size in characters.
     *
     * @param formFields the list of form fields
     * @return the estimated rendered form size in characters
     */
    protected int getFormSizeEst(List formFields) {
        return 400 + (formFields.size() * 350) + (getButtonList().size() * 50);
    }

    /**
     * Render the given form start tag and the form hidden fields to the given
     * buffer.
     *
     * @param buffer the HTML string buffer to render to
     * @param formFields the list of form fields
     */
    protected void renderHeader(HtmlStringBuffer buffer, List formFields) {

        buffer.elementStart(getTag());

        buffer.appendAttribute("method", getMethod());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("action", getActionURL());
        buffer.appendAttribute("enctype", getEnctype());

        appendAttributes(buffer);

        if (getJavaScriptValidation()) {
            String javaScript = "return on_" + getId() + "_submit();";
            buffer.appendAttribute("onsubmit", javaScript);
        }
        buffer.closeTag();
        buffer.append("\n");

        // Render hidden fields
        for (int i = 0, size = formFields.size(); i < size; i++) {
            Field field = (Field) formFields.get(i);
            if (field.isHidden()) {
                field.render(buffer);
                buffer.append("\n");
            }
        }
    }

    /**
     * Render the non hidden Form Fields to the string buffer.
     * <p/>
     * This method delegates the rendering of the form fields to
     * {@link #renderControls(net.sf.click.util.HtmlStringBuffer, net.sf.click.control.AbstractContainer, java.util.List, java.util.Map)}.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderFields(HtmlStringBuffer buffer) {

        // If Form contains only HiddenField, exit early
        if (getControls().size() == 1) {

            // getControlMap is cheaper than getFields, so check that first
            if (getControlMap().containsKey("form_name")) {
                return;
            } else if (ContainerUtils.getFieldMap(this).containsKey("form_name")) {
                return;
            }
        }

        buffer.append("<tr><td>\n");
        renderControls(buffer, this, getControls(), getFieldWidths());
        buffer.append("</td></tr>\n");
    }

    /**
     * Render the specified controls of the container to the string buffer.
     * <p/>
     * fieldWidths is a map specifying the width for specific fields contained
     * in the list of controls. The fieldWidths map is keyed on field name.
     *
     * @param buffer the StringBuffer to render to
     * @param container the container which controls to render
     * @param controls the controls to render
     * @param fieldWidths a map of field widths keyed on field name
     */
    protected void renderControls(HtmlStringBuffer buffer,
        AbstractContainer container, List controls, Map fieldWidths) {

        buffer.append("<table class=\"fields\"");
        String containerId = container.getId();
        if (containerId != null) {
            buffer.appendAttribute("id", containerId + "-fields");
        }
        buffer.append(">\n");

        int column = 1;
        boolean openTableRow = false;

        for (int i = 0, size = controls.size(); i < size; i++) {

            Control control = (Control) controls.get(i);

            if (!isHidden(control)) {

                // Control width
                Integer width = (Integer) fieldWidths.get(control.getName());

                if (column == 1) {
                    buffer.append("<tr class=\"fields\">\n");
                    openTableRow = true;
                }

                if (control instanceof Label) {
                    Label label = (Label) control;
                    buffer.append("<td class=\"fields\" align=\"");
                    buffer.append(getLabelAlign());
                    buffer.append("\"");

                    if (width != null) {
                        int colspan = (width.intValue() * 2);
                        buffer.appendAttribute("colspan", colspan);
                    } else {
                        buffer.appendAttribute("colspan", 2);
                    }

                    if (label.hasAttributes()) {
                        //Temporarily remove the style attribute
                        String tempStyle = null;
                        if (label.hasAttribute("style")) {
                            tempStyle = label.getAttribute("style");
                            label.setAttribute("style", null);
                        }
                        buffer.appendAttributes(label.getAttributes());

                        //Put style back in attribute map
                        if (tempStyle != null) {
                            label.setAttribute("style", tempStyle);
                        }
                    }
                    buffer.append(">");
                    label.render(buffer);
                    buffer.append("</td>\n");

                } else if (control instanceof FieldSet) {
                    FieldSet fieldSet = (FieldSet) control;
                    buffer.append("<td class=\"fields\"");

                    if (width != null) {
                        int colspan = (width.intValue() * 2);
                        buffer.appendAttribute("colspan", colspan);
                    } else {
                        buffer.appendAttribute("colspan", 2);
                    }
                    buffer.append(">\n");

                    renderFieldSet(buffer, fieldSet);

                    buffer.append("</td>\n");
                } else {
                    Field field = (Field) control;
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
                        buffer.append(getMessage("label-required-prefix"));
                    } else {
                        buffer.append(getMessage("label-not-required-prefix"));
                    }
                    buffer.elementStart("label");
                    buffer.appendAttribute("for", field.getId());
                    if (field.isDisabled()) {
                        buffer.appendAttributeDisabled();
                    }
                    if (field.getError() != null) {
                        buffer.appendAttribute("class", "error");
                    }
                    buffer.closeTag();
                    buffer.append(field.getLabel());
                    buffer.elementEnd("label");
                    if (field.isRequired()) {
                        buffer.append(getMessage("label-required-suffix"));
                    } else {
                        buffer.append(getMessage("label-not-required-suffix"));
                    }

                    if (POSITION_LEFT.equals(getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align=\"left\"");
                        buffer.appendAttribute("style", getFieldStyle());

                        if (width != null) {
                            int colspan = (width.intValue() * 2) - 1;
                            buffer.appendAttribute("colspan", colspan);
                        }

                        buffer.append(">");
                    } else {
                        buffer.append("<br/>");
                    }

                    // Write out field
                    field.render(buffer);
                    buffer.append("</td>\n");
                }

                if (width != null) {
                    if (control instanceof Label || control instanceof FieldSet) {
                        column += width.intValue();

                    } else {
                        column += (width.intValue() - 1);
                    }
                }

                if (column >= getColumns()) {
                    buffer.append("</tr>\n");
                    openTableRow = false;
                    column = 1;

                } else {
                    column++;
                }
            }
        }

        if (openTableRow) {
            buffer.append("</tr>\n");
        }

        buffer.append("</table>\n");
    }

    /**
     * Render the form errors to the given buffer is form processed.
     *
     * @param buffer the string buffer to render the errors to
     * @param processed the flag indicating whether has been processed
     */
    protected void renderErrors(HtmlStringBuffer buffer, boolean processed) {

        if (processed && !isValid()) {

            buffer.append("<tr><td align=\"");
            buffer.append(getErrorsAlign());
            buffer.append("\">\n");
            buffer.append("<table class=\"errors\" id=\"");
            buffer.append(getId());
            buffer.append("-errors\">\n");

            if (getError() != null) {
                buffer.append("<tr class=\"errors\">");
                buffer.append("<td class=\"errors\"");
                buffer.appendAttribute("align", getErrorsAlign());
                buffer.appendAttribute("colspan", getColumns() * 2);
                buffer.appendAttribute("style", getErrorsStyle());
                buffer.append(">\n");
                buffer.append("<span class=\"error\">");
                buffer.append(getError());
                buffer.append("</span>\n");
                buffer.append("</td></tr>\n");
            }

            List errorFieldList = getErrorFields();

            for (int i = 0, size = errorFieldList.size(); i < size; i++) {
                Field field = (Field) errorFieldList.get(i);

                // Certain fields (FieldSet) might be invalid because
                // one of their contained fields are invalid. However these
                // controls might not have an error message to display.
                // If field error message is null don't render.
                if (field.getError() == null) {
                    continue;
                }

                buffer.append("<tr class=\"errors\">");
                buffer.append("<td class=\"errors\"");
                buffer.appendAttribute("align", getErrorsAlign());
                buffer.appendAttribute("colspan", getColumns() * 2);
                buffer.appendAttribute("style", getErrorsStyle());
                buffer.append(">");

                buffer.append("<a class=\"error\"");
                buffer.append(" href=\"javascript:");
                buffer.append(field.getFocusJavaScript());
                buffer.append("\">");
                buffer.append(field.getError());
                buffer.append("</a>");
                buffer.append("</td></tr>\n");
            }

            buffer.append("</table>\n");
            buffer.append("</td></tr>\n");
        }

        if (getValidate() && getJavaScriptValidation()) {
            buffer.append("<tr style=\"display:none\" id=\"");
            buffer.append(getId());
            buffer.append("-errorsTr\"><td width='100%' align=\"");
            buffer.append(getErrorsAlign());
            buffer.append("\">\n");
            buffer.append("<div class=\"errors\" id=\"");
            buffer.append(getId());
            buffer.append("-errorsDiv\"/>\n");
            buffer.append("</td></tr>\n");
        }
    }

    /**
     * Render the Form Buttons to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderButtons(HtmlStringBuffer buffer) {

        if (!getButtonList().isEmpty()) {
            buffer.append("<tr><td");
            buffer.appendAttribute("align", getButtonAlign());
            buffer.append(">\n");

            buffer.append("<table class=\"buttons\" id=\"");
            buffer.append(getId());
            buffer.append("-buttons\">\n");
            buffer.append("<tr class=\"buttons\">");

            for (int i = 0, size = getButtonList().size(); i < size; i++) {
                buffer.append("<td class=\"buttons\"");
                buffer.appendAttribute("style", getButtonStyle());
                buffer.closeTag();

                Button button = (Button) getButtonList().get(i);
                button.render(buffer);

                buffer.append("</td>");
            }

            buffer.append("</tr>\n");
            buffer.append("</table>\n");
            buffer.append("</td></tr>\n");
        }
    }

    /**
     * Close the form tag and render any additional content after the Form.
     * <p/>
     * Additional content includes <tt>javascript validation</tt> and
     * <tt>javascript focus</tt> scripts.
     *
     * @param formFields all fields contained within the form
     * @param buffer the buffer to render to
     */
    protected void renderTagEnd(List formFields, HtmlStringBuffer buffer) {

        buffer.elementEnd(getTag());
        buffer.append("\n");

        renderFocusJavaScript(buffer, formFields);

        renderValidationJavaScript(buffer, formFields);
    }

    /**
     * Render the Form field focus JavaScript to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     * @param formFields the list of form fields
     */
    protected void renderFocusJavaScript(HtmlStringBuffer buffer, List formFields) {

        // Set field focus
        boolean errorFieldFound = false;
        for (int i = 0, size = formFields.size(); i < size; i++) {
            Field field = (Field) formFields.get(i);

            if (field.getError() != null
                && !field.isHidden()
                && !field.isDisabled()) {

                String focusJavaScript =
                    StringUtils.replace(FOCUS_JAVASCRIPT,
                                        "$id",
                                        field.getId());
                buffer.append(focusJavaScript);
                errorFieldFound = true;
                break;
            }
        }

        if (!errorFieldFound) {
            for (int i = 0, size = formFields.size(); i < size; i++) {
                Field field = (Field) formFields.get(i);

                if (field.getFocus()
                    && !field.isHidden()
                    && !field.isDisabled()) {

                    String focusJavaScript =
                        StringUtils.replace(FOCUS_JAVASCRIPT,
                                            "$id",
                                            field.getId());
                    buffer.append(focusJavaScript);
                    break;
                }
            }
        }
    }

    /**
     * Render the Form validation JavaScript to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     * @param formFields the list of form fields
     */
    protected void renderValidationJavaScript(HtmlStringBuffer buffer, List formFields) {

        // Render JavaScript form validation code
        if (getValidate() && getJavaScriptValidation()) {
            List functionNames = new ArrayList();

            buffer.append("<script type=\"text/javascript\"><!--\n");

            // Render field validation functions & build list of function names
            for (Iterator i = formFields.iterator(); i.hasNext();) {
                Field field = (Field) i.next();
                String fieldJS = field.getValidationJavaScript();
                if (fieldJS != null) {
                    buffer.append(fieldJS);

                    StringTokenizer tokenizer = new StringTokenizer(fieldJS);
                    tokenizer.nextToken();
                    functionNames.add(tokenizer.nextToken());
                }
            }

            if (!functionNames.isEmpty()) {
                buffer.append("function on_");
                buffer.append(getId());
                buffer.append("_submit() {\n");
                buffer.append("   var msgs = new Array(");
                buffer.append(functionNames.size());
                buffer.append(");\n");
                for (int i = 0; i < functionNames.size(); i++) {
                    buffer.append("   msgs[");
                    buffer.append(i);
                    buffer.append("] = ");
                    buffer.append(functionNames.get(i).toString());
                    buffer.append(";\n");
                }
                buffer.append("   return validateForm(msgs, '");
                buffer.append(getId());
                buffer.append("', '");
                buffer.append(getErrorsAlign());
                buffer.append("', ");
                if (getErrorsStyle() == null) {
                    buffer.append("null");
                } else {
                    buffer.append("'" + getErrorsStyle() + "'");
                }
                buffer.append(");\n");
                buffer.append("}\n");

            } else {
                buffer.append("function on_");
                buffer.append(getId());
                buffer.append("_submit() { return true; }\n");
            }
            buffer.append("//--></script>\n");
        }
    }

    /**
     * Render the fieldsets form fields to the string buffer. This method will
     * apply the parent Forms properties to the layout and rendering of fields.
     * <p/>
     * This method delegates the rendering of the fieldset fields to
     * {@link #renderControls(net.sf.click.util.HtmlStringBuffer, net.sf.click.control.AbstractContainer, java.util.List, java.util.Map)}.
     *
     * @param buffer the StringBuffer to render to
     * @param fieldSet the fieldSet to render
     */
    protected void renderFieldSetFields(HtmlStringBuffer buffer, FieldSet fieldSet) {
        if (fieldSet.getControls().isEmpty()) {
            return;
        }

        // TODO should we render all controls or only Fields???
        // List fieldSetFields = fieldSet.getControl();
        List fieldSetFields = ContainerUtils.getFields(fieldSet);
        renderControls(buffer, fieldSet, fieldSetFields,
            fieldSet.getFieldWidths());
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Return true if the control is hidden, false otherwise.
     *
     * @param control control to check hidden status
     * @return true if the control is hidden, false otherwise
     */
    private boolean isHidden(Control control) {
        if (!(control instanceof Field)) {
            // Non-Field Controls can not be hidden
            return false;
        } else {
            return ((Field) control).isHidden();
        }
    }
}
