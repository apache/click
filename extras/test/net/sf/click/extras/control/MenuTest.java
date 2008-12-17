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
package net.sf.click.extras.control;

import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Form;

public class MenuTest extends TestCase {

    /**
     * Check that FileField value is escaped.
     */
    public void testEscapeValue() {
        MockContext.initContext();

        Form form = new Form("form");
        Menu menu = new Menu("menu");
        form.add(menu);

        String value = "<script>";
        String expected = "title=\"&lt;script&gt;\"";

        menu.setTitle(value);
        menu.setLabel(value);

        assertTrue(menu.toString().indexOf(expected) > 1);
    }
}
