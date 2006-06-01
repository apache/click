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
 * For units of work spanning mutiple requests, such as a multi-page work flow,
 * it is recommended that you add a separate DataContext to the session
 * for the unit of work.
 *
 * <h4>Example</h4>
 *
 * An example data context filter configuration in the web application's
 * <tt>/WEB-INF/web.xml</tt> file is provided below
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">data-context-filter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">net.sf.click.extras.cayenne.DataContext</span>&lt;/filter-class&gt;
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

    /**
     * Initialize the shared Cayenne configuration.
     *
     * @see Filter#init(FilterConfig)
     *
     * @param config the filter configuration
     * @throws ServletException if an initialization error occurs
     */
    public synchronized void init(FilterConfig config) throws ServletException {
        ServletUtil.initializeSharedConfiguration(config.getServletContext());
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
        DataContext dataContext = ServletUtil.getSessionContext(session);

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
