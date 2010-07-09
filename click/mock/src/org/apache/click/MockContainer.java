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
package org.apache.click;

import org.apache.click.servlet.MockServletContext;
import org.apache.click.servlet.MockServletConfig;
import org.apache.click.servlet.MockResponse;
import org.apache.click.servlet.MockRequest;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.click.servlet.MockSession;
import org.apache.commons.lang.StringUtils;

/**
 * Provides a mock container for testing Click Pages.
 * <p/>
 * Use the {@link #start()} and {@link #stop()} methods to control the life cycle
 * of the container. Each call to <tt>start / stop</tt> instantiates new
 * mock instances for the container.
 * <p/>
 * To instantiate a container you must specify a web application directory
 * where your page templates and other resources like images, javascript and
 * stylesheets are available.
 * <p/>
 * You can set the <tt>web application root</tt> to refer to your actual live
 * project's web directory.<p/>
 * For  example if you are busy developing a web  application located under
 * <tt>'c:\dev\myapp\web'</tt>, you can start the MockContainerTest as follows:
 *
 * <pre class="prettyprint">
 * public class MyTest extends junit.framework.TestCase {
 *     public void testMyPage() {
 *         MockContainer container = new MockContainer("c:/dev/myapp/web");
 *         container.start();
 *
 *         container.setParameter("param", "one");
 *         MyPage page = (MyPage) container.testPage(MyPage.class);
 *         Assert.assertEquals("one", page.getParam());
 *
 *         container.stop();
 *     }
 * }
 * </pre>
 *
 * Together with a valid web application directory you also need to have the
 * click.xml available, either in the WEB-INF/click.xml directory or on your
 * classpath.
 * <p/>
 * Taking the above example further, if your application is developed under
 * <tt>'c:\dev\myapp\web'</tt>, click.xml would be available at <tt>'WEB-INF\click.xml'</tt>.
 * The full path would be <tt>'c:\dev\myapp\web\WEB-INF\click.xml'</tt>.
 * <p/>
 * Alternatively click.xml can also be specified on the classpath. For example
 * you can save click.xml in your <tt>src</tt> folder eg:
 * <tt>'c:\dev\myapp\web\src\click.xml'</tt>.
 * Below is an example click.xml to get up and running quickly:
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="UTF-8" standalone="yes"?&gt;
 * &lt;click-app charset="UTF-8"&gt;
 *   &lt;pages package="com.mycorp.pages"/&gt;
 *   &lt;mode value="trace"/&gt;
 * &lt;/click-app&gt;
 * </pre>
 */
public class MockContainer {

    // -------------------------------------------------------- Private variables

    /** Holds the MockRequest instance. */
    private MockRequest request;

    /** Holds the MockResponse instance. */
    private MockResponse response;

    /** Holds the ClickServlet instance. */
    private ClickServlet clickServlet;

    /** Holds the MockServletConfig instance. */
    private MockServletConfig servletConfig;

    /** Holds the MockServletContext instance. */
    private MockServletContext servletContext;

    /** Holds the MockSession instance. */
    private MockSession session;

    /** Indicates if the MockContainer has been started or not. */
    private boolean started = false;

    /**
     * Specifies the web application root path where resources eg templates and
     * images can be found.
     */
    private String webappPath;

    /** Specified the locale for the container. */
    private Locale locale;

    // -------------------------------------------------------- Public constructors

    /**
     * Create a new container for the specified webappPath.
     *
     * @param webappPath specifies the web application root path where
     * resources eg templates and images can be found.
     */
    public MockContainer(String webappPath) {
        if (StringUtils.isBlank(webappPath)) {
            throw new IllegalArgumentException("webappPath cannot be blank");
        }
        this.webappPath = webappPath;
    }

    /**
     * Create a new container for the specified webappPath and locale.
     *
     * @param webappPath specifies the web application root path where
     * resources eg templates and images can be found.
     * @param locale the container locale
     */
    public MockContainer(String webappPath, Locale locale) {
        if (StringUtils.isBlank(webappPath)) {
            throw new IllegalArgumentException("webappPath cannot be blank");
        }
        this.webappPath = webappPath;
        this.locale = locale;
    }

    // -------------------------------------------------------- Public getters/setters

    /**
     * Return the container {@link org.apache.click.servlet.MockRequest}.
     *
     * @return the container MockRequest
     */
    public MockRequest getRequest() {
        return request;
    }

    /**
     * Set the container {@link org.apache.click.servlet.MockRequest}.
     *
     * @param request the container MockRequest
     */
    public void setRequest(MockRequest request) {
        this.request = request;
    }

    /**
     * Return the container {@link org.apache.click.servlet.MockResponse}.
     *
     * @return the container MockResponse
     */
    public MockResponse getResponse() {
        return response;
    }

    /**
     * Set the container {@link org.apache.click.servlet.MockResponse}.
     *
     * @param response the container MockResponse
     */
    public void setResponse(MockResponse response) {
        this.response = response;
    }

    /**
     * Return the container {@link org.apache.click.ClickServlet}.
     *
     * @return the container ClickServlet
     */
    public ClickServlet getClickServlet() {
        return clickServlet;
    }

    /**
     * Set the container {@link org.apache.click.ClickServlet}.
     *
     * @param clickServlet the container ClickServlet
     */
    public void setClickServlet(ClickServlet clickServlet) {
        this.clickServlet = clickServlet;
    }

    /**
     * Return the container {@link org.apache.click.servlet.MockServletConfig}.
     *
     * @return the container MockServletConfig
     */
    public MockServletConfig getServletConfig() {
        return servletConfig;
    }

    /**
     * Set the container {@link org.apache.click.servlet.MockServletConfig}.
     *
     * @param servletConfig the container MockServletConfig
     */
    public void setServletConfig(MockServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    /**
     * Return the container {@link org.apache.click.servlet.MockServletContext}.
     *
     * @return the container MockServletContext
     */
    public MockServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Set the container {@link org.apache.click.servlet.MockServletContext}.
     *
     * @param servletContext the container MockServletContext
     */
    public void setServletContext(MockServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Return the container {@link org.apache.click.servlet.MockSession}.
     *
     * @return the container MockSession
     */
    public MockSession getSession() {
        return session;
    }

    /**
     * Set the container {@link org.apache.click.servlet.MockSession}.
     *
     * @param session the container MockSession
     */
    public void setSession(MockSession session) {
        this.session = session;
    }

    // -------------------------------------------------------- Public methods

    /**
     * Starts the container and configure it for testing
     * {@link org.apache.click.Page} instances.
     * <p/>
     * During configuration a full mock servlet stack is created consisting of:
     * <ul>
     *     <li>{@link org.apache.click.ClickServlet}</li>
     *     <li>{@link org.apache.click.servlet.MockRequest}</li>
     *     <li>{@link org.apache.click.servlet.MockResponse}</li>
     *     <li>{@link org.apache.click.servlet.MockServletContext}</li>
     *     <li>{@link org.apache.click.servlet.MockServletConfig}</li>
     *     <li>{@link org.apache.click.servlet.MockSession}</li>
     *     <li>{@link org.apache.click.MockContext}</li>
     * </ul>
     * <p/>
     * You can provide your own Mock implementations and set them on the
     * container using the appropriate <tt>setter</tt> method for example:
     * {@link #setRequest(org.apache.click.servlet.MockRequest)}.
     * <p/>
     * <b>Please note</b> that you must set the mock objects on the container
     * <tt>before</tt> calling <tt>start()</tt>.
     * <p/>
     * You also have full access to the mock objects after starting the container
     * by using the appropriate <tt>getter</tt> method for example:
     * {@link #getRequest()}.
     * <p/>
     * Below is an example of how to start the container:
     * <pre class="prettyprint">
     * public class TestPages extends junit.framework.TestCase {
     *
     *     public void testAll() {
     *         String webApplicationDir = "c:/dev/app/web";
     *         MockContainer container = new MockContainer(webApplicationDir);
     *
     *         container.start();
     *         ...
     *         container.stop();
     *     }
     * }
     * </pre>
     *
     * @see #stop()
     */
    public void start() {
        configure();
        this.started = true;
    }

    /**
     * Stops the container. The container cannot be used until {@link #start}
     * is called again.
     * <p/>
     * <b>Please note</b> that after each <tt>start / stop</tt> cycle the
     * container is reconfigured with <tt>new</tt> mock instances. The mock
     * instances from the previous test run is discarded.
     *
     * @see #start()
     */
    public void stop() {
        started = false;
    }

    /**
     * Convenience method for setting the {@link org.apache.click.servlet.MockRequest}
     * attribute.
     * <p/>
     * <b>Note</b> this method returns <tt>this</tt> so you can easily chain
     * calls to this method.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * container.setAttribute("id", "100").setAttribute("name", "Peter").setAttribute("amount", "555.43");
     * </pre>
     *
     * @param key the attribute key
     * @param value the attribute value
     * @return this MockContainer instance
     */
    public MockContainer setAttribute(String key, Object value) {
        if (!started) {
            throw new IllegalStateException("Container has not been started yet. Call start() first.");
        }
        getRequest().setAttribute(key, value);
        return this;
    }

    /**
     * Convenience method for setting the {@link org.apache.click.servlet.MockRequest}
     * parameter.
     * <p/>
     * <b>Note</b> this method returns <tt>this</tt> so you can easily chain
     * calls to this method.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * container.setParameter("id", "100").setParameter("name", "Peter").setParameter("amount", "555.43");
     * </pre>
     *
     * @param key the parameter key
     * @param value the parameter value
     * @return this MockContainer instance
     */
    public MockContainer setParameter(String key, String value) {
        if (!started) {
            throw new IllegalStateException("Container has not been started yet. Call start() first.");
        }
        getRequest().setParameter(key, value);

        return this;
    }

    /**
     * Convenience method for setting multi-valued {@link org.apache.click.servlet.MockRequest}
     * parameters.
     * <p/>
     * <b>Note</b> this method returns <tt>this</tt> so you can easily chain
     * calls to this method.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * String[] array = {"one", "two", "three"};
     * container.setParameter("id", "100").setParameter("name", "Peter").setParameter("amount", "555.43");
     * </pre>
     *
     * @param key the parameter name
     * @param value the parameter values
     * @return this MockContainer instance
     */
    public MockContainer setParameter(String key, String[] value) {
        if (!started) {
            throw new IllegalStateException("Container has not been started yet. Call start() first.");
        }
        getRequest().setParameter(key, value);

        return this;
    }

    /**
     * Convenience method for setting files to be uploaded.
     * <p/>
     * <b>Note</b> this method returns <tt>this</tt> so you can easily chain
     * calls to this method.
     * <p/>
     * For example:
     * <pre class="prettyprint">
     * container.setParameter("helpfile", new File("c:/help.pdf"), "application/pdf").setParameter("toc", new File("c:/toc.html"),"text/html");
     * </pre>
     *
     * @param fieldName the name of the upload field.
     * @param file the file to upload
     * @param contentType content type of the file
     * @return this MockContainer instance
     */
    public MockContainer setParameter(String fieldName, File file, String contentType) {
        if (!started) {
            throw new IllegalStateException("Container has not been started yet. Call start() first.");
        }
        getRequest().setUseMultiPartContentType(true);
        getRequest().addFile(fieldName, file, contentType);
        return this;
    }

    /**
     * This method simulates a browser requesting (GET) or submitting (POST)
     * the url associated with the specified pageClass and parameters.
     *
     * @see #testPage(Class)
     *
     * @param pageClass specifies the class of the Page to test
     * @param parameters the request parameters
     * @return the Page instance for the specified pageClass
     */
    public <T extends Page> Page testPage(Class<T> pageClass, Map<?, ?> parameters) {
        if (pageClass == null) {
            throw new IllegalArgumentException("pageClass cannot be null");
        }
        if (parameters == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        for (Entry<?, ?> entry : parameters.entrySet()) {
            setParameter(String.valueOf(entry.getKey()),
                String.valueOf(entry.getValue()));
        }
        return testPage(pageClass);
    }

    /**
     * This method simulates a browser requesting (GET) or submitting (POST)
     * the url associated with the specified pageClass and request parameters.
     * <p/>
     * The container forwards the request to {@link org.apache.click.ClickServlet}
     * for processing and returns the Page instance that was created.
     *
     * @param pageClass specifies the class of the Page to test
     * @return the Page instance for the specified pageClass
     */
    @SuppressWarnings("unchecked")
    public <T extends Page> T testPage(Class<T> pageClass) {
        if (!started) {
            throw new IllegalStateException("Container has not been started yet. Call start() first.");
        }
        if (pageClass == null) {
            throw new IllegalArgumentException("pageClass cannot be null");
        }

        try {
            // Reset internal state before test
            reset();

            String servletPath = getClickServlet().getConfigService().getPagePath(pageClass);
            if (servletPath == null) {
                throw new IllegalArgumentException("The class " + pageClass.getName()
                    + " was not mapped by Click and does not have a"
                    + " corresponding template file.");
            }

            getRequest().setServletPath(servletPath);
            getClickServlet().service(request, getResponse());
            return (T) getPage();

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CleanRuntimeException("MockContainer threw exception", ex);
        }
    }

    /**
     * This method simulates a browser requesting (GET) or submitting (POST)
     * the specified path.
     * <p/>
     * <b>Note:</b> the path must have a leading slash '/' for example '/test.htm'.
     * If the path does not begin with the '/' character it will automatically
     * be added.
     *
     * @see #testPage(Class)
     *
     * @param path the page path
     * @return a new Page instance for the specified path
     */
    public Page testPage(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        path = appendLeadingSlash(path);
        Class<? extends Page> pageClass = getClickServlet().getConfigService().getPageClass(path);
        return testPage(pageClass);
    }

    /**
     * This method simulates a browser requesting (GET) or submitting (POST)
     * the specified path and request parameters.
     * <p/>
     * <b>Note:</b> the path must have a leading slash '/' for example '/test.htm'.
     * If the path does not begin with the '/' character it will automatically
     * be added.
     *
     * @see #testPage(Class)
     *
     * @param path the page path
     * @param parameters the request parameters to set
     *
     * @return a new Page instance for the specified path
     */
    public Page testPage(String path, Map<?, ?> parameters) {
        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }
        path = appendLeadingSlash(path);
        Class<? extends Page> pageClass = getClickServlet().getConfigService().getPageClass(path);
        return testPage(pageClass, parameters);
    }

    /**
     * Returns the html output that was generated by
     * {@link javax.servlet.http.HttpServletResponse}.
     * <p/>
     * <b>Please note:</b> if the <tt>Page</tt> invokes
     * {@link org.apache.click.Page#setForward(Class)} or
     * {@link org.apache.click.Page#setRedirect(Class)}, this method will
     * return blank.
     * </p/>
     * The reason for this is that <tt>forward</tt> and <tt>redirect</tt> calls
     * are only recorded, <b>not</b> executed.
     * <p/>
     * The forward and redirect path's are only used for assertion purposes.
     * <p/>
     * JSP templates is not supported by this method because a JSP template
     * is always accessed through a {@link org.apache.click.Page#setForward(Class)}
     * call.
     *
     * @return the rendered html document
     */
    public String getHtml() {
        return getResponse().getDocument();
    }

    /**
     * Return the forward or redirect url as set by the Page.
     * <p/>
     * <b>Note:</b> redirect url's inside this application will have their
     * context path removed. This ensures that a forward or redirect will have
     * the same value for the same url.
     *
     * @return either forward or redirect value.
     */
    public String getForwardOrRedirectUrl() {
        String forward = getRequest().getForward();

        if (forward != null) {
            return forward;
        }

        String redirect = removeContextPath(getResponse().getRedirectUrl());

        return redirect;
    }

    /**
     * Return the path that {@link org.apache.click.Page} forwarded to.
     *
     * @return the path that Page forwarded to
     */
    public String getForward() {
        return getRequest().getForward();
    }

    /**
     * Return the Class that {@link org.apache.click.Page} forwarded to.
     *
     * @return the class that Page forwarded to
     */
    public Class<? extends Page> getForwardPageClass() {
        if (Context.getContextStack().isEmpty()) {
            return null;
        }
        if (getRequest().getForward() == null) {
            return null;
        }
        Context context = Context.getThreadLocalContext();
        return context.getPageClass(getRequest().getForward());
    }

    /**
     * Return the path that {@link org.apache.click.Page} redirected to.
     *
     * @return the path that Page redirected to
     */
    public String getRedirect() {
        return getResponse().getRedirectUrl();
    }

    /**
     * Return the Class that {@link org.apache.click.Page} redirected to.
     *
     * @return the Class that Page redirected to
     */
    public Class<? extends Page> getRedirectPageClass() {
        if (Context.getContextStack().isEmpty()) {
            return null;
        }
        if (getResponse().getRedirectUrl() == null) {
            return null;
        }
        Context context = Context.getThreadLocalContext();
        String redirect = removeContextPath(getResponse().getRedirectUrl());
        return context.getPageClass(redirect);
    }

    /**
     * Find and return the MockRequest from the request stack.
     *
     * @param request the servlet request
     * @return the mock request
     */
    public static MockRequest findMockRequest(ServletRequest request) {
        while (!(request instanceof MockRequest)
            && request instanceof HttpServletRequestWrapper && request != null) {
            request = ((HttpServletRequestWrapper) request).getRequest();
        }

        if (request instanceof MockRequest) {
            return (MockRequest) request;
        } else {
            throw new IllegalStateException("A MockRequest is not present in "
                + "the request stack. To use the mock package you must use "
                + "a MockRequest.");
        }
    }

    // Package private methods ------------------------------------------------

    /**
     * Reset container internal state.
     *
     * @see #start()
     */
    void reset() {
        Context.getContextStack().clear();
        HttpServletResponse response = getResponse();
        if (response != null) {
            response.reset();
        }
        ActionEventDispatcher.getDispatcherStack().clear();
        ControlRegistry.getRegistryStack().clear();
    }

    /**
     * Configure the container by instantiating the needed mock objects to
     * simulate a complete servlet environment.
     */
    void configure() {
        try {
            if (getServletContext() == null) {
                setServletContext(new MockServletContext());
            }

            getServletContext().setWebappPath(webappPath);

            if (getServletConfig() == null) {
                String servletName = "click-servlet";
                setServletConfig(new MockServletConfig(servletName,
                    getServletContext()));
            }

            if (getClickServlet() == null) {
                this.setClickServlet(new ClickServlet());
            }

            getClickServlet().init(getServletConfig());

            if (getSession() == null) {
                setSession(new MockSession(getServletContext()));
            }

            if (getResponse() == null) {
                setResponse(new MockResponse());
            }

            if (locale == null) {
                locale = Locale.getDefault();
            }
            if (getRequest() == null) {
                setRequest(new MockRequest(locale, getServletContext(),
                    getSession()));
            }
            getRequest().setAttribute(ClickServlet.MOCK_MODE_ENABLED, Boolean.TRUE);
            getServletContext().setAttribute(ClickServlet.MOCK_MODE_ENABLED,
                Boolean.TRUE);

        } catch (Exception e) {
            throw new CleanRuntimeException(e);
        }
    }

    /**
     * Appends a leading '/' character to the path unless the path already
     * begins with a '/'.
     *
     * @param path the path to append a '/' character
     * @return the path with a leading '/' character
     */
    String appendLeadingSlash(String path) {
        if (path.charAt(0) == '/') {
            return path;
        }
        return '/' + path;
    }

    /**
     * A RuntimeException that only prints the original cause, instead of
     * printing nested stackTraces.
     */
    static class CleanRuntimeException extends RuntimeException {

        /** Serialization version indicator. */
        private static final long serialVersionUID = 1L;

        /**
         * Default constructor.
         */
        public CleanRuntimeException() {
        }

        /**
         * Construct an exception with the given message.
         *
         * @param message exception message
         */
        public CleanRuntimeException(String message) {
            super(message);
        }

        /**
         * Construct an exception for the given cause.
         *
         * @param cause the cause of this exception
         */
        public CleanRuntimeException(Throwable cause) {
            super(cause);
        }

        /**
         * Construct an exception for the given cause.
         *
         * @param message exception message
         * @param cause the cause of this exception
         */
        public CleanRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @see java.lang.Throwable#getLocalizedMessage()
         *
         * @return localized description of this exception
         */
        @Override
        public String getLocalizedMessage() {
            if (getCause() == null) {
                return super.getLocalizedMessage();
            }
            if (super.getMessage() == null) {
                return getCause().getLocalizedMessage();
            }
            return super.getLocalizedMessage();
        }

        /**
         * @see java.lang.Throwable#getMessage()
         *
         * @return the exception error message
         */
        @Override
        public String getMessage() {
            if (getCause() == null) {
                return super.getMessage();
            }
            if (super.getMessage() == null) {
                return getCause().getMessage();
            }
            return super.getMessage();
        }

        /**
         * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
         *
         * @param printStream the PrintStream to print to
         */
        @Override
        public void printStackTrace(PrintStream printStream) {
            synchronized (printStream) {
                if (getCause() == null) {
                    super.printStackTrace(printStream);
                } else {
                    getCause().printStackTrace(printStream);
                }
            }
        }

        /**
         * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
         *
         * @param printWriter the PrintWriter to print to
         */
        @Override
        public void printStackTrace(PrintWriter printWriter) {
            synchronized (printWriter) {
                if (getCause() == null) {
                    super.printStackTrace(printWriter);
                } else {
                    getCause().printStackTrace(printWriter);
                }
            }
        }

        /**
         * @see java.lang.Throwable#fillInStackTrace()
         *
         * @return a reference to either the underlying cause (if its defined)
         * or this Throwable instance.
         */
        @Override
        public synchronized Throwable fillInStackTrace() {
            if (getCause() == null) {
                return this;
            }
            return getCause().fillInStackTrace();
        }
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Return the Page instance of the most recent test run.
     *
     * @return the Page instance of the most recent test run
     */
    private Page getPage() {
        return (Page) getRequest().getAttribute(ClickServlet.MOCK_PAGE_REFERENCE);
    }

    /**
     * Removes the context path from the specified value.
     *
     * @param value the value to remove context path from
     * @return the value without context path
     */
    private String removeContextPath(String value) {
        if (value != null
            && value.startsWith(getRequest().getContextPath())) {
            value = value.substring(value.indexOf('/', 1));
        }
        return value;
    }
}
