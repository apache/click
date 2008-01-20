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
 * <h3>How does compression work?</h3>
 * Compression only works if the browser supports gzip and the resource content
 * is bigger than a  certain threshold (see below).
 * <p/>
 * If compression can be applied the response output of non image resources
 * handled by the filter will be gzipped before output to the browser.
 * <p/>
 * The browser will receive the gzipped resources, unzip it, and
 * display in its original form again.
 *
 * <h3>How does caching work?</h3>
 * First off, for a good explanation of how browsers and caching work read the
 * following
 * <a href="http://betterexplained.com/articles/how-to-optimize-your-site-with-http-caching/" action="_blank">article.</a>
 * <p/>
 * Only configured resource (see below) will have expiry headers added. Once an
 * expiry header is added for a resource, the browser will not contact the
 * server again to check if the resource content has changed. Only when the
 * expiry date is reached will the browser ask the server for the new resource.
 * <p/>
 * The downside is that even if the resource content is changed the browser will
 * not receive the updated version.
 * <p/>
 * This is where <tt>version indicators</tt> comes in.
 *
 * <h3>How does version indicators work?</h3>
 * A <tt>version indicator</tt> is a string that is added to the resource name.
 * If the resource content changed, the <tt>version indicator</tt> must also
 * change (preferably incremented). The browser will then download the new
 * resource and cache it under its new name.
 * <p/>
 * <b>Please note:</b> <tt>version indicators</tt> are only generated and used
 * at runtime. The physical resource (file) is never affected.
 * <p/>
 * Lets look at an example. Say we have a javascript file called
 * '/click-examples/click/control.js'. Below is how this resource will
 * be rendered when this filter is <b>not</b> applied:
 *
 * <pre class="codeHtml">
 * &lt;script type=<span class="st">"text/javascript"</span> src=<span class="st">"/click-examples/click/control.js"</span>&gt;&lt;/script&gt;
 * </pre>
 *
 * and here is how '/click-examples/click/control.js' will be rendered when the
 * filter <b>is</b> applied:
 *
 * <pre class="codeHtml">
 * &lt;script type=<span class="st">"text/javascript"</span> src=<span class="st">"/click-examples/click/control<span class="red">-1.4</span>.js"</span>&gt;&lt;/script&gt;
 * </pre>
 *
 * Notice the <tt>version indicator</tt> <tt>'-1.4'</tt> was added to the
 * resource path. When the resource content is changed the
 * <tt>version indicator</tt> needs to change as well:
 *
 * <pre class="codeHtml">
 * &lt;script type=<span class="st">"text/javascript"</span> src=<span class="st">"/click-examples/click/control<span class="red">-1.5</span>.js"</span>&gt;&lt;/script&gt;
 * </pre>
 *
 * Since the real physical resource does not have a <tt>version indicator</tt>,
 * it must be stripped from the path before this filter continues down the
 * chain.
 * <p/>
 * In the example above the javascript file is located at
 * <tt>'/click-examples/click/control.js'</tt>. The <tt>version indicator</tt>
 * <tt>'-1.4'</tt> must be removed from the path, otherwise the server will try
 * and find a file called <tt>'/click-examples/click/control-1.4.js'</tt> which
 * does not exist.
 * <p/>
 * See the method {@link #stripResourceVersionIndicator(String)}.
 * <p/>
 * For custom controls you can provide alternative <tt>version indicators</tt>.
 * For example you might want to <tt>version</tt> based on your application's
 * version number or a resource's last modified date.
 * <p/>
 * Please see the following resources for more information:
 * <ul>
 *   <li>{@link net.sf.click.control.AbstractControl#getResourceVersionIndicator()}</li>
 *   <li>{@link net.sf.click.Control#getHtmlImports()}</li>
 *   <li>{@link net.sf.click.util.ClickUtils#createHtmlImport(String, String, Context)}</li>
 *   <li>{@link #getResourceVersionIndicator(String)}</li>
 *   <li>{@link #stripResourceVersionIndicator(String)}</li>
 * </ul>
 * <b>Remember</b> that <tt>version indicators</tt> are generated and used only
 * at runtime. So if you override
 * {@link net.sf.click.control.AbstractControl#getResourceVersionIndicator()}
 * and generate custom <tt>version indicators</tt>, you must also strip the
 * <tt>indicator</tt> from the resource path before the filter continues down
 * the chain. You do this by providing a custom implementation of this filter's
 * {@link #stripResourceVersionIndicator(String)}
 * and / or {@link #getResourceVersionIndicator(String)}.
 *
 * <h3>Can you provide an example of how version indicators work?</h3>
 *
 * Say the browser contacts the server to download a specific page for example
 * <tt>'/click-examples/click/home.htm'</tt>. The request is forwarded to
 * ClickServlet and the page and all its controls are rendered.
 * <p/>
 * When a {@link net.sf.click.Control} is rendered, its method
 * {@link net.sf.click.Control#getHtmlImports()} is called to generate paths
 * to external resources.
 * <p/>
 * If the application is running in <tt>production</tt> or <tt>profile</tt> mode
 * and the attribute {@link net.sf.click.ClickServlet#ENABLE_RESOURCE_VERSION}
 * is set to "true", {@link net.sf.click.Control#getHtmlImports()} will add a
 * <tt>version indicator</tt> to the resource path.
 * <p/>
 * Here is an example resource path that could be generated by
 * <tt>getHtmlImports()</tt>:
 *
 * <pre class="codeHtml">
 * &lt;script type=<span class="st">"text/javascript"</span> src=<span class="st">"/click-examples/click/control<span class="red">-1.4</span>.js"</span>&gt;&lt;/script&gt;
 * </pre>
 *
 * The generated page content is delivered back to the browser. The browser
 * notices the external file referenced in the <tt>&lt;script&gt;</tt> tag.
 * <p/>
 * If the browser does not have the specified resource in its cache, it must
 * be downloaded from the server. The browser opens a connection to the server
 * and asks for the resource named '/click-examples/click/control-1.4.js'.
 * <p/>
 * The browser's request is first processed by the PerformanceFilter. The
 * PerformanceFilter checks if it needs to process the request resource path.
 * If the resource path is configured to be processed, the filter adds an
 * expiry headers to the response so that the browser will cache the resource.
 * <p/>
 * Next the PerformanceFilter removes the <tt>version indicator</tt> from the
 * path and forwards the request to the real path
 * <tt>'/click-examples/click/control.js'</tt>.
 * <p/>
 * The server finds the resource and delivers the javascript file to the browser
 * along with the expiry headers.
 * <p/>
 * Next time the same external file is referenced, the browser will use its
 * cached copy of '/click-examples/click/control-1.4.js'.
 * <p/>
 * Now say the resource <tt>'control.js'</tt> content is changed and the
 * <tt>version indicator</tt> for the javascript file is incremented.
 * The resource path might now be:
 *
 * <pre class="codeHtml">
 * &lt;script type=<span class="st">"text/javascript"</span> src=<span class="st">"/click-examples/click/control<span class="red">-1.5</span>.js"</span>&gt;&lt;/script&gt;
 * </pre>
 *
 * When the browser again request the page <tt>'/click-examples/click/home.htm'</tt>,
 * it notices that the resource <tt>'/click-examples/click/control-1.5.js'</tt>
 * is not available in its cache.
 * <p/>
 * The browser will ask the server for the new resource and the cycle continues.
 * <p/>
 * PerformanceFilter will strip the <tt>version indicator</tt> <tt>'-1.5'</tt>
 * from the path and forward the request to the path
 * <tt>'/click-examples/click/control.js'</tt>, where the updated content will
 * be returned to the browser.
 *
 * <h3>When does it work?</h3>
 * PerformanceFilter only works in <tt>production</tt> and <tt>profile</tt>
 * modes. In development modes, this filter will simply pass through to the
 * ClickServlet without adding expiry headers or compressing.
 * <p/>
 * This ensures a smoother development experience by not having to worry about
 * server and browser resources being out of sync. In development mode, simply
 * edit a javascript or style sheet and the browser will pick up the latest
 * version.
 * <p/>
 * <b>NOTE:</b> <tt>versioned indicators</tt> are only generated by Click in
 * <tt>production</tt> and <tt>profile</tt> mode, <b>and</b> when the attribute
 * {@link net.sf.click.ClickServlet#ENABLE_RESOURCE_VERSION} is set to
 * <tt>"true"</tt>. PerformanceFilter will set this attribute automatically in
 * <tt>production</tt> and <tt>profile</tt> mode.
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
 * You can also configure your own applications static resources such as CSS, JS
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
 * click-extras.jar in your application and add the following filter elements to
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
     * "production", "profile", "development", "debug", "trace".
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

    /** The filter logger. */
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
     * Return the <tt>version indicator</tt> for the specified path.
     * <p/>
     * By default this method will delegate to
     * {@link net.sf.click.util.ClickUtils#getResourceVersionIndicator()}.
     * <p/>
     * You can override this method to provide an alternative implementation.
     * For example provide a <tt>version indicator</tt> based on your
     * <tt>application version</tt> or a resource's <tt>last modified date</tt>.
     *
     * @see net.sf.click.control.AbstractControl#getResourceVersionIndicator()
     * @see #stripResourceVersionIndicator(String)
     * @see #stripResourceVersionIndicator(String)
     *
     * @param path the resource path
     * @return a version indicator for web resources
     */
    protected String getResourceVersionIndicator(String path) {
        return ClickUtils.getResourceVersionIndicator();
    }

    /**
     * Removes the version indicator from the specified path.
     * <p/>
     * For example, given the path '/example/control-1.4.js, where '-1.4' is
     * the version indicator, this method will return '/example/control.js'.
     * <p/>
     * <b>Please note</b> a <tt>version indicator</tt> is generated and used
     * only at runtime, thus the physical resource name does not contain a
     * <tt>version indicator</tt>.
     * <p/>
     * This method is used to strip off the <tt>version indicator</tt> from the
     * path so that the path points to the actual physical resource.
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

