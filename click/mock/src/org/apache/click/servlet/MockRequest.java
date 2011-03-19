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
import org.apache.click.util.ClickUtils;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Mock implementation of {@link javax.servlet.http.HttpServletRequest}.
 * <p/>
 * Implements all of the methods from the standard HttpServletRequest class
 * plus helper methods to aid setting up a request.
 * <p/>
 * This class was adapted from <a href="http://wicket.apache.org">Apache Wicket</a>.
 */
public class MockRequest implements HttpServletRequest {

    // -------------------------------------------------------- Constants

    /** Newline indicator. */
    private static final String CRLF = "\r\n";

    /** File attachment boundary indicator. */
    private static final String BOUNDARY = "--abcdefgABCDEFG";

    /** The REMOTE_USER header. */
    public static final String REMOTE_USER = "REMOTE_USER";

    // -------------------------------------------------------- Variables

    /** The request default locale. */
    private Locale locale = Locale.getDefault();

    /** The request attributes map. */
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    /** The request authentication type (BASIC, FORM, DIGEST, CLIENT_CERT). */
    private String authType;

    /** The request character encoding. */
    private String characterEncoding;

    /** The request servlet context. */
    private ServletContext servletContext;

    /** The request list of cookies. */
    private final List<Cookie> cookies = new ArrayList<Cookie>();

    /** The request headers map. */
    private final Map<String, List<String>> headers = new HashMap<String, List<String>>();

    /** The name of the HTTP method with which this request was made. */
    private String method = "POST";

    /** The request parameter map. */
    private final Map<String, Object> parameters = new HashMap<String, Object>();

    /** The request HTTP session. */
    private HttpSession session;

    /**
     * Map of uploaded files, where the fieldname is the key and uploaded file
     * is the value.
     */
    private Map<String, UploadedFile> uploadedFiles = new HashMap<String, UploadedFile>();

    /**
     * Indicates if this request is multipart (contains binary attachment) or
     * not, false by default.
     */
    private boolean useMultiPartContentType;

    /** The url that was forwarded to. */
    private String forward;

    /** The list of server side included url's. */
    private List<String> includes = new ArrayList<String>();

    /** The scheme used to make this request, defaults to "http". */
    private String scheme = "http";

    /** The request context path, defaults to {@link MockServletContext#DEFAULT_CONTEXT_PATH}. */
    private String contextPath = MockServletContext.DEFAULT_CONTEXT_PATH;

    /** The request servlet path, defaults to an empty String "". */
    private String servletPath = "";

    /** The request path info, defaults to an empty String "". */
    private String pathInfo = "";

    /** The host name to which the request was sent, defaults to "localhost". */
    private String serverName = "localhost";

    /** The port number to which the request was sent, defaults to 8080. */
    private int serverPort = 8080;

    /** A random number generator to create unique session id's. */
    private Random random = new Random();

    /** The user principal. */
    private Principal userPrincipal;

    /**
     * Create new MockRequest.
     */
    public MockRequest() {
        initialize();
    }

    /**
     * Create new MockRequest for the specified local.
     *
     * @param locale locale for this request
     */
    public MockRequest(final Locale locale) {
        this(locale, null);
    }

    /**
     * Create a new MockRequest for the specified context.
     *
     * @param servletContext the servletContext for this request
     */
    public MockRequest(final ServletContext servletContext) {
        this(null, servletContext);
    }

    /**
     * Create a new MockRequest for the specified locale and servletContext.
     *
     * @param locale locale for this request
     * @param servletContext the servletContext for this request
     */
    public MockRequest(final Locale locale, final ServletContext servletContext) {
        this(locale, servletContext, null);
    }

    /**
     * Create a new MockRequest for the specified arguments.
     *
     * @param locale The request locale, or null to use the default locale
     * @param session The session object
     * @param servletContext The current servlet context
     */
    public MockRequest(final Locale locale, final ServletContext servletContext,
        final HttpSession session) {
        this(locale, MockServletContext.DEFAULT_CONTEXT_PATH, "", servletContext, session);
    }

    /**
     * Create a new MockRequest for the specified arguments.
     *
     * @param locale The request locale, or null to use the default locale
     * @param contextPath the request context path
     * @param servletPath the request servlet path
     * @param servletContext The current servlet context
     * @param session the request session
     */
    public MockRequest(Locale locale, String contextPath, String servletPath,
        final ServletContext servletContext, final HttpSession session) {
        if (locale != null) {
            this.locale = locale;
        }
        this.contextPath = contextPath;
        this.servletPath = servletPath;
        this.session = session;
        this.servletContext = servletContext;
        initialize();
    }

    // -------------------------------------------------------- Mock intialization methods

    /**
     * Set the request's servletContext instance.
     *
     * @param servletContext the new ServletContext instance
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Set the request's session instance.
     *
     * @param session the new HttpSession instance
     */
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
                "File does not exist. You must provide an existing file: "
                + file.getAbsolutePath());
        }

        if (file.isFile() == false) {
            throw new IllegalArgumentException(
                "You can only add a File, which is not a directory. Only files "
                + "can be uploaded.");
        }

        if (uploadedFiles == null) {
            uploadedFiles = new HashMap<String, UploadedFile>();
        }

        UploadedFile uf = new UploadedFile(fieldName, file, contentType);

        uploadedFiles.put(fieldName, uf);
        setUseMultiPartContentType(true);
    }

    /**
     * Add a header to the request.
     *
     * @param name the name of the header to add
     * @param value the value
     */
    public void addHeader(String name, String value) {
        List<String> list = headers.get(name);
        if (list == null) {
            list = new ArrayList<String>(1);
            headers.put(name, list);
        }
        list.add(value);
    }

    /**
     * Set request header value. The existing header value will be replaced.
     *
     * @param name the name of the header to set
     * @param value the header value
     */
    public void setHeader(String name, String value) {
        setHeader(name, new String[] {value});
    }

    /**
     * Set request header values. The existing header values will be replaced.
     *
     * @param name the name of the header to set
     * @param values the header values
     */
    public void setHeader(String name, String... values) {
        List<String> list = new ArrayList<String>(values.length);
        headers.put(name, list);
        for (String value : values) {
            list.add(value);
        }
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
    public Enumeration<String> getAttributeNames() {
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
     * True will force Request to generate multiPart ContentType and ContentLength.
     *
     * @param useMultiPartContentType true if the request is multi-part, false
     * otherwise
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
        return cookies.toArray(result);
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
        final List<String> l = headers.get(name);
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
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    /**
     * Get enumeration of all header values with the given name.
     *
     * @param name The name
     * @return The header values
     */
    public Enumeration<String> getHeaders(final String name) {
        List<String> list = headers.get(name);
        if (list == null) {
            list = new ArrayList<String>();
        }
        return Collections.enumeration(list);
    }

    /**
     * Return the map of headers for this request.
     *
     * @return the map of headers for this request
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
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

                @Override
                public int read() {
                    return bais.read();
                }
            };
        } else {
            return new ServletInputStream() {

                @Override
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
    public Enumeration<Locale> getLocales() {
        List<Locale> list = new ArrayList<Locale>(1);
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
        Object value = parameters.get(name);
        if (value instanceof String[]) {
            return ((String[]) value)[0];
        } else {
            return (String) value;
        }
    }

    /**
     * Get the map of all of the parameters.
     *
     * @return The parameters
     */
    public Map<String, Object> getParameterMap() {
        return parameters;
    }

    /**
     * Get the names of all of the parameters.
     *
     * @return The parameter names
     */
    public Enumeration<String> getParameterNames() {
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
                for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext();) {
                    final String name = iterator.next();
                    final Object value = parameters.get(name);
                    if (value instanceof String[]) {
                        String[] aValue = (String[]) value;
                        for (int i = 0; i < aValue.length; i++) {
                            buf.append(URLEncoder.encode(name, "UTF-8"));
                            buf.append('=');
                            buf.append(URLEncoder.encode(aValue[i], "UTF-8"));
                            if (i < aValue.length) {
                                buf.append("&amp;");
                            }
                        }
                    } else {
                        buf.append(URLEncoder.encode(name, "UTF-8"));
                        buf.append('=');
                        buf.append(URLEncoder.encode((String) value, "UTF-8"));
                    }
                    if (iterator.hasNext()) {
                        buf.append("&amp;");
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
     * Return the name of the {@link #userPrincipal} if set, otherwise
     * the value of the {@value #REMOTE_USER} header.
     * <p/>
     * To set the remote user, create an instance of a {@link MockPrincipal}
     * and set it on the request through the method
     * {@link #setUserPrincipal(java.security.Principal)}.
     *
     * @return the name of the remote user
     */
    public String getRemoteUser() {
        if (userPrincipal != null) {
            return userPrincipal.getName();
        }
        return getHeader(REMOTE_USER);
    }

    /**
     * Return the local address, <em>"127.0.0.1"</em>.
     *
     * @return "127.0.0.1" as the local address
     */
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    /**
     * Return the local name, <em>"127.0.0.1"</em>.
     *
     * @return "127.0.0.1" as the local name
     */
    public String getLocalName() {
        return "127.0.0.1";
    }

    /**
     * Return the local port, <em>80</em>.
     *
     * @return 80 as the local port
     */
    public int getLocalPort() {
        return 80;
    }

    /**
     * Return the remote port, <em>80</em>.
     *
     * @return 80 as the remote port
     */
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
     * <tt>/applicationClassName/applicationClassName</tt>.
     *
     * @return The path value
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    public String getRequestURI() {
        return getContextPath() + getServletPath();
    }

    /**
     * Returns (an attempt at) a reconstructed URL based on it's constituent
     * parts.
     *
     * @return a StringBuffer object containing the reconstructed URL
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

    /**
     * Return whether the request is a post or not.
     *
     * @return true if the request is a post, false otherwise
     */
    public boolean isPost() {
        return getMethod().equalsIgnoreCase("post");
    }

    /**
     * Get the scheme http, https, or ftp.
     *
     * @return the scheme used by this request
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Set the request's scheme, for example http, https, or ftp.
     *
     * @param scheme the request's scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Get the host server name to which the request was sent.
     *
     * @return always the host server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the host server name to which the request was sent.
     *
     * @param serverName the server name the request was sent to
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Returns the port number to which the request was sent.
     *
     * @return the server port to which the request was sent
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Set the port number to which the request was sent.
     *
     * @param serverPort the port number to which the request was sent
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Returns the portion of the request URI that indicates the context of the
     * request.
     *
     * @return the portion of the request URI that indicates the context of
     * the request.
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
        if (servletContext instanceof MockServletContext) {
            MockServletContext mockServletContext = (MockServletContext) servletContext;
            if (!MockServletContext.DEFAULT_CONTEXT_PATH.equals(mockServletContext.getContextPath())) {
               return mockServletContext.getContextPath();
            }
        }

        // Lastly fallback to the default contextPath.
        return contextPath;
    }

    /**
     * Set the portion of the request URI that indicates the context of the
     * request.
     *
     * @param contextPath the portion of the request URI that indicates the
     * context of the request.
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * Return a String containing the name or path of the servlet being called.
     *
     * @return The servlet path
     */
    public String getServletPath() {
        return servletPath;
    }

    /**
     * Set the string containing the name or path of the servlet being called.
     *
     * @param servletPath a String containing the name or path of the servlet
     * being called
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    /**
     * Returns the current HttpSession associated with this request.
     *
     * @return the session associated with this request
     */
    public HttpSession getSession() {
        return getSession(true);
    }

    /**
     * Set the current HttpSession associated with this request.
     *
     * @param session the HttpSession to associate with this request
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * Returns the current HttpSession associated with this request.
     *
     * @param create if true creates a new session if one does not exist
     * @return the current HttpSession associated with this request.
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
     * Get the user principal. If no user principal was set this method will
     * create a user principal for the {@link #getRemoteUser()}.
     *
     * @return the user principal
     */
    public Principal getUserPrincipal() {
        if (userPrincipal == null) {
            final String user = getRemoteUser();

            if (user == null) {
                return null;
            } else {
                userPrincipal = new MockPrincipal() {

                    @Override
                    public String getName() {
                        return user;
                    }
                };
            }
        }
        return userPrincipal;
    }

    /**
     * Set the user principal.
     *
     * @param userPrincipal the user principal
     */
    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    /**
     * @return True if there has been added files to this request using
     * {@link #addFile(String, File, String)}.
     */
    public boolean hasUploadedFiles() {
        return uploadedFiles != null;
    }

    /**
     * Reset the request back to a default state.
     */
    public final void initialize() {
        authType = null;
        method = "post";
        cookies.clear();
        setDefaultHeaders();
        pathInfo = null;
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
     * Returns true if the {@link #getUserPrincipal() authenticated user} is
     * included in the given role, false otherwise.
     * <p/>
     * To mock up roles for a user, create a {@link MockPrincipal user principal}
     * and set the necessary roles. See {@link MockPrincipal} for an example.
     *
     * @param role the role name
     * @return true if the user is included in the specified role, false
     * otherwise
     */
    public boolean isUserInRole(String role) {
        Principal principal = getUserPrincipal();
        if (principal instanceof MockPrincipal) {
            return ((MockPrincipal) principal).getRoles().contains(role);
        }
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
    public void setParameters(final Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
    }

    /**
     * Set the path that this request is supposed to be serving. The path is
     * relative to the web application root and should start with a / character
     *
     * @param path specifies the request path to serve
     */
    public void setPathInfo(final String path) {
        this.pathInfo = path;
    }

    /**
     * Returns the url that was forwarded to, otherwise return null.
     *
     * @see org.apache.click.servlet.MockRequestDispatcher#forward(javax.servlet.ServletRequest,
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
     * @see org.apache.click.servlet.MockRequestDispatcher#include(javax.servlet.ServletRequest,
     * javax.servlet.ServletResponse)
     *
     * @return list of urls that were included
     */
    public List<String> getIncludes() {
        return this.includes;
    }

    /**
     * Returns the String representation of the mock request.
     *
     * @return string representation of the mock request
     */
    @Override
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
     * Helper method to create some default headers for the request.
     */
    private void setDefaultHeaders() {
        headers.clear();
        addHeader("Accept", "text/xml,application/xml,application/xhtml+xml," + "text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        Locale l = locale;
        addHeader("Accept-Language", l.getLanguage().toLowerCase() + "-"
            + l.getCountry().toLowerCase() + "," + l.getLanguage().toLowerCase() + ";q=0.5");
        addHeader("User-Agent",
            "Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.7) Gecko/20040707 Firefox/0.9.2");
    }

    /**
     * Helper method to create new attachment.
     *
     * @param out the output stream to add attachment to
     * @throws java.io.IOException if an I/O error occurs
     */
    private void newAttachment(OutputStream out) throws IOException {
        out.write(BOUNDARY.getBytes());
        out.write(CRLF.getBytes());
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
            for (String name : parameters.keySet()) {
                newAttachment(out);
                out.write("; name=\"".getBytes());
                out.write(name.getBytes());
                out.write("\"".getBytes());
                out.write(CRLF.getBytes());
                out.write(CRLF.getBytes());
                out.write(parameters.get(name).toString().getBytes());
                out.write(CRLF.getBytes());
            }

            // Add files
            if (uploadedFiles != null) {
                for (String fieldName : uploadedFiles.keySet()) {
                    UploadedFile uf = uploadedFiles.get(fieldName);

                    newAttachment(out);
                    out.write("; name=\"".getBytes());
                    out.write(fieldName.getBytes());
                    out.write("\"; filename=\"".getBytes());
                    out.write(uf.getFile().getName().getBytes());
                    out.write("\"".getBytes());
                    out.write(CRLF.getBytes());
                    out.write("Content-Type: ".getBytes());
                    out.write(uf.getContentType().getBytes());
                    out.write(CRLF.getBytes());
                    out.write(CRLF.getBytes());

                    // Load the file and put it into the the inputstream
                    FileInputStream fis = new FileInputStream(uf.getFile());
                    IOUtils.copy(fis, out);
                    fis.close();
                    out.write(CRLF.getBytes());
                }
            }

            out.write(BOUNDARY.getBytes());
            out.write("--".getBytes());
            out.write(CRLF.getBytes());
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create and return a new session id.
     *
     * @return new session id
     */
    private String createSessionId() {
        String mockId = getRemoteAddr().replaceAll("\\.", "") + "_"
            + System.currentTimeMillis() + "_"
            + Math.abs(random.nextLong());
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
    private static class UploadedFile {

        /** Name of the file field. */
        private String fieldName;

        /** The uploaded file. */
        private File file;

        /** The uploaded file content type. */
        private String contentType;

        /**
         * Construct.
         *
         * @param fieldName name of the file field
         * @param file the uploaded file
         * @param contentType the uploaded file content type
         */
        public UploadedFile(String fieldName, File file, String contentType) {
            this.fieldName = fieldName;
            this.file = file;
            this.contentType = contentType;
        }

        /**
         * Return the uploaded file content type.
         *
         * @return The content type of the file. Mime type.
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * Set the uploaded file content type.
         *
         * @param contentType The content type.
         */
        @SuppressWarnings("unused")
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * Return the file field name.
         *
         * @return The field name.
         */
        @SuppressWarnings("unused")
        public String getFieldName() {
            return fieldName;
        }

        /**
         * Set the file field name.
         *
         * @param fieldName the name of the file field
         */
        @SuppressWarnings("unused")
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * Return the uploaded file.
         *
         * @return The uploaded file.
         */
        public File getFile() {
            return file;
        }

        /**
         * Set the uploaded file.
         *
         * @param file the uploaded file
         */
        @SuppressWarnings("unused")
        public void setFile(File file) {
            this.file = file;
        }
    }

}
