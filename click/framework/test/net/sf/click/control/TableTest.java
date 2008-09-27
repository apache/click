package net.sf.click.control;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import junit.framework.TestCase;
import net.sf.click.MockContext;

public class TableTest extends TestCase {

    public void testNoRows() {
        MockContext.initContext();

        Table table = new Table();
        Column column = new Column("Foo");
        column.setSortable(false);
        table.addColumn(column);

        String header = "<thead>\n<tr>\n<th>Foo</th></tr></thead>\n";
        String body = "<tbody>\n<tr class=\"odd\"><td colspan=\"1\" class=\"error\">No records found.</td></tr>\n</tbody>";
        assertEquals("<table>\n" + header + body + "</table>\n", table.toString());
    }

    public void testTdId() {
        MockContext.initContext();
        
        List foos = new ArrayList();
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

    public void testPagingCurrentPage() {
        MockContext.initContext();

        List foos = new ArrayList();
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

    public void testSetRowAttributes() {
        MockContext.initContext();

        List foos = new ArrayList();
        for (int i = 0; i < 3; i++) {
            foos.add(new Foo("foo" + i));
        }

        Table table = new Table("table") {
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

}
