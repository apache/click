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
import java.util.Map;

import org.apache.click.Context;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Field;
import org.apache.click.control.TextField;
import org.apache.click.element.CssImport;
import org.apache.click.element.CssStyle;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.ClickUtils;

/**
 * This example demonstrates how to manipulate the Head elements of a Page.
 */
public class PageHeadDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private ActionLink link;
    private Field field;

    // Constructor ------------------------------------------------------------

    public PageHeadDemo() {
        // When this link is clicked it will toggle the Field's disabled attribute
        link = new ActionLink("link", "Hide");
        link.setId("link-id");

         // Create a new TextField and add it the Page controls
        field = new TextField("field");

        addControl(link);
        addControl(field);
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return the Page list of HEAD elements.
     *
     * @return the Page list of HEAD elements
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Add a Css import to the Page
            headElements.add(new CssImport("/general/page-head-demo.css"));

            // Add inline Css content to the Page that increases the field font-size
            headElements.add(new CssStyle("#" + field.getId() + " { font-size: 18px; }"));

            // Add the JQuery library to the Page
            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));

            // Add a JQuery template which adds a 'click' listener to the link
            // that will show/hide the field
            Context context = getContext();

            // Create a template model and pass in the linkId
            Map jsModel = ClickUtils.createTemplateModel(this, context);
            jsModel.put("linkId", '#' + link.getId());

            String content =
                context.renderTemplate("/general/page-head-demo.js", jsModel);

            headElements.add(new JsScript(content));
        }
        return headElements;
    }
}
