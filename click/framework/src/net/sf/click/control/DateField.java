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
 * The DateField control provides a Date entry field and a popup DHTML Calendar
 * &lt;div&gt;. Users can either key in a Date value or select a Date using the
 * Calendar.
 * <p/>
 * The Calendar popup is provided by the DHTML Calendar by
 * <a href="http://www.dynarch.com/">Dynarch.com</a>. The Calendar popup is
 * created as a &lt;div&gt; element using JavaScript. To enable the Calenar
 * popup, reference the method {@link Form#getHtmlImports()} in the page
 * template (imports click/form.js file). For example:
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
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class DateField extends TextField {

    private static final long serialVersionUID = 3379108282465075759L;

    // ----------------------------------------------------- Instance Variables

    /** The JavaScript DHTML Calendar pattern. */
    protected String calendarPattern;

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    /** The date format pattern value. */
    protected String formatPattern;

    /** The Calendar popup show time display bar flag. */
    protected boolean showTime;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the Date Field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param label the label of the field
     */
    public DateField(String label) {
        super(label);
        setAttribute("id", getName() + "-field");
        setFormatPattern("dd MMM yyyy");
    }

    /**
     * Construct the Date Field with the given name and label.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public DateField(String name, String label) {
        super(name, label);
        setAttribute("id", getName() + "-field");
        setFormatPattern("dd MMM yyyy");
    }

    /**
     * Construct the Date Field with the given label and required status.
     * <p/>
     * The field name will be Java property representation of the given label.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param label the label of the field
     * @param required the field required status
     */
    public DateField(String label, boolean required) {
        this(label);
        setRequired(required);
    }

    /**
     * Create a date field with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public DateField() {
        super();
        setFormatPattern("dd MMM yyyy");
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the JavaScript DHTML Calendar pattern. The DHTML Calendar pattern
     * is defined when you set the format pattern.
     *
     * @return the JavaScript DHTML Calendar pattern
     */
    public String getCalendarPattern() {
        return calendarPattern;
    }

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
        if (pattern == null) {
            throw new IllegalArgumentException("Null pattern parameter");
        }
        formatPattern = pattern;
        calendarPattern = parseDateFormatPattern(pattern);
    }

    /**
     * @see Field#setName(String)
     */
    public void setName(String name) {
        super.setName(name);
        setAttribute("id", getName() + "-field");
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

    /**
     * Return true if the DHTML Calendar popup will show the time display bar.
     *
     * @return true if the DHTML Calendar popup will show the time display bar
     */
    public boolean getShowTime() {
        return showTime;
    }

    /**
     * Set the DHTML Calendar popup show the time display bar flag.
     *
     * @param showTime the flag to show the Calendar time display bar
     */
    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
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
        if (formatPattern == null) {
            throw new IllegalStateException("dateFormat attribute is null");
        }

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

            } else if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[]{ getLabel(), new Integer(getMaxLength()) };
                setError(getMessage("field-maxlength-error", args));
                return true;

            } else {
                SimpleDateFormat dateFormat = getDateFormat();
                dateFormat.setLenient(false);

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

        if (!isReadonly() && !isDisabled()) {
            buffer.append("<img align='middle' hspace='2' style='cursor:hand' src='");
            buffer.append(getForm().getContext().getRequest().getContextPath());
            buffer.append("/click/calendar.gif' id='");
            buffer.append(getId());
            buffer.append("-button' ");

            String calendarTitle = getMessage("calendar-image-title");
            if (calendarTitle != null) {
                buffer.append(" alt='");
                buffer.append(calendarTitle);
                buffer.append("' title='");
                buffer.append(calendarTitle);
                buffer.append("'/>\n");
            } else {
                buffer.append("/>\n");
            }

            buffer.append("<script type='text/javascript'>\n");
            buffer.append("Calendar.setup({ \n");
            buffer.append(" inputField : '");
            buffer.append(getId());
            buffer.append("', \n");
            buffer.append(" ifFormat :    '");
            buffer.append(getCalendarPattern());
            buffer.append("', \n");
            if (getShowTime()) {
                buffer.append(" showsTime : true, \n");
            }
            buffer.append(" button : '");
            buffer.append(getId());
            buffer.append("-button', \n");
            buffer.append(" align :    'cr', \n");
            buffer.append(" singleClick : true \n");
            buffer.append("});\n");
            buffer.append("</script> \n");
        }

        return buffer.toString();
    }

    /**
     * Return the JavaScript Calendar pattern for the given Java DateFormat
     * pattern.
     *
     * @param pattern the Java DateFormat pattern
     * @return JavaScript Calendar pattern
     */
    protected String parseDateFormatPattern(String pattern) {
        StringBuffer jsPattern = new StringBuffer(20);
        int tokenStart = -1;
        int tokenEnd = -1;
        boolean debug = false;

        for (int i = 0; i < pattern.length(); i++) {
            char aChar = pattern.charAt(i);
            if (debug) {
                System.err.print("["+i+","+tokenStart+","+tokenEnd+"]="+aChar);
            }

            // If character is in SimpleDateFormat pattern character set
            if ("GyMwWDdFEaHkKhmsSzZ".indexOf(aChar) == -1) {
                if (debug) {
                    System.err.println(" N");
                }
                if (tokenStart > -1) {
                    tokenEnd = i;
                }
            } else {
                if (debug) {
                    System.err.println(" Y");
                }
                if (tokenStart == -1) {
                    tokenStart = i;
                }
            }

            if (tokenStart > -1) {

                if (tokenEnd == -1 && i == pattern.length() -1) {
                    tokenEnd = pattern.length();
                }

                if (tokenEnd > -1) {
                    String token = pattern.substring(tokenStart, tokenEnd);

                    if ("yyyy".equals(token)) {
                        jsPattern.append("%Y");
                    } else if ("yy".equals(token)) {
                        jsPattern.append("%y");
                    } else if ("MMMM".equals(token)) {
                        jsPattern.append("%B");
                    } else if ("MMM".equals(token)) {
                        jsPattern.append("%b");
                    } else if ("MM".equals(token)) {
                        jsPattern.append("%m");
                    } else if ("dd".equals(token)) {
                        jsPattern.append("%e");
                    } else if ("EEEE".equals(token)) {
                        jsPattern.append("%A");
                    } else if ("EEE".equals(token)) {
                        jsPattern.append("%a");
                    } else if ("EE".equals(token)) {
                        jsPattern.append("%a");
                    } else if ("E".equals(token)) {
                        jsPattern.append("%a");
                    } else if ("aaa".equals(token)) {
                        jsPattern.append("%p");
                    } else if ("aa".equals(token)) {
                        jsPattern.append("%p");
                    } else if ("a".equals(token)) {
                        jsPattern.append("%p");
                    } else if ("HH".equals(token)) {
                        jsPattern.append("%H");
                        setShowTime(true);
                    } else if ("H".equals(token)) {
                        jsPattern.append("%H");
                        setShowTime(true);
                    } else if ("hh".equals(token)) {
                        jsPattern.append("%l");
                        setShowTime(true);
                    } else if ("h".equals(token)) {
                        jsPattern.append("%l");
                        setShowTime(true);
                    } else if ("mm".equals(token)) {
                        jsPattern.append("%M");
                        setShowTime(true);
                    } else if ("m".equals(token)) {
                        jsPattern.append("%M");
                        setShowTime(true);
                    } else if ("ss".equals(token)) {
                        jsPattern.append("%S");
                        setShowTime(true);
                    } else if ("s".equals(token)) {
                        jsPattern.append("%S");
                        setShowTime(true);
                    } else {
                        if (debug) {
                            System.err.println("Not mapped:" + token);
                        }
                    }

                    if (debug) {
                        System.err.println("token["+tokenStart+","+tokenEnd+"]='" + token + "'");
                    }
                    tokenStart = -1;
                    tokenEnd = -1;
                }
            }

            if (tokenStart == -1 && tokenEnd == -1) {
                if ("GyMwWDdFEaHkKhmsSzZ".indexOf(aChar) == -1) {
                    jsPattern.append(aChar);
                }
            }
        }

        return jsPattern.toString();
    }
}
