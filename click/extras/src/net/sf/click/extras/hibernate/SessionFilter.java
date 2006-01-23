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
package net.sf.click.extras.hibernate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

/**
 * Provides a Hibernate session filter to support the SessionContext class,
 * closing sessions at the end of each request.
 * <p/>
 * To use {@link SessionContext} configure the SessionFilter in you
 * web application's <tt>/WEB-INF/web.xml</tt> file.
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *   &lt;filter&gt;
 *     &lt;filter-name&gt;<span class="blue">session-filter</span>&lt;/filter-name&gt;
 *     &lt;filter-class&gt;<span class="red">net.sf.click.extras.hibernate.SessionFilter</span>&lt;/filter-class&gt;
 *   &lt;/filter&gt;
 *
 *   &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;<span class="blue">session-filter</span>&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 *   &lt;/filter-mapping&gt;
 *
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 *   ..
 * &lt;/web-app&gt; </pre>
 * <p/>
 * The SessionFilter <tt>init()</tt> method loads the SessionContext class
 * which in turn initializes the Hibernate runtime.
 *
 * @see SessionContext
 * @see HibernateForm
 *
 * @author Malcolm Edgar
 */
public class SessionFilter implements Filter {

    /**
     * Initialize the Hibernate Configuration and SessionFactory.
     *
     * @see Filter#init(FilterConfig)
     *
     * @param filterConfig the filter configuration
     * @throws ServletException if an initialization error occurs
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // Load the SessionContext class initializing the SessionFactory
        try {
            SessionContext.class.getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * Close any user defined sessions if present.
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        chain.doFilter(request, response);

        if (SessionContext.hasSession()) {
            SessionContext.close();
        }
    }

}
