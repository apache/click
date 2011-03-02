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
package org.apache.click.extras.gae;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ognl.OgnlRuntime;

/**
 * Provides <a href="http://code.google.com/appengine/docs/java/overview.html" class="external" target="_blank">Google App Engine</a>
 * (GAE) support for Click applications. GAE is a free Java hosting service
 * provided by Google that allows you to quickly and easily make your Click
 * applications available online.
 *
 * <h3>Configuration</h3>
 *
 * To deploy Click applications to GAE, you need to set the
 * <tt>GoogleAppEngineListener</tt> listener in your <tt>web.xml</tt>:
 *
 * <pre class="codeHtml">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5"&gt;
 *
 *     &lt;listener&gt;
 *         &lt;listener-class&gt;<span class="red">org.apache.click.extras.gae.GoogleAppEngineListener</span>&lt;/listener-class&gt;
 *     &lt;/listener&gt;
 *
 *     &lt;servlet&gt;
 *         &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
 *         &lt;servlet-class&gt;org.apache.click.ClickServlet&lt;/servlet-class&gt;
 *         &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *     &lt;/servlet&gt;
 *     &lt;servlet-mapping&gt;
 *         &lt;servlet-name&gt;ClickServlet&lt;/servlet-name&gt;
 *         &lt;url-pattern&gt;*.htm&lt;/url-pattern&gt;
 *     &lt;/servlet-mapping&gt;
 * &lt;/web-app&gt; </pre>
 *
 * You also need to configure GAE to exclude <tt>*.htm</tt> files from being
 * served as static resources. Also you should enable <tt>http-session</tt>
 * support. You set these changes in the GAE file <tt>war/WEB-INF/appengine-web.xml</tt>:
 *
 * <pre class="codeHtml">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;appengine-web-app xmlns="http://appengine.google.com/ns/1.0"&gt;
 *     &lt;application>myapp&lt;/application&gt;
 *     &lt;version&gt;1&lt;/version&gt;
 *
 *       &lt;!-- Configure java.util.logging --&gt;
 *     &lt;system-properties&gt;
 *         &lt;property name="java.util.logging.config.file" value="WEB-INF/logging.properties"/&gt;
 *     &lt;/system-properties&gt;
 *
 *     &lt;!-- Enable HttpSession usage --&gt;
 *     &lt;<span class="blue">sessions-enabled</span>&gt;<span class="red">true</span>&lt;/<span class="blue">sessions-enabled</span>&gt;
 *
 *     &lt;!-- Exclude *.htm files from being served as static files by GAE,
 *             because the *.htm extension is mapped to ClickServlet. --&gt;
 *     &lt;static-files&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.htm</span>" /&gt;
 *     &lt;/static-files&gt;
 *
 * &lt;/appengine-web-app&gt; </pre>
 *
 * <h3>Performance Filter</h3>
 *
 * If you use Click's {@link org.apache.click.extras.filter.PerformanceFilter}
 * you should also exclude the following static files from GAE, so that
 * PerformanceFilter can set their <tt>expiry headers</tt>:
 * <tt>*.css</tt>, <tt>*.js</tt>, <tt>*.png</tt> and <tt>*.gif</tt>. For example:
 *
 * <pre class="codeHtml">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;
 * &lt;appengine-web-app xmlns="http://appengine.google.com/ns/1.0"&gt;
 *
 *     ...
 *
 *     &lt;!-- Exclude the following files from being served as static files by GAE,
 *             as they will be processed by Click's PerformanceFilter. --&gt;
 *     &lt;static-files&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.htm</span>" /&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.css</span>" /&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.js</span>" /&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.png</span>" /&gt;
 *         &lt;<span class="blue">exclude</span> path="<span class="red">**.gif</span>" /&gt;
 *     &lt;/static-files&gt;
 *
 * &lt;/appengine-web-app&gt; </pre>
 *
 * <h3>File Uploads</h3>
 *
 * GAE does not allow web application to write to files on disk. This poses a
 * problem for the {@link org.apache.click.control.FileField} control that
 * depends on <a class="external" target="_blank" href="http://commons.apache.org/fileupload/">Commons FileUpload</a>
 * which stores uploaded files on disk. To work around this limitation Click
 * provides the {@link MemoryFileUploadService} which stores uploaded files in
 * memory.
 * <p/>
 * Below is an example configuration of a {@link MemoryFileUploadService}:
 *
 * <pre class="prettyprint">
 * &lt;click-app charset="UTF-8"&gt;
 *     &lt;pages package="com.myapp.pages"/&gt;
 *     &lt;mode value="production"/&gt;
 *
 *     &lt;file-upload-service classname="org.apache.click.extras.gae.MemoryFileUploadService"&gt;
 *         &lt;!-- Set the total request maximum size to 10mb (10 x 1024 x 1024 = 10485760).
 *                 The default request upload size is unlimited. --&gt;
 *         &lt;property name="sizeMax" value="10485760"/&gt;
 *
 *         &lt;!-- Set the maximum individual file size to 2mb (2 x 1024 x 1024 = 2097152).
 *             The default file upload size is unlimited. --&gt;
 *         &lt;property name="fileSizeMax" value="2097152"/&gt;
 *    &lt;/file-upload-service&gt;
 * &lt;/click-app&gt; </pre>
 *
 * <h2>Limitations</h2>
 *
 * <h3>Page Automapping</h3>
 * GAE does not always adhere to the Servlet specification. One of the areas that affects
 * Click directly is the <a href="../../../../../../user-guide/html/ch05s02.html#application-automapping">automatic mapping</a>
 * of Page templates to page classes. GAE does not implement the ServletContext
 * method <tt>getResourcePaths("/")</tt>. Instead of returning the resources under
 * the web-app root, it returns an empty set. Click needs these resources to map
 * between page templates and classes, and since GAE does not return anything,
 * it isn't possible to perform the automapping.
 * <p/>
 * Fortunately GAE does work properly for resources under subfolders of the web-app root.
 * For example if the folders <span class="blue">/path</span> or <span class="blue">/paths</span>
 * exists under the web-app root, calling <tt>getResourcePaths("/path")</tt> or
 * <tt>getResourcePaths("/paths")</tt> will return the set of resources contained
 * under these folders.
 * <p/>
 * Taking advantage of the fact that GAE supports subfolders, Click
 * provides automapping support to GAE applications with a slight caveat: Page
 * templates <b>must</b> be placed under the folders <span class="blue">/path</span>
 * or <span class="blue">/paths</span> of the web-app root. Click explicitly
 * maps these two folders if it is running on GAE, other subfolders are not supported.
 * <p/>
 * <b>Please note:</b> manual mapping works as expected.
 * <p/>
 * Below is an automapping example for the folder <span class="blue">/page</span>
 * (note the page template <tt>index.htm</tt> is not placed under the folder
 * <tt>/page</tt>, and has to be mapped manually):
 *
 * <pre class="codeConfig">
 * /index.htm
 * <span class="blue">/page</span>/search.htm
 * <span class="blue">/page</span>/customer/customer-edit.htm
 * <span class="blue">/page</span>/customer/customer-search.htm </pre>
 *
 * The Page classes are placed under the <span class="blue">page</span> package:
 *
 * <pre class="codeConfig">
 * com.mycorp<span class="blue">.page</span>.IndexPage.java
 * com.mycorp<span class="blue">.page</span>.SearchPage.java
 * com.mycorp<span class="blue">.page</span>.customer.CustomerEditPage.java
 * com.mycorp<span class="blue">.page</span>.customer.CustomerSearchPage.java </pre>
 *
 * Lastly define the <tt>click.xml</tt> to automatically map page templates
 * and classes under the package <tt>com.mycorp</tt>:
 *
 * <pre class="prettyprint">
 * &lt;click-app&gt;
 *
 *   &lt;pages package="com.mycorp"&gt;
 *     &lt;page path="/index.htm" classname="page.IndexPage"/&gt;
 *   &lt;/pages&gt;
 *
 *   &lt;mode value="production"/&gt;
 *
 * &lt;/click-app&gt; </pre>
 *
 * <b>Please note:</b> automapping will work in a GAE <tt>development</tt> environment
 * but not when hosted on the server. GAE uses the Jetty server for local development
 * which properly implements <tt>getResourcePaths("/")</tt>.
 * <p/>
 * <b>Also note:</b> when running Click on GAE in development mode, it will appear
 * that automapping is working when it really isn't. This is because
 * Click uses a variety of ways to detect new page templates in development mode.
 * So even though automapping failed, Click still serves page requests because it
 * used an alternative way of looking up the template template. While these techniques
 * are useful for development modes it impacts performance and is not used
 * in production mode.
 *
 * <h3>Deployment limitation</h3>
 *
 * On application startup, Click automatically deploys all its JavaScript, CSS
 * and image resources to the "<tt>/click</tt>" folder in the root directory of
 * the webapp. Since GAE doesn't allow writing to disk, Click cannot
 * automatically deploy its resources.
 * <p/>
 * Please see the user-guide section,
 * <a href="../../../../../../user-guide/html/ch05s03.html#deploying-restricted-env">Deploying resources in a restricted environment</a>,
 * for various solutions.
 */
public class GoogleAppEngineListener implements ServletContextListener {

    /**
     * Creates a default GoogleAppEngineListener.
     */
    public GoogleAppEngineListener() {
    }

    /**
     * This method does nothing.
     *
     * @param servletContextEvent the event class for notifications about
     * changes to the servlet context
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    /**
     * Sets the Ognl Runtime SecurityManager to <tt>null</tt> so as not to
     * interfere with Google App Engine (GAE). GAE provides its own strict
     * SecurityManager which clashes with Ognl security checks.
     *
     * <pre class="prettyprint">
     * OgnlRuntime.setSecurityManager(null); </pre>
     *
     * @param servletContextEvent the event class for notifications about
     * changes to the servlet context
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        OgnlRuntime.setSecurityManager(null);
    }
}
