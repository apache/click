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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test Column Comparator behavior.
 */
public class ColumnCompareTest extends TestCase {

    /**
     * Check that Column sorting using the built in comparator works properly.
     */
    public void test_1() {
        Column column = new Column("name");

        Table table = new Table("table");
        table.addColumn(column);

        Column.ColumnComparator comparator = new Column.ColumnComparator(column);
        List rowList = createRowList1();
        
        assertTrue(indexOf("-234", rowList) == 11);
        assertTrue(indexOf(Boolean.TRUE, rowList) == 3);
        assertTrue(indexOf("Data 213 Services", rowList) == 5);

        Collections.sort(rowList, comparator);
        System.out.println(rowList);

        // Perform spot checks for ascending order
        // "-234" should be first
        assertTrue(indexOf("-234", rowList) == 0);
        // true should be last
        assertTrue(indexOf(Boolean.TRUE, rowList) == rowList.size() - 1);
        assertTrue(indexOf("Data 213 Services", rowList) == 8);

        table.setSortedAscending(false);
        Collections.sort(rowList, comparator);
        
        // Perform spot checks for descending order
        // "-234" should be last
        assertTrue(indexOf("-234", rowList) == rowList.size() - 1);
        // true should be first
        assertTrue(indexOf(Boolean.TRUE, rowList) == 0);
        assertTrue(indexOf("Data 213 Services", rowList) == 10);

        System.out.println(rowList);
    }

    /**
     * Another check that Column sorting using the built in comparator works
     * properly.
     */
    public void test_2() {
        Column column = new Column("name");

        Table table = new Table("table");
        table.addColumn(column);

        Column.ColumnComparator comparator = new Column.ColumnComparator(column);
        List rowList = createRowList2();
        Collections.sort(rowList, comparator);

        // Check sort order for ascending
        // null should be first
        assertTrue(indexOf(null, rowList) == 0);
        // true should be last
        assertTrue(indexOf(Boolean.TRUE, rowList) == rowList.size() - 1);
        // false should be in the middle
        assertTrue(indexOf(Boolean.FALSE, rowList) == 1);

        table.setSortedAscending(false);
        Collections.sort(rowList, comparator);
        
        // Check sort order for descending
        // null should be last
        assertTrue(indexOf(null, rowList) == rowList.size() - 1);
        // true should be first
        assertTrue(indexOf(Boolean.TRUE, rowList) == 0);
        // false should still be in the middle
        assertTrue(indexOf(Boolean.FALSE, rowList) == 1);
    }

    /**
     * Create and return a test Table row list.
     *
     * @return a test Table row list
     */
    private List createRowList1() {
        List rowList = new ArrayList();

        rowList.add(createRow("Dht"));
        rowList.add(createRow("DHT"));
        rowList.add(createRow(Boolean.FALSE));
        rowList.add(createRow(Boolean.TRUE));
        rowList.add(createRow("Data Services"));
        rowList.add(createRow("Data 213 Services"));
        rowList.add(createRow("Data 123 Services"));
        rowList.add(createRow("Data 2.1.3 Services"));
        rowList.add(createRow("Data 1.2.3 Services"));
        rowList.add(createRow("0123"));
        rowList.add(createRow("1234"));
        rowList.add(createRow("-234"));
        rowList.add(createRow("32-34"));
        rowList.add(createRow("23-34"));
        rowList.add(createRow("item4"));
        rowList.add(createRow("item3"));
        rowList.add(createRow("item2"));
        rowList.add(createRow("item1"));
        rowList.add(createRow("item10"));

        return rowList;
    }

    /**
     * Create and return a test Table row list.
     *
     * @return a test Table row list
     */
    private List createRowList2() {
        List rowList = new ArrayList();

        rowList.add(createRow(null));
        rowList.add(createRow(Boolean.TRUE));
        rowList.add(createRow(Boolean.FALSE));

        return rowList;
    }

    /**
     * Create and return a map representing a Table row.
     *
     * @param value the of the row
     * @return a map representing a Table row
     */
    private Map createRow(Object value) {
        return Collections.singletonMap("name", value);
    }

    /**
     * Return the index of the specified object in the rowList.
     *
     * @param value the object which index to find
     * @param rowList the rowList to find the object in
     * @return the index of the object in the rowList
     */
    private int indexOf(Object value, List rowList) {
        for (int i = 0; i < rowList.size(); i++) {
            Map row = (Map) rowList.get(i);
            // Check for null value
            if (value == null) {
                if (row.get("name") == null) {
                    return i;
                }
            } else if (value.equals(row.get("name"))) {
                return i;
            }
        }
        return -1;
    }
}