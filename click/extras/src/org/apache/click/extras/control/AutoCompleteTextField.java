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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.click.Behavior;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.Page;
import org.apache.click.ActionResult;
import org.apache.click.ajax.AjaxBehavior;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

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
 * <pre class="prettyprint">
 * AutoCompleteTextField nameField = new AutoCompleteTextField("name") {
 *     public List getAutoCompleteList(String criteria) {
 *         return getCustomerService().getCustomerNamesLike(criteria);
 *     }
 * };
 * form.add(nameField); </pre>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * AutoCompleteTextField depends on the <a class="external" target="_blank" href="http://www.prototypejs.org">Prototype</a>
 * and <a class="external" target="_blank" href="http://script.aculo.us/">Scriptaculous/</a>
 * JavaScript libraries to handle the auto-complete functionality. See
 * <a class="external" target="_blank" href="http://wiki.github.com/madrobby/scriptaculous/ajax-autocompleter">Ajax.Autocompleter</a>
 * for more details.
 * <p/>
 * The AutoCompleteTextField control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.css</tt></li>
 * <li><tt>click/control.js</tt></li>
 * <li><tt>click/prototype/prototype.js</tt></li>
 * <li><tt>click/prototype/effects.js</tt></li>
 * <li><tt>click/prototype/controls.js</tt></li>
 * </ul>
 *
 * See also the W3C HTML reference:
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public abstract class AutoCompleteTextField extends TextField {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

    /**
     * The JavaScript 'script.aculo.us' Autocompleter initialization options,
     * default value is: <tt>minChars:1</tt>.
     */
    protected String autoCompleteOptions = "minChars:1";

    /**
     * Additional parameters to send to server. Note the AutoCompleteTextField
     * {@link #getId() id} is always sent as a parameter to the server in order
     * for Click to identify the field.
     */
    protected Map<String, Object> parameters;

    /** The Field Ajax Behavior provides autocomplete support. */
    protected Behavior behavior;

    // Constructors -----------------------------------------------------------

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

    // Abstract Methods -------------------------------------------------------

    /**
     * Return the list of suggested values for the given search criteria.
     *
     * @param criteria the search criteria
     * @return the list of suggested values for the given search criteria
     */
    abstract public List<?> getAutoCompleteList(String criteria);

    // Public Methods ---------------------------------------------------------

    /**
     * Return the JavaScript 'script.aculo.us' Autocompleter initialization
     * options, default value is <tt>"minChars:1"</tt>.
     *
     * @return the JavaScript Autocompleter initialization options
     */
    public String getAutoCompleteOptions() {
        return autoCompleteOptions;
    }

    /**
     * Set the JavaScript 'script.aculo.us' Autocompleter initialization
     * options, default value is: <tt>minChars:1</tt>.
     * <p/>
     * For the full list of available options, see the Ajax-AutoCompleter
     * <a href="http://github.com/madrobby/scriptaculous/wikis/ajax-autocompleter" target="_blank">documentation</a>.
     * <p/>
     * Below is an example of how to set some of these options:
     * <pre class="prettyprint">
     * public void onInit() {
     *     AutoCompleteTextField cityField = new AutoCompleteTextField("cityField");
     *     field.setAutoCompleteOptions("minChars:1, frequency: 0.6");
     * } </pre>
     *
     * <b>Please note:</b> to send additional request parameters use
     * {@link #setParameter(java.lang.String, java.lang.Object)} instead
     *
     * @param options the JavaScript Autocompleter initialization options
     */
    public void setAutoCompleteOptions(String options) {
        this.autoCompleteOptions = options;
    }

    /**
     * Return true if this field has additional parameters, false otherwise.
     *
     * @return true if this field has additional parameters, false otherwise
     */
    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    /**
     * Return this field map of additional parameters.
     *
     * @return this field map of additional parameters
     */
    public Map<String, Object> getParameters() {
        if (parameters == null) {
             parameters = new HashMap<String, Object>();
        }
        return parameters;
    }

    /**
     * Set the field parameter with the given parameter name and value. If the
     * value is null the parameter will be removed.
     * <p/>
     * Scriptaculous AutoCompleter supports sending arbitrary request parameters
     * as part of its options. See the Ajax-AutoCompleter
     * <a href="http://github.com/madrobby/scriptaculous/wikis/ajax-autocompleter" target="_blank">documentation</a>
     * for some examples.
     * <p/>
     * Below is an example of how to send additional request parameters:
     * <pre class="prettyprint">
     * public void onInit() {
     *     AutoCompleteTextField cityField = new AutoCompleteTextField("cityField");
     *     cityField.setParameter(stateField.getName(), stateField.getValue());
     * } </pre>
     *
     * @param name the attribute name
     * @param value the attribute value
     */
    public void setParameter(String name, Object value) {
        if (value != null) {
            getParameters().put(name, value);
        } else {
            getParameters().remove(name);
        }
    }

    /**
     * Return the list of HEAD {@link org.apache.click.element.Element elements}
     * (resources) to be included in the page. The resources are:
     * <p/>
     * <ul>
     * <li>/click/extras-control.css</li>
     * <li>/click/control.js</li>
     * <li>/click/prototype/prototype.js</li>
     * <li>/click/prototype/effects.js</li>
     * <li>/click/prototype/controls.js</li>
     * </ul>
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the list of HEAD elements to be included in the page
     * @throws IllegalStateException if the field's name has not been set or
     * if the field is not attached to the Page
     */
    @Override
    public List<Element> getHeadElements() {
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

            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new CssImport("/click/extras-control.css",
                versionIndicator));
            headElements.add(new JsImport("/click/control.js", versionIndicator));
            headElements.add(new JsImport("/click/prototype/prototype.js",
                versionIndicator));
            headElements.add(new JsImport("/click/prototype/effects.js",
                versionIndicator));
            headElements.add(new JsImport("/click/prototype/controls.js",
                versionIndicator));
        }

        // Note: the setup script is recreated and checked if it is contained in
        // the headElement. This check cater for when the field is used by another
        // Control using the fly-weight pattern eg. FormTable.
        String fieldId = getId();
        JsScript script = new JsScript();
        script.setId(fieldId + "-autocomplete");
        if (!headElements.contains(script)) {
            // Script must be executed as soon as browser dom is ready
            script.setExecuteOnDomReady(true);

            String contextPath = context.getRequest().getContextPath();
            HtmlStringBuffer buffer = new HtmlStringBuffer(150);
            buffer.append("new Ajax.Autocompleter(");
            buffer.append("'").append(fieldId).append("'");
            buffer.append(",'").append(fieldId).append("-auto-complete-div'");
            buffer.append(",'").append(contextPath).append(page.getPath()).append("'");

            String id  = getId();

            // Include the field id as a parameter
            buffer.append(",{parameters: '").append(id).append("=1'");

            if (hasParameters()) {

                for (Entry<String, Object> entry : getParameters().entrySet()) {
                    // Add additional parameters
                    buffer.append("&amp;");
                    buffer.append(entry.getKey());
                    buffer.append("=");
                    buffer.append(entry.getValue());
                }
            }

            if (StringUtils.isNotEmpty(getAutoCompleteOptions())) {
                buffer.append(",").append(getAutoCompleteOptions());
            }

            buffer.append("});");

            script.setContent(buffer.toString());
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
    @Override
    public void render(HtmlStringBuffer buffer) {
        super.render(buffer);

        buffer.elementStart("div");
        buffer.appendAttribute("class", "auto_complete");
        buffer.append(" id=\"").append(getId()).append("-auto-complete-div\"");
        buffer.closeTag();
        buffer.elementEnd("div");
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * Register the field with the parent page to intercept POST autocompletion
     * requests.
     *
     * @see org.apache.click.Control#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();
        super.addBehavior(getBehavior());
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param behavior the behavior to add
     * @throws UnsupportedOperationException this field uses an internal behavior
     * instead
     */
    @Override
    public void addBehavior(Behavior behavior) {
        throw new UnsupportedOperationException("AutoCompleteTextField uses"
            + " an internal behavior, extra behaviors are not supported");
    }

    /**
     * This method is not supported and throws an UnsupportedOperationException
     * if invoked.
     *
     * @param behavior the behavior to add
     * @throws UnsupportedOperationException if invoked
     */
    @Override
    public void removeBehavior(Behavior behavior) {
        throw new UnsupportedOperationException("AutoCompleteTextField uses"
            + " an internal behavior, extra behaviors are not supported");
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Return the field internal Ajax behavior instance.
     *
     * @return the field internal Ajax behavior instance
     */
    protected Behavior getBehavior() {
        if (behavior == null) {
            behavior = createBehavior();
        }
        return behavior;
    }

    /**
     * Create the field Ajax behavior instance.
     *
     * @return the field Ajax behavior instance
     */
    protected Behavior createBehavior() {
        AjaxBehavior internalBehavior = new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                ActionResult actionResult = new ActionResult();

                String contentType = getPage().getContentType();
                actionResult.setContentType(contentType);

                List<?> autocompleteList = getAutoCompleteList(getValue());
                if (autocompleteList != null) {
                    HtmlStringBuffer buffer = new HtmlStringBuffer(10 + (autocompleteList.size() * 20));
                    renderAutoCompleteList(buffer, autocompleteList);
                    actionResult.setContent(buffer.toString());
                }
                return actionResult;
            }
        };

        return internalBehavior;
    }

    /**
     * Render the suggested auto completion list to the servlet response.
     *
     * @param autoCompleteList the suggested list of auto completion values
     */
    protected void renderAutoCompleteList(HtmlStringBuffer buffer, List<?> autoCompleteList) {

        buffer.append("<ul>");

        for (int i = 0; i < autoCompleteList.size(); i++) {
            String value = autoCompleteList.get(i).toString();
            buffer.append("<li>");
            buffer.appendEscaped(value);
            buffer.append("</li>");
        }

        buffer.append("</ul>");
    }
}
