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

import java.util.ArrayList;

import java.util.List;
import org.apache.commons.lang.Validate;

/**
 * Provides a control ActionListener event dispatcher.
 * <p/>
 * <b>Please note:</b> this class is meant for component development and can be
 * ignored otherwise.
 * <p/>
 * When registering an ActionListener you can specify the callback to occur for
 * a specific event. For example ActionListeners can be registered to fire
 * <tt>after</tt> the <tt>onProcess</tt> event. This event can be specified
 * through the constant {@link #POST_ON_PROCESS_EVENT}.
 * <p/>
 * The ClickServlet will notify the ActionEventDispatcher which ActionListeners
 * to fire. For example, after the <tt>onProcess</tt> event, the ClickServlet
 * will notify the dispatcher to fire ActionListeners registered for the
 * {@link #POST_ON_PROCESS_EVENT} (this is the default event when listeners are
 * fired).
 * <p/>
 * Out of the box ActionEventDispatcher only supports the event
 * {@link #POST_ON_PROCESS_EVENT} (the default).
 *
 * <h4>Example Usage</h4>
 * The following example shows how to register an ActionListener with a custom
 * Control:
 *
 * <pre class="prettyprint">
 * public class MyLink extends AbstractControl {
 *     ...
 *
 *     public boolean onProcess() {
 *         bindRequestValue();
 *
 *         if (isClicked()) {
 *             // Dispatch an action listener event for invocation after
 *             // control processing has finished
 *             dispatchActionEvent();
 *         }
 *
 *         return true;
 *     }
 * } </pre>
 *
 * In this example if the link is clicked, it then calls
 * {@link org.apache.click.control.AbstractControl#dispatchActionEvent()}.
 * This method registers the Control's action listener with ActionEventDispatcher.
 * The ClickServlet will subsequently invoke the registered
 * {@link ActionListener#onAction(Control)} method after all the Page controls
 * <tt>onProcess()</tt> method have been invoked.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public class ActionEventDispatcher {

    // -------------------------------------------------------------- Constants

    /** The thread local dispatcher holder. */
    private static final ThreadLocal THREAD_LOCAL_DISPATCHER = new ThreadLocal();

    /**
     * Indicates the listener should fire <tt>AFTER</tt> the onProcess event.
     * The <tt>POST_ON_PROCESS_EVENT</tt> is the event during which control
     * listeners will fire.
     */
    public static final int POST_ON_PROCESS_EVENT = 300;

    // -------------------------------------------------------------- Variables

    /** The POST_PROCESS events holder. */
    private EventHolder postProcessEventHolder;

    // --------------------------------------------------------- Public Methods

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet once all the controls have been processed.
     * <p/>
     * Listeners registered by this method will be fired in the
     * {@link #POST_ON_PROCESS_EVENT}.
     *
     * @see #dispatchActionEvent(org.apache.click.Control, org.apache.click.ActionListener, int)
     *
     * @param source the action event source
     * @param listener the event action listener
     */
    public static void dispatchActionEvent(Control source, ActionListener listener) {
        dispatchActionEvent(source, listener, POST_ON_PROCESS_EVENT);
    }

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet in the specified event.
     *
     * @param source the action event source
     * @param listener the event action listener
     * @param event the specific event to trigger the action event
     */
    public static void dispatchActionEvent(Control source,
        ActionListener listener, int event) {

        Validate.notNull(source, "Null source parameter");
        Validate.notNull(listener, "Null listener parameter");

        ActionEventDispatcher instance = getThreadLocalDispatcher();
        EventHolder eventHolder = instance.getEventHolder(event);
        eventHolder.registerActionEvent(source, listener);
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Allow the dispatcher to handle the error that occurred.
     *
     * @param throwable the error which occurred during processing
     */
    protected void errorOccurred(Throwable throwable) {
        // Clear the POST_ON_PROCESS_EVENT control listeners from the dispatcher
        // Registered listeners from other phases must still be invoked
        getEventHolder(ActionEventDispatcher.POST_ON_PROCESS_EVENT).clear();
    }

    /**
     * Clear the event list.
     */
    protected void clearEvents() {
        getPostProcessEventHolder().clear();
    }

    /**
     * Return the thread local dispatcher instance.
     *
     * @return the thread local dispatcher instance.
     * @throws RuntimeException if a ActionEventDispatcher is not available on the
     * thread.
     */
    protected static ActionEventDispatcher getThreadLocalDispatcher() {
        return getDispatcherStack().peek();
    }

    /**
     * Fire the actions for the given listener list and event source list which
     * return true if the page should continue processing.
     * <p/>
     * This method will be passed the listener list and event source list
     * of a specific event e.g. {@link #POST_ON_PROCESS_EVENT}.
     * event.
     * <p/>
     * This method can be overridden if you need to customize the way events
     * are fired.
     *
     * @param context the request context
     * @param eventSourceList the list of source controls
     * @param eventListenerList the list of listeners to fire
     * @param event the specific event which events to fire
     *
     * @return true if the page should continue processing or false otherwise
     */
    protected boolean fireActionEvents(Context context, List eventSourceList,
        List eventListenerList, int event) {

        boolean continueProcessing = true;

        for (int i = 0, size = eventSourceList.size(); i < size; i++) {
            Control source = (Control) eventSourceList.get(0);
            ActionListener listener = (ActionListener) eventListenerList.get(0);

            // Pop the first entry in the list
            eventSourceList.remove(0);
            eventListenerList.remove(0);

            if (!fireActionEvent(context, source, listener, event)) {
                continueProcessing = false;
            }
        }

        return continueProcessing;
    }

    /**
     * Fire all the registered action events after the Page Controls have been
     * processed and return true if the page should continue processing.
     * <p/>
     * @see #fireActionEvents(org.apache.click.Context, int)
     *
     * @param context the request context
     *
     * @return true if the page should continue processing or false otherwise
     */
    protected boolean fireActionEvents(Context context) {
        return fireActionEvents(context, ActionEventDispatcher.POST_ON_PROCESS_EVENT);
    }

    /**
     * Fire all the registered action events for the specified event and return
     * true if the page should continue processing.
     *
     * @param context the request context
     * @param event the event which listeners to fire
     *
     * @return true if the page should continue processing or false otherwise
     */
    protected boolean fireActionEvents(Context context, int event) {
        EventHolder eventHolder = getEventHolder(event);
        return eventHolder.fireActionEvents(context);
    }

    /**
     * Fire the action for the given listener and event source which
     * return true if the page should continue processing.
     * <p/>
     * This method will be passed a listener and source of a specific event
     * e.g. {@link #POST_ON_PROCESS_EVENT}.
     * <p/>
     * This method can be overridden if you need to customize the way events
     * are fired.
     *
     * @param context the request context
     * @param source the source control
     * @param listener the listener to fire
     * @param event the specific event which events to fire
     *
     * @return true if the page should continue processing or false otherwise
     */
    protected boolean fireActionEvent(Context context, Control source,
        ActionListener listener, int event) {
        return listener.onAction(source);
    }

    /**
     * Return the EventHolder for the specified event.
     *
     * @param event the event which EventHolder to retrieve
     *
     * @return the EventHolder for the specified event
     */
    protected EventHolder getEventHolder(int event) {
        if (event == POST_ON_PROCESS_EVENT) {
            return getPostProcessEventHolder();
        } else {
            return null;
        }
    }

    /**
     * Create a new EventHolder instance.
     *
     * @param event the EventHolder's event
     * @return new EventHolder instance
     */
    protected EventHolder createEventHolder(int event) {
        return new EventHolder(event);
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Return the {@link #POST_ON_PROCESS_EVENT} {@link EventHolder}.
     *
     * @return the {@link #POST_ON_PROCESS_EVENT} {@link EventHolder}
     */
    EventHolder getPostProcessEventHolder() {
        if (postProcessEventHolder == null) {
            postProcessEventHolder = createEventHolder(POST_ON_PROCESS_EVENT);
        }
        return postProcessEventHolder;
    }

    /**
     * Adds the specified ActionEventDispatcher on top of the dispatcher stack.
     *
     * @param actionEventDispatcher the ActionEventDispatcher to add
     */
    static void pushThreadLocalDispatcher(ActionEventDispatcher actionEventDispatcher) {
        getDispatcherStack().push(actionEventDispatcher);
    }

    /**
     * Remove and return the actionEventDispatcher instance on top of the
     * dispatcher stack.
     *
     * @return the actionEventDispatcher instance on top of the dispatcher stack
     */
    static ActionEventDispatcher popThreadLocalDispatcher() {
        DispatcherStack dispatcherStack = getDispatcherStack();
        ActionEventDispatcher actionEventDispatcher = dispatcherStack.pop();

        if (dispatcherStack.isEmpty()) {
            THREAD_LOCAL_DISPATCHER.set(null);
        }

        return actionEventDispatcher;
    }

    /**
     * Return the stack data structure where ActionEventDispatchers are stored.
     *
     * @return stack data structure where ActionEventDispatcher are stored
     */
    static DispatcherStack getDispatcherStack() {
        DispatcherStack dispatcherStack = (DispatcherStack) THREAD_LOCAL_DISPATCHER.get();

        if (dispatcherStack == null) {
            dispatcherStack = new DispatcherStack(2);
            THREAD_LOCAL_DISPATCHER.set(dispatcherStack);
        }

        return dispatcherStack;
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * Holds the list of listeners and event sources.
     */
    public class EventHolder {

        /** The EventHolder's event. */
        protected int event;

        /** The list of registered event sources. */
        private List eventSourceList;

        /** The list of registered event listeners. */
        private List eventListenerList;

        // ------------------------------------------------------- Constructors

        /**
         * Create a new EventHolder for the given event.
         *
         * @param event the EventHolder's event
         */
        public EventHolder(int event) {
            this.event = event;
        }

        /**
         * Register the event source and event ActionListener to be fired in the
         * specified event.
         *
         * @param source the action event source
         * @param listener the event action listener
         */
        public void registerActionEvent(Control source, ActionListener listener) {
            Validate.notNull(source, "Null source parameter");
            Validate.notNull(listener, "Null listener parameter");

            getEventSourceList().add(source);
            getEventListenerList().add(listener);
        }

        /**
         * Checks if any Action Events have been registered.
         *
         * @return true if the dispatcher has any Action Events registered
         */
        public boolean hasActionEvents() {
            if (eventListenerList == null || eventListenerList.isEmpty()) {
                return false;
            }
            return true;
        }

        /**
         * Return the list of event listeners.
         *
         * @return list of event listeners
         */
        public List getEventListenerList() {
            if (eventListenerList == null) {
                eventListenerList = new ArrayList();
            }
            return eventListenerList;
        }

        /**
         * Return the list of event sources.
         *
         * @return list of event sources
         */
        public List getEventSourceList() {
            if (eventSourceList == null) {
                eventSourceList = new ArrayList();
            }
            return eventSourceList;
        }

        /**
         * Clear the events.
         */
        public void clear() {
            if (hasActionEvents()) {
                getEventSourceList().clear();
                getEventListenerList().clear();
            }
        }

        /**
         * Fire all the registered action events and return true if the page should
         * continue processing.
         *
         * @param context the page request context
         * @return true if the page should continue processing or false otherwise
         */
        public boolean fireActionEvents(Context context) {

            if (!hasActionEvents()) {
                return true;
            }

            return ActionEventDispatcher.this.fireActionEvents(context,
                getEventSourceList(), getEventListenerList(), event);
        }
    }

    /**
     * Provides an unsynchronized Stack.
     */
    static class DispatcherStack extends ArrayList {

        /** Serialization version indicator. */
        private static final long serialVersionUID = 1L;

        /**
         * Create a new DispatcherStack with the given initial capacity.
         *
         * @param initialCapacity specify initial capacity of this stack
         */
        private DispatcherStack(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Pushes the ActionEventDispatcher onto the top of this stack.
         *
         * @param actionEventDispatcher the ActionEventDispatcher to push onto this stack
         * @return the ActionEventDispatcher pushed on this stack
         */
        private ActionEventDispatcher push(ActionEventDispatcher actionEventDispatcher) {
            add(actionEventDispatcher);

            return actionEventDispatcher;
        }

        /**
         * Removes and return the ActionEventDispatcher at the top of this stack.
         *
         * @return the ActionEventDispatcher at the top of this stack
         */
        private ActionEventDispatcher pop() {
            ActionEventDispatcher actionEventDispatcher = peek();

            remove(size() - 1);

            return actionEventDispatcher;
        }

        /**
         * Looks at the ActionEventDispatcher at the top of this stack without
         * removing it.
         *
         * @return the ActionEventDispatcher at the top of this stack
         */
        private ActionEventDispatcher peek() {
            int length = size();

            if (length == 0) {
                String msg = "No ActionEventDispatcher available on ThreadLocal Dispatcher Stack";
                throw new RuntimeException(msg);
            }

            return (ActionEventDispatcher) get(length - 1);
        }
    }
}
