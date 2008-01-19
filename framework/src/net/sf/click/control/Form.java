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
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Page;
import net.sf.click.util.ClickUtils;
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
 * <h3>HTML Imports</h3>
 *
 * The Form control automatically deploys the control CSS style sheet
 * (<tt>control.css</tt>) and JavaScript file (<tt>control.js</tt>) to
 * the application directory <tt>/click</tt>.
 * To import these files and any form control imports simply reference the
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
public class Form extends AbstractControl {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The align left, form layout constant: &nbsp; <tt>"left"</tt>. */
    public static final String ALIGN_LEFT = "left";

    /** The align center, form layout constant: &nbsp; <tt>"center"</tt>. */
    public static final String ALIGN_CENTER = "center";

    /** The align right, form layout constant: &nbsp; <tt>"right"</tt>. */
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
     * The position left, labels of left form layout constant: &nbsp;
     * <tt>"left"</tt>.
     */
    public static final String POSITION_LEFT = "left";

    /**
     * The submit check reserved request parameter prefix: &nbsp;
     * <tt>SUBMIT_CHECK_</tt>.
     */
    public static final String SUBMIT_CHECK = "SUBMIT_CHECK_";

    /** The HTML imports statements. */
    protected static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/control{1}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control{1}.js\"></script>\n";

    /** The Form set field focus JavaScript. */
    protected static final String FOCUS_JAVASCRIPT =
        "<script type=\"text/javascript\"><!--\n"
        + "var field = document.getElementById('$id');\n"
        + "if (field && field.focus && field.type != 'hidden' && field.disabled != true) { field.focus(); };\n"
        + "//--></script>\n";

    // -------------------------------------------------------- Class Variables

    /** The label-required-prefix resource property. */
    protected static String LABEL_REQUIRED_PREFIX = "";

    /** The label-required-suffix resource property. */
    protected static String LABEL_REQUIRED_SUFFIX = "";

    /** The label-not-required-prefix resource property. */
    protected static String LABEL_NOT_REQUIRED_PREFIX = "";

    /** The label-not-required-suffix resource property. */
    protected static String LABEL_NOT_REQUIRED_SUFFIX = "";

    static {
        ResourceBundle bundle = ResourceBundle.getBundle(CONTROL_MESSAGES);

        LABEL_REQUIRED_PREFIX = bundle.getString("label-required-prefix");
        LABEL_REQUIRED_SUFFIX = bundle.getString("label-required-suffix");
        LABEL_NOT_REQUIRED_PREFIX = bundle.getString("label-not-required-prefix");
        LABEL_NOT_REQUIRED_SUFFIX = bundle.getString("label-not-required-suffix");
    }

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
     * value: &nbsp; <tt>"top"</tt>.
     */
    protected String errorsPosition = POSITION_TOP;

    /** The error &lt;td&gt; "style" attribute value. */
    protected String errorsStyle;

    /** The ordered list of field values, excluding buttons. */
    protected final List fieldList = new ArrayList();

    /** The map of fields keyed by field name. */
    protected final Map fields = new HashMap();

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

    /** The listener target object. */
    protected Object listener;

    /** The listener method name. */
    protected String listenerMethod;

    /**
     * The form method <tt>["post, "get"]</tt>, default value: &nbsp;
     * <tt>post</tt>.
     */
    protected String method = "post";

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
     * Create an form with no name.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
     * <p/>
     * If the form has a
     *
     * @param field the field to add to the form
     * @throws IllegalArgumentException if the form already contains a field or
     *  button with the same name, or if the field name is not defined
     */
    public void add(Field field) {
        if (field == null) {
            throw new IllegalArgumentException("Field parameter cannot be null");
        }
        if (StringUtils.isBlank(field.getName())) {
            String msg = "Field name not defined: " + field.getClass().getName();
            throw new IllegalArgumentException(msg);
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
    }

    /**
     * Add the field to the form and specify the field's width in columns.
     * <p/>
     * Note Button or HiddenFields types are not valid for this method.
     *
     * @param field the field to add to the form
     * @param width the width of the field in table columns
     * @throws IllegalArgumentException if the form already contains a field or
     *  a button is added, or if the field name is not defined
     */
    public void add(Field field, int width) {
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
    }

    /**
     * Remove the given field from the form.
     *
     * @param field the field to remove from the form
     */
    public void remove(Field field) {
        if (field != null && getFields().containsKey(field.getName())) {
            field.setForm(null);
            if (field.getParent() == this) {
                field.setParent(null);
            }

            getFields().remove(field.getName());
            getFieldWidths().remove(field.getName());

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
        return response.encodeURL(request.getRequestURI());
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
     * Return the named field if contained in the form or the forms fieldsets,
     * or null if not found.
     *
     * @param name the name of the field
     * @return the named field if contained in the form
     */
    public Field getField(String name) {
        Field field = (Field) fields.get(name);

        if (field != null) {
            return field;

        } else {
            for (Iterator i = getFields().values().iterator(); i.hasNext();) {
                field = (Field) i.next();
                if (field instanceof FieldSet) {
                    FieldSet fieldSet = (FieldSet) field;
                    if (fieldSet.getField(name) != null) {
                        return fieldSet.getField(name);
                    }
                }
            }
            return null;
        }
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
     * Return true if the page request is a submission from this form.
     *
     * @return true if the page request is a submission from this form
     */
    public boolean isFormSubmission() {
        String requestMethod = getContext().getRequest().getMethod();

        if (!getMethod().equalsIgnoreCase(requestMethod)) {
            return false;
        }

        return getName().equals(getContext().getRequestParameter(FORM_NAME));
    }

    /**
     * Return the HTML head import statements for the CSS stylesheet
     * (<tt>click/control.css</tt>) and JavaScript
     * (<tt>click/control.js</tt>) files.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {
        return ClickUtils.createHtmlImport(HTML_IMPORTS, getContext());
    }

    /**
     * Return the HTML head imports for the form and all its controls.
     *
     * {@link net.sf.click.Control#getHtmlImports()}
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
     * Return the field not required label prefix. If the value is null it will
     * be initialized with the <tt>label-not-required-prefix</tt> resource
     * property.
     *
     * @return the field not required label prefix
     */
    public String getLabelNotRequiredPrefix() {
        if (labelNotRequiredPrefix == null) {
            labelNotRequiredPrefix = LABEL_NOT_REQUIRED_PREFIX;
        }
        return labelNotRequiredPrefix;
    }

    /**
     * Set the field not required label prefix.
     *
     * @param value the field not required label prefix
     */
    public void setLabelNotRequiredPrefix(String value) {
        this.labelNotRequiredPrefix = value;
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
     * Return the field not required label suffix. If the value is null it will
     * be initialized with the <tt>label-not-required-suffix</tt> resource
     * property.
     *
     * @return the field not required label suffix
     */
    public String getLabelNotRequiredSuffix() {
        if (labelNotRequiredSuffix == null) {
            labelNotRequiredSuffix = LABEL_NOT_REQUIRED_SUFFIX;
        }
        return labelNotRequiredSuffix;
    }

    /**
     * Set the field not required label suffix.
     *
     * @param value the field not required label suffix
     */
    public void setLabelNotRequiredSuffix(String value) {
        this.labelNotRequiredSuffix = value;
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
     * Return the form method <tt>["post" | "get"]</tt>.
     *
     * @return the form method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Set the form method <tt>["post" | "get"]</tt>.
     *
     * @param value the form method
     */
    public void setMethod(String value) {
        method = value;
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
     * Clear any form or field errors by setting them to null.
     */
    public void clearErrors() {
        setError(null);
        List fields = ClickUtils.getFormFields(this);
        Field field = null;
        for (int i = 0, size = fields.size(); i < size; i++) {
            field = (Field) fields.get(i);
            field.setError(null);
        }
    }

    /**
     * Clear all the form field values setting them to null.
     */
    public void clearValues() {
        List fields = ClickUtils.getFormFields(this);
        Field field = null;
        for (int i = 0, size = fields.size(); i < size; i++) {
            field = (Field) fields.get(i);

            if (!field.getName().equals(FORM_NAME)) {
                field.setValue(null);
            }
        }
    }

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
     * copyForm also supports <tt>java.util.Map</tt> as an argument.
     * <p/>
     * By specifying a map, the Form's field values will be populated by
     * matching key/value pairs. A match occurs when the map's key is equal to
     * a field's name.
     * <p/>
     * The following example populates the Form fields with a map's key/value
     * pairs:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onInit() {
     *     form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
     *     form.add(<span class="kw">new</span> TextField(<span class="st">"name"</span>));
     *     form.add(<span class="kw">new</span> TextField(<span class="st">"address.street"</span>));
     *  }
     *
     *  <span class="kw">public void</span> onGet() {
     *     Map map = <span class="kw">new</span> HashMap();
     *     map.put(<span class="st">"name"</span>, <span class="st">"Steve"</span>);
     *     map.put(<span class="st">"address.street"</span>, <span class="st">"12 Long street"</span>);
     *     form.copyFrom(map);
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
     * attributes. copyFrom also supports <tt>java.util.Map</tt> as an argument.
     * <p/>
     * If the debug parameter is true, debugging messages will be
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
     * The following example populates the Customer object attributes with the
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
     * copyTo also supports <tt>java.util.Map</tt> as an argument.
     * <p/>
     * By specifying a map, the map's key/value pairs are populated from
     * matching Form field names. A match occurs when a field's name is
     * equal to a map's key.
     * <p/>
     * The following example populates the map with the Form field values:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onInit() {
     *     form = <span class="kw">new</span> Form(<span class="st">"form"</span>);
     *     form.add(<span class="kw">new</span> TextField(<span class="st">"name"</span>));
     *     form.add(<span class="kw">new</span> TextField(<span class="st">"address.street"</span>));
     *  }
     *
     *  <span class="kw">public void</span> onGet() {
     *     Map map = <span class="kw">new</span> HashMap();
     *     map.put(<span class="st">"name"</span>, null);
     *     map.put(<span class="st">"address.street"</span>, null);
     *     form.copyTo(map);
     *  }
     * </pre>
     * Note that the map acts as a template to specify which fields to populate
     * from.
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
     * Initialize the fields and buttons contained in the Form.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.onInit();
        }

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

        if (isFormSubmission()) {

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
     * Perform any pre rendering logic.
     *
     * @see net.sf.click.Control#onRender()
     */
    public void onRender() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            field.onRender();
        }

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
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            try {
                field.onDestroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        for (int i = 0, size = getButtonList().size(); i < size; i++) {
            Button button = (Button) getButtonList().get(i);
            try {
                button.onDestroy();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        setError(null);
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
     * @throws IllegalArgumentException if the page or redirectPath is null
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
     * is valid this method will return true, otherwise set the page to
     * redirect to the given Page class and return false.
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
     *         <span class="kw">return</span> form.onSubmitCheck(<span class="kw">this</span>, InvalidSubmitPage.<span class="kw">class</span>);
     *     }
     * } </pre>
     *
     * Form submit checks should generally be combined with the Post-Redirect
     * pattern which provides a better user experience when pages are refreshed.
     *
     * @param page the page invoking the Form submit check
     * @param pageClass the page class to redirect invalid submissions to
     * @return true if the form submit is OK or false otherwise
     * @throws IllegalArgumentException if the page or pageClass is null
     */
    public boolean onSubmitCheck(Page page, Class pageClass) {
        if (page == null) {
            throw new IllegalArgumentException("Null page parameter");
        }
        if (pageClass == null) {
            throw new IllegalArgumentException("Null pageClass parameter");
        }

        if (performSubmitCheck()) {
            return true;

        } else {
            page.setRedirect(pageClass);

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
     *         <span class="kw">return</span> form.onSubmitCheck(<span class="kw">this</span>, <span class="kw">this</span>, <span class="st">"onInvalidSubmit"</span>);
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
     * @param page the page invoking the Form submit check
     * @param submitListener the listener object to call when an invalid submit
     *      occurs
     * @param submitListenerMethod the listener method to invoke when an
     *      invalid submit occurs
     * @return true if the form submit is valid, or the return value of the
     *      listener method otherwise
     * @throws IllegalArgumentException if the page, submitListener or
     *      submitListenerMethod is null
     */
    public boolean onSubmitCheck(Page page, Object submitListener,
            String submitListenerMethod) {

        if (page == null) {
            throw new IllegalArgumentException("Null page parameter");
        }
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

        buffer.append("</form>\n");

        renderFocusJavaScript(buffer, formFields);

        renderValidationJavaScript(buffer, formFields);

        return buffer.toString();
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
            getContext().getRequest().getMethod().equalsIgnoreCase(getMethod());

        List formFields = ClickUtils.getFormFields(this);

        int bufferSize = getFormSizeEst(formFields);

        HtmlStringBuffer buffer = new HtmlStringBuffer(bufferSize);

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

        buffer.append(endTag());

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

        if (StringUtils.isBlank(getName())) {
            throw new IllegalStateException("Form name is not defined.");
        }

        final HttpServletRequest request = getContext().getRequest();
        final String submitTokenName =
            SUBMIT_CHECK + getName() + "_" + getContext().getResourcePath();

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
                String value = getContext().getRequestParameter(submitTokenName);
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

        buffer.elementStart("form");

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
                buffer.append(field);
                buffer.append("\n");
            }
        }
    }

    /**
     * Render the non hidden Form Fields to the string buffer.
     *
     * @param buffer the StringBuffer to render to
     */
    protected void renderFields(HtmlStringBuffer buffer) {
        if (getFieldList().size() == 1
            && getFields().containsKey("form_name")) {
            return;
        }

        buffer.append("<tr><td>\n");
        buffer.append("<table class=\"fields\" id=\"");
        buffer.append(getId());
        buffer.append("-fields\">\n");

        int column = 1;
        boolean openTableRow = false;

        for (int i = 0, size = getFieldList().size(); i < size; i++) {

            Field field = (Field) getFieldList().get(i);

            if (!field.isHidden()) {

                // Field width
                Integer width = (Integer) getFieldWidths().get(field.getName());

                if (column == 1) {
                    buffer.append("<tr class=\"fields\">\n");
                    openTableRow = true;
                }

                if (field instanceof FieldSet) {
                    buffer.append("<td class=\"fields\"");

                    if (width != null) {
                        int colspan = (width.intValue() * 2);
                        buffer.appendAttribute("colspan", colspan);
                    } else {
                        buffer.appendAttribute("colspan", 2);
                    }

                    buffer.append(">\n");
                    buffer.append(field);
                    buffer.append("</td>\n");

                } else if (field instanceof Label) {
                    buffer.append("<td class=\"fields\" align=\"");
                    buffer.append(getLabelAlign());
                    buffer.append("\"");

                    if (width != null) {
                        int colspan = (width.intValue() * 2);
                        buffer.appendAttribute("colspan", colspan);
                    } else {
                        buffer.appendAttribute("colspan", 2);
                    }

                    if (field.hasAttributes()) {
                        //Temporarily remove the style attribute
                        String tempStyle = null;
                        if (field.hasAttribute("style")) {
                            tempStyle = field.getAttribute("style");
                            field.setAttribute("style", null);
                        }
                        buffer.appendAttributes(field.getAttributes());

                        //Put style back in attribute map
                        if (tempStyle != null) {
                            field.setAttribute("style", tempStyle);
                        }
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
                    } else {
                        buffer.append(getLabelNotRequiredPrefix());
                    }
                    buffer.append("<label");
                    buffer.appendAttribute("for", field.getId());
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
                    } else {
                        buffer.append(getLabelNotRequiredSuffix());
                    }

                    if (POSITION_LEFT.equals(getLabelsPosition())) {
                        buffer.append("</td>\n");
                        buffer.append("<td align=\"left\"");
                        buffer.appendAttribute("style", getFieldStyle());

                        if (width != null) {
                            int colspan = (width.intValue() * 2) + 1;
                            buffer.appendAttribute("colspan", colspan);
                        }

                        buffer.append(">");
                    } else {
                        buffer.append("<br>");
                    }

                    // Write out field
                    buffer.append(field);
                    buffer.append("</td>\n");
                }

                if (width != null) {
                    if (field instanceof Label || field instanceof FieldSet) {
                        column += width.intValue();

                    } else {
                        column += (width.intValue() - 1);
                    }
                }

                if (column >= getColumns()) {
                    buffer.append("</tr>\n");
                    openTableRow = false;
                    column = 1;

                } else if (field instanceof FieldSet) {
                    column++;

                } else {
                    column++;
                }
            }

        }

        if (openTableRow) {
            buffer.append("</tr>\n");
        }

        buffer.append("</table>\n");
        buffer.append("</td></tr>\n");
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
                buffer.append(button);

                buffer.append("</td>");
            }

            buffer.append("</tr>\n");
            buffer.append("</table>\n");
            buffer.append("</td></tr>\n");
        }
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

}
