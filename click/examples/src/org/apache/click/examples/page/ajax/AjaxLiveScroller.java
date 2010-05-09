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
package org.apache.click.examples.page.ajax;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

/**
 * Demonstrates how to to dynamically load more customer while scrolling down
 * the Page.
 */
@Component
public class AjaxLiveScroller extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Specifies the number of customers to retrieve at a time
    private int pageSize = 10;

    // Event Handlers --------------------------------------------------------

    @Override
    public void onGet() {
        // Check if the offset parameter was received.
        int offset = NumberUtils.toInt(getContext().getRequest().getParameter("offset"));

        if (offset < 0) {
            // If no offset was given, we assume this is not an Ajax request
            // and set the offset to 0
            offset = 0;

            // We also set a message to display
            addModel("msg", "Top customers");
        }

        // Return customers between the given offset and pageSize
        addModel("customers", customerService.getTopCustomersForPage(offset, pageSize));
    }

    @Override
    public List<Element> getHeadElements() {
        // Lazily load head elements and ensure they are only loaded once
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Add the jQuery library
            headElements.add(new JsImport("/assets/js/jquery-1.3.2.js"));

            // Create a default model and add the pageSize variable to pass to
            // the JavaScript template: ajax-live-scroller.js
            Map<String, Object> model = ClickUtils.createTemplateModel(this, getContext());
            model.put("pageSize", pageSize);

            // Note the actual JavaScript necessary to setup the dynamic scrolling
            // is specified in the Page JavaScript template: ajax-live-scroller.js.
            headElements.add(new JsScript("/ajax/ajax-live-scroller.js", model));
        }
        return headElements;
    }

    // Public Methods ---------------------------------------------------------

    @Override
    public String getTemplate() {
        // For Ajax requests we want to render the Page template only as there is
        // no need to include the Border template in the response
        if (getContext().isAjaxRequest()) {
            return getPath();
        } else {
            return super.getTemplate();
        }
    }
}
