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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.click.service.ConfigService;
import org.apache.click.service.LogService;
import org.apache.commons.lang.Validate;

/**
 * Provides a registry for Controls.
 *
 * TODO: javadoc
 */
public class ControlRegistry {

    // Constants --------------------------------------------------------------

    /** The thread local registry holder. */
    private static final ThreadLocal<RegistryStack> THREAD_LOCAL_REGISTRY =
                    new ThreadLocal<RegistryStack>();

    // Variables --------------------------------------------------------------

    /** The set of Ajax target controls. */
    Set<Control> ajaxTargetControls;

    /** The list of registered callbacks. */
    List<CallbackHolder> callbacks;

    /** The application log service. */
    LogService logger;

    // Constructors -----------------------------------------------------------

    public ControlRegistry(ConfigService configService) {
        this.logger = configService.getLogService();
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Register the control to be processed by the ClickServlet if the control
     * is the Ajax target. A control is an Ajax target if the
     * {@link Control#isAjaxTarget(org.apache.click.Context)} method returns true.
     * Target controls have their {@link Control#onProcess()} method invoked.
     *
     * @param control the control to register
     */
    public static void registerAjaxTarget(Control control) {
        ControlRegistry instance = getThreadLocalRegistry();
        instance.internalRegisterAjaxTarget(control);
    }

    public static void registerCallback(Control control, Callback callback) {
        ControlRegistry instance = getThreadLocalRegistry();
        instance.internalRegisterCallback(control, callback);
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Allow the dispatcher to handle the error that occurred.
     *
     * @param throwable the error which occurred during processing
     */
    protected void errorOccurred(Throwable throwable) {
        clear();
    }

    // Package Private Methods ------------------------------------------------

    /**
     * Remove all callbacks and controls from this dispatcher.
     */
    void clear() {
        if (hasCallbacks()) {
            getCallbacks().clear();
        }

        if (hasAjaxTargetControls()) {
            getAjaxTargetControls().clear();
        }
    }

    /**
     * Register the ajax target control.
     *
     * @param control the ajax target control
     */
    void internalRegisterAjaxTarget(Control control) {
        Validate.notNull(control, "Null control parameter");
        getAjaxTargetControls().add(control);
    }

    /**
     * Register the behavior source control.
     *
     * @param source the behavior source control
     */
    void internalRegisterCallback(Control source, Callback callback) {
        Validate.notNull(source, "Null source parameter");
        Validate.notNull(callback, "Null callback parameter");

        CallbackHolder callbackHolder = new CallbackHolder(source, callback);
        getCallbacks().add(callbackHolder);
    }

    void processPreResponse(Context context) {
        if (hasAjaxTargetControls()) {
            for (Control control : getAjaxTargetControls()) {
                for (Behavior behavior : control.getBehaviors()) {
                    behavior.preResponse(control);
                }
            }
        }

        if (hasCallbacks()) {
            for (CallbackHolder callbackHolder : getCallbacks()) {
                Callback callback = callbackHolder.getCallback();
                Control control = callbackHolder.getControl();
                callback.preResponse(control);
            }
        }
    }

    void processPreGetHeadElements(Context context) {
        if (hasAjaxTargetControls()) {
            for (Control control : getAjaxTargetControls()) {
                for (Behavior behavior : control.getBehaviors()) {
                    behavior.preGetHeadElements(control);
                }
            }
        }

        if (hasCallbacks()) {
            for (CallbackHolder callbackHolder : getCallbacks()) {
                Callback callback = callbackHolder.getCallback();
                Control control = callbackHolder.getControl();
                callback.preGetHeadElements(control);
            }
        }
    }

    void processPreDestroy(Context context) {
        if (hasAjaxTargetControls()) {
            for (Control control : getAjaxTargetControls()) {
                for (Behavior behavior : control.getBehaviors()) {
                    behavior.preDestroy(control);
                }
            }
        }

        if (hasCallbacks()) {
            for (CallbackHolder callbackHolder : getCallbacks()) {
                Callback callback = callbackHolder.getCallback();
                Control control = callbackHolder.getControl();
                callback.preDestroy(control);
            }
        }
    }

    /**
     * Checks if any control callbacks have been registered.
     */
    boolean hasAjaxTargetControls() {
        if (ajaxTargetControls == null || ajaxTargetControls.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Return the set of behavior enabled controls.
     *
     * @return the set of behavior enabled controls.
     */
    Set<Control> getAjaxTargetControls() {
        if (ajaxTargetControls == null) {
            ajaxTargetControls = new LinkedHashSet<Control>();
        }
        return ajaxTargetControls;
    }

    /**
     * Checks if any control callbacks have been registered.
     */
    boolean hasCallbacks() {
        if (callbacks == null || callbacks.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Return the set of registered callbacks.
     *
     * @return set of registered callbacks
     */
    List<CallbackHolder> getCallbacks() {
        if (callbacks == null) {
            callbacks = new ArrayList<CallbackHolder>();
        }
        return callbacks;
    }

    static ControlRegistry getThreadLocalRegistry() {
        return getDispatcherStack().peek();
    }

    /**
     * Adds the specified CallbackDispatcher on top of the dispatcher stack.
     *
     * @param callbackDispatcher the CallbackDispatcher to add
     */
    static void pushThreadLocalDispatcher(ControlRegistry callbackDispatcher) {
        getDispatcherStack().push(callbackDispatcher);
    }

    /**
     * Remove and return the callbackDispatcher instance on top of the
     * dispatcher stack.
     *
     * @return the callbackDispatcher instance on top of the dispatcher stack
     */
    static ControlRegistry popThreadLocalDispatcher() {
        RegistryStack registryStack = getDispatcherStack();
        ControlRegistry callbackDispatcher = registryStack.pop();

        if (registryStack.isEmpty()) {
            THREAD_LOCAL_REGISTRY.set(null);
        }

        return callbackDispatcher;
    }

    static RegistryStack getDispatcherStack() {
        RegistryStack dispatcherStack = THREAD_LOCAL_REGISTRY.get();

        if (dispatcherStack == null) {
            dispatcherStack = new RegistryStack(2);
            THREAD_LOCAL_REGISTRY.set(dispatcherStack);
        }

        return dispatcherStack;
    }

    /**
     * Provides an unsynchronized Stack.
     */
    static class RegistryStack extends ArrayList<ControlRegistry> {

        /** Serialization version indicator. */
        private static final long serialVersionUID = 1L;

        /**
         * Create a new DispatcherStack with the given initial capacity.
         *
         * @param initialCapacity specify initial capacity of this stack
         */
        private RegistryStack(int initialCapacity) {
            super(initialCapacity);
        }

        /**
         * Pushes the CallbackDispatcher onto the top of this stack.
         *
         * @param callbackDispatcher the CallbackDispatcher to push onto this stack
         * @return the CallbackDispatcher pushed on this stack
         */
        private ControlRegistry push(ControlRegistry callbackDispatcher) {
            add(callbackDispatcher);

            return callbackDispatcher;
        }

        /**
         * Removes and return the CallbackDispatcher at the top of this stack.
         *
         * @return the CallbackDispatcher at the top of this stack
         */
        private ControlRegistry pop() {
            ControlRegistry callbackDispatcher = peek();

            remove(size() - 1);

            return callbackDispatcher;
        }

        /**
         * Looks at the CallbackDispatcher at the top of this stack without
         * removing it.
         *
         * @return the CallbackDispatcher at the top of this stack
         */
        private ControlRegistry peek() {
            int length = size();

            if (length == 0) {
                String msg = "No CallbackDispatcher available on ThreadLocal Dispatcher Stack";
                throw new RuntimeException(msg);
            }

            return get(length - 1);
        }
    }

    static class CallbackHolder {

        private Callback callback;

        private Control control;

        public CallbackHolder(Control control, Callback callback) {
            this.control = control;
            this.callback = callback;
        }

        public Callback getCallback() {
            return callback;
        }

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        public Control getControl() {
            return control;
        }

        public void setControl(Control control) {
            this.control = control;
        }
    }
}
