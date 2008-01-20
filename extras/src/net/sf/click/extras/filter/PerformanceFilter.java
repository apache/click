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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.ClickServlet;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a filter for improving the performance of web applications by
 * setting Expires header on static resources and by compressing the HTTP
 * response.
 * <p/>
 * Please see Yahoo's <a href="http://developer.yahoo.com/performance/rules.html">Exceptional Performance</a>
 * best practices for speeding up your web site. This filter will enable you to
 * apply rule:
 * <ul>
 * <li>Rule 1 - Make Fewer HTTP Requests</li>
 * <li>Rule 3 - Add an Expires Header</li>
 * <li>Rule 4 - Gzip Components</li>
 * </ul>
 *
 * The Click Framework can also help you with the other rules:
 * <ul>
 * <li>Rule 5 - Put Stylesheets at the Top, by using $cssImports at the
 * top of you page</li>
 * <li>Rule 6 - Put Scripts at the Bottom, by using $jsImports at the bottom
 * of your page</li>
 * </ul>
 *
 * <h3>Click Static Resources</h3>
 * This filter will automatically add long expiry headers to static Click
 * resources such as CSS style sheets imports, JavaScript imports, and images.
 * The filter will also compress non image static resources such as style sheets
 * and JavaScript imports.
 * <p/>
 * Clicks static resources are deployed automatically by the ClickServlet on
 * startup and include a Click version number in the filename, which is used to
 * identify Click resources.
 *
 * <h3>Configured Resources</h3>
 * Click *.htm pages are automatically compressed by the filter.
 * <p/>
 * You can also configure your own applications static resources such CSS, JS
 * files and images to be processed by the filter.
 *
 * Provides a GZIP compression <tt>Filter</tt> to compress HTML ServletResponse
 * content. The content will only be compressed if it is bigger than a
 * configurable threshold. The default threshold is 384 bytes.
 * <p/>
 *
 * <h3>Configuration</h3>
 *
 * To configure your application to use the PerformanceFilter include the
 * click-extras.jar in you application and add the following filter elements to
 * your <tt>/WEB-INF/web.xml</tt> file:
 * <pre class="codeConfig">
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">net.sf.click.extras.filter.PerformanceFilter</span>&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;<font color="blue">cachable-paths</font>&lt;/param-name&gt;
 *   &lt;param-value&gt;<font color="red">/images/*, *.css</font>&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;<span class="green">*.css</span>&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;<span class="green">*.js</span>&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;<span class="green">*.gif</span>&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;<span class="green">*.png</span>&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;servlet&gt;
 *  &lt;servlet-name&gt;<span class="green">ClickServlet</span>&lt;/servlet-name&gt;
 * .. </pre>
 *
 * This filter will automaitically set the configured click.xml charset as the
 * requests character encoding.
 * <p/>
 * This class is derived from the Jakarta CompressionFilter from
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>.
 *
 * @author Malcolm Edgar
 * @author Amy Roh
 * @author Dmitri Valdin
 */
public class PerformanceFilter implements Filter {

    // ---------------------------------------------- Private Constants

    /**
     * The default Click configuration filename: &nbsp;
     * "<tt>/WEB-INF/click.xml</tt>".
     */
    private static final String DEFAULT_APP_CONFIG = "/WEB-INF/click.xml";

    // ---------------------------------------------- Protected Constants

    /** Default cache max-age in seconds (1 year): 31536000. */
    protected static final int DEFAULT_CACHE_MAX_AGE = 31536000;

    /** Forever cache max-age in seconds (5 years). */
    protected static final int FOREVER_CACHE_MAX_AGE = DEFAULT_CACHE_MAX_AGE * 5;

    /** Minimum compress threshold: 384 bytes. */
    protected static final int MIN_COMPRESSION_THRESHOLD = 384;

    // ----------------------------------------------------- Instance Variables

    /** The configured cache max age in seconds, default value is 1 year. */
    protected long cacheMaxAge = DEFAULT_CACHE_MAX_AGE;

    /** The configured click application request encoding character set. */
    protected String charset;

    /**
     * The application mode value:
     * "production", "profile", "development", "debug", "trace"
     */
    protected String modeValue;

    /** The threshold number to compress, default value is 384 bytes. */
    protected int compressionThreshold = MIN_COMPRESSION_THRESHOLD;

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    /** The cachable-path include directories. */
    protected List includeDirs = new ArrayList();

    /** The cachable-path include files. */
    protected List includeFiles = new ArrayList();

    protected ClickLogger logger = new ClickLogger("PerformanceFilter");

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the filter.
     *
     * @see Filter#init(FilterConfig)
     *
     * @param filterConfig The filter configuration object
     */
    public void init(FilterConfig filterConfig) {

        if (filterConfig != null) {

            this.filterConfig = filterConfig;

            // Get compression threshold
            String param = filterConfig.getInitParameter("compression-threshold");
            if (param != null) {
                compressionThreshold = Integer.parseInt(param);
                if (compressionThreshold != 0
                        && compressionThreshold < MIN_COMPRESSION_THRESHOLD) {

                    compressionThreshold = MIN_COMPRESSION_THRESHOLD;
                }
            }

            param = filterConfig.getInitParameter("cachable-paths");
            if (param != null) {
                String[] paths = StringUtils.split(param, ',');

                for (int i = 0; i  < paths.length; i++) {
                    String path = paths[i].trim();

                    if (path.endsWith("*")) {
                        includeDirs.add(path.substring(0, path.length() - 1));

                    } else if (path.startsWith("*")) {
                        includeFiles.add(path.substring(1));

                    } else {
                        String message = "cachable-path '" + path + "' ignored, "
                            + "path must start or end with a wildcard character: *";
                        logger.warn(message);
                    }
                }
            }

            // Get the cache max-age in seconds
            param = filterConfig.getInitParameter("cachable-max-age");
            if (param != null) {
                cacheMaxAge = Long.parseLong(param);
            }

            charset = getCharset(filterConfig.getServletContext());

            modeValue = getApplicationMode(filterConfig.getServletContext());

            String message =
                "initialized with: cachable-paths="
                + filterConfig.getInitParameter("cachable-paths")
                + " and cachable-max-age=" + cacheMaxAge;

            logger.info(message);
        }
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
     * Perform the filter operation applying any necessary Expire headers and
     * compressing the response content.
     *
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     *
     * @param servletRequest the servlet request
     * @param servletResponse the servlet response
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        // if modeValue is not in one of the production modes eg "production" or
        // "profile", do not apply the performance filter.
        if (modeValue.indexOf("pro") != 0) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // enable resource versioning in Click
        request.setAttribute(ClickServlet.ENABLE_RESOURCE_VERSION, "true");

        // Apply cache expiry Headers
        final String path = ClickUtils.getResourcePath(request);

        if (useForeverCacheHeader(path)) {
            setHeaderExpiresCache(response, FOREVER_CACHE_MAX_AGE);

        } else if (useConfiguredCacheHeader(path)) {
            setHeaderExpiresCache(response, cacheMaxAge);
        }

        // Set the character set
        if (charset != null) {
            try {
                request.setCharacterEncoding(charset);

            } catch (UnsupportedEncodingException ex) {
                String msg =
                    "The character encoding " + charset + " is invalid.";
                logger.warn(msg, ex);
            }
        }

        final String realPath = stripResourceVersionIndicator(path);

        // Apply response compression
        if (useGzipCompression(request, path)) {

            CompressionServletResponseWrapper wrappedResponse =
                new CompressionServletResponseWrapper(response);

            wrappedResponse.setCompressionThreshold(compressionThreshold);

            try {
                if (realPath.length() == path.length()) {
                    // path is not a versioned resource, continue normally
                    chain.doFilter(request, wrappedResponse);
                } else {
                    // path is a versioned resource, so forward to the real
                    // resource
                    request.getRequestDispatcher(realPath).forward(request,
                        wrappedResponse);
                }
            } finally {
                wrappedResponse.finishResponse();
            }

        } else {
            if (realPath.length() == path.length()) {
                // path is not a versioned resource, continue normally
                chain.doFilter(request, response);
            } else {
                // path is a versioned resource, so forward to the real resource
                request.getRequestDispatcher(realPath).forward(request,
                    response);
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
     * Removes the version indicator from the specified path.
     * <p/>
     * For example, given the path '/example/control-1.4.js, where '-1.4' is
     * the version indicator, this method will return '/example/control.js'.
     *
     * @param path the resource path
     * @return path without the version indicator
     */
    protected String stripResourceVersionIndicator(String path) {
        int versionIndex =
            path.lastIndexOf(ClickUtils.getResourceVersionIndicator());

        if (versionIndex >= 0) {
            String extension = path.substring(
                versionIndex + ClickUtils.getResourceVersionIndicator().length());
            return path.substring(0, versionIndex) + extension;
        }
        return path;
    }

    /**
     * Return the configured click application character set.
     *
     * @param servletContext the servlet context
     * @return the configured click application character set
     */
    protected String getCharset(ServletContext servletContext) {

        InputStream inputStream = getClickConfig(servletContext);

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

    /**
     * Return the configured click application mode.
     *
     * @param servletContext the servlet context
     * @return the configured click application mode
     */
    protected String getApplicationMode(ServletContext servletContext) {

        InputStream inputStream = getClickConfig(servletContext);

        try {
            Document document = ClickUtils.buildDocument(inputStream);

            Element rootElm = document.getDocumentElement();

            Element modeElm = getChild(rootElm, "mode");

            String tmpModeValue = "development";

            if (modeElm != null) {
                if (StringUtils.isNotBlank(modeElm.getAttribute("value"))) {
                     tmpModeValue = modeElm.getAttribute("value");
                }
            }

            tmpModeValue = System.getProperty("click.mode", tmpModeValue);

            return tmpModeValue;

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * Set the response "Expires" and "Cache-Control" headers with the given
     * maximum cache duration age in seconds.
     *
     * @param response the response to set the headers in
     * @param maxAgeSeconds the maximum cache duration in seconds
     */
    protected void setHeaderExpiresCache(HttpServletResponse response, long maxAgeSeconds) {
        long expiresMs = System.currentTimeMillis() + (maxAgeSeconds * 1000);
        response.setDateHeader("Expires", expiresMs);
        response.setHeader("Cache-Control", "max-age=" + maxAgeSeconds);
    }

    /**
     * Return true if a path is a static versioned Click resource and should be
     * cached forever.
     *
     * @param path the request path to test
     * @return true if the response should be cached forever
     */
    protected boolean useForeverCacheHeader(String path) {
        String versionIndicator = ClickUtils.getResourceVersionIndicator();
        return path.startsWith("/click/") && path.indexOf(versionIndicator) != -1;
    }

    /**
     * Return true if the response should be cached using the configure cache
     * max-age.
     *
     * @param path the request path to test
     * @return true if the response should be cached with the configured max-age
     */
    protected boolean useConfiguredCacheHeader(String path) {
        if (!includeFiles.isEmpty()) {
            for (int i = 0; i < includeFiles.size(); i++) {
                String file = includeFiles.get(i).toString();
                if (path.endsWith(file)) {
                    return true;
                }
            }
        }

        if (!includeDirs.isEmpty()) {
            for (int i = 0; i < includeDirs.size(); i++) {
                String dir = includeDirs.get(i).toString();
                if (path.startsWith(dir)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return true if the response should be GZIP compressed.
     *
     * @param request the request to test
     * @param path the request path to test
     * @return true if the response should be GZIP compressed
     */
    protected boolean useGzipCompression(HttpServletRequest request, String path) {

        if (compressionThreshold > 0) {
            if (path.endsWith(".gif") || path.endsWith(".png") || path.endsWith(".jpg")) {
                return false;
            }

            Enumeration e = request.getHeaders("Accept-Encoding");

            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                if (name.indexOf("gzip") != -1) {
                    return true;
                }
            }
        }

        return false;
    }

    // ------------------------------------------------------ Private Methods

    private InputStream getClickConfig(ServletContext servletContext) {
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

        return inputStream;
    }

     private Element getChild(Element element, String name) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                if (node.getNodeName().equals(name)) {
                    return (Element) node;
                }
            }
        }
        return null;
    }
}

