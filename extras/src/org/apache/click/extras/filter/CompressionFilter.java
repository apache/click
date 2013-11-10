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
package org.apache.click.extras.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.service.ConfigService;
import org.apache.click.util.ClickUtils;

/**
 * Provides a GZIP compression <tt>Filter</tt> to compress HTML ServletResponse
 * content. The content will only be compressed if it is bigger than a
 * configurable threshold. The default threshold is 2048 bytes.
 * <p/>
 * To configure your application to GZIP compress HTML content include the
 * click-extras.jar in you application and add the following filter elements to
 * your <tt>/WEB-INF/web.xml</tt> file:
 * <pre class="codeConfig">
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">compression-filter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">org.apache.click.extras.filter.CompressionFilter</span>&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">compression-filter</span>&lt;/filter-name&gt;
 *  &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;servlet&gt;
 *  &lt;servlet-name&gt;<span class="green">click-servlet</span>&lt;/servlet-name&gt;
 * .. </pre>
 *
 * This filter will automatically set the configured click.xml charset as the
 * requests character encoding.
 * <p/>
 * This package is derived from the Jakarta
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>
 * examples compression filter and is distributed in Click Extras for convenience.
 */
public class CompressionFilter implements Filter {

    /** Minimal reasonable threshold, 2048 bytes. */
    protected int minThreshold = 2048;

    /** The threshold number to compress, default value is 2048 bytes. */
    protected int compressionThreshold = minThreshold;

    /** The filter has been configured flag. */
    protected boolean configured;

    /** The application configuration service. */
    protected ConfigService configService;

    /**
     * The filter configuration object we are associated with. If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig filterConfig;

    // --------------------------------------------------------- Public Methods

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Take this filter out of service.
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * The <code>doFilter</code> method of the Filter is called by the container
     * each time a request/response pair is passed through the chain due
     * to a client request for a resource at the end of the chain.
     * The FilterChain passed into this method allows the Filter to pass on the
     * request and response to the next entity in the chain.<p>
     * This method first examines the request to check whether the client support
     * compression. <br>
     * It simply just pass the request and response if there is no support for
     * compression.<br>
     * If the compression support is available, it creates a
     * CompressionServletResponseWrapper object which compresses the content and
     * modifies the header if the content length is big enough.
     * It then invokes the next entity in the chain using the FilterChain object
     * (<code>chain.doFilter()</code>)
     *
     * @param servletRequest the servlet request
     * @param servletResponse the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        if (!configured) {
            loadConfiguration();
        }

        if (compressionThreshold == 0) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        boolean supportCompression = false;

        String charset = getConfigService().getCharset();
        if (charset != null) {
            try {
                servletRequest.setCharacterEncoding(charset);

            } catch (UnsupportedEncodingException ex) {
                String msg =
                    "The character encoding " + charset + " is invalid.";
                getConfigService().getLogService().warn(msg, ex);
            }
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String path = ClickUtils.getResourcePath(request);
        supportCompression = useGzipCompression(request, response, path);

        if (!supportCompression) {
            chain.doFilter(request, response);

        } else {

            CompressionServletResponseWrapper wrappedResponse =
                new CompressionServletResponseWrapper(response, request);

            wrappedResponse.setCompressionThreshold(compressionThreshold);

            try {
                chain.doFilter(request, wrappedResponse);
            } finally {
                wrappedResponse.finishResponse();
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
     * Return true if the response should be GZIP compressed.
     *
     * @param request the request to test
     * @param response the response to test
     * @param path the request path to test
     * @return true if the response should be GZIP compressed
     */
    protected boolean useGzipCompression(HttpServletRequest request,
        HttpServletResponse response, String path) {

        // If Content-Encoding header is already set on response, skip compression
        if (response.containsHeader("Content-Encoding")) {
            return false;
        }

        // Are we allowed to compress ?
        String s = request.getParameter("gzip");
        if ("false".equals(s)) {
            return false;
        }

        if (compressionThreshold > 0) {

            path = path.toLowerCase();

            if (path.endsWith(".gif") || path.endsWith(".png") || path.endsWith(".jpg")) {
                return false;
            }

            Enumeration<?> e = request.getHeaders("Accept-Encoding");

            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                if (name.contains("gzip")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return the application configuration service.
     *
     * @return the application configuration service
     */
    protected ConfigService getConfigService() {
        return configService;
    }

    /**
     * Load the filters configuration and set the configured flat to true.
     */
    protected void loadConfiguration() {

        ServletContext servletContext = getFilterConfig().getServletContext();
        configService = ClickUtils.getConfigService(servletContext);

        String str = filterConfig.getInitParameter("compressionThreshold");
        if (str != null) {
            compressionThreshold = Integer.parseInt(str);
            if (compressionThreshold != 0
                && compressionThreshold < minThreshold) {

                compressionThreshold = minThreshold;
            }
        } else {
            compressionThreshold = minThreshold;
        }
    }

}

