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
import java.util.Iterator;
import java.util.LinkedHashSet;

import java.util.List;
import java.util.Set;
import net.sf.click.util.Partial;
import org.apache.commons.lang.Validate;

/**
 * Provides a thread local register for managing Ajax controls and ActionListener
 * events.
 * <p/>
 * Developers who implement their own controls, should look at the following
 * example <tt>onProcess</tt> implementation. Note the call to
 * {@link net.sf.click.control.AbstractControl#registerActionEvent()} which
 * registers the Control listener with ControlRegistry.
 *
 * <pre class="prettyprint">
 * public class MyLink extends AbstractControl {
 *
 *     ...
 *     public boolean onProcess() {
 *         bindRequestValue();
 *
 *         if (isClicked()) {
 *             // Register this controls listener for invocation after process
 *             // finish
 *             registerActionEvent();
 *         }
 *
 *         return true;
 *     }
 *     ...
 *
 * } </pre>
 * <p/>
 * Registering Ajax Controls for processing is another common use case:
 *
 * <pre class="prettyprint">
 * public void onInit() {
 *     Form form = new Form("form");
 *
 *     // Ajaxify the form by registering it in the ControlRegistry
 *     ControlRegistry.registerAjaxControl(form);
 *
 *     Submit submit = new Submit("submit");
 *     submit.setListener(new AjaxListener() {
 *
 *         public Partial onAjaxAction(Control control) {
 *             return new Partial("Hello World!");
 *         }
 *     });
 * } </pre>
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public final class ControlRegistry {

    // -------------------------------------------------------- Constants

    // TODO investigate using a Stack of Registries instead of single ControlRegistry
    // in case of forwarding to another Page.

    /** The thread local registry holder. */
    private static final ThreadLocal THREAD_LOCAL_REGISTRY = new ThreadLocal();

    // -------------------------------------------------------- Variables

    /** The set of unique registered Ajax Controls. */
    private Set ajaxControlList;

    /** The list of registered event sources. */
    private List eventSourceList;

    /** The list of registered event listeners. */
    private List eventListenerList;

    // --------------------------------------------------------- Public Methods

    /**
     * Register the control to be processed by the ClickServlet for Ajax
     * requests.
     *
     * @param control the control to register
     */
    public static void registerAjaxControl(Control control) {
        Validate.notNull(control, "Null control parameter");

        ControlRegistry instance = getThreadLocalRegistry();
        Set controlList = instance.getAjaxControls();
        controlList.add(control);
    }

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
     * Checks if any Ajax controls have been registered.
     */
    static boolean hasAjaxControls() {
        ControlRegistry instance = (ControlRegistry) THREAD_LOCAL_REGISTRY.get();
        if (instance == null) {
            return false;
        }

        if (instance.ajaxControlList == null || instance.ajaxControlList.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Process all the registered controls and return true if the page should
     * continue processing.
     *
     * @return true if the page should continue processing or false otherwise
     */
    static boolean processAjaxControls(Context context) {
        ControlRegistry instance = (ControlRegistry) THREAD_LOCAL_REGISTRY.get();

        // If no instance is available exit early
        if (instance == null) {
            return true;
        }

        Set controlList = instance.getAjaxControls();

        if (!controlList.isEmpty()) {
            for (Iterator it = controlList.iterator(); it.hasNext();) {
                Control control = (Control) it.next();

                // Check if control is targeted by this request
                if (context.getRequestParameter(control.getId()) != null) {
                    control.onProcess();
                }
            }

            // Fire the registered listeners
            return fireActionEvents(context);

        } else {
            return true;
        }
    }

    /**
     * Fire all the registered action events and return true if the page should
     * continue processing.
     *
     * @return true if the page should continue processing or false otherwise
     */
    static boolean fireActionEvents(Context context) {
        ControlRegistry instance = getThreadLocalRegistry();
        List eventSourceList = (List) instance.getEventSourceList();
        List eventListenerList = (List) instance.getEventListenerList();

        boolean continueProcessing = true;

        if (eventSourceList != null && eventListenerList != null) {
            for (int i = 0, size = eventSourceList.size(); i < size; i++) {
                Control source = (Control) eventSourceList.get(i);
                ActionListener listener = (ActionListener) eventListenerList.get(i);

                if (context.isAjaxRequest() && listener instanceof AjaxListener) {

                    Partial partial = ((AjaxListener) listener).onAjaxAction(source);
                    if (partial != null) {
                        // Have to process Partial here
                        partial.process(context);
                    }

                    // Ajax requests stops further processing
                    continueProcessing = false;

                } else {
                    if (!listener.onAction(source)) {
                        continueProcessing = false;
                    }
                }
            }

        } else if (eventSourceList == null && eventListenerList == null) {
            continueProcessing = true;

        } else {
            // This should never happen
            throw new IllegalStateException("ControlRegistry is invalid");
        }

        return continueProcessing;
    }

    /**
     * Clear the registry.
     */
    static void clearRegistry() {
        THREAD_LOCAL_REGISTRY.set(null);
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Return the thread local registry instance.
     *
     * @return the thread local registry instance.
     */
    private static ControlRegistry getThreadLocalRegistry() {
        ControlRegistry instance = (ControlRegistry) THREAD_LOCAL_REGISTRY.get();
        if (instance == null) {
            instance = new ControlRegistry();
            THREAD_LOCAL_REGISTRY.set(instance);
        }
        return instance;
    }

    /**
     * Return the list of event listeners.
     *
     * @return list of event listeners
     */
    private List getEventListenerList() {
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
    private List getEventSourceList() {
        if (eventSourceList == null) {
            eventSourceList = new ArrayList();
        }
        return eventSourceList;
    }

    /**
     * Return the set of unique Ajax Controls.
     *
     * @return set of unique Ajax Controls
     */
    private Set getAjaxControls() {
        if (ajaxControlList == null) {
            ajaxControlList = new LinkedHashSet();
        }
        return ajaxControlList;
    }

}
