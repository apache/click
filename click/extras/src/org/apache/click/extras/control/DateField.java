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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.click.control.TextField;
import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides a Date Field control: &nbsp; &lt;input type='text'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td style="vertical-align:baseline">Date Field</td>
 * <td style="vertical-align:baseline"><input type='text' size='20' title='DateField Control' value='15 Mar 2006'/></td>
 * </tr>
 * </table>
 *
 * The DateField control provides a Date entry field where users can key in a
 * Date value.
 * <p/>
 * Example:
 * <pre class="prettyprint">
 * public MyPage extends Page {
 *
 *     public void onInit() {
 *         Form form = new Form("form");
 *
 *         // Create new DateField with default date format: 'dd MMM yyyy'
 *         DateField dateField = new DateField("dateField");
 *
 *         // You can change the format to: 'yyyy-MM-dd'
 *         dateField.setFormatPattern("yyyy-MM-dd");
 *
 *         // Finally add dateField to form
 *         form.add(dateField);
 *     }
 * } </pre>
 *
 * See also W3C HTML reference
 * <a class="external" target="_blank" title="W3C HTML 4.01 Specification"
 *    href="http://www.w3.org/TR/html401/interact/forms.html#h-17.4">INPUT</a>
 *
 * @author Malcolm Edgar
 */
public class DateField extends TextField {

    // ----------------------------------------------------- Instance Variables

    /** The DateField's date value. */
    protected Date date;

    /** The date format. */
    protected SimpleDateFormat dateFormat;

    /** The date format pattern value. */
    protected String formatPattern;

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
    }

    /**
     * @see org.apache.click.control.Field#setName(String)
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
     *   <li>/org/apache/click/extras/control/DateField.properties
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

    /**
     * Render the HTML representation of the DateField. Note the button label is
     * rendered as the HTML "value" attribute.
     *
     * @see #toString()
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer) {
        // Set default title
        if (getTitle() == null) {
            setTitle(getMessage("date-title", formatPattern));
        }
        super.render(buffer);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Returns the <tt>Locale</tt> that should be used in this control.
     *
     * @return the locale that should be used in this control
     */
    protected Locale getLocale() {
        return getContext().getLocale();
    }
}
