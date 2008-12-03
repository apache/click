/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;

import net.sf.click.Context;
import net.sf.click.control.TextField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

/**
 * Provides a Date Field control: &nbsp; &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td style="vertical-align:baseline">Date Field</td>
 * <td style="vertical-align:baseline"><input type='text' size='20' title='DateField Control' value='15 Mar 2006'/><input type='hidden'/><img align='middle' hspace='2' style='cursor:hand' src='calendar.gif' title='Calendar'/></td>
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
 * popup, reference <span class="blue">$cssImports</span> and <span class="blue">$jsImports</span>
 * in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$cssImports</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$form</span>
 * &lt;/body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsImports</span> </pre>
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

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** The HTML import statements. */
    public static final String HTML_IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/click/calendar/calendar-{1}{7}.css\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/control{7}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/calendar/calendar{7}.js\"></script>\n"
        + "<script type=\"text/javascript\" src=\"{0}/click/calendar/calendar-{2}{7}.js\" charset=\"UTF-8\"></script>\n"
        + "<script type=\"text/javascript\">addLoadEvent( function() '{'Calendar.setup('{' inputField : ''{3}'', ifFormat : ''{4}'', showsTime : {5}, button : ''{3}-button'', align : ''cr'', singleClick : true, firstDay : {6} '}');'}');</script>\n";

    /** The Calendar resource file names. */
    static final String[] CALENDAR_RESOURCES =
        { ".gif", ".js", "-al.js", "-bg.js", "-cs.js", "-da.js", "-de.js",
          "-el.js", "-en.js", "-es.js", "-fi.js", "-fr.js", "-he.js", "-it.js",
          "-ja.js", "-ko.js", "-lt.js", "-lv.js", "-nl.js", "-no.js", "-pl.js",
          "-pt.js", "-ro.js", "-ru.js", "-sk.js", "-sv.js", "-zh.js", "-blue.css",
          "-blue2.css", "-brown.css", "-green.css", "-system.css", "-tas.css",
          "-win2k-1.css", "-win2k-2.css", "-win2k-cold-1.css",
          "-win2k-cold-2.css", "-menuarrow.gif", "-menuarrow2.gif" };

    /** Supported locales. */
    static final String[] SUPPORTTED_LANGUAGES =
        {"al", "bg", "cs", "da", "de", "el", "en", "es", "fi", "fr", "he", "it",
         "ja", "ko", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sv",
         "zh"};

    // ----------------------------------------------------- Instance Variables

    /** The DateField's date value. */
    protected Date date;

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
        setAttribute("id", getName() + "_field");
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
        setAttribute("id", getName() + "_field");
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
     * Construct the Date Field with the given name, label and required status.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public DateField(String name, String label, boolean required) {
        super(name, label, required);
        setAttribute("id", getName() + "_field");
        setFormatPattern("dd MMM yyyy");
    }

    /**
     * Construct the Date Field with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public DateField(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the Date Field with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public DateField(String name, String label, int size, boolean required) {
        this(name, label, required);
        setSize(size);
    }

    /**
     * Create a Date Field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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
     * error occurred.
     *
     * @return the field Date value
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the field Date value.
     *
     * @param date the Date value to set
     */
    public void setDate(Date date) {
        this.date = date;
        if (date != null) {
            super.setValue(getDateFormat().format(date));
        } else {
            super.setValue(null);
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
            dateFormat = new SimpleDateFormat(formatPattern, getLocale());
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
     * <h4>SimpleDateFormat Pattern Characters</h4>
     *
     *  <table border="1" cellspacing="0" cellpadding="3">
     *  <tr bgcolor="#ccccff">
     *           <th align=left>Letter
     *           <th align=left>Date or Time Component
     *           <th align=left>Presentation
     *           <th align=left>Examples
     *       <tr>
     *           <td><code>G</code>
     *           <td>Era designator
     *           <td>Text
     *           <td><code>AD</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>y</code>
     *           <td>Year
     *           <td>Year
     *           <td><code>1996</code>; <code>96</code>
     *       <tr>
     *           <td><code>M</code>
     *           <td>Month in year
     *           <td>Month
     *           <td><code>July</code>; <code>Jul</code>; <code>07</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>w</code>
     *           <td>Week in year
     *           <td>Number
     *           <td><code>27</code>
     *       <tr>
     *           <td><code>W</code>
     *           <td>Week in month
     *           <td>Number
     *           <td><code>2</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>D</code>
     *           <td>Day in year
     *           <td>Number
     *           <td><code>189</code>
     *       <tr>
     *           <td><code>d</code>
     *           <td>Day in month
     *           <td>Number
     *           <td><code>10</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>F</code>
     *           <td>Day of week in month
     *           <td>Number
     *           <td><code>2</code>
     *       <tr>
     *           <td><code>E</code>
     *           <td>Day in week
     *           <td>Text
     *           <td><code>Tuesday</code>; <code>Tue</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>a</code>
     *           <td>Am/pm marker
     *           <td>Text
     *           <td><code>PM</code>
     *       <tr>
     *           <td><code>H</code>
     *           <td>Hour in day (0-23)
     *           <td>Number
     *           <td><code>0</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>k</code>
     *           <td>Hour in day (1-24)
     *           <td>Number
     *           <td><code>24</code>
     *       <tr>
     *           <td><code>K</code>
     *           <td>Hour in am/pm (0-11)
     *           <td>Number
     *           <td><code>0</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>h</code>
     *           <td>Hour in am/pm (1-12)
     *           <td>Number
     *           <td><code>12</code>
     *       <tr>
     *           <td><code>m</code>
     *           <td>Minute in hour
     *           <td>Number
     *           <td><code>30</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>s</code>
     *           <td>Second in minute
     *           <td>Number
     *           <td><code>55</code>
     *       <tr>
     *           <td><code>S</code>
     *           <td>Millisecond
     *           <td>Number
     *           <td><code>978</code>
     *       <tr bgcolor="#eeeeff">
     *           <td><code>z</code>
     *           <td>Time zone
     *           <td>General time zone
     *           <td><code>Pacific Standard Time</code>; <code>PST</code>; <code>GMT-08:00</code>
     *       <tr>
     *           <td><code>Z</code>
     *           <td>Time zone
     *           <td>RFC 822 time zone
     *           <td><code>-0800</code>
     *   </table>
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
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports() {

        // CLK-309. Skip imports if dateField is disabled or readonly.
        if (isReadonly() || isDisabled()) {
            return null;
        }

        Context context = getContext();

        Object args[] = {
                context.getRequest().getContextPath(),
                getStyle(),
                getLocale().getLanguage(),
                getId(),
                getCalendarPattern(),
                new Boolean(getShowTime()),
                new Integer(getFirstDayOfWeek() - 1),
                ClickUtils.getResourceVersionIndicator(context)
        };

        return MessageFormat.format(HTML_IMPORTS, args);
    }

    /**
     * @see net.sf.click.control.Field#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name) {
        super.setName(name);
        setAttribute("id", getName() + "_field");
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
     * Set the DateField value.
     *
     * @param value the DateField value
     */
    public void setValue(String value) {
        if (value != null && value.length() > 0) {
            try {
                Date parsedDate = getDateFormat().parse(value);

                // Cache date for subsequent retrievals
                date = new Date(parsedDate.getTime());

            } catch (ParseException pe) {
                date = null;
            }

        } else {
            date = null;
        }
        super.setValue(value);
    }

    /**
     * Return the field Date value, or null if value was empty or a parsing
     * error occured.
     *
     * @return the Date object representation of the Field value
     */
    public Object getValueObject() {
        return getDate();
    }

    /**
     * Set the date value of the field using the given object.
     *
     * @param object the object value to set
     */
    public void setValueObject(Object object) {
        if (object != null) {
            if (Date.class.isAssignableFrom(object.getClass())) {
                setDate((Date) object);

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
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext) {
        // Deploy DateField resources files
        for (int i = 0; i < CALENDAR_RESOURCES.length; i++) {
            String calendarFilename = "calendar" + CALENDAR_RESOURCES[i];
            String calendarResource =
                "/net/sf/click/extras/control/calendar/" + calendarFilename;

            ClickUtils.deployFile(servletContext,
                                  calendarResource,
                                  "click/calendar");
        }
    }

    /**
     * Render the HTML representation of the DateField.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        super.render(buffer);

        if (!isReadonly() && !isDisabled()) {
            Context context = getContext();
            buffer.append("<img align=\"top\" ");
            buffer.append("style=\"cursor:hand\" src=\"");
            buffer.append(context.getRequest().getContextPath());
            buffer.append("/click/calendar/calendar");
            buffer.append(ClickUtils.getResourceVersionIndicator(context));
            buffer.append(".gif\"");
            String id = getId();
            if (id != null) {
                buffer.append(" id=\"");
                buffer.append(getId());
                buffer.append("-button\" ");
            }

            String calendarTitle = getMessage("calendar-image-title");
            buffer.appendAttribute("alt", calendarTitle);
            buffer.appendAttribute("title", calendarTitle);
            buffer.elementEnd();
        }
    }

    /**
     * Validate the DateField request submission.
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
     *   <li>/net/sf/click/extras/control/DateField.properties
     *     <ul>
     *       <li>date-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        if (formatPattern == null) {
            String msg = "dateFormat attribute is null for field: " + getName();
            throw new IllegalStateException(msg);
        }

        super.validate();

        if (isValid() && getValue().length() > 0) {
            SimpleDateFormat dateFormat = getDateFormat();
            dateFormat.setLenient(false);

            try {
                dateFormat.parse(getValue()).getTime();

            } catch (ParseException pe) {
                Object[] args = new Object[] {
                    getErrorLabel(), formatPattern
                };
                setError(getMessage("date-format-error", args));
            }
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the first day of the week. For example e.g., Sunday in US,
     * Monday in France and Australia.
     *
     * @return the first day of the week
     */
    protected int getFirstDayOfWeek() {
        Locale locale = getLocale();

        Calendar calendar = Calendar.getInstance(getLocale());

        int dayOfWeek = calendar.getFirstDayOfWeek();

        if ("AU".equals(locale.getCountry())) {
            dayOfWeek += 1;
        }

        return dayOfWeek;
    }

    /**
     * Returns the <tt>Locale</tt> that should be used in this control.
     *
     * @return the locale that should be used in this control
     */
    protected Locale getLocale() {
        Locale locale = null;

        locale = getContext().getLocale();
        String lang = locale.getLanguage();
        if (Arrays.binarySearch(SUPPORTTED_LANGUAGES, lang) >= 0) {
            return locale;
        }

        locale = Locale.getDefault();
        lang = locale.getLanguage();
        if (Arrays.binarySearch(SUPPORTTED_LANGUAGES, lang) >= 0) {
            return locale;
        }

        return Locale.ENGLISH;
    }

    /**
     * Return the JavaScript Calendar pattern for the given Java DateFormat
     * pattern.
     *
     * @param pattern the Java DateFormat pattern
     * @return JavaScript Calendar pattern
     */
    protected String parseDateFormatPattern(String pattern) {
        HtmlStringBuffer jsPattern = new HtmlStringBuffer(20);
        int tokenStart = -1;
        int tokenEnd = -1;
        boolean debug = false;

        for (int i = 0; i < pattern.length(); i++) {
            char aChar = pattern.charAt(i);
            if (debug) {
                System.err.print("[" + i + "," + tokenStart + "," + tokenEnd
                                 + "]=" + aChar);
            }

            // If character is in SimpleDateFormat pattern character set
            if ("GyMwWDdFEaHkKhmsSzZ".indexOf(aChar) == - 1) {
                if (debug) {
                    System.err.println(" N");
                }
                if (tokenStart > - 1) {
                    tokenEnd = i;
                }
            } else {
                if (debug) {
                    System.err.println(" Y");
                }
                if (tokenStart == - 1) {
                    tokenStart = i;
                }
            }

            if (tokenStart > -1) {

                if (tokenEnd == -1 && i == pattern.length() - 1) {
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
                    } else if ("M".equals(token)) {
                        jsPattern.append("%m");
                    } else if ("dd".equals(token)) {
                        jsPattern.append("%d");
                    } else if ("d".equals(token)) {
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
                        System.err.println("token[" + tokenStart + ","
                                           + tokenEnd + "]='" + token + "'");
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
