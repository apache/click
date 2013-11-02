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

/**
 * Test AbstractContainer behavior.
 */
public class AbstractContainerTest extends TestCase {

    /**
     * Test AbstractContainer#add(Control) and #insert(Control, int)
     * pre conditions.
     */
    public void testInsertPreCondition() {
        MockContext.initContext();
        AbstractContainer container = new AbstractContainer("form") {
            private static final long serialVersionUID = 1L;
        };

        // Check that adding null control fails
        try {
            container.add(null);
            fail("Control cannot be null");
        } catch (Exception expected) {
        }

        // Check that inserting into invalid negative index is caught
        try {
            container.insert(new TextField("field"), -1);
            fail("insert index is out of bounds");
        } catch (Exception expected) {
        }

        // Check that inserting into invalid positive index is caught
        try {
            container.insert(new TextField("field"), 1);
            fail("insert index is out of bounds");
        } catch (Exception expected) {
        }

        // Check that inserting into valid index succeeds
        try {
            container.insert(new TextField("field"), 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail("index 0 should be valid");
        }

        // Create new container for testing
        container = new AbstractContainer("form") {
            private static final long serialVersionUID = 1L;
        };

        // Check that adding TextField with name succeeds
        try {
            container.add(new TextField("field"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Field has name defined and is valid");
        }

        // Check that adding TextField without name fails because Field#setParent
        // ensures that Field name is set
        try {
            container.add(new TextField());
            fail("Field must define a name");
        } catch (Exception expected) {
            // Should fail since Field does not have name defined
        }

        // Check that adding Table without name fails
        try {
            Table table = new Table();
            container.add(table);
            fail("Table must define a name");
        } catch (Exception expected) {
            // Should fail since Table does not have name defined
        }

        // Check that adding Container without name succeeds
        try {
            container.add(new AbstractContainer() {
                private static final long serialVersionUID = 1L;
            });
        } catch (Exception e) {
            e.printStackTrace();
            fail("Container does not need to define name");
        }

        // Check that adding FieldSet with name succeeds
        try {
            container.add(new FieldSet("fieldSet"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("FieldSet has name defined and is valid");
        }

        // Check that adding FieldSet without name fails
        try {
            container.add(new FieldSet());
            fail("FieldSet must define a name");
        } catch (Exception expected) {
            // Should fail since FieldSet does not have name defined
        }

        // Check that adding label without name fails
        try {
            container.add(new Label());
            fail("Label must define a name");
        } catch (Exception expected) {
            // Should fail since Label does not have name defined
        }

        // Check that adding label with name succeeds
        try {
            container.add(new Label("label"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Label has name defined and is valid");
        }

        // Check that adding label with duplicate name also succeeds
        try {
            container.add(new Label("label"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Form should accept labels with duplicate names");
        }

        // Check that adding container to itself fails
        try {
            container.add(container);
            fail("Cannot add container to itself");
        } catch (Exception expected) {
            // Should fail since container cannot be added to itself
        }
    }

    /**
     * Check that adding controls replace existing controls with the same name.
     *
     * CLK-666
     */
    public void testReplace() {
        AbstractContainer container = new AbstractContainer("container") {
            private static final long serialVersionUID = 1L;
        };

        // Add two fields named child1 and child2
        Field child1 = new TextField("child1");
        Field child2 = new TextField("child2");
        container.add(child1);
        container.add(child2);
        assertEquals(2, container.getControlMap().size());
        assertEquals(2, container.getControls().size());
        assertSame(child1, container.getControls().get(0));
        assertSame(child2, container.getControls().get(1));

        // Add another two fields named child1 and child2 and test that these
        // panels replaces the previous fields
        child1 = new TextField("child1");
        child2 = new TextField("child2");
        container.add(child1);
        container.add(child2);
        assertEquals(2, container.getControlMap().size());
        assertEquals(2, container.getControls().size());
        assertSame(child1, container.getControls().get(0));
        assertSame(child2, container.getControls().get(1));
    }
}
