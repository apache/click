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
import org.apache.click.ajax.AjaxBehavior;
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

    /** Indicators that callback events was invoked. */
    private boolean preGetHeadElementsCalled = false;
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
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("save", "save");

        submitCalled = false;
        Submit submit = new Submit("save");
        // Registry a listener which must be invoked
        submit.setActionListener(new ActionListener() {
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
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");
        // Registry a listener which must be invoked
        submit.setActionListener(new ActionListener() {
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
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("save", "save");

        submitCalled = false;

        Submit submit = new Submit("save");
        // Register an ajax behavior
        submit.addBehavior(new AjaxBehavior() {
            public Partial onAction(Control source) {
                // When action is invoked, set flag to true
                submitCalled = true;
                return new Partial();
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is one behavior registered
        assertEquals(1, dispatcher.getBehaviorSourceSet().size());

        // Fire all behaviors registered in the onProcess method
        context.executeBehaviors();

        assertTrue("Submit behavior was not invoked", submitCalled);

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, dispatcher.getBehaviorSourceSet().size());
    }

    /**
     * Test that registered behaviors are removed through the method
     * MockContext.reset().
     */
    public void testResetBehaviors() {
        MockContext context = MockContext.initContext();
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");
        // Register an ajax behavior
        submit.addBehavior(new AjaxBehavior() {
            public Partial onAction(Control source) {
                // When action is invoked, set flag to true
                return new Partial();
            }
        });
        assertTrue(submit.onProcess());

        ActionEventDispatcher dispatcher = ActionEventDispatcher.getThreadLocalDispatcher();

        // Assert there is one behavior registered
        assertEquals(1, dispatcher.getBehaviorSourceSet().size());

        // Context reset should clear the dispatcher
        context.reset();

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, dispatcher.getBehaviorSourceSet().size());
    }

    // Test Callbacks ---------------------------------------------------------

    /**
     * Test that context can properly process callbacks.
     */
    public void testProcessCallbacks() {
        MockContext context = MockContext.initContext();

        Submit submit = new Submit("save");

        preGetHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        CallbackDispatcher.registerCallback(submit, new Callback() {

            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            public void preGetHeadElements(Control source) {
                preGetHeadElementsCalled = true;
            }

            public void preResponse(Control source) {
                preResponseCalled = true;
            }
        });

        CallbackDispatcher dispatcher = CallbackDispatcher.getThreadLocalDispatcher();

        // Assert there is one callback registered
        assertEquals(1, dispatcher.getCallbacks().size());

        // Process the preResponse callback event
        context.executePreResponseCallbackEvent();
        assertTrue("preResponse callback event was not processed", preResponseCalled);

        // Process the preGetHeadElements callback event
        context.executePreGetHeadElementsCallbackEvent();
        assertTrue("preGetHeadElements callback event was not processed", preGetHeadElementsCalled);

        // Process the preGetHeadElements callback event
        context.executePreDestroyCallbackEvent();
        assertTrue("preDestroy callback event was not processed", preDestroyCalled);

        // Assert that the callback was not removed after all events was processed
        // The reason the callbacks are not automatically removed is because the
        // last callback is onDestroy, which is right before the request goes out
        // of scope
        assertEquals(1, dispatcher.getCallbacks().size());
    }

    /**
     * Test that context can properly fire callbacks.
     */
    public void testResetCallbacks() {
        MockContext context = MockContext.initContext();

        Submit submit = new Submit("save");

        preGetHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        CallbackDispatcher.registerCallback(submit, new Callback() {

            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            public void preGetHeadElements(Control source) {
                preGetHeadElementsCalled = true;
            }

            public void preResponse(Control source) {
                preResponseCalled = true;
            }
        });

        CallbackDispatcher dispatcher = CallbackDispatcher.getThreadLocalDispatcher();

        // Assert there is one callback registered
        assertEquals(1, dispatcher.getCallbacks().size());

        // Context reset should clear the dispatcher
        context.reset();

        // Assert that the callback was not removed after all events was processed
        // The reason the callbacks are not automatically removed is because the
        // last callback is onDestroy, which is right before the request goes out
        // of scope
        assertEquals(0, dispatcher.getCallbacks().size());
    }

    // Callback + Behavior tests ----------------------------------------------

    /**
     * Test that context can properly process callbacks and behaviors.
     */
    public void testProcessBehaviorAndCallbacks() {
        MockContext context = MockContext.initContext();
        MockRequest request = (MockRequest) context.getMockRequest();
        request.setParameter("save", "save");

        Submit submit = new Submit("save");

        submitCalled = false;
        preGetHeadElementsCalled = false;
        preResponseCalled = false;
        preDestroyCalled = false;

        submit.addBehavior(new AjaxBehavior() {

            @Override
            public Partial onAction(Control source) {
                // When action is invoked, set flag to true
                submitCalled = true;
                return new Partial();
            }

            @Override
            public void preDestroy(Control source) {
                preDestroyCalled = true;
            }

            @Override
            public void preGetHeadElements(Control source) {
                preGetHeadElementsCalled = true;
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
        assertEquals(1, eventDispatcher.getBehaviorSourceSet().size());

        // Fire all behaviors registered in the onProcess method
        context.executeBehaviors();

        assertTrue("Submit behavior was not invoked", submitCalled);

        // Assert there are no behaviors registered after reset is invoked
        assertEquals(0, eventDispatcher.getBehaviorSourceSet().size());

        CallbackDispatcher callbackDispatcher = CallbackDispatcher.getThreadLocalDispatcher();

        // Assert that the submit control is registered as a callback
        assertEquals(1, callbackDispatcher.getBehaviorEnabledControls().size());
        assertSame(submit, callbackDispatcher.getBehaviorEnabledControls().iterator().next());

        // Process the preResponse callback event
        context.executePreResponseCallbackEvent();
        assertTrue("preResponse callback event was not processed", preResponseCalled);

        // Process the preGetHeadElements callback event
        context.executePreGetHeadElementsCallbackEvent();
        assertTrue("preGetHeadElements callback event was not processed", preGetHeadElementsCalled);

        // Process the preGetHeadElements callback event
        context.executePreDestroyCallbackEvent();
        assertTrue("preDestroy callback event was not processed", preDestroyCalled);

        // Assert that the callback was not removed after all events was processed
        assertEquals(1, callbackDispatcher.getBehaviorEnabledControls().size());

        // Test that reset will clear the callback dispatcher
        context.reset();

        // Assert that the callback was removed after reset
        assertEquals(0, callbackDispatcher.getBehaviorEnabledControls().size());
    }
}
