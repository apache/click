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
 * Provides a centralized registry where Controls can be registered, allowing
 * Click to provide advanced functionality such as AJAX
 * {@link org.apache.click.Behavior Behaviors} and
 * {@link org.apache.click.Callback Callbacks}.
 * <p/>
 * <b>Please note:</b> the registry is recreated each request and Controls have
 * to be registered for every request.
 *
 * <h3>Behavior Usage</h3>
 * TODO
 *
 * <h3>Callback Usage</h3>
 * This class will only rarely be used by Control developers who want to create
 * a custom Control with {@link org.apache.click.Callback Callback} functionality.
 * <p/>
 * Example:
 * <pre class="prettyprint">
 * public class MyControl extends AbstractControl {
 *
 *     // The Callback is registered during onInit to cater for both stateless and
 *     // stateful pages
 *     public void onInit() {
 *         Callback callback = getCallback();
 *         ControlRegistry.registerCallback(this, callback);
 *     }
 *
 *     private Callback getCallback() {
 *         callback = new Callback() {
 *             public void preResponse(Control source) {
 *                 // Invoked before the controls are rendered to the client
 *                 addIndexToControlNames();
 *             }
 *
 *             public void preGetHeadElements(Control source) {
 *                 // Invoked before the HEAD elements are retrieved for each Control
 *             }
 *
 *             public void preDestroy(Control source) {
 *                 // Invoked before onDestroy event
 *             }
 *         };
 *         return callback;
 *     }
 * } </pre>
 */
public class ControlRegistry {

    // Constants --------------------------------------------------------------

    /** The thread local registry holder. */
    private static final ThreadLocal<RegistryStack> THREAD_LOCAL_REGISTRY =
                    new ThreadLocal<RegistryStack>();

    // Variables --------------------------------------------------------------

    /** The set of Ajax target controls. */
    Set<Control> ajaxTargetControls;

    /** The list of registered behaviors. */
    List<BehaviorHolder> behaviors;

    /** The application log service. */
    LogService logger;

    // Constructors -----------------------------------------------------------

    /**
     * Construct the ControlRegistry with the given ConfigService.
     *
     * @param configService the click application configuration service
     */
    public ControlRegistry(ConfigService configService) {
        this.logger = configService.getLogService();
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Register the control to be processed by the ClickServlet if the control
     * is the Ajax target. A control is an Ajax target if the
     * {@link Control#isAjaxTarget(org.apache.click.Context)} method returns true.
     * Once a target control is identified, ClickServlet invokes its
     * {@link Control#onProcess()} method invoked.
     * <p/>
     * <b>Please note:</b> the ControlRegistry is stateless. For each request
     * a new registry is created. This means a control is only registered for
     * a single request and must be registered again for subsequent requests.
     *
     * <b>Stateful Page note:</b> when invoking this method directly from a stateful
     * page, ensure the control is registered on every request. Generally this
     * means that for stateful pages this method should be used in the Page
     * <tt>onInit</tt> method (which is invoked for every request) instead of the
     * Page constructor (which is invoked only once). This warning can be ignored
     * for stateless pages since both the constructor and onInit method is invoked
     * every request.
     *
     * @param control the control to register as an Ajax target
     */
    public static void registerAjaxTarget(Control control) {
        if (control == null) {
            throw new IllegalArgumentException("control cannot be null");
        }

        ControlRegistry instance = getThreadLocalRegistry();
        instance.internalRegisterAjaxTarget(control);
    }

    /**
     * Register a control event interceptor for the given control and behavior.
     * The control will be passed as the source control to the Behavior
     * interception methods:
     * {@link org.apache.click.Behavior#preGetHeadElements(org.apache.click.Control) preGetHeadElements(Control)},
     * {@link org.apache.click.Behavior#preResponse(org.apache.click.Control) preResponse(Control) and
     * {@link org.apache.click.Behavior#preDestroy()}.
     * <p/>
     * <b>Please note:</b> the ControlRegistry is stateless. For each request
     * a new registry is created. This means a control and behavior is only
     * registered for a single request and must be registered again for subsequent
     * requests.
     *
     * <b>Stateful Page note:</b> when invoking this method directly from a stateful
     * page, ensure the control is registered on every request. Generally this
     * means that for stateful pages this method should be used in the Page
     * <tt>onInit</tt> method (which is invoked for every request) instead of the
     * Page constructor (which is invoked only once). This warning can be ignored
     * for stateless pages since both the constructor and onInit method is invoked
     * every request.
     *
     * @param source the behavior source control
     * @param behavior the behavior to register
     */
    public static void registerInterceptor(Control control, Behavior behavior) {
        if (control == null) {
            throw new IllegalArgumentException("control cannot be null");
        }
        if (behavior == null) {
            throw new IllegalArgumentException("behavior cannot be null");
        }

        ControlRegistry instance = getThreadLocalRegistry();
        instance.internalRegisterCallback(control, behavior);
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Allow the registry to handle the error that occurred.
     *
     * @param throwable the error which occurred during processing
     */
    protected void errorOccurred(Throwable throwable) {
        clear();
    }

    // Package Private Methods ------------------------------------------------

    /**
     * Remove all behaviors and controls from this registry.
     */
    void clear() {
        if (hasBehaviors()) {
            getBehaviors().clear();
        }

        if (hasAjaxTargetControls()) {
            getAjaxTargetControls().clear();
        }
    }

    /**
     * Register the AJAX target control.
     *
     * @param control the AJAX target control
     */
    void internalRegisterAjaxTarget(Control control) {
        Validate.notNull(control, "Null control parameter");
        getAjaxTargetControls().add(control);
    }

    /**
     * Register the source control and associated behavior.
     *
     * @param source the behavior source control
     * @param behavior the behavior to register
     */
    void internalRegisterCallback(Control source, Behavior behavior) {
        Validate.notNull(source, "Null source parameter");
        Validate.notNull(behavior, "Null behavior parameter");

        BehaviorHolder behaviorHolder = new BehaviorHolder(source, behavior);

        // Guard against adding duplicate behaviors
        List<BehaviorHolder> localBehaviors = getBehaviors();
        if (!localBehaviors.contains(behaviorHolder)) {
            localBehaviors.add(behaviorHolder);
        }
    }

    void processPreResponse(Context context) {
        if (hasAjaxTargetControls()) {
            for (Control control : getAjaxTargetControls()) {
                for (Behavior behavior : control.getBehaviors()) {
                    behavior.preResponse(control);
                }
            }
        }

        if (hasBehaviors()) {
            for (BehaviorHolder behaviorHolder : getBehaviors()) {
                Behavior behavior = behaviorHolder.getBehavior();
                Control control = behaviorHolder.getControl();
                behavior.preResponse(control);
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

        if (hasBehaviors()) {
            for (BehaviorHolder behaviorHolder : getBehaviors()) {
                Behavior behavior = behaviorHolder.getBehavior();
                Control control = behaviorHolder.getControl();
                behavior.preGetHeadElements(control);
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

        if (hasBehaviors()) {
            for (BehaviorHolder behaviorHolder : getBehaviors()) {
                Behavior behavior = behaviorHolder.getBehavior();
                Control control = behaviorHolder.getControl();
                behavior.preDestroy(control);
            }
        }
    }

    /**
     * Checks if any AJAX target control have been registered.
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
     * Checks if any control behaviors have been registered.
     */
    boolean hasBehaviors() {
        if (behaviors == null || behaviors.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Return the set of registered behaviors.
     *
     * @return set of registered behaviors
     */
    List<BehaviorHolder> getBehaviors() {
        if (behaviors == null) {
            behaviors = new ArrayList<BehaviorHolder>();
        }
        return behaviors;
    }

    static ControlRegistry getThreadLocalRegistry() {
        return getRegistryStack().peek();
    }

    /**
     * Adds the specified ControlRegistry on top of the registry stack.
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

    static RegistryStack getRegistryStack() {
        RegistryStack controlRegistry = THREAD_LOCAL_REGISTRY.get();

        if (controlRegistry == null) {
            controlRegistry = new RegistryStack(2);
            THREAD_LOCAL_REGISTRY.set(controlRegistry);
        }

        return controlRegistry;
    }

    /**
     * Provides an unsynchronized Stack.
     */
    static class RegistryStack extends ArrayList<ControlRegistry> {

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

            return get(length - 1);
        }
    }

    static class BehaviorHolder {

        private Behavior behavior;

        private Control control;

        public BehaviorHolder(Control control, Behavior behavior) {
            this.control = control;
            this.behavior = behavior;
        }

        public Behavior getBehavior() {
            return behavior;
        }

        public void setBehavior(Behavior behavior) {
            this.behavior = behavior;
        }

        public Control getControl() {
            return control;
        }

        public void setControl(Control control) {
            this.control = control;
        }

        /**
         * @see Object#equals(java.lang.Object)
         *
         * @param o the reference object with which to compare
         * @return true if this object equals the given object
         */
        @Override
        public boolean equals(Object o) {

            //1. Use the == operator to check if the argument is a reference to this object.
            if (o == this) {
                return true;
            }

            //2. Use the instanceof operator to check if the argument is of the correct type.
            if (!(o instanceof BehaviorHolder)) {
                return false;
            }

            //3. Cast the argument to the correct type.
            BehaviorHolder that = (BehaviorHolder) o;

            boolean equals = this.control == null ? that.control == null : this.control.equals(that.control);
            if (!equals) {
                return false;
            }

            return this.behavior == null ? that.behavior == null : this.behavior.equals(that.behavior);
        }

        /**
         * @see java.lang.Object#hashCode()
         *
         * @return the BehaviorHolder hashCode
         */
        @Override
        public int hashCode() {
            int result = 17;
            result = 37 * result + (control == null ? 0 : control.hashCode());
            result = 37 * result + (behavior == null ? 0 : behavior.hashCode());
            return result;
        }
    }
}
