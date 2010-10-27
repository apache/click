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

import junit.framework.TestCase;
import org.apache.click.ajax.DefaultAjaxBehavior;
import org.apache.click.control.TextField;

/**
 * Provides tests for ControlRegistry.
 */
public class ControlRegistryTest extends TestCase {

    /**
     * Check that AbstractControl#addBehavior registers the control as an Ajax
     * target.
     */
    public void testRegisterAsAjaxTarget() {
        MockContext.initContext();
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        TextField field = new TextField("field");
        assertFalse(registry.hasAjaxTargetControls());
        field.addBehavior(new DefaultAjaxBehavior());
        assertTrue(registry.hasAjaxTargetControls());
    }

    /**
     * Check that multiple calls to AbstractControl#addBehavior registers the
     * control as an Ajax target only once.
     */
    public void testRegisterAsAjaxTargetMultipleTimes() {
        MockContext.initContext();
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        TextField field = new TextField("field");

        // Test that adding behavior registers control as ajax target
        assertFalse(registry.hasAjaxTargetControls());
        field.addBehavior(new DefaultAjaxBehavior());
        assertTrue(registry.hasAjaxTargetControls());
        assertEquals(1, registry.getAjaxTargetControls().size());

        // Test that adding another behavior does not register the control twice
        field.addBehavior(new DefaultAjaxBehavior());
        assertEquals(1, registry.getAjaxTargetControls().size());

        // Test that invoking onInit does not register the control twice
        field.onInit();
        assertEquals(1, registry.getAjaxTargetControls().size());
    }

    /**
     * Check that ControlRegistry.registerInterceptor registers the control and
     * behavior as interceptor.
     */
    public void testRegisterInterceptorMethods() {
        MockContext.initContext();
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        TextField field = new TextField("field");

        Behavior interceptor = new DefaultAjaxBehavior();

        // Check interceptor is registered with registry
        assertFalse(registry.hasInterceptors());
        ControlRegistry.registerInterceptor(field, interceptor);
        assertTrue(registry.hasInterceptors());

        assertFalse(registry.hasAjaxTargetControls());
    }

    /**
     * Check that multiple calls to AbstractControl#registerInterceptor registers the
     * control as an Ajax target only once.
     */
    public void testRegisterInterceptorMultipleTimes() {
        MockContext.initContext();
        ControlRegistry registry = ControlRegistry.getThreadLocalRegistry();

        TextField field = new TextField("field");

        Behavior interceptor = new DefaultAjaxBehavior();

        // Check interceptor is registered with registry
        assertFalse(registry.hasInterceptors());
        ControlRegistry.registerInterceptor(field, interceptor);
        assertTrue(registry.hasInterceptors());
        assertEquals(1, registry.getInterceptors().size());

        // Test that adding another interceptor does not register the interceptor twice
        ControlRegistry.registerInterceptor(field, interceptor);
        assertEquals(1, registry.getInterceptors().size());
    }
}
