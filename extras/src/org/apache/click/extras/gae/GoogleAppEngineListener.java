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
 * Provides Google App Engine (GAE) support for Click applications.
 * <p/>
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
 *         &lt;servlet-name&gt;ClickClickServlet&lt;/servlet-name&gt;
 *         &lt;servlet-class&gt;net.sf.clickclick.ClickClickServlet&lt;/servlet-class&gt;
 *         &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *     &lt;/servlet&gt;
 *     &lt;servlet-mapping&gt;
 *         &lt;servlet-name&gt;ClickClickServlet&lt;/servlet-name&gt;
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
 * <h3>Deployment issues</h3>
 *
 * On application startup, Click automatically deploys all its JavaScript and
 * CSS resources to the "<tt>/click</tt>" folder. Since GAE doesn't allow
 * writing to disk, Click cannot automatically deploy its resources.
 * <p/>
 * Instead you will have to manually add Click's resources in your GAE
 * application's "<tt>/war</tt>" folder. To do this create a <tt>click</tt>
 * folder under your <tt>/war</tt> directory -> "<tt>/war/click</tt>".
 * <p/>
 * Next you need to copy the resources from the click-core.X.X.X.jar and
 * click-extras.X.X.X.jar. Use your favorite IDE or ZIP utility to open the
 * jars and navigate to "<tt>META-INF/web/</tt>" where you will find the
 * "<tt>/click</tt>" folder with all the resources packaged for that jar. Simple
 * copy the content of the "<tt>/click</tt>" folder to your GAE folder:
 * "<tt>/war/click</tt>".
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
