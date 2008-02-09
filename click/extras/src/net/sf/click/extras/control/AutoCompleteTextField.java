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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

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
 * <p/>
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
     * options, default value is: <tt>{}</tt>.
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
        String[] args = {
            getContext().getRequest().getContextPath(),
            ClickUtils.getResourceVersionIndicator(getContext()),
            getId(),
            getPage().getPath(),
            getAutoCompleteOptions()
        };

        return MessageFormat.format(HTML_IMPORTS, args);
    }

    /**
     * Return a HTML rendered AutoCompleteTextField.
     *
     * @return a HTML rendered AutoCompleteTextField string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.append(super.toString());

        buffer.elementStart("div");
        buffer.appendAttribute("class", "auto_complete");
        buffer.appendAttribute("id", getId() + "_auto_complete_div");
        buffer.elementEnd();

        return buffer.toString();
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Register the field with the parent page to intercept POST autocompletion
     * requests.
     *
     * @see net.sf.click.Control#onInit()
     */
    public void onInit() {
        // See whether control has been registered at Page level.
        Object control = getPage().getModel().get(getName());

        // If not registered, then register control
        if (control == null) {
            getPage().addControl(this);

        } else if (!(control instanceof AutoCompleteTextField)) {
            String message =
                "Non AutoCompleteTextField object '"
                + control.getClass().toString()
                + "' already registered in Page as: "
                + getName();
            throw new IllegalStateException(message);
        }
    }

    /**
     * Process the page request and if an auto completion POST request then
     * render an list of suggested values.
     *
     * @see net.sf.click.Control#onProcess()
     *
     * @return false if an auto complete request, otherwise returns true
     */
    public boolean onProcess() {
        if (getContext().isPost()) {
            // If an auto complete POST request then render suggested list,
            // otherwise continue as normal
            if (!getForm().isFormSubmission()) {
                String criteria = getContext().getRequestParameter(getName());
                if (criteria != null) {
                    List autoCompleteList = getAutoCompleteList(criteria);
                    renderAutoCompleteList(autoCompleteList);
                    return false;

                } else {
                    return true;
                }

            } else {
                return super.onProcess();
            }

        } else {
            return true;
        }
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
