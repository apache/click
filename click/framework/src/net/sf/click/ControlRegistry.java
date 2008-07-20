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
package net.sf.click;

import java.util.ArrayList;

import java.util.List;
import org.apache.commons.lang.Validate;

/**
 * Provides a thread local register for ActionListener events. The ClickServlet
 * will fire any ActionListeners registered after processing all the page's
 * controls.
 *
 * <h4>Example Usage</h4>
 * Developers who implement their own controls, should look at the following
 * example control <tt>onProcess</tt> implementation.
 *
 * <pre class="prettyprint">
 * public class MyLink extends AbstractControl {
 *     ...
 *
 *     public boolean onProcess() {
 *         bindRequestValue();
 *
 *         if (isClicked()) {
 *             // Register this controls listener for invocation after
 *             // control processing has finished
 *             registerActionEvent();
 *         }
 *
 *         return true;
 *     }
 * } </pre>
 *
 * In this example if the link is clicked, it then calls
 * {@link net.sf.click.control.AbstractControl#registerActionEvent()}.
 * This method registers the Control's action listener with ControlRegistry.
 * The ClickServlet will subsequently invoke the registered
 * {@link ActionListener#onAction(Control)} methods after all the Page's control
 * <tt>onProcess()</tt> methods have been invoked.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public class ControlRegistry {

    // -------------------------------------------------------------- Constants

    /** The thread local registry holder. */
    private static final ThreadLocal THREAD_LOCAL_REGISTRY = new ThreadLocal();

    // -------------------------------------------------------------- Variables

    /** The list of registered event sources. */
    private List eventSourceList;

    /** The list of registered event listeners. */
    private List eventListenerList;

    // --------------------------------------------------------- Public Methods

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet once all the controls have been processed.
     *
     * @param source the action event source
     * @param listener the event action listener
     */
    public static void registerActionEvent(Control source, ActionListener listener) {
        Validate.notNull(source, "Null source parameter");
        Validate.notNull(listener, "Null listener parameter");

        ControlRegistry instance = getThreadLocalRegistry();
        List eventSourceList = (List) instance.getEventSourceList();
        List eventListenerList = (List) instance.getEventListenerList();
        eventSourceList.add(source);
        eventListenerList.add(listener);
    }

    // ------------------------------------------------ Package Private Methods

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
     * Fire all the registered action events and return true if the page should
     * continue processing.
     *
     * @return true if the page should continue processing or false otherwise
     */
    boolean fireActionEvents(Context context) {
        boolean continueProcessing = true;

        if (!hasActionEvents()) {
            return true;
        }

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
     * Clear the registry.
     */
    void clearRegistry() {
        if (hasActionEvents()) {
            eventListenerList.clear();
            eventSourceList.clear();
        }
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

        private static final long serialVersionUID = 1L;

        private RegistryStack(int initialCapacity) {
            super(initialCapacity);
        }

        private ControlRegistry push(ControlRegistry controlRegistry) {
            add(controlRegistry);

            return controlRegistry;
        }

        private ControlRegistry pop() {
            ControlRegistry controlRegistry = peek();

            remove(size() - 1);

            return controlRegistry;
        }

        private ControlRegistry peek() {
            int length = size();

            if (length == 0) {
                String msg = "No ControlRegistry available on ThreadLocal Registry Stack";
                throw new RuntimeException(msg);
            }

            return (ControlRegistry) get(length - 1);
        }
    }
}
