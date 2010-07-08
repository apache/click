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

import org.apache.click.service.ConfigService;
import org.apache.click.util.ClickUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Provides a filter for improving the performance of web applications by
 * setting Expires header on static resources and by compressing the HTTP
 * response.
 * <p/>
 * Please see Yahoo's <a href="http://developer.yahoo.com/performance/rules.html">Exceptional Performance</a>
 * best practices for speeding up your web site. This filter will enable you to
 * apply the rules:
 * <ul>
 * <li><a class="external" target="_blank" href="http://developer.yahoo.com/performance/rules.html#expires">Add an Expires Header</a></li>
 * <li><a class="external" target="_blank" href="http://developer.yahoo.com/performance/rules.html#gzip">Gzip Components</a></li>
 * </ul>
 *
 * Apache Click can also help you with the following rules:
 * <ul>
 * <li><a class="external" target="_blank" href="http://developer.yahoo.com/performance/rules.html#css_top">Put Stylesheets at the Top</a>,
 * by using $headElements at the top of your page</li>
 * <li><a class="external" target="_blank" href="http://developer.yahoo.com/performance/rules.html#js_bottom">Put Scripts at the Bottom</a>,
 * by using $jsElements at the bottom of your page</li>
 * <li><a class="external" target="_blank" href="http://developer.yahoo.com/performance/rules.html#js-dupes">Remove Duplicate Scripts</a>
 * - {@link org.apache.click.Control#getHeadElements()} automatically
 * removes duplicate scripts.</li>
 * </ul>
 *
 * <h3>Click Static Resources</h3>
 * This filter will automatically add long expiry headers (5 years) to static Click
 * resources such as CSS style sheets imports, JavaScript imports, and images.
 * This will ensure these resources are cached in the users browser and will not
 * have to be requested again. With Click, static resources are automatically
 * deployed on startup to the web directory <tt style="color:blue;">/click</tt>.
 * <p/>
 * When the PerformanceFilter is active Click will add a <tt>version</tt> number
 * to the static resource filenames and the long expiry header will be applied to
 * these versioned files. When you upgrade the the next version of Click, this
 * version number will increment, and the new static resources will be requested
 * again and cached by the users browser.
 * <p/>
 * When the PerformanceFilter is not active Click will not include a version
 * number in the static resource filenames and no expiry header will be applied.
 * <p/>
 * The filter will always GZIP compress non image, static Click resources, such as
 * style sheets and JavaScript imports.
 *
 * <h3>Configured Static Resources</h3>
 * You can also configure your application's static resources such as CSS, JS
 * files and images to be processed by the filter.
 * <p/>
 * This filter will automatically add long expiry headers to configured
 * resources. The default expiry header is 1 year, but can be changed through
 * the <tt>init-param</tt> <span class="blue">"cacheable-max-age"</span>.
 * This ensures the resources are cached in the users browser and will not
 * have to be requested again.
 * <p/>
 * The PerformanceFilter provides the ability to add <tt>versioning</tt>
 * to application specific resources through the
 * <tt>init-param</tt> <span class="blue">"application-version"</span>. For example
 * to set the <span class="blue">"application-version"</span> to <span class="red">1.0</span>
 * you can define the filter as follows:
 *
 * <pre class="codeConfig">
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">org.apache.click.extras.filter.PerformanceFilter</span>&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;<font color="blue">application-version</font>&lt;/param-name&gt;
 *     &lt;param-value&gt;<font color="red">1.0</font>&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt; </pre>
 *
 * Application versioning is supported by {@link org.apache.click.element.ResourceElement resource elements}
 * such as {@link org.apache.click.element.JsImport JsImport} and
 * {@link org.apache.click.element.CssImport CssImport}. When the
 * <tt>application version</tt> is set, {@link org.apache.click.element.ResourceElement ResourceElements}
 * will add the <tt>application version</tt> number to their filenames
 * and PerformanceFilter will apply the long expiry header to these versioned files.
 * When you increment the <tt>application version</tt>, the resource path will
 * change and the static resources will be requested again and cached by the
 * browser.
 * <p/>
 * PerformanceFilter provides GZIP compression to compress HTML ServletResponse
 * content. The content will only be compressed if it is bigger than a
 * configurable threshold. The default threshold is 384 bytes but can be changed
 * through the <tt>init-param</tt> <span class="blue">"compression-threshold"</span>.
 * <p/>
 * Click *.htm pages are automatically compressed by the filter.
 * <p/>
 * It is also possible to disable GZIP compression by setting the
 * <tt>init-param</tt> <span class="blue">"compression-enabled"</span> to false.
 *
 * <h3>Page Template Import References</h3>
 *
 * To import static control references in your page template you simply reference
 * the <tt class="blue">$headElements</tt> and <tt class="blue">$jsElements</tt>.
 * For example:
 *
 * <pre class="codeHtml">
 * &lt;html&gt;
 * &lt;head&gt;
 * <span class="blue">$headElements</span>
 * &lt;/head&gt;
 * &lt;body&gt;
 * <span class="red">$table</span>
 * &lt;/body&gt;
 * &lt;/html&gt;
 * <span class="blue">$jsElements</span></pre>
 *
 * HEAD elements should be included in the head section of your page, and
 * JavaScript elements should be included at the bottom of your page to support
 * progressive rendering in the browser.
 *
 * <h3>Configuration</h3>
 *
 * To configure your application to use the PerformanceFilter include the
 * click-extras.jar in your application and add the following filter elements to
 * your <tt>/WEB-INF/web.xml</tt> file:
 * <pre class="codeConfig">
 * &lt;filter&gt;
 *  &lt;filter-name&gt;<span class="blue">PerformanceFilter</span>&lt;/filter-name&gt;
 *  &lt;filter-class&gt;<span class="red">org.apache.click.extras.filter.PerformanceFilter</span>&lt;/filter-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;<font color="blue">cacheable-paths</font>&lt;/param-name&gt;
 *     &lt;param-value&gt;<font color="red">/assets/*</font>, <font color="red">*.css</font>&lt;/param-value&gt;
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
 * The <tt>init-param</tt> <span class="blue">"cacheable-paths"</span>, allows
 * you to specify paths for resources such as JavaScript, CSS and images to be
 * <tt>cached</tt> by the browser. (Caching here means setting the
 * "Expires" and "Cache-Control" headers). The <tt>param-value</tt> accepts a
 * comma separated list of directories and files to match against.
 * To differentiate between directory and file values the following convention
 * is used:
 * <ul>
 * <li>To specify a directory, the value must <b>end</b> with the asterisk
 * character (*). When a resource is requested, the filter will only cache
 * the resource if the resource path starts with the specified value. For example
 * if the specified value is <tt>&lt;param-value&gt;<span class="red">/assets/*</span>&lt;/param-value&gt;</tt>,
 * the resource <tt>"<span class="red">/assets/</span>library.js"</tt> will be
 * cached while <tt>"/public/library.js"</tt> will not be.
 * </li>
 * <li>To specify a file, the value must <b>start</b> with the asterisk character
 * (*). When a resource is requested, the filter will only cache the resource
 * if the resource path ends with the specified value. For example if the specified
 * value is <tt>&lt;param-value&gt;<span class="red">*.css</span>&lt;/param-value&gt;</tt>,
 * the resource <tt>"/public/table<span class="red">.css</span>"</tt> will be
 * cached while <tt>"/public/table.js"</tt> will not be.
 * </li>
 * </ul>
 *
 * This filter will automatically set the configured click.xml charset as the
 * requests character encoding.
 *
 * <h3>Frequently Asked Questions</h3>
 *
 * <h4>How does compression work?</h4>
 * The response from non image (gif, jpg, png) content will be gzipped before
 * writing to the browser.  The browser will receive the gzipped content, unzip it,
 * and display the content in its original form.
 * <p/>
 * As the GZIP compression greatly reduces the size of HTML, CSS and JavaScript
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
 * <h3>Acknowledgments</h3>
 * This class is adapted from the Jakarta CompressionFilter from
 * <a href="http://jakarta.apache.org/tomcat">Tomcat</a>.
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

    /** The threshold number to compress, default value is 384 bytes. */
    protected int compressionThreshold = MIN_COMPRESSION_THRESHOLD;

    /** Indicates if compression is enabled or not, default value is true. */
    protected boolean compressionEnabled = true;

    /** The filter has been configured flag. */
    protected boolean configured;

    /** The application configuration service. */
    protected ConfigService configService;

    /**
     * The filter configuration object we are associated with.  If this value
     * is null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    /** The cacheable-path include directories. */
    protected List<String> includeDirs = new ArrayList<String>();

    /** The cacheable-path include files. */
    protected List<String> includeFiles = new ArrayList<String>();

    /** The cacheable-path exclude directories. */
    protected List<String> excludeDirs = new ArrayList<String>();

    /** The cacheable-path exclude files. */
    protected List<String> excludeFiles = new ArrayList<String>();

    /** The application resource version indicator. */
    protected String applicationVersionIndicator = "";

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

        if (!configured) {
            loadConfiguration();
        }

        if (!getConfigService().isProductionMode()
            && !getConfigService().isProfileMode()) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final String path = ClickUtils.getResourcePath(request);

        if (isExcludePath(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // Enable resource versioning in Click
        request.setAttribute(ClickUtils.ENABLE_RESOURCE_VERSION, "true");

        if (useForeverCacheHeader(path)) {
            setHeaderExpiresCache(response, FOREVER_CACHE_MAX_AGE);

        } else if (useConfiguredCacheHeader(path)) {
            setHeaderExpiresCache(response, cacheMaxAge);
        }

        // Set the character set
        String charset = getConfigService().getCharset();
        if (charset != null) {
            try {
                request.setCharacterEncoding(charset);

            } catch (UnsupportedEncodingException ex) {
                String msg =
                    "The character encoding " + charset + " is invalid.";
                getConfigService().getLogService().warn(msg, ex);
            }
        }

        final String realPath = stripResourceVersionIndicator(path);
        final boolean isVersionedResourcePath = (realPath.length() != path.length());

        // Apply response compression
        if (useGzipCompression(request, response, path)) {

            CompressionServletResponseWrapper wrappedResponse =
                new CompressionServletResponseWrapper(response, request);

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

        // Get gzip enabled parameter
        String param = filterConfig.getInitParameter("compression-enabled");
        if (StringUtils.isNotBlank(param)) {
            compressionEnabled = Boolean.parseBoolean(param);
        }

        // Get compression threshold
        param = filterConfig.getInitParameter("compression-threshold");
        if (param != null) {
            compressionThreshold = Integer.parseInt(param);
            if (compressionThreshold != 0
                    && compressionThreshold < MIN_COMPRESSION_THRESHOLD) {

                compressionThreshold = MIN_COMPRESSION_THRESHOLD;
            }
        }

        param = filterConfig.getInitParameter("application-version");
        if (StringUtils.isNotBlank(param)) {
            applicationVersionIndicator = ClickUtils.VERSION_INDICATOR_SEP
                + param;
            ClickUtils.setApplicationVersion(param);
        }

        param = filterConfig.getInitParameter("cacheable-paths");
        if (param != null) {
            String[] paths = StringUtils.split(param, ',');

            for (int i = 0; i  < paths.length; i++) {
                String path = paths[i].trim();

                if (path.endsWith("*")) {
                    includeDirs.add(path.substring(0, path.length() - 1));

                } else if (path.startsWith("*")) {
                    includeFiles.add(path.substring(1));

                } else {
                    String message = "cacheable-path '" + path + "' ignored, "
                        + "path must start or end with a wildcard character: *";
                    getConfigService().getLogService().warn(message);
                }
            }
        }

        // Fixed misspelling. Use cacheable-paths instead.
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
                    getConfigService().getLogService().warn(message);
                }
            }
        }

        param = filterConfig.getInitParameter("exclude-paths");
        if (param != null) {
            String[] paths = StringUtils.split(param, ',');

            for (int i = 0; i  < paths.length; i++) {
                String path = paths[i].trim();

                if (path.endsWith("*")) {
                    excludeDirs.add(path.substring(0, path.length() - 1));

                } else if (path.startsWith("*")) {
                    excludeFiles.add(path.substring(1));

                } else {
                    String message = "exclude-path '" + path + "' ignored, "
                    + "path must start or end with a wildcard character: *";
                    getConfigService().getLogService().warn(message);
                }
            }
        }

        // Get the cache max-age in seconds
        param = filterConfig.getInitParameter("cacheable-max-age");
        if (param != null) {
            cacheMaxAge = Long.parseLong(param);

        } else {
            // Fixed misspelling. Use cacheable-max-age instead.
            param = filterConfig.getInitParameter("cachable-max-age");
            if (param != null) {
                cacheMaxAge = Long.parseLong(param);
            }
        }

        String message =
            "PerformanceFilter initialized with: cacheable-paths="
            + filterConfig.getInitParameter("cacheable-paths")
            + " and cacheable-max-age=" + cacheMaxAge;

        getConfigService().getLogService().info(message);

        configured = true;
    }

    /**
     * Return true if a path should be excluded from the performance filter.
     *
     * @param path the request path to test
     * @return true if the response should be excluded from the performance filter
     */
    protected boolean isExcludePath(String path) {
        if (!excludeFiles.isEmpty()) {
            for (int i = 0; i < excludeFiles.size(); i++) {
                String file = excludeFiles.get(i).toString();
                if (path.endsWith(file)) {
                    return true;
                }
            }
        }

        if (!excludeDirs.isEmpty()) {
            for (int i = 0; i < excludeDirs.size(); i++) {
                String dir = excludeDirs.get(i).toString();
                if (path.startsWith(dir)) {
                    return true;
                }
            }
        }

        return false;
    }

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
     * Return the application <tt>version indicator</tt> for the specified path.
     *
     * @param path the resource path
     * @return an application version indicator for web resources
     */
    protected String getApplicationResourceVersionIndicator(String path) {
        String indicator = ClickUtils.getApplicationResourceVersionIndicator();

        if (StringUtils.isBlank(indicator)) {
            // NOTE: getApplicationResourceVersionIndicator will return an empty
            // string on the first request to this filter because the Context is
            // not available. Thus we default to the application version
            // indicator that may have been set on the filter.
            indicator = applicationVersionIndicator;
        }
        return indicator;
    }

    /**
     * Removes the version indicator from the specified path.
     * <p/>
     * For example, given the path <tt>'/example/control_1.4.js'</tt>, where
     * <tt>'_1.4'</tt> is the <tt>version indicator</tt>, this method will
     * return <tt>'/example/control.js'</tt>.
     *
     * @see #getResourceVersionIndicator(String)
     * @see #getApplicationResourceVersionIndicator(java.lang.String)
     *
     * @param path the resource path
     * @return path without the version indicator
     */
    protected String stripResourceVersionIndicator(String path) {
        String realPath = path;

        realPath = StringUtils.replace(realPath,
            getApplicationResourceVersionIndicator(path), "");

        realPath = StringUtils.replace(realPath,
            getResourceVersionIndicator(path), "");

        return realPath;
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
     * Return true if a path is a static versioned resource and should be
     * cached forever.
     *
     * @see #getResourceVersionIndicator(java.lang.String)
     * @see #getApplicationResourceVersionIndicator(java.lang.String)
     *
     * @param path the request path to test
     * @return true if the response should be cached forever
     */
    protected boolean useForeverCacheHeader(String path) {
        String versionIndicator = getResourceVersionIndicator(path);
        if (path.startsWith("/click/") && path.indexOf(versionIndicator) != -1) {
            return true;
        }
        versionIndicator = getApplicationResourceVersionIndicator(path);

        // Only apply application version if one is defined
        if (StringUtils.isNotBlank(versionIndicator)) {
            if (path.indexOf(versionIndicator) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the response should be cached using the configured cache
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
     * @param response the response to test
     * @param path the request path to test
     * @return true if the response should be GZIP compressed
     */
    protected boolean useGzipCompression(HttpServletRequest request,
        HttpServletResponse response, String path) {

        if (!compressionEnabled) {
            return false;
        }

        // If Content-Encoding header is already set on response, skip compression
        if (response.containsHeader("Content-Encoding")) {
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
                if (name.indexOf("gzip") != -1) {
                    return true;
                }
            }
        }

        return false;
    }

}

