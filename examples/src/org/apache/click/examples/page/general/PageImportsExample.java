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

import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.PageImports;

public class PageImportsExample extends BorderPage {

    private static final String IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/assets/css/imports.css\" title=\"Style\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/assets/js/imports.js\"></script>\n"
        + "<script type=\"text/javascript\">addLoadEvent(function() '{' initMenu(); '}');</script>";

    /**
     * Provides an optimized home page imports.
     *
     * @see org.apache.click.Page#getPageImports()
     */
    public PageImports getPageImports() {
        PageImports pageImports = super.getPageImports();

        String imports = ClickUtils.createHtmlImport(IMPORTS, getContext());

        pageImports.addImport(imports);
        pageImports.setInitialized(true);

        return pageImports;
    }

    /**
     * @see org.apache.click.Page#getTemplate()
     */
    public String getTemplate() {
        return getPath();
    }

}
