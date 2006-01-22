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

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.ServletContext;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

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
 * The Calendar popup is provided by the JSCalendar library by
 * <a href="http://www.dynarch.com/">Dynarch.com</a>. The Calendar popup is
 * created as a &lt;div&gt; element using JavaScript. To enable the Calenar
 * popup, reference the {@link net.sf.click.util.PageImports} object
 * in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 *  &lt;head&gt;
 *   <span class="blue">$imports</span>
 *  &lt;/head&gt;
 *  &lt;body&gt;
 *   <span class="red">$form</span>
 *  &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * The default Calendar style is 'system' which has a similar appearance
 * to the Windows Calendar control. The JSCalendar styles include:
 * <ul style="margin-top: 0.5em;">
 * <li>blue</li>
 * <li>blue2</li>
 * <li>brown</li>
 * <li>green</li>
 * <li>system</li>
 * <li>tas</li>
 * <li>win2k-1</li>
 * <li>win2k-2</li>
 * <li>win2k-cold-1</li>
 * <li>win2k-cold-2</li>
 * </ul>
 *
 * The DateField JavaScript, CSS and image resources are automatically deployed
 * to the <tt>click/calendar</tt> web directory on application startup.
 * <p/>
 * See also W3C HTML reference
 * <a title="W3C HTML 4.01 Specification"
 *    href="../../../../../html/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class DateField extends TextField {

    private static final long serialVersionUID = 3379108282465075759L;

    protected static final String HTML_IMPORTS =
        "<link rel=\"stylesheet\" type=\"text/css\" href=\"{0}/click/calendar/calendar-{1}.css\" title=\"style\"/>\n" +
        "<script type=\"text/javascript\" src=\"{0}/click/calendar/calendar.js\"></script>\n" +
        "<script type=\"text/javascript\" src=\"{0}/click/calendar/calendar-{2}.js\" charset=\"UTF-8\"></script>\n";

    protected static final String[] CALENDAR_RESOURCES =
        { ".gif", ".js", "-de.js", "-en.js", "-es.js", "-fr.js", "-ko.js",
          "-it.js", "-ja.js", "-ru.js", "-zh.js", "-blue.css", "-blue2.css",
          "-brown.css", "-green.css", "-system.css", "-tas.css",
          "-win2k-1.css", "-win2k-2.css", "-win2k-cold-1.css",
          "-win2k-cold-2.css", "-menuarrow.gif", "-menuarrow2.gif" };

    // ----------------------------------------------------- Instance Variables

    /** The JavaScript DHTML Calendar pattern. */
    protected String calendarPattern;

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    /** The date format pattern value. */
    protected String formatPattern;

    /** The Calendar popup show time display bar flag. */
    protected boolean showTime;

    /**
     * The JSCalendar CSS style, default value: <tt>system</tt>. &nbsp;
     * Available styles include:
     * <tt>[blue, blue2, brown, green, system, tas, win2k-1, win2k-2,
     * win2k-cold-1, win2k-cold-2]</tt>
     */
    protected String style = "system";

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the Date Field with the given name.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     */
    public DateField(String name) {
        super(name);
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
     * Construct the Date Field with the given name and required status.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public DateField(String name, boolean required) {
        this(name);
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
            if (getContext() != null) {
                Locale locale = getContext().getLocale();
                dateFormat = new SimpleDateFormat(formatPattern, locale);

            } else {
                dateFormat = new SimpleDateFormat(formatPattern);
            }
        }
        return dateFormat;
    }

    /**
     * Return the SimpleDateFormat pattern.
     *
     * @return the SimpleDateFormat pattern
     */
    public String getFormatPattern() {
        return formatPattern;
    }

    /**
     * Set the SimpleDateFormat pattern.
     *
     * @param pattern the SimpleDateFormat pattern
     */
    public void setFormatPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Null pattern parameter");
        }
        formatPattern = pattern;
        calendarPattern = parseDateFormatPattern(pattern);
    }

    /**
     * Return the DateField <tt>calendar.js</tt> and <tt>calendar-{lang}.js</tt>
     * includes.
     *
     * @see net.sf.click.control.Field#getHtmlImports()
     */
    public String getHtmlImports() {
        String[] args = {
            getContext().getRequest().getContextPath(),
            getStyle(),
            getContext().getLocale().getLanguage()
        };

        return MessageFormat.format(HTML_IMPORTS, args);
    }

    /**
     * @see net.sf.click.control.Field#setName(String)
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

    /**
     * Return the JSCalendar CSS style.
     * <p/>
     * Available styles include: <tt>[blue, blue2, brown, green, system, tas,
     * win2k-1, win2k-2, win2k-cold-1, win2k-cold-2]</tt>
     *
     * @return the JSCalendar CSS style
     */
    public String getStyle() {
        return style;
    }

    /**
     * Set the JSCalendar CSS style.
     *
     * @param style the JSCalendar CSS style
     */
    public void setStyle(String style) {
        if (style == null) {
            throw new IllegalArgumentException("Null style parameter");
        }
        this.style = style;
    }

    /**
     * Return the <tt>java.util.Date.class</tt>.
     *
     * @see net.sf.click.control.Field#getValueClass()
     *
     * @return the <tt>java.util.Date.class</tt>
     */
    public Class getValueClass() {
        return Date.class;
    }

    /**
     * Return the field Date value, or null if value was empty or a parsing
     * error occured.
     *
     * @see net.sf.click.control.Field#getValueObject()
     */
    public Object getValueObject() {
        return getDate();
    }

    /**
     * Set the date value of the field using the given object.
     *
     * @see net.sf.click.control.Field#setValueObject(Object)
     */
    public void setValueObject(Object object) {
        if (object != null) {
            if (getValueClass().isAssignableFrom(object.getClass())) {
                setDate((Date)object);

            } else {
                String msg =
                    "Invalid object class: " + object.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Deploy the Calendar Javascript and CSS resources to the web app
     * directory <tt>click/calendar</tt>.
     *
     * @see net.sf.click.Control#onDeploy(ServletContext)
     */
    public void onDeploy(ServletContext servletContext) throws IOException {
        String targetDir = "click" + File.separator + "calendar";

        // Deploy DateField resources files
        for (int i = 0; i < CALENDAR_RESOURCES.length; i++) {

            String calendarFilename = "calendar" + CALENDAR_RESOURCES[i];
            String calendarResource =
                "/net/sf/click/extras/control/calendar/" + calendarFilename;

            ClickUtils.deployFile
                (servletContext, calendarResource, targetDir);
        }
    }

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
                Object[] args = new Object[] { getErrorLabel(), new Integer(getMinLength()) };
                setError(getMessage("field-minlength-error", args));
                return true;

            } else if (getMaxLength() > 0 && length > getMaxLength()) {
                Object[] args = new Object[]{ getErrorLabel(), new Integer(getMaxLength()) };
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
                    Object[] args = new Object[] { getErrorLabel(), formatPattern };
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
                setError(getMessage("field-required-error", getErrorLabel()));
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
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        String textField = super.toString();
        buffer.append(textField);

        if (!isReadonly() && !isDisabled()) {
            Calendar calendar = new GregorianCalendar();

            buffer.append("<img align=\"middle\" hspace=\"2\" style=\"cursor:hand\" src=\"");
            buffer.append(getForm().getContext().getRequest().getContextPath());
            buffer.append("/click/calendar/calendar.gif\" id=\"");
            buffer.append(getId());
            buffer.append("-button\" ");

            String calendarTitle = getMessage("calendar-image-title");
            buffer.appendAttribute("alt", calendarTitle);
            buffer.appendAttribute("title", calendarTitle);
            buffer.elementEnd();

            buffer.append("<script type=\"text/javascript\">\n");
            buffer.append("Calendar.setup({ \n");
            buffer.append(" inputField :  '");
            buffer.append(getId());
            buffer.append("', \n");
            buffer.append(" ifFormat :    '");
            buffer.append(getCalendarPattern());
            buffer.append("', \n");
            if (getShowTime()) {
                buffer.append(" showsTime : true, \n");
            }
            buffer.append(" button :      '");
            buffer.append(getId());
            buffer.append("-button', \n");
            buffer.append(" align :       'cr', \n");
            buffer.append(" singleClick : true, \n");
            buffer.append(" firstDay :    ");
            buffer.append(calendar.getFirstDayOfWeek());
            buffer.append("\n});\n");
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
