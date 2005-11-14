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
 * Provides a Hibernate Session filter ensuring any open sessions are closed
 * at the end of the request.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class SessionFilter implements Filter {

    /**
     * Initialize the Hibernate Configuration and SessionFactory.
     *
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig filterConfig) {
        // Load the SessionContext class initializing the SessionFactory
        try {
            SessionContext.class.getName();
        } catch (RuntimeException re) {
            re.printStackTrace();
            throw re;
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
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        chain.doFilter(request, response);

        if (SessionContext.hasSession()) {
            SessionContext.close();
        }
    }

}
