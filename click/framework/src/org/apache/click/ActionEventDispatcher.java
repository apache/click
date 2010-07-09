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
import java.util.Iterator;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.Set;
import org.apache.click.service.ConfigService;
import org.apache.click.service.LogService;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.Validate;

/**
 * Provides a control ActionListener and Behavior dispatcher. The
 * ClickServlet will dispatch registered ActionListeners and Behaviors after
 * page controls have been processed.
 *
 * <h4>Example Usage</h4>
 * The following example shows how to register an ActionListener with a custom
 * Control:
 *
 * <pre class="prettyprint">
 * public class MyControl extends AbstractControl {
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
 * When the link is clicked it invokes the method
 * {@link org.apache.click.control.AbstractControl#dispatchActionEvent()}.
 * This method registers the Control's action listener with the
 * ActionEventDispatcher. The ClickServlet will subsequently invoke the registered
 * {@link ActionListener#onAction(Control)} method after all the Page controls
 * <tt>onProcess()</tt> method have been invoked.
 */
public class ActionEventDispatcher {

    // Constants --------------------------------------------------------------

     /** The thread local dispatcher holder. */
    private static final ThreadLocal<DispatcherStack> THREAD_LOCAL_DISPATCHER
        = new ThreadLocal<DispatcherStack>();

    // Variables --------------------------------------------------------------

    /** The list of registered event sources. */
    List<Control> eventSourceList;

    /** The list of registered event listeners. */
    List<ActionListener> eventListenerList;

    /** The set of Controls with attached Behaviors. */
    Set<Control> behaviorSourceSet;

    /**
     * The {@link org.apache.click.Partial} response to render. This partial is
     * set from the target Behavior.
     */
    Partial partial;

    /** The application log service. */
    LogService logger;

    // Constructors -----------------------------------------------------------

    public ActionEventDispatcher(ConfigService configService) {
        this.logger = configService.getLogService();
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Register the event source and event ActionListener to be fired by the
     * ClickServlet once all the controls have been processed.
     *
     * @param source the action event source
     * @param listener the event action listener
     */
    public static void dispatchActionEvent(Control source, ActionListener listener) {
        Validate.notNull(source, "Null source parameter");
        Validate.notNull(listener, "Null listener parameter");

        ActionEventDispatcher instance = getThreadLocalDispatcher();
        instance.registerActionEvent(source, listener);
    }

    /**
     * Register the source control which behaviors should be fired by the
     * ClickServlet.
     *
     * @param source the source control which behaviors should be fired
     */
    public static void dispatchBehavior(Control source) {
        Validate.notNull(source, "Null source parameter");

        ActionEventDispatcher instance = getThreadLocalDispatcher();
        instance.registerBehaviorSource(source);
    }

    /**
     * Fire all the registered action events after the Page Controls have been
     * processed and return true if the page should continue processing.
     *
     * @param context the request context
     *
     * @return true if the page should continue processing, false otherwise
     */
    public boolean fireActionEvents(Context context) {

        if (!hasActionEvents()) {
            return true;
        }

        return fireActionEvents(context, getEventSourceList(), getEventListenerList());
    }

    /**
     * Fire all the registered behaviors after the Page Controls have been
     * processed and return true if the page should continue processing.
     *
     * @see #fireBehaviors(org.apache.click.Context, java.util.Set)
     *
     * @param context the request context
     *
     * @return true if the page should continue processing, false otherwise
     */
    public boolean fireBehaviors(Context context) {

        if (!hasBehaviorSourceSet()) {
            return true;
        }

        return fireBehaviors(context, getBehaviorSourceSet());
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Allow the dispatcher to handle the error that occurred.
     *
     * @param throwable the error which occurred during processing
     */
    protected void errorOccurred(Throwable throwable) {
        // Clear the control listeners and behaviors from the dispatcher
        clear();
    }

    /**
     * Return the thread local dispatcher instance.
     *
     * @return the thread local dispatcher instance.
     * @throws RuntimeException if a ActionEventDispatcher is not available on the
     * thread
     */
    protected static ActionEventDispatcher getThreadLocalDispatcher() {
        return getDispatcherStack().peek();
    }

    /**
     * Fire the actions for the given listener list and event source list which
     * return true if the page should continue processing.
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
    protected boolean fireActionEvents(Context context,
        List<Control> eventSourceList, List<ActionListener> eventListenerList) {

        boolean continueProcessing = true;

        for (int i = 0, size = eventSourceList.size(); i < size; i++) {
            Control source = eventSourceList.remove(0);
            ActionListener listener = eventListenerList.remove(0);

            if (!fireActionEvent(context, source, listener)) {
                continueProcessing = false;
            }
        }

        return continueProcessing;
    }

    /**
     * Fire the action for the given listener and event source which
     * return true if the page should continue processing.
     * <p/>
     * This method can be overridden if you need to customize the way events
     * are fired.
     *
     * @param context the request context
     * @param source the source control
     * @param listener the listener to fire
     *
     * @return true if the page should continue processing, false otherwise
     */
    protected boolean fireActionEvent(Context context, Control source,
        ActionListener listener) {
        return listener.onAction(source);
    }

    /**
     * Fire the behaviors for the given control set and return true if the page
     * should continue processing, false otherwise.
     * <p/>
     * This method can be overridden if you need to customize the way behaviors
     * are fired.
     *
     * @see #fireBehavior(org.apache.click.Context, org.apache.click.Control)
     *
     * @param context the request context
     * @param behaviorSourceSet the set of controls with attached behaviors
     *
     * @return true if the page should continue processing, false otherwise
     */
    protected boolean fireBehaviors(Context context, Set<Control> behaviorSourceSet) {

        boolean continueProcessing = true;

        for (Iterator<Control> it = behaviorSourceSet.iterator(); it.hasNext();) {
            Control source = it.next();

            // Pop the first entry in the set
            it.remove();

            if (!fireBehavior(context, source)) {
                continueProcessing = false;
            }
        }

        return continueProcessing;
    }

    /**
     * Fire the behavior for the given control and return true if the page
     * should continue processing, false otherwise.
     * <p/>
     * This method can be overridden if you need to customize the way behaviors
     * are fired.
     *
     * @param context the request context
     * @param source the control which attached behaviors should be fired
     *
     * @return true if the page should continue processing, false otherwise
     */
    protected boolean fireBehavior(Context context, Control source) {

        boolean continueProcessing = true;

        if (logger.isTraceEnabled()) {
            String sourceClassName = ClassUtils.getShortClassName(source.getClass());
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            buffer.append("   processing Behaviors for control: '");
            buffer.append(source.getName()).append("' ");
            buffer.append(sourceClassName);
            logger.trace(buffer.toString());
        }

        for (Behavior behavior : source.getBehaviors()) {

            boolean isRequestTarget = behavior.isRequestTarget(context);

            if (logger.isTraceEnabled()) {
                String behaviorClassName = ClassUtils.getShortClassName(behavior.getClass());
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                buffer.append("      invoked: ");
                buffer.append(behaviorClassName);
                buffer.append(".isRequestTarget() : ");
                buffer.append(isRequestTarget);
                logger.trace(buffer.toString());
            }

            if (isRequestTarget) {

                // The first non-null Partial returned will be rendered, other Partials are ignored
                Partial behaviorPartial = behavior.onAction(source);
                if (partial == null && behaviorPartial != null) {
                    partial = behaviorPartial;
                }

                if (logger.isTraceEnabled()) {
                    String behaviorClassName = ClassUtils.getShortClassName(behavior.getClass());
                    String partialClassName = null;

                    if (behaviorPartial != null) {
                        partialClassName = ClassUtils.getShortClassName(behaviorPartial.getClass());
                    }

                    HtmlStringBuffer buffer = new HtmlStringBuffer();
                    buffer.append("      invoked: ");
                    buffer.append(behaviorClassName);
                    buffer.append(".onAction() : ");
                    buffer.append(partialClassName);

                    if (partial == behaviorPartial && behaviorPartial != null) {
                        buffer.append(" (Partial content will be rendered)");
                    } else {
                        if (behaviorPartial == null) {
                            buffer.append(" (Partial is null and will be ignored)");
                        } else {
                            buffer.append(" (Partial will be ignored since another Behavior already retuned a non-null Partial)");
                        }
                    }

                    logger.trace(buffer.toString());
                }

                continueProcessing = false;
                break;
            }
        }

        if (logger.isTraceEnabled()) {

            // Provide trace if no target behavior was found
            if (continueProcessing) {
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                String sourceClassName = ClassUtils.getShortClassName(source.getClass());
                buffer.append("   *no* target behavior found for '");
                buffer.append(source.getName()).append("' ");
                buffer.append(sourceClassName);
                buffer.append(" - invoking Behavior.isRequestTarget() returned false for all behaviors");
                logger.trace(buffer.toString());
            }
        }

        // Ajax requests stops further processing
        return continueProcessing;
    }

    // Package Private Methods ------------------------------------------------

    /**
     * Register the event source and event ActionListener.
     *
     * @param source the action event source
     * @param listener the event action listener
     */
    void registerActionEvent(Control source, ActionListener listener) {
        Validate.notNull(source, "Null source parameter");
        Validate.notNull(listener, "Null listener parameter");

        getEventSourceList().add(source);
        getEventListenerList().add(listener);
    }

    /**
     * Register the behavior source control.
     *
     * @param source the behavior source control
     */
    void registerBehaviorSource(Control source) {
        Validate.notNull(source, "Null source parameter");

        getBehaviorSourceSet().add(source);
    }

    /**
     * Checks if any Action Events have been registered.
     *
     * @return true if the dispatcher has any Action Events registered
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
    List<ActionListener> getEventListenerList() {
        if (eventListenerList == null) {
            eventListenerList = new ArrayList<ActionListener>();
        }
        return eventListenerList;
    }

    /**
     * Return the list of event sources.
     *
     * @return list of event sources
     */
    List<Control> getEventSourceList() {
        if (eventSourceList == null) {
            eventSourceList = new ArrayList<Control>();
        }
        return eventSourceList;
    }

    /**
     * Clear the events and behaviors.
     */
    void clear() {
        if (hasActionEvents()) {
            getEventSourceList().clear();
            getEventListenerList().clear();
        }

        if (hasBehaviorSourceSet()) {
            getBehaviorSourceSet().clear();
        }
    }

    /**
     * Return the Partial Ajax response or null if no behavior was dispatched.
     *
     * @return the Partial Ajax response or null if no behavior was dispatched
     */
    Partial getPartial() {
        return partial;
    }

    /**
     * Return true if a control with behaviors was registered, false otherwise.
     *
     * @return true if a control with behaviors was registered, false otherwise.
     */
    boolean hasBehaviorSourceSet() {
        if (behaviorSourceSet == null || behaviorSourceSet.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Return the set of controls with attached behaviors.
     *
     * @return set of control with attached behaviors
     */
    Set<Control> getBehaviorSourceSet() {
        if (behaviorSourceSet == null) {
            behaviorSourceSet = new LinkedHashSet<Control>();
        }
        return behaviorSourceSet;
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
        DispatcherStack dispatcherStack = THREAD_LOCAL_DISPATCHER.get();

        if (dispatcherStack == null) {
            dispatcherStack = new DispatcherStack(2);
            THREAD_LOCAL_DISPATCHER.set(dispatcherStack);
        }

        return dispatcherStack;
    }

    // Inner Classes ----------------------------------------------------------

    /**
     * Provides an unsynchronized Stack.
     */
    static class DispatcherStack extends ArrayList<ActionEventDispatcher> {

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

            return get(length - 1);
        }
    }
}
