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
package org.apache.click.extras.cayenne;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.LifecycleListener;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.cache.OSQueryCacheFactory;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.map.LifecycleEvent;
import org.apache.cayenne.reflect.LifecycleCallbackRegistry;
import org.apache.click.service.ConfigService;
import org.apache.click.service.LogService;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a servlet filter which binds DataContext objects to the current
 * request thread and optionally the users HttpSession. This filter will
 * automatically rollback any uncommitted changes at the end of each request.
 * <p/>
 * When the click application is in <tt>debug</tt> or <tt>trace</tt> mode this
 * filter will log any uncommitted data objects at the end of each request.
 * <p/>
 * For units of work spanning multiple requests, such as a multi-page work flow,
 * it is recommended that you add a separate DataContext to the session
 * for the unit of work.
 *
 * <h3>Session vs Request Scope</h3>
 *
 * By default DataContext objects will be associated with the users HttpSession
 * allowing objects to be cached in the users DataContext. Alternatively the
 * filter can be configured to create a new DataContext object for each request.
 * <p/>
 * Using session scope DataObjects is a good option for web applications which
 * have exclusive access to the underlying database. However when web applications
 * share a database you should probably disable this option by setting the
 * <tt>session-scope</tt> init parameter to false.
 *
 * <h3>Shared Cache</h3>
 *
 * By default DataContext objects will be created which use the Cayenne shared
 * cache. This is a good option for web applications which have exclusive
 * access to the underlying database.
 * <p/>
 * However when web applications which share a database you should probably
 * disable this option by setting the <tt>shared-cache</tt> init parameter to false.
 *
 *
 * <h3>OSCache Enabled</h3>
 *
 * This option enables you to specify whether
 * <a href="http://www.opensymphony.com/oscache/">OSCache</a> should be used
 * as the query cache for the DataDomain. By default OSCache is not enabled.
 * <p/>
 * OSCache enables you to significantly
 * increase the performance of your applications with in-memory query caching.
 * OSCache provides fine grain control over query caching, expiry and supports
 * clustered cache invalidation.
 * <p/>
 * See
 * Cayenne <a href="http://cayenne.apache.org/doc/query-result-caching.html">Query Result Caching</a>
 * for more details.
 *
 * <h3>Lifecycle Listener</h3>
 *
 * You can register a data domain
 * <a href="http://cayenne.apache.org/doc/api/org/apache/cayenne/LifecycleListener.html">LifecycleListener</a>
 * class to listen to persistent object lifecycle events. Please see the
 * Cayenne <a href="http://cayenne.apache.org/doc/lifecycle-callbacks.html">Lifecycle Callbacks</a>
 * documentation for more details.
 * <p/>
 * To configure a Lifecycle Listener simply specify the class name of the listener
 * class as a filter init parameter. For example:
 *
 * <pre class="codeConfig">
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">DataContextFilter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">org.apache.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;<font color="blue">lifecycle-listener</font>&lt;/param-name&gt;
 *       &lt;param-value&gt;<font color="red">com.mycorp.service.AuditListener</font>&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *   &lt;/filter&gt; </pre>
 *
 * <h3>Configuration Examples</h3>
 *
 * An example data context filter configuration in the web application's
 * <tt>/WEB-INF/web.xml</tt> file is provided below. This example stores the
 * DataContext in the users session and uses the Cayenne shared cache when
 * creating DataContext objects.
 * <p/>
 * This configuration is particularly useful when the web application is the
 * only application making changes to the database.
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">DataContextFilter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">org.apache.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
 *   &lt;/filter&gt;
 *
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;<span class="blue">DataContextFilter</span>&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 *   &lt;/filter-mapping&gt;
 *
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;org.apache.click.ClickServlet&lt;/servlet-class&gt;
 *     ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * An example data context filter configuration in the web application's
 * <tt>/WEB-INF/web.xml</tt> file is provided below. This example creates
 * a new DataContext object for each request and does <b>not</b> use the
 * Cayenne shared cache when creating DataContext objects.
 * <p/>
 * This configuration is useful when multiple applications are making changes to
 * the database.
 
 * <pre class="codeConfig">
 *
 * &lt;web-app&gt;
 *
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">DataContextFilter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">org.apache.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;<font color="blue">session-scope</font>&lt;/param-name&gt;
 *       &lt;param-value&gt;<font color="red">false</font>&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;<font color="blue">shared-cache</font>&lt;/param-name&gt;
 *       &lt;param-value&gt;<font color="red">false</font>&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *   &lt;/filter&gt;
 *
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;<span class="blue">DataContextFilter</span>&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 *   &lt;/filter-mapping&gt;
 *
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;org.apache.click.ClickServlet&lt;/servlet-class&gt;
 *     ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * <h3>Examples</h3>
 *
 * Please see the Click Examples application for a demonstration of Cayenne integration.
 * <p/>
 * This class is adapted from the Cayenne
 * {@link org.apache.cayenne.conf.WebApplicationContextFilter}.
 */
public class DataContextFilter implements Filter {

    /**
     * Automatically rollback any changes to the DataContext at the end of
     * each request, the default value is true.
     * <p/>
     * This option is only useful for sessionScope DataObjects.
     */
    protected boolean autoRollback = true;

    /** The Cayenne DataDomain. */
    protected DataDomain dataDomain;

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig;

    /**
     * Maintain user DataContext object in their HttpSession, the default value
     * is false. If sessionScope is false then a new DataContext object will be
     * created for each request.
     */
    protected boolean sessionScope = false;

    /** Create DataContext objects using the shared cache. */
    protected Boolean sharedCache;

    /** The Click log service. */
    protected LogService logger;

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the shared Cayenne configuration. If the
     * <tt>use-shared-cache</tt> init parameter is defined
     *
     * @see Filter#init(FilterConfig)
     *
     * @param config the filter configuration
     * @throws RuntimeException if an initialization error occurs
     */
    public synchronized void init(FilterConfig config) {

         HtmlStringBuffer buffer = new HtmlStringBuffer();
         buffer.append("DataContextFilter initialized: ");

        filterConfig = config;

        ServletUtil.initializeSharedConfiguration(config.getServletContext());

        dataDomain = Configuration.getSharedConfiguration().getDomain();

        String value = null;

        value = config.getInitParameter("auto-rollback");
        if (StringUtils.isNotBlank(value)) {
            autoRollback = "true".equalsIgnoreCase(value);
        }
        buffer.append(" auto-rollback=" + autoRollback);


        value = config.getInitParameter("session-scope");
        if (StringUtils.isNotBlank(value)) {
            sessionScope = "true".equalsIgnoreCase(value);
        }
        buffer.append(", session-scope=" + sessionScope);

        value = config.getInitParameter("shared-cache");
        if (StringUtils.isNotBlank(value)) {
            sharedCache = "true".equalsIgnoreCase(value);
        }
        buffer.append(", shared-cache=");
        buffer.append((sharedCache != null) ? sharedCache : "default");

        value = config.getInitParameter("oscache-enabled");
        boolean oscacheEnabled = "true".equalsIgnoreCase(value);
        if (oscacheEnabled) {
            dataDomain.setQueryCacheFactory(new OSQueryCacheFactory());
        }
        buffer.append(", oscache-enabled=" + oscacheEnabled);

        String classname = config.getInitParameter("lifecycle-listener");

        if (StringUtils.isNotEmpty(classname)) {
            try {
                @SuppressWarnings("unchecked")
                Class listenerClass = ClickUtils.classForName(classname);

                LifecycleCallbackRegistry registry =
                    dataDomain.getEntityResolver().getCallbackRegistry();

                LifecycleListener lifecycleListener = (LifecycleListener)
                    listenerClass.newInstance();

                if (registry.isEmpty(LifecycleEvent.POST_LOAD)) {
                    registry.addDefaultListener(lifecycleListener);
                    buffer.append(", lifecycle-listener=" + classname);

                } else {
                    String message =
                        "Could not get LifecycleCallbackRegistry from domain: "
                        + dataDomain.getName();
                    throw new RuntimeException(message);
                }

            } catch (Exception e) {
                String message =
                    "Could not configure LifecycleCallbackRegistry: " + classname;
                throw new RuntimeException(message, e);
            }
        }

        // Log init data, note LogService is not yet initialized
        getFilterConfig().getServletContext().log(buffer.toString());
    }

    /**
     * Destroy the DataContextFilter.
     */
    public void destroy() {
        Configuration.getSharedConfiguration().shutdown();
        this.filterConfig = null;
    }

    /**
     * This filter binds the session DataContext to the current thread, and
     * removes the DataContext from the thread once the chained request has
     * completed.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (logger == null) {
             ServletContext servletContext = getFilterConfig().getServletContext();
             ConfigService configService = ClickUtils.getConfigService(servletContext);
             logger = configService.getLogService();
        }

        // Obtain the users DataContext
        DataContext dataContext = getDataContext((HttpServletRequest) request);

        if (dataContext == null) {
            throw new RuntimeException("DataContext could not be obtained");
        }

        // Bind DataContext to the request thread
        BaseContext.bindThreadObjectContext(dataContext);

        try {
            chain.doFilter(request, response);

        } finally {
            BaseContext.bindThreadObjectContext(null);

            if (logger.isDebugEnabled() && dataContext.hasChanges()) {
                logger.debug("Uncommitted data objects:");

                for (Object uncommitted : dataContext.uncommittedObjects()) {
                    logger.debug("   " + uncommitted);
                }
            }

            if (autoRollback) {
                dataContext.rollbackChanges();
            }
        }
    }

    /**
     * Set filter configuration. This function is equivalent to init and is
     * required by Weblogic 6.1.
     *
     * @param filterConfig the filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        init(filterConfig);
    }

    /**
     * Return filter config. This is required by Weblogic 6.1
     *
     * @return the filter configuration
     */
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return a DataContext instance. If the DataContextFilter is configured
     * to associate the DataContext with the session (which is the default
     * behaviour), the DataContext will be bound to the users session. If
     * the DataContext is already available, the existing DataContext will be
     * used otherwise a new DataContext object will be created.
     * <p/>
     * If this filter is configured with <tt>create-each-request</tt> to be true
     * then a new DataContext will be created for each request and the DataContext
     * will not be bound to the session.
     *
     * @param request the page request
     * @return the DataContext object
     */
    protected DataContext getDataContext(HttpServletRequest request) {

        if (sessionScope) {
            HttpSession session = request.getSession(true);

            DataContext dataContext = (DataContext)
                session.getAttribute(ServletUtil.DATA_CONTEXT_KEY);

            if (dataContext == null) {
                synchronized (session) {
                    dataContext = createDataContext();

                    session.setAttribute(ServletUtil.DATA_CONTEXT_KEY, dataContext);
                }
            }

            return dataContext;

        } else {
            return createDataContext();
        }
    }

    /**
     * Return a new DataContext instance using a shared cache if the filter is
     * configured with <tt>use-shared-cache</tt>, otherwise the DataContext
     * will not use a shared cache.
     *
     * @return the DataContext object
     */
    protected DataContext createDataContext() {

        DataContext dataContext = null;
        if (sharedCache != null) {
            dataContext = dataDomain.createDataContext(sharedCache);

        } else {
            dataContext = dataDomain.createDataContext();
        }

        if (logger.isTraceEnabled()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("DataContext created with ");
            if (sessionScope) {
                buffer.append("session scope");
            } else {
                buffer.append("request scope");
            }
            if (sharedCache != null) {
                buffer.append(", and shared cache ");
                buffer.append(sharedCache);
            }
            buffer.append(".");
            logger.trace(buffer);
        }

        return dataContext;
    }

}
