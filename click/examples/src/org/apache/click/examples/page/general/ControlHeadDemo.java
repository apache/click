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

import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.element.CssImport;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.HtmlStringBuffer;

/**
 * This example demonstrates how to manipulate the Head elements of a
 * custom StarRating Control.
 */
public class ControlHeadDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    public ControlHeadDemo() {

        Form form = new Form("form");

        StarRating rating = new StarRating("rating", 5, 2);
        form.add(rating);

        form.add(new Submit("save"));

        addControl(form);
    }

    /**
     * A custom StarRating Control based on the JQuery Rating plugin.
     */
    public class StarRating extends Field {

        private static final long serialVersionUID = 1L;

        private int maxStars;

        public StarRating(String name, int maxStars, int selectedValue) {
            super(name);
            this.maxStars = maxStars;
            setValue(Integer.toString(selectedValue));
        }

        /**
         * Return the list of HEAD elements.
         *
         * @return list the list of HEAD elements
         */
        @Override
        public List<Element> getHeadElements() {
            if (headElements == null) {
                headElements = super.getHeadElements();

                // Add the JQuery library to the control
                headElements.add(new JsImport("/assets/js/jquery-1.4.2.js"));

                // Add the Rating JavaScript library to the control
                headElements.add(new JsImport("/assets/rating/jquery.rating.js"));

                // Add the Rating Css to the control
                headElements.add(new CssImport("/assets/rating/jquery.rating.css"));
            }
            return headElements;
        }

        /**
         * Render the HTML representation of the StarRating control.
         *
         * @param buffer the buffer to render the output to
         */
        @Override
        public void render(HtmlStringBuffer buffer) {
            // Render a radio button for each star
            for (int i = 1; i <= maxStars; i++) {
                String strValue = Integer.toString(i);
                buffer.elementStart("input");
                buffer.appendAttribute("type", "radio");
                buffer.appendAttribute("name", getName());
                buffer.appendAttribute("value", strValue);
                buffer.appendAttribute("class", "star");
                if (strValue.equals(getValue())) {
                    buffer.appendAttribute("checked", "checked");
                }
                buffer.elementEnd();
                buffer.append("\n");
            }
        }
    }
}
