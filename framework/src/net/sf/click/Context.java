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
package net.sf.click;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;

/**
 * Provides the HTTP request context information for pages. A new Context object
 * is created for each Page.
 *
 * @author Malcolm Edgar
 */
public class Context {

    /** The servlet context. */
    protected final ServletContext context;

    /** The servlet config. */
    protected final ServletConfig config;

    /** The page maker factory. */
    protected final ClickServlet.PageMaker pageMaker;

    /** The servlet request. */
    protected final HttpServletRequest request;

    /** The servlet response. */
    protected final HttpServletResponse response;

    /** The http session. */
    protected HttpSession session;

    /** The Map of form data for Content-Type "multipart/form-data" request. */
    protected Map multiPartFormData;

    /** The HTTP method is POST flag. */
    protected final boolean isPost;

    /**
     * Create a new request context.
     */
    public Context(ServletContext context, ServletConfig config,
        HttpServletRequest request, HttpServletResponse response,
        boolean isPost, ClickServlet.PageMaker pageMaker) {

        this.context = context;
        this.config = config;
        this.request = request;
        this.response = response;
        this.isPost = isPost;
        this.pageMaker = pageMaker;
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
     * Return the page resouce path from the request. For example:
     * <pre class="codeHtml">
     * <span class="blue">http://www.mycorp.com/banking/secure/login.htm</span>  ->  <span class="red">/secure/login.htm</span> </pre>
     *
     * @return the page resource path from the request
     */
    public String getResourcePath() {
        // Adapted from VelocityViewServlet.handleRequest() method:

        // If we get here from RequestDispatcher.include(), getServletPath()
        // will return the original (wrong) URI requested.  The following special
        // attribute holds the correct path.  See section 8.3 of the Servlet
        // 2.3 specification.

        String path = (String) request.getAttribute("javax.servlet.include.servlet_path");

        // Also take into account the PathInfo stated on SRV.4.4 Request Path Elements.
        String info = (String) request.getAttribute("javax.servlet.include.path_info");

        if (path == null) {
            path = request.getServletPath();
            info = request.getPathInfo();
        }

        if (info != null) {
            path += info;
        }

        return path;
    }

    /**
     * Return true if the request has been forwarded. A forwarded request
     * will contain a {@link ClickServlet#CLICK_FORWARD} request attribute.
     *
     * @return true if the request has been forwarded
     */
    public boolean isForward() {
        return (request.getAttribute(ClickServlet.CLICK_FORWARD) != null);
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
     * Return the named request parameter. If the request Content-type is
     * "multipart/form-data" and the {@link #multiPartFormData} is defined
     * the request parameter will be derived from the multiPartFormData map.
     *
     * @param name the name of the request parameter
     * @return the value of the request parameter.
     */
    public String getRequestParameter(String name) {
        if (isPost() && multiPartFormData != null) {
            FileItem fileItem = (FileItem) multiPartFormData.get(name);
            if (fileItem != null) {
                return fileItem.getString();
            } else {
                return null;
            }

        } else {
            return request.getParameter(name);
        }
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
        if (hasSession()) {
            return getSession().getAttribute(name);
        } else {
            return null;
        }
    }

    /**
     * This method will set the named object in the HttpSession.
     * <p/>
     * This method will create a session if one does not alerady exist.
     *
     * @param name the storage name for the object in the session
     * @param value the object to store in the session
     */
    public void setSessionAttribute(String name, Object value) {
        getSession().setAttribute(name, value);
    }

    /**
     * Remove the named attribute from the session. If the session does not
     * exist or the name is null, this method does nothing.
     *
     * @param name of the attribute to remove from the session
     */
    public void removeSessionAttribute(String name) {
        if (hasSession() && name != null) {
            getSession().removeAttribute(name);
        }
    }

    /**
     * Return true if there is a session and it contains the named attribute.
     *
     * @param name the name of the attribute
     * @return true if the session contains the named attribute
     */
    public boolean hasSessionAttribute(String name) {
        return (getSessionAttribute(name) != null);
    }

    /**
     * Return a object stored in the session using the objects class name.
     * If the object does not exist in the session, an new instance will be
     * created. The specified class must be <tt>public</tt> visibility and
     * provide an no-args public constructor.
     *
     * @param aClass the class of the object to get from the session
     * @return a object stored in the session using the objects class name, or
     * a new object instance if it does not exist.
     */
    public Object getSessionObject(Class aClass) {
        if (aClass == null) {
            throw new IllegalArgumentException("Null class parameter.");
        }
        Object object = getSessionAttribute(aClass.getName());
        if (object == null) {
            try {
                object = aClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    /**
     * Store the given object in the session using the object's class name as
     * the key.
     *
     * @param object the object to store in the session
     */
    public void setSessionObject(Object object) {
        if (object != null) {
            setSessionAttribute(object.getClass().getName(), object);
        }
    }

    /**
     * Remove the class object from the session. If the session does not exist
     * or the class is null, this method does nothing.
     *
     * @param aClass the class object to remove from the session
     */
    public void removeSessionObject(Class aClass) {
        if (hasSession() && aClass != null) {
            getSession().removeAttribute(aClass.getName());
        }
    }

    /**
     * Return true if a HttpSession exists, or false otherwise.
     *
     * @return true if a HttpSession exists, or false otherwise
     */
    public boolean hasSession() {
        return (request.getSession(false) != null);
    }

    /**
     * Return a new Page instance for the given path.
     *
     * @param path the Page path as configured in the click.xml file
     * @return a new Page object
     * @throws IllegalArgumentException if the Page is not found
     */
    public Page createPage(String path) {
        return pageMaker.createPage(path);
    }

    /**
     * Returns a map of <tt>FileItem</tt> keyed on name for Content-type
     * "multipart/form-data" requests, or an empty map otherwise.
     *
     * @return map of <tt>FileItem</tt> keyed on name for
     * "multipart/form-data" requests
     */
    public Map getMultiPartFormData() {
        if (multiPartFormData != null) {
            return multiPartFormData;
        } else {
            return Collections.EMPTY_MAP;
        }
    }

    /**
     * Set the map of FileItem keyed on name for a Content-type
     * "multipart/form-data" request.
     *
     * @param multiPartFormData the map of form FileItem data keyed on name
     */
    public void setMultiPartFormData(Map multiPartFormData) {
        if (!isPost() || !FileUploadBase.isMultipartContent(request)) {
            String msg = "Not a POST Content-type 'multipart' request";
            throw new IllegalStateException(msg);
        }
        this.multiPartFormData = multiPartFormData;
    }
}
