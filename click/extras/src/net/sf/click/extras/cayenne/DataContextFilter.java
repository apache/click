/*
 * Copyright 2004-2006 Malcolm A. Edgar
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

import net.sf.click.util.ClickLogger;

import org.apache.commons.lang.StringUtils;
import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.conf.ServletUtil;

/**
 * Provides a servlet filter which binds the user's session scope DataContext to the
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
 * <h3>Cayenne Shared Cache</h3>
 *
 * By default DataContext objects will be created which use the Cayenne shared
 * cache. This is a good option for web applications which have exclusive
 * access to the underlying database. However when web applications share a
 * database you should probably disable this option by setting the
 * <tt>use-shared-cache</tt> init parameter to false.
 *
 * <h3>Configuration Example</h3>
 *
 * An example data context filter configuration in the web application's
 * <tt>/WEB-INF/web.xml</tt> file is provided below. This example does not
 * use the Cayenne shared cache when creating DataContext objects.
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">net.sf.click.extras.cayenne.DataContextFilter</span>&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;<font color="blue">use-shared-cache</font>&lt;/param-name&gt;
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
 *   ..
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

    /** Create DataContext objects using the shared cache. */
    protected boolean useSharedCache;

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

        String value = config.getInitParameter("use-shared-cache");
        if (StringUtils.isNotBlank(value)) {
            useSharedCache = "true".equalsIgnoreCase(value);
        }
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
        DataContext dataContext = getSessionContext(session);

        if (dataContext == null) {
            throw new RuntimeException("dataContex could not be obtained");
        }

        // Bind DataContext to the request thread
        DataContext.bindThreadDataContext(dataContext);

        try {
            chain.doFilter(request, response);

        } finally {
            logUncommittedChanges(dataContext);

            dataContext.rollbackChanges();

            DataContext.bindThreadDataContext(null);
        }
    }

    /**
     * Returns default Cayenne DataContext associated with the HttpSession,
     * creating it on the fly and storing in the session if needed.
     * <p/>
     * This method will create DataContext objects using the Cayenne shared cache
     * unless the filter init param <tt>use-shared-cache</tt> is set to false.
     *
     * @param session the users session
     * @return the session DataContext object
     */
    protected synchronized DataContext getSessionContext(HttpSession session) {
        DataContext context =
            (DataContext) session.getAttribute(ServletUtil.DATA_CONTEXT_KEY);

        if (context == null) {
            context = DataContext.createDataContext(useSharedCache);

            session.setAttribute(ServletUtil.DATA_CONTEXT_KEY, context);
        }

        return context;
    }

    /**
     * Log modified and uncommitted data objects in the given data context.
     *
     * @param dataContext the data context to log uncommitted objects
     */
    protected void logUncommittedChanges(DataContext dataContext) {
        if (!dataContext.hasChanges()) {
            return;
        }

        ClickLogger logger = ClickLogger.getInstance();
        if (logger.isDebugEnabled()) {
            logger.debug("Uncommitted data objects:");

            Collection uncommitted = dataContext.uncommittedObjects();
            for (Iterator i = uncommitted.iterator(); i.hasNext();) {
                logger.debug("   " + i.next());
            }
        }
    }

}
