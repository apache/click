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
package org.apache.click.extras.prototype;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.apache.click.Context;
import org.apache.click.element.CssImport;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.extras.control.DateField;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a Calendar Field control:   &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td style="vertical-align:baseline">Calendar Field</td>
 * <td style="vertical-align:baseline"><input type='text' size='20' title='CalendarField Control' value='12 May 2009'/><img align='top' hspace='2' style='cursor:hand' src='calendar.gif' title='Calendar'/></td>
 * </tr>
 * </table>
 *
 * The CalendarField control provides a Date entry field and a popup Calendar
 * &lt;div&gt;. Users can either key in a Date value or select a Date using the
 * Calendar.
 * <p/>
 * Example:
 * <pre class="prettyprint">
 * public MyPage extends Page {
 *
 *     public void onInit() {
 *         Form form = new Form("form");
 *
 *         // Create new CalendarField with default date format: 'dd MMM yyyy'
 *         CalendarField calendarField = new CalendarField("calendar");
 *
 *         // You can change the format to: 'yyyy-MM-dd'
 *         calendarField.setFormatPattern("yyyy-MM-dd");
 *
 *         // Finally add calendarField to form
 *         form.add(calendarField);
 *
 *         addControl(form);
 *     }
 * } </pre>
 *
 * <p/>
 * The Calendar popup is provided by the <a target="_blank" class="external" href="http//www.prototypejs.org">Prototype</a>
 * based <a target="_blank" class="external" href="http://code.google.com/p/calendardateselect/">CalendarDateSelect</a>
 * library. The Calendar popup is created as a &lt;div&gt; element using JavaScript.
 * To enable the Calendar popup, reference <span class="blue">$headElements</span>
 * and <span class="blue">$jsElements</span> in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$headElements</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$form</span>
 * &lt;/body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsElements</span> </pre>
 *
 * The default Calendar style is 'default' which has a gray theme.
 * The Calendar styles include:
 * <ul style="margin-top: 0.5em;">
 * <li>blue</li>
 * <li>default</li>
 * <li>plain</li>
 * <li>red</li>
 * <li>silver</li>
 * </ul>
 *
 * The DateField JavaScript, CSS and image resources are automatically deployed
 * to the <tt>click/calendar</tt> web directory on application startup.
 *
 * @author Malcolm Edgar
 * @author Bob Schellink
 */
public class CalendarField extends DateField {

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /** Supported locales. */
    static final String[] SUPPORTTED_LANGUAGES =
        {"de", "fi", "fr", "pl", "pt", "ru"};

    // ----------------------------------------------------- Instance Variables

    /** The JavaScript Calendar pattern. */
    protected String calendarPattern;

    /** The Calendar popup show time display bar flag. */
    protected boolean showTime;

    /**
     * The Calendar CSS style, default value: <tt>default</tt>.
     * Available styles include:
     * <tt>[blue, default, plain, red, silver]</tt>
     */
    protected String style = "default";

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the Calendar Field with the given name.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     */
    public CalendarField(String name) {
        super(name);
    }

    /**
     * Construct the Calendar Field with the given name and label.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public CalendarField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the Calendar Field with the given name and required status.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public CalendarField(String name, boolean required) {
        super(name, required);
    }


    /**
     * Construct the Calendar Field with the given name, label and required status.
     * <p/>
     * The date format pattern will be set to <tt>dd MMM yyyy</tt>.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public CalendarField(String name, String label, boolean required) {
        super(name, label, required);
    }

    /**
     * Construct the Calendar Field with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public CalendarField(String name, String label, int size) {
        super(name, label, size);
    }

    /**
     * Construct the Calendar Field with the given name, label, size and
     * required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     * @param required the field required status
     */
    public CalendarField(String name, String label, int size, boolean required) {
        super(name, label, size, required);
    }

    /**
     * Create a Calendar Field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CalendarField() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the JavaScript Calendar pattern. The Calendar pattern
     * is defined when you set the format pattern.
     *
     * @return the JavaScript Calendar pattern
     */
    public String getCalendarPattern() {
        return calendarPattern;
    }

    /**
     * @see org.apache.click.extras.control.DateField
     *
     * @param pattern the SimpleDateFormat pattern
     */
    public void setFormatPattern(String pattern) {
        super.setFormatPattern(pattern);
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
        return null;
    }

    /**
     * Return the CalendarField <tt>prototype.js</tt>,
     * <tt>calendar_date_select.js</tt>, <tt>{lang}.js</tt>
     * and <tt>{style}.css</tt> includes.
     * <p/>
     * This method delegates to {@link #addCalendarOptions(java.util.List)} to
     * include the Calendar Options script.
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public List getHeadElements() {
        // CLK-309. Skip imports if dateField is disabled or readonly.
        if (isReadonly() || isDisabled()) {
            return super.getHeadElements();
        }

        // Check that the field id has been set
        String fieldName = getName();
        if (fieldName == null) {
            throw new IllegalStateException("CalendarField name"
                + " is not defined. Set the name before calling"
                + " getHeadElements().");
        }

        String language = getLocale().getLanguage();

        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new CssImport("/click/prototype/calendar/" + getStyle() + ".css"));
            headElements.add(new JsImport("/click/prototype/prototype.js"));
            headElements.add(new JsImport("/click/prototype/calendar/calendar_date_select.js"));

            // English is default language, only include language pack if other
            // than English
            if (!"en".equals(language)) {
                JsImport jsImport = new JsImport("/click/prototype/calendar/" + getLocale().getLanguage() + ".js");
                jsImport.setAttribute("charset", "UTF-8");
                headElements.add(jsImport);
            }
        }

        addCalendarOptions(headElements);

        return headElements;
    }

    /**
     * Return true if the Calendar popup will show the time display bar.
     *
     * @return true if the Calendar popup will show the time display bar
     */
    public boolean getShowTime() {
        return showTime;
    }

    /**
     * Set the Calendar popup show the time display bar flag.
     *
     * @param showTime the flag to show the Calendar time display bar
     */
    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    /**
     * Return the Calendar CSS style.
     * <p/>
     * Available styles include: <tt>[blue, default, plain, red, silver]</tt>.
     *
     * @return the Calendar CSS style
     */
    public String getStyle() {
        return style;
    }

    /**
     * Set the Calendar CSS style.
     *
     * @param style the Calendar CSS style
     */
    public void setStyle(String style) {
        if (style == null) {
            throw new IllegalArgumentException("Null style parameter");
        }
        this.style = style;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the HTML representation of the CalendarField.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        String help = getHelp();
        // Nullify help to ensure it is not rendered by super impl.
        if (help != null) {
            setHelp(null);
        }

        super.render(buffer);

        if (!isReadonly() && !isDisabled()) {
            Context context = getContext();
            buffer.append("<img align=\"top\" ");
            buffer.append("style=\"cursor:hand\" src=\"");
            buffer.append(context.getRequest().getContextPath());
            buffer.append("/click/prototype/calendar/calendar");
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

        if (help != null) {
            buffer.append(help);
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Add the calendar options as a script to the list of head elements.
     * <p/>
     * The default option script will render as (depending on the values off
     * course):
     *
     * <pre class="prettyprint">
     * document.observe('dom:loaded', function(){
     *   Event.observe('my-calendar-button', 'click', function(){
     *     Date.first_day_of_week=0;
     *     calendar = new CalendarDateSelect($('my-calendar'), {
     *       minute_interval: 1,
     *       popup_by: 'my-calendar-button',
     *       embedded: false,
     *       time: 'mixed',
     *       formatValue: 'dd MMM yyyy',
     *       year_range: [1930,2050]});
     *     });
     * }); </pre>
     *
     * You can override this method to set your own options using a
     * {@link org.apache.click.element.JsScript}.
     *
     * @param headElements the list of head elements to include for this control
     */
    protected void addCalendarOptions(List headElements) {
        String fieldId = getId();
        String imgId = fieldId + "-button";

        JsScript script = new JsScript();
        script.setId(fieldId + "_calendar_date_select");

        // Note the Calendar options script is recreated and checked if it
        // is contained in the headElement. This caters for when the field is
        // used in a fly-weight pattern such as FormTable.
        if (!headElements.contains(script)) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(150);
            buffer.append("document.observe('dom:loaded', function(){");
            buffer.append("  Event.observe('").append(imgId).append(
                "', 'click', function(){");
            buffer.append("  Date.first_day_of_week=").append(getFirstDayOfWeek() - 1);
            buffer.append(";    calendar = new CalendarDateSelect($('").append(
                fieldId).append("'), {");

            buffer.append("minute_interval: 1, popup_by: '").append(imgId).append(
                "'");
            buffer.append(", embedded: false");
            buffer.append(", time: ").append(getShowTime() ? "'mixed'" : "false");
            buffer.append(", formatValue: '").append(getCalendarPattern()).append(
                "'");
            buffer.append(", year_range: [1930,2050]});");

            buffer.append("  });");
            buffer.append("});");
            script.setContent(buffer);
            headElements.add(script);
        }
    }

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
                        jsPattern.append("yyyy");
                    } else if ("yy".equals(token)) {
                        jsPattern.append("yy");
                    } else if ("y".equals(token)) {
                        jsPattern.append("y");
                    } else if ("MMMM".equals(token)) {
                        jsPattern.append("MMM");
                    } else if ("MMM".equals(token)) {
                        jsPattern.append("NNN");
                    } else if ("MM".equals(token)) {
                        jsPattern.append("MM");
                    } else if ("M".equals(token)) {
                        jsPattern.append("M");
                    } else if ("dd".equals(token)) {
                        jsPattern.append("dd");
                    } else if ("d".equals(token)) {
                        jsPattern.append("d");
                    } else if ("EEEE".equals(token)) {
                        jsPattern.append("EE");
                    } else if ("EEE".equals(token)) {
                        jsPattern.append("E");
                    } else if ("EE".equals(token)) {
                        jsPattern.append("E");
                    } else if ("E".equals(token)) {
                        jsPattern.append("E");
                    } else if ("aaa".equals(token)) {
                        jsPattern.append("a");
                    } else if ("aa".equals(token)) {
                        jsPattern.append("a");
                    } else if ("a".equals(token)) {
                        jsPattern.append("a");
                    } else if ("HH".equals(token)) {
                        jsPattern.append("HH");
                        setShowTime(true);
                    } else if ("H".equals(token)) {
                        jsPattern.append("H");
                        setShowTime(true);
                    } else if ("hh".equals(token)) {
                        jsPattern.append("hh");
                        setShowTime(true);
                    } else if ("h".equals(token)) {
                        jsPattern.append("h");
                        setShowTime(true);
                    } else if ("mm".equals(token)) {
                        jsPattern.append("mm");
                        setShowTime(true);
                    } else if ("m".equals(token)) {
                        jsPattern.append("m");
                        setShowTime(true);
                    } else if ("ss".equals(token)) {
                        jsPattern.append("ss");
                        setShowTime(true);
                    } else if ("s".equals(token)) {
                        jsPattern.append("s");
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
