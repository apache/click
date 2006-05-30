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
            assertTrue(re.getCause() instanceof NoSuchMethodException);
        }
    }
    
    public static class TestObject {
        private String name;
        private Object value;
        
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
    }

}
