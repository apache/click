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
import org.apache.click.util.HtmlStringBuffer;

/**
 * Test Column behavior.
 */
public class ColumnTest extends TestCase {
	
	public void setUp() {
		 MockContext.initContext();
	}

    /**
     * Sanity checks for Column.
     */
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
            
        } catch (RuntimeException expected) {
            assertTrue(true);
        }
    }

    /**
     * Check that Column renders when binding object is set.
     */
    public void testOuterJoin() {
        TestObject row = new TestObject("name", "label");

        // Test with child object
        row.setChild(new Child("mina"));

    	  Column column = new Column("child.name");

        HtmlStringBuffer buffer = new HtmlStringBuffer();
        column.renderTableData(row, buffer, null, 0);
        assertTrue(buffer.length() > 0);
    }

    /**
     * Check that Column renders when binding object is null.
     */
    public void testNullOuterJoin() {
        // Test with null child object
        TestObject row = new TestObject("name", "label");
       
      	Column column = new Column("child.name");
    	
        HtmlStringBuffer buffer = new HtmlStringBuffer();        
        column.renderTableData(row, buffer, null, 0);
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

        @Override
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
        
        @Override
        public String toString() {
            return "Child [ " + getName() + "]";
        }
    }

}
