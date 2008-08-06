/*
 * Copyright 2008 Malcolm A. Edgar
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
package net.sf.click.extras.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import net.sf.click.service.ConfigService;
import net.sf.click.util.ClickUtils;

/**
 * Provides a Filter for serving static click resources.
 *
 * @author Malcolm Edgar
 */
public class ResourceFilter implements Filter {

    /** The application configuration service. */
    protected ConfigService configService;

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the filter.
     *
     * @see Filter#init(FilterConfig)
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Take this filter out of service.
     *
     * @see Filter#destroy()
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     *
     * @param servletRequest the servlet request
     * @param servletResponse the servlet response
     * @param filterChain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        if (configService == null) {
            ServletContext servletContext = getFilterConfig().getServletContext();
            configService = ClickUtils.getConfigService(servletContext);
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String path = ClickUtils.getResourcePath(request);

        byte[] resourceBytes = (byte[])
            configService.getResourcesDeployed().get(path);

        if (resourceBytes != null) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            response.setContentLength(resourceBytes.length);

            OutputStream outputStream = null;
            try {
                outputStream = servletResponse.getOutputStream();

                IOUtils.write(resourceBytes, outputStream);

            } finally {
                ClickUtils.close(outputStream);
            }

        } else {
            filterChain.doFilter(servletRequest, servletResponse);
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

}
