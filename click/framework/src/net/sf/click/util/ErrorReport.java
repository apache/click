/*
 * Copyright 2005 Malcolm A. Edgar
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.click.Page;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.exception.ParseErrorException;

/**
 * Provides an HTML &lt;div&gt; error report for the display of page error
 * information. This class is used by ErrorPage and ClickServlet for the
 * display of error information.
 *
 * @author Malcolm Edgar
 */
public class ErrorReport {
    
    /** The Java language keywords. Used to render Java source code. */
    protected static final String[] JAVA_KEYWORDS = { "package", "import",
            "class", "public", "protected", "private", "extends", "implements",
            "return", "if", "while", "for", "do", "else", "try", "new", "void",
            "catch", "throws", "throw", "static", "final", "break", "continue",
            "super", "finally", "true", "false", "null", "boolean", "int",
            "char", "long", "float", "double", "short" };
    
    /** The column number of the error, or -1 if not defined. */
    protected int columnNumber;
       
    /** The cause of the error. */
    protected final Throwable error;
    

    /** The line number of the error, or -1 if not defined. */
    protected int lineNumber;
    
    /** The error is Velocity parsing exception. */
    protected final boolean isParseError;
       
    /** The applicaiton is in "production" mode flag. */
    protected final boolean isProductionMode;
    
    /** The page which caused the error. */
    protected final Page page;
    
    /** The name of the error source. */
    protected final String sourceName;
    
    /** The error souce LineNumberReader */
    protected LineNumberReader sourceReader;
    
    /**
     * Create a ErrorReport instance from the given error and ServletContext.
     * 
     * @param error the cause of the error
     * @param page the Page causing the error
     * @param isProductionMode the application is in "production" mode
     */
    public ErrorReport(Throwable error, Page page, boolean isProductionMode) {
        this.page = page;
        this.error = error;
        this.isProductionMode = isProductionMode;
        
        isParseError = error instanceof ParseErrorException;
        
        if (isParseError) {
            ParseErrorException pee = (ParseErrorException) error;
            sourceName = pee.getTemplateName();
            lineNumber = pee.getLineNumber();
            columnNumber = pee.getColumnNumber();
            
            ServletContext context = page.getContext().getServletContext();
            
            InputStream is = 
                context.getResourceAsStream(pee.getTemplateName());
            
            sourceReader = new LineNumberReader(new InputStreamReader(is));
            
        } else {
            sourceName = null;
            columnNumber = -1;
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            getCause().printStackTrace(pw);
            
            StringTokenizer tokenizer = new StringTokenizer(sw.toString(), "\n");
            
            try {
                tokenizer.nextToken();
                String line = tokenizer.nextToken();
                int nameStart = line.indexOf("at ");
                int nameEnd = line.indexOf("(");
                nameEnd = line.lastIndexOf(".", nameEnd);
                String classname = line.substring(nameStart + 3, nameEnd);
                    
                int lineStart = line.indexOf(":");
                if (lineStart != -1) {
                    int lineEnd = line.indexOf(")");
                    String linenumber = line.substring(lineStart + 1, lineEnd);
                    System.err.println("linenumber="+linenumber);
                    this.lineNumber = Integer.parseInt(linenumber);
                    
                    String filename = "/" + classname.replace('.', '/') + ".java";
                    
                    sourceReader = getJavaSourceReader(filename);
                }
                                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Return a error report HTML &lt;div&gt; element for the given error and
     * page. The HTML &lt;div&gt; element 'id' and 'class' attribute values are
     * 'errorReport'.
     *
     * @return a error HTML display
     */
    public String getErrorReport() {
        
        if (isProductionMode()) {
            Locale locale = page.getContext().getRequest().getLocale();
            ResourceBundle bundle = 
                ResourceBundle.getBundle("click-control", locale);
            return bundle.getString("production-error-message");
        }
        
        StringBuffer buffer = new StringBuffer(10 * 1024);

        Throwable cause = getCause();

        HttpServletRequest request = page.getContext().getRequest();     

        buffer.append("<div id='errorReport' class='errorReport'>\n");

        // Exception table
        buffer.append("<table border='1' cellspacing='1' cellpadding='4' width='100%'>");
        if (isParseError()) {
            buffer.append("<tr><td colspan='2' style='color:white; background-color: navy; font-weight: bold'>Page Parsing Error</td></tr>");
            buffer.append("<tr><td width='12%'><b>Source</b></td><td>");
            buffer.append(getSourceName());
            buffer.append("</td></tr>");
        } else {
            buffer.append("<tr><td colspan='2' style='color:white; background-color: navy; font-weight: bold'>Exception</td></tr>");
            buffer.append("<tr><td width='12%'><b>Class</b></td><td>");
            buffer.append(cause.getClass().getName());
            buffer.append("</td></tr>");
        }     
        buffer.append("<tr><td valign='top' width='12%'><b>Message</b></td><td>");
        buffer.append(getMessage());
        buffer.append("</td></tr>");
        buffer.append("<tr><td valign='top' colspan='2'>\n");
        buffer.append(getErrorSource());
        buffer.append("</td></tr>");
        buffer.append("</table>");
        buffer.append("<br/>");

        // Page table
        buffer.append("<table border='1' cellspacing='1' cellpadding='4' width='100%'>");
        buffer.append("<tr><td colspan='2' style='color:white; background-color: navy; font-weight: bold'>Page</td></tr>");
        buffer.append("<tr><td width='12%'><b>Classname</b></td><td>");
        buffer.append(page.getClass().getName());
        buffer.append("</td></tr>");
        buffer.append("<tr><td width='12%'><b>Path</b></td><td>");
        buffer.append(page.getPath());
        buffer.append("</td></tr>");
        buffer.append("<tr><td><b width='12%'>Template</b></td><td>");
        buffer.append(page.getTemplate());
        buffer.append("</td></tr>");
        buffer.append("</table>");
        buffer.append("<br/>");

        // Request table
        buffer.append("<table border='1' cellspacing='1' cellpadding='4' width='100%'>");
        buffer.append("<tr><td colspan='2' style='color:white; background-color: navy; font-weight: bold'>Request</td></tr>");

        TreeMap requestAttributes = new TreeMap();
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement().toString();
            requestAttributes.put(name, request.getAttribute(name));
        }
        buffer.append("<tr><td width='12%' valign='top'><b>Attributes</b></td><td>");
        writeMap(requestAttributes, buffer);
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%'><b>Auth Type</b></td><td>");
        buffer.append(request.getAuthType());
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%'><b>Context Path</b></td><td>");
        buffer.append("<a href='");
        buffer.append(request.getContextPath());
        buffer.append("'>");
        buffer.append(request.getContextPath());
        buffer.append("</a>");
        buffer.append("</td></tr>");

        TreeMap requestHeaders = new TreeMap();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement().toString();
            requestHeaders.put(name, request.getHeader(name));
        }
        buffer.append("<tr><td width='12%' valign='top'><b>Headers</b></td><td>");
        writeMap(requestHeaders, buffer);
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%'><b>Query</b></td><td>");
        buffer.append(request.getQueryString());
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%'><b>Method</b></td><td>");
        buffer.append(request.getMethod());
        buffer.append("</td></tr>");

        TreeMap requestParams = new TreeMap();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement().toString();
            requestParams.put(name, request.getParameter(name));
        }
        buffer.append("<tr><td width='12%' valign='top'><b>Parameters</b></td><td>");
        writeMap(requestParams, buffer);
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%'><b>Remote User</b></td><td>");
        buffer.append(request.getRemoteUser());
        buffer.append("</td></tr>");

        buffer.append("<tr><td width='12%' valign='top'><b>URI</b></td><td>");
        buffer.append("<a href='");
        buffer.append(request.getRequestURI());
        buffer.append("'>");
        buffer.append(request.getRequestURI());
        buffer.append("</a>");
        buffer.append("</td></tr>");

        buffer.append("<tr><td><b width='12%'>URL</b></td><td>");
        buffer.append("<a href='");
        buffer.append(request.getRequestURL());
        buffer.append("'>");
        buffer.append(request.getRequestURL());
        buffer.append("</a>");
        buffer.append("</td></tr>");

        TreeMap sessionAttributes = new TreeMap();
        if (page.getContext().hasSession()) {
            HttpSession session = request.getSession();
            attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement().toString();
                sessionAttributes.put(name, session.getAttribute(name));
            }
        }
        buffer.append("<tr><td width='12%' valign='top'><b>Session</b></td><td>");
        writeMap(sessionAttributes, buffer);
        buffer.append("</td></tr>");
        buffer.append("</table>\n");

        buffer.append("</div>\n");

        return buffer.toString();
    }
    
    /**
     * Return the cause of the error.
     * 
     * @return the cause of the error
     */
    public Throwable getCause() {
        Throwable cause = null;
        if (error instanceof ServletException) {
            cause = ((ServletException) error).getRootCause();
            if (cause == null) {
                cause = error.getCause();
            }
        } else {
            cause = error.getCause();
        }
        if (cause == null) {
            cause = error;
        }
        return cause;
    }
    
    /**
     * Return the error source column number, or -1 if not determined.
     * 
     * @return the error source column number, or -1 if not determined
     */
    public int getColumnNumber() {
        return columnNumber;
    }
    
    /**
     * Return true if the error was a Velocity parsing exception.
     * 
     * @return true if the error was a Velocity parsing exception
     */
    public boolean isParseError() {
        return isParseError;
    }
    
    /**
     * Return true if the application is in "production" mode.
     * 
     * @return true if the application is in "production" mode
     */
    public boolean isProductionMode() {
        return isProductionMode;
    }
    
    /**
     * Return the error source line number, or -1 if not determined.
     * 
     * @return the error source line number, or -1 if not determined
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * Return the error message.
     * 
     * @return the error message
     */
    public String getMessage() {
        if (isParseError()) {
            String message = error.getMessage();
            
            int startIndex = message.indexOf('\n');
            int endIndex = message.lastIndexOf("...");
            
            String parseMsg = message.substring(startIndex + 1, endIndex);
            
            parseMsg = StringEscapeUtils.escapeHtml(parseMsg);
            
            parseMsg = StringUtils.replace(parseMsg, "...", ", &nbsp;");

            return parseMsg;
            
        } else {
            Throwable cause = getCause();
            
            String value = 
                (cause.getMessage() != null) ? cause.getMessage() : "null";
                
            return StringEscapeUtils.escapeHtml(value);
        }
    }
    
    /**
     * Return the error source name
     * 
     * @return the error source name
     */
    public String getSourceName() {
        return sourceName;
    }
    
    /**
     * Return a LineNumberReader for the error source file, or null if not
     * defined
     * 
     * @returna LineNumberReader for the error source file, or null if not
     *      defined
     */
    public LineNumberReader getSourceReader() {
        return sourceReader;
    }
    
    // ------------------------------------------------------ Protected Methods
    
    /**
     * Return Java Source LineNumberReader for the given filename, or null if
     * not found.
     * 
     * @param filename the name of the Java source file, e.g. /examples/Page.java
     * @return LineNumberReader for the given source filename, or null if
     *      not found
     * @throws FileNotFoundException if file could not be found
     */
    protected LineNumberReader getJavaSourceReader(String filename) 
        throws FileNotFoundException {
        
        // Look for source file on classpath
        InputStream is = page.getClass().getResourceAsStream(filename);        
        if (is != null) {
            return new LineNumberReader(new InputStreamReader(is));
        }
        
        // Else search for source file under WEB-INF 
        String rootPath = 
            page.getContext().getServletContext().getRealPath("/");
        
        String webInfPath = rootPath + File.separator + "WEB-INF";
        
        File sourceFile = null;
        
        File webInfDir = new File(webInfPath);
        if (webInfDir.isDirectory() && webInfDir.canRead()) {
            File[] dirList = webInfDir.listFiles();
            for (int i = 0; i < dirList.length; i++) {
                File file = dirList[i];
                if (file.isDirectory() && file.canRead()) {
                    String sourcePath = file.toString() + filename;
                    sourceFile = new File(sourcePath);
                    if (sourceFile.isFile() && sourceFile.canRead()) {
                        
                        FileInputStream fis = new FileInputStream(sourceFile);
                        return new LineNumberReader(new InputStreamReader(fis));
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Return a HTML rendered section of the source error with the error
     * line highlighted.
     *
     * @return a HTML rendered section of the parsing error page template
     */
    protected String getErrorSource() {
        if (sourceReader != null) {
            return getRenderedSource();
        } else {
            return getStackTrace();
        }
    }
    
    /**
     * Return a HTML rendered section of the source error with the error
     * line highlighted.
     *
     * @return a HTML rendered section of the parsing error page template
     */
    protected String getRenderedSource() {

        StringBuffer buffer = new StringBuffer(5*1024);
        
        buffer.append("<span style='font-family: Courier New, courier;'>");

        if (sourceReader == null) {
            buffer.append("Source is not available.</span>");
            return buffer.toString();
        }
        
        final String normalLineStyle = "style='white-space:pre;'";
        
        final String errorLineStyle = 
            "style='white-space:pre;background-color:yellow;'"; 
        
        final String errorCharSpan = 
            "<span style='white-space:pre;color:red;text-decoration:underline;font-weight:bold;'>";

        try {
            String line = sourceReader.readLine();
            
            while (line != null) {
                boolean isErrorLine = 
                    sourceReader.getLineNumber() == lineNumber;
                
                // Start div tag
                buffer.append("<div ");
                if (isErrorLine) {
                    buffer.append(errorLineStyle);
                } else {
                    buffer.append(normalLineStyle);
                }
                buffer.append(">");
                
                // Write out line number
                String lineStr = "" + sourceReader.getLineNumber();
                int numberSpace = 3 - lineStr.length();
                for (int i = 0; i < numberSpace; i++) {
                    buffer.append(" ");
                }
                if (isErrorLine) {
                    buffer.append("<b>");
                }
                buffer.append(sourceReader.getLineNumber());
                if (isErrorLine) {
                    buffer.append("</b>");
                }
                buffer.append(":  ");
                
                // Write out line content
                if (isErrorLine) {
                    if (isParseError()) {
                        StringBuffer htmlLine = new StringBuffer(line.length() * 2);
                        for (int i = 0; i < line.length(); i++) {
                            if (i == getColumnNumber() - 1) {
                                htmlLine.append(errorCharSpan);
                                htmlLine.append(line.charAt(i));
                                htmlLine.append("</span>");
                            } else {
                                htmlLine.append(line.charAt(i));
                            }
                        }
                        buffer.append(htmlLine.toString());
                        
                    } else {
                        buffer.append(getRenderJavaLine(line));
                    }
                    
                } else {
                    if (isParseError()) {
                        buffer.append(StringEscapeUtils.escapeHtml(line));
                    } else {
                        buffer.append(getRenderJavaLine(line));
                    }
                }
                
                // Close div tag
                buffer.append("</div>\n");
                
                line = sourceReader.readLine();
            }
            
        } catch (IOException ioe) {
            buffer.append("Could not load page source: ");
            buffer.append(StringEscapeUtils.escapeHtml(ioe.toString()));
        } finally {
            try {
                sourceReader.close();
            } catch (IOException ioe) {
                // do nothing
            }
        }
        
        buffer.append("</span>");

        return buffer.toString();
    }
    
    /**
     * Return a HTML encode stack trace string from the given error.
     *
     * @return a HTML encode stack trace string.
     */
    protected String getStackTrace() {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        getCause().printStackTrace(pw);

        StringBuffer buffer = new StringBuffer(sw.toString().length() + 80);       
        buffer.append("<span style='white-space:pre;font-family: Courier New, courier;'>");
        buffer.append(StringEscapeUtils.escapeHtml(sw.toString()));
        buffer.append("</span>");
        
        return buffer.toString();
    }
    
    /**
     * Write out the map name value pairs as name=value lines to the string
     * buffer.
     *
     * @param map the Map of name value pairs
     * @param buffer the string buffer to write out the values to
     */
    protected void writeMap(Map map, StringBuffer buffer) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            String name = i.next().toString();
            String value = map.get(name).toString();
            buffer.append(name);
            buffer.append("=");
            buffer.append(StringEscapeUtils.escapeHtml(value));
            buffer.append("</br>");
        }
        if (map.isEmpty()) {
            buffer.append("&nbsp;");
        }
    }
    
    /**
     * Return a HTML rendered Java source line with keywords highlighted
     * using the given line.
     * 
     * @param line the Java source line to render
     * @return HTML rendred Java source line
     */
    protected String getRenderJavaLine(String line) {
        line = StringEscapeUtils.escapeHtml(line);
        
        for (int i = 0; i < JAVA_KEYWORDS.length; i++) {
            String keyword = JAVA_KEYWORDS[i];
            line = renderJavaKeywords(line, keyword);
        }

        return line;
    }

    /**
     * Render the HTML rendered Java source line with the given keyword 
     * highlighted.
     * 
     * @param line the given Java source line to render
     * @param keyword the Java keyword to highlight
     * @return the HTML rendered Java source line with the given keyword 
     *      highlighted
     */
    protected String renderJavaKeywords(String line, String keyword) {
        String markupToken = 
            "<span style='color:#7f0055;font-weight:bold;'>" + keyword + "</span>";

        line = StringUtils.replace
            (line, " " + keyword + " ", " " + markupToken + " ");

        if (line.startsWith(keyword)) {
            line = markupToken + line.substring(keyword.length());
        }

        if (line.endsWith(keyword)) {
            line = line.substring(0, line.length() - keyword.length())
                    + markupToken;
        }

        return line;
    }
}
