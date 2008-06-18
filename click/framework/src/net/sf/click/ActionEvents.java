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
 * Provides a thread local registry for managing control action events.
 *
 * @author Malcolm Edgar
 */
public final class ActionEvents {

    /** The thread local list holder of registered event sources. */
    private static ThreadLocal eventSources = new ThreadLocal();

    /** The thread local list holder of registered event listeners. */
    private static ThreadLocal eventListeners = new ThreadLocal();

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

        List eventSourceList = (List) eventSources.get();
        if (eventSourceList == null) {
            eventSourceList = new ArrayList();
            eventSources.set(eventSourceList);
        }

        List eventListenerList = (List) eventListeners.get();
        if (eventListenerList == null) {
            eventListenerList = new ArrayList();
            eventListeners.set(eventListenerList);
        }

        eventSourceList.add(source);
        eventListenerList.add(listener);
    }

    /**
     * Fire all the registered action events and return true if the page should
     * continue processing.
     *
     * @return true if the page should continue processing or false otherwise
     */
    protected static boolean fireActionEvents() {
        List eventSourceList = (List) eventSources.get();
        List eventListenerList = (List) eventListeners.get();

        if (eventSourceList != null && eventListenerList != null) {
            for (int i = 0, size = eventSourceList.size(); i < size; i++) {
                Control source = (Control) eventSourceList.get(i);
                ActionListener listener = (ActionListener) eventListenerList.get(i);

                boolean continueProcessing = listener.onAction(source);
                if (!continueProcessing) {
                    return false;
                }
            }

        } else if (eventSourceList == null && eventListenerList == null) {
            return true;

        } else {
            // This should never happen
            throw new IllegalStateException("ActionEvents registry invalid");
        }

        return true;
    }

    /**
     * Clear all the registered action events.
     */
    protected static void clearActionEvents() {
        eventSources.set(null);
        eventListeners.set(null);
    }
}
