package net.sf.click.control;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.MockRequest;

public class TableTest extends TestCase {

    public void testNoRows() {
        MockRequest request = new MockRequest();
        MockContext.initContext(request);

        Table table = new Table();
        Column column = new Column("Foo");
        column.setSortable(false);
        table.addColumn(column);

        String header = "<thead>\n<tr>\n<th>Foo</th></tr></thead>\n";
        String body = "<tbody>\n<tr class=\"odd\"><td colspan=\"1\" class=\"error\">No records found.</td></tr>\n</tbody>";
        assertEquals("<table>\n" + header + body + "</table>\n", table.toString());
    }

    public void testTdId() {
        MockRequest request = new MockRequest();
        MockContext.initContext(request);
        
        List foos = new ArrayList();
        foos.add(new Foo("foo1"));
        foos.add(new Foo("foo2"));

        Table table = new Table();
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
