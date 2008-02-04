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

import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides a filter for improving the performance of web applications by
 * setting Expires header on static resources and by compressing the HTTP
 * response.
 * <p/>
 * Please see Yahoo's <a href="http://developer.yahoo.com/performance/rules.html">Exceptional Performance</a>
 * best practices for speeding up your web site. This filter will enable you to
 * apply the rules:
 * <ul>
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
 * This filter will automatically add long expiry headers (5 years) to static Click
 * resources such as CSS style sheets imports, JavaScript imports, and images.
 * This will ensure these resources are cached in the users browser and will not
 * have to be requested again.  With Click static resources are deployed automatically
 * on startup to the web directory <tt style="color:blue;">/click</tt>.
 * <p/>
 * When the PerformanceFilter is active Click will add a version number to the
 * static resource filenames and apply a long expiry header to these versioned files.
 * When you upgrade the the next version of Click Framework this version number
 * will increment, and the new static resources will be requested and cached by
 * the users browser.
 * <p/>
 * When the PerformanceFilter is not active Click not include a version number in the
 * static resource filenames and no expiry header will be applied..
 * <p/>
 * The filter will always compress non image static Click resources such as
 * style sheets and JavaScript imports.
 *
 * <h3>Configured Static Resources</h3>
 * You can also configure your own applications static resources such as CSS, JS
 * files and images to be processed by the filter.
 *
 * Provides a GZIP compression <tt>Filter</tt> to compress HTML ServletResponse
 * content. The content will only be compressed if it is bigger than a
 * configurable threshold. The default threshold is 384 bytes.
 * <p/>
 *
 * <h3>Click Pages</h3>
 * Click *.htm pages are automatically compressed by the filter.
 *
 * <h3>Configuration</h3>
 *
 * To configure your application to use the PerformanceFilter include the
 * click-extras.jar in your application and add the following filter elements to
 * your <tt>/WEB-INF/web.xml</tt> file:
 * <pre class="codeConfig">
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">net.sf.click.extras.filter.PerformanceFilter</span>&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;<font color="blue">cachable-paths</font>&lt;/param-name&gt;
 *   &lt;param-value&gt;<font color="red">/assets/*, *.css</font>&lt;/param-value&gt;
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
 *
 * <h3>Frequently Asked Questions</h3>
 *
 * <h4>How does compression work?</h4>
 * The response from non image (gif, jpg, png) content will be gzipped before
 * writing to the browser.  The browser will receive the gzipped content, unzip it,
 * and display the content in its original form.
 * <p/>
 * As the GZIP compresssion greatly reduces the size of HTML, CSS and JavaScript
 * content these resources are downloaded faster and displayed quicker in the
 * users browser.
 * <p/>
 * GZIP compression is only applied if the browser supports it, and if the size
 * of the content is greater than 384 bytes.
 *
 * <h4>How does caching work?</h4>
 * For an explanation of how browsers and caching work, you can read the
 * following
 * <a href="http://betterexplained.com/articles/how-to-optimize-your-site-with-http-caching/" action="_blank">article.</a>
 * <p/>
 * Only configured resources (see below) will have expiry headers added. The
 * browser will not contact the server until the specified expiry date. When the
 * resource expires, the browser will request a new copy from the server.
 *
 * <h4>Does PerformanceFilter work in development, debug or trace modes?</h4>
 * PerformanceFilter is only applied in <tt>production</tt> and <tt>profile</tt>
 * modes. In the development modes, this filter will simply pass through to
 * ClickServlet without adding expiry headers or compressing content.
 * <p/>
 * This ensures a smoother development experience. There is not need to worry
 * about server and browser resources getting out of sync. In development mode,
 * simply edit a javascript or style sheet and the browser will pick up the
 * latest version.
 *
 * <h3>Acknowledgements</h3>
 * This class is adapted from the Jakarta CompressionFilter from
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>.
 * <p/>
 *
 * @author Malcolm Edgar
 * @author Bob Schellink
 */
public class PerformanceFilter implements Filter {

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

    /** The application in production or profile mode flag. */
    protected boolean inProductionProfileMode = false;

    /** The filter logger. */
    protected final ClickLogger logger = new ClickLogger("PerformanceFilter");

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

            // Get the configured application character set if defined
            charset = getCharset(filterConfig.getServletContext());

            // Determine whether the appliation is in production or profile mode
            String modeValue = getApplicationMode(filterConfig.getServletContext());
            inProductionProfileMode = modeValue.startsWith("pro");

            String message = null;
            if (inProductionProfileMode) {
                message =
                    "initialized with: cachable-paths="
                    + filterConfig.getInitParameter("cachable-paths")
                    + " and cachable-max-age=" + cacheMaxAge;

            } else {
                message =
                    "initialized but not active in application mode: "
                    + modeValue;
            }

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

        // Don't apply the performance filter if application is not
        // "production" or "profile" mode.
        if (!inProductionProfileMode) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Enable resource versioning in Click
        request.setAttribute(ClickUtils.ENABLE_RESOURCE_VERSION, "true");

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
        final boolean isVersionedResourcePath = (realPath.length() != path.length());

        // Apply response compression
        if (useGzipCompression(request, path)) {

            CompressionServletResponseWrapper wrappedResponse =
                new CompressionServletResponseWrapper(response);

            wrappedResponse.setCompressionThreshold(compressionThreshold);

            try {
                // If a versioned resource path, forward request to real resource path
                if (isVersionedResourcePath) {
                    request.getRequestDispatcher(realPath).forward(request, wrappedResponse);

                // Else chain filter
                } else {
                    chain.doFilter(request, wrappedResponse);
                }

            } finally {
                wrappedResponse.finishResponse();
            }

        } else {
            // If a versioned resource path, forward request to real resource path
            if (isVersionedResourcePath) {
                request.getRequestDispatcher(realPath).forward(request, response);

            // Else chain filter
            } else {
                chain.doFilter(request, response);
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
     * Return the <tt>version indicator</tt> for the specified path.
     *
     * @param path the resource path
     * @return a version indicator for web resources
     */
    protected String getResourceVersionIndicator(String path) {
        return ClickUtils.RESOURCE_VERSION_INDICATOR;
    }

    /**
     * Removes the version indicator from the specified path.
     * <p/>
     * For example, given the path <tt>'/example/control-1.4.js'</tt>, where
     * <tt>'-1.4'</tt> is the <tt>version indicator</tt>, this method will
     * return <tt>'/example/control.js'</tt>.
     *
     * @see #getResourceVersionIndicator(String)
     *
     * @param path the resource path
     * @return path without the version indicator
     */
    protected String stripResourceVersionIndicator(String path) {
        int versionIndex =
            path.lastIndexOf(getResourceVersionIndicator(path));

        if (versionIndex >= 0) {
            String extension = path.substring(
                versionIndex + getResourceVersionIndicator(path).length());
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

        InputStream inputStream = ClickUtils.getClickConfig(servletContext);

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

        InputStream inputStream = ClickUtils.getClickConfig(servletContext);

        try {
            Document document = ClickUtils.buildDocument(inputStream);

            Element rootElm = document.getDocumentElement();

            Element modeElm = ClickUtils.getChild(rootElm, "mode");

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
     * @see #getResourceVersionIndicator(String)
     *
     * @param path the request path to test
     * @return true if the response should be cached forever
     */
    protected boolean useForeverCacheHeader(String path) {
        String versionIndicator = getResourceVersionIndicator(path);
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

}

