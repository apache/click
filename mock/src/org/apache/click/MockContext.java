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

import org.apache.click.servlet.MockServletConfig;
import org.apache.click.servlet.MockResponse;
import org.apache.click.servlet.MockRequest;
import java.util.Locale;
import javax.servlet.ServletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.click.service.ConfigService;
import org.apache.click.service.ConsoleLogService;
import org.apache.click.servlet.MockServletContext;
import org.apache.click.servlet.MockSession;
import org.apache.click.util.ClickUtils;

/**
 * Provides a mock {@link org.apache.click.Context} object for unit testing.
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
 *   <li>{@link #initContext(MockServletConfig, MockRequest, MockResponse, ClickServlet)}</li>
 * </ul>
 * To use this class in your own tests invoke one of the methods above.
 * For example:
 * <pre class="prettyprint">
 * public class FormTest extends TestCase {
 *     // Create a mock context
 *     MockContext context = MockContext.initContext("test-form.htm");
 *     MockRequest request = context.getMockRequest();
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
 *     // Simulate a form onProcess event
 *     form.onProcess();
 *
 *     // Check that nameField value is now bound to request value
 *     Assert.assertEquals(requestValue, nameField.getValueObject());
 * } </pre>
 *
 * <b>Please note:</b> using MockContext to run performance tests over a large
 * number of Controls could lead to <tt>out of memory</tt> errors. If you run
 * into memory issues, you can either re-recreate a MockContext or invoke
 * {@link #reset()}, which removes all references to Controls,
 * ActionListeners and Behaviors.
 */
public class MockContext extends Context {

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
     * Return the mock {@link org.apache.click.ClickServlet} instance for this
     * context.
     *
     * @return the clickServlet instance
     */
    public ClickServlet getServlet() {
        return clickServlet;
    }

    /**
     * Return the {@link org.apache.click.servlet.MockRequest} instance for this
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
     * <b>Note:</b> servletPath will default to '/mock.htm'.
     *
     * @return new Context instance
     */
    public static MockContext initContext() {
        return initContext("/mock.htm");
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
     * <b>Note:</b> servletPath will default to '/mock.htm'.
     *
     * @param locale the requests locale
     * @return new Context instance
     */
    public static MockContext initContext(Locale locale) {
        return initContext(locale, "/mock.htm");
    }

    /**
     * Creates and returns new Context instance for the specified request.
     *
     * @deprecated use one of the other initContext methods because those will
     * construct a complete mock stack including a MockRequest.
     *
     * @param request the mock request
     * @return new Context instance
     */
    public static MockContext initContext(HttpServletRequest request) {
        MockContext mockContext = new MockContext(request);

        // Remove lingering ThreadLocal variables of the Mock stack
        mockContext.cleanup();

        Context.pushThreadLocalContext(mockContext);
        return (MockContext) Context.getThreadLocalContext();
    }

    /**
     * Creates and returns a new Context instance for the specified locale and
     * servletPath.
     *
     * @param locale the requests locale
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

        ClickServlet servlet = new ClickServlet();

        MockResponse response = new MockResponse();

        MockSession session = new MockSession(servletContext);

        MockRequest request = new MockRequest(locale, MockServletContext.DEFAULT_CONTEXT_PATH,
            servletPath, servletContext, session);

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
        MockRequest request, MockResponse response, ClickServlet clickServlet) {

        return initContext(servletConfig, request, response, clickServlet,
            null, null);
    }

    /**
     * Creates and returns a new Context instance for the specified mock
     * objects.
     *
     * @param servletConfig the mock servletConfig
     * @param request the mock request
     * @param response the mock response
     * @param clickServlet the mock clickServlet
     * @param actionEventDispatcher action and behavior dispatcher
     * @param controlRegistry the control registry
     * @return new Context instance
     */
    public static MockContext initContext(MockServletConfig servletConfig,
        MockRequest request, MockResponse response, ClickServlet clickServlet,
        ActionEventDispatcher actionEventDispatcher, ControlRegistry controlRegistry) {

        try {
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

            boolean isPost = request.getMethod().equalsIgnoreCase("POST");

            MockServletContext servletContext =
                (MockServletContext) servletConfig.getServletContext();

            servletContext.setAttribute(ClickServlet.MOCK_MODE_ENABLED, Boolean.TRUE);
            request.setAttribute(ClickServlet.MOCK_MODE_ENABLED, Boolean.TRUE);

            clickServlet.init(servletConfig);

            ConfigService configService = clickServlet.getConfigService();
            if (configService == null) {
                throw new IllegalArgumentException("ClickServlet.getConfigService() cannot return null");
            }

            MockContext mockContext = new MockContext(servletConfig, request,
                response, isPost, clickServlet);

            if (actionEventDispatcher == null) {
                actionEventDispatcher = new ActionEventDispatcher(configService);
            }

            if (controlRegistry == null) {
                controlRegistry = new ControlRegistry(configService);
            }

            // Remove lingering ThreadLocal variables of the Mock stack
        mockContext.cleanup();

            ActionEventDispatcher.pushThreadLocalDispatcher(actionEventDispatcher);
            ControlRegistry.pushThreadLocalRegistry(controlRegistry);
            Context.pushThreadLocalContext(mockContext);

            if (ClickUtils.getLogService() instanceof ConsoleLogService) {
                ConsoleLogService logService = (ConsoleLogService) ClickUtils.getLogService();
                logService.setLevel(ConsoleLogService.TRACE_LEVEL);
            }
            return (MockContext) Context.getThreadLocalContext();
        } catch (Exception e) {
            throw new MockContainer.CleanRuntimeException(e);
        }
    }

    /**
     * Execute all listeners that was registered by processed Controls.
     *
     * @return true if all listeners returned true, false otherwise
     */
    public boolean executeActionListeners() {
        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Fire action events
        return dispatcher.fireActionEvents(this);
    }

    /**
     * Execute all behaviors that was registered by processed Controls.
     *
     * @return true if all behaviors returned true, false otherwise
     */
    public boolean executeBehaviors() {
        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Fire behaviors
        return dispatcher.fireAjaxBehaviors(this);
    }

    /**
     * Execute the preResponse method for all registered behaviors.
     */
    public void executePreResponse() {
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        registry.processPreResponse(this);
    }

    /**
     * Execute the preRenderHeadElements method for all registered behaviors.
     */
    public void executePreRenderHeadElements() {
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        registry.processPreRenderHeadElements(this);
    }

    /**
     * Execute the preDestroy method for all registered behaviors.
     */
    public void executePreDestroy() {
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        registry.processPreDestroy(this);
    }

    /**
     * Fire all action events that was registered by the processed Controls.
     *
     * @deprecated use {@link #executeActionListeners()} instead
     *
     * @return true if all listeners returned true, false otherwise
     */
    public boolean fireActionEventsAndClearRegistry() {
        return executeActionListeners();
    }

    /**
     * Reset mock internal state. Running a large number of tests using the same
     * MockContext could lead to <tt>out of memory</tt> errors. Calling this
     * method will remove any references to objects, thus freeing up memory.
     */
    public void reset() {
        if (ControlRegistry.hasThreadLocalRegistry()) {
            ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();
            registry.clear();
        }

        if (ActionEventDispatcher.hasThreadLocalDispatcher()) {
            ActionEventDispatcher actionEventDispatcher = ActionEventDispatcher.getThreadLocalDispatcher();
            actionEventDispatcher.clear();
        }
    }

    /**
     * Cleanup the MockContext.
     * <p/>
     * This method removes any lingering ThreadLocal variables from the Mock stack.
     */
    void cleanup() {
        // Cleanup ThreadLocals
        Context.getContextStack().clear();
        ControlRegistry.getRegistryStack().clear();
        ActionEventDispatcher.getDispatcherStack().clear();
    }
}
