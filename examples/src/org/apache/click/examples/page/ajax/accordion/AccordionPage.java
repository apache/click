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
package org.apache.click.examples.page.ajax.accordion;

import java.util.HashMap;
import java.util.List;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides a demo of the <a target="_blank" class="external" href="http://flowplayer.org/tools/demos/tabs/accordion.html">jQuery Tools</a>
 * Accordion.
 */
public class AccordionPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Override
    public List<Element> getHeadElements() {

        // Lazily load head elements
        if (headElements == null) {
            headElements = super.getHeadElements();

            headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));
            headElements.add(new JsImport("/assets/js/jquery.tools.min.js"));
            headElements.add(new CssImport("/assets/css/tabs-accordion.css"));

            // Note the actual JavaScript necessary to setup the accordion is
            // specified in the Page JavaScript template -> accordion.js.
            headElements.add(new JsScript("/ajax/accordion/accordion.js", new HashMap()));

            // Alternatively, the JsScript below could be used to add
            // the necessary JavaScript to setup the accordion, for example:
            /*
            String content =
                "$(document).ready(function() {"
              + "  $('#accordion').tabs('#accordion div', {"
              + "    tabs: 'h2',"
              + "    effect: 'slide'"
              + "  });"
              + "});";
            headElements.add(new JsScript(content));
            */
        }
        return headElements;
    }
}
