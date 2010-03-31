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
package org.apache.click.examples.control.html.cssform;

import org.apache.click.extras.control.HtmlForm;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.HtmlStringBuffer;

/**
 * A custom Form that adds the Html import, "cssform.css", to the page.
 */
public class CssForm extends HtmlForm {

    private static final long serialVersionUID = 1L;

    public CssForm(String name) {
        super(name);
    }

    @Override
    public String getHtmlImports() {
        HtmlStringBuffer buffer = new HtmlStringBuffer(512);

        // Include the Form default imports
        String htmlImports = super.getHtmlImports();
        if (htmlImports != null) {
            buffer.append(htmlImports);
        }

        // Include CSS for ContactDetailsForm
        String imports = "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/assets/css/cssform{1}.css\"/>\n";
        buffer.append(ClickUtils.createHtmlImport(imports, getContext()));
        return buffer.toString();
    }
}
