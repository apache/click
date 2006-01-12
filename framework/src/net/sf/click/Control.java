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

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * Provides the interface for Page controls. When a Page request
 * event is processed Controls may perform server side event processing
 * through their {@link #onProcess()} method.
 * <p/>
 * Controls are generally rendered in a Page by calling their
 * <tt>toString()</tt> method.
 *
 * <h4>HTML Header Imports</h4>
 *
 * Control HTML header imports can be exposed by overriding the
 * {@link #getHtmlImports()} method.
 * <p/>
 * For example a custom TextField control specifies that the
 * <tt>custom.js</tt> file should be included in the HTML header imports:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomField <span class="kw">extends</span> TextField {
 *
 *     <span class="kw">protected static final</span> String HTML_IMPORT =
 *         <span class="st">"&lt;script type=\"text/javascript\" src=\"{0}/click/custom.js\"&gt;&lt;/script&gt;\n"</span>;
 *
 *     <span class="kw">public</span> String getHtmlImports() {
 *         String[] args = { getContext().getRequest().getContextPath() };
 *         <span class="kw">return</span> MessageFormat.format(HTML_IMPORTS, args);
 *     }
 *
 *     ..
 * } </pre>
 *
 * <a name="on-deploy"><h4>Deploying Resources</h4></a>
 *
 * The Click framework uses the Velocity Tools <tt>WebappLoader</tt> for loading templates.
 * This avoids issues associate with using the Velocity <tt>ClasspathResourceLoader</tt> and
 * <tt>FileResourceLoader</tt> on J2EE application servers.
 * To make preconfigured resources (templates, stylesheets, etc.) available to web applications
 * Click automatically deploys configured classpath resources to the <tt class="blue">/click</tt>
 * directory at startup (existing files will not be overwritten).
 * <p/>
 * To enable Controls to deploy static resources on startup this interface
 * provides an {@link #onDeploy(ServletContext)} method.
 * <p/>
 * Continuing our example the <tt>CustomField</tt> control deploys its
 * <tt>custom.js</tt> file to the <tt>/click</tt> directory:
 *
 * <pre class="codeJava">
 * <span class="kw">public class</span> CustomField <span class="kw">extends</span> TextField {
 *     ..
 *
 *     <span class="kw">public void</span> onDeploy(ServletContext servletContext) <span class="kw">throws</span> IOException {
 *         ClickUtils.deployFile
 *             (servletContext, <span class="st">"/com/mycorp/control/custom.js"</span>, <span class="st">"click"</span>);
 *     }
 * } </pre>
 *
 * Controls using the <tt>onDeploy()</tt> method must be registered in the
 * application <tt>WEB-INF/click.xml</tt> for them to be invoked.
 * For example:
 *
 * <pre class="codeConfig">
 * &lt;click-app&gt;
 *   &lt;pages package="com.mycorp.page" automapping="true"/&gt;
 *
 *   &lt;controls&gt;
 *     &lt;control classname=<span class="st">"com.mycorp.control.CustomField"</span>/&gt;
 *   &lt;/controls&gt;
 * &lt;/click-app&gt; </pre>
 *
 * When the Click applicatin starts up it will deploy any control elements
 * defined in the following files in sequential order:
 * <ul>
 *  <li><tt>/click-controls.xml</tt>
 *  <li><tt>/extras-controls.xml</tt>
 *  <li><tt>WEB-INF/click.xml</tt>
 * </ul>
 *
 * @author Malcolm Edgar
 */
public interface Control extends Serializable {
 
    /**
     * Return the Page request Context of the Control.
     *
     * @return the Page request Context
     */
    public Context getContext();

    /**
     * Set the Page request Context of the Control.
     *
     * @param context the Page request Context
     * @throws IllegalArgumentException if the Context is null
     */
    public void setContext(Context context);

    /**
     * Return the HTML head element import string. This method returns null.
     * <p/>
     * Override this method to specify JavaScript and CSS includes for the
     * HTML head element. For example:
     *
     * <pre class="codeJava">
     * <span class="kw">protected static final</span> String HTML_IMPORT =
     *     <span class="st">"&lt;script type=\"text/javascript\" src=\"{0}/click/custom.js\"&gt;&lt;/script&gt;\n"</span>;
     *
     * <span class="kw">public</span> String getHtmlImports() {
     *     String[] args = { getContext().getRequest().getContextPath() };
     *     <span class="kw">return</span> MessageFormat.format(HTML_IMPORTS, args);
     * } </pre>
     *
     * @return the HTML head import statements for the control stylesheet and
     * JavaScript files
     */
    public String getHtmlImports();

    /**
     * Return HTML element identifier attribute "id" value.
     *
     * @return HTML element identifier attribute "id" value
     */
    public String getId();


    /**
     * Set the controls event listener.
     * <p/>
     * The method signature of the listener is:<ul>
     * <li>must hava a valid Java method name</li>
     * <li>takes no arguments</li>
     * <li>returns a boolean value</li>
     * </ul>
     * <p/>
     * An example event listener method would be:
     *
     * <pre class="codeJava">
     * <span class="kw">public boolean</span> onClick() {
     *     System.out.println(<span class="st">"onClick called"</span>);
     *     <span class="kw">return true</span>;
     * } </pre>
     *
     * @param listener the listener object with the named method to invoke
     * @param method the name of the method to invoke
     */
    public void setListener(Object listener, String method);

    /**
     * Return the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     *
     * @return the name of the control
     */
    public String getName();
 
    /**
     * Set the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name);

    /**
     * Return the localized messages <tt>Map</tt> of the Control's parent.
     *
     * @return the localization <tt>Map</tt> of the Control's parent
     */
    public Map getParentMessages();

    /**
     * Set the parent's localized messages <tt>Map</tt> for the  Control.
     *
     * @param messages the parent's the localized messages <tt>Map</tt>
     */
    public void setParentMessages(Map messages);

    /**
     * The on deploy event handler, which provides classes the
     * opportunity to deploy static resources when the Click application is
     * initialized.
     * <p/>
     * For example:
     * <pre class="codeJava">
     * <span class="kw">public void</span> onDeploy(ServletContext servletContext) <span class="kw">throws</span> IOException {
     *     ClickUtils.deployFile
     *         (servletContext, <span class="st">"/com/mycorp/control/custom.js"</span>, <span class="st">"click"</span>);
     * } </pre>
     *
     * @param servletContext the servlet context
     * @throws IOException if a resource could not be deployed
     */
    public void onDeploy(ServletContext servletContext) throws IOException;

    /**
     * The on process event handler. Each Page control will be processed when
     * the Page is requested.
     * <p/>
     * These controls may be processed by the ClickServlet, as with the
     * {@link net.sf.click.control.ActionLink} and {@link net.sf.click.control.Form}
     * controls, or they maybe processed by the Form, in the case of
     * {@link net.sf.click.control.Field} controls.
     * <p/>
     * When a control is processed it should return true if the Page should
     * continue event processing, or false if no other controls should be
     * processed and the {@link Page#onGet()} or {@link Page#onPost()} methods
     * should not be invoked.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess();
}
