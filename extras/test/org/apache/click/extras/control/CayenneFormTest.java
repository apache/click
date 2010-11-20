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
package org.apache.click.extras.control;

import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.control.Field;
import org.apache.click.control.TextField;
import org.apache.click.extras.cayenne.CayenneForm;

/**
 * Provides test cases for CayenneForm.
 */
public class CayenneFormTest extends TestCase {


    /**
     * Test that CayenneForm.getState contains the state of all the Fields in
     * the CayenneForm. Also test that Immutable HiddenFields are not saved as
     * it is unnecessary state to keep in memory.

     * CLK-715
     */
    public void testGetState() {
        MockContext.initContext();

        // Setup CayenneForm and Fields
        String foOid = "100";
        String foClass = "com.mycorp.Person";
        String name = "Steve";

        CayenneForm form = new CayenneForm();
        form.setName("form");
        Field nameField  = new TextField("name");
        nameField.setValue(name);
        form.add(nameField);
        Field foOidField = form.getField(CayenneForm.FO_ID);
        foOidField.setValue(foOid);
        Field foClassField = form.getField(CayenneForm.FO_CLASS);
        foClassField.setValue(foClass);

        // Retrieve state
        Object state = form.getState();
        Map formStateMap = (Map) state;

        // Perform tests
        assertEquals(formStateMap.get(nameField.getName()), name);
        assertEquals(formStateMap.get(foOidField.getName()), foOid);

        // Check that the FO_CLASS isn't persisted. FO_CLASS will always be the
        // same so no need store it in the session
        assertNull(formStateMap.get(foClassField.getName()));

        // Check that only the fields defined above are returned
        assertEquals(2, formStateMap.size());
    }

    /**
     * Test that CayenneForm.setState correctly set the state of the Fields in
     * the CayenneForm.
     *
     * CLK-715
     */
    public void testSetState() {
        MockContext.initContext();

        // Setup CayenneForm and Fields
        CayenneForm form = new CayenneForm();
        form.setName("form");
        Field nameField  = new TextField("name");
        form.add(nameField);
        Field foOidField = form.getField(CayenneForm.FO_ID);

        // Setup state
        Map state = new HashMap();
        String foOid = "100";
        state.put(CayenneForm.FO_ID, foOid);
        String name = "Steve";
        state.put("name", name);
        // Set state
        form.setState(state);

        // Perform tests
        assertEquals(name, nameField.getValue());
        assertEquals(foOid, foOidField.getValue());
    }
}
