/*
 * Copyright 2004-2006 Malcolm A. Edgar
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
package net.sf.click;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Provides a mock HttpServletRequest object for unit testing.
 * 
 * @author Malcolm Edgar
 */
public class MockRequest implements HttpServletRequest {
    
    // ----------------------------------------------------- Instance Variables
    
    protected Map attributes = new HashMap();
    
    protected Map headers = new HashMap();
    
    protected Locale locale = Locale.getDefault();
    
    protected Map parameters = new HashMap();
    
    protected String method = "POST";
        
    // ----------------------------------------------------------- Constructors
    
    public MockRequest() {
    }
    
    public MockRequest(Locale locale) {
        this.locale = locale;
    }
    
    // --------------------------------------------- Test Configuration Methods
    
    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }
    
    public void setHeaders(Map headers) {
        this.headers = headers;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
    
    // -------------------------------------------------------- Request Methods

    public String getAuthType() {
        return null;
    }

    public Cookie[] getCookies() {
        return null;
    }

    public long getDateHeader(String arg0) {
        return 0;
    }

    public String getHeader(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key).toString();
        } else {
            return null;
        }
    }

    public Enumeration getHeaders(String arg0) {
        return null;
    }

    public Enumeration getHeaderNames() {
        return null;
    }

    public int getIntHeader(String arg0) {
        return 0;
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return null;
    }

    public String getPathTranslated() {
        return null;
    }

    public String getContextPath() {
        return null;
    }

    public String getQueryString() {
        return null;
    }

    public String getRemoteUser() {
        return null;
    }

    public boolean isUserInRole(String arg0) {
        return false;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public String getRequestedSessionId() {
        return null;
    }

    public String getRequestURI() {
        return null;
    }

    public StringBuffer getRequestURL() {
        return null;
    }

    public String getServletPath() {
        return null;
    }

    public HttpSession getSession(boolean arg0) {
        return null;
    }

    public HttpSession getSession() {
        return null;
    }

    public boolean isRequestedSessionIdValid() {
        return false;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    public Object getAttribute(String key) {
        if (attributes.containsKey(key)) {
            return attributes.get(key);
        } else {
            return null;
        }
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public void setCharacterEncoding(String arg0)
            throws UnsupportedEncodingException {
    }

    public int getContentLength() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public String getParameter(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key).toString();
        } else {
            return null;
        }
    }

    public Enumeration getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String arg0) {
        return null;
    }

    public Map getParameterMap() {
        return parameters;
    }

    public String getProtocol() {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public Locale getLocale() {
        return locale;
    }

    public Enumeration getLocales() {
        return null;
    }

    public boolean isSecure() {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    public String getRealPath(String arg0) {
        return null;
    }

}
