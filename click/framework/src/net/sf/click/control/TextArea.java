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
 * <div class="code">
 * // Java code
 * TextArea commentsField = new TextArea("Comments");
 * commentsField.setCols(40);
 * commentsField.setRows(6);
 * form.add(commentsField);
 *
 * &lt;-- HTML output --&gt;
 * &lt;textarea name='comments' rows='6' cols='40'/&gt;&lt;/textarea&gt;
 * </div>
 *
 * See also the W3C HTML reference:
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.7">TEXTAREA</a>
 *
 * @author Malcolm Edgar
 */
public class TextArea extends Field {

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
     * Construct the TextArea with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public TextArea(String label) {
        super(label);
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
     * Set the number of text area columns.
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
     * Set the number of text area rows.
     *
     * @param rows set the number of text area rows
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Process the TextArea submission. If the text value passes the validation
     * constraints and a Control listener is defined then the listener
     * method will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
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
     * Return a HTML rendered TextArea string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(50);

        buffer.append("<textarea");
        buffer.append(" name='");
        buffer.append(getName());
        buffer.append("' rows='");
        buffer.append(getRows());
        buffer.append("' cols='");
        buffer.append(getCols());
        buffer.append("'");
        if (getTitle() != null) {
            buffer.append(" title='");
            buffer.append(getTitle());
            buffer.append("'");
        }

        renderAttributes(buffer);

        if (!isValid()) {
            buffer.append(" class='error'");
        } else if (isDisabled()) {
            buffer.append(" class='disabled'");
        }
        buffer.append(getDisabled());
        buffer.append(">");
        buffer.append(getValue());
        buffer.append("</textarea>");

        return buffer.toString();
    }

}
