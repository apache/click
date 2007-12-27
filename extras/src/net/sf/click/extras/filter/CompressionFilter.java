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
import java.io.InputStream;
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

import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
 * This filter will automaitically set the configured click.xml charset as the
 * requests character encoding.
 * <p/>
 * This package is derived from the Jakarta
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>
 * examples compression filter and is distributed in Click Extras for convenience.
 *
 * @author Amy Roh
 * @author Dmitri Valdin
 * @author Malcolm Edgar
 */
public class CompressionFilter implements Filter {

    /**
     * The default Click configuration filename: &nbsp;
     * "<tt>/WEB-INF/click.xml</tt>".
     */
    static final String DEFAULT_APP_CONFIG = "/WEB-INF/click.xml";

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    private FilterConfig config = null;

    /** Minimal reasonable threshold, 2048 bytes. */
    protected int minThreshold = 2048;

    /** The threshold number to compress, default value is 2048 bytes. */
    protected int compressionThreshold;

    /** The configured click application request encoding character set. */
    protected String charset;

    /**
     * Place this filter into service.
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {

        config = filterConfig;

        if (filterConfig != null) {

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

            charset = getCharset(filterConfig.getServletContext());

        } else {
            compressionThreshold = minThreshold;
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
     * (<code>chain.doFilter()</code>)
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (compressionThreshold == 0) {
            chain.doFilter(request, response);
            return;
        }

        boolean supportCompression = false;

        if (charset != null) {
            try {
                request.setCharacterEncoding(charset);

            } catch (UnsupportedEncodingException ex) {
                String msg =
                    "The character encoding " + charset + " is invalid.";
                ClickLogger.getInstance().warn(msg, ex);
            }
        }

        if (request instanceof HttpServletRequest) {

            // Are we allowed to compress ?
            String s = ((HttpServletRequest) request).getParameter("gzip");
            if ("false".equals(s)) {
                chain.doFilter(request, response);
                return;
            }

            Enumeration e =
                ((HttpServletRequest) request).getHeaders("Accept-Encoding");

            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                if (name.indexOf("gzip") != -1) {
                    supportCompression = true;
                }
            }
        }

        if (!supportCompression) {
            chain.doFilter(request, response);

        } else {
            if (response instanceof HttpServletResponse) {

                HttpServletResponse hsr = (HttpServletResponse) response;
                CompressionServletResponseWrapper wrappedResponse =
                    new CompressionServletResponseWrapper(hsr);

                wrappedResponse.setCompressionThreshold(compressionThreshold);

                try {
                    chain.doFilter(request, wrappedResponse);
                } finally {
                    wrappedResponse.finishResponse();
                }
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
        return config;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Return the configured click application character set.
     *
     * @param servletContext the servlet context
     * @return the configured click application character set
     */
    protected String getCharset(ServletContext servletContext) {
        InputStream inputStream =
            servletContext.getResourceAsStream(DEFAULT_APP_CONFIG);

        if (inputStream == null) {
            inputStream = ClickUtils.getResourceAsStream("/click.xml", getClass());
            if (inputStream == null) {
                String msg =
                    "could not find click app configuration file: "
                    + DEFAULT_APP_CONFIG + " or click.xml on classpath";
                throw new RuntimeException(msg);
            }
        }

        try {
            Document document = ClickUtils.buildDocument(inputStream);

            Element rootElm = document.getDocumentElement();

            String charset = rootElm.getAttribute("charset");

            if (charset != null && charset.length() > 0) {
                return charset;

            } else {
                return null;
            }

        } finally {
            ClickUtils.close(inputStream);
        }
    }

}

