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
package org.springframework.orm.cayenne;

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

import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.conf.BasicServletConfiguration;
import org.objectstyle.cayenne.conf.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Cayenne filter which binds the user's session DataContext to 
 * the current thread.
 * 
 * @author Malcolm Edgar
 */
public class DataContextFilter implements Filter {
    
    /** The Cayenne Configuration Spring bean name. */
    public static final String CAYENNE_CONFIGURATION_BEAN = "cayenneConfig";

    /** The Cayenne data domain. */
    protected DataDomain dataDomain;
    
    /**
     * Initialize the Hibernate Configuration and SessionFactory.
     *
     * @see Filter#init(FilterConfig)
     *
     * @param filterConfig the filter configuration
     * @throws ServletException if an initialization error occurs
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        
        ServletContext servletContext = filterConfig.getServletContext();
        
        ApplicationContext applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        if (applicationContext == null) {
            String msg = 
                "Error initializing DataContextFilter, Spring "
                + "applicationContext could not be loaded";
            servletContext.log(msg);
            throw new ServletException(msg);
        }
        
        if (applicationContext.containsBean(CAYENNE_CONFIGURATION_BEAN)) {
            Configuration configuration = (Configuration)
                applicationContext.getBean(CAYENNE_CONFIGURATION_BEAN);
            
            dataDomain = configuration.getDomain();
            
        } else {
            String msg = 
                "Error initializing DataContextFilter, Spring "
                + "cayenneConfig is not available in applicationContext";
            servletContext.log(msg);
            throw new ServletException(msg);            
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
        HttpSession session = ((HttpServletRequest) request).getSession();
        DataContext dataContext = (DataContext) 
            session.getAttribute(BasicServletConfiguration.DATA_CONTEXT_KEY);

        if (dataContext == null) {
            dataContext = dataDomain.createDataContext();
            session.setAttribute(BasicServletConfiguration.DATA_CONTEXT_KEY,
                                 dataContext);
        }

        // Bind DataContext to the request thread
        DataContext.bindThreadDataContext(dataContext);
        
        chain.doFilter(request, response);
        
        DataContext.bindThreadDataContext(null);
    }

}
