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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides a Date Field control: &nbsp; &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>Date Field</td>
 * <td><input type='text' size='20' title='DateField Control' value='15 Mar 2006'/><input type='hidden'/><img align='middle' hspace='2' style='cursor:hand' src='calendar.gif' title='Calendar'/></td>
 * </tr>
 * </table>
 *
 * The DateField control provides a Date entry field and a popup Calendar Date
 * picker. Users can either key in a Date value or select a Date using the Calendar.
 * <p/>
 * The Calendar popup is created as a &lt;div&gt; element using JavaScript.
 * To enable the Calenar popup, reference the method {@link Form#getHtmlImports()}
 * in the page template (imports click/form.js file). For example.
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$form.htmlImports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="blue">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * <b>Important Notes</b>
 * <ul>
 * <li>
 * Take care laying out DateFields above Select controls, as there is a rendering
 * bug in Internet Explorer which draws Selects on top of the popup Calendar
 * &lt;div&gt;. This bug is not present in Mozilla Firefox.
 * <p/>
 * </li>
 * <li>
 * Including the HTML &lt;DOCTYPE&gt; in the page causes a positioning bug in
 * the display of the popup Calendar &lt;div&gt;.
 * </li>
 * </ul>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class CalendarField extends TextField {

    /**
     * The date format pattern value, default value: &nbsp;
     * "<tt>dd MMM yyyy</tt>"
     */
    protected String formatPattern = "dd MMM yyyy";

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the Date Field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the field
     */
    public CalendarField(String label) {
        super(label);
        setAttribute("id", getName() + "-field");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the field Date value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the field Date value
     */
    public Date getDate() {
        if (value != null || value.length() > 0) {
            try {
                Date date = getDateFormat().parse(value);

                return new Date(date.getTime());

            } catch (ParseException pe) {
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * Set the field Date value.
     *
     * @param date the Date value to set
     */
    public void setDate(Date date) {
        if (date != null) {
            value = getDateFormat().format(date);
        }
    }

    /**
     * Return the SimpleDateFormat for the {@link #formatPattern}
     * property.
     *
     * @return the SimpleDateFormat for the formatPattern
     */
    public SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(formatPattern);
        }
        return dateFormat;
    }

    /**
     * Return the SimpleDateFormat pattern.
     *
     * @return the SimpleDateFormat pattern.
     */
    public String getFormatPattern() {
        return formatPattern;
    }

    /**
     * Set the SimpleDateFormat pattern.
     *
     * @param pattern the SimpleDateFormat pattern.
     */
    public void setFormatPattern(String pattern) {
        formatPattern = pattern;
    }

    /**
     * Return the Timestamp value, or null if value was empty
     * or a parsing error occured.
     *
     * @return the Timestamp value
     */
    public Timestamp getTimestamp() {
        Date date = getDate();

        if (date != null) {
            return new Timestamp(date.getTime());
        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods
    /**
     * Process the DateField submission. If the Date value can be parsed
     * the controls listener will be invoked.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle: <blockquote>
     * <pre>/click-control.properties</pre></blockquote>
     * <p/>
     * Error message bundle key names include: <blockquote><ul>
     * <li>date-format-error</li>
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

            } else if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[]{ getLabel(), new Integer(getMaxLength()) };
                setError(getMessage("field-maxlength-error", args));
                return true;

            } else {
                SimpleDateFormat dateFormat = getDateFormat();

                boolean parsedOk = false;
                try {
                    dateFormat.parse(value).getTime();
                    parsedOk = true;

                } catch (ParseException pe) {
                    Object[] args = new Object[] { getLabel(), formatPattern };
                    setError(getMessage("date-format-error", args));
                }

                // Dont want to invoke listener if unable to parse dateFormat.
                if (parsedOk) {
                    return invokeListener();

                } else {
                    return true;
                }
            }

        } else {
            if (isRequired()) {
                setError(getMessage("field-required-error", getLabel()));
            }
            return true;
        }
    }

    /**
     * Return the HTML rendered Date Field string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(50);

        String textField = super.toString();
        buffer.append(textField);

        buffer.append("<img align='middle' hspace='2' style='cursor:hand' src='");
        buffer.append(getForm().getContext().getRequest().getContextPath());
        buffer.append("/click/calendar.gif' id='");
        buffer.append(getName());
        buffer.append("-button' ");

        String calendarTitle = getMessage("calendar-image-title");
        if (calendarTitle != null) {
            buffer.append(" alt='");
            buffer.append(calendarTitle);
            buffer.append("' title='");
            buffer.append(calendarTitle);
            buffer.append("'>\n");
        } else {
            buffer.append(">\n");
        }
        
        buffer.append("<script type='text/javascript'>\n");
        buffer.append("Calendar.setup({ \n");
        buffer.append(" inputField : '");
        buffer.append(getName());
        buffer.append("-field', \n");
        buffer.append(" ifFormat :    '%e %b %Y', \n");
        buffer.append(" button : '");
        buffer.append(getName());
        buffer.append("-button', \n");
        buffer.append(" align :    'cr', \n");
        buffer.append(" singleClick : true \n");
        buffer.append("});\n");
        buffer.append("</script> \n");

        return buffer.toString();
    }
}
