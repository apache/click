package net.sf.click.control;

import junit.framework.TestCase;
import net.sf.click.util.HtmlStringBuffer;

public class ColumnTest extends TestCase {
    
    public void testRenderTableData() {
        TestObject row = new TestObject("name", null);
        
        // Test rendering valid property
        Column column1 = new Column("name");
        
        HtmlStringBuffer buffer1 = new HtmlStringBuffer();        
        column1.renderTableData(row, buffer1, null, 0);
        assertTrue(buffer1.length() > 0);
        
        // Test rendering a null property
        Column column2 = new Column("value");
        
        HtmlStringBuffer buffer2 = new HtmlStringBuffer();        
        column2.renderTableData(row, buffer2, null, 0);
        assertTrue(buffer2.length() > 0);        
        
        // Test rendering an invalid property
        try {
            Column column3 = new Column("missing");
            HtmlStringBuffer buffer3 = new HtmlStringBuffer(); 
            column3.renderTableData(row, buffer3, null, 0);
            assertTrue(false);
            
        } catch (RuntimeException re) {
            assertTrue(true);
        }
    }
    
    public void testOuterJoin() {
    	  // Test with null child object
        TestObject row = new TestObject("name", "label");
        row.setChild(new Child());
        
    	  Column column = new Column("child.name");
    	
        HtmlStringBuffer buffer = new HtmlStringBuffer();        
        column.renderTableData(row, buffer, null, 0);
        assertTrue(buffer.length() > 0);        
    }
    
    public void testNullOuterJoin() {
        // Test with null child object
        TestObject row = new TestObject("name", "label");
       
      	Column column = new Column("child.name");
    	
        HtmlStringBuffer buffer = new HtmlStringBuffer();        
        column.renderTableData(row, buffer, null, 0);
        System.out.println(buffer);
        assertTrue(buffer.length() > 0);        
    }
    
    /**
     * Check that textfield value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        String value = "<script>";
        TestObject row = new TestObject(value, null);
        
        // Test rendering valid property
        Column column = new Column("name");
        
        HtmlStringBuffer buffer = new HtmlStringBuffer();        
        column.renderTableData(row, buffer, null, 0);

        String expected = "&lt;script&gt;";
        assertTrue(buffer.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(buffer.toString().indexOf(value) < 0);
    }

    // ---------------------------------------------------------- Inner Classes

    public static class TestObject {

        private String name;

        private Object value;

        private Child child;

        public TestObject(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public Child getChild() {
            return child;
        }

        public void setChild(Child child) {
            this.child = child;
        }

        public String toString() {
            return "TextObject [ " + getName() + ", " + getValue() + "]";
        }
    }

    public static class Child {

        private String name;

        public Child() {
        }

        public Child(String name) {
            setName(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
        public String toString() {
            return "Child [ " + getName() + "]";
        }
    }

}
