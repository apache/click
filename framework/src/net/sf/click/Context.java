/*
 * Copyright 2004 Malcolm A. Edgar
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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Provides the HTTP request context information for pages. A new Context object
 * will be created for each Page.
 * 
 * @author Malcolm Edgar
 */
public class Context {
    
    /** The servlet context. */
    protected final ServletContext context;

    /** The servlet config. */
    protected final ServletConfig config;

    /** The servlet request. */
    protected final HttpServletRequest request;

    /** The servlet response. */
    protected final HttpServletResponse response;

    /** The http session. */
    protected HttpSession session;
    
    /** The HTTP method is POST flag. */
    protected final boolean isPost;

    /**
     * 
     */
    public Context(ServletContext context, ServletConfig config,
        HttpServletRequest request, HttpServletResponse response,
        boolean isPost) {

        this.context = context;
        this.config = config;
        this.request = request;
        this.response = response;
        this.isPost = isPost;
    }

    /**
     * Return the user's HttpSession, creating one if neccessary.
     *
     * @return the user's HttpSession, creating one if neccessary.
     */
    public HttpSession getSession() {
        if (session == null) {
            session = request.getSession();
        }
        return session;
    }

    /**
     * Returns the servlet request.
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Returns the servlet response.
     *
     * @return HttpServletResponse
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Returns the servlet config.
     *
     * @return ServletConfig
     */
    public ServletConfig getServletConfig() {
        return config;
    }

    /**
     * Returns the servlet context.
     *
     * @return ServletContext
     */
    public ServletContext getServletContext() {
        return context;
    }
    
    /**
     * Return the page resouce path from the request (Request URI path - Request 
     * Context path). For example:
     * <pre>
     * http://www.mycorp.com/banking/secure/login.html  ->  secure/login.html
     * </pre>
     * 
     * @return the page resource path from the request
     */
    public String getResourcePath() {
        int length = request.getContextPath().length();
        return request.getRequestURI().substring(length + 1);
    }
    
    /**
     * Return true if the request has been forwarded. With a forwarded request
     * the URL and URI ends are different.
     * 
     * @return true if the request has been forwarded
     */
    public boolean isForward() {
        String url = request.getRequestURL().toString();
        
        return !url.endsWith(request.getRequestURI());
    }
    
    /**
     * Return true if the HTTP request method is "POST".
     * 
     * @return true if the HTTP request method is "POST"
     */
    public boolean isPost() {
        return isPost;
    }
    
    /**
     * Return the named request attribute, or null if not defined.
     * 
     * @param name the name of the request attribute
     * @return the named request attribute, or null if not defined
     */
    public Object getRequestAttribute(String name) {
        return request.getAttribute(name);
    }
    
    /**
     * This method will set the named object in the HTTP request.
     * 
     * @param name the storage name for the object in the request
     * @param value the object to store in the request
     */
    public void setRequestAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    /**
     * Return the named session attribute, or null if not defined.
     * <p/>
     * If the session is not defined this method will return null, and a
     * session will not be created.
     * 
     * @param name the name of the session attribute
     * @return the named session attribute, or null if not defined
     */
    public Object getSessionAttribute(String name) {
        if (session == null) {
            session = request.getSession(false);
            
            if (session != null) {
                return session.getAttribute(name);
            } else {
                return null;
            }
        } else {
            return session.getAttribute(name);
        }
    }
    
    /**
     * This method will set the named object in the HTTP session.
     * <p/>
     * This method will create a session if one does not alerady exist.
     * 
     * @param name the storage name for the object in the session
     * @param value the object to store in the session
     */
    public void setSessionAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }
}
