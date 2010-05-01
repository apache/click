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

/**
 * Test Panel behavior.
 */
public class PanelTest extends TestCase {

    /**
     * Check that Panel.getId() returns null when Panel name is null.
     *
     * CLK-464
     */
    public void testProcessControlWhenNameIsNull() {
        // Check that panel without name returns null id
        Panel panel = new Panel();
        assertNull(panel.getId());
        
        // Check that panel with name returns name id
        String name = "mypanel";
        panel = new Panel(name);
        assertEquals(name, panel.getId());
    }

    /**
     * Check that adding controls replace existing controls with the same name.
     *
     * CLK-666
     */
    public void testReplace() {
        Panel panel = new Panel("panel");

        // Add two panels named child1 and child2
        Panel child1 = new Panel("child1");
        Panel child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);
        assertEquals(2, panel.getControlMap().size());
        assertEquals(2, panel.getControls().size());
        assertSame(child1, panel.getControls().get(0));
        assertSame(child2, panel.getControls().get(1));
        assertSame(child1, panel.getPanels().get(0));
        assertSame(child2, panel.getPanels().get(1));

        // Add another two panels named child1 and child2 and test that these
        // panels replaces the previous panels
        child1 = new Panel("child1");
        child2 = new Panel("child2");
        panel.add(child1);
        panel.add(child2);
        assertEquals(2, panel.getControlMap().size());
        assertEquals(2, panel.getControls().size());
        assertSame(child1, panel.getControls().get(0));
        assertSame(child2, panel.getControls().get(1));
        assertSame(child1, panel.getPanels().get(0));
        assertSame(child2, panel.getPanels().get(1));
    }
}
