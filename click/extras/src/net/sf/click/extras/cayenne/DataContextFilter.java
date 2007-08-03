/*
 * Copyright 2004-2007 Malcolm A. Edgar
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
package net.sf.click.extras.cayenne;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.conf.ServletUtil;

/**
 * Provides a servlet filter which binds DataContext objects to the request.
 * This filter will automatically rollback any uncommitted changes at
 *
 * which binds the user's session scope DataContext to the
 * current thread. This filter will automatically rollback any uncommitted
 * changes at the end of each request.
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
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">net.sf.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
 *   &lt;/filter&gt;
 *
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 *   &lt;/filter-mapping&gt;
 *
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
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
 *
 * <pre class="codeConfig">
 *
 * &lt;web-app&gt;
 *
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">net.sf.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
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
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 *   &lt;/filter-mapping&gt;
 *
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 *     ..
 *
 * &lt;/web-app&gt; </pre>
 *
 * <h3>Examples</h3>
 *
 * Please see the Click Examples application for a demonstration of Cayenne integration.
 * <p/>
 * This class is adapted from the Cayenne
 * {@link org.objectstyle.cayenne.conf.WebApplicationContextFilter}.
 *
 * @author Malcolm Edgar
 */
public class DataContextFilter implements Filter {

    /**
     * Automatically rollback any changes to the DataContext at the end of
     * each request, the default value is true.
     * <p/>
     * This option is only useful for sessionScope DataObjects.
     */
    protected boolean autoRollback = true;

    /**
     * Maintain user HttpSession scope DataContext object, the default value is
     * true. If sessionScope is false then a new DataContext object will be
     * created for each request.
     */
    protected boolean sessionScope = true;

    /** Create DataContext objects using the shared cache. */
    protected boolean sharedCache = true;

    /** The DataContextFilter logger. */
    protected Logger logger = Logger.getLogger(getClass());

    /**
     * Initialize the shared Cayenne configuration. If the
     * <tt>use-shared-cache</tt> init parameter is defined
     *
     * @see Filter#init(FilterConfig)
     *
     * @param config the filter configuration
     * @throws ServletException if an initialization error occurs
     */
    public synchronized void init(FilterConfig config) throws ServletException {

        ServletUtil.initializeSharedConfiguration(config.getServletContext());

        String value = null;

        value = config.getInitParameter("auto-rollback");
        if (StringUtils.isNotBlank(value)) {
            autoRollback = "true".equalsIgnoreCase(value);
        }

        value = config.getInitParameter("session-scope");
        if (StringUtils.isNotBlank(value)) {
            sessionScope = "true".equalsIgnoreCase(value);
        }

        value = config.getInitParameter("shared-cache");
        if (StringUtils.isNotBlank(value)) {
            sharedCache = "true".equalsIgnoreCase(value);
        }

        String msg = "DataContextFilter initialized with: auto-rollback="
            + autoRollback + ", session-scope=" + sessionScope
            + ", shared-cache=" + sharedCache;

        logger.info(msg);
    }

    /**
     * Destroy the DataContextFilter.
     */
    public void destroy() {
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

        // Obtain the users DataContext from their session
        HttpSession session = ((HttpServletRequest) request).getSession(true);
        DataContext dataContext = getDataContext(session);

        if (dataContext == null) {
            throw new RuntimeException("dataContex could not be obtained");
        }

        // Bind DataContext to the request thread
        DataContext.bindThreadDataContext(dataContext);

        try {
            chain.doFilter(request, response);

        } finally {
            if (logger.isDebugEnabled() && dataContext.hasChanges()) {
                logger.debug("Uncommitted data objects:");

                Collection uncommitted = dataContext.uncommittedObjects();
                for (Iterator i = uncommitted.iterator(); i.hasNext();) {
                    logger.debug("   " + i.next());
                }
            }

            if (autoRollback) {
                dataContext.rollbackChanges();
            }

            DataContext.bindThreadDataContext(null);
        }
    }

    /**
     * Return a DataContext instance. If the DataContextFilter is configured
     * to associate the DataContext with the session (which is the default
     * behaviour), the DataContext will be bound to the users session. If
     * the DataContext is already available, the existing DataContext will be
     * used otherwise a new DataContex object will be created.
     * <p/>
     * If this filter is configured with <tt>create-each-request</tt> to be true
     * then a new DataContext will be created for each request and the DataContext
     * will not be bound to the session.
     * <p/>
     * If this filter is configured with <tt>use-shared-cache</tt> to be true
     * (which is the default behaviour) this method will create DataContext objects
     * using the Cayenne shared cache, otherwise they will not use the shared cache.
     *
     * @param session the users session
     * @return the session DataContext object
     */
    protected synchronized DataContext getDataContext(HttpSession session) {

        DataContext dataContext = null;

        if (sessionScope) {
            dataContext = (DataContext) session.getAttribute(ServletUtil.DATA_CONTEXT_KEY);
        }

        if (dataContext == null) {
            dataContext = DataContext.createDataContext(sharedCache);

            if (logger.isDebugEnabled()) {
                String msg = "created DataContex with shared-cache=" + sharedCache;
                logger.debug(msg);
            }

            if (sessionScope) {
                session.setAttribute(ServletUtil.DATA_CONTEXT_KEY, dataContext);
            }
        }

        return dataContext;
    }

}
