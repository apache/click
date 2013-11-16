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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.dataprovider.DataProvider;

/**
 * Test Select behavior.
 */
public class SelectTest extends TestCase {

    /**
     * Check that Select value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Select select = new Select("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";
        select.add(value);
        assertTrue(select.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(select.toString().indexOf(value) < 0);
    }

    /**
     * Test that Select.getState contains the select value.
     * CLK-715
     */
    public void testGetState() {
        // Setup Select
        Select select  = new Select("gender");
        select.add(new Option("male"));
        select.add(new Option("female"));

        String expectedState = "female";
        select.setValue(expectedState);

        // Setup Select value
        List<String> selectedValues = new ArrayList<String>();
        selectedValues.add(expectedState);
        select.setSelectedValues(selectedValues);

        Object state = select.getState();

        // Perform tests
        assertEquals(expectedState, state);
        assertEquals(state, select.getValue());
        assertEquals(state, select.getSelectedValues().get(0));
    }

    /**
     * Test that Select.getState contains the select values.
     * CLK-715
     */
    public void testGetStateMultiple() {
        // Setup Select
        Select select  = new Select("gender");
        select.add(new Option("male"));
        select.add(new Option("female"));
                select.setMultiple(true);

        // Setup Select values
        List<String> selectedValues = new ArrayList<String>();
        selectedValues.add("male");
        selectedValues.add("female");
        select.setSelectedValues(selectedValues);

        String[] state = (String[]) select.getState();

        Object expectedState = "male";
        assertEquals(expectedState, state[0]);

        // Perform tests
        String[] expectedStateArray = new String[] {"male", "female"};
        assertTrue(Arrays.equals(expectedStateArray, state));

        assertTrue(Arrays.equals(state, select.getSelectedValues().toArray()));
    }

    /**
     * Test that Select.setState set the select value.
     *
     * CLK-715
     */
    public void testSetState() {
        // Setup Select
        Select select  = new Select("gender");
        select.add(new Option("male"));
        select.add(new Option("female"));

        String expectedState = "female";

        select.setState(expectedState);

        // Perform tests
        assertEquals(expectedState, select.getValue());
        assertEquals(expectedState, select.getSelectedValues().get(0));

        // Make sure we can still add values to the Select after state is
        // restored
        select.getSelectedValues().add("male");
    }

    /**
     * Test that Select.setState set the select value if multiple is true.
     *
     * CLK-715
     */
    public void testSetStateMultiple() {
        // Setup Select
        Select select  = new Select("gender");
        select.setMultiple(true);
        select.add(new Option("male"));
        select.add(new Option("female"));

        String[] expectedState = {"male", "female"};

        select.setState(expectedState);

        // Perform tests
        assertEquals(expectedState[0], select.getValue());
        assertTrue(Arrays.equals(expectedState, select.getSelectedValues().toArray()));

        // Make sure we can still add values to the Select after state is
        // restored
        select.getSelectedValues().add("male");
    }

    /**
     * CLK-745
     */
    public void testSetInitialValue() {
        // Setup Select
        Select select  = new Select("gender");
        select.add(new Option("male"));
        select.add(new Option("female"));

        String expectedValue = "male";

        // Test initial value
        assertEquals(expectedValue, select.getValue());
        select.setValue(null);
        select.setOptionList(null);

        select.setDataProvider(new DataProvider() {

            public List getData() {
                List list = new ArrayList();
                list.add(new Option("male"));
                list.add(new Option("female"));
                return list;
            }
        });

        // Trigger dataProvider
        select.getOptionList();

        // Test initial value
        assertEquals(expectedValue, select.getValue());
    }

    /**
     * CLK-745
     */
    public void testDataProviderValues() {
        MockContext.initContext();

        // Setup Select
        Select select  = new Select("gender");

        select.setDataProvider(new DataProvider() {

            public List getData() {
                List list = new ArrayList();
                list.add("male");
                return list;
            }
        });

        try {
            // Trigger dataProvider
            select.toString();
            fail("Cannot pass String to dataProvider");
        } catch (IllegalArgumentException expected) {
        }

        select.setDataProvider(new DataProvider() {

            public Set getData() {
                Set set = new LinkedHashSet();
                set.add("male");
                return set;
            }
        });

        try {
            // Trigger dataProvider
            select.toString();
            fail("Cannot pass String to dataProvider");
        } catch (IllegalArgumentException expected) {
        }
    }
}
