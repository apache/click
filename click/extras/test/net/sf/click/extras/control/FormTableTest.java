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
import org.apache.commons.lang.StringUtils;

public class FormTableTest extends TestCase {

    /**
     * Check that both Table and Form imports are included.
     * CLK-453
     */
    public void testGetHtmlImports() {
        MockContext.initContext();
        
        // Check imports using an internal Form Control
        FormTable table = new FormTable("table");

        String imports = table.getHtmlImports();
        assertEquals(1, StringUtils.countMatches(imports, "/table.css"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.js"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.css"));


        // Check imports using an external Form Control
        Form form = new Form("form");
        table = new FormTable("table", form);
        form.add(table);

        imports = form.getHtmlImports();
        assertEquals(1, StringUtils.countMatches(imports, "/table.css"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.js"));
        assertEquals(1, StringUtils.countMatches(imports, "/control.css"));
    }
}
