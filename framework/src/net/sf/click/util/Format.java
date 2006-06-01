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
package net.sf.click.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Provides the default object for formatting the display of model objects
 * in Velocity templates and JSP pages.
 * <p/>
 * For Velocity templates a Format object is added to the Velocity Context using
 * the name "<span class="blue">format</span>", while for JSP pages an instance
 * is added as a request attribute using the same key.
 * <p/>
 * For example the following Page code adds a date to the model:
 *
 * <pre class="codeJava">
 * <span class="kw">public void</span> onGet() {
 *    Date date = order.deliveryDate();
 *    addModel("<span class="st">deliveryDate</span>", date);
 * } </pre>
 *
 * In the page template we use the format object:
 *
 * <pre class="codeHtml">
 * Delivery date: <span class="red">$format</span>.date(<span class="st">$deliveryDate</span>, "dd MMM yyyy") </pre>
 *
 * Which renders the output as:
 *
 * <table class="htmlExample" cellspacing="12">
 * <tr><td>
 * Delivery date: 21 Jan 2004
 * </td></tr>
 * </table>
 *
 * The format class can defined in the "click.xml" configuration file
 * using the syntax:
 *
 * <pre class="codeConfig">
 * &lt;format classname="<span class="st">com.mycorp.utils.Format</span>"/&gt; </pre>
 *
 * The format subclass must provide a public constructor which takes a single
 * Locale object argument. After a Page is
 * created its <a href="../Page.html#format">format</a> property is set.
 * The ClickServlet will then add this property to the Velocity Context.
 * <p/>
 * When subclassing Format ensure it is light weight object, as a new format
 * object will be created for every new Page.
 *
 * @see PageImports
 *
 * @author Malcolm Edgar
 */
public class Format {

    /** The request context locale. */
    protected Locale locale;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new Format object with the given locale.
     *
     * @param locale the locale to use to format objects
     */
    public Format(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Null locale parameter");
        }
        this.locale = locale;
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return the locale used to format objects.
     *
     * @return the locale used to format objects
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Returns the format empty string value: &nbsp; <tt>""</tt>.
     * <p/>
     * This method is designed to be overridden. If you need a different
     * empty string value simply override this method.
     * <p/>
     * Note the IE browser does not fully support CSS attribute: &nbsp;
     * <tt>table { empty-cells: show }</tt>. Also note returning
     * <tt>&amp;nbsp;</tt>
     * value will prevent AJAX XML responses being rendered in browsers.
     *
     * @return the formatter methods empty string value
     */
    public String getEmptyString() {
        return "";
    }

    /**
     * Return a currency formatted String value for the given number, using
     * the default Locale.
     * <p/>
     * If the number is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param number the number to format
     * @return a currency formatted number string
     */
    public String currency(Number number) {
        if (number != null) {
            NumberFormat format = NumberFormat.getCurrencyInstance(getLocale());

            return format.format(number.doubleValue());

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return a formatted current date string using the default DateFormat.
     *
     * @return a formatted date string
     */
    public String currentDate() {
        DateFormat format =
            DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale());

        return format.format(new Date());
    }

    /**
     * Return a formatted current date string using the given formatting
     * pattern. See SimpleDateFormat for information on the format
     * pattern string.
     *
     * @param pattern the SimpleDateFormat formatting pattern
     * @return a formatted date string
     * @throws IllegalArgumentException if the pattern string is null
     */
    public String currentDate(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Null pattern parameter");
        }

        SimpleDateFormat format =
            new SimpleDateFormat(pattern, getLocale());

        return format.format(new Date());
    }

    /**
     * Return a formatted date string using the given date and formatting
     * pattern. See SimpleDateFormat for information on the format
     * pattern string.
     * <p/>
     * If the date is null this method will return the {@link #getEmptyString()}
     * value.
     *
     * @param date the date value to format
     * @param pattern the SimpleDateFormat formatting pattern
     * @return a formatted date string
     * @throws IllegalArgumentException if the pattern string is null
     */
    public String date(Date date, String pattern) {
        if (date != null) {
            if (pattern == null) {
                throw new IllegalArgumentException("Null pattern parameter");
            }

            SimpleDateFormat format =
                new SimpleDateFormat(pattern, getLocale());

            return format.format(date);

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return a formatted date string using the given date and the default
     * DateFormat.
     * <p/>
     * If the date is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param date the date value to format
     * @return a formatted date string
     */
    public String date(Date date) {
        if (date != null) {
            DateFormat format =
                DateFormat.getDateInstance(DateFormat.DEFAULT, getLocale());

            return format.format(date);

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return a decimal formatted string using the given number and pattern.
     * See DecimalFormat for information on the format pattern string.
     * <p/>
     * If the number is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param number the number to format
     * @param pattern the decimal format pattern
     * @return the fornmatted decimal number
     * @throws IllegalArgumentException if the pattern string is null
     */
    public String decimal(Number number, String pattern) {
        if (number != null) {
            if (pattern == null) {
                throw new IllegalArgumentException("Null pattern parameter");
            }

            DecimalFormat format = new DecimalFormat(pattern);

            return format.format(number.doubleValue());

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return a decimal formatted string using the given number and pattern.
     * <p/>
     * If the number is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param number the number to format
     * @return the fornmatted decimal number
     */
    public String decimal(Number number) {
        if (number != null) {
            DecimalFormat format = new DecimalFormat();

            return format.format(number.doubleValue());

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return an email hyperlink using the given email address. If the
     * given value is not a valid email string it will be rendered as is
     * and not as a hyperlink.
     * <p/>
     * If the given value is blank then the {@link #getEmptyString()} value
     * will rendered instead.
     * <p/>
     * The format of the returned email string will be:
     * <pre class="codeHtml">
     * &lt;a href='mailto:email'&gt;email&lt;/a&gt; </pre>
     *
     * @param email the email address to hyperlink
     * @return a hyperlinked email
     */
    public String email(String email) {
        return email(email, null);
    }

    /**
     * Return an email hyperlink using the given email address. If the
     * given value is not a valid email string it will be rendered as is
     * and not as a hyperlink.
     * <p/>
     * If the given value is blank then the {@link #getEmptyString()} value
     * will rendered instead.
     * <p/>
     * The format of the returned email string will be:
     * <pre class="codeHtml">
     * &lt;a href='mailto:email' attribute&gt;email&lt;/a&gt; </pre>
     *
     * @param email the email address to hyperlink
     * @param attribute the anchor tag attribute to render
     * @return a hyperlinked email
     */
    public String email(String email, String attribute) {
        if (StringUtils.isNotBlank(email)) {
            if (email.indexOf('@') != -1
                && !email.startsWith("@")
                && !email.endsWith("@")) {

                HtmlStringBuffer buffer = new HtmlStringBuffer(128);
                buffer.elementStart("a");
                buffer.appendAttribute("href", "mailto:" + email);
                if (StringUtils.isNotBlank(attribute)) {
                    buffer.append(" ");
                    buffer.append(attribute);
                }
                buffer.closeTag();
                buffer.appendEscaped(email);
                buffer.elementEnd("a");

                return buffer.toString();

            } else {
                return email;
            }

        } else {
            return getEmptyString();
        }
    }

    /**
     * Escape the given object value as a HTML string.
     * <p/>
     * If the value is null this method will return the
     * {@link #getEmptyString()} value.
     * <p>
     * Implementation is provided by Jakarta Commons Lang utility:
     * <tt>StringEscapeUtils.escapeHtml(String)</tt>
     *
     * @param value unescaped HTML
     * @return the HTML escaped string
     */
    public String html(Object value) {
        if (value != null) {
            return StringEscapeUtils.escapeHtml(value.toString());
        } else {
            return getEmptyString();
        }
    }

    /**
     * Escape the given object value as a JavaScript string, or "" if the object
     * is null.
     * <p>
     * Implementation is provided by Jakarta Commons Lang utility:
     * <tt>StringEscapeUtils.escapeJavaScript(String)</tt>
     *
     * @param value unescaped JavaScript
     * @return the JavaScript escaped string
     */
    public String javascript(String value) {
        if (value != null) {
            return StringEscapeUtils.escapeJavaScript(value);
        } else {
            return "";
        }
    }

    /**
     * Return the value string limited to maxlength characters. If the string
     * gets curtailed, "..." is appended to it.
     * <p/>
     * Adapted from Velocity Tools Formatter.
     *
     * @param value the string value to limit the length of
     * @param maxlength the maximum string length
     * @return a length limited string
     */
    public String limitLength(String value, int maxlength) {
        return limitLength(value, maxlength, "...");
    }

    /**
     * Return the value string limited to maxlength characters. If the string
     * gets curtailed and the suffix parameter is appended to it.
     * <p/>
     * Adapted from Velocity Tools Formatter.
     *
     * @param value the string value to limit the length of
     * @param maxlength the maximum string length
     * @param suffix the suffix to append to the length limited string
     * @return a length limited string
     */
    public String limitLength(String value, int maxlength, String suffix) {
        String ret = value;
        if (value.length() > maxlength) {
            ret = value.substring(0, maxlength - suffix.length()) + suffix;
        }
        return ret;
    }

    /**
     * Return an hyperlink using the given URL or email address value. If the
     * given value is not a valid email string or URL it will note be
     * hyperlinked and will be rendered as is.
     * <p/>
     * If the given value is blank then the {@link #getEmptyString()} value
     * will rendered instead.
     *
     * @param value the URL or email address to hyperlink
     * @return a hyperlinked URL or email address
     */
    public String link(String value) {
        return link(value, null);
    }

    /**
     * Return an hyperlink using the given URL or email address value. If the
     * given value is not a valid email string or URL it will note be
     * hyperlinked and will be rendered as is.
     * <p/>
     * If the given value is blank then the {@link #getEmptyString()} value
     * will rendered instead.
     *
     * @param value the URL or email address to hyperlink
     * @param attribute the anchor tag attribute to render
     * @return a hyperlinked URL or email address
     */
    public String link(String value, String attribute) {
        if (StringUtils.isNotBlank(value)) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(128);

            // If email
            if (value.indexOf('@') != -1
                && !value.startsWith("@")
                && !value.endsWith("@")) {

                buffer.elementStart("a");
                buffer.appendAttribute("href", "mailto:" + value);
                if (StringUtils.isNotBlank(attribute)) {
                    buffer.append(" ");
                    buffer.append(attribute);
                }
                buffer.closeTag();
                buffer.appendEscaped(value);
                buffer.elementEnd("a");

            } else if (value.startsWith("http")) {
                int index = value.indexOf("//");
                if (index != -1) {
                    index += 2;
                } else {
                    index = 0;
                }
                buffer.elementStart("a");
                buffer.appendAttribute("href", value);
                if (StringUtils.isNotBlank(attribute)) {
                    buffer.append(" ");
                    buffer.append(attribute);
                }
                buffer.closeTag();
                buffer.appendEscaped(value.substring(index));
                buffer.elementEnd("a");

            } else if (value.startsWith("www")) {
                buffer.elementStart("a");
                buffer.appendAttribute("href", "http://" + value);
                if (StringUtils.isNotBlank(attribute)) {
                    buffer.append(" ");
                    buffer.append(attribute);
                }
                buffer.closeTag();
                buffer.appendEscaped(value);
                buffer.elementEnd("a");

            } else {
                buffer.append(value);
            }

            return buffer.toString();
        }

        return getEmptyString();
    }

    /**
     * Return a percentage formatted number string using number.
     * <p/>
     * If the number is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param number the number value to format
     * @return a percentage formatted number string
     */
    public String percentage(Number number) {
        if (number != null) {
            NumberFormat format = NumberFormat.getPercentInstance(getLocale());

            return format.format(number.doubleValue());

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return a formatted time string using the given date and the default
     * DateFormat.
     * <p/>
     * If the date is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param date the date value to format
     * @return a formatted time string
     */
    public String time(Date date) {
        if (date != null) {
            DateFormat format =
                DateFormat.getTimeInstance(DateFormat.DEFAULT, getLocale());

            return format.format(date);

        } else {
            return getEmptyString();
        }
    }

    /**
     * Return the string representation of the given object.
     * <p/>
     * If the object is null this method will return the
     * {@link #getEmptyString()} value.
     *
     * @param object the object to format
     * @return the string representation of the object
     */
    public String string(Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return getEmptyString();
        }
    }
}
