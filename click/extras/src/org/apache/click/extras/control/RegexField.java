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

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.click.Context;

import org.apache.click.control.TextField;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.util.ClickUtils;

/**
 * Provides a Regex Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Regex Field</td>
 * <td><input type='text' value='1.0.2' title='RegexField Control'/></td>
 * </tr>
 * </table>
 *
 * RegexField will validate the value using regular expression when the control
 * is processed and invoke the control listener if defined.
 *
 * <h3>RegexField Example</h3>
 *
 * Examples using RegexField for version number and URL input are provided below:
 *
 * <pre class="codeJava">
 * RegexField versionField = <span class="kw">new</span> RegexField(<span class="st">"version"</span>);
 * versionField.setPattern(<span class="st">"[0-9]+\\.[0-9]+\\.[0-9]+"</span>);
 * form.add(versionField);
 *
 * RegexField urlField = <span class="kw">new</span> RegexField(<span class="st">"url"</span>, <span class="st">"URL"</span>);
 * urlField.setPattern(<span class="st">"(http|https)://.+"</span>);
 * form.add(urlField); </pre>
 *
 * For details on valid regular expression patterns see
 * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html">Pattern</a>
 * Javadoc.
 * <p/>
 * Note for performance reasons the regular expression pattern is compiled when
 * the field is processed not when its value is set. If you set an invalid
 * expression pattern a
 * <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/PatternSyntaxException.html">PatternSyntaxException</a>
 * will be thrown by the {@link #onProcess()} method.
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The RegexField control makes use of the following resources
 * (which Click automatically deploys to the application directory, <tt>/click</tt>):
 *
 * <ul>
 * <li><tt>click/extras-control.js</tt></li>
 * </ul>
 *
 * To import these RegexField files simply reference the variables
 * <span class="blue">$headElements</span> and
 * <span class="blue">$jsElements</span> in the page template.
 * <p/>
 *
 * See also the W3C HTML reference:
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class RegexField extends TextField {

    private static final long serialVersionUID = 1L;

    // Constants --------------------------------------------------------------

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the minimum length</li>
     * <li>3 - is the maximum length</li>
     * <li>4 - is the field pattern (regular expression)</li>
     * <li>5 - is the localized error message for required validation</li>
     * <li>6 - is the localized error message for minimum length validation</li>
     * <li>7 - is the localized error message for maximum length validation</li>
     * <li>8 - is the localized error message for pattern validation</li>
     * </ul>
     */
    protected final static String VALIDATE_REGEXFIELD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateRegexField(\n"
        + "         ''{0}'',{1}, {2}, {3}, ''{4}'', [''{5}'',''{6}'',''{7}'', ''{8}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // Instance Variables -----------------------------------------------------

    /**
     * The field pattern based on regular expression.
     * If the pattern is specified, RegexField validates the field value using this.
     */
    protected String pattern;

    // Constructors -----------------------------------------------------------

    /**
     * Construct the RegexField with the given name.
     *
     * @param name the name of the field
     */
    public RegexField(String name) {
        super(name);
    }

    /**
     * Construct the RegexField with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public RegexField(String name, boolean required) {
        super(name, required);
    }

    /**
     * Construct the RegexField with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public RegexField(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Construct the RegexField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public RegexField(String name, String label, int size) {
        super(name, label, size);
    }

    /**
     * Construct the RegexField with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public RegexField(String name, String label, int size, boolean required) {
        super(name, label, size, required);
    }

    /**
     * Construct the RegexField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public RegexField(String name, String label) {
        super(name, label);
    }

    /**
     * Create a RegexField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public RegexField() {
        super();
    }

    // Public Attributes ------------------------------------------------------

    /**
     * Sets the field pattern as regular expression.
     *
     * @param pattern the field regular expression pattern
     * @throws java.util.regex.PatternSyntaxException if the regular expression pattern cannot be compiled
     */
    public void setPattern(String pattern) {
        Pattern.compile(pattern);
        this.pattern = pattern;
    }

    /**
     * Returns the field pattern.
     *
     * @return the field pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Returns the RegexField HTML HEAD elements for the
     * <tt>click/extras-control.js</tt> resource.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML HEAD elements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Context context = getContext();
            String versionIndicator = ClickUtils.getResourceVersionIndicator(context);

            headElements.add(new JsImport("/click/extras-control.js", versionIndicator));
        }
        return headElements;
    }

    /**
     * Return the field JavaScript client side validation function.
     * <p/>
     * The function name must follow the format <tt>validate_[id]</tt>, where
     * the id is the DOM element id of the fields focusable HTML element, to
     * ensure the function has a unique name.
     *
     * @return the field JavaScript client side validation function
     */
    @Override
    public String getValidationJavaScript() {
        Object[] args = new Object[9];
        args[0] = getId();
        args[1] = String.valueOf(isRequired());
        args[2] = String.valueOf(getMinLength());
        args[3] = String.valueOf(getMaxLength());
        args[4] = escapeMessage(getPattern());
        args[5] = getMessage("field-required-error", getErrorLabel());
        args[6] = getMessage("field-minlength-error",
                getErrorLabel(), String.valueOf(getMinLength()));
        args[7] = getMessage("field-maxlength-error",
                getErrorLabel(), String.valueOf(getMaxLength()));
        args[8] = escapeMessage(getMessage("field-pattern-error",
                getErrorLabel(), getPattern()));
        return MessageFormat.format(VALIDATE_REGEXFIELD_FUNCTION, args);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Validate the RegexField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-maxlength-error</li>
     *       <li>field-minlength-error</li>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/org/apache/click/extras/control/RegexField.properties
     *     <ul>
     *       <li>field-pattern-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     *
     * @throws java.util.regex.PatternSyntaxException if the pattern has a
     *      syntax error
     */
    @Override
    public void validate() {
        super.validate();

        if (isValid() && getValue().length() > 0) {
            String value = getValue();
            String pattern = getPattern();

            if (pattern != null && !Pattern.matches(pattern, value)) {
                setErrorMessage("field-pattern-error", pattern);
            }
        }
    }

    // Private Methods ------------------------------------------------------

    /**
     * Escape the JavaScript string.
     *
     * @param message the raw message
     * @return the escaped message
     */
    private String escapeMessage(String message) {
        if (message == null) {
            return "";
        }
        message = message.replaceAll("\\\\", "\\\\\\\\");
        message = message.replaceAll("'", "\\\\'");
        return message;
    }

}
