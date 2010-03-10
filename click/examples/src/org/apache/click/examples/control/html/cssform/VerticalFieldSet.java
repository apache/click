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

import org.apache.click.Control;
import org.apache.click.control.Field;
import org.apache.click.examples.control.html.FeedbackBorder;
import org.apache.click.examples.control.html.FieldLabel;
import org.apache.click.examples.control.html.list.HtmlList;
import org.apache.click.examples.control.html.list.ListItem;
import org.apache.click.extras.control.HtmlFieldSet;

/**
 * A custom FieldSet that renders its fields vertically using an HtmlList
 * instance.
 */
public class VerticalFieldSet extends HtmlFieldSet {

    private static final long serialVersionUID = 1L;

    private HtmlList htmlList = new HtmlList();

    public VerticalFieldSet(String name) {
        super(name);
        add(htmlList);
    }

    // Public Methods ---------------------------------------------------------

    public Control insert(Control control, int index) {
        if (!(control instanceof HtmlList)) {
            throw new IllegalArgumentException("only HtmlLists can be added to CssFieldSet.");
        }
        return super.insert(control, index);
    }

    public Field add(Field field) {
        return add(field, null);
    }

    public Field add(Field field, String labelStr) {
        ListItem item = new ListItem();
        htmlList.add(item);

        field.setAttribute("class", "text");
        FieldLabel label = null;
        if (labelStr != null) {
            label = new FieldLabel(field, labelStr);
        } else {
            label = new FieldLabel(field);
        }
        item.add(label);

        FeedbackBorder border = new FeedbackBorder();
        border.add(field);
        item.add(border);
        return field;
    }

    public HtmlList getHtmlList() {
        return htmlList;
    }
}
