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

import java.util.regex.Pattern;

import net.sf.click.control.TextField;

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
 * RegexField will validate the value using regular expression
 * when the control is processed and invoke the control listener
 * if the value format is valid.
 * <p/>
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
     * The field pattern based on regular expression.
     * If the pattern is specified, RegexField validates the field value using this.
     */
    protected String pattern;

    // ----------------------------------------------------------- Constructors

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
     * Construct the RegexField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public RegexField(String name, String label) {
        super(name, label);
    }

    /**
     * Create a RegexField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public RegexField() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Sets the field pattern as regular expression. Note compiling the regex
     * pattern is deferred until <tt>onProcess()</tt> method is invoked. If
     * at this point the pattern is invalid a <tt>PatternSyntaxException</tt>
     * will be thrown.
     *
     * @param pattern the field regular expression pattern
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
     *
     * @return true to continue Page event processing or false otherwise
     * @throws java.util.regex.PatternSyntaxException if the pattern has a
     *      syntax error
     */
    public boolean onProcess() {
        value = getRequestValue();

        if (!validate()) {
            return true;
        }

        int length = value.length();
        if (length > 0) {
            if (getMinLength() > 0 && length < getMinLength()) {
                Object[] args = new Object[] {
                    getErrorLabel(), new Integer(getMinLength())
                };
                setError(getMessage("field-minlength-error", args));
                return true;
            }

            if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[] {
                    getErrorLabel(), new Integer(getMaxLength())
                };
                setError(getMessage("field-maxlength-error", args));
                return true;
            }

            if (pattern!=null && !Pattern.matches(pattern, value)){
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

}
