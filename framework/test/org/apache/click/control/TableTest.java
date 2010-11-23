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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.Map;
import junit.framework.TestCase;
import org.apache.click.MockContext;

/**
 * Test Table behavior.
 */
public class TableTest extends TestCase {

    /**
     * Check that Table prints message when no records are set.
     */
    public void testNoRows() {
        MockContext.initContext(Locale.ENGLISH);

        Table table = new Table();
        Column column = new Column("Foo");
        column.setSortable(false);
        table.addColumn(column);

        String header = "<thead>\n<tr>\n<th>Foo</th></tr></thead>\n";
        String body = "<tbody>\n<tr class=\"odd\"><td colspan=\"1\" class=\"error\">No records found.</td></tr>\n</tbody>";
        assertEquals("<table>\n" + header + body + "</table>\n", table.toString());
    }

    /**
     * Check that Column id's are rendered properly.
     */
    public void testTdId() {
        MockContext.initContext(Locale.ENGLISH);
        
        List<Foo> foos = new ArrayList<Foo>();
        foos.add(new Foo("foo1"));
        foos.add(new Foo("foo2"));

        Table table = new Table();
        table.setRenderId(true);
        table.setName("Foos");
        table.setRowList(foos);
        Column column = new Column("Name");
        column.setSortable(false);
        table.addColumn(column);

        String header = "<thead>\n<tr>\n<th>Name</th></tr></thead>\n";
        String row1 = "<tr class=\"odd\">\n<td id=\"Foos-Name_0\">foo1</td></tr>\n";
        String row2 = "<tr class=\"even\">\n<td id=\"Foos-Name_1\">foo2</td></tr>";
        String body = "<tbody>\n" + row1 + row2 + "</tbody>";
        assertEquals("<table id=\"Foos\">\n" + header + body + "</table>\n", table.toString());
    }

    /**
     * Check Table paging shows correct page.
     */
    public void testPagingCurrentPage() {
        MockContext.initContext(Locale.ENGLISH);

        List<Foo> foos = new ArrayList<Foo>();
        for (int i = 0; i < 1000; i++) {
            foos.add(new Foo("foo" + i));
        }

        Table table = new Table("table");
        table.setRowList(foos);
        table.setPageSize(10);
        table.setPageNumber(0);
        Column column = new Column("name");
        column.setSortable(false);
        table.addColumn(column);

        // Since page number is zero based check that if page number is 0,
        // Page 1 is the current page
        assertTrue(table.toString().indexOf("<strong>1</strong>") > 0);

        table.setPageNumber(99);

        // Check that if page number is 99, Page 100 is the current page
        assertTrue(table.toString().indexOf("<strong>100</strong>") > 0);
    }

    /**
     * Check that table row attributes are set.
     */
    public void testSetRowAttributes() {
        MockContext.initContext(Locale.ENGLISH);

        List<Foo> foos = new ArrayList<Foo>();
        for (int i = 0; i < 3; i++) {
            foos.add(new Foo("foo" + i));
        }

        Table table = new Table("table") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void addRowAttributes(Map attributes, Object row, int rowIndex) {
                Foo foo = (Foo) row;
                attributes.put("id", foo.getName());
                attributes.put("class", "foo bar");
            }
        };
        table.setRowList(foos);
        Column column = new Column("name");
        table.addColumn(column);

        // Check that a row with the id=foo0 is available
        assertTrue(table.toString().indexOf("<tr id=\"foo0\"") > 0);

        // Check that a row with the class=foo bar is available
        assertTrue(table.toString().indexOf("<tr id=\"foo0\" class=\"foo bar") > 0);
    }

    /**
     * Helper class for <tt>testRowId</tt>.
     */
    public static class Foo {
        private String name;

        public Foo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Test CLK-673 caption
     */
    public void testCaption() {
        MockContext.initContext(Locale.ENGLISH);

        List<Foo> foos = new ArrayList<Foo>();
        foos.add(new Foo("foo1"));
        foos.add(new Foo("foo2"));

        Table table = new Table("table");
        table.setCaption("caption<tt>tt</tt>");
        table.setRowList(foos);
        Column column = new Column("name");
        table.addColumn(column);

        assertTrue(table.toString().contains("<caption>caption<tt>tt</tt></caption>"));
    }

    /**
     * Test that Table.getState contains the table internal state.
     * CLK-715
     */
    public void testGetState() {
        // Setup table

        Table table  = new Table("table");
        // Set table state
        int pageNumber = 5;
        boolean ascending = false;
        String sortedColumn = "Dummy";
        String linkValue = "myval";

        table.setPageNumber(pageNumber);
        table.setSortedAscending(ascending);
        table.setSortedColumn(sortedColumn);
        table.getControlLink().setValue(linkValue);

        // Retrieve table state
        Object[] state = (Object[]) table.getState();

        // Perform tests
        assertEquals(state[0], pageNumber);
        assertEquals(state[1], sortedColumn);
        assertEquals(state[2], ascending);

        Map controlLinkParams = (Map) state[3];

        assertEquals(controlLinkParams, table.getControlLink().getParameters());
        assertEquals(controlLinkParams.get("value"), linkValue);
    }

    /**
     * Test that Table.setState set the table internal state.
     *
     * CLK-715
     */
    public void testSetState() {
                // Setup table

        Table table  = new Table("table");
        // Set table state
        int pageNumber = 5;
        boolean ascending = false;
        String sortedColumn = "Dummy";
        String linkValue = "myval";

        Object[] state = new Object[4];
        state[0] = Integer.valueOf(pageNumber);
        state[1] = sortedColumn;
        state[2] = Boolean.valueOf(ascending);
        Map controlLinkParams = new HashMap();
        controlLinkParams.put("value", linkValue);
        state[3] = controlLinkParams;

        // Set table state
        table.setState(state);

        // Perform tests
        assertEquals(pageNumber, table.getPageNumber());
        assertEquals(sortedColumn, table.getSortedColumn());
        assertEquals(ascending, table.isSortedAscending());
        assertEquals(controlLinkParams, table.getControlLink().getParameters());
        assertEquals(linkValue, table.getControlLink().getValue());
    }

    /**
     * Test CLK-241. Table Headers use incorrect title attribute (table-last-title)
     * when sortable=true.
     */
    public void testNoTitleOnLinkWhenSorting() {
        MockContext.initContext(Locale.ENGLISH, "/mock.htm");

        List<Foo> foos = new ArrayList<Foo>();
        foos.add(new Foo("foo1"));
        foos.add(new Foo("foo2"));

        Table table = new Table("table");
        table.setPageSize(1);
        table.setPaginatorAttachment(Table.PAGINATOR_ATTACHED);
        table.setBannerPosition(Table.POSITION_TOP);
        table.setSortable(true);
        table.setRowList(foos);
        Column column = new Column("name");
        table.addColumn(column);

        // Test that Name header column does not render a title attribute
        // that was set by the TablePaginator
        assertTrue(table.toString().contains("<th class=\"sortable\"><a href=\"/mock/mock.htm?actionLink=table-controlLink&amp;column=name&amp;page=0\">Name"));
    }

}
