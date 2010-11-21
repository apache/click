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
package org.apache.click.ajax;

import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.ActionResult;

/**
 * Provides a default implementation of the AjaxBehavior interface.
 * <p/>
 * This class also provides the method,
 * {@link #addHeadElementsOnce(org.apache.click.Control) addHeadElementsOnce},
 * that subclasses can implement if they need to add HTML HEAD elements only to
 * the <tt>first</tt> Control that this Behavior is registered with.
 * <p/>
 * If this Behavior should add HTML HEAD elements to all the Controls it is
 * registered with, rather implement
 * {@link #preRenderHeadElements(org.apache.click.Control) preRenderHeadElements}.
 */
public class DefaultAjaxBehavior implements AjaxBehavior {

    // Variables --------------------------------------------------------------

    /** Indicates whether the Behavior HEAD elements have been processed or not. */
    protected boolean headElementsProcessed = false;

    // Behavior Methods -------------------------------------------------------

    /**
     * @see org.apache.click.ajax.AjaxBehavior#onAction(org.apache.click.Control)
     *
     * @param source the control the behavior is registered with
     * @return the action result
     */
    public ActionResult onAction(Control source) {
        return null;
    }

    /**
     * @see org.apache.click.ajax.AjaxBehavior#isAjaxTarget(org.apache.click.Context)
     *
     * @param context the request context
     * @return true if the behavior is the request target, false otherwise
     */
    public boolean isAjaxTarget(Context context) {
        return true;
    }

    // Callback Methods -------------------------------------------------------

    /**
     * @see org.apache.click.Behavior#preResponse(org.apache.click.Control)
     *
     * @param source the control the behavior is registered with
     */
    public void preResponse(Control source) {
    }

    /**
     * @see org.apache.click.Behavior#preRenderHeadElements(org.apache.click.Control)
     *
     * @param source the control the behavior is registered with
     */
    public void preRenderHeadElements(Control source) {
        // Guard against adding HEAD elements to more than one control
        if (headElementsProcessed) {
            return;
        }

        addHeadElementsOnce(source);

        headElementsProcessed = true;
    }

    /**
     * @see org.apache.click.Behavior#preDestroy(org.apache.click.Control)
     *
     * @param source the control the behavior is registered with
     */
    public void preDestroy(Control source) {
        headElementsProcessed = false;
    }

    // Protected methods ------------------------------------------------------

    /**
     * Provides a method for adding HTML HEAD elements to the first Control
     * this Behavior was registered with. This method will only be called once,
     * passing in the first Control the Behavior was registered with.
     * <p/>
     * Subclasses can implement this method instead of
     * {@link #preRenderHeadElements(org.apache.click.Control)} if HTML HEAD
     * elements should only be added to one Control, even if the Behavior is
     * added to multiple Controls.
     *
     * @param source the control the behavior is registered with
     */
    protected void addHeadElementsOnce(Control source) {
        // Subclasses can override the default to add head specific elements
        // NOTE: if this method is ever made public the headElementsProcessed
        // check should be done here
    }
}
