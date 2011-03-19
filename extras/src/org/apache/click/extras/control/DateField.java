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
package org.apache.click.extras.control;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.click.Context;
import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a Date Field control:   &lt;input type='text'&gt;&lt;img&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td style="vertical-align:baseline">Date Field</td>
 * <td style="vertical-align:baseline"><input type='text' size='20' title='DateField Control' value='12 May 2009'/><img align='top' hspace='2' style='cursor:hand' src='calendar.gif' title='Calendar'/></td>
 * </tr>
 * </table>
 *
 * The DateField control provides a Date entry field and a popup Calendar
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
 *         // Create new DateField with default date format: 'dd MMM yyyy'
 *         DateField dateField = new DateField("date");
 *
 *         // You can change the format to: 'yyyy-MM-dd'
 *         dateField.setFormatPattern("yyyy-MM-dd");
 *
 *         // Finally add dateField to form
 *         form.add(dateField);
 *
 *         addControl(form);
 *     }
 * } </pre>
 *
 * <a name="resources"></a>
 * <h3>CSS and JavaScript resources</h3>
 *
 * The Calendar popup is provided by the <a target="_blank" class="external" href="http//www.prototypejs.org">Prototype</a>
 * based <a target="_blank" class="external" href="http://code.google.com/p/calendardateselect/">CalendarDateSelect</a>
 * library.
 * <p/>
 * <b>Please note:</b> if you don't want to have a dependency on the
 * Prototype library you can use the <a class="external" target="_blank" href="http://code.google.com/p/click-calendar/">Click Calendar</a>
 * CalendarField which is based on the <a class="external" target="_blank" href="http://www.dynarch.com/">Dynarch.com</a>
 * library. Consider this option when using an alternative JavaScript library
 * than Prototype, such as <a class="external" target="_blank" href="http://jquery.com">JQuery</a>.
 * <p/>
 * Alternatively you can switch off the Calendar popup by setting the
 * {@link #setShowCalendar(boolean)} to false. No JavaScript and CSS will be
 * included when this option is false.
 * <p/>
 * The DateField control makes use of the following resources
 * (which Click automatically deploys to the application directories,
 * <tt>/click/calendar</tt> and <tt>/click/prototype</tt>):
 *
 * <ul>
 * <li><tt>click/calendar/{style}.css</tt> - where {style} is a specific Calendar style e.g. <tt>default</tt>, <tt>red</tt>, <tt>blue</tt> etc.</li>
 * <li><tt>click/prototype/prototype.js</tt></li>
 * <li><tt>click/calendar/calendar_date_select.js</tt></li>
 * </ul>
 *
 * The Calendar popup is created as a &lt;div&gt; element using JavaScript.
 * To enable the Calendar popup, reference <span class="blue">$headElements</span>
 * and <span class="blue">$jsElements</span> in the page template. For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$headElements</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 *
 * <span class="red">$form</span>
 *
 * <span class="blue">$jsElements</span>
 * &lt;/body&gt;
 * &lt;/html&gt; </pre>
 *
 * <a name="styles"></a>
 * <h3>Calendar Styles</h3>
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
 * <h3>Default date format pattern</h3>
 * The default date format pattern is set to the message <tt>"date-format-pattern"</tt>
 * defined in the resource bundle <tt>/org/apache/click/extras/control/DateField.properties</tt>.
 * <p/>
 * You can easily customize the default date format pattern by overriding the
 * <tt>"date-format-pattern"</tt> in your <tt>click-page<lang>.properties</tt>
 * files.
 * <p/>
 * An example src/click-page.properties file is shown below:
 * <pre class="prettyprint">
 * date-format-pattern=dd-MM-yyyy </pre>
 *
 * <h3>Localization</h3>
 * The following localization messages are available in the resource bundle:
 * <tt>/org/apache/click/extras/control/DateField.properties</tt>.
 * <ul>
 *   <li><tt>date-title</tt> - the <tt>title</tt> attribute of the input field.</li>
 *   <li><tt>calendar-image-title</tt> - the <tt>title</tt> attribute of the
 *       calendar image icon</tt></li>
 *   <li><tt>calendar-weekdays-heading</tt> - a comma separated list of
 *       abbreviated week day names starting with Sunday. The default value is
 *       <tt>"S,M,T,W,T,F,S"</tt>. If not defined for a locale, the abbreviated
 *       week day names from the JDK is used. The names are used as the column
 *       heading in the popup.</li>
 *   <li><tt>calendar-ok</tt> - the label on the "OK" button.</li>
 *   <li><tt>calendar-now</tt> - the label on the "Now" button.</li>
 *   <li><tt>calendar-today</tt> - the label on the "Today" button.</li>
 *   <li><tt>calendar-clear</tt> - the label on the "Clear" button.</li>
 * </ul>
 * The buttons are only shown when DateField popup also contains time:
 * <tt>field.setShowTime(true);</tt>
 * <p/>
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 */
public class DateField extends TextField {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Instance Variables -----------------------------------------------------

    /** The DateField's date value. */
    protected Date date;

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    /** The date format pattern value. */
    protected String formatPattern;

    /** The JavaScript Calendar pattern. */
    protected String calendarPattern;

    /** The Calendar popup show time display bar flag. */
    protected boolean showTime;

    /**
     * Flag indicating if the Calendar popup is displayed or not,
     * default value is true.
     */
    protected boolean showCalendar = true;

    /** The minimum year of the calendar, default value is 1930. */
    protected int minimumYear = 1930;

    /** The maximum year of the calendar, default value is 2050. */
    protected int maximumYear = 2050;

    /**
     * The Calendar CSS style, default value: <tt>default</tt>.
     * Available styles include:
     * <tt>[blue, default, plain, red, silver]</tt>
     */
    protected String style = "default";

    /** Indicates whether the help property should be rendered or not. */
    private boolean renderHelp = true;

    // Constructors -----------------------------------------------------------

    /**
     * Construct the Date Field with the given name.
     *
     * @param name the name of the field
     */
    public DateField(String name) {
        super(name);
    }

    /**
     * Construct the Date Field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public DateField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the Date Field with the given name and required status.
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
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the field required status
     */
    public DateField(String name, String label, boolean required) {
        super(name, label, required);
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
    }

    // Public Attributes ------------------------------------------------------

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
     * Return the SimpleDateFormat for the {@link #formatPattern} property.
     *
     * @return the SimpleDateFormat for the formatPattern
     */
    public SimpleDateFormat getDateFormat() {
        if (dateFormat == null) {
            String formatPattern = getFormatPattern();
            dateFormat = new SimpleDateFormat(formatPattern, getLocale());
        }
        return dateFormat;
    }

    /**
     * Return the date format pattern. If the date format pattern is not defined
     * it will be loaded through the method, {@link #loadFormatPattern()}.
     *
     * @return the date format pattern
     */
    public String getFormatPattern() {
        if (formatPattern == null) {
            loadFormatPattern();
        }
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
     * @see org.apache.click.control.Field#setName(String)
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * Return the Timestamp value, or null if value was empty
     * or a parsing error occurred.
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
     * Set the DateField value.
     *
     * @param value the DateField value
     */
    @Override
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
     * error occurred.
     *
     * @return the Date object representation of the Field value
     */
    @Override
    public Object getValueObject() {
        return getDate();
    }

    /**
     * Set the date value of the field using the given object.
     *
     * @param object the object value to set
     */
    @Override
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

    /**
     * Return the JavaScript Calendar pattern. The Calendar pattern is defined
     * when you set the {@link #getFormatPattern() format pattern}.
     * <p/>
     * If the date format pattern is not defined it will be loaded through the
     * method {@link #loadFormatPattern()}.
     *
     * @return the JavaScript Calendar pattern
     */
    public String getCalendarPattern() {
        if (calendarPattern == null) {
            loadFormatPattern();
        }
        return calendarPattern;
    }

    /**
     * Return the DateField HTML head imports statements for the following
     * resources:
     *
     * <ul>
     * <li><tt>click/calendar/{style}.css</tt> - where {style} is a specific
     * Calendar style e.g. <tt>default</tt>, <tt>red</tt>, <tt>blue</tt> etc.</li>
     * <li><tt>click/control.js</tt></li>
     * <li><tt>click/prototype/prototype.js</tt></li>
     * <li><tt>click/calendar/calendar_date_select.js</tt></li>
     * <li><tt>click/calendar/{lang}.js</tt> - where {lang} is the language
     * specified by the browser e.g. <tt>fr</tt> (French), <tt>de</tt> (German)
     * etc.</li>
     * </ul>
     *
     * This method delegates to {@link #addCalendarOptions(java.util.List)} to
     * include the Calendar Options script.
     *
     * @see org.apache.click.Control#getHeadElements()
     *
     * @return the HTML head import statements for the control
     */
    @Override
    public List<Element> getHeadElements() {
        // CLK-309. Skip imports if dateField is disabled, readonly or calendar
        // should not be displayed.
        if (isReadonly() || isDisabled() || !isShowCalendar()) {
            return super.getHeadElements();
        }

        // Check that the field id has been set
        String fieldName = getName();
        if (fieldName == null) {
            throw new IllegalStateException("DateField name"
                + " is not defined. Set the name before calling"
                + " getHeadElements().");
        }

        if (headElements == null) {
            headElements = super.getHeadElements();

            String versionIndicator = ClickUtils.getResourceVersionIndicator(getContext());

            headElements.add(new CssImport("/click/calendar/" + getStyle()
                + ".css", versionIndicator));
            headElements.add(new JsImport("/click/control.js",
                versionIndicator));
            headElements.add(new JsImport("/click/prototype/prototype.js",
                versionIndicator));
            headElements.add(new JsImport("/click/calendar/calendar_date_select.js",
                versionIndicator));

            String language = getLocale().getLanguage();
            // English is default language; only include translations for other
            // languages
            if (!"en".equals(language)) {
                addCalenderTranslations(headElements);
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
    public boolean isShowTime() {
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
     * Return true if the Calendar popup will be displayed, false otherwise.
     * Default value is true.
     *
     * @return true if the Calendar popup will be displayed, false otherwise
     */
    public boolean isShowCalendar() {
        return showCalendar;
    }

    /**
     * Set whether the Calendar popup is displayed or not. If set to false the
     * DateField will not include any JavaScript or CSS.
     *
     * @param showCalendar flag indicating whether the Calendar popup is
     * displayed or not
     */
    public void setShowCalendar(boolean showCalendar) {
        this.showCalendar = showCalendar;
    }

    /**
     * Return the minimum year of the Calendar, default value is 1930.
     *
     * @return the minimum year of the Calendar
     */
    public int getMinimumYear() {
        return minimumYear;
    }

    /**
     * Set the minimum year of the Calendar.
     *
     * @param minimumYear the minimum year of the Calendar
     */
    public void setMinimumYear(int minimumYear) {
        this.minimumYear = minimumYear;
    }

    /**
     * Return the maximum year of the Calendar, default value is 2050.
     *
     * @return the minimum year of the Calendar
     */
    public int getMaximumYear() {
        return maximumYear;
    }

    /**
     * Set the maximum year of the Calendar.
     *
     * @param maximumYear the maximum year of the Calendar
     */
    public void setMaximumYear(int maximumYear) {
        this.maximumYear = maximumYear;
    }

    /**
     * Return the Calendar CSS style.
     *
     * @return the Calendar CSS style
     */
    public String getStyle() {
        return style;
    }

    /**
     * Set the Calendar CSS style.
     * <p/>
     * Available styles are: <tt>[blue, default, plain, red, silver]</tt>.
     *
     * @param style the Calendar CSS style
     */
    public void setStyle(String style) {
        if (style == null) {
            throw new IllegalArgumentException("Null style parameter");
        }
        this.style = style;
    }

    /**
     * Return the field help text.
     *
     * @see org.apache.click.control.Field#getHelp()
     *
     * @return the help text of the Field
     */
    @Override
    public String getHelp() {
        // Conditionally render help property, otherwise return null.
        if (renderHelp) {
            return super.getHelp();
        }
        return null;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Validate the DateField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>field-required-error</li>
     *     </ul>
     *   </li>
     *   <li>/org/apache/click/extras/control/DateField.properties
     *     <ul>
     *       <li>date-format-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    @Override
    public void validate() {
        String formatPattern = getFormatPattern();

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

    /**
     * Render the HTML representation of the DateField. Note the button label is
     * rendered as the HTML "value" attribute.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    @Override
    public void render(HtmlStringBuffer buffer) {

        // Set default title
        if (getTitle() == null) {
            setTitle(getMessage("date-title", getFormatPattern()));
        }

        // Ensure help is not rendered by super implementation
        renderHelp = false;

        super.render(buffer);

        renderHelp = true;

        if (isShowCalendar()) {
            renderCalendarButton(buffer);
        }

        // Render help
        String help = getHelp();
        if (help != null) {
            buffer.append(help);
        }
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Render the calendar button HTML representation to the buffer.
     *
     * @param buffer the buffer to render the calendar button HTML
     * representation to
     */
    protected void renderCalendarButton(HtmlStringBuffer buffer) {

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
     * Add the calendar options as a script to the list of head elements.
     * <p/>
     * The default option script will render as (depending on the values):
     *
     * <pre class="prettyprint">
     * Click.addLoadEvent(function() {
     *   Event.observe('my-calendar-button', 'click', function(){
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
    protected void addCalendarOptions(List<Element> headElements) {
        String fieldId = getId();

        JsScript script = new JsScript();
        script.setId(fieldId + "-js-setup");

        // Note: the setup script is recreated and checked if it is contained in
        // the headElement. This check cater for when the field is used by another
        // Control using the fly-weight pattern eg. FormTable.
        if (!headElements.contains(script)) {

            // Script must be executed as soon as browser dom is ready
            script.setExecuteOnDomReady(true);

            HtmlStringBuffer buffer = new HtmlStringBuffer(150);

            buffer.append("Event.observe('").append(fieldId).append("-button").append("', 'click', function(){");
            buffer.append(" calendar = new CalendarDateSelect($('").append(fieldId).append("'), {");
            buffer.append("  minute_interval: 1, popup_by: '").append(fieldId).append("-button").append("',");
            buffer.append("  embedded: false,");
            buffer.append("  footer: false,");
            buffer.append("  buttons: ").append(isShowTime()).append(",");
            buffer.append("  time: ").append(isShowTime() ? "'mixed'," : "false,");
            buffer.append("  formatValue: '").append(getCalendarPattern()).append("',");
            buffer.append("  year_range: [").append(getMinimumYear()).append(",").append(getMaximumYear()).append("]");
            buffer.append(" });");
            buffer.append("});");

            script.setContent(buffer.toString());
            headElements.add(script);
        }
    }

    /**
     * Return the names of months and weekdays as a script.
     */
    protected void addCalenderTranslations(List<Element> headElements) {
        JsScript script = new JsScript();
        script.setId("datefield-js-setup-global");
        if (!headElements.contains(script)) {
            DateFormatSymbols dfs = new DateFormatSymbols(getLocale());

            HtmlStringBuffer buffer = new HtmlStringBuffer(150);
            buffer.append("Date.months=new Array(");
            generateJavaScriptArray(buffer, dfs.getMonths(), 0, 12);
            buffer.append(");\n");

            buffer.append("Date.monthAbbreviations=new Array(");
            generateJavaScriptArray(buffer, dfs.getShortMonths(), 0, 12);
            buffer.append(");\n");

            buffer.append("Date.dayNames=new Array(");
            generateJavaScriptArray(buffer, dfs.getWeekdays(),
                    Calendar.SUNDAY, Calendar.SATURDAY + 1);
            buffer.append(");\n");

            buffer.append("Date.dayAbbreviations=new Array(");
            generateJavaScriptArray(buffer, dfs.getShortWeekdays(),
                    Calendar.SUNDAY, Calendar.SATURDAY + 1);
            buffer.append(");\n");

            String[] weekdays = null;
            if (getMessages().containsKey("calendar-weekdays-heading")) {
                String headings = getMessage("calendar-weekdays-heading");
                weekdays = StringUtils.splitPreserveAllTokens("," + headings, ',');
            } else {
                weekdays = dfs.getShortWeekdays();
            }
            String[] days = new String[7];
            int firstDayOfWeek = getFirstDayOfWeek() - 1;
            for (int i = 0; i < 7; i++) {
                days[i] = weekdays[(i + firstDayOfWeek) % 7 + 1];
            }
            buffer.append("Date.weekdays=new Array(");
            generateJavaScriptArray(buffer, days, 0, 7);
            buffer.append(");\n");

            buffer.append("Date.first_day_of_week=").append(firstDayOfWeek).append(";\n");
            if (getMessages().containsKey("calendar-ok")) {
                buffer.append("_translations[\"OK\"] = \"");
                buffer.append(getMessage("calendar-ok"));
                buffer.append("\";\n");
            }
            if (getMessages().containsKey("calendar-now")) {
                buffer.append("_translations[\"Now\"] = \"");
                buffer.append(getMessage("calendar-now"));
                buffer.append("\";\n");
            }
            if (getMessages().containsKey("calendar-today")) {
                buffer.append("_translations[\"Today\"] = \"");
                buffer.append(getMessage("calendar-today"));
                buffer.append("\";\n");
            }
            if (getMessages().containsKey("calendar-clear")) {
                buffer.append("_translations[\"Clear\"] = \"");
                buffer.append(getMessage("calendar-clear"));
                buffer.append("\";\n");
            }

            script.setContent(buffer.toString());
            headElements.add(script);
        }
    }

    /**
     * Append a list of quoted names delimited by commas.
     * @param buffer the buffer to append to.
     * @param names the list of names.
     * @param start start index in the list.
     * @param end end index in the list.
     */
    private void generateJavaScriptArray(HtmlStringBuffer buffer,
           String[] names, int start, int end) {
        for (int i = start; i < end; i++) {
            buffer.append('"');
            buffer.append(names[i]);
            buffer.append('"');
            if (i < end - 1) {
                buffer.append(',');
            }
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

        if ("AU".equals(locale.getCountry())) {
            return Calendar.MONDAY;
        }
        Calendar calendar = Calendar.getInstance(locale);

        return calendar.getFirstDayOfWeek();
    }

    /**
     * Returns the <tt>Locale</tt> that should be used in this control.
     *
     * @return the locale that should be used in this control
     */
    protected Locale getLocale() {
        return getContext().getLocale();
    }

    /**
     * Load the default date format pattern. The format pattern is set to the
     * message <tt>"date-format-pattern"</tt> defined in the resource bundle
     * <tt>/org/apache/click/extras/control/DateField.properties</tt>.
     * <p/>
     * The default date format pattern for English is: <tt>"dd MMM yyyy"</tt>.
     */
    protected void loadFormatPattern() {
        String dateFormatPattern = getMessage("date-format-pattern");
        setFormatPattern(dateFormatPattern);
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
