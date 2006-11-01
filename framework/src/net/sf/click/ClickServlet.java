/*
 * Copyright 2004-2006 Malcolm A. Edgar
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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.ErrorPage;
import net.sf.click.util.ErrorReport;
import net.sf.click.util.Format;
import net.sf.click.util.HtmlStringBuffer;
import net.sf.click.util.PageImports;
import net.sf.click.util.SessionMap;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
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
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;<font color="blue">click-servlet</font>&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;<font color="red">net.sf.click.ClickServlet</font>&lt;/servlet-class&gt;
 *       &lt;load-on-startup&gt;<font color="red">0</font>&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *    &lt;servlet-mapping&gt;
 *       &lt;servlet-name&gt;<font color="blue">click-servlet</font>&lt;/servlet-name&gt;
 *       &lt;url-pattern&gt;<font color="red">*.htm</font>&lt;/url-pattern&gt;
 *    &lt;/servlet-mapping&gt;
 * &lt;/web-app&gt; </pre>
 *
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
 *
 * <a name="app-reloading"><h4>Application Reloading</h4></a>
 * The <tt>ClickServlet</tt> supports the ability to reload the click
 * application "<tt>click.xml</tt>" without having to restart the entire web
 * application.
 * <p/>
 * To reload the application simply make the GET request
 * <font color="blue">/click/reload-app.htm</font> while in the role
 * <font color="red">click-admin</font>.
 * <p/>
 * To enable application reloading you need to configure the servlet
 * init parameter <tt>app-reloadable</tt> as true, and secure
 * the path <tt>/click/reload-app.htm</tt> with the role <tt>click-admin</tt>.
 * If the user making a GET request to this path is not in this role the
 * ClickServlet will return the page not found template.
 *
 * <pre class="codeConfig">
 * &lt;web-app&gt;
 *    &lt;servlet&gt;
 *       &lt;servlet-name&gt;click-servlet&lt;/servlet-name&gt;
 *       &lt;servlet-class&gt;net.sf.click.ClickServlet&lt;/servlet-class&gt;
 *       &lt;init-param&gt;
 *         &lt;param-name&gt;<font color="blue">app-reloadable</font>&lt;/param-name&gt;
 *         &lt;param-value&gt;<font color="red">true</font>&lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *       &lt;load-on-startup&gt;0&lt;/load-on-startup&gt;
 *    &lt;/servlet&gt;
 *
 *    &lt;servlet-mapping&gt;
 *       &lt;servlet-name&gt;click-servlet&lt;/servlet-name&gt;
 *       &lt;url-pattern&gt;*.htm&lt;/url-pattern&gt;
 *    &lt;/servlet-mapping&gt;
 *
 *    &lt;security-constraint&gt;
 *      &lt;web-resource-collection&gt;
 *        &lt;url-pattern&gt;<font color="blue">/click/reload-app.htm</font>&lt;/url-pattern&gt;
 *      &lt;/web-resource-collection&gt;
 *      &lt;auth-constraint&gt;
 *        &lt;role-name&gt;<font color="red">click-admin</font>&lt;/role-name&gt;
 *      &lt;/auth-constraint&gt;
 *    &lt;/security-constraint&gt;
 *
 *    &lt;login-config&gt;
 *      &lt;auth-method&gt;DIGEST&lt;/auth-method&gt;
 *      &lt;realm-name&gt;MyCorp&lt;/realm-name&gt;
 *    &lt;/login-config&gt;
 *
 *    &lt;security-role&gt;
 *      &lt;role-name&gt;<font color="red">click-admin</font>&lt;/role-name&gt;
 *    &lt;/security-role&gt;
 * &lt;/web-app&gt; </pre>
 *
 * @author Malcolm Edgar
 */
public class ClickServlet extends HttpServlet {

    // --------------------------------------------------------------- Contants

    private static final long serialVersionUID = 1L;

    private static final String APPLICAION_RELOADED_MSG  =
        "<html><head>"
        + "<style type='text/css'>body{font-family:Arial;}</style></head>"
        + "<body><h2>Application Reloaded</h2></body></html>";

    /**
     * The forwarded request marker attribute: &nbsp; "<tt>click-forward</tt>".
     */
    protected final static String CLICK_FORWARD = "click-forward";

    /**
     * The click application is reloadable flag servlet init parameter name:
     * &nbsp; "<tt>app-reloadable</tt>".
     */
    protected final static String APP_RELOADABLE = "app-reloadable";

    /**
     * The Page to forward to request attribute: &nbsp; "<tt>click-page</tt>".
     */
    protected final static String FORWARD_PAGE = "forward-page";

    // ------------------------------------------------------ Instance Varables

    /** The click application. */
    protected ClickApp clickApp;

    /** The servlet logger. */
    protected ClickLogger logger;

    /** The page creator factory. */
    protected final ClickService pageMaker = new ClickService();

    /** The click application is reloadable flag. */
    protected boolean reloadable = false;

    /** Cache of velocity writers. */
    protected SimplePool writerPool;

    // --------------------------------------------------------- Public Methods

    /**
     * Initialize the Click servlet and the Velocity runtime.
     *
     * @see javax.servlet.GenericServlet#init()
     *
     * @throws ServletException if the click app could not be initialized
     */
    public void init() throws ServletException {

        try {
            // Dereference any allocated objects
            clickApp = null;
            writerPool = null;

            // Determine whether the click application is reloadable
            reloadable =
                "true".equalsIgnoreCase(getInitParameter(APP_RELOADABLE));

            // Initialize the click application.
            ClickApp newClickApp = new ClickApp();

            newClickApp.setServletConfig(getServletConfig());
            newClickApp.setServletContext(getServletContext());

            newClickApp.init();

            logger = newClickApp.getLogger();

            // Initialise the Cache of velocity writers.
            SimplePool newWriterPool = new SimplePool(40);

            // Set the new ClickApp and writer pool
            clickApp = newClickApp;
            writerPool = newWriterPool;

            if (logger.isInfoEnabled()) {
                logger.info("initialized in " + clickApp.getModeValue()
                            + " mode");
            }

        } catch (Throwable e) {
            e.printStackTrace();

            String msg = "error initializing throwing "
                         + "javax.servlet.UnavailableException";

            log(msg, e);

            throw new UnavailableException(e.toString());
        }
    }

    /**
     * Handle HTTP GET requests. This method will delegate the request to
     * {@link #handleRequest(HttpServletRequest, HttpServletResponse, boolean)}.
     *
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     *
     * @param request the servlet request
     * @param response the servlet response
     * @throws ServletException if click app has not been initialized
     * @throws IOException if an I/O error occurs
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ClickLogger.setInstance(logger);

        ensureAppInitialized();

        if (ifAuthorizedReloadRequest(request)) {
            reloadClickApp(request, response);

        } else {
            handleRequest(request, response, false);
        }
    }

    /**
     * Handle HTTP POST requests. This method will delegate the request to
     * {@link #handleRequest(HttpServletRequest, HttpServletResponse, boolean)}.
     *
     * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     *
     * @param request the servlet request
     * @param response the servlet response
     * @throws ServletException if click app has not been initialized
     * @throws IOException if an I/O error occurs
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ClickLogger.setInstance(logger);

        ensureAppInitialized();

        handleRequest(request, response, true);
    }

    /**
     * Handle the given servlet request and render the results to the
     * servlet response.
     * <p/>
     * If an exception occurs within this method the exception will be delegated
     * to: <p/>
     * {@link #handleException(HttpServletRequest, HttpServletResponse, boolean, Throwable, Class)}
     *
     * @param request the servlet request to process
     * @param response the servlet response to render the results to
     * @param isPost determines whether the request is a POST
     */
    protected void handleRequest(HttpServletRequest request,
        HttpServletResponse response, boolean isPost) {

        long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(200);
            buffer.append(request.getMethod());
            buffer.append(" ");
            buffer.append(request.getRequestURL());
            logger.debug(buffer);
        }

        if (clickApp.getCharset() != null) {
            try {
                request.setCharacterEncoding(clickApp.getCharset());

            } catch (UnsupportedEncodingException ex) {
                String msg = "The character encoding "
                             + clickApp.getCharset() + " is invalid.";
                logger.warn(msg, ex);
            }
        }

        if (logger.isTraceEnabled()) {
            Map requestParams = getRequestParameters(request);
            Iterator i = requestParams.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                String name = entry.getKey().toString();
                String value = entry.getValue().toString();
                logger.trace("   " + name + "=" + value);
            }
        }


        Page page = null;
        try {
            page = createPage(request, response, isPost);

            processPage(page);

        } catch (Exception e) {
            Class pageClass =
                clickApp.getPageClass(ClickUtils.getResourcePath(request));

            handleException(request, response, isPost, e, pageClass);

        } catch (ExceptionInInitializerError eiie) {
            Throwable cause = eiie.getException();
            cause = (cause != null) ? cause : eiie;

            Class pageClass =
                clickApp.getPageClass(ClickUtils.getResourcePath(request));

            handleException(request, response, isPost, cause, pageClass);

        } finally {
            if (page != null) {
                page.onDestroy();

                if (!clickApp.isProductionMode()) {
                    logger.info("handleRequest:  " + page.getPath() + " - "
                                + (System.currentTimeMillis() - startTime)
                                + " ms");
                }
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
     *
     * <pre class="codeConfig">
     * &lt;page path="<span class="navy">click/error.htm</span>" classname="<span class="maroon">com.mycorp.util.ErrorPage</span>"/&gt; </pre>
     *
     * If the ErrorPage throws an exception, it will be logged as an error and
     * then be rethrown nested inside a RuntimeException.
     *
     * @param request the servlet request with the associated error
     * @param response the servlet response
     * @param isPost boolean flag denoting the request method is "POST"
     * @param exception the error causing exception
     * @param pageClass the page class with the error
     */
    protected void handleException(HttpServletRequest request,
        HttpServletResponse response, boolean isPost, Throwable exception,
        Class pageClass) {

        if (exception instanceof ParseErrorException == false) {
            logger.error("handleException: ", exception);
        }

        Context context = new Context(getServletContext(),
                                      getServletConfig(),
                                      request,
                                      response,
                                      isPost,
                                      pageMaker);

        ErrorPage errorPage = null;
        try {
            errorPage = (ErrorPage) clickApp.getErrorPageClass().newInstance();

            errorPage.setContext(context);
            errorPage.setError(exception);
            if (errorPage.getFormat() == null) {
                errorPage.setFormat(clickApp.getFormat(context.getLocale()));
            }
            errorPage.setHeaders(clickApp.getPageHeaders(ClickApp.ERROR_PATH));
            errorPage.setMode(clickApp.getModeValue());
            errorPage.setPageClass(pageClass);
            errorPage.setPath(ClickApp.ERROR_PATH);

            processPage(errorPage);

        } catch (Exception ex) {
            String message =
                "handleError: " + ex.getClass().getName()
                 + " thrown while handling " + exception.getClass().getName()
                 + ". Now throwing RuntimeException.";

            logger.error(message, ex);

            throw new RuntimeException(ex);

        } finally {
            if (errorPage != null) {
                errorPage.onDestroy();
            }
        }
    }

    /**
     * Process the given page invoking its "on" event callback methods
     * and directing the response. This method does not invoke the "onDestroy()"
     * callback method.
     *
     * @param page the Page to process
     * @throws Exception if an error occurs
     */
    protected void processPage(Page page) throws Exception {

        final HttpServletRequest request = page.getContext().getRequest();
        final HttpServletResponse response = page.getContext().getResponse();
        final boolean isPost = page.getContext().isPost();

        page.onInit();

        boolean continueProcessing = page.onSecurityCheck();

        if (continueProcessing && page.hasControls()) {

            // Make sure dont process a forwarded request
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
            page.onRender();
        }

        if (page.getRedirect() != null) {
            String url = page.getRedirect();

            if (url.charAt(0) == '/') {
                url = request.getContextPath() + url;
            }

            url = response.encodeRedirectURL(url);

            if (logger.isDebugEnabled()) {
                logger.debug("redirect=" + url);
            }

            response.sendRedirect(url);

        } else if (page.getForward() != null) {
            request.setAttribute(CLICK_FORWARD, CLICK_FORWARD);

            if (logger.isDebugEnabled()) {
                logger.debug("forward=" + page.getForward());
            }

            if (page.getForward().endsWith(".jsp")) {
                renderJSP(request, response, page);

            } else {
                RequestDispatcher dispatcher =
                    request.getRequestDispatcher(page.getForward());

                dispatcher.forward(request, response);
            }

        } else if (page.getPath() != null) {
            renderTemplate(page, request);

        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Page path not defined for "
                             + page.getClass().getName());
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
     * This method was adapted from org.apache.velocity.servlet.VelocityServlet.
     *
     * @param page the page template to merge
     * @param request the page request
     * @throws Exception if an error occurs
     */
    protected void renderTemplate(Page page, HttpServletRequest request)
        throws Exception {

        long startTime = System.currentTimeMillis();

        final VelocityContext context = createVelocityContext(page);

        // May throw parsing error if template could not be obtained
        final Template template = clickApp.getTemplate(page.getTemplate());

        final HttpServletResponse response = page.getContext().getResponse();

        response.setContentType(page.getContentType());

        OutputStream output = response.getOutputStream();

        if (page.getHeaders() != null) {
            setPageResponseHeaders(response, page.getHeaders());
        }

        final String encoding = response.getCharacterEncoding();

        VelocityWriter velocityWriter = null;

        try {
            velocityWriter = (VelocityWriter) writerPool.get();

            OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(output, encoding);

            if (velocityWriter == null) {
                velocityWriter =
                    new VelocityWriter(outputStreamWriter, 4 * 1024, true);

            } else {
                velocityWriter.recycle(outputStreamWriter);
            }

            template.merge(context, velocityWriter);

        } catch (Exception error) {
            // Exception occured merging template and model. It is possible
            // that some output has already been written, so we will append the
            // error report to the previous output.
            ErrorReport errorReport =
                new ErrorReport(error,
                                page.getClass(),
                                clickApp.isProductionMode(),
                                request,
                                getServletContext());

            if (velocityWriter == null) {
                OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(output, encoding);

                velocityWriter =
                    new VelocityWriter(outputStreamWriter, 4 * 1024, true);
            }

            velocityWriter.write(errorReport.getErrorReport());

            throw error;

        } finally {
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

            output.flush();
            output.close();
        }

        if (!clickApp.isProductionMode()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(50);
            buffer.append("renderTemplate: ");
            if (!page.getTemplate().equals(page.getPath())) {
                buffer.append(page.getPath());
                buffer.append(",");
            }
            buffer.append(page.getTemplate());
            buffer.append(" - ");
            buffer.append(System.currentTimeMillis() - startTime);
            buffer.append(" ms");
            logger.info(buffer);
        }
    }

    /**
     * Render the given page as a JSP to the response.
     *
     * @param request the page request
     * @param response the servlet response
     * @param page the page to render
     * @throws Exception if an error occurs rendering the JSP
     */
    protected void renderJSP(HttpServletRequest request,
            HttpServletResponse response, Page page) throws Exception {

        long startTime = System.currentTimeMillis();

        setRequestAttributes(page);

        RequestDispatcher dispatcher = null;

           if (page.getForward().equals(page.getTemplate())) {
               dispatcher = request.getRequestDispatcher(page.getForward());

        } else {
               dispatcher = request.getRequestDispatcher(page.getTemplate());
        }

        dispatcher.forward(request, response);

        if (!clickApp.isProductionMode()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(50);
            buffer.append("renderJSP: ");
            if (!page.getTemplate().equals(page.getForward())) {
                buffer.append(page.getTemplate());
                buffer.append(",");
            }
            buffer.append(page.getForward());
            buffer.append(" - ");
            buffer.append(System.currentTimeMillis() - startTime);
            buffer.append(" ms");
            logger.info(buffer);
        }
    }

    /**
     * Return a new Page instance for the given request. This method will
     * invoke {@link #initPage(String, Class, HttpServletRequest)} to create
     * the Page instance and then set the properties on the page.
     *
     * @param request the servlet request
     * @param response the servlet response
     * @param isPost determines whether the request is a POST
     * @return a new Page instance for the given request
     */
    protected Page createPage(HttpServletRequest request,
        HttpServletResponse response, boolean isPost) {

        Context context = new Context(getServletContext(),
                                      getServletConfig(),
                                      request,
                                      response,
                                      isPost,
                                      pageMaker);

        String path = context.getResourcePath();

        if (request.getAttribute(FORWARD_PAGE) != null) {
            Page forwardPage = (Page) request.getAttribute(FORWARD_PAGE);

            forwardPage.setContext(context);

            if (forwardPage.getFormat() == null) {
                forwardPage.setFormat(clickApp.getFormat(context.getLocale()));
            }

            request.removeAttribute(FORWARD_PAGE);

            return forwardPage;
        }

        Class pageClass = clickApp.getPageClass(path);

        if (pageClass == null) {
            pageClass = clickApp.getNotFoundPageClass();
            path = ClickApp.NOT_FOUND_PATH;
        }

        final Page page = initPage(path, pageClass, request);

        page.setContext(context);

        if (page.getFormat() == null) {
            page.setFormat(clickApp.getFormat(context.getLocale()));
        }

        return page;
    }

    /**
     * Initialize a new page instance using
     * {@link #newPageInstance(String, Class, HttpServletRequest)} method and
     * setting format, headers and the forward if a JSP.
     * <p/>
     * This method will also automatically register any public Page controls
     * in the page's model. When the page is created any public visiblity
     * page Control variables will be automatically added to the page using
     * the method {@link Page#addControl(Control)} method. If the controls name
     * is not defined it is set to the member variables name before it is added
     * to the page.
     * <p/>
     * This feature saves you from having to mannually add the controls yourself.
     * If you dont want the controls automatically added, simply declare them
     * as non public variables.
     * <p/>
     * An example auto control registration is provided below. In this example
     * the Table control is automatically added to the model using the name
     * <tt>"table"</tt>, and the ActionLink controls are added using the names
     * <tt>"editDetailsLink"</tt> and <tt>"viewDetailsLink"</tt>.
     *
     * <pre class="javaCode">
     * <span class="kw">public class</span> OrderDetailsPage <span class="kw">extends</span> Page {
     *
     *     <span class="kw">public</span> Table table = <span class="kw">new</span> Table();
     *     <span class="kw">public</span> ActionLink editDetailsLink = <span class="kw">new</span> ActionLink();
     *     <span class="kw">public</span>ActionLink viewDetailsLink = <span class="kw">new</span> ActionLink();
     *
     *     <span class="kw">public</span> OrderDetailsPage() {
     *         ..
     *     }
     * } </pre>
     *
     * @param path the page path
     * @param pageClass the page class
     * @param request the page request
     * @return initialized page
     */
    protected Page initPage(String path, Class pageClass,
            HttpServletRequest request) {

        try {
            final Page newPage = newPageInstance(path, pageClass, request);

            if (newPage.getHeaders() == null) {
                newPage.setHeaders(clickApp.getPageHeaders(path));
            }

            newPage.setPath(path);

            if (clickApp.isJspPage(path)) {
                newPage.setForward(StringUtils.replace(path, ".htm", ".jsp"));
            }

            processPageFields(newPage, new FieldCallback() {
                public void processField(String fieldName, Object fieldValue) {
                    if (fieldValue instanceof Control) {
                        Control control = (Control) fieldValue;
                        if (control.getName() == null) {
                            control.setName(fieldName);
                        }

                        if (!newPage.getModel().containsKey(control.getName())) {
                            newPage.addControl(control);
                        }
                    }
                }
            });

            return newPage;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a new Page instance for the given page path, class and request.
     * <p/>
     * The default implementation of this method simply creates a new page
     * instances:
     * <pre class="codeJava">
     * <span class="kw">protected</span> Page newPageInstance(String path, Class pageClass,
     *     HttpServletRequest request) <span class="kw">throws</span> Exception {
     *
     *     <span class="kw">return</span> (Page) pageClass.newInstance();
     * } </pre>
     *
     * This method is designed to be overridden by applications providing their
     * own page creation patterns.
     * <p/>
     * A typical example of this would be with Inversion of Control (IoC)
     * frameworks such as Spring or HiveMind. For example a Spring application
     * could override this method and use a <tt>ApplicationContext</tt> to instantiate
     * new Page objects:
     * <pre class="codeJava">
     * <span class="kw">protected</span> Page newPageInstance(String path, Class pageClass,
     *     HttpServletRequest request) <span class="kw">throws</span> Exception {
     *
     *     String beanName = path.substring(0, path.indexOf(<span class="st">"."</span>));
     *
     *     <span class="kw">if</span> (applicationContext.containsBean(beanName)) {
     *         Page page = (Page) applicationContext.getBean(beanName);
     *
     *     } <span class="kw">else</span> {
     *         page = (Page) pageClass.newIntance();
     *     }
     *
     *     <span class="kw">return</span> page;
     * } </pre>
     *
     * @param path the request page path
     * @param pageClass the page Class the request is mapped to
     * @param request the page request
     * @return a new Page object
     * @throws Exception if an error occurs creating the Page
     */
    protected Page newPageInstance(String path, Class pageClass,
            HttpServletRequest request) throws Exception {

        return (Page) pageClass.newInstance();
    }

    /**
     * Return a new VelocityContext for the given pages model and Context.
     * <p/>
     * The following values automatically added to the VelocityContext:
     * <ul>
     * <li>any public Page fields using the fields name</li>
     * <li>context - the Servlet context path, e.g. /mycorp</li>
     * <li>format - the {@link Format} object for formatting the display of objects</li>
     * <li>imports - the {@link PageImports} object</li>
     * <li>messages - the page messages bundle</li>
     * <li>path - the page of the page template to render</li>
     * <li>request - the pages servlet request</li>
     * <li>response - the pages servlet request</li>
     * <li>session - the {@link SessionMap} adaptor for the users HttpSession</li>
     * </ul>
     *
     * @param page the page to create a VelocityContext for
     * @return a new VelocityContext
     */
    protected VelocityContext createVelocityContext(Page page) {

        final VelocityContext context = new VelocityContext(page.getModel());

        processPageFields(page, new FieldCallback() {
            public void processField(String fieldName, Object fieldValue) {
                if (fieldValue instanceof Control == false) {
                    context.put(fieldName, fieldValue);
                }
            }
        });

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

        Format format = page.getFormat();
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

        String path = page.getPath();
        if (path != null) {
           pop = context.put("path", path);
            if (pop != null) {
                String msg = page.getClass().getName() + " on "
                        + page.getPath()
                        + " model contains an object keyed with reserved "
                        + "name \"path\". The page model object " + pop
                        + " has been replaced with the page path";
                logger.warn(msg);
            }
        }

        pop = context.put("messages", page.getMessages());
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"messages\". The page model object "
                         + pop + " has been replaced with the request "
                         + " messages";
            logger.warn(msg);
        }

        pop = context.put("imports", new PageImports(page));
        if (pop != null) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                         + " model contains an object keyed with reserved "
                         + "name \"imports\". The page model object "
                         + pop + " has been replaced with a PageImports object";
            logger.warn(msg);
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

        for (Iterator i = headers.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();

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

    /**
     * Ensure the ClickApp and the Velocity WriterPool have been initialized
     * otherwise throw a UnavailableException.
     * <p/>
     * If the <tt>click-reloadable</tt> a temporarily UnavailableException is
     * thrown, otherwise if not reloadable then a permantantly
     * UnavailableException is thrown.
     *
     * @throws UnavailableException if the application has not been initialized
     */
    protected void ensureAppInitialized() throws UnavailableException {
        if (clickApp == null || writerPool == null) {
            if (reloadable) {
                String msg = "The application is temporarily unavailable"
                             + " - please try again in 1 minute";
                throw new UnavailableException(msg, 60);
            } else {
                String msg = "The application is unavailable.";
                throw new UnavailableException(msg);
            }
        }
    }

    /**
     * Return true if the request is click application reload request GET
     * <tt>"/click/reload-app.htm"</tt> and the user is in the role
     * <tt>"click-admin"</tt>. To reload the click application the
     * servlet init parameter <tt>app-reloadable</tt> must also be defined.
     *
     * @param request the servlet request
     * @return if a reload request and servlet configured to enable reloading
     *  and the user is in "click-admin" role
     */
    protected boolean ifAuthorizedReloadRequest(HttpServletRequest request) {
        if (reloadable && request.isUserInRole("click-admin")) {
            String path = ClickUtils.getResourcePath(request);

            return "/click/reload-app.htm".equals(path);

        } else {
            return false;
        }
    }

    /**
     * Reload the ClickApp and send status message to the given response.
     *
     * @param request the servlet request
     * @param response the response to write the status message to
     * @throws ServletException if an error occurs reloading the application
     * @throws IOException if an I/O error occurs
     */
    protected void reloadClickApp(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        init();

        final String msg =
            "ClickApp reloaded by " + request.getRemoteUser() + " on "
            + (new Date()).toString();
        logger.info(msg);

        response.setContentType("text/html");
        response.getWriter().print(APPLICAION_RELOADED_MSG);
        response.getWriter().close();
    }

    /**
     * Set the page model, context, format, messages and path as request
     * attributes to support JSP rendering. These request attributes include:
     * <ul>
     * <li>any public Page fields using the fields name</li>
     * <li>context - the Servlet context path, e.g. /mycorp</li>
     * <li>format - the {@link Format} object for formatting the display of objects</li>
     * <li>forward - the page forward path, if defined</li>
     * <li>imports - the {@link PageImports} object</li>
     * <li>messages - the page messages bundle</li>
     * <li>path - the page of the page template to render</li>
     * </ul>
     *
     * @param page the page to set the request attributes on
     */
    protected void setRequestAttributes(Page page) {
        final HttpServletRequest request = page.getContext().getRequest();

        processPageFields(page, new FieldCallback() {
            public void processField(String fieldName, Object fieldValue) {
                if (fieldValue instanceof Control == false) {
                    request.setAttribute(fieldName, fieldValue);
                }
            }
        });

        Map model = page.getModel();
        for (Iterator i = model.entrySet().iterator(); i.hasNext();)  {
            Map.Entry entry = (Map.Entry) i.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();

            request.setAttribute(name, value);
        }

        request.setAttribute("context", request.getContextPath());
        if (model.containsKey("context")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                            + " model contains an object keyed with reserved "
                            + "name \"context\". The request attribute "
                            + "has been replaced with the request "
                            + "context path";
            logger.warn(msg);
        }

        request.setAttribute("format", page.getFormat());
        if (model.containsKey("format")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                            + " model contains an object keyed with reserved "
                            + "name \"format\". The request attribute "
                            + "has been replaced with the format object";
            logger.warn(msg);
        }

        request.setAttribute("forward", page.getForward());
        if (model.containsKey("forward")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                            + " model contains an object keyed with reserved "
                            + "name \"forward\". The request attribute "
                            + "has been replaced with the page path";
            logger.warn(msg);
        }

        request.setAttribute("path", page.getPath());
        if (model.containsKey("path")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                            + " model contains an object keyed with reserved "
                            + "name \"path\". The request attribute "
                            + "has been replaced with the page path";
            logger.warn(msg);
        }

        request.setAttribute("messages", page.getMessages());
        if (model.containsKey("messages")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                            + " model contains an object keyed with reserved "
                            + "name \"messages\". The request attribute "
                            + "has been replaced with the page messages";
            logger.warn(msg);
        }

        request.setAttribute("imports", new PageImports(page));
        if (model.containsKey("imports")) {
            String msg = page.getClass().getName() + " on " + page.getPath()
                             + " model contains an object keyed with reserved "
                             + "name \"imports\". The request attribute "
                             + "has been replaced with a PageImports object";
            logger.warn(msg);
        }

    }

    /**
     * Return an ordered map of request parameters from the given request.
     *
     * @param request the servlet request to obtain request parameters from
     * @return the ordered map of request parameters
     */
    protected Map getRequestParameters(HttpServletRequest request) {

        TreeMap requestParams = new TreeMap();

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement().toString();

            String[] values = request.getParameterValues(name);
            HtmlStringBuffer valsBuffer = new HtmlStringBuffer(32);

            if (values.length == 1) {
                valsBuffer.append(values[0]);

            } else {
                for (int i = 0; i < values.length; i++) {
                    if (i == 0) {
                        valsBuffer.append("[");
                    }
                    valsBuffer.append(values[i]);
                    if (i == values.length - 1) {
                        valsBuffer.append("]");
                    } else {
                        valsBuffer.append(",");
                    }
                }
            }
            requestParams.put(name, valsBuffer.toString());
        }

        return requestParams;
    }

    /**
     * Process all the Pages public fields using the given callback.
     *
     * @param page the page to obtain the fields from
     * @param callback the fields iteractor callback
     */
    protected void processPageFields(Page page, FieldCallback callback) {

        Field[] fields = page.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            try {
                Object fieldValue = field.get(page);

                if (fieldValue != null) {
                    callback.processField(field.getName(), fieldValue);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * Provdes a package visibility interface for the Context class for
     * accessing ClickApp and ClickServlet services.
     *
     * @author Malcolm Edgar
     */
    class ClickService {

        /**
         * Return a new Page instance for the given path.
         *
         * @param path the Page path configured in the click.xml file
         * @return a new Page object
         * @throws IllegalArgumentException if the Page is not found
         */
        Page createPage(String path, HttpServletRequest request) {
            Class pageClass = clickApp.getPageClass(path);

            if (pageClass == null) {
                String msg = "No Page class configured for path: " + path;
                throw new IllegalArgumentException(msg);
            }

            return initPage(path, pageClass, request);
        }

        /**
         * Return a new Page instance for the page Class.
         *
         * @param page the class of the Page to create
         * @return a new Page object
         * @throws IllegalArgumentException if the Page Class is not configured
         * with a unique path
         */
        Page createPage(Class pageClass, HttpServletRequest request) {
            String path = clickApp.getPagePath(pageClass);

            if (path == null) {
                String msg =
                    "No path configured for Page class: " + pageClass.getName();
                throw new IllegalArgumentException(msg);
            }

            return initPage(path, pageClass, request);
        }

        /**
         * Return the Click application mode value: &nbsp;
         * <tt>["production", "profile", "development", "debug"]</tt>.
         *
         * @return the application mode value
         */
        String getApplicationMode() {
            return clickApp.getModeValue();
        }

        /**
         * Return the Click application charset or null if not defined.
         *
         * @return the application charset value
         */
        String getCharset() {
            return clickApp.getCharset();
        }

        /**
         * Return the Click application locale or null if not defined.
         *
         * @return the application locale value
         */
        Locale getLocale() {
            return clickApp.getLocale();
        }

        /**
         * Return the path for the given page Class.
         *
         * @param pageClass the class of the Page to lookup the path for
         * @return the path for the given page Class
         * @throws IllegalArgumentException if the Page Class is not configured
         * with a unique path
         */
        String getPagePath(Class pageClass) {
            String path = clickApp.getPagePath(pageClass);

            if (path == null) {
                String msg =
                    "No path configured for Page class: " + pageClass.getName();
                throw new IllegalArgumentException(msg);
            }

            return path;
        }

        /**
         * Return a rendered Velocity template and model for the given
         * class and model data.
         * <p/>
         * This method will merge the class <tt>.htm</tt> and model using the
         * Velocity Engine.
         * <p/>
         * An example of the class template resolution is provided below:
         * <pre class="codeConfig">
         * <span class="cm">// Full class name</span>
         * com.mycorp.control.CustomTextField
         *
         * <span class="cm">// Template path name</span>
         * /com/mycorp/control/CustomTextField.htm </pre>
         *
         * Example method usage:
         * <pre class="codeJava">
         * <span class="kw">public String</span> toString() {
         *     Map model = getModel();
         *     <span class="kw">return</span> getContext().renderTemplate(getClass(), model);
         * } </pre>
         *
         * @param templateClass the class to resolve the template for
         * @param model the model data to merge with the template
         * @return rendered Velocity template merged with the model data
         * @throws RuntimeException if an error occurs
         */
        String renderTemplate(Class templateClass, Map model) {

            if (templateClass == null) {
                String msg = "Null templateClass parameter";
                throw new IllegalArgumentException(msg);
            }

            String templatePath = templateClass.getName();
            templatePath = '/' + templatePath.replace('.', '/') + ".htm";

            return renderTemplate(templatePath, model);
        }

        /**
         * Return a rendered Velocity template and model data.
         * <p/>
         * Example method usage:
         * <pre class="codeJava">
         * <span class="kw">public String</span> toString() {
         *     Map model = getModel();
         *     <span class="kw">return</span> getContext().renderTemplate(<span class="st">"/custom-table.htm"</span>, model);
         * } </pre>
         *
         * @param templatePath the path of the Velocity template to render
         * @param model the model data to merge with the template
         * @return rendered Velocity template merged with the model data
         * @throws RuntimeException if an error occurs
         */
        String renderTemplate(String templatePath, Map model) {

            if (templatePath == null) {
                String msg = "Null templatePath parameter";
                throw new IllegalArgumentException(msg);
            }

            if (model == null) {
                String msg = "Null model parameter";
                throw new IllegalArgumentException(msg);
            }

            VelocityContext context = new VelocityContext(model);

            StringWriter stringWriter = new StringWriter(1024);

            try {
                Template template = null;

                String charset = clickApp.getCharset();
                if (charset != null) {
                    template = clickApp.getTemplate(templatePath, charset);
                } else {
                    template = clickApp.getTemplate(templatePath);
                }

                if (template == null) {
                    String msg =
                        "Template not found for template path: " + templatePath;
                    throw new IllegalArgumentException(msg);
                }

                template.merge(context, stringWriter);

            } catch (Exception e) {
                String msg = "Error occured rendering template: "
                             + templatePath;
                logger.error(msg, e);

                throw new RuntimeException(e);
            }

            return stringWriter.toString();
        }

    }

    /**
     * Field iterator callback.
     */
    static interface FieldCallback {

        public void processField(String fieldName, Object fieldValue);

    }

}
