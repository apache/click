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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.click.util.ClickUtils;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

/**
 * Provides the HTTP request context information for pages. A new Context object
 * is created for each Page.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Context {

    /** The user's session Locale key: &nbsp; <tt>locale</tt> */
    public static final String LOCALE = "locale";

    /** The servlet context. */
    protected final ServletContext context;

    /** The servlet config. */
    protected final ServletConfig config;

    /**
     * The Map of form data for Content-Type <tt>"multipart/form-data"</tt>
     * request.
     */
    protected Map multiPartFormData;

    /** The page maker factory. */
    protected final ClickServlet.PageMaker pageMaker;

    /** The servlet request. */
    protected final HttpServletRequest request;

    /** The servlet response. */
    protected final HttpServletResponse response;

    /** The http session. */
    protected HttpSession session;

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

    // --------------------------------------------------------- Public Methods

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
        return ClickUtils.getResourcePath(request);
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
     * Return the named request parameter. If the request is a Content-type
     * <tt>"multipart/form-data"</tt> POST request, then request parameter will
     * be derived from the {@link #multiPartFormData} map.
     * <p/>
     * Generally the <tt>Form</tt> control is responsible for populating the
     * multipart form data map. However if a call is made to this method
     * before the {@link net.sf.click.control.Form#onProcess()} has had a chance
     * to populate the multipart form data map, then this method will load the
     * data itself using the <tt>DefaultFileItemFactory</tt>.
     * <p/>
     * If the Context loads the multi part data map then the Form will not
     * attempt to reload the data map and any <tt>FileField</tt> configurations
     * will not be applied. The reason for this is because
     * <tt>HttpServletRequest</tt> <tt>InputStream</tt> cannot be processed
     * twice.
     *
     * @see net.sf.click.control.Form#onProcess()
     * @see #isMultipartRequest()
     * @see #getMultiPartFormData()
     *
     * @param name the name of the request parameter
     * @return the value of the request parameter.
     */
    public String getRequestParameter(String name) {
        if (isMultipartRequest()) {
            // If form has not alreay initialized multipart form data, load it
            // now. Form will overwrite it later during the request processing
            if (multiPartFormData == null) {
                FileUpload fileUpload = new FileUpload();
                fileUpload.setFileItemFactory(new DefaultFileItemFactory());

                try {
                    List itemsList = fileUpload.parseRequest(request);

                    Map itemsMap = new HashMap(itemsList.size());
                    for (int i = 0; i < itemsList.size(); i++) {
                        FileItem fileItem = (FileItem) itemsList.get(i);
                        itemsMap.put(fileItem.getFieldName(), fileItem);
                    }

                    multiPartFormData = itemsMap;

                } catch (FileUploadException fue) {
                    throw new RuntimeException(fue);
                }
            }

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
     * Return the users Locale stored in their session, or the request Locale
     * if not available. To override the default request Locale set the users
     * Locale using the {@link #setLocale(Locale)} method.
     * <p/>
     * Pages and Controls obtain the users Locale using this method.
     *
     * @return the users Locale in the session, or if null the request Locale
     */
    public Locale getLocale() {
        Locale locale = (Locale) getSessionAttribute(LOCALE);
        if (locale == null) {
            locale = getRequest().getLocale();
        }
        return locale;
    }

    /**
     * This method stores the given Locale in the users session. If the given
     * Locale is null, the "locale" attribute will be removed from the session.
     * <p/>
     * The Locale object is stored in the session using the {@link #LOCALE}
     * key.
     *
     * @param locale the Locale to store in the users session using the key
     * "locale"
     */
    public void setLocale(Locale locale) {
        if (locale == null && hasSession()) {
            getSession().removeAttribute(LOCALE);
        } else {
            setSessionAttribute(LOCALE, locale);
        }
    }

    /**
     * Returns a map of <tt>FileItem</tt> keyed on name for Content-type
     * "multipart/form-data" requests, or an <tt>Collections.EMPTY_MAP</tt>
     * otherwise.
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
        if (!isMultipartRequest()) {
            String msg = "Not a POST Content-type 'multipart' request";
            throw new IllegalStateException(msg);
        }
        this.multiPartFormData = multiPartFormData;
    }

    /**
     * Return true if the request is a multi-part content type POST request.
     *
     * @return true if the request is a multi-part content type POST request
     */
    public boolean isMultipartRequest() {
        return (isPost() && FileUploadBase.isMultipartContent(request));
    }
}
