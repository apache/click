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

import java.util.Iterator;
import java.util.LinkedHashSet;

import java.util.Set;
import org.apache.commons.lang.Validate;

/**
 * Provides a thread local registry for managing controls in various scenarios.
 * <p/>
 * Registering Ajax Controls for processing is a common usage of this class.
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
 *         public boolean onAction(Control control) {
 *             actionPerformed();
 *             return true;
 *         }
 *
 *         public void onAjaxAction(Control control) {
 *             actionPerformed();
 *             return new Partial("Hello World!");
 *         }
 *
 *         private void actionPerformed() {
 *             if (form.isValid()) {
 *                 //Save data to database
 *             }
 *         }
 *     });
 * } </pre>
 *
 * @author Malcolm Edgar
 * @author Bob Schellink
 */
public final class ControlRegistry {

    /** The thread local registry holder. */
    private static final ThreadLocal THREAD_LOCAL_REGISTRY = new ThreadLocal();

    /** The list of registered Ajax Controls. */
    private Set ajaxControlList;

    /**
     * Register the control to be processed by the ClickServlet for Ajax
     * requests.
     *
     * @param control the control to register
     */
    public static void registerAjaxControl(Control control) {
        Validate.notNull(control, "Null control parameter");

        ControlRegistry instance = (ControlRegistry) THREAD_LOCAL_REGISTRY.get();
        if (instance == null) {
            instance = new ControlRegistry();
            THREAD_LOCAL_REGISTRY.set(instance);
        }
        Set controlList = instance.getAjaxControls();
        controlList.add(control);
    }

    Set getAjaxControls() {
        if (ajaxControlList == null) {
            ajaxControlList = new LinkedHashSet();
        }
        return ajaxControlList;
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
            for (Iterator it = controlList.iterator(); it.hasNext(); ) {
                Control control = (Control) it.next();

                // Check if control is targeted by this request
                if (context.getRequestParameter(control.getId()) != null) {
                    control.onProcess();
                }
            }

            // Fire the registered listeners
            return ActionEvents.fireActionEvents(context);

        } else {
            return true;
        }
    }

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
     * Clear all the registered controls.
     */
    static void clearRegistry() {
        THREAD_LOCAL_REGISTRY.set(null);
    }
}
