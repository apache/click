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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.Context;
import org.apache.click.Page;
import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides an Auto Complete Text Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Text Field</td>
 * <td><input type='text' value='string' title='AutoCompleteTextField Control'/></td>
 * </tr>
 * </table>
 *
 * <h3>AutoCompleteTextField Example</h3>
 *
 * The example below shows how to a create an AutoCompleteTextField. Note how
 * the abstract method <tt>getAutoCompleteList()</tt> is implemented to provide
 * the list of suggested values.
 *
 * <pre class="codeJava">
 * AutoCompleteTextField nameField = <span class="kw">new</span> AutoCompleteTextField(<span class="st">"name"</span>) {
 *     public List getAutoCompleteList(String criteria) {
 *         <span class="kw">return</span> getCustomerService().getCustomerNamesLike(criteria);
 *     }
 * };
 * form.add(nameField); </pre>
 *
 * <p>
 * This control uses the JavaScript 'script.aculo.us' <tt>Ajax.Autocompleter</tt> class.
 * </p>
 *
 * See also the W3C HTML reference:
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public abstract class AutoCompleteTextField extends TextField {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The Prototype resource file names. */
    static final String[] PROTOTYPE_RESOURCES = {
        "/org/apache/click/extras/control/prototype/controls.js",
        "/org/apache/click/extras/control/prototype/effects.js",
        "/org/apache/click/extras/control/prototype/prototype.js",
    };

    // ----------------------------------------------------- Instance Variables

    /**
     * The JavaScript 'script.aculo.us' Autocompleter initialization options,
     * default value is: <tt>{minChars:1}</tt>.
     */
    protected String autoCompleteOptions = "{minChars:1}";

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the AutoCompleteTextField with the given name. The default text field size
     * is 20 characters.
     *
     * @param name the name of the field
     */
    public AutoCompleteTextField(String name) {
        super(name);
    }

    /**
     * Construct the AutoCompleteTextField with the given name and required status.
     * The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public AutoCompleteTextField(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Construct the AutoCompleteTextField with the given name and label. The default text
     * field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public AutoCompleteTextField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the AutoCompleteTextField with the given name, label and required status.
     * The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public AutoCompleteTextField(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Construct the AutoCompleteTextField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public AutoCompleteTextField(String name, String label, int size) {
        super(name, label);
        setSize(size);
    }

    /**
     * Create a AutoCompleteTextField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public AutoCompleteTextField() {
    }

    // ------------------------------------------------------- Abstract Methods

    /**
     * Return the list of suggested values for the given search criteria.
     *
     * @param criteria the search criteria
     * @return the list of suggested values for the given search criteria
     */
    abstract public List getAutoCompleteList(String criteria);

    // --------------------------------------------------------- Public Methods

    /**
     * Return the JavaScript 'script.aculo.us' Autocompleter initialization
     * options, default value is: <tt>{}</tt>.
     *
     * @return the JavaScript Autocompleter initialization options
     */
    public String getAutoCompleteOptions() {
        return autoCompleteOptions;
    }

    /**
     * Set the JavaScript 'script.aculo.us' Autocompleter initialization
     * options, default value is: <tt>{minChars:1}</tt>.
     * <p/>
     * Scriptaculous AutoCompleter supports sending arbitrary request parameters
     * as part of its options. See the Ajax-AutoCompleter
     * <a href="http://github.com/madrobby/scriptaculous/wikis/ajax-autocompleter" target="_blank">documentation</a>
     * for some examples.
     * <p/>
     * Below is an example of how to send extra request parameters:
     * <pre class="prettyprint">
     * public void onInit() {
     *     AutoCompleteTextField cityField = new AutoCompleteTextField("cityField");
     *     HtmlStringBuffer buffer = new HtmlStringBuffer();
     *     buffer.append("{"); // Options opens with squiggly bracket
     *     buffer.append(stateField.getName());
     *     buffer.append("=");
     *     buffer.append(stateField.getValue());
     *     buffer.append("&amp;");
     *     buffer.append(idField.getName());
     *     buffer.append("=");
     *     buffer.append(idField.getValue());
     *     buffer.append("}"); // Options closes with squiggly bracket
     *     field.setAutoCompleteOptions(options.toString());
     * } </pre>
     *
     * Note that you can add any of the options specified on the
     * Ajax-AutoCompleter wiki.
     *
     * @param options the JavaScript Autocompleter initialization options
     */
    public void setAutoCompleteOptions(String options) {
        this.autoCompleteOptions = options;
    }

    /**
     * Return the list of HEAD elements to be included in the page.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the list of HEAD elements to be included in the page
     * @throws IllegalStateException if the field's name has not been set
     * @throws IllegalStateException if the field is not attached to the Page
     */
    public List getHeadElements() {
        // Check that the field name and parent Page has been set
        String fieldName = getName();
        if (fieldName == null) {
            throw new IllegalStateException("AutoCompleteTextField name"
                + " is not defined. Set the name before calling"
                + " getHeadElements().");
        }

        Page page = getPage();
        if (page == null) {
            throw new IllegalStateException("The AutoCompleteTextField, '"
                + fieldName + "', is not attached to the Page. Add"
                + " AutoCompleteTextField to a parent form or container and"
                + " attach the parent to the Page before calling"
                + " getHeadElements().");
        }

        Context context = getContext();

        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new CssImport("/click/extras-control.css"));
            headElements.add(new JsImport("/click/control.js"));
            headElements.add(new JsImport("/click/prototype/prototype.js"));
            headElements.add(new JsImport("/click/prototype/effects.js"));
            headElements.add(new JsImport("/click/prototype/controls.js"));

            String fieldId = getId();
            String contextPath = context.getRequest().getContextPath();

            JsScript script = new JsScript();
            script.setId(fieldName + "_autocomplete");
            HtmlStringBuffer buffer = new HtmlStringBuffer(150);
            buffer.append("addLoadEvent(function() { new Ajax.Autocompleter(");
            buffer.append("'").append(fieldId).append("'");
            buffer.append(",'").append(fieldId).append("_auto_complete_div'");
            buffer.append(",'").append(contextPath).append(page.getPath()).append("'");
            buffer.append(",").append(getAutoCompleteOptions()).append(");})");

            headElements.add(script);
        }
        return headElements;
    }

    /**
     * Render the HTML representation of the AutoCompleteTextField.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        super.render(buffer);

        buffer.elementStart("div");
        buffer.appendAttribute("class", "auto_complete");
        buffer.appendAttribute("id", getId() + "_auto_complete_div");
        buffer.elementEnd();
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Process the page request and if an auto completion POST request then
     * render an list of suggested values.
     *
     * @see org.apache.click.Control#onProcess()
     *
     * @return false if an auto complete request, otherwise returns true
     */
    public boolean onProcess() {
        Context context = getContext();
        // If an auto complete POST request, render suggestion list,
        // otherwise continue as normal
        if (getForm().isFormSubmission()) {
            return super.onProcess();
        } else {
            String criteria = context.getRequestParameter(getName());
            if (criteria != null) {
                List autoCompleteList = getAutoCompleteList(criteria);
                renderAutoCompleteList(autoCompleteList);
                return false;
            }
        }
        return true;
    }

    /**
     * Deploys the controls static CSS and JavaScript resources.
     *
     * @see org.apache.click.control.Field#onDeploy(javax.servlet.ServletContext)
     *
     * @param servletContext the context
     */
    public void onDeploy(ServletContext servletContext) {
        ClickUtils.deployFile(servletContext, "click/control.js", "click");
        ClickUtils.deployFile(servletContext, "click/extras-control.css", "click");

        ClickUtils.deployFiles(servletContext,
                               PROTOTYPE_RESOURCES,
                               "click/prototype");
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Render the suggested auto completion list to the servlet response.
     *
     * @param autoCompleteList the suggested list of auto completion values
     */
    protected void renderAutoCompleteList(List autoCompleteList) {
        HtmlStringBuffer buffer = new HtmlStringBuffer(10 + (autoCompleteList.size() * 20));

        buffer.append("<ul>");

        for (int i = 0; i < autoCompleteList.size(); i++) {
            String value = autoCompleteList.get(i).toString();
            buffer.append("<li>");
            buffer.appendEscaped(value);
            buffer.append("</li>");
        }

        buffer.append("</ul>");

        HttpServletResponse response = getContext().getResponse();

        response.setContentType(getPage().getContentType());

        try {
            PrintWriter writer = response.getWriter();
            writer.print(buffer.toString());
            writer.flush();
            writer.close();

            getPage().setPath(null);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


}
