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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


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
 * Velocity template defined by the {@link #path}.</li>
 * <li>Call {@link #onFinally()} to clean up any resources.</li>
 * </ol></blockquote>
 * For POST requests the default execution path is identical, except the 
 * {@link #onPost()} method is called instead of {@link #onGet()}.
 * <p/>
 * When a Velocity template is rendered the ClickServlet uses Pages:<ul>
 * <li>the {@link #path} to find the Velocity
 * template.</li>
 * <li>the {@link #model} to populate the Velocity Context</tt>
 * <li>the {@link #format} to add to the Velocity Context</tt>
 * <li>the {@link #getContentType()} to set as the HttpServletResponse content type</tt>
 * <li>the {@link #headers} to set as the HttpServletResponse headers</li>
 * </ul>
 * 
 * @author Malcolm Edgar
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
    
    /** The messages resource bundle. **/
    protected ResourceBundle messages;

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
     * @throws IllegalArgumentException if the control is null
     */
    public void addControl(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("Null control parameter");
        }
        if (controls == null) {
            controls = new ArrayList();
        }
        controls.add(control);
        control.setContext(getContext());
        
        addModel(control.getName(), control);
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
     * Set the request context of the page.
     * 
     * @param value the request context to set
     */
    public void setContext(Context value) {
        context = value;
    }
    
    /**
     * Return the HTTP response content type, by default <tt>"text/html"</tt>.
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
     * See also {@link #setPath(String)}, {@link #setRedirect(String)}
     * 
     * @param value the path to forward the request to
     */
    public void setForward(String value) {
        forward = value;
    }
    
    /**
     * Return the map of HTTP header to be set in the HttpServletResponse.
     *  
     * @return the map of HTTP header to be set in the HttpServletResponse
     */
    public Map getHeaders() {
        return headers;
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
     * Return the Page resource message for the given resource property key. The 
     * resource message returned will use the Local of the HttpServletRequest.
     * <p/>
     * Pages can define text properties files to store localized messages. These
     * properties files must be stored on the Page class path with a name 
     * matching the class name. For example:
     * <blockquote>
     * <pre>
     *  // The page classname
     *  com.mycorp.pages.Login
     * 
     *  // The properties filenames and location
     *  /com/mycorp/pages/Login.properties
     *  /com/mycorp/pages/Login_en.properties
     *  /com/mycorp/pages/Login_fr.properties
     * </pre>
     * </blockquote>
     * 
     * @param key the message property key name
     * @return the Page message for the given message property key
     * @throws MissingResourceException if the properties file could not be
     * found, or the message for the given key could not be found.
     */
    public String getMessage(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Null key parameter");
        }
        if (messages == null) {
            Locale locale = getContext().getRequest().getLocale();
            messages = ResourceBundle.getBundle(getClass().getName(), locale);
        }
        return messages.getString(key);
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
            throw new IllegalArgumentException("Null name parameter");
        }
        if (value == null) {
            throw new IllegalArgumentException("Null value parameter");
        }
        if (getModel().containsKey(name)) {
            throw new IllegalArgumentException
                ("Page model already contains element named: " + name);
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
     * The request is redirected to using the HttpServletResponse 
     * setRedirect() method.
     * <p/>
     * See also {@link #getForward()}, {@link #getPath()}
     * 
     * @return the path to redirect the request to
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * Set the path to redirect the request to.
     * <p/>
     * If the {@link #redirect} property is not null it will be used to redirect 
     * the request in preference to {@link #forward} or {@link #path} properties.
     * The request is redirected to using the HttpServletResponse 
     * setRedirect() method.
     * <p/>
     * See also {@link #setForward(String)}, {@link #setPath(String)}
     * 
     * @param value the path to redirect the request to
     */
    public void setRedirect(String value) {
        redirect = value;
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
     * The on Finally request event handler. Subclasses may override this method
     * to add any resource clean up code.
     * <p/>
     * This method is guaranteed to be called before the Page object reference 
     * goes out of scope and is available for garbage collection.
     */
    public void onFinally() {
    }

}
