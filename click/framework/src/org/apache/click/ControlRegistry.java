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
 * Provides a thread local register for ActionListener callbacks.
 * <p/>
 * <b>Please note:</b> this class is meant for component development and can be
 * ignored otherwise.
 * <p/>
 * When registering an ActionListener you can specify the callback to occur for
 * a specific event. For example ActionListeners can be registered to fire
 * <tt>after</tt> the <tt>onProcess</tt> or <tt>onRender</tt> events. By default
 * ActionListeners will be fired <tt>after</tt> the <tt>onProcess</tt> event.
 * <p/>
 * The ClickServlet will notify the ControlRegistry which ActionListeners
 * to fire. For example, after the <tt>onProcess</tt> event, the ClickServlet
 * will notify the registry to fire ActionListeners registered for the
 * {@link #POST_ON_PROCESS_EVENT} (this is the default event when listeners are fired).
 * Similarly, after the <tt>onRender</tt> event, ClickServlet will notify the
 * registry to fire ActionListeners registered for the {@link #POST_ON_RENDER_EVENT}.
 * <p/>
 * Out of the box ControlRegistry supports the events {@link #POST_ON_PROCESS_EVENT}
 * (the default) and {@link #POST_ON_RENDER_EVENT}.
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
 *             // Register the control listener for invocation after
 *             // control processing has finished
 *             registerActionEvent();
 *         }
 *
 *         return true;
 *     }
 * } </pre>
 *
 * In this example if the link is clicked, it then calls
 * {@link org.apache.click.control.AbstractControl#registerActionEvent()}.
 * This method registers the Control's action listener with ControlRegistry.
 * The ClickServlet will subsequently invoke the registered
 * {@link ActionListener#onAction(Control)} method after all the Page controls
 * <tt>onProcess()</tt> method have been invoked.
 * <p/>
 * On rare occasions one need to manipulate a Control's state right before it
 * is rendered. The {@link #POST_ON_RENDER_EVENT} callback can be used for this
 * situation. For example:
 *
 * <pre class="prettyprint">
 * public class MyForm extends Form {
 *
 *     public MyForm() {
 *         init();
 *     }
 *
 *     public MyForm(String name) {
 *         super(name);
 *         init();
 *     }
 *
 *     private void init() {
 *         ActionListener listener = new ActionListener() {
 *             public boolean onAction(Control source {
 *                 // Add a hidden field to hold state for MyForm
 *                 add(new HiddenField("my-form-name", getName() + '_' + "myform"));
 *                 return true;
 *             }
 *         };
 *
 *         ControlRegistry.registerActionEvent(this, listener, ControlRegistry.POST_ON_RENDER_EVENT);
 *     }
 *
 *     ...
 *
 * } </pre>
 *
 * The above example fires the ActionListener <tt>after</tt> the <tt>onRender</tt>
 * event. This ensures a HiddenField is added right before the MyForm is
 * streamed to the browser.
 * <p/>
 * Registering the listener in MyForm constructor guarantees that the
 * listener will be registered even if MyForm is subclassed because the compiler
 * forces subclasses to invoke their super constructor.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public class ControlRegistry {

    // -------------------------------------------------------------- Constants

    /** The thread local registry holder. */
    private static final ThreadLocal THREAD_LOCAL_REGISTRY = new ThreadLocal();

    /**
     * Indicates the listener should fire <tt>AFTER</tt> the onProcess event.
     * The <tt>POST_ON_PROCESS_EVENT</tt> is the event during which control
     * listeners will fire.
     */
    public static final int POST_ON_PROCESS_EVENT = 1;

    /**
     * Indicates the listener should fire <tt>AFTER</tt> the onRender event.
     * Listeners fired in the <tt>POST_ON_RENDER_EVENT</tT> are
     * <tt>guaranteed</tt> to trigger, even when redirecting, forwarding or if
     * page processing is cancelled.
     */
    public static final int POST_ON_RENDER_EVENT = 2;

    // -------------------------------------------------------------- Variables

    /** The POST_PROCESS events holder. */
    private EventHolder postProcessEventHolder;

    /** The POST_RENDER events holder. */
    private EventHolder postRenderEventHolder;

    // --------------------------------------------------------- Public Methods

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet once all the controls have been processed.
     * <p/>
     * Listeners registered by this method will be fired in the
     * {@link #POST_ON_PROCESS_EVENT}.
     *
     * @see #registerActionEvent(org.apache.click.Control, org.apache.click.ActionListener, int)
     *
     * @param source the action event source
     * @param listener the event action listener
     */
    public static void registerActionEvent(Control source, ActionListener listener) {
        registerActionEvent(source, listener, POST_ON_PROCESS_EVENT);
    }

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet in the specified event.
     *
     * @param source the action event source
     * @param listener the event action listener
     * @param event the specific event to trigger the action event
     */
    public static void registerActionEvent(Control source,
        ActionListener listener, int event) {

        Validate.notNull(source, "Null source parameter");
        Validate.notNull(listener, "Null listener parameter");

        ControlRegistry instance = getThreadLocalRegistry();
        EventHolder eventHolder = instance.getEventHolder(event);
        eventHolder.registerActionEvent(source, listener);
    }

    // ------------------------------------------------ Package Private Methods

    /**
     * Fire the actions for the given listener list and event source list which
     * return true if the page should continue processing.
     * <p/>
     * This method will be passed the listener list and event source list
     * of a specific event e.g. {@link #POST_ON_PROCESS_EVENT} or
     * {@link #POST_ON_RENDER_EVENT}.
     * event.
     * <p/>
     * This method can be overridden if you need to customize the way events
     * are fired.
     *
     * @param context the request context
     * @param eventSourceList the list of source controls
     * @param eventListenerList the list of listeners to fire
     *
     * @return true if the page should continue processing or false otherwise
     */
    boolean fireActionEvents(Context context, List eventSourceList,
        List eventListenerList) {

        boolean continueProcessing = true;

        for (int i = 0, size = eventSourceList.size(); i < size; i++) {
            Control source = (Control) eventSourceList.get(i);
            ActionListener listener = (ActionListener) eventListenerList.get(i);

            if (!listener.onAction(source)) {
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
    boolean fireActionEvents(Context context) {
        return fireActionEvents(context, ControlRegistry.POST_ON_PROCESS_EVENT);
    }

    /**
     * Fire all the registered action events for the specified event and return
     * true if the page should continue processing.
     *
     * @param context the request context
     * @param event the specific event which events to fire
     *
     * @return true if the page should continue processing or false otherwise
     */
    boolean fireActionEvents(Context context, int event) {
        EventHolder eventHolder = getEventHolder(event);
        return eventHolder.fireActionEvents(context);
    }

    /**
     * Return the EventHolder for the specified event.
     *
     * @param event the event which EventHolder to retrieve
     *
     * @return the EventHolder for the specified event
     */
    EventHolder getEventHolder(int event) {
        if (event == POST_ON_RENDER_EVENT) {
            return getPostRenderEventHolder();
        } else if (event == POST_ON_PROCESS_EVENT) {
            return getPostProcessEventHolder();
        } else {
            return null;
        }
    }

    /**
     * Return the {@link #POST_ON_PROCESS_EVENT} {@link EventHolder}.
     *
     * @return the {@link #POST_ON_PROCESS_EVENT} {@link EventHolder}
     */
    EventHolder getPostProcessEventHolder() {
        if (postProcessEventHolder == null) {
            postProcessEventHolder = new EventHolder();
        }
        return postProcessEventHolder;
    }

    /**
     * Return the {@link #POST_ON_RENDER_EVENT} {@link EventHolder}.
     *
     * @return the {@link #POST_ON_RENDER_EVENT} {@link EventHolder}
     */
    EventHolder getPostRenderEventHolder() {
        if (postRenderEventHolder == null) {
            postRenderEventHolder = new EventHolder();
        }
        return postRenderEventHolder;
    }

    /**
     * Clear the registry.
     */
    void clearRegistry() {
        getPostProcessEventHolder().clear();
        getPostRenderEventHolder().clear();
    }

    /**
     * Return the thread local registry instance.
     *
     * @return the thread local registry instance.
     * @throws RuntimeException if a ControlRegistry is not available on the
     * thread.
     */
    static ControlRegistry getThreadLocalRegistry() {
        return getRegistryStack().peek();
    }

    /**
     * Adds the specified ControlRegistry on top of the Registry stack.
     *
     * @param controlRegistry the ControlRegistry to add
     */
    static void pushThreadLocalRegistry(ControlRegistry controlRegistry) {
        getRegistryStack().push(controlRegistry);
    }

    /**
     * Remove and return the controlRegistry instance on top of the
     * registry stack.
     *
     * @return the controlRegistry instance on top of the registry stack
     */
    static ControlRegistry popThreadLocalRegistry() {
        RegistryStack registryStack = getRegistryStack();
        ControlRegistry controlRegistry = registryStack.pop();

        if (registryStack.isEmpty()) {
            THREAD_LOCAL_REGISTRY.set(null);
        }

        return controlRegistry;
    }

    /**
     * Return the stack data structure where Context's are stored.
     *
     * @return stack data structure where Context's are stored
     */
    static RegistryStack getRegistryStack() {
        RegistryStack registryStack = (RegistryStack) THREAD_LOCAL_REGISTRY.get();

        if (registryStack == null) {
            registryStack = new RegistryStack(2);
            THREAD_LOCAL_REGISTRY.set(registryStack);
        }

        return registryStack;
    }

    // ---------------------------------------------------------- Inner Classes

    /**
     * Provides an unsynchronized Stack.
     */
    static class RegistryStack extends ArrayList {

        /** Serialization version indicator. */
        private static final long serialVersionUID = 1L;

        /**
         * Create a new RegistryStack with the given initial capacity.
         *
         * @param initialCapacity specify initial capacity of this stack
         */
        private RegistryStack(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Pushes the ControlRegistry onto the top of this stack.
         *
         * @param controlRegistry the ControlRegistry to push onto this stack
         * @return the ControlRegistry pushed on this stack
         */
        private ControlRegistry push(ControlRegistry controlRegistry) {
            add(controlRegistry);

            return controlRegistry;
        }

        /**
         * Removes and return the ControlRegistry at the top of this stack.
         *
         * @return the ControlRegistry at the top of this stack
         */
        private ControlRegistry pop() {
            ControlRegistry controlRegistry = peek();

            remove(size() - 1);

            return controlRegistry;
        }

        /**
         * Looks at the ControlRegistry at the top of this stack without
         * removing it.
         *
         * @return the ControlRegistry at the top of this stack
         */
        private ControlRegistry peek() {
            int length = size();

            if (length == 0) {
                String msg = "No ControlRegistry available on ThreadLocal Registry Stack";
                throw new RuntimeException(msg);
            }

            return (ControlRegistry) get(length - 1);
        }
    }

    /**
     * Holds the list of listeners and event sources.
     */
    class EventHolder {

        /** The list of registered event sources. */
        private List eventSourceList;

        /** The list of registered event listeners. */
        private List eventListenerList;

        /**
         * Register the event source and event ActionListener to be fired in the
         * specified event.
         *
         * @param source the action event source
         * @param listener the event action listener
         * @param event the specific event to trigger the action event
         */
        void registerActionEvent(Control source, ActionListener listener) {
            Validate.notNull(source, "Null source parameter");
            Validate.notNull(listener, "Null listener parameter");

            getEventSourceList().add(source);
            getEventListenerList().add(listener);
        }

        /**
         * Checks if any Action Events have been registered.
         */
        boolean hasActionEvents() {
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
        List getEventListenerList() {
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
        List getEventSourceList() {
            if (eventSourceList == null) {
                eventSourceList = new ArrayList();
            }
            return eventSourceList;
        }

        /**
         * Clear the events.
         */
        void clear() {
            if (hasActionEvents()) {
                getEventSourceList().clear();
                getEventListenerList().clear();
            }
        }

        /**
         * Fire all the registered action events and return true if the page should
         * continue processing.
         *
         * @return true if the page should continue processing or false otherwise
         */
        boolean fireActionEvents(Context context) {

            if (!hasActionEvents()) {
                return true;
            }

            return ControlRegistry.this.fireActionEvents(context,
                getEventSourceList(), getEventListenerList());
        }
    }
}
