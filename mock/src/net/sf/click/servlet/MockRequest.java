/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.Random;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.sf.click.util.ClickUtils;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Mock servlet request. Implements all of the methods from the standard
 * HttpServletRequest class plus helper methods to aid setting up a request.
 * <p/>
 * This class was adapted from <a href="http://wicket.apache.org">Apache Wicket</a>.
 *
 * @author Chris Turner
 * @author Bob Schellink
 */
public class MockRequest implements HttpServletRequest {

    private Locale locale = Locale.getDefault();

    private final Map attributes = new HashMap();

    private String authType;

    private String characterEncoding;

    private ServletContext servletContext;

    private final List cookies = new ArrayList();

    private final Map headers = new HashMap();

    private String method = "POST";

    private final Map parameters = new HashMap();

    private HttpSession session;

    private String url;

    private Map /*<String, UploadedFile>*/ uploadedFiles =
        new HashMap /*<String, UploadedFile>*/();

    private boolean useMultiPartContentType;

    private String forward;

    private List includes = new ArrayList();

    private String scheme = "http";

    private String contextPath = MockServletContext.DEFAULT_CONTEXT_PATH;

    private String servletPath = "";

    private String pathInfo = "";

    private String serverName = "localhost";

    private int serverPort = 8080;

    private Random random = new Random();

    public MockRequest() {
        initialize();
    }

    public MockRequest(final Locale locale) {
        this(locale, null);
    }

    public MockRequest(final ServletContext context) {
        this(null, context);
    }

    public MockRequest(final Locale locale, final ServletContext context) {
        this(locale, context, null);
    }

    /**
     * Create the request using the supplied session object.
     *
     * @param locale The request locale, or null to use the default locale
     * @param session The session object
     * @param context The current servlet context
     */
    public MockRequest(final Locale locale, final ServletContext context,
        final HttpSession session) {
        this(locale, MockServletContext.DEFAULT_CONTEXT_PATH, "", context, session);
    }

    public MockRequest(Locale locale, String contextPath, String servletPath,
        final ServletContext context, final HttpSession session) {
        if (locale != null) {
            this.locale = locale;
        }
        this.contextPath = contextPath;
        this.servletPath = servletPath;
        this.session = session;
        this.servletContext = context;
        initialize();
    }

    // -------------------------------------------------------- Mock intialization methods

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setHttpSession(HttpSession session) {
        this.session = session;
    }

    /**
     * Add a new cookie.
     *
     * @param cookie The cookie
     */
    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * Add an uploaded file to the request. Use this to simulate a file that
     * has been uploaded to a field.
     *
     * @param fieldName The fieldname of the upload field.
     * @param file The file to upload.
     * @param contentType The content type of the file. Must be a correct
     * mimetype.
     */
    public void addFile(String fieldName, File file, String contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        }

        if (file.exists() == false) {
            throw new IllegalArgumentException(
                "File does not exist. You must provide an existing file: " +
                file.getAbsolutePath());
        }

        if (file.isFile() == false) {
            throw new IllegalArgumentException(
                "You can only add a File, which is not a directory. Only files " +
                "can be uploaded.");
        }

        if (uploadedFiles == null) {
            uploadedFiles = new HashMap/* <String, UploadedFile> */();
        }

        UploadedFile uf = new UploadedFile(fieldName, file, contentType);

        uploadedFiles.put(fieldName, uf);
    }

    /**
     * Add a header to the request.
     *
     * @param name The name of the header to add
     * @param value The value
     */
    public void addHeader(String name, String value) {
        List list = (List) headers.get(name);
        if (list == null) {
            list = new ArrayList(1);
            headers.put(name, list);
        }
        list.add(value);
    }

    /**
     * Get an attribute.
     *
     * @param name The attribute name
     * @return The value, or null
     */
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Get the names of all of the values.
     *
     * @return The names
     */
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    // -------------------------------------------------------- HttpServletRequest methods
    /**
     * Get the auth type.
     *
     * @return The auth type
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * Get the current character encoding.
     *
     * @return The character encoding
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * true will force Request generate multiPart ContentType and ContentLength
     *
     * @param useMultiPartContentType
     */
    public void setUseMultiPartContentType(boolean useMultiPartContentType) {
        this.useMultiPartContentType = useMultiPartContentType;
    }

    /**
     * Return the length of the content. This is always -1 except if
     * useMultiPartContentType set as true. Then the length will be the length
     * of the generated request.
     *
     * @return -1 if useMultiPartContentType is false. Else the length of the
     * generated request.
     */
    public int getContentLength() {
        if (useMultiPartContentType) {
            byte[] request = buildRequest();
            return request.length;
        }

        return -1;
    }

    /**
     * If useMultiPartContentType set as true return the correct content-type.
     *
     * @return The correct multipart content-type if useMultiPartContentType
     * is true. Else null.
     */
    public String getContentType() {
        if (useMultiPartContentType) {
            return FileUploadBase.MULTIPART_FORM_DATA + "; boundary=abcdefgABCDEFG";
        }

        return null;
    }

    /**
     * Get all of the cookies for this request.
     *
     * @return The cookies
     */
    public Cookie[] getCookies() {
        if (cookies.size() == 0) {
            return null;
        }
        Cookie[] result = new Cookie[cookies.size()];
        return (Cookie[]) cookies.toArray(result);
    }

    /**
     * Get the given header as a date.
     *
     * @param name The header name
     * @return The date, or -1 if header not found
     * @throws IllegalArgumentException If the header cannot be converted
     */
    public long getDateHeader(final String name)
        throws IllegalArgumentException {
        String value = getHeader(name);
        if (value == null) {
            return -1;
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        try {
            return df.parse(value).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't convert header to date " + name + ": " + value);
        }
    }

    /**
     * Get the given header value.
     *
     * @param name The header name
     * @return The header value or null
     */
    public String getHeader(final String name) {
        final List l = (List) headers.get(name);
        if (l == null || l.size() < 1) {
            return null;
        } else {
            return (String) l.get(0);
        }
    }

    /**
     * Get the names of all of the headers.
     *
     * @return The header names
     */
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    /**
     * Get enumeration of all header values with the given name.
     *
     * @param name The name
     * @return The header values
     */
    public Enumeration getHeaders(final String name) {
        List list = (List) headers.get(name);
        if (list == null) {
            list = new ArrayList();
        }
        return Collections.enumeration(list);
    }

    /**
     * Returns an input stream if there has been added some uploaded files. Use
     * {@link #addFile(String, File, String)} to add some uploaded files.
     *
     * @return The input stream
     * @throws IOException If an I/O related problem occurs
     */
    public ServletInputStream getInputStream() throws IOException {
        if (uploadedFiles != null && uploadedFiles.size() > 0) {
            byte[] request = buildRequest();

            // Ok lets make an input stream to return
            final ByteArrayInputStream bais = new ByteArrayInputStream(request);

            return new ServletInputStream() {

                public int read() {
                    return bais.read();
                }
            };
        } else {
            return new ServletInputStream() {

                public int read() {
                    return -1;
                }
            };
        }
    }

    /**
     * Get the given header as an int.
     *
     * @param name The header name
     * @return The header value or -1 if header not found
     * @throws NumberFormatException If the header is not formatted correctly
     */
    public int getIntHeader(final String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1;
        }
        return Integer.valueOf(value).intValue();
    }

    /**
     * Get the locale of the request. Attempts to decode the Accept-Language
     * header and if not found returns the default locale of the JVM.
     *
     * @return The locale
     */
    public Locale getLocale() {
        final String header = getHeader("Accept-Language");
        if (header == null) {
            return Locale.getDefault();
        }

        final String[] firstLocale = header.split(",");
        if (firstLocale.length < 1) {
            return Locale.getDefault();
        }

        final String[] bits = firstLocale[0].split("-");
        if (bits.length < 1) {
            return Locale.getDefault();
        }

        final String language = bits[0].toLowerCase();
        if (bits.length > 1) {
            final String country = bits[1].toUpperCase();
            return new Locale(language, country);
        } else {
            return new Locale(language);
        }
    }

    /**
     * Return all the accepted locales. This implementation always returns just
     * one.
     *
     * @return The locales
     */
    public Enumeration getLocales() {
        List list = new ArrayList(1);
        list.add(getLocale());
        return Collections.enumeration(list);
    }

    /**
     * Get the method.
     * <p/>
     * The returned string will be in upper case eg. <tt>POST</tt>.
     *
     * @return The method
     */
    public String getMethod() {
        return StringUtils.upperCase(method);
    }

    /**
     * Get the request parameter with the given name.
     *
     * @param name The parameter name
     * @return The parameter value, or null
     */
    public String getParameter(final String name) {
        return (String) parameters.get(name);
    }

    /**
     * Get the map of all of the parameters.
     *
     * @return The parameters
     */
    public Map getParameterMap() {
        return parameters;
    }

    /**
     * Get the names of all of the parameters.
     *
     * @return The parameter names
     */
    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    /**
     * Get the values for the given parameter.
     *
     * @param name The name of the parameter
     * @return The return values
     */
    public String[] getParameterValues(final String name) {
        Object value = parameters.get(name);
        if (value == null) {
            return new String[0];
        }

        if (value instanceof String[]) {
            return (String[]) value;
        } else {
            String[] result = new String[1];
            result[0] = value.toString();
            return result;
        }
    }

    /**
     * Get the path info.
     *
     * @return The path info
     */
    public String getPathInfo() {
        return pathInfo;
    }

    /**
     * Always returns null.
     *
     * @return null
     */
    public String getPathTranslated() {
        return null;
    }

    /**
     * Get the protocol.
     *
     * @return Always HTTP/1.1
     */
    public String getProtocol() {
        return "HTTP/1.1";
    }

    /**
     * Get the query string part of the request.
     *
     * @return The query string
     */
    public String getQueryString() {
        if (parameters.size() == 0) {
            return null;
        } else {
            final StringBuffer buf = new StringBuffer();
            try {
                for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();) {
                    final String name = (String) iterator.next();
                    final String value = (String) parameters.get(name);
                    buf.append(URLEncoder.encode(name, "UTF-8"));
                    buf.append('=');
                    buf.append(URLEncoder.encode(value, "UTF-8"));
                    if (iterator.hasNext()) {
                        buf.append('&');
                    }
                }
            } catch (UnsupportedEncodingException e) {
            // Should never happen!
            }
            return buf.toString();
        }
    }

    /**
     * This feature is not implemented at this time as we are not supporting
     * binary servlet input. This functionality may be added in the future.
     *
     * @return The reader
     * @throws IOException If an I/O related problem occurs
     */
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new CharArrayReader(new char[0]));
    }

    /**
     * Deprecated method - should not be used.
     *
     * @param name The name
     *
     * @return The path
     *
     * @deprecated Use ServletContext.getRealPath(String) instead.
     */
    public String getRealPath(String name) {
        return servletContext.getRealPath(name);
    }

    /**
     * Get the remote address of the client.
     *
     * @return Always 127.0.0.1
     */
    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    /**
     * Get the remote host.
     *
     * @return Always localhost
     */
    public String getRemoteHost() {
        return "localhost";
    }

    /**
     * Get the name of the remote user from the REMOTE_USER header.
     *
     * @return The name of the remote user
     */
    public String getRemoteUser() {
        return getHeader("REMOTE_USER");
    }

    public String getLocalAddr() {
        return "127.0.0.1";
    }

    public String getLocalName() {
        return "127.0.0.1";
    }

    public int getLocalPort() {
        return 80;
    }

    public int getRemotePort() {
        return 80;
    }

    /**
     * Returns a RequestDispatcher for the specified path. The dispatcher
     * will not dispatch to the resource. It only records the specified path
     * so that one can test if the correct path was dispatched to.
     *
     * @param path a String specifying the pathname to the resource
     * @return a dispatcher for the specified path
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return servletContext.getRequestDispatcher(path);
    }

    /**
     * Get the requested session id. Always returns the id of the current
     * session.
     *
     * @return The session id
     */
    public String getRequestedSessionId() {
        return session.getId();
    }

    /**
     * Returns context path and servlet path concatenated, typically
     * /applicationClassName/applicationClassName
     *
     * @return The path value
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    public String getRequestURI() {
        if (url == null) {
            return getContextPath() + getServletPath();
        }
        return url;
    }

    /**
     * Returns (an attempt at) a reconstructed URL based on it's constituent
     * parts.
     */
    public StringBuffer getRequestURL() {
        StringBuffer buffer = new StringBuffer().append(getScheme());
        buffer.append("://");
        buffer.append(this.getServerName());
        buffer.append(":");
        buffer.append(this.getServerPort());
        buffer.append(this.contextPath);
        buffer.append(this.servletPath);

        if (getPathInfo() != null) {
            buffer.append(getPathInfo());
        }

        if (!isPost()) {
            final String query = getQueryString();
            if (query != null) {
                buffer.append('?');
                buffer.append(query);
            }
        }
        return buffer;
    }

    public boolean isPost() {
        return getMethod().equalsIgnoreCase("post");
    }

    /**
     * Get the scheme.
     *
     * @return the scheme used by this request
     */
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Get the server name.
     *
     * @return Always localhost
     */
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the server port.
     *
     * @return Always the server port
     */
    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Get the context path.
     *
     * @return The context path
     */
    public String getContextPath() {

        // If request's contextPath was set manually (eg user set 
        // servletContext's contextPath), return that value.
        if (!MockServletContext.DEFAULT_CONTEXT_PATH.equals(contextPath)) {
            return contextPath;
        }

        // If servletContext path was set manually (eg user set servletContext's
        // contextPath) then use that value in preference to the request default
        // contextPath.
        if (!MockServletContext.DEFAULT_CONTEXT_PATH.equals(servletContext.getContextPath())) {
            return servletContext.getContextPath();
        }

        // Lastly fallback to the default contextPath.
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * The servlet path may either be the application name or /.
     * For test purposes we always return the servlet name.
     *
     * @return The servlet path
     */
    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * Get the sessions.
     *
     * @return The session
     */
    public HttpSession getSession() {
        return getSession(true);
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * Get the session.
     *
     * @param create if true creates a new session if one does not exist
     * @return The session
     */
    public HttpSession getSession(boolean create) {
        if (session != null) {
            return session;
        }
        if (create) {
            String sessionId = createSessionId();
            session = new MockSession(sessionId, servletContext);
        }
        return session;
    }

    /**
     * Get the user principal.
     *
     * @return A user principal
     */
    public Principal getUserPrincipal() {
        final String user = getRemoteUser();
        if (user == null) {
            return null;
        } else {
            return new Principal() {

                public String getName() {
                    return user;
                }
            };
        }
    }

    /**
     * @return True if there has been added files to this request using
     * {@link #addFile(String, File, String)}
     */
    public boolean hasUploadedFiles() {
        return uploadedFiles != null;
    }

    /**
     * Reset the request back to a default state.
     */
    public void initialize() {
        authType = null;
        method = "post";
        cookies.clear();
        setDefaultHeaders();
        pathInfo = null;
        url = null;
        characterEncoding = "UTF-8";
        parameters.clear();
        attributes.clear();
    }

    /**
     * Delegate to initialize method.
     */
    public void reset() {
        initialize();
    }

    /**
     * Check whether session id is from a cookie. Always returns true.
     *
     * @return Always true
     */
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    /**
     * Check whether session id is from a url rewrite. Always returns false.
     *
     * @return Always false
     */
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    /**
     * Check whether session id is from a url rewrite. Always returns false.
     *
     * @return Always false
     */
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /**
     * Check whether the session id is valid.
     *
     * @return Always true
     */
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    /**
     * Always returns false.
     *
     * @return Always false
     */
    public boolean isSecure() {
        return this.scheme.equalsIgnoreCase("https");
    }

    /**
     * NOT IMPLEMENTED.
     *
     * @param name The role name
     * @return Always false
     */
    public boolean isUserInRole(String name) {
        return false;
    }

    /**
     * Remove the given attribute.
     *
     * @param name The name of the attribute
     */
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    /**
     * Set the given attribute.
     *
     * @param name The attribute name
     * @param o The value to set
     */
    public void setAttribute(final String name, final Object o) {
        attributes.put(name, o);
    }

    /**
     * Set the auth type.
     *
     * @param authType The auth type
     */
    public void setAuthType(final String authType) {
        this.authType = authType;
    }

    /**
     * Set the character encoding.
     *
     * @param encoding The character encoding
     * @throws UnsupportedEncodingException If encoding not supported
     */
    public void setCharacterEncoding(final String encoding)
        throws UnsupportedEncodingException {
        characterEncoding = encoding;
    }

    /**
     * Set the cookies.
     *
     * @param theCookies The cookies
     */
    public void setCookies(final Cookie[] theCookies) {
        cookies.clear();
        for (int i = 0; i < theCookies.length; i++) {
            cookies.add(theCookies[i]);
        }
    }

    /**
     * Set the method.
     *
     * @param method The method
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Set a parameter.
     *
     * @param name The name
     * @param value The value
     */
    public void setParameter(final String name, final String value) {
        parameters.put(name, value);
    }

    /**
     * Set the specified parameter name to the array of strings.
     *
     * @param name name of the parameter
     * @param values the parameter values
     */
    public void setParameter(final String name, final String[] values) {
        parameters.put(name, values);
    }

    /**
     * Remove the specified parameter.
     *
     * @param name the parameter name to remove
     */
    public void removeParameter(final String name) {
        parameters.remove(name);
    }

    /**
     * Sets a map of parameters.
     *
     * @param parameters the parameters to set
     */
    public void setParameters(final Map parameters) {
        this.parameters.putAll(parameters);
    }

    /**
     * Set the path that this request is supposed to be serving. The path is
     * relative to the web application root and should start with a / character
     *
     * @param path
     */
    public void setPathInfo(final String path) {
        this.pathInfo = path;
    }

    /**
     * Returns the url that was forwarded to, otherwise return null.
     *
     * @see net.sf.click.servlet.MockRequestDispatcher#forward(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     *
     * @return url that was forwarded to
     */
    public String getForward() {
        return this.forward;
    }

    /**
     * Returns the list of server side included url's.
     *
     * @see net.sf.click.servlet.MockRequestDispatcher#include(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     *
     * @return list of urls that were included
     */
    public List getIncludes() {
        return this.includes;
    }

    public String toString() {
        return getRequestURL().toString();
    }

    // ------------------------------------------------ package methods
    /**
     * MockRequestDispatcher adds server side included url's to the request.
     *
     * @param url the url to include
     */
    void addInclude(String url) {
        this.includes.add(url);
    }

    /**
     * MockRequestDispatcher sets the forward url on the request.
     *
     * @param url the url to forward to
     */
    void setForward(String url) {
        this.forward = url;
    }

    // -------------------------------------------------------- Private methods

    /**
     * Helper method to create some default headers for the request
     */
    private void setDefaultHeaders() {
        headers.clear();
        addHeader("Accept", "text/xml,application/xml,application/xhtml+xml," + "text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        Locale l = locale;
        addHeader("Accept-Language", l.getLanguage().toLowerCase() + "-" +
            l.getCountry().toLowerCase() + "," + l.getLanguage().toLowerCase() + ";q=0.5");
        addHeader("User-Agent",
            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7) Gecko/20040707 Firefox/0.9.2");
    }

    private static final String crlf = "\r\n";

    private static final String boundary = "--abcdefgABCDEFG";

    private void newAttachment(OutputStream out) throws IOException {
        out.write(boundary.getBytes());
        out.write(crlf.getBytes());
        out.write("Content-Disposition: form-data".getBytes());
    }

    /**
     * Build the request based on the uploaded files and the parameters.
     *
     * @return The request as a string.
     */
    private byte[] buildRequest() {
        try {
            // Build up the input stream based on the files and parameters
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Add parameters
            for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();) {
                final String name = (String) iterator.next();
                newAttachment(out);
                out.write("; name=\"".getBytes());
                out.write(name.getBytes());
                out.write("\"".getBytes());
                out.write(crlf.getBytes());
                out.write(crlf.getBytes());
                out.write(parameters.get(name).toString().getBytes());
                out.write(crlf.getBytes());
            }

            // Add files
            if (uploadedFiles != null) {
                for (Iterator iterator = uploadedFiles.keySet().iterator(); iterator.hasNext();) {
                    String fieldName = (String) iterator.next();

                    UploadedFile uf = (UploadedFile) uploadedFiles.get(fieldName);

                    newAttachment(out);
                    out.write("; name=\"".getBytes());
                    out.write(fieldName.getBytes());
                    out.write("\"; filename=\"".getBytes());
                    out.write(uf.getFile().getName().getBytes());
                    out.write("\"".getBytes());
                    out.write(crlf.getBytes());
                    out.write("Content-Type: ".getBytes());
                    out.write(uf.getContentType().getBytes());
                    out.write(crlf.getBytes());
                    out.write(crlf.getBytes());

                    // Load the file and put it into the the inputstream
                    FileInputStream fis = new FileInputStream(uf.getFile());
                    IOUtils.copy(fis, out);
                    fis.close();
                    out.write(crlf.getBytes());
                }
            }

            out.write(boundary.getBytes());
            out.write("--".getBytes());
            out.write(crlf.getBytes());
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createSessionId() {
        String mockId = getRemoteAddr().replaceAll("\\.", "") + "_" +
            System.currentTimeMillis() + "_" +
            Math.abs(random.nextLong());
        try {
            //make it look secure ;-)
            mockId = ClickUtils.toMD5Hash(mockId);
        } catch (Exception e) {
        //ignore
        }
        return mockId;
    }

    /**
     * A holder class for an uploaded file.
     *
     * @author Frank Bille (billen)
     */
    private class UploadedFile {

        private String fieldName;

        private File file;

        private String contentType;

        /**
         * Construct.
         *
         * @param fieldName
         * @param file
         * @param contentType
         */
        public UploadedFile(String fieldName, File file, String contentType) {
            this.fieldName = fieldName;
            this.file = file;
            this.contentType = contentType;
        }

        /**
         * @return The content type of the file. Mime type.
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * @param contentType The content type.
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * @return The field name.
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * @param fieldName
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * @return The uploaded file.
         */
        public File getFile() {
            return file;
        }

        /**
         * @param file
         */
        public void setFile(File file) {
            this.file = file;
        }
    }

}
