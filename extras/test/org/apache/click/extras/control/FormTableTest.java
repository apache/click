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
package org.apache.click.extras.control;

import java.util.List;

import junit.framework.TestCase;

import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.element.Element;
import org.apache.click.util.PageImports;

public class FormTableTest extends TestCase {

    /**
     * Check that both Table and Form imports are included.
     * CLK-453
     */
    public void testGetHeadElements() {
        MockContext.initContext();

        Page page = new Page();
        PageImports pageImports = new PageImports(page);

        // Check imports using an internal Form Control
        FormTable table = new FormTable("table");

        pageImports.processControl(table);
        List<Element> headElements = pageImports.getHeadElements();
        List<Element> jsElements = pageImports.getJsElements();

        assertTrue(headElements.get(0).toString().contains("/table.css"));
        assertTrue(headElements.get(1).toString().contains("/control.css"));
        assertTrue(jsElements.get(0).toString().contains("/control.js"));


        // Check imports using an external Form Control
        page = new Page();
        pageImports = new PageImports(page);

        Form form = new Form("form");
        table = new FormTable("table", form);
        form.add(table);

        pageImports.processControl(form);
        headElements = pageImports.getHeadElements();
        jsElements = pageImports.getJsElements();

        assertTrue(headElements.get(0).toString().contains("/control.css"));
        assertTrue(headElements.get(1).toString().contains("/table.css"));
        assertTrue(jsElements.get(0).toString().contains("/control.js"));
    }
}
