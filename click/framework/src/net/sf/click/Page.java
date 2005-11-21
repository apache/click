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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.click.util.MessagesMap;

import org.apache.commons.lang.StringUtils;

/**
 * Provides the Page request event handler class.
 * <p/>
 * The Page class plays a central role in Click applications defining how the
 * application's pages are processed and rendered. All application pages
 * must extend the base Page class, and provide a no arguments constructor.
 * <p/>
 * The default Page execution path for a GET request is:<blockquote><ol>
 * <li>Construct a new Page object.</li>
 * <li>Set {@link #context} property.</li>
 * <li>Set {@link #format} property.</li>
 * <li>Set {@link #headers} property.</li>
 * <li>Set {@link #path} property.</tt>
 * <li>Call {@link #onInit()} to initialize the page.</li>
 * <li>Call {@link #onSecurityCheck()} to check the users permissions.</li>
 * <li>Process any {@link #controls} calling their {@link Control#onProcess()} method.</li>
 * </tt>
 * <li>Call {@link #onGet()} for any additional processing.</li>
 * <li>Render the page merging the {@link #model} with the
 * Velocity template defined by the {@link #getTemplate()}.</li>
 * <li>Call {@link #onDestroy()} to clean up any resources.</li>
 * </ol></blockquote>
 * For POST requests the default execution path is identical, except the
 * {@link #onPost()} method is called instead of {@link #onGet()}.
 * <p/>
 * When a Velocity template is rendered the ClickServlet uses Pages:<ul>
 * <li>the {@link #getTemplate()} to find the Velocity
 * template.</li>
 * <li>the {@link #model} to populate the Velocity Context</tt>
 * <li>the {@link #format} to add to the Velocity Context</tt>
 * <li>the {@link #getContentType()} to set as the HttpServletResponse content type</tt>
 * <li>the {@link #headers} to set as the HttpServletResponse headers</li>
 * </ul>
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class Page {

    /** The request context. */
    protected Context context;

    /** The list of page controls. */
    protected List controls;

    /** The Velocity template formatter object. */
    protected Object format;

    /** The forward path. */
    protected String forward;

    /** The HTTP response headers. */
    protected Map headers;

    /** The headers have been edited flag, to support copy on write. */
    protected boolean headersEdited;

    /** The map of localized page resource messages. **/
    protected MessagesMap messages;

    /** The page model, which is used to populate the Velocity context. */
    protected Map model = new HashMap();

    /** The path of the page template to render. */
    protected String path;

    /** The redirect path. */
    protected String redirect;

    /**
     * Add the control to the page. The control will be added to the pages model
     * using the controls name as the key. The Controls context property will
     * also be set.
     *
     * @param control the control to add
     * @throws IllegalArgumentException if the control is null, or if the name
     * of the control is not defined
     */
    public void addControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        if (StringUtils.isBlank(control.getName())) {
            throw new IllegalArgumentException("Control name not defined");
        }

        getControls().add(control);
        addModel(control.getName(), control);

        if (getContext() != null) {
            control.setContext(getContext());
            control.setParentMessages(getMessages());
        }
    }

    /**
     * Return the list of page Controls.
     *
     * @return the list of page Controls
     */
    public List getControls() {
        if (controls == null) {
            controls = new ArrayList();
        }
        return controls;
    }

    /**
     * Return true if the page has any controls defined.
     *
     * @return true if the page has any controls defined
     */
    public boolean hasControls() {
        return (controls == null) ? false : !controls.isEmpty();
    }

    /**
     * Return the request context of the page.
     *
     * @return the request context of the page
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the request context of the page. If the page model contains any
     * Controls the context will also be set in the Controls.
     *
     * @param context the request context to set
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }
        this.context = context;

        if (hasControls()) {
            for (int i = 0; i < getControls().size(); i++) {
               Control control = (Control) getControls().get(i);
               control.setContext(context);
               control.setParentMessages(getMessages());
            }
        }
    }

    /**
     * Return the HTTP response content type. By default this method returns
     * <tt>"text/html"</tt>.
     * <p/>
     * The ClickServlet uses the pages content type for setting the
     * HttpServletResponse content type.
     *
     * @return the HTTP response content type
     */
    public String getContentType() {
        return "text/html";
    }

    /**
     * Return the Velocity template formatter object.
     * <p/>
     * The ClickServlet adds the format object to the Velocity context using
     * the key <tt>"format"</tt> so that it can be used in the page template.
     *
     * @return the Velocity template formatter object
     */
    public Object getFormat() {
        return format;
    }

    /**
     * Set the Velocity template formatter object.
     *
     * @param value the Velocity template formatter object.
     */
    public void setFormat(Object value) {
        format = value;
    }

    /**
     * Return the path to forward the request to.
     * <p/>
     * If the {@link #forward} property is not null it will be used to forward
     * the request to in preference to rendering the template defined by the
     * {@link #path} property. The request is forwarded using the
     * RequestDispatcher.
     * <p/>
     * See also {@link #getPath()}, {@link #getRedirect()}
     *
     * @return the path to forward the request to
     */
    public String getForward() {
        return forward;
    }

    /**
     * Set the path to forward the request to.
     * <p/>
     * If the {@link #forward} property is not null it will be used to forward
     * the request to in preference to rendering the template defined by the
     * {@link #path} property. The request is forwarded using the
     * RequestDispatcher.
     * <p/>
     * If forward paths start with a <span class="wr">"/"</span>
     * character the forward path is
     * relative to web applications root context, otherwise the path is
     * relative to the request's current location.
     * <p/>
     * For example given a web application deployed to context <tt>mycorp</tt>
     * with the pages:
     * <pre class="codeConfig" style="color:navy">
     *  /index.htm
     *  /customer/search.htm
     *  /customer/details.htm
     *  /customer/management/add-customer.htm </pre>
     *
     * To forward to the customer <tt class="wr">search.htm</tt> page from
     * the web app root you could set forward as
     * <tt>setFoward(<span class="navy">"/customer/search.htm"</span>)</tt>
     * or <tt>setFoward(<span class="navy">"customer/search.htm"</span>)</tt>.
     * <p/>
     * If a user was currently viewing the <tt class="wr">add-customer.htm</tt>
     * to forward to customer <span class="wr">details.htm</span> you could
     * set forward as
     * <tt>setFoward(<span class="navy">"/customer/details.htm"</span>)</tt>
     * or <tt>setFoward(<span class="navy">"../details.htm"</span>)</tt>.
     * <p/>
     * See also {@link #setPath(String)}, {@link #setRedirect(String)}
     *
     * @param value the path to forward the request to
     */
    public void setForward(String value) {
        forward = value;
    }

    /**
     * The Page instance to forward the request to. The given Page object
     * must have a valid path defined.
     *
     * @param page the Page object to forward the request to.
     */
    public void setForward(Page page) {
        if (page == null) {
            throw new IllegalArgumentException("Null page parameter");
        }
        if (page.getPath() == null) {
            throw new IllegalArgumentException("Page has no path defined");
        }
        setForward(page.getPath());
        getContext().setRequestAttribute(ClickServlet.FORWARD_PAGE, page);
    }

    /**
     * Return the map of HTTP header to be set in the HttpServletResponse.
     * Note to edit header values use {@link #setHeader(String, Object)} as
     * headers Map is initially unmodifiable.
     *
     * @return the map of HTTP header to be set in the HttpServletResponse
     */
    public Map getHeaders() {
        return headers;
    }

    /**
     * Set the named header with the given value. This method uses copy on
     * write to the headers Map, as the initial loaded headers Map is
     * unmodifiable.
     *
     * @param name the name of the header
     * @param value the value of the header
     */
    public void setHeader(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Null header name parameter");
        }
        if (!headersEdited) {
            headersEdited = true;
            headers = new HashMap(headers);
        }
        headers.put(name, value);
    }

    /**
     * Set the map of HTTP header to be set in the HttpServletResponse.
     *
     * @param value the map of HTTP header to be set in the HttpServletResponse
     */
    public void setHeaders(Map value) {
        headers = value;
    }

    /**
     * Return the localized Page resource message for the given resource
     * property key. The resource message returned will use the Locale obtained
     * from the Context.
     * <p/>
     * Pages can define text properties files to store localized messages. These
     * properties files must be stored on the Page class path with a name
     * matching the class name. For example:
     * <p/>
     * The page class:
     * <pre class="codeJava">
     *  <span class="kw">package</span> com.mycorp.pages;
     *
     *  <span class="kw">public class</span> Login <span class="kw">extends</span> Page {
     *     .. </pre>
     *
     * The page class property filenames and their path:
     * <pre class="codeConfig">
     *  /com/mycorp/pages/Login.properties
     *  /com/mycorp/pages/Login_en.properties
     *  /com/mycorp/pages/Login_fr.properties </pre>
     *
     * Note page messages can be accessed directly in the page template using
     * the <span class="st">$messages</span> reference. For examples:
     *
     * <pre class="codeHtml">
     * <span class="blue">$messages.title</span> </pre>
     *
     * Please see the {@link net.sf.click.util.MessagesMap} adaptor for more details.
     *
     * @param key the message property key name
     * @return the Page message for the given message property key
     */
    public String getMessage(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key parameter");
        }
        return (String) getMessages().get(key);
    }

    /**
     * Return a Map of localized messages for the Page.
     *
     * @return a Map of localized messages for the Page
     * @throws IllegalStateException if the context for the Page has not be set
     */
    public Map getMessages() {
        if (messages == null) {
            if (getContext() != null) {
                String baseName = getClass().getName();
                Locale locale = getContext().getLocale();
                messages = new MessagesMap(baseName, locale);

            } else {
                String msg = "Context not set cannot initialize messages";
                throw new IllegalStateException(msg);
            }
        }
        return messages;
    }

    /**
     * Add the named object value to the Pages model map.
     *
     * @param name the key name of the object to add
     * @param value the object to add
     * @throws IllegalArgumentException if the name or value parameters are
     * null, or if there is already a named value in the model
     */
    public void addModel(String name, Object value) {
        if (name == null) {
            String msg = "Cannot add null parameter name to " +
                         getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "Cannot add null " + name + " parameter " +
                         "to " + getClass().getName() + " model";
            throw new IllegalArgumentException(msg);
        }
        if (getModel().containsKey(name)) {
            String msg = getClass().getName() + " model already contains " +
                         "value named " + name;
            throw new IllegalArgumentException(msg);
        } else {
            getModel().put(name, value);
        }
    }

    /**
     * Return the Page's model map. The model is used populate the
     * Velocity Context with is merged with the page template before rendering.
     *
     * @return the Page's model map
     */
    public Map getModel() {
        return model;
    }

    /**
     * Return the path of the Velocity template to render.
     * <p/>
     * See also {@link #getForward()}, {@link #getRedirect()}
     *
     * @return the path of the Velocity template to render
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path of the Velocity template to render.
     * <p/>
     * See also {@link #setForward(String)}, {@link #setRedirect(String)}
     *
     * @param value the path of the Velocity template to render
     */
    public void setPath(String value) {
        path = value;
    }

    /**
     * Return the path to redirect the request to.
     * <p/>
     * If the {@link #redirect} property is not null it will be used to redirect
     * the request in preference to {@link #forward} or {@link #path} properties.
     * The request is redirected to using the HttpServletResponse.setRedirect()
     * method.
     * <p/>
     * See also {@link #getForward()}, {@link #getPath()}
     *
     * @return the path to redirect the request to
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * Set the location to redirect the request to.
     * <p/>
     * If the {@link #redirect} property is not null it will be used to redirect
     * the request in preference to {@link #forward} or {@link #path} properties.
     * The request is redirected to using the HttpServletResponse.setRedirect()
     * method.
     * <p/>
     * If the redirect location is begins with a <tt class="wr">"/"</tt>
     * character the redirect location will be prefixed with the web applications
     * context path.
     * <p/>
     * For example if an application is deployed to the context
     * <tt class="wr">"mycorp"</tt> calling
     * <tt>setRedirect(<span class="navy">"/customer/details.htm"</span>)</tt>
     * will redirect the request to:
     * <tt class="wr">"/mycorp/customer/details.htm"</tt>
     * <p/>
     * See also {@link #setForward(String)}, {@link #setPath(String)}
     *
     * @param location the path to redirect the request to
     */
    public void setRedirect(String location) {
        redirect = location;
    }

    /**
     * Return the path of the page template to render, by default this method
     * returns {@link #getPath()}.
     * <p/>
     * Pages can override this method to return an alternative page template.
     * This is very useful when implementing an standardized look and feel for
     * a web site. The example below provides a BorderedPage base Page which
     * other site templated Pages should extend.
     *
     * <pre class="codeJava">
     * <span class="kw">public class</span> BorderedPage <span class="kw">extends</span> Page {
     *     <span class="kw">public</span> String getTemplate() {
     *         <span class="kw">return</span> <span class="st">"border.htm"</span>;
     *     }
     * } </pre>
     *
     * The BorderedPage returns the page border template <span class="st">"border.htm"</span>:
     *
     * <pre class="codeHtml">
     * &lt;html&gt;
     *   &lt;head&gt;
     *     &lt;title&gt; <span class="blue">$title</span> &lt;/title&gt;
     *     &lt;link rel="stylesheet" type="text/css" href="style.css" title="Style"/&gt;
     *   &lt;/head&gt;
     *   &lt;body&gt;
     *
     *     &lt;h1&gt; <span class="blue">$title</span> &lt;/h1&gt;
     *     &lt;hr/&gt;
     *
     *     <span class="red">#parse</span>( <span class="blue">$path</span> )
     *
     *   &lt;/body&gt;
     * &lt;/html&gt; </pre>
     *
     * Other pages insert their content into this template, via their
     * {@link #path} property using the Velocity
     * <a href="../../../../velocity/vtl-reference-guide.html#parse">#parse</a>
     * directive. Note the <span class="blue">$path</span> value is automatically
     * added to the VelocityContext by the ClickServlet.
     *
     * @return the path of the page template to render, by default returns
     * {@link #getPath()}.
     */
    public String getTemplate() {
        return getPath();
    }

    /**
     * The on Initialization event handler. This event handler is invoked after
     * the pages constructor has been called and all the page poperties have
     * been set.
     * <p/>
     * Subclasses should place their control initialization code in this method.
     * <p/>
     * Time consuming operations such as fetching the results of a database
     * query should not be placed in this method. These operations should be
     * performed in the {@link #onGet()} or {@link #onPost()} methods so that
     * other event handlers may take alternative execution paths without
     * performing these expensive operations.
     */
    public void onInit() {
    }

    /**
     * The on Security Check event handler.
     * <p/>
     * Security check provides the Page an opportunity to check the users
     * security credentials before processing the Page. This method is called
     * immediately after the <tt>onInit</tt> method.
     * <p/>
     * If security check returns true the Page is processed as
     * normal. If the method returns false then the no Page controls are
     * processed and the <tt>onGet()</tt> or <tt>onPost()</tt> methods
     * are not invoked.
     * <p/>
     * If the method returns false, the forward or redirect property should be
     * set to send the request to another Page.
     * <p/>
     * By default this method returns true, subclass may override this method
     * to provide their security authorisation/authentication mechanism.
     *
     * @return true
     */
    public boolean onSecurityCheck() {
        return true;
    }

    /**
     * The on Get request event handler. This event handler is invoked if the
     * HTTP request method is "GET".
     * <p/>
     * The event handler is invoked after {@link #onSecurityCheck()} has been
     * called and all the Page {@link #controls} have been processed. If either
     * the security check or one of the controls cancels continued event
     * processing the <tt>onGet()</tt> method will not be invoked.
     */
    public void onGet() {
    }
 
    /**
     * The on Post request event handler. This event handler is invoked if the
     * HTTP request method is "POST".
     * <p/>
     * The event handler is invoked after {@link #onSecurityCheck()} has been
     * called and all the Page {@link #controls} have been processed. If either
     * the security check or one of the controls cancels continued event
     * processing the <tt>onPost()</tt> method will not be invoked.
     */
    public void onPost() {
    }

    /**
     * The on Destroy request event handler. Subclasses may override this method
     * to add any resource clean up code.
     * <p/>
     * This method is guaranteed to be called before the Page object reference
     * goes out of scope and is available for garbage collection.
     */
    public void onDestroy() {
    }

}
