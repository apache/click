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
package org.apache.click;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.click.util.HtmlStringBuffer;

/**
 * Provides the interface for Page controls. Controls are also referred to
 * as components or widgets.
 * <p/>
 * When a Page request event is processed Controls may perform server side event
 * processing through their {@link #onProcess()} method. Controls are generally
 * rendered in a Page by calling their <tt>toString()</tt> method.
 * <p/>
 * The Control execution sequence is illustrated below:
 * <p/>
 * <img src="control-post-sequence-diagram.png"/>
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
 * Please note multiple import lines should be separated by a <tt>'\n'</tt> char,
 * as the {@link org.apache.click.util.PageImports} will parse multiple import lines
 * on the <tt>'\n'</tt> char and ensure that imports are not included twice.
 *
 * <a name="on-deploy"></a>
 * <h4>Deploying Resources</h4>
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
 *     <span class="kw">public void</span> onDeploy(ServletContext servletContext) {
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
 * When the Click application starts up it will deploy any control elements
 * defined in the following files in sequential order:
 * <ul>
 *  <li><tt>/click-controls.xml</tt>
 *  <li><tt>/extras-controls.xml</tt>
 *  <li><tt>WEB-INF/click.xml</tt>
 * </ul>
 * <p/>
 * Click also supports an alternative deployment strategy which relies on
 * packaging resource (stylesheets, JavaScript, images etc.) following a
 * specific convention. See the section
 * <a href="../../../../configuration.html#deploying-custom-resources">Deploying Custom Resources</a>
 * for further details.
 *
 * <p/>
 * <b>Please note</b> {@link org.apache.click.control.AbstractControl} provides
 * a default implementation of the Control interface to make it easier for
 * developers to create their own controls.
 *
 * @see org.apache.click.util.PageImports
 *
 * @author Malcolm Edgar
 */
public interface Control extends Serializable {
 
    /**
     * The global control messages bundle name: &nbsp; <tt>click-control</tt>.
     */
    public static final String CONTROL_MESSAGES = "click-control";

    /**
     * Return the Page request Context of the Control.
     *
     * @deprecated getContext() is now obsolete on the Control interface,
     * but will still be available on AbstractControl:
     * {@link org.apache.click.control.AbstractControl#getContext()}
     *
     * @return the Page request Context
     */
    public Context getContext();

    /**
     * Return the HTML import string to be included in the page.
     * <p/>
     * Implement this method to specify JavaScript and CSS includes for the
     * page. For example:
     *
     * <pre class="codeJava">
     * <span class="kw">protected static final</span> String HTML_IMPORT =
     *     <span class="st">"&lt;script type=\"text/javascript\" src=\"{0}/click/custom.js\"&gt;&lt;/script&gt;"</span>;
     *
     * <span class="kw">public</span> String getHtmlImports() {
     *     <span class="kw">return</span> ClickUtils.createHtmlImport(HTML_IMPORTS, getResourceVersionIndicator(), getContext());
     * } </pre>
     *
     * <b>Note</b> multiple import lines should be separated by a
     * <tt>'\n'</tt> char, as the {@link org.apache.click.util.PageImports} will
     * parse multiple import lines on the <tt>'\n'</tt> char and ensure that
     * imports are not included twice.
     * <p/>
     * The order in which JS and CSS files are included will be preserved in the
     * page.
     * <p/>
     * <b>Also note:</b> a common problem when overriding getHtmlImports in
     * subclasses is forgetting to call <em>super.getHtmlImports</em>. Consider
     * carefully whether you should call <em>super.getHtmlImports</em> or not.
     *
     * @deprecated use the new {@link #getHeadElements()} instead
     *
     * @return the HTML includes statements for the control stylesheet and
     * JavaScript files, or null if no includes are available
     */
    public String getHtmlImports();

    /**
     * Return the list of {@link org.apache.click.element.Element HEAD elements}
     * to be included in the page. Example HEAD elements include
     * {@link org.apache.click.element.JsImport JavaScript imports},
     * {@link org.apache.click.element.JsScript inline JavasSript},
     * {@link org.apache.click.util.CssImport Css imports} and
     * {@link org.apache.click.util.Css inline Css}.
     * <p/>
     * Controls can include their own list of HEAD elements by implementing
     * this method.
     * <p/>
     * The recommended approach when implementing this method is to use
     * <tt>lazy loading</tt> to only add HEAD elements <tt>once</tt> and when
     * <tt>needed</tt>.
     * For example:
     *
     * <pre class="prettyprint">
     * public MyControl extends AbstractControl {
     *
     *     public List getHeadElements() {
     *         // Use lazy loading to ensure the JS is only added the
     *         // first time this method is called.
     *         if (headElements == null) {
     *             // Get the head elements from the super implementation
     *             headElements = super.getHeadElements();
     *
     *             // Include the control's external JavaScript resource
     *             JsImport jsImport = new JsImport("/mycorp/mycontrol/mycontrol.js");
     *             headElements.add(jsImport);
     *
     *             // Include the control's external Css resource
     *             CssImport cssImport = new CssImport("/mycorp/mycontrol/mycontrol.css");
     *             headElements.add(cssImport);
     *         }
     *         return headElements;
     *     }
     * } </pre>
     *
     * Alternatively one can add the HEAD elements in the Control's constructor:
     *
     * <pre class="prettyprint">
     * public MyControl extends AbstractControl {
     *
     *     public MyControl() {
     *         JsImport jsImport = new JsImport("/mycorp/mycontrol/mycontrol.js");
     *         getHeadElements().add(jsImport);
     *         CssImport cssImport = new CssImport("/mycorp/mycontrol/mycontrol.css");
     *         getHeadHeaders().add(cssImport);
     *     }
     * } </pre>
     *
     * One can also add HEAD elements from event handler methods such as
     * {@link #onInit()}, {@link #onProcess()}, {@link #onRender()}
     * etc. <b>Please note:</b> when adding HEAD elements to event handlers,
     * its possible that the control will be added to a
     * {@link Page#stateful Stateful} page, so you will need to set the HEAD
     * elements list to <tt>null</tt> in the Control's {@link #onDestroy()}
     * event handler, otherwise the HEAD elements list will continue to grow
     * with each request:
     *
     * <pre class="prettyprint">
     * public MyControl extends AbstractControl {
     *
     *     // Set HEAD elements in the onInit event handler
     *     public void onInit() {
     *         // Add HEAD elements
     *         JsImport jsImport = new JsImport("/mycorp/mycontrol/mycontrol.js");
     *         getHeadElements().add(jsImport);
     *         CssImport cssImport = new CssImport("/mycorp/mycontrol/mycontrol.css");
     *         getHeadElements().add(cssImport);
     *     }
     *
     *     public void onDestroy() {
     *         // Nullify the HEAD elements
     *         headElements = null;
     *     }
     * } </pre>
     *
     * The order in which JS and CSS files are included will be preserved in the
     * page.
     * <p/>
     * <b>Note:</b> this method must never return null. If no HEAD elements
     * are available this method must return an empty {@link java.util.List}.
     * <p/>
     * <b>Also note:</b> a common problem when overriding getHeadElements in
     * subclasses is forgetting to call <em>super.getHeadElements</em>. Consider
     * carefully whether you should call <em>super.getHeadElements</em> or not.
     *
     * @return the list of HEAD elements to be included in the page
     */
    public List getHeadElements();

    /**
     * Return HTML element identifier attribute "id" value.
     *
     * {@link org.apache.click.control.AbstractControl#getId()}
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
     *
     * @deprecated this method is now obsolete on the Control interface, but
     * will still be available on AbstractControl:
     * {@link org.apache.click.control.AbstractControl#setListener(java.lang.Object, java.lang.String)}
     */
    public void setListener(Object listener, String method);

    /**
     * Return the localized messages <tt>Map</tt> of the Control.
     *
     * @return the localized messages <tt>Map</tt> of the Control
     */
    public Map getMessages();

    /**
     * Return the name of the Control. Each control name must be unique in the
     * containing Page model or the containing Form.
     *
     * @return the name of the control
     */
    public String getName();
 
    /**
     * Set the name of the Control. Each control name must be unique in the
     * containing Page model or the parent container.
     * <p/>
     * <b>Please note:</b> changing the name of a Control after it has been
     * added to its parent container is undefined. Thus it is  best <b>not</b>
     * to change the name of a Control once its been set.
     *
     * @param name of the control
     * @throws IllegalArgumentException if the name is null
     */
    public void setName(String name);

    /**
     * Return the parent of the Control.
     *
     * @return the parent of the Control
     */
    public Object getParent();

    /**
     * Set the parent of the  Control.
     *
     * @param parent the parent of the Control
     */
    public void setParent(Object parent);

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
     * <b>Please note:</b> a common problem when overriding onDeploy in
     * subclasses is forgetting to call <em>super.onDeploy</em>. Consider
     * carefully whether you should call <em>super.onDeploy</em> or not.
     * <p/>
     * Click also supports an alternative deployment strategy which relies on
     * packaging resource (stylesheets, JavaScript, images etc.) following a
     * specific convention. See the section
     * <a href="../../../../configuration.html#deploying-custom-resources">Deploying Custom Resources</a>
     * for further details.
     *
     * @param servletContext the servlet context
     */
    public void onDeploy(ServletContext servletContext);

    /**
     * The on initialize event handler. Each control will be initialized
     * before its {@link #onProcess()} method is called.
     * <p/>
     * {@link org.apache.click.control.Container} implementations should recursively
     * invoke the onInit method on each of their child controls ensuring that
     * all controls receive this event.
     * <p/>
     * <b>Please note:</b> a common problem when overriding onInit in
     * subclasses is forgetting to call <em>super.onInit()</em>. Consider
     * carefully whether you should call <em>super.onInit()</em> or not,
     * especially for {@link org.apache.click.control.Container}s which by default
     * call <em>onInit</em> on all their child controls as well.
     */
    public void onInit();

    /**
     * The on process event handler. Each control will be processed when the
     * Page is requested.
     * <p/>
     * ClickServlet will process all Page controls in the order they were added
     * to the Page.
     * <p/>
     * {@link org.apache.click.control.Container} implementations should recursively
     * invoke the onProcess method on each of their child controls ensuring that
     * all controls receive this event. However when a control onProcess method
     * return false, no other controls onProcess method should be invoked.
     * <p/>
     * When a control is processed it should return true if the Page should
     * continue event processing, or false if no other controls should be
     * processed and the {@link Page#onGet()} or {@link Page#onPost()} methods
     * should not be invoked.
     * <p/>
     * <b>Please note:</b> a common problem when overriding onProcess in
     * subclasses is forgetting to call <em>super.onProcess()</em>. Consider
     * carefully whether you should call <em>super.onProcess()</em> or not,
     * especially for {@link org.apache.click.control.Container}s which by default
     * call <em>onProcess</em> on all their child controls as well.
     *
     * @return true to continue Page event processing or false otherwise
     */
    public boolean onProcess();

    /**
     * The on render event handler. This event handler is invoked prior to the
     * control being rendered, and is useful for providing pre rendering logic.
     * <p/>
     * The on render method is typically used to populate tables performing some
     * database intensive operation. By putting the intensive operations in the
     * on render method they will not be performed if the user navigates away
     * to a different page.
     * <p/>
     * {@link org.apache.click.control.Container} implementations should recursively
     * invoke the onRender method on each of their child controls ensuring that
     * all controls receive this event.
     * <p/>
     * <b>Please note:</b> a common problem when overriding onRender in
     * subclasses is forgetting to call <em>super.onRender()</em>. Consider
     * carefully whether you should call <em>super.onRender()</em> or not,
     * especially for {@link org.apache.click.control.Container}s which by default
     * call <em>onRender</em> on all their child controls as well.
     */
    public void onRender();

    /**
     * The on destroy request event handler. Control classes should use this
     * method to add any resource clean up code.
     * <p/>
     * This method is guaranteed to be called before the Page object reference
     * goes out of scope and is available for garbage collection.
     * <p/>
     * {@link org.apache.click.control.Container} implementations should recursively
     * invoke the onDestroy method on each of their child controls ensuring that
     * all controls receive this event.
     * <p/>
     * <b>Please note:</b> a common problem when overriding onDestroy in
     * subclasses is forgetting to call <em>super.onDestroy()</em>. Consider
     * carefully whether you should call <em>super.onDestroy()</em> or not,
     * especially for {@link org.apache.click.control.Container}s which by default
     * call <em>onDestroy</em> on all their child controls as well.
     */
    public void onDestroy();

    /**
     * Render the control's HTML representation to the specified buffer. The
     * control's {@link java.lang.Object#toString()} method should delegate the
     * rendering to the render method for improved performance.
     * <p/>
     * An example implementation:
     * <pre class="prettyprint">
     * public class Border extends AbstractContainer {
     *
     *     public String toString() {
     *         int estimatedSizeOfControl = 100;
     *         HtmlStringBuffer buffer = new HtmlStringBuffer(estimatedSizeOfControl);
     *         render(buffer);
     *         return buffer.toString();
     *     }
     *
     *     &#47;**
     *      * &#64;see Control#render(HtmlStringBuffer)
     *      *&#47;
     *     public void render(HtmlStringBuffer buffer) {
     *         buffer.elementStart("div");
     *         buffer.appendAttribute("name", getName());
     *         buffer.closeTag();
     *         buffer.append(getField());
     *         buffer.elementEnd("div");
     *     }
     * }
     * </pre>
     *
     * @param buffer the specified buffer to render the control's output to
     */
    public void render(HtmlStringBuffer buffer);
}
