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
package net.sf.click.control;

import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Text Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Text Field</td>
 * <td><input type='text' value='string' title='TextField Control'/></td>
 * </tr>
 * </table>
 *
 * The example below shows how to a TextField to a Form, and how it will be
 * rendered as HTML.
 *
 * <pre class="codeJava">
 * TextField usernameField = <span class="kw">new</span> TextField(<span class="st">"Username"</span>);
 * usernameField.setRequired(<span class="kw">true</span>);
 * usernameField.setSize(12);
 * usernameField.setMaxLength(12);
 * usernameField.setMinLength(6);
 * form.add(usernameField); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;input type='text' name='username' value='' size='12' maxlength='12'&gt; </pre>
 *
 * For another example using TextField see the {@link net.sf.click.control.Form}
 * Javadoc example.
 * <p/>
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class TextField extends Field {

    // ----------------------------------------------------------- Constructors

    private static final long serialVersionUID = 6187190854708434969L;

    // ----------------------------------------------------- Instance Variables

    /**
     * The maximum field length validation contraint. If the value is zero this
     * validation constraint is not applied. The default value is zero.
     * <p/>
     * If maxLenth is greater than zero, then maxLength is rendered as the
     * HTML attribute 'maxlength'.
     */
    protected int maxLength = 0;

    /**
     * The minimum field length validation constraint. If the valid is zero this
     * validation constraint is not applied. The default value is zero.
     */
    protected int minLength = 0;

    /** The text field size attribute. The default size is 20. */
    protected int size = 20;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the TextField with the given name. The default text field size
     * is 20 characters.
     *
     * @param name the name of the field
     */
    public TextField(String name) {
        super(name);
    }

    /**
     * Construct the TextField with the given name and required status.
     * The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public TextField(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Construct the TextField with the given name and label. The default text
     * field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public TextField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the TextField with the given name, label and required status.
     * The default text field size is 20 characters.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public TextField(String name, String label, boolean required) {
        super(label, label);
        setRequired(required);
    }

    /**
     * Construct the TextField with the given name, label and size.
     *
     * @param label the label of the field
     * @param size the size of the field
     */
    public TextField(String name, String label, int size) {
        super(name, label);
        setSize(size);
    }

    /**
     * Create a TextField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public TextField() {
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Returns the maximum field length validation constraint. If the
     * {@link #maxLength} property is greater than zero, the Field values length
     * will be validated against this constraint when processed.
     * <p/>
     * If maxLenth is greater than zero, it is rendered as the field
     * attribute 'maxlength'
     *
     * @return the maximum field length validation contraint
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum field length. If the {@link #maxLength} property is
     * greater than zero, the Field values length will be validated against
     * this constraint when processed.
     * <p/>
     * If maxLenth is greater than zero, it is rendered as the field
     * attribute 'maxlength'
     *
     * @param maxLength the maximum field length validation constraint
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Returns the minimum field length validation constraint. If the
     * {@link #minLength} property is greater than zero, the Field values length
     * will be validated against this constraint when processed.
     *
     * @return the minimum field length validation contraint
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Sets the minimum field length validation constraint. If the
     * {@link #minLength} property is greater than zero, the Field values length
     * will be validated against this constraint when processed.
     *
     * @param minLength the minimum field length validation constraint
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Return the field size.
     *
     * @return the field size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the field size.
     *
     * @param  size the field size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the input type: '<tt>text</tt>'.
     *
     * @return the input type: '<tt>text</tt>'
     */
    public String getType() {
        return "text";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the TextField submission. If the text value passes the validation
     * constraints and a Control listener is defined then the listener
     * method will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>net.sf.click.control.MessageProperties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>field-maxlength-error</li>
     * <li>field-minlength-error</li>
     * <li>field-required-error</li>
     * </ul></blockquote>
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
                Object[] args = new Object[] { getLabel(), new Integer(getMinLength()) };
                setError(getMessage("field-minlength-error", args));
                return true;
            }

            if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[] { getLabel(), new Integer(getMaxLength()) };
                setError(getMessage("field-maxlength-error", args));
                return true;
            }

            return invokeListener();

        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getLabel()));
            }
        }

        return true;
    }

    /**
     * Return a HTML rendered TextField string.
     *
     * @see Object#toString()
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(96);

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getValue());
        buffer.appendAttribute("size", getSize());
        buffer.appendAttribute("title", getTitle());
        if (getMaxLength() > 0) {
            buffer.appendAttribute("maxlength", getMaxLength());
        }
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        if (isReadonly()) {
            buffer.appendAttributeReadonly();
        }
        if (!isValid()) {
            buffer.appendAttribute("class", "error");
        } else if (isDisabled()) {
            buffer.appendAttribute("class", "disabled");
        }

        buffer.elementEnd();

        return buffer.toString();
    }
}
