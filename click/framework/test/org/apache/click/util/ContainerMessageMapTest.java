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
package org.apache.click.util;

import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.TextField;

/**
 * Provides a message map test for Containers.
 */
public class ContainerMessageMapTest extends TestCase {

    /**
     * Assert that Control properties are resolved correctly when
     * the Control is part of a hierarchy of Controls e.g. Page -> Form -> Field.
     *
     * CLK-373.
     */
    public void testContainerMessageInheritance() {
        MockContext.initContext(Locale.ENGLISH);

        Page page = new Page();
        MyForm form = new MyForm("myform");
        page.addControl(form);
        Field customField = form.getField("customField");
        Map<String, String> map = form.getMessages();
        assertFalse(map.isEmpty());
        assertTrue(map.size() >= 2);
        assertEquals("Custom Name", customField.getLabel());
        assertEquals("Enter the custom name!", customField.getTitle());
        assertEquals("Custom Name", map.get("customField.label"));
        assertEquals("Enter the custom name!", map.get("customField.title"));
    }

    /**
     * Custom Form class.
     */
    public class MyForm extends Form {
        private static final long serialVersionUID = 1L;

        /**
         * Construct a MyForm instance for the given name.
         * 
         * @param name the name of the form
         */
        public MyForm(String name) {
            super(name);
            buildForm();
        }

        /**
         * Builds the form contents.
         */
        private void buildForm() {
            TextField customField = new TextField("customField");
            this.add(customField);
        }
    }
}
