/**
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.click.Page;

import org.apache.log4j.Logger;
import org.apache.velocity.exception.ParseErrorException;

/**
 * Provides the base error handling Page. The ErrorPage handles any
 * unexpected Exceptions. When the application is not in "production" mode the
 * ErrorPage will provide diagnostic information.
 * <p/>
 * The ErrorPage template "<span class="blue">click/error.htm</span>" can be 
 * customized to your needs.
 * <p/>
 * Applications which require additional error handling logic must subclass
 * the ErrorPage. For example to rollback a Connection if an SQLException occured:
 * 
 * <pre class="codeJava">
 * package com.mycorp.util;
 *
 * import java.sql.Connection;
 * import java.sql.SQLException;
 * import net.sf.click.util.ErrorPage;
 *
 * public class MyCorpErrorPage extends ErrorPage {
 *
 *     /**
 *      * @see Page#onFinally()
 *      * /
 *     public void onFinally() {
 *         Exception errror = getError();
 *
 *         if (error instanceof SQLException) {
 *             rollbackAndClose();
 *         }
 *         else {
 *             Throwable cause = error.getCause();
 *
 *             if (cause instanceof SQLException) {
 *                 rollbackAndClose();
 *             }
 *         }
 *     }
 *
 *     protected void rollbackAndClose() {
 *         Connection connection =
 *             ConnectionProviderThreadLocal.getConnection();
 *
 *         if (connection != null) {
 *             try {
 *                 connection.rollback();
 *             }
 *             catch (SQLException sqle) {
 *             }
 *             finally {
 *                 try {
 *                     connection.close();
 *                 }
 *                 catch (SQLException sqle) {
 *                 }
 *             }
 *         }
 *     }
 * } </pre>
 *
 * The ClickServlet sets the following ErrorPage properties in addition to
 * the normal Page properties:<ul>
 * <li>{@link #error} - the error causing exception</li>
 * <li>{@link #mode} - the Click application mode</li>
 * <li>{@link #page} - the Page object in error</tt>
 * <li>{@link #pagePath} - the path of the page with the error</li>
 * </ul>
 *
 * @author Malcolm Edgar
 */
public class ErrorPage extends Page {

    /** The number of lines to display. */
    protected static final int NUMB_LINES = 8;

    /** The error causing exception. */
    protected Throwable error;

    /**
     * The application mode: &nbsp;
     * ["production", "profile", "development", "debug"]
     */
    protected String mode;

    /** The page in error. */
    protected Page page;

    /** The target page path of the error. */
    protected String pagePath;

    // --------------------------------------------------------- Public Methods

    /**
     * Return the causing error.
     *
     * @return the causing error
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Set the causing error.
     *
     * @param cause the causing error
     */
    public void setError(Throwable cause) {
        this.error = cause;
    }

    /**
     * Return the application mode: <tt>["production", "profile", "development",
     * debug"]</tt>
     *
     * @return the application mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Set the application mode: <tt>["production", "profile", "development",
     * debug"]</tt>
     * <p/>
     * The application mode is added to the model by the {@link #onInit()} method.
     * This property is used to determines whether the error page template
     * should display error diagnostic information. The default "error.htm" will
     * display error diagnostic information so long as the application mode is
     * not "production".
     *
     * @param value the application mode.
     */
    public void setMode(String value) {
        mode = value;
    }

    /**
     * Return the page in error
     *
     * @return the page in error
     */
    public Page getPage() {
        return page;
    }

    /**
     * Set the page in error.
     *
     * @param page the page in error
     */
    public void setPage(Page page) {
        this.page = page;
        pagePath = page.getPath();
    }

    /**
     * This method initializes the ErrorPage, populating the model with error
     * diagnostic information.
     * <p/>
     * The following values are added to ErrorPage model for rendering by the
     * error page template:
     * 
     * <ul style="margin-top: 0.5em;">
     * <li><tt>errorClass</tt> &nbsp; - &nbsp; the classname of the exception</li>
     * <li><tt>errorMessage</tt> &nbsp; - &nbsp; the exception error message</li>
     * <li><tt>errorStackTrace</tt> &nbsp; - &nbsp; the HTML rendered error stack trace</li>
     * <li><tt>isParseError</tt> &nbsp; - &nbsp; a boolean flag denoting a Velocity parsing error</li>
     * <li><tt>mode</tt> &nbsp; - &nbsp; the application mode</li>
     * <li><tt>page</tt> &nbsp; - &nbsp; the error causing page</li>
     * <li><tt>pagePath</tt> &nbsp; - &nbsp; the path of the error causing page</li>
     * <li><tt>requestAttributes</tt> &nbsp; - &nbsp; a sorted Map of the HTTP request attributes</li>
     * <li><tt>requestHeaders</tt> &nbsp; - &nbsp; a sorted Map of the HTTP request headers</li>
     * <li><tt>requestParams</tt> &nbsp; - &nbsp; a sorted Map of the HTTP request parameters</li>
     * <li><tt>template</tt> &nbsp; - &nbsp; the HTML section of the page template causing a parse error</li>
     * </ul>
     *
     * @see Page#onInit()
     */
    public void onInit() {
        addModel("mode", getMode());
        addModel("page", getPage());
        addModel("isParseError", new Boolean(isParseError()));
 
        if (error instanceof ParseErrorException) {
            addModel("pagePath", pagePath);

            String errorMessage = getParseMessage(error);

            addModel("errorMessage", errorMessage);

            if (pagePath != null) {
                int errorLine = getErrorLine(error.getMessage());
                getModel().put("template", getTemplate(pagePath, errorLine));
            }

        } else {
            Throwable cause = null;
            if (error instanceof ServletException) {
                cause = ((ServletException) error).getRootCause();
            } else {
                cause = error.getCause();
            }

            if (cause != null) {
                addModel("errorMessage",
                         ClickUtils.toHtmlEncode(cause.getMessage()));
                addModel("errorClass", cause.getClass().getName());
                addModel("errorStackTrace", ClickUtils.toStackTrace(cause));

            } else {
                addModel("errorMessage",
                        ClickUtils.toHtmlEncode(error.getMessage()));
                addModel("errorClass", error.getClass().getName());
                addModel("errorStackTrace", ClickUtils.toStackTrace(error));
            }
        }

        HttpServletRequest request = getContext().getRequest();

        TreeMap requestParams = new TreeMap();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement().toString();
            requestParams.put(name, request.getParameter(name));
        }
        addModel("requestParams", requestParams);

        TreeMap requestAttributes = new TreeMap();
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement().toString();
            requestAttributes.put(name, request.getAttribute(name));
        }
        addModel("requestAttributes", requestAttributes);

        TreeMap requestHeaders = new TreeMap();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement().toString();
            requestHeaders.put(name, request.getHeader(name));
        }
        addModel("requestHeaders", requestHeaders);

        TreeMap sessionAttributes = new TreeMap();
        if (getPage().getContext().hasSession()) {
            HttpSession session = getPage().getContext().getSession();
            attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement().toString();
                sessionAttributes.put(name, session.getAttribute(name));
            }
        }
        addModel("sessionAttributes", sessionAttributes);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return true if the error is a ParseErrorException.
     *
     * @return true if the error is a ParseErrorException
     */
    protected boolean isParseError() {
        return (error instanceof ParseErrorException);
    }

    /**
     * Return a HTML rendered section of the specified page template, with
     * the error line highlighted.
     *
     * @param pagePath the path of the template to render
     * @param errorLine the parse error causing line number to highlight
     * @return a HTML rendered section of the parsing error page template
     */
    protected String getTemplate(String pagePath, int errorLine) {

        StringBuffer buffer = new StringBuffer();

        ServletContext context = getContext().getServletContext();

        InputStream is = context.getResourceAsStream(pagePath);

        if (is != null) {
            try {
                int line = 1;
                if (displayLine(line, errorLine)) {
                    buffer.append(formatLineNumber(line, errorLine));
                }

                int value = is.read();
                while (value > -1) {


                    char aChar = (char) value;
                    if (aChar == '\n') {

                        line++;

                        // If previous line was error line close bold tag.
                        if (line == errorLine + 1) {
                            buffer.append("</b>");
                        }

                        if (displayLine(line, errorLine)) {
                            buffer.append("</font><br/>");
                            buffer.append(formatLineNumber(line, errorLine));
                        }

                    } else if (aChar == '<') {
                        if (displayLine(line, errorLine)) {
                            buffer.append("&lt;");
                        }
                    } else if (aChar == '>') {
                        if (displayLine(line, errorLine)) {
                            buffer.append("&gt;");
                        }
                    } else if (aChar == '&') {
                        if (displayLine(line, errorLine)) {
                            buffer.append("&amp;");
                        }
                    } else if (aChar == '"') {
                        if (displayLine(line, errorLine)) {
                            buffer.append("&quot;");
                        }
                    } else if (aChar == ' ') {
                        if (displayLine(line, errorLine)) {
                            buffer.append("&nbsp;");
                        }
                    } else {
                        if (displayLine(line, errorLine)) {
                            buffer.append(aChar);
                        }
                    }
                    value = is.read();
                }

            } catch (IOException ioe) {
                buffer = new StringBuffer();
                buffer.append("Could not load page template: " + ioe);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {
                    // do nothing
                }
            }

        } else {
            buffer.append("Page template not available.");
        }

        return buffer.toString();
    }

    /**
     * Return the HTML formatted parse error error message from the given error.
     *
     * @param error the Velocity ParseErrorException error
     * @return the HTML formatted error message
     */
    protected String getParseMessage(Throwable error) {

        StringTokenizer tokenizer =
            new StringTokenizer(error.getMessage(), "\n\r");

        StringBuffer buffer = new StringBuffer();
        buffer.append(tokenizer.nextToken());
        buffer.append(" ");

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.replace('"', ' ');
            if (token.endsWith("...")) {
                buffer.append(token.substring(0, token.length() - 3));
            } else {
                buffer.append(token);
            }
        }

        return ClickUtils.toHtmlEncodeNoBreaks(buffer.toString());
    }

    /**
     * Return the error line number from the ParseException error message, or
     * -1 if it could not be determined.
     *
     * @param parseErrorMessage the Velocity ParseException error message
     * @return the error line number of the parse error
     */
    protected int getErrorLine(String parseErrorMessage) {
        StringTokenizer tokenizer =
            new StringTokenizer(parseErrorMessage, " ,");

        boolean atFound = false;
        boolean lineFound = false;
        int lineNumber = -1;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            // Line number must be next token.
            if (atFound && lineFound) {
                try {
                    lineNumber = Integer.parseInt(token.trim());
                    break;
                } catch (NumberFormatException nfe) {
                    Logger.getLogger(getClass()).warn
                        ("Could not parse line number", nfe);
                    break;
                }
            }

            if (!atFound && token.equals("at")) {
                atFound = true;
            }
            if (!lineFound && token.equals("line")) {
                lineFound = true;
            }
        }

        return lineNumber;
    }

    /**
     * Return a HTML formatted line number string.
     *
     * @param lineNumber the line number to format
     * @param errorLine the error line number
     * @return a HTML formatted line number string
     */
    protected String formatLineNumber(int lineNumber, int errorLine) {
        StringBuffer buffer = new StringBuffer();

        if (lineNumber == errorLine) {
            buffer.append("<font color='red'><b>");
        } else {
            buffer.append("<font color='navy'>");
        }

        String lineStr = "" + lineNumber;

        int numberSpace = 3 - lineStr.length();

        for (int i = 0; i < numberSpace; i++) {
            buffer.append("&nbsp;");
        }

        buffer.append(lineNumber);
        if (lineNumber == errorLine) {
            buffer.append(":&nbsp;&nbsp;");
        } else {
            buffer.append(":&nbsp;&nbsp;");
        }

        return buffer.toString();
    }

    /**
     * Return true if the given line should be displayed for the given error
     * line.
     *
     * @param line the line number to test
     * @param errorLine the line number where the error occured
     * @return true if line should be displayed for the given error
     */
    protected boolean displayLine(int line, int errorLine) {
        return (line >= errorLine - NUMB_LINES
                && line <= errorLine + NUMB_LINES);
    }
}
