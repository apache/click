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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.service.FileUploadService;
import net.sf.click.service.LogService;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ContainerUtils;
import net.sf.click.util.HtmlStringBuffer;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a Basic Form control for performing manual form layout:
 * &nbsp; &lt;form method='post'&gt;.
 * <p/>
 * BasicForm allows you to specify your own form layout and error reporting.
 * <p/>
 * <b>Please note</b>, for most cases {@link Form} is a better option since
 * it provides auto layout and error reporting.
 *
 * @author Bob Schellink
 */
public class BasicForm extends AbstractContainer {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /**
     * The form name parameter for multiple forms: &nbsp; <tt>"form_name"</tt>.
     */
    public static final String FORM_NAME = "form_name";

    /** The HTTP content type header for multipart forms. */
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * The submit check reserved request parameter prefix: &nbsp;
     * <tt>SUBMIT_CHECK_</tt>.
     */
    public static final String SUBMIT_CHECK = "SUBMIT_CHECK_";

    /** The HTML imports statements. */
    protected static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/control{1}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control{1}.js\"></script>\n";

    // ----------------------------------------------------- Instance Variables

    /** The form action URL. */
    protected String actionURL;

    /** The form disabled value. */
    protected boolean disabled;

    /** The form "enctype" attribute. */
    protected String enctype;

    /** The form level error message. */
    protected String error;

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
     * Create a basic form with the given name.
     *
     * @param name the name of the form
     */
    public BasicForm(String name) {
        super(name);
    }

    /**
     * Create a basic form with no name.
     */
    public BasicForm() {
    }

    // -------------------------------------------------------- Public Attributes

    /**
     * Return the form's html tag: <tt>form</tt>.
     *
     * @see AbstractControl#getTag()
     *
     * @return this controls html tag
     */
    public String getTag() {
        return "form";
    }

    /**
     * Return the form "action" attribute URL value. If the action URL attribute
     * has not been explicitly set the form action attribute will target the
     * page containing the form. This is the default behaviour for most scenarios.
     * However if you explicitly specify the form "action" URL attribute, this
     * value will be used instead.
     * <p/>
     * Setting the form action attribute is useful for situations where you want
     * a form to submit to a different page. This can also be used to have a
     * form submit to the J2EE Container for authentication, by setting the
     * action URL to "<tt>j_security_check</tt>".
     * <p/>
     * The action URL will always be encoded by the response to ensure it includes
     * the Session ID if required.
     *
     * @return the form "action" attribute URL value.
     */
    public String getActionURL() {
        HttpServletResponse response = getContext().getResponse();
        if (actionURL == null) {
            HttpServletRequest request = getContext().getRequest();
            return response.encodeURL(ClickUtils.getRequestURI(request));

        } else {
            return response.encodeURL(actionURL);
        }
    }

    /**
     * Return the form "action" attribute URL value. By setting this value you
     * will override the default action URL which points to the page containing
     * the form.
     * <p/>
     * Setting the form action attribute is useful for situations where you want
     * a form to submit to a different page. This can also be used to have a
     * form submit to the J2EE Container for authentication, by setting the
     * action URL to "<tt>j_security_check</tt>".
     *
     * @param value the form "action" attribute URL value
     */
    public void setActionURL(String value) {
        this.actionURL = value;
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
            List fieldList = ContainerUtils.getFields(this);
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
     * Return a list of form fields which are not valid, not hidden and not
     * disabled.
     *
     * @return list of form fields which are not valid, not hidden and not
     *  disabled
     */
    public List getErrorFields() {
        return ContainerUtils.getErrorFields(this);
    }

    /**
     * Return the named field if contained in the form or null if not found.
     *
     * @param name the name of the field
     * @return the named field if contained in the form
     *
     * @throws IllegalStateException if a non-field control is found with the
     * specified name
     */
    public Field getField(String name) {
        Control control = ContainerUtils.findControlByName(this, name);

        if (control != null && !(control instanceof Field)) {
            throw new IllegalStateException("The control named " + name
                + " is an instance of the class " + control.getClass().getName()
                + ", which is not a " + Field.class.getName() + " subclass.");
        }
        return (Field) control;
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

        List fields = ContainerUtils.getFields(this);
        for (Iterator i = fields.iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            if (!field.isValid()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the ordered list of form {@link Field}s.
     * <p/>
     * The order of the fields is the same order they were added to the form.
     *
     * @return the ordered List of form fields
     */
    public List getFieldList() {
        return ContainerUtils.getFieldsAndLabels(this);
    }

    /**
     * Return the Map of form fields, keyed on field name.
     *
     * @return the Map of form fields, keyed on field name
     */
    public Map getFields() {
        return ContainerUtils.getFieldMap(this);
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
        List fields = ContainerUtils.getFields(this);
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
        List fields = ContainerUtils.getFields(this);
        Field field = null;
        for (int i = 0, size = fields.size(); i < size; i++) {
            field = (Field) fields.get(i);

            if (!field.getName().equals(FORM_NAME)
                && (!field.getName().startsWith(SUBMIT_CHECK))) {
                field.setValue(null);
            }
        }
    }

    /**
     * Copy the given object's attributes into the BasicForm's field values. In
     * other words automatically populate BasicForm's field values with the
     * given objects attributes.
     * <p/>
     * The following example populates the BasicForm field with Customer
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
     * By specifying a map, the BasicForm's field values will be populated by
     * matching key/value pairs. A match occurs when the map's key is equal to
     * a field's name.
     * <p/>
     * The following example populates the BasicForm fields with a map's
     * key/value pairs:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onInit() {
     *     form = <span class="kw">new</span> BasicForm(<span class="st">"form"</span>);
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
        ContainerUtils.copyObjectToContainer(object, this);
    }

    /**
     * Copy the BasicForm's field values into the given object's attributes. In
     * other words automatically populate Object attributes with the BasicForm's
     * field values.
     * <p/>
     * The following example populates the Customer attributes with the
     * BasicForm's field values:
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
     * matching BasicForm field names. A match occurs when a field's name is
     * equal to a map's key.
     * <p/>
     * The following example populates the map with the BasicForm field values:
     *
     * <pre class="codeJava">
     *  <span class="kw">public void</span> onInit() {
     *     form = <span class="kw">new</span> BasicForm(<span class="st">"form"</span>);
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
        ContainerUtils.copyContainerToObject(this, object);
    }

    /**
     * @see net.sf.click.Control#onProcess().
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

        boolean continueProcessing = true;

        if (isFormSubmission()) {

            if (hasControls()) {
                for (Iterator it = getControls().iterator(); it.hasNext();) {
                    Control control = (Control) it.next();
                    String controlName = control.getName();
                    if (controlName == null || !controlName.startsWith(SUBMIT_CHECK)) {

                        if (!control.onProcess()) {
                            continueProcessing = false;
                        }
                    }
                }
            }

            registerActionEvent();
        }

        return continueProcessing;
    }

    /**
     * Validate the Form request submission.
     * <p/>
     * A form error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>file-size-limit-exceeded-error</li>
     *       <li>post-size-limit-exceeded-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        setError(null);

        Exception exception = (Exception) getContext().getRequest()
            .getAttribute(FileUploadService.UPLOAD_EXCEPTION);

        if (!(exception instanceof FileUploadException)) {
            return;
        }

        FileUploadException fue = (FileUploadException) exception;

        String key = null;
        Object args[] = null;

        if (fue instanceof SizeLimitExceededException) {
            SizeLimitExceededException se =
                (SizeLimitExceededException) fue;

            key = "post-size-limit-exceeded-error";

            args = new Object[2];
            args[0] = new Long(se.getPermittedSize());
            args[1] = new Long(se.getActualSize());
            setError(getMessage(key, args));

        } else if (fue instanceof FileSizeLimitExceededException) {
            FileSizeLimitExceededException fse =
                (FileSizeLimitExceededException) fue;

            key = "file-size-limit-exceeded-error";

            // Parse the FileField name from the message
            String msg = fue.getMessage();
            int start = 10;
            int end = msg.indexOf(' ', start);
            String fieldName = fue.getMessage().substring(start, end);

            args = new Object[3];
            args[0] = ClickUtils.toLabel(fieldName);
            args[1] = new Long(fse.getPermittedSize());
            args[2] = new Long(fse.getActualSize());
            setError(getMessage(key, args));
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
     * <p/>
     * <b>Please note:</b> a call to onSubmitCheck always succeeds for Ajax
     * requests.
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
     * <p/>
     * <b>Please note:</b> a call to onSubmitCheck always succeeds for Ajax
     * requests.
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
     * <p/>
     * <b>Please note:</b> a call to onSubmitCheck always succeeds for Ajax
     * requests.
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

    // -------------------------------------------------------- Protected Methods

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

        // CLK-333. Don't regenerate submit tokens for Ajax requests.
        if (getContext().isAjaxRequest()) {
            return true;
        }

        String resourcePath = getContext().getResourcePath();
        int slashIndex = resourcePath.indexOf('/');
        if (slashIndex != -1) {
            resourcePath = resourcePath.replace('/', '_');
        }

        // Ensure resourcePath starts with a '_' seperator. If slashIndex == -1
        // or slashIndex > 0, resourcePath does not start with slash.
        if (slashIndex != 0) {
            resourcePath = '_' + resourcePath;
        }

        final HttpServletRequest request = getContext().getRequest();
        final String submitTokenName =
            SUBMIT_CHECK + getName() + resourcePath;

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
                if (value == null || value.length() == 0) {
                    // CLK-289. If a session attribute exists for the
                    // SUBMIT_CHECK, but no request parameter, we assume the
                    // submission is a duplicate and therefore invalid.
                    LogService logService = ClickUtils.getLogService();
                    logService.warn("    'Redirect After Post' token called '"
                        + submitTokenName + "' is registered in the session, "
                        + "but no matching request parameter was found. "
                        + "(form name: '" + getName()
                        + "'). To protect against a 'duplicate post', "
                        + "Form.onSubmitCheck() will return false.");
                    isValidSubmit = false;
                } else {
                    Long formTime = Long.valueOf(value);
                    isValidSubmit = formTime.equals(sessionTime);
                }
            }
        }

        // CLK-267: check against adding a duplicate field
        HiddenField field = (HiddenField) getField(submitTokenName);
        if (field == null) {
            field = new HiddenField(submitTokenName, Long.class);
            add(field);
        }

        // Save state info to form and session
        final Long time = new Long(System.currentTimeMillis());
        field.setValueObject(time);

        getContext().setSessionAttribute(submitTokenName, time);

        if (isValidSubmit) {
            return true;

        } else {
            return false;
        }
    }

    /**
     * @see AbstractControl#renderTagBegin(java.lang.String, net.sf.click.util.HtmlStringBuffer)
     *
     * @param tagName the name of the tag to render
     * @param buffer the buffer to append the output to
     */
    protected void renderTagBegin(String tagName, HtmlStringBuffer buffer) {
        if (tagName == null) {
            throw new IllegalStateException("Tag cannot be null");
        }

        buffer.elementStart(tagName);

        buffer.appendAttribute("method", getMethod());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("action", getActionURL());
        buffer.appendAttribute("enctype", getEnctype());

        appendAttributes(buffer);
    }

    /**
     * @see AbstractContainer#renderContent(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderContent(HtmlStringBuffer buffer) {
        // Render hidden fields
        List fields = ContainerUtils.getFields(this);
        for (Iterator it = fields.iterator(); it.hasNext();) {
            Field field = (Field) it.next();
            if (field.isHidden()) {
                field.render(buffer);
                buffer.append("\n");
            }
        }
        renderChildren(buffer);
    }

    /**
     * @see AbstractContainer#renderChildren(net.sf.click.util.HtmlStringBuffer)
     *
     * @param buffer the buffer to append the output to
     */
    protected void renderChildren(HtmlStringBuffer buffer) {
        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
                Control control = (Control) getControls().get(i);

                // Don't render hidden fields again.
                if (control instanceof Field) {
                    Field field = (Field) control;
                    if (field.isHidden()) {
                        continue;
                    }
                }
                int before = buffer.length();
                control.render(buffer);
                int after = buffer.length();
                if (before != after) {
                    buffer.append("\n");
                }
            }
        }
    }

    /**
     * @see AbstractControl#getControlSizeEst()
     *
     * @return the estimated rendered control size in characters
     */
    protected int getControlSizeEst() {
        return 400 + (getControls().size() * 350);
    }

    /**
     * Returns true if a POST error occurred, false otherwise.
     *
     * @return true if a POST error occurred, false otherwise
     */
    protected boolean hasPostError() {
        Exception e = (Exception)
            getContext().getRequest().getAttribute(FileUploadService.UPLOAD_EXCEPTION);

        if (e instanceof FileSizeLimitExceededException
            || e instanceof SizeLimitExceededException) {
            return true;
        }

        return false;
    }

}
