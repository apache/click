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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.click.util.ClickUtils;
import net.sf.click.util.FlashAttribute;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Provides the HTTP request context information for pages. A new Context object
 * is created for each Page.
 *
 * @author Malcolm Edgar
 */
public class Context {

    /** The user's session Locale key: &nbsp; <tt>locale</tt>. */
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

    /** The click services interface. */
    protected final ClickServlet.ClickService clickService;

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
     *
     * @param context the servlet context
     * @param config the servlet config
     * @param request the servlet request
     * @param response the servlet response
     * @param isPost the servlet request is a POST
     * @param clickService the click service interface
     */
    public Context(ServletContext context, ServletConfig config,
        HttpServletRequest request, HttpServletResponse response,
        boolean isPost, ClickServlet.ClickService clickService) {

        this.context = context;
        this.config = config;
        this.request = request;
        this.response = response;
        this.isPost = isPost;
        this.clickService = clickService;
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
     * Return true if the HTTP request method is "GET".
     *
     * @return true if the HTTP request method is "GET"
     */
    public boolean isGet() {
        if (isPost) {
            return false;

        } else {
            return getRequest().getMethod().equalsIgnoreCase("GET");
        }
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
        String value = null;

        if (isMultipartRequest()) {
            // If form has not alreay initialized multipart form data, load it
            // now. Form will overwrite it later during the request processing
            if (multiPartFormData == null) {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload fileUpload = new ServletFileUpload(factory);

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
                if (request.getCharacterEncoding() == null) {
                    value = fileItem.getString();

                } else {
                    try {
                        value = fileItem.getString(request.getCharacterEncoding());

                    } catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

        } else {
            value = request.getParameter(name);
        }

        return value;
    }

    /**
     * Return an ordered map of request parameters.
     *
     * @return the ordered map of request parameters
     */
    public Map getRequestParameters() {
        return ClickUtils.getRequestParameters(getRequest());
    }

    /**
     * Return the named session attribute, or null if not defined.
     * <p/>
     * If the session is not defined this method will return null, and a
     * session will not be created.
     * <p/>
     * This method supports {@link FlashAttribute} which when accessed are then
     * removed from the session.
     *
     * @param name the name of the session attribute
     * @return the named session attribute, or null if not defined
     */
    public Object getSessionAttribute(String name) {
        if (hasSession()) {
            Object object = getSession().getAttribute(name);

            if (object instanceof FlashAttribute) {
                FlashAttribute flashObject = (FlashAttribute) object;
                object = flashObject.getValue();
                removeSessionAttribute(name);
            }

            return object;
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
     * Return true if a HttpSession exists, or false otherwise.
     *
     * @return true if a HttpSession exists, or false otherwise
     */
    public boolean hasSession() {
        return (request.getSession(false) != null);
    }

    /**
     * This method will set the named object as a flash HttpSession object.
     * <p/>
     * The flash object will exist in the session until it is accessed once,
     * and then removed. Flash objects are typically used to display a message
     * once.
     *
     * @param name the storage name for the object in the session
     * @param value the object to store in the session
     */
    public void setFlashAttribute(String name, Object value) {
        getSession().setAttribute(name, new FlashAttribute(value));
    }

    /**
     * Return the cookie for the given name or null if not found.
     *
     * @param name the name of the cookie
     * @return the cookie for the given name or null if not found
     */
    public Cookie getCookie(String name) {
        return ClickUtils.getCookie(getRequest(), name);
    }

    /**
     * Return the cookie value for the given name or null if not found.
     *
     * @param name the name of the cookie
     * @return the cookie value for the given name or null if not found
     */
    public String getCookieValue(String name) {
        return ClickUtils.getCookieValue(getRequest(), name);
    }

    /**
     * Sets the given cookie value in the servlet response with the path "/".
     * <p/>
     * @see ClickUtils#setCookie(HttpServletRequest, HttpServletResponse, String, String, int, String)
     *
     * @param name the cookie name
     * @param value the cookie value
     * @param maxAge the maximum age of the cookie in seconds. A negative
     * value will expire the cookie at the end of the session, while 0 will delete
     * the cookie.
     * @return the Cookie object created and set in the response
     */
    public Cookie setCookie(String name, String value, int maxAge) {
        return ClickUtils.setCookie(getRequest(),
                getResponse(),
                name,
                value,
                maxAge,
                "/");
    }

    /**
     * Invalidate the specified cookie and delete it from the response object.
     * Deletes only cookies mapped against the root "/" path.
     *
     * @see ClickUtils#invalidateCookie(HttpServletRequest, HttpServletResponse, String)
     *
     * @param name the name of the cookie you want to delete.
     */
    public void invalidateCookie(String name) {
        ClickUtils.invalidateCookie(getRequest(), getResponse(), name);
    }

    /**
     * Return a new Page instance for the given path.
     * <p/>
     * This method can be used to create a target page for the
     * {@link Page#setForward(Page)}, for example:
     *
     * <pre class="codeJava">
     * UserEdit userEdit = (UserEdit) getContext().createPage(<span class="st">"/user-edit.htm"</span>);
     * userEdit.setUser(user);
     *
     * setForward(userEdit); </pre>
     *
     * @param path the Page path as configured in the click.xml file
     * @return a new Page object
     * @throws IllegalArgumentException if the Page is not found
     */
    public Page createPage(String path) {
        return clickService.createPage(path, request);
    }

    /**
     * Return a new Page instance for the given class.
     * <p/>
     * This method can be used to create a target page for the
     * {@link Page#setForward(Page)}, for example:
     *
     * <pre class="codeJava">
     * UserEdit userEdit = (UserEdit) getContext().createPage(UserEdit.<span class="kw">class</span>);
     * userEdit.setUser(user);
     *
     * setForward(userEdit); </pre>
     *
     * @param pageClass the Page class as configured in the click.xml file
     * @return a new Page object
     * @throws IllegalArgumentException if the Page is not found, or is not
     * configured with a unique path
     */
    public Page createPage(Class pageClass) {
        return clickService.createPage(pageClass, request);
    }

    /**
     * Return the path for the given page Class.
     *
     * @param pageClass the class of the Page to lookup the path for
     * @return the path for the given page Class
     * @throws IllegalArgumentException if the Page Class is not configured
     * with a unique path
     */
    public String getPagePath(Class pageClass) {
        return clickService.getPagePath(pageClass);
    }

    /**
     * Return the Click application mode value: &nbsp;
     * <tt>["production", "profile", "development", "debug"]</tt>.
     *
     * @return the application mode value
     */
    public String getApplicationMode() {
        return clickService.getApplicationMode();
    }

    /**
     * Return the Click application charset or ISO-8859-1 if not is defined.
     * <p/>
     * The charset is defined in click.xml through the charset attribute
     * on the click-app element.
     *
     * <pre class="codeConfig">
     * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
     * &lt;click-app <span class="blue">charset</span>="<span class="red">UTF-8</span>"&gt;
     *    ..
     * &lt;/click-app&gt; </pre>
     *
     * @return the application charset or ISO-8859-1 if not defined
     */
    public String getCharset() {
        String charset = clickService.getCharset();
        if (charset == null) {
            charset = "ISO-8859-1";
        }
        return charset;
    }

    /**
     * Return the users Locale.
     * <p/>
     * If the users Locale is stored in their session this will be returned.
     * Else if the click-app configuration defines a default Locale this
     * value will be returned, otherwise the request's Locale will be returned.
     * <p/>
     * To override the default request Locale set the users Locale using the
     * {@link #setLocale(Locale)} method.
     * <p/>
     * Pages and Controls obtain the users Locale using this method.
     *
     * @return the users Locale in the session, or if null the request Locale
     */
    public Locale getLocale() {
        Locale locale = (Locale) getSessionAttribute(LOCALE);

        if (locale == null) {

            if (clickService.getLocale() != null) {
                locale = clickService.getLocale();

            } else {
                locale = getRequest().getLocale();
            }
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
        return (isPost() && ServletFileUpload.isMultipartContent(request));
    }

    /**
     * Return a rendered Velocity template and model for the given
     * class and model data.
     * <p/>
     * This method will merge the class <tt>.htm</tt> Velocity template and
     * model data using the applications Velocity Engine.
     * <p/>
     * An example of the class template resolution is provided below:
     * <pre class="codeConfig">
     * <span class="cm">// Full class name</span>
     * com.mycorp.control.CustomTextField
     *
     * <span class="cm">// Template path name</span>
     * /com/mycorp/control/CustomTextField.htm </pre>
     *
     * Example method usage:
     * <pre class="codeJava">
     * <span class="kw">public String</span> toString() {
     *     Map model = getModel();
     *     <span class="kw">return</span> getContext().renderTemplate(getClass(), model);
     * } </pre>
     *
     * @param templateClass the class to resolve the template for
     * @param model the model data to merge with the template
     * @return rendered Velocity template merged with the model data
     * @throws RuntimeException if an error occurs
     */
    public String renderTemplate(Class templateClass, Map model) {
        return clickService.renderTemplate(templateClass, model);
    }

    /**
     * Return a rendered Velocity template and model data.
     * <p/>
     * Example method usage:
     * <pre class="codeJava">
     * <span class="kw">public String</span> toString() {
     *     Map model = getModel();
     *     <span class="kw">return</span> getContext().renderTemplate(<span class="st">"/custom-table.htm"</span>, model);
     * } </pre>
     *
     * @param templatePath the path of the Velocity template to render
     * @param model the model data to merge with the template
     * @return rendered Velocity template merged with the model data
     * @throws RuntimeException if an error occurs
     */
    public String renderTemplate(String templatePath, Map model) {
        return clickService.renderTemplate(templatePath, model);
    }

}
