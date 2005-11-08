/*
* Copyright 2004 The Apache Software Foundation
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
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Provides a GZIP compression <tt>Filter</tt> to compress HTML ServletResponse
 * content. The content will only be compressed if it is bigger than a
 * configurable threshold.
 * <p/>
 * To configure your application to GZIP compress HTML content include the
 * click-extras.jar in you application and add the following filter elements to
 * your <tt>/WEB-INF/web.xml</tt> file:
 * <pre class="codeConfig"/>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">compression-filter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">net.sf.click.extras.filter.CompressionFilter</span>&lt;/filter-class&gt;
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
 * This package is derived from the Jakarta
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>
 * examples compression filter and is distributed in Click Extras for convenience.
 *
 * @author Amy Roh
 * @author Dmitri Valdin
 * @version Revision: 1.2, Date: 2004/03/18 16:40:28
 */
public class CompressionFilter implements Filter {

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig config = null;

    /**
     * Minimal reasonable threshold
     */
    private int minThreshold = 128;

    /**
     * The threshold number to compress
     */
    protected int compressionThreshold;

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {

        config = filterConfig;
        if (filterConfig != null) {

            String str = filterConfig.getInitParameter("compressionThreshold");
            if (str!=null) {
                compressionThreshold = Integer.parseInt(str);
                if (compressionThreshold != 0 && compressionThreshold < minThreshold) {
                    compressionThreshold = minThreshold;
                }
            } else {
                compressionThreshold = 0;
            }

        } else {
            compressionThreshold = 0;
        }

    }

    /**
     * Take this filter out of service.
     */
    public void destroy() {
        this.config = null;
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
     * (<code>chain.doFilter()</code>), <br>
     **/

    public void doFilter ( ServletRequest request, ServletResponse response,
                        FilterChain chain ) throws IOException, ServletException {


        if (compressionThreshold == 0) {
            chain.doFilter(request, response);
            return;
        }

        boolean supportCompression = false;
        if (request instanceof HttpServletRequest) {

            // Are we allowed to compress ?
            String s = (String) ((HttpServletRequest)request).getParameter("gzip");
            if ("false".equals(s)) {
                chain.doFilter(request, response);
                return;
            }

            Enumeration e =
                ((HttpServletRequest)request).getHeaders("Accept-Encoding");
            while (e.hasMoreElements()) {
                String name = (String)e.nextElement();
                if (name.indexOf("gzip") != -1) {
                    supportCompression = true;
                }
            }
        }

        if (!supportCompression) {
            chain.doFilter(request, response);
            return;
        } else {
            if (response instanceof HttpServletResponse) {
                CompressionServletResponseWrapper wrappedResponse =
                    new CompressionServletResponseWrapper((HttpServletResponse)response);

                wrappedResponse.setCompressionThreshold(compressionThreshold);

                try {
                    chain.doFilter(request, wrappedResponse);
                } finally {
                    wrappedResponse.finishResponse();
                }
                return;
            }
        }
    }

    /**
     * Set filter config
     * This function is equivalent to init. Required by Weblogic 6.1
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        init(filterConfig);
    }

    /**
     * Return filter config
     * Required by Weblogic 6.1
     */
    public FilterConfig getFilterConfig() {
        return config;
    }

}

