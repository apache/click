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

import java.io.Serializable;
import org.apache.click.Behavior;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.ActionResult;

/**
 * Provides an abstract implementation of the Behavior interface.
 *
 * TODO: javadoc
 */
public class AjaxBehavior implements Behavior, Serializable {

    // Constants --------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // Variables --------------------------------------------------------------

    protected boolean headElementsProcessed = false;

    // Behavior Methods--------------------------------------------------------

    /**
     * @see org.apache.click.Behavior#onAction(org.apache.click.Control)
     *
     * @param source the control the behavior is attached to
     * @return the action result
     */
    public ActionResult onAction(Control source) {
        return null;
    }

    /**
     * TODO: javadoc
     *
     * @param context
     * @return
     */
    public boolean isRequestTarget(Context context) {
        return true;
    }

    // Callback Methods -------------------------------------------------------

    /**
     *
     * @param source
     */
    public void preResponse(Control source) {
    }

    public void preGetHeadElements(Control source) {
        // Guard against adding HEAD elements to more than one control
        if (headElementsProcessed) {
            return;
        }

        addHeadElements(source);

        headElementsProcessed = true;
    }

    public void preDestroy(Control source) {
        headElementsProcessed = false;
    }

    // Protected methods ------------------------------------------------------

    protected void addHeadElements(Control source) {
        // Subclasses can override the default to add head specific elements
        // NOTE: if this method is ever made public the headElementsProcessed
        // check should be done here
    }
}