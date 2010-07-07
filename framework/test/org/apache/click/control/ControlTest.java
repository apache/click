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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.Page;

/**
 * Miscellaneous Control tests.
 */
public class ControlTest extends TestCase {

    /**
     * TODO this test is commented because of backward compatibility issues.
     * There are use cases where a Control's name can change for example using
     * the Fly pattern. Perhaps look at introducing
     * Container#changeName(Control control, String newName);
     *
     * Description: A Control cannot change its name after it was added to a 
     * container. The reason is that when a control is added to a container, its
     * name is used as the hash in the container's HashMap. If a controls name 
     * is changed after it was added, it will NOT be possible to find that 
     * control again using its new name, as the HashMap will not be updated with
     * the new name.
     *
     * If a Control does not have its name set, and it tries to set a name
     * after its been added to a container, an exception will still be thrown.
     * 
     * Note: changing a controls name is really a corner case, but it would be
     * better to throw a nice descriptive exception than the alternative, which
     * is not returning the Control.
     */
    public void testChangingControlNameAfterParentIsSet() {
        /*
        Form form = new Form("form");

        // Run test with a Control having a name 
        String fieldName = "field";
        TextField field = new TextField();
        field.setName(fieldName);
        form.add(field);
        
        // Test retrieve field
        Field fieldFound = form.getField(fieldName);
        Assert.assertNotNull(fieldFound);

        // Changing field's name must throw exception once added to a container
        try {
            field.setName("gone");
            fail("Once a control is added to a container, invoking "
                + "setName(String) should throw an exception");
        } catch (IllegalStateException expected) {
            //success
        }


        form = new Form("form");

        // Run test with a Control without having a name 
        field = new TextField();
        form.add(field);

        // Should not be able to retrieve field
        fieldFound = form.getField(fieldName);
        Assert.assertNull(fieldFound);

        // Changing field's name must throw exception once added to a container
        try {
            field.setName("gone");
            fail("Once a control is added to a container, invoking "
                + "setName(String) should throw an exception");
        } catch (IllegalStateException expected) {
            //success
        }
        */
    }

    /**
     * Checks that Click vetoes the addition of a Container to itself.
     *
     * CLK-414.
     */
    public void testAddContainerToItself() {
        MockContext.initContext();
        try {
            Form form = new Form("form");
            form.add(form);
            Assert.assertFalse("Cannot add container to itself", true);
        } catch (RuntimeException expected) {
            Assert.assertTrue(true);
        }
    }

    /**
     * Test getMessage variations.
     */
    public void testGetMessage() {
        MockContext.initContext();

        String expected = "Version 0.21";

        Page page = new Page();
        Field field = new TextField("field");
        page.addControl(field);

        String version = field.getMessage("version");
        System.out.println("V " + version);
        assertEquals(expected, version);

        version = field.getMessage("version", "arg");
        assertEquals(expected, version);

        version = field.getMessage("version", "arg1", "arg2");
        assertEquals(expected, version);

        version = field.getMessage("version", (String) null);
        assertEquals(expected, version);

        Object args[] = new Object[1];
        args[0] = null;
        version = field.getMessage("version", args);
        assertEquals(expected, version);
    }
}