/*
 * Copyright 2004 Malcolm A. Edgar
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
package net.sf.click;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.GenericServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.util.ErrorPage;
import net.sf.click.util.SessionMap;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.util.SimplePool;

/**
 * Provides the Click application HttpServlet.
 * <p/>
 * Generally developers will simply configure the <tt>ClickServlet</tt> and
 * will not use it directly in their code. For a Click web application to
 * function the <tt>ClickServlet</tt> must be configured in the web
 * application's <tt>/WEB-INF/web.xml</tt> file. A simple web application which
 * maps all <tt>*.htm</tt> requests to a ClickServlet is provided below.
 * <blockquote><pre>
 * &lt;web-app>
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;<font color="blue">ClickServlet</font>&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;<font color="red">net.sf.click.ClickServlet</font>&lt;/servlet-class&gt;
 *       &lt;load-on-startup&gt;<font color="red">0</font>&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *    &lt;servlet-mapping&gt;
 *       &lt;servlet-name&gt;<font color="blue">ClickServlet</font>&lt;/servlet-name&gt;
 *       &lt;url-pattern&gt;<font color="red">*.htm</font>&lt;/url-pattern&gt;
 *    &lt;/servlet-mapping&gt;
 * &lt;/web-app&gt;
 * </pre></blockquote>
 * <p/>
 * By default the <tt>ClickServlet</tt> will attempt to load an application
 * configuration file using the path: &nbsp; <tt>/WEB-INF/click.xml</tt>
 *
 * <h4>Servlet Mapping</h4>
 * By convention all Click page templates should have a .htm extension, and
 * the ClickServlet should be mapped to process all *.htm URL requests. With
 * this convention you have all the static HTML pages use a .html extension
 * and they will not be processed as Click pages.
 *
 * <h4>Load On Startup</h4>
 * Note you should always set <tt>load-on-startup</tt> element to be 0 so the
 * servlet is initialized when the server is started. This will prevent any
 * delay for the first client which uses the application.
 * <p/>
 * The <tt>ClickServlet</tt> performs as much work as possible at startup to
 * improve performance later on. The Click start up and caching strategy is
 * configured with the Click application mode in the "<tt>click.xml</tt>" file.
 * See the User Guide for information on how to configure the application mode.
 * <p/>
 *
 * @author Malcolm Edgar
 */
public class ClickServlet extends HttpServlet {

    // ------------------------------------------------------ Instance Varables

    /** The servlet logger. */
    protected final Logger logger = Logger.getLogger(ClickServlet.class);

    /** The click application. */
    protected ClickApp clickApp;

    /** Cache of velocity writers */
    protected final SimplePool writerPool = new SimplePool(40);

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the Click servlet and the Velocity runtime.
     *
     * @see GenericServlet#init()
     */
    public void init() throws ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("init(): start for servlet '" + getServletName() + "'");
        }

        // Get the context path for identifying the webapp
        String path = getServletContext().getRealPath("/");
        if (path != null) {
            path = path.replace('\\', '/');
            int index = path.lastIndexOf('/', path.length() - 2);
            if (index != -1) {
                path = path.substring(index, path.length() - 1);
            }
        }

        try {
            // Initialize the click application.
            clickApp = new ClickApp(getServletContext());

            if (logger.isInfoEnabled()) {
                logger.info("initialized on context path '" + path +
                            "' in " + clickApp.getModeValue() + " mode");
            }

        } catch (Throwable e) {

            String message =
                "error initializing on context path '" + path +
                "' throwing javax.servlet.UnavailableException";

            logger.error(message, e);

            log(message, e);

            throw new UnavailableException(e.toString());
        }
    }

    /**
     * Handle HTTP GET requests. This method will delegate the request to
     * {@link #handleRequest(HttpServletRequest, HttpServletResponse, boolean)}.
     *
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     * @throws UnavailableException if click app has not been initialized
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // Ensure click app has been initialized
        if (clickApp == null) {
            throw new UnavailableException
                ("permanantly unavailable - click app has not been initialized");
        }

        handleRequest(request, response, false);
    }

    /**
     * Handle HTTP POST requests. This method will delegate the request to
     * {@link #handleRequest(HttpServletRequest, HttpServletResponse, boolean)}.
     *
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     * @throws UnavailableException if click app has not been initialized
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // Ensure click app has been initialized
        if (clickApp == null) {
            throw new UnavailableException
                ("permanantly unavailable - click app has not been initialized");
        }

        handleRequest(request, response, true);
    }

    /**
     * Handle the given servlet request and render the results to the
     * servlet response.
     * <p/>
     * If an exception occurs within this method the exception will be delegated
     * to: <p/>
     * {@link #handleException(HttpServletRequest, HttpServletResponse, boolean, Throwable, Page)}
     *
     * @param request the servlet request to process
     * @param response the servlet response to render the results to
     * @param isPost determines whether the request is a POST
     */
    protected void handleRequest(HttpServletRequest request,
        HttpServletResponse response, boolean isPost) {

        long startTime = 0;
        if (clickApp.getMode() != ClickApp.PRODUCTION) {
            startTime = System.currentTimeMillis();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("");
            logger.debug("method: " + request.getMethod());
            logger.debug("uri: " + request.getRequestURI());
            logger.debug("url: " + request.getRequestURL());
            logger.debug("query: " + request.getQueryString());
        }

        Page page = null;
        try {
            page = createPage(request, response, isPost);

            page.onInit();

            boolean continueProcessing = page.onSecurityCheck();

            if (continueProcessing && page.hasControls()) {
                // Make sure dont processed forwarded request
                if (!page.getContext().isForward()) {

                    List controls = page.getControls();

                    for (int i = 0, size = controls.size(); i < size; i++) {
                        Control control = (Control) controls.get(i);

                        continueProcessing = control.onProcess();

                        if (!continueProcessing) {
                            break;
                        }
                    }
                }
            }

            if (continueProcessing) {
                if (isPost) {
                    page.onPost();
                } else {
                    page.onGet();
                }
            }

            if (page.getRedirect() != null) {
                String url = response.encodeRedirectURL(page.getRedirect());

                if (logger.isDebugEnabled()) {
                    logger.debug("redirect=" + url);
                }

                response.sendRedirect(url);

            } else if (page.getForward() != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("forward=" + page.getForward());
                }
                RequestDispatcher dispatcher =
                    request.getRequestDispatcher(page.getForward());

                dispatcher.forward(request, response);

            } else if (page.getPath() != null) {
                renderTemplate(page);

            } else {
                String msg =
                    "Path not defined for Page " + page.getClass().getName();
                throw new RuntimeException(msg);
            }

        } catch (Exception e) {
            handleException(request, response, isPost, e, page);

        } catch (ExceptionInInitializerError eiie) {
            Throwable cause = eiie.getException();
            cause = (cause != null) ? cause : eiie;

            handleException(request, response, isPost, cause, page);

        } finally {
            if (page != null) {
                page.onFinally();
            }

            if (clickApp.getMode() != ClickApp.PRODUCTION) {
                logger.info("handledRequest(): " + page.getPath() + " - "
                            + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
    }

    /**
     * Provides the application exception handler. The application exception
     * will be delegated to the configured error page. The default error page
     * is {@link ErrorPage} and the page template is "click/error.htm"
     * <p/>
     * Applications which wish to provide their own customised error handling
     * must subclass ErrorPage and specify their page in the "/WEB-INF/click.xml"
     * application configuration file. For example:
     * <blockquote><pre>
     * &lt;page path="click/error.htm" classname="com.mycorp.util.ErrorPage"/&gt;
     * </pre></blockquote>
     * If the ErrorPage throws an exception, it will be logged as an error and
     * then be rethrown nested inside a RuntimeException.
     *
     * @param request the servlet request with the associated error
     * @param response the servlet response
     * @param isPost boolean flag denoting the request method is "POST"
     * @param exception the error causing exception
     * @param page the error causing page
     */
    protected void handleException(HttpServletRequest request,
        HttpServletResponse response, boolean isPost, Throwable exception,
        Page page) {

        if (logger.isDebugEnabled()) {
            // Useful to log exceptions which may occur when causing page is
            // being rendered, as they may not be displayed in error page.
            logger.debug("handleException:", exception);

            if (exception instanceof ServletException) {
                Throwable cause = ((ServletException) exception).getRootCause();
                logger.debug("ServletException.rootCause", cause);
            }
        }

        Context context = new Context
            (getServletContext(), getServletConfig(), request, response, isPost);

        ErrorPage errorPage = null;
        try {
            errorPage = (ErrorPage) clickApp.getErrorPageClass().newInstance();

            errorPage.setContext(context);
            errorPage.setError(exception);
            errorPage.setFormat(clickApp.getPageFormat(ClickApp.ERROR_PATH));
            errorPage.setHeaders(clickApp.getPageHeaders(ClickApp.ERROR_PATH));
            errorPage.setMode(clickApp.getModeValue());
            errorPage.setPage(page);
            errorPage.setPath(ClickApp.ERROR_PATH);

            errorPage.onInit();

            errorPage.onSecurityCheck();

            if (isPost) {
                errorPage.onPost();
            } else {
                errorPage.onGet();
            }

            renderTemplate(errorPage);

        } catch (Exception ex) {
            String message =
                "handleError(): " + ex.toString() + " thrown while handling "
                 + " error: " + exception.toString()
                 + ". Now throwing RuntimeException";

            logger.error(message, ex);

            log(message, ex);

            throw new RuntimeException(ex);

        } finally {
            if (errorPage != null) {
                errorPage.onFinally();
            }
        }
    }

    /**
     * Render the Velocity template defined by the page's path.
     * <p/>
     * This method creates a Velocity Context using the Page's model Map and
     * then merges the template with the Context writing the result to the
     * HTTP servlet response.
     * <p/>
     * This method was adapted from VelocityServlet.
     *
     * @param page the page template to merge
     * @throws Exception if an error occurs
     */
    protected void renderTemplate(Page page) throws Exception {

        long startTime = 0;
        if (clickApp.getMode() != ClickApp.PRODUCTION) {
            startTime = System.currentTimeMillis();
        }

        final HttpServletRequest request = page.getContext().getRequest();

        final HttpServletResponse response = page.getContext().getResponse();

        response.setContentType(page.getContentType());

        OutputStream output = response.getOutputStream();

        // If an ErrorPage clear any response "Content-Encoding" "gzip" header
        // and dont compress the output stream
        if (page instanceof ErrorPage) {
            if (response.containsHeader("Content-Encoding")) {
                response.setHeader("Content-Encoding", null);
            }

        // Else if Page has a "Content-Encoding" "gzip" header then we can
        // look to compressing the output stream
        } else if (hasContentEncodingGzipHeader(page)) {

            // If client accepts gzip encoding compress output stream
            String acceptEncoding = request.getHeader("Accept-Encoding");
            if (acceptEncoding != null) {
                if (acceptEncoding.toLowerCase().indexOf("gzip") > -1) {
                    output = new GZIPOutputStream(output, 4 * 1024);
                    response.setHeader("Content-Encoding", "gzip");
                }
            }
        }

        if (page.getHeaders() != null) {
            setPageResponseHeaders(response, page.getHeaders());
        }

        final String encoding = response.getCharacterEncoding();

        final VelocityContext context = createVelocityContext(page);

        final Template template = clickApp.getTemplate(page.getPath());

        VelocityWriter velocityWriter = null;

        try {
            velocityWriter = (VelocityWriter) writerPool.get();

            if (velocityWriter == null) {
                velocityWriter = new VelocityWriter
                    (new OutputStreamWriter(output, encoding), 4 * 1024, true);

            } else {
                velocityWriter.recycle(new OutputStreamWriter(output, encoding));
            }

            template.merge(context, velocityWriter);

        } finally {
            try {
                if (velocityWriter != null) {
                    // flush and put back into the pool don't close to allow
                    // us to play nicely with others.
                    velocityWriter.flush();

                    // Clear the VelocityWriter's reference to its
                    // internal OutputStreamWriter to allow the latter
                    // to be GC'd while vw is pooled.
                    velocityWriter.recycle(null);

                    writerPool.put(velocityWriter);
                }

                if (output != null) {
                    output.flush();
                    output.close();
                }

            } catch (Exception e) {
                // do nothing
            }
        }

        if (clickApp.getMode() != ClickApp.PRODUCTION) {
            logger.info("renderedTemplate(): " + page.getPath() + " - "
                        + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    /**
     * Return a new Page instance for the given request.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param isPost determines whether the request is a POST
     * @return a new Page instance for the given request
     */
    protected Page createPage(HttpServletRequest request,
        HttpServletResponse response, boolean isPost) {

        Context context = new Context
            (getServletContext(), getServletConfig(), request, response, isPost);

        String path = context.getResourcePath();

        Class pageClass = clickApp.getPageClass(path);

        if (pageClass == null) {
            pageClass = clickApp.getNotFoundPageClass();
            path = ClickApp.NOT_FOUND_PATH;
        }

        try {
            Page newPage = (Page) pageClass.newInstance();

            newPage.setContext(context);
            newPage.setFormat(clickApp.getPageFormat(path));
            newPage.setHeaders(clickApp.getPageHeaders(path));
            newPage.setPath(path);

            return newPage;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return true if the page headers contain a "Content-Encoding" header
     * with a value of "gzip".
     *
     * @param page the page to test
     * @return true if the page has a gzip content-encoding header
     */
    protected boolean hasContentEncodingGzipHeader(Page page) {
        Map headers = page.getHeaders();

        if (headers != null && !headers.isEmpty()) {
            String value = (String) headers.get("Content-Encoding");

            if (value == null) {
                value = (String) headers.get("content-encoding");
            }

            return "gzip".equalsIgnoreCase(value);
        }
        return false;
    }

    /**
     * Return a new VelocityContext for the given pages model and Context.
     *
     * @param page the page to create a VelocityContext for
     * @return a new VelocityContext
     */
    protected VelocityContext createVelocityContext(Page page) {

        final VelocityContext context = new VelocityContext(page.getModel());

        final HttpServletRequest request = page.getContext().getRequest();

        Object pop = context.put("request", request);
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"request\". The page model object "
                         + pop + " has been replaced with the request object";
            logger.warn(msg);
        }

        pop = context.put("response", page.getContext().getResponse());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"response\". The page model object "
                         + pop + " has been replaced with the response object";
            logger.warn(msg);
        }

        SessionMap sessionMap = new SessionMap(request.getSession(false));
        pop = context.put("session", sessionMap);
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"session\". The page model object "
                         + pop + " has been replaced with the request "
                         + " session";
            logger.warn(msg);
        }

        pop = context.put("context", request.getContextPath());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"context\". The page model object "
                         + pop + " has been replaced with the request "
                         + " context path";
            logger.warn(msg);
        }

        Object format = page.getFormat();
        if (format != null) {
           pop = context.put("format", format);
            if (pop != null) {
                String msg = page.getClass().getName() + " on "
                        + page.getPath()
                        + " model contains an object keyed with reserved "
                        + "name \"format\". The page model object " + pop
                        + " has been replaced with the format object";
                logger.warn(msg);
            }
        }

        return context;
    }

    /**
     * Set the HTTP headers in the servlet response. The Page response headers
     * are defined in {@link Page#getHeaders()}.
     *
     * @param response the response to set the headers in
     * @param headers the map of HTTP headers to set in the response
     */
    protected void setPageResponseHeaders(HttpServletResponse response,
            Map headers) {

        Iterator headerNames = headers.keySet().iterator();
        while (headerNames.hasNext()) {
            String name = headerNames.next().toString();
            Object value = headers.get(name);

            if (value instanceof String) {
                String strValue = (String) value;
                if (!strValue.equalsIgnoreCase("Content-Encoding")) {
                    response.setHeader(name, strValue);
                }

            } else if (value instanceof Date) {
                long time = ((Date) value).getTime();
                response.setDateHeader(name, time);

            } else {
                int intValue = ((Integer) value).intValue();
                response.setIntHeader(name, intValue);
            }
        }
    }
}
