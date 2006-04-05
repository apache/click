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
 * Provides a TextArea control: &nbsp; &lt;textarea&gt;&lt;/textarea&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Text Area</td>
 * <td><textarea title='TextArea Control'>Rather lengthy text</textarea></td>
 * </tr>
 * </table>
 *
 * The example below shows how to a TextArea to a Form, and how it will be
 * rendered as HTML.
 *
 * <pre class="codeJava">
 * TextArea commentsField = <span class="kw">new</span> TextArea(<span class="st">"comments"</span>);
 * commentsField.setCols(40);
 * commentsField.setRows(6);
 * form.add(commentsField); </pre>
 *
 * HTML output:
 * <pre class="codeHtml">
 * &lt;textarea name='comments' rows='6' cols='40'/&gt;&lt;/textarea&gt; </pre>
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.7">TEXTAREA</a>
 *
 * @author Malcolm Edgar
 */
public class TextArea extends Field {

    private static final long serialVersionUID = 850919582013675611L;

    // ----------------------------------------------------- Instance Variables

    /**
     * The number of text area columns. The default number of columns is twenty.
     */
    protected int cols = 20;

    /**
     * The maximum field length validation contraint. If the value is zero this
     * validation constraint is not applied. The default value is zero.
     */
    protected int maxLength = 0;

    /**
     * The minimum field length validation constraint. If the valid is zero this
     * validation constraint is not applied. The default value is zero.
     */
    protected int minLength = 0;

    /** The number of text area rows. The default number of rows is three. */
    protected int rows = 3;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the TextArea with the given name. The area will have a
     * default size of 20 cols and 3 rows.
     *
     * @param name the name of the field
     */
    public TextArea(String name) {
        super(name);
    }


    /**
     * Construct the TextArea with the given name and label. The area will have
     * a default size of 20 cols and 3 rows.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public TextArea(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the TextArea with the given name and required status. The
     * area will have a default size of 20 cols and 3 rows.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public TextArea(String name, boolean required) {
        super(name);
        setRequired(required);
    }

    /**
     * Construct the TextArea with the given name, label and required status.
     * The area will have a default size of 20 cols and 3 rows.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public TextArea(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
    }

    /**
     * Construct the TextArea with the given name, number of columns and
     * number of rows.
     *
     * @param name the name of the field
     * @param cols the number of text area cols
     * @param rows the number of text area rows
     */
    public TextArea(String name, int cols, int rows) {
        super(name);
        setCols(cols);
        setRows(rows);
    }

    /**
     * Construct the TextArea with the given name, label, number of columns and
     * number of rows.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param cols the number of text area cols
     * @param rows the number of text area rows
     */
    public TextArea(String name, String label, int cols, int rows) {
        super(name, label);
        setCols(cols);
        setRows(rows);
    }

    /**
     * Create a TextArea with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public TextArea() {
        super();
    }

    // ------------------------------------------------------- Public Attributes

    /**
     * Return the number of text area columns.
     *
     * @return the number of text area columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Set the number of text area columns. The default number of columns is 20.
     *
     * @param cols set the number of text area columns.
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * Returns the maximum field length validation constraint. If the
     * {@link #maxLength} property is greater than zero, the Field values length
     * will be validated against this constraint when processed.
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
     * Return the number of text area rows.
     *
     * @return the number of text area rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Set the number of text area rows. The default number of rows is 3.
     *
     * @param rows set the number of text area rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return a HTML rendered TextArea string.
     *
     * @return a HTML rendered TextArea string
     */
    public String toString() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(96);

        buffer.elementStart("textarea");

        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("rows", getRows());
        buffer.appendAttribute("cols", getCols());
        buffer.appendAttribute("title", getTitle());
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
        buffer.closeTag();

        buffer.appendEscaped(getValue());

        buffer.elementEnd("textarea");

        return buffer.toString();
    }

    /**
     * Validate the TextArea request submission.
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
     */
    public void validate() {
        setError(null);

        String value = getValue();

        int length = value.length();
        if (length > 0) {
            if (getMinLength() > 0 && length < getMinLength()) {
                setErrorMessage("field-minlength-error", getMinLength());
                return;
            }

            if (getMaxLength() > 0 && length > getMaxLength()) {
                setErrorMessage("field-maxlength-error", getMaxLength());
                return;
            }

        } else {
            if (isRequired()) {
                setErrorMessage("field-required-error");
            }
        }
    }
}
