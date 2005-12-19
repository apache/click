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
package net.sf.click.extras.control;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.click.control.TextField;
import net.sf.click.util.MessagesMap;

/**
 * Provides a Regex Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Regex Field</td>
 * <td><input type='text' value='string' title='RegexField Control'/></td>
 * </tr>
 * </table>
 *
 * RegexField will validate the value using regular expression
 * when the control is processed and invoke the control listener
 * if the value format is valid.
 *
 * <pre class="codeJava">
 * RegexField field = <span class="kw">new</span> RegexField(<span class="st">"versionNumber"</span>);
 * field.setPattern(<span class="st">"[0-9]+\\.[0-9]+\\.[0-9]+"</span>);
 * form.add(field);</pre>
 *
 * For more usage of RegexField see the {@link net.sf.click.control.TextField}.
 *
 * <p>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Naoki Takezoe
 */
public class RegexField extends TextField {

    // ----------------------------------------------------------- Constructors

    private static final long serialVersionUID = 1030492442713326113L;

    // ----------------------------------------------------- Instance Variables
    /**
     * The Click Extras messages bundle name: &nbsp; <tt>click-extras</tt>
     */
    protected static final String CONTROL_MESSAGES = "click-extras";

    /**
     * The field pattern based on regular expression.
     * If the pattern is specified, RegexField validates the field value using this.
     */
    protected String pattern;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a TextField with no name defined,
     * <b>please note</b> the control's name must be defined before it is valid.
     */
    public RegexField() {
        super();
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
     * @param size
     */
    public RegexField(String name, String label, int size) {
        super(name, label, size);
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
     * Construct the RegexField with the given name.
     *
     * @param name the name of the field
     */
    public RegexField(String name) {
        super(name);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Sets the field pattern as regular expression.
     * This field validates the value using this pattern.
     *
     * @param pattern the field pattern
     * @throws PatternSyntaxException if the pattern has a syntax error
     */
    public void setPattern(String pattern){
        Pattern.compile(pattern);
        this.pattern = pattern;
    }

    /**
     * Returns the field pattern.
     *
     * @return the field pattern
     */
    public String getPattern(){
        return this.pattern;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the RegexField submission. If the text value passes the validation
     * constraints and a Control listener is defined then the listener method will
     * be invoked.
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
     *   <li>/net/sf/click/extras/control/RegexField.properties
     *     <ul>
     *       <li>field-pattern-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        value = getRequestValue();

        if (!validate()) {
            return true;
        }

        int length = value.length();
        if (length > 0) {
            if (getMinLength() > 0 && length < getMinLength()) {
                Object[] args = new Object[] { getErrorLabel(), new Integer(getMinLength()) };
                setError(getMessage("field-minlength-error", args));
                return true;
            }

            if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[] { getErrorLabel(), new Integer(getMaxLength()) };
                setError(getMessage("field-maxlength-error", args));
                return true;
            }

            if(pattern!=null && !Pattern.matches(pattern, value)){
                Object[] args = new Object[] { getErrorLabel(), pattern };
                setError(getMessage("field-pattern-error", args));
                return true;
            }

            return invokeListener();

        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getErrorLabel()));
            }
        }

        return true;
    }

    /**
     * Return a Map of localized messages for the Field.
     *
     * @return a Map of localized messages for the Field
     * @throws IllegalStateException if the context for the Field has not be set
     */
    public Map getMessages() {
        // TODO: will become redundant, when Field getMessages() is updated...

        if (messages == null) {
            if (getContext() != null) {
                Locale locale = getContext().getLocale();
                messages = new HashMap();
                messages.putAll(new MessagesMap(TextField.CONTROL_MESSAGES, locale));
                messages.putAll(new MessagesMap(getClass().getName(), locale));

            } else {
                String msg = "Cannot initialize messages as context not set";
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }
}
