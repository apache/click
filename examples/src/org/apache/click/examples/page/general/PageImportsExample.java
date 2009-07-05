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
package org.apache.click.examples.page.general;

import java.util.List;
import org.apache.click.element.CssImport;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.PageImports;

/**
 * This page provides an example of how to programatically optimize your
 * PageImports for high traffic pages. You optimize your Page by combinding
 * multiple CSS and JavaScript import files into a single file, which reduces
 * the number of HTTP requests required to serve the page.
 */
public class PageImportsExample extends BorderPage {

    /**
     * Provides an optimized home page imports.
     *
     * @see org.apache.click.Page#getPageImports()
     */
    @Override
    public PageImports getPageImports() {
        PageImports pageImports = super.getPageImports();

        pageImports.addAll(getHeadElements());
        pageImports.setInitialized(true);

        return pageImports;
    }

    /**
     * Return the list of the Page HEAD elements.
     *
     * @return the list of Page HEAD elements
     */
    @Override
    public List getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            headElements.add(new CssImport("/assets/css/imports.css"));
            headElements.add(new JsImport("/assets/js/imports.js"));
            headElements.add(new JsScript("addLoadEvent(function() { initMenu(); });"));
        }
        return headElements;
    }
}