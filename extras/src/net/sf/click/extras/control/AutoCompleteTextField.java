/*
 * Copyright 2008 Malcolm A. Edgar
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
package net.sf.click.extras.control;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.click.AjaxListener;
import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.Partial;
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
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public abstract class AutoCompleteTextField extends TextField {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The Prototype resource file names. */
    static final String[] PROTOTYPE_RESOURCES = {
        "/net/sf/click/extras/control/prototype/controls.js",
        "/net/sf/click/extras/control/prototype/effects.js",
        "/net/sf/click/extras/control/prototype/prototype.js",
    };

    /** The JavaScript sorting HTML import statements. */
    public static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/extras-control{1}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/prototype{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/effects{1}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/prototype/controls{1}.js\"></script>\n"
        + "<script type=\"text/javascript\">addLoadEvent(function() '{'new Ajax.Autocompleter( ''{2}'', ''{2}_auto_complete_div'', ''{0}{3}'', {4} );'}');</script>\n";

    // ----------------------------------------------------- Instance Variables

    /**
     * The JavaScript 'script.aculo.us' Autocompleter initialization options,
     * default value is: <tt>minChars:1</tt>.
     */
    protected String autoCompleteOptions = "minChars:1";

    /**
     * Additional parameters to send to server. The default value is "".
     * Note however that the AutoCompleteTextField's {@link #getId() id} is
     * always sent as a parameter to the server.
     */
    protected String parameters = "";

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
     * Return the parameters to send to server when Autocompleter sends Ajax
     * request.
     * <p/>
     * The default value is "", however the AutoCompleteTextField's
     * {@link #getId() id} is always sent as a parameter to the server.
     *
     * @return the Autocompleter parameters to send to server
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * Set the extra parameters Autocompleter must send to the server when it
     * makes an Ajax request.
     * <p/>
     * The default value is "", however the AutoCompleteTextField's
     * {@link #getId() id} is always sent as a parameter to the server.
     * <p/>
     * The format of the parameters is the same as for a URL ->
     * key1=value1&key2=value2
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * AutoCompleteTextField cityField = new AutoCompleteTextField("cityField");
     * HtmlStringBuffer buffer = new HtmlStringBuffer();
     * buffer.append(stateField.getName());
     * buffer.append("=");
     * buffer.append(stateField.getValue());
     * buffer.append("&");
     * ...
     * field.setParameters(buffer.toString());
     * </pre>
     *
     * @param parameters the extra parameters Autocompleter must send to server
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    /**
     * Return the JavaScript 'script.aculo.us' Autocompleter initialization
     * options, default value is: <tt>minChars:1</tt>.
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
     * <b>Please note</b> when setting options, Click will automatically add
     * curly brackets {} for you.
     * <p/>
     * <b>Further note</b> if you want to send additional parameters use
     * {@link #setParameters(java.lang.String)} instead.
     * Example options:
     * <pre class="prettyprint">
     * AutoCompleteTextField field = new AutoCompleteTextField("field");
     * // Add extra options
     * HtmlStringBuffer buffer = new HtmlStringBuffer();
     * buffer.append("paramName: 'value'");
     * buffer.append(",minChars: 2");
     * buffer.append(",updateElement: addItemToList");
     * buffer.append(",indicator: 'indicator1'");
     * field.setAutoCompleteOptions(options);
     *
     * // To add additional parameters use setParameters(String)
     * buffer = new HtmlStringBuffer();
     * buffer.append(stateField.getName());
     * buffer.append("=");
     * buffer.append(stateField.getValue());
     * field.setParameters(buffer.toString());
     * </pre>
     *
     * @param options the JavaScript Autocompleter initialization options
     */
    public void setAutoCompleteOptions(String options) {
        this.autoCompleteOptions = options;
    }

    /**
     * Return the HTML CSS and JavaScript includes.
     *
     * @see net.sf.click.Control#getHtmlImports()
     *
     * @return the HTML CSS and JavaScript includes
     */
    public String getHtmlImports() {
        Context context = getContext();
        String id = getId();
        HtmlStringBuffer options = new HtmlStringBuffer();

        // Include the field's id as a parameter
        options.append("{parameters: '" + id + "=1");
        if (StringUtils.isNotEmpty(getParameters())) {
            // Add additional parameters
            options.append("&").append(getParameters());
        }
        options.append("'");
        if (StringUtils.isNotEmpty(getAutoCompleteOptions())) {
            options.append(",").append(getAutoCompleteOptions());
        }
        options.append("}");

        String[] args = {
            context.getRequest().getContextPath(),
            ClickUtils.getResourceVersionIndicator(context),
            id,
            getPage().getPath(),
            options.toString()
        };

        return MessageFormat.format(HTML_IMPORTS, args);
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
     * Register the field with the parent page to intercept POST autocompletion
     * requests.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        setActionListener(new AjaxListener() {

            public Partial onAjaxAction(Control source) {
                String criteria = getContext().getRequestParameter(getName());
                if (criteria != null) {
                    List autoCompleteList = getAutoCompleteList(criteria);
                    return createPartial(autoCompleteList);
                }
                return null;
            }
        });

        super.onInit();
    }

    /**
     * Deploys the controls static CSS and JavaScript resources.
     *
     * @see net.sf.click.control.Field#onDeploy(javax.servlet.ServletContext)
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
    protected Partial createPartial(List autoCompleteList) {
        HtmlStringBuffer buffer = new HtmlStringBuffer(10 + (autoCompleteList.size() * 20));

        buffer.append("<ul>");

        for (int i = 0; i < autoCompleteList.size(); i++) {
            String value = autoCompleteList.get(i).toString();
            buffer.append("<li>");
            buffer.appendEscaped(value);
            buffer.append("</li>");
        }

        buffer.append("</ul>");

        Partial partial = new Partial(buffer.toString(), getPage().getContentType());
        return partial;
    }
}
