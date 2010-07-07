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
package org.apache.click.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Mock implementation of {@link javax.servlet.http.HttpServletResponse}.
 * <p/>
 * Implements all of the methods from the standard HttpServletResponse class
 * plus helper methods to aid viewing the generated
 * response.
 * <p/>
 * This class was adapted from <a href="http://wicket.apache.org">Apache Wicket</a>.
 */
public class MockResponse implements HttpServletResponse {

    // -------------------------------------------------------- Constants

    /* BEGIN: This code comes from Jetty 6.1.1 */

    /** Days of the week. */
    private final static String[] DAYS = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu",
        "Fri", "Sat"};

    /** Months of the year. */
    private final static String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"
    };

    /** Indicates that response is in binary mode. */
    private static final int MODE_BINARY = 1;

    /** Indicates that response is in neither binary nor text mode. */
    private static final int MODE_NONE = 0;

    /** Indicates that response is in text (character) mode. */
    private static final int MODE_TEXT = 2;

    // -------------------------------------------------------- Instance Variables

    /** The response byte stream. */
    private ByteArrayOutputStream byteStream;

    /** The response character encoding, defaults to UTF-8. */
    private String characterEncoding = "UTF-8";

    /**
     * The response code, defaults to
     * {@link javax.servlet.http.HttpServletResponse#SC_OK}.
     */
    private int code = HttpServletResponse.SC_OK;

    /** The response list of cookies. */
    private final List<Cookie> cookies = new ArrayList<Cookie>();

    /** The response error message. */
    private String errorMessage = null;

    /** The response headers map. */
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();

    /** The response locale. */
    private Locale locale = null;

    /** Indicates the response mode, defaults to {@link #MODE_NONE}. */
    private int mode = MODE_NONE;

    /** The response print writer. */
    private PrintWriter printWriter;

    /** The location that was redirected to. */
    private String redirectUrl = null;

    /** The response servlet stream. */
    private ServletOutputStream servletStream;

    /**
     * The response status, defaults to
     * {@link javax.servlet.http.HttpServletResponse#SC_OK}.
     */
    private int status = HttpServletResponse.SC_OK;

    /** The response string writer. */
    private StringWriter stringWriter;

    /**
     * Create the response object.
     */
    public MockResponse() {
        initialize();
    }

    /**
     * Add a cookie to the response.
     *
     * @param cookie The cookie to add
     */
    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * Add a date header.
     *
     * @param name The header value
     * @param l The long value
     */
    public void addDateHeader(String name, long l) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        addHeader(name, df.format(new Date(l)));
    }

    /**
     * Add the given header value, including an additional entry if one already
     * exists.
     *
     * @param name The name for the header
     * @param value The value for the header
     */
    public void addHeader(final String name, final String value) {
        List<String> list = headers.get(name);
        if (list == null) {
            list = new ArrayList<String>(1);
            headers.put(name, list);
        }
        list.add(value);
    }

    /**
     * Add an int header value.
     *
     * @param name The header name
     * @param i The value
     */
    public void addIntHeader(final String name, final int i) {
        addHeader(name, "" + i);
    }

    /**
     * Check if the response contains the given header name.
     *
     * @param name The name to check
     * @return Whether header in response or not
     */
    public boolean containsHeader(final String name) {
        return headers.containsKey(name);
    }

    /**
     * Encode the redirectUrl. Does no changes as this test
     * implementation uses cookie based url tracking.
     *
     * @param url The url to encode
     * @return The encoded url
     */
    public String encodeRedirectUrl(final String url) {
        return url;
    }

    /**
     * Encode the redirectUrl. Does no changes as this test
     * implementation uses cookie based url tracking.
     *
     * @param url The url to encode
     * @return The encoded url
     */
    public String encodeRedirectURL(final String url) {
        return url;
    }

    /**
     * Encode thr URL. Does no changes as this test implementation uses cookie
     * based url tracking.
     *
     * @param url The url to encode
     * @return The encoded url
     */
    public String encodeUrl(final String url) {
        return url;
    }

    /**
     * Encode thr URL. Does no changes as this test implementation uses cookie
     * based url tracking.
     *
     * @param url The url to encode
     * @return The encoded url
     */
    public String encodeURL(final String url) {
        return url;
    }

    /**
     * Flush the buffer.
     *
     * @throws IOException if exception occurs during flush
     */
    public void flushBuffer() throws IOException {
    }

    /**
     * Get the binary content that was written to the servlet stream.
     *
     * @return The binary content
     */
    public byte[] getBinaryContent() {
        return byteStream.toByteArray();
    }

    /**
     * Return the current buffer size.
     *
     * @return The buffer size
     */
    public int getBufferSize() {
        if (mode == MODE_NONE) {
            return 0;
        } else if (mode == MODE_BINARY) {
            return byteStream.size();
        } else {
            return stringWriter.getBuffer().length();
        }
    }

    /**
     * Get the character encoding of the response.
     *
     * @return The character encoding
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Get the response code for this request.
     *
     * @return The response code
     */
    public int getCode() {
        return code;
    }

    /**
     * Get all of the cookies that have been added to the response.
     *
     * @return The collection of cookies
     */
    public Collection<Cookie> getCookies() {
        return cookies;
    }

    /**
     * Get the text document that was written as part of this response.
     *
     * @return The document
     */
    public String getDocument() {
        if (mode == MODE_BINARY) {
            return new String(byteStream.toByteArray());
        } else {
            return stringWriter.getBuffer().toString();
        }
    }

    /**
     * Get the error message.
     *
     * @return The error message, or null if no message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Return the value of the given named header.
     *
     * @param name The header name
     * @return The value, or null
     */
    public String getHeader(final String name) {
        List<String> l = headers.get(name);
        if (l == null || l.size() < 1) {
            return null;
        } else {
            return l.get(0);
        }
    }

    /**
     * Get the names of all of the headers.
     *
     * @return The header names
     */
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * Get the encoded locale.
     *
     * @return The locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get the output stream for writing binary data from the servlet.
     *
     * @return The binary output stream.
     */
    public ServletOutputStream getOutputStream() {
        if (mode == MODE_TEXT) {
            throw new IllegalArgumentException("Can't write binary after "
                + "already selecting text");
        }
        mode = MODE_BINARY;
        return servletStream;
    }

    /**
     * Get the location that was redirected to.
     *
     * @return The redirect url, or null if not a redirect
     */
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /**
     * Get the status code.
     *
     * @return The status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the print writer for writing text output for this response.
     *
     * @return The writer
     * @throws java.io.IOException - if an input or output exception occurred
     * @throws IllegalStateException - if the getOutputStream method has
     * already been called for this response object
     */
    public PrintWriter getWriter() throws IOException {
        if (mode == MODE_BINARY) {
            throw new IllegalStateException("Can't write text after "
                + "getOutputStream() have already been called.");
        }
        mode = MODE_TEXT;
        return printWriter;
    }

    /**
     * Reset the response ready for reuse.
     */
    public void initialize() {
        cookies.clear();
        headers.clear();
        code = HttpServletResponse.SC_OK;
        errorMessage = null;
        redirectUrl = null;
        status = HttpServletResponse.SC_OK;
        characterEncoding = "UTF-8";
        locale = null;

        byteStream = new ByteArrayOutputStream();
        servletStream = new ServletOutputStream() {

            @Override
            public void write(int b) {
                byteStream.write(b);
            }
        };
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter) {

            @Override
            public void close() {
            // Do nothing
            }

            @Override
            public void flush() {
            // Do nothing
            }
        };
        mode = MODE_NONE;
    }

    /**
     * Delegate to initialise method.
     */
    public void reset() {
        initialize();
    }

    /**
     * Always returns false.
     *
     * @return Always false
     */
    public boolean isCommitted() {
        return false;
    }

    /**
     * Return whether the servlet returned an error code or not.
     *
     * @return Whether an error occurred or not
     */
    public boolean isError() {
        return (code != HttpServletResponse.SC_OK);
    }

    /**
     * Check whether the response was redirected or not.
     *
     * @return Whether the state was redirected or not
     */
    public boolean isRedirect() {
        return (redirectUrl != null);
    }

    /**
     * Clears the buffer.
     */
    public void resetBuffer() {
        if (mode == MODE_BINARY) {
            byteStream.reset();
        } else if (mode == MODE_TEXT) {
            stringWriter.getBuffer().delete(0, stringWriter.getBuffer().length());
        }
    }

    /**
     * Send an error code. This implementation just sets the internal error
     * state information.
     *
     * @param code The code
     * @throws IOException Not used
     */
    public void sendError(final int code) throws IOException {
        this.code = code;
        errorMessage = null;
    }

    /**
     * Send an error code. This implementation just sets the internal error
     * state information.
     *
     * @param code The error code
     * @param msg The error message
     * @throws IOException Not used
     */
    public void sendError(final int code, final String msg) throws IOException {
        this.code = code;
        errorMessage = msg;
    }

    /**
     * Indicate sending of a redirectUrl to a particular named resource.
     * <p/>
     * This implementation just keeps hold of the redirectUrl info and
     * makes it available for querying.
     *
     * @param url The url to set redirectUrl to
     * @throws IOException Not used
     */
    public void sendRedirect(String url) throws IOException {
        redirectUrl = url;
    }

    /**
     * Method ignored.
     *
     * @param size The size
     */
    public void setBufferSize(final int size) {
    }

    /**
     * Set the character encoding.
     *
     * @param characterEncoding The character encoding
     */
    public void setCharacterEncoding(final String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    /**
     * Set the content length.
     *
     * @param length The length
     */
    public void setContentLength(final int length) {
        setIntHeader("Content-Length", length);
    }

    /**
     * Set the content type.
     *
     * @param type The content type
     */
    public void setContentType(final String type) {
        setHeader("Content-Type", type);
    }

    /**
     * Return the response content type.
     *
     * @return the response content type
     */
    public String getContentType() {
        return getHeader("Content-Type");
    }

    /**
     * Set a date header.
     *
     * @param name The header name
     * @param l The long value
     */
    public void setDateHeader(final String name, final long l) {
        setHeader(name, formatDate(l));
    }

    /**
     * Formats the specified long value as a date.
     *
     * @param value the specified long value to format
     * @return the formatted date
     */
    public static String formatDate(long value) {
        StringBuffer _dateBuffer = new StringBuffer(32);
        Calendar _calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        _calendar.setTimeInMillis(value);
        formatDate(_dateBuffer, _calendar, false);
        return _dateBuffer.toString();
    }

    /**
     * Format HTTP date "EEE, dd MMM yyyy HH:mm:ss 'GMT'" or
     * "EEE, dd-MMM-yy HH:mm:ss 'GMT'" for cookies.
     *
     * @param buf the buffer to render to formatted date to
     * @param calendar the date to format
     * @param cookie should date be formatted for usage in a cookie
     */
    public static void formatDate(StringBuffer buf, Calendar calendar,
        boolean cookie) {
        // "EEE, dd MMM yyyy HH:mm:ss 'GMT'"
        // "EEE, dd-MMM-yy HH:mm:ss 'GMT'", cookie

        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        int day_of_month = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int century = year / 100;
        year = year % 100;

        int epoch = (int) ((calendar.getTimeInMillis() / 1000) % (60 * 60 * 24));
        int seconds = epoch % 60;
        epoch = epoch / 60;
        int minutes = epoch % 60;
        int hours = epoch / 60;

        buf.append(DAYS[day_of_week]);
        buf.append(',');
        buf.append(' ');
        append2digits(buf, day_of_month);

        if (cookie) {
            buf.append('-');
            buf.append(MONTHS[month]);
            buf.append('-');
            append2digits(buf, year);
        } else {
            buf.append(' ');
            buf.append(MONTHS[month]);
            buf.append(' ');
            append2digits(buf, century);
            append2digits(buf, year);
        }
        buf.append(' ');
        append2digits(buf, hours);
        buf.append(':');
        append2digits(buf, minutes);
        buf.append(':');
        append2digits(buf, seconds);
        buf.append(" GMT");
    }

    /**
     * Append two digits if specified int is less than 100.
     *
     * @param buf the buffer to add 2 digits to
     * @param i the digits to add
     */
    public static void append2digits(StringBuffer buf, int i) {
        if (i < 100) {
            buf.append((char) (i / 10 + '0'));
            buf.append((char) (i % 10 + '0'));
        }
    }

    /* END: This code comes from Jetty 6.1.1 */

    /**
     * Set the given header value.
     *
     * @param name The name for the header
     * @param value The value for the header
     */
    public void setHeader(final String name, final String value) {
        List<String> l = new ArrayList<String>(1);
        l.add(value);
        headers.put(name, l);
    }

    /**
     * Set an int header value.
     *
     * @param name The header name
     * @param i The value
     */
    public void setIntHeader(final String name, final int i) {
        setHeader(name, "" + i);
    }

    /**
     * Set the locale in the response header.
     *
     * @param locale The locale
     */
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Set the status for this response.
     *
     * @param status The status
     */
    public void setStatus(final int status) {
        this.status = status;
    }

    /**
     * Set the status for this response.
     *
     * @param status The status
     * @param msg The message
     * @deprecated
     */
    public void setStatus(final int status, final String msg) {
        setStatus(status);
    }
}
