/*
 * Copyright 2004-2008 Malcolm A. Edgar
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

import net.sf.click.servlet.MockServletConfig;
import net.sf.click.servlet.MockResponse;
import net.sf.click.servlet.MockRequest;
import java.util.Locale;
import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.click.servlet.MockServletContext;
import net.sf.click.servlet.MockSession;

/**
 * Provides a mock {@link net.sf.click.Context} object for unit testing.
 * <p/>
 * <b>Note:</b> if you want to test your Click Page instances use
 * {@link MockContainer} instead.
 * <p/>
 * This class defines a couple of helper methods to quickly create all the mock
 * objects needed for unit testing. Please see the following methods:
 * <ul>
 *   <li>{@link #initContext()}</li>
 *   <li>{@link #initContext(Locale)}</li>
 *   <li>{@link #initContext(String)}</li>
 *   <li>{@link #initContext(Locale, String)}</li>
 *   <li>{@link #initContext(MockServletConfig, MockRequest, MockResponse, MockClickServlet)}</li>
 * </ul>
 * To use this class in your own tests invoke one of the methods above.
 * For example:
 * <pre class="prettyprint">
 * public class FormTest extends TestCase {
 *     // Create a mock context
 *     MockContext context = (MockContext) MockContext.initContext("test-form.htm");
 *     MockRequest request = (MockRequest) context.getMockRequest();
 *
 *     // The request value that should be set as the textField value
 *     String requestValue = "one";
 *
 *     // Set form name and field name parameters
 *     request.setParameter("form_name", "form");
 *     request.setParameter("name", requestValue);
 *
 *     // Create form and fields
 *     Form form = new Form("form");
 *     TextField nameField = new TextField("name");
 *     form.add(nameField);
 *
 *     // Check that nameField value is null
 *     Assert.assertNull(nameField.getValueObject());
 *
 *     // Simulate a form onProcess callback
 *     form.onProcess();
 *
 *     // Check that nameField value is now bound to request value
 *     Assert.assertEquals(nameField.getValueObject(), requestValue);
 * }
 * </pre>
 *
 * @author Bob Schellink
 */
public class MockContext extends Context {

    private MockClickServlet mockClickServlet;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new MockContext instance for the specified request.
     *
     * @deprecated use the other constructor instead.
     *
     * @param request the servlet request
     */
    MockContext(HttpServletRequest request) {
        super(request, null);
    }

    /**
     * Create a new MockContext instance for the specified Mock objects.
     *
     * @param servletConfig the mock servletConfig
     * @param request the mock request
     * @param response the mock response
     * @param isPost specified if this a POST or GET request
     * @param clickServlet the mock clickServlet
     */
    MockContext(ServletConfig servletConfig, HttpServletRequest request,
        HttpServletResponse response, boolean isPost, ClickServlet clickServlet) {
        super(servletConfig == null ? null : servletConfig.getServletContext(),
            servletConfig, request, response, isPost, clickServlet);
    }

    // --------------------------------------------------------- Public getters and setters

    /**
     * Return the mock {@link net.sf.click.MockClickServlet} instance for this
     * context.
     *
     * @return the mockClickServlet instance
     */
    public MockClickServlet getServlet() {
        return mockClickServlet;
    }

    /**
     * Sets the {@link net.sf.click.MockClickServlet} instance for this context.
     *
     * @param mockClickServlet the specified mockClickServlet to set
     */
    public void setServlet(MockClickServlet mockClickServlet) {
        this.mockClickServlet = mockClickServlet;
    }

    /**
     * Returns the application mode the test is running in.
     *
     * @see net.sf.click.Context#getApplicationMode()
     */
    public String getApplicationMode() {
        return mockClickServlet.getClickApp().getModeValue();
    }

    /**
     * Return the {@link net.sf.click.servlet.MockRequest} instance for this
     * context.
     *
     * @return the MockRequest instance
     */
    public MockRequest getMockRequest() {
        return MockContainer.findMockRequest(request);
    }

    // -------------------------------------------------------- Public methods

    /**
     * Creates and returns a new Context instance.
     *<p/>
     * <b>Note:</b> servletPath will default to 'mock.htm'.
     *
     * @return new Context instance
     */
    public static MockContext initContext() {
        return initContext("mock.htm");
    }

    /**
     * Creates and returns a new Context instance for the specified servletPath.
     *
     * @param servletPath the requests servletPath
     * @return new Context instance
     */
    public static MockContext initContext(String servletPath) {
        return initContext(Locale.getDefault(), servletPath);
    }

    /**
     * Creates and returns a new Context instance for the specified locale.
     *
     * <b>Note:</b> servletPath will default to 'mock.htm'.
     *
     * @param locale the requests locale
     * @return new Context instance
     */
    public static MockContext initContext(Locale locale) {
        return initContext(locale, "mock.htm");
    }

    /**
     * Creates and returns new Context instance for the specified request.
     *
     * @deprecated use one of the other initContext methods because those will
     * construct a complete mock stack including a MockRequest.
     *
     * @return new Context instance
     */
    public static MockContext initContext(HttpServletRequest request) {
        MockContext mockContext = new MockContext(request);
        Context.pushThreadLocalContext(mockContext);
        return (MockContext) Context.getThreadLocalContext();
    }

    /**
     * Creates and returns a new Context instance for the specified locale and
     * servletPath.
     *
     * @param locale the requets locale
     * @param servletPath the requests servletPath
     * @return new Context instance
     */
    public static MockContext initContext(Locale locale, String servletPath) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null");
        }
        MockServletContext servletContext = new MockServletContext();
        String servletName = "click-servlet";
        MockServletConfig servletConfig = new MockServletConfig(servletName,
            servletContext);

        MockClickServlet servlet = new MockClickServlet();

        MockResponse response = new MockResponse();

        MockSession session = new MockSession(servletContext);

        MockRequest request = new MockRequest(locale, null, servletPath, servletContext,
            session);

        return initContext(servletConfig, request, response, servlet);
    }

    /**
     * Creates and returns a new Context instance for the specified mock
     * objects.
     *
     * @param servletConfig the mock servletConfig
     * @param request the mock request
     * @param response the mock response
     * @param clickServlet the mock clickServlet
     * @return new Context instance
     */
    public static MockContext initContext(MockServletConfig servletConfig,
        MockRequest request, MockResponse response, MockClickServlet clickServlet) {

        //Sanity checks
        if (servletConfig == null) {
            throw new IllegalArgumentException("ServletConfig cannot be null");
        }
        if (servletConfig.getServletContext() == null) {
            throw new IllegalArgumentException("ServletConfig.getServletContext() cannot return null");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        if (clickServlet == null) {
            throw new IllegalArgumentException("ClickServlet cannot be null");
        }

        boolean isPost = true;
        if (request != null) {
            isPost = request.getMethod().equalsIgnoreCase("POST");
        }

        clickServlet.init(servletConfig);

        MockContext mockContext = new MockContext(servletConfig, request,
            response, isPost, clickServlet);
        mockContext.setServlet(clickServlet);
        Context.pushThreadLocalContext(mockContext);
        return (MockContext) Context.getThreadLocalContext();
    }
}
