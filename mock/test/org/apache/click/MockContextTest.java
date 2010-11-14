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

import junit.framework.TestCase;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.Submit;
import org.apache.click.servlet.MockServletConfig;
import org.apache.click.servlet.MockServletContext;
import org.apache.click.servlet.MockRequest;
import org.apache.click.servlet.MockResponse;

/**
 * MockContext tests.
 */
public class MockContextTest extends TestCase {

    // Variables --------------------------------------------------------------

    /** Indicates that an actionListener was invoked. */
    private boolean submitCalled = false;

    /** Indicators that behavior events was invoked. */
    private boolean preRenderHeadElementsCalled = false;
    private boolean preResponseCalled = false;
    private boolean preDestroyCalled = false;

    /**
     * Test MockContext.
     */
    public void testContext() {
        MockServletContext servletContext = new MockServletContext();
        MockContext.initContext(new MockServletConfig(servletContext), 
            new MockRequest(), new MockResponse(), new ClickServlet());
    }

    // Test ActionListeners ---------------------------------------------------

    /**
     * Test that context can properly fire action listeners.
     */
    public void testFireActionListeners() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("save", "save");

        submitCalled = false;
        Submit submit = new Submit("save");
        // Registry a listener which must be invoked
        submit.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                // When action is invoked, set flag to true
                return submitCalled = true;
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is only one event listener and event source registered
        assertEquals(1, dispatcher.getEventListenerList().size());
        assertEquals(1, dispatcher.getEventSourceList().size());

        // Fire all action events that was registered in the onProcess method
        context.executeActionListeners();

        assertTrue("Submit action was not invoked", submitCalled);

        // Assert that after invoking executeActionListeners there are no event
        // listeners and event sources registered
        assertEquals(0, dispatcher.getEventListenerList().size());
        assertEquals(0, dispatcher.getEventSourceList().size());
    }

    /**
     * Test that registered action listeners are removed through the method
     * MockContext.reset().
     */
    public void testResetActionListeners() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");
        // Registry a listener which must be invoked
        submit.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                // When action is invoked, set flag to true
                return true;
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is only one event listener and event source registered
        assertEquals(1, dispatcher.getEventListenerList().size());
        assertEquals(1, dispatcher.getEventSourceList().size());

        // Context reset should clear the dispatcher
        context.reset();

        // Assert there are no event listener and event source registered after invoking reset
        assertEquals(0, dispatcher.getEventListenerList().size());
        assertEquals(0, dispatcher.getEventSourceList().size());
    }

    // Test Behaviors ---------------------------------------------------------

    /**
     * Test that context can properly fire behaviors.
     */
    public void testFireBehaviors() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("save", "save");

        submitCalled = false;

        Submit submit = new Submit("save");
        // Register an ajax behavior
        submit.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                // When action is invoked, set flag to true
                submitCalled = true;
                return new ActionResult();
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is one behavior registered
        assertEquals(1, dispatcher.getAjaxBehaviorSourceSet().size());

        // Fire all behaviors registered in the onProcess method
        context.executeBehaviors();

        assertTrue("Submit behavior was not invoked", submitCalled);

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, dispatcher.getAjaxBehaviorSourceSet().size());
    }

    /**
     * Test that registered behaviors are removed through the method
     * MockContext.reset().
     */
    public void testResetAjaxBehaviors() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");
        // Register an ajax behavior
        submit.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                // When action is invoked, set flag to true
                return new ActionResult();
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is one behavior registered
        assertEquals(1, dispatcher.getAjaxBehaviorSourceSet().size());

        // Context reset should clear the dispatcher
        context.reset();

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, dispatcher.getAjaxBehaviorSourceSet().size());
    }

    // Test Behavior Interceptor Methods --------------------------------------

    /**
     * Test that context can properly process Behavior interceptor methods.
     */
    public void testProcessBehaviorInterceptorMethods() {
        MockContext context = MockContext.initContext();

        Submit submit = new Submit("save");

        preRenderHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        ControlRegistry.registerInterceptor(submit, new Behavior() {

            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            public void preRenderHeadElements(Control source) {
                preRenderHeadElementsCalled = true;
            }

            public void preResponse(Control source) {
                preResponseCalled = true;
            }
        });

        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        // Assert there is one interceptor registered
        assertEquals(1, registry.getInterceptors().size());

        // Process the preResponse interceptor methods
        context.executePreResponse();
        assertTrue("preResponse was not processed", preResponseCalled);

        // Process the preRenderHeadElements interceptor methods
        context.executePreRenderHeadElements();
        assertTrue("preRenderHeadElements was not processed", preRenderHeadElementsCalled);

        // Process the preRenderHeadElements interceptor methods
        context.executePreDestroy();
        assertTrue("preDestroy was not processed", preDestroyCalled);

        // Assert that the behavior was not removed after all events was processed
        // The reason the behaviors are not automatically removed is because the
        // last behavior is onDestroy, which is right before the request goes out
        // of scope anyway
        assertEquals(1, registry.getInterceptors().size());
    }

    /**
     * Test that context can properly fire and reset behavior interceptors.
     */
    public void testResetControlInterceptors() {
        MockContext context = MockContext.initContext();

        Submit submit = new Submit("save");

        preRenderHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        ControlRegistry.registerInterceptor(submit, new Behavior() {

            public boolean isRequestTarget(Context context) {
                return false;
            }

            public ActionResult onAction(Control source) {
                return null;
            }

            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            public void preRenderHeadElements(Control source) {
                preRenderHeadElementsCalled = true;
            }

            public void preResponse(Control source) {
                preResponseCalled = true;
            }
        });

        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        // Assert there is one interceptor registered
        assertEquals(1, registry.getInterceptors().size());

        // Context reset should clear the dispatcher
        context.reset();

        // Assert that the interceptor was not removed after all events was processed
        // The reason the interceptor are not automatically removed is because the
        // last interceptor is onDestroy, which is right before the request goes out
        // of scope anyway
        assertEquals(0, registry.getInterceptors().size());
    }

    // Behavior tests ---------------------------------------------------------

    /**
     * Test that context can properly process behaviors.
     */
    public void testProcessBehaviors() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");

        submitCalled = false;
        preRenderHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        submit.addBehavior(new DefaultAjaxBehavior() {

            @Override
            public ActionResult onAction(Control source) {
                // When action is invoked, set flag to true
                submitCalled = true;
                return new ActionResult();
            }

            @Override
            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            @Override
            public void preRenderHeadElements(Control source) {
                preRenderHeadElementsCalled = true;
            }

            @Override
            public void preResponse(Control source) {
                preResponseCalled = true;
            }
        });
        assertTrue(submit.onProcess());

        submit.onProcess();

        ActionEventDispatcher eventDispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is one behavior registered
        assertEquals(1, eventDispatcher.getAjaxBehaviorSourceSet().size());

        // Fire all behaviors registered in the onProcess method
        context.executeBehaviors();

        assertTrue("Submit behavior was not invoked", submitCalled);

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, eventDispatcher.getAjaxBehaviorSourceSet().size());

        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        // Assert that the submit control is registered as an AJAX target
        assertEquals(1, registry.getAjaxTargetControls().size());
        assertSame(submit, registry.getAjaxTargetControls().iterator().next());

        // Process the preResponse interceptor methods
        context.executePreResponse();
        assertTrue("preResponse was not processed", preResponseCalled);

        // Process the preRenderHeadElements interceptor methods
        context.executePreRenderHeadElements();
        assertTrue("preRenderHeadElements was not processed", preRenderHeadElementsCalled);

        // Process the preRenderHeadElements interceptor methods
        context.executePreDestroy();
        assertTrue("preDestroy was not processed", preDestroyCalled);

        // Assert that the behaviors was not removed after all events was processed
        assertEquals(1, registry.getAjaxTargetControls().size());

        // Test that reset will clear the ControlRegistry
        context.reset();

        // Assert that the behavior was removed after reset
        assertEquals(0, registry.getAjaxTargetControls().size());
    }

    /**
     * Test that initContext removes previous Contexts from the ThreadLocal.
     */
    public void testInitContextCleanup() {
        MockContext.initContext();
        assertEquals(1, Context.getContextStack().size());
        assertEquals(1, ActionEventDispatcher.getDispatcherStack().size());
        assertEquals(1, ControlRegistry.getRegistryStack().size());

        MockContext.initContext();
        assertEquals(1, Context.getContextStack().size());
        assertEquals(1, ActionEventDispatcher.getDispatcherStack().size());
        assertEquals(1, ControlRegistry.getRegistryStack().size());
        
    }
}
