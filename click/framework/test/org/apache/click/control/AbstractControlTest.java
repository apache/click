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
package org.apache.click.control;

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.ajax.AjaxBehavior;

/**
 * Test AbstractLink behavior.
 */
public class AbstractControlTest extends TestCase {

    /**
     * Check that AbstractControl#addBehavior registers the control as an AJAX
     * target.
     */
    public void testRegisterAsAjaxTargetThroughAddBehavior() {
        MockContext.initContext();

        AbstractControl control = new AbstractControl() {
            @Override
            public String getTag() {
                return null;
            }
        };

        assertFalse(control.registeredAsAjaxTarget);
        control.addBehavior(new AjaxBehavior());
        assertTrue(control.registeredAsAjaxTarget);

        control.onInit();
    }

    /**
     * Check that AbstractControl#onInit registers the control as an AJAX target
     * if it has behaviors.
     */
    public void testRegisterAsAjaxTargetThroughOnInit() {
        MockContext.initContext();

        AbstractControl control = new AbstractControl() {
            @Override
            public String getTag() {
                return null;
            }
        };

        // Since no behaviors have been added yet, check that onInit does not
        // register the control as an AJAX target
        assertFalse(control.registeredAsAjaxTarget);
        control.onInit();
        assertFalse(control.registeredAsAjaxTarget);

        // Bypass addBehavior which would register the control with the ControlRegistry
        control.getBehaviors().add(new AjaxBehavior());
        assertFalse(control.registeredAsAjaxTarget);

        // Check that onInit does register the control as an AJAX target
        control.onInit();
        assertTrue(control.registeredAsAjaxTarget);
    }
}
