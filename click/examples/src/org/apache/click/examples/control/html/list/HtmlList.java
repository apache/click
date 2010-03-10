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
package org.apache.click.examples.control.html.list;

import java.util.List;

import org.apache.click.Control;
import org.apache.click.control.AbstractContainer;

/**
 * This control provides HTML ordered and unordered lists.
 */
public class HtmlList extends AbstractContainer {

    private static final long serialVersionUID = 1L;

    public static final int UNORDERED_LIST = 0;

    public static final int ORDERED_LIST = 1;

    private int listMode = UNORDERED_LIST;

    public HtmlList() {
    }

    public HtmlList(int listMode) {
        this.listMode = listMode;
    }

    public HtmlList(String name) {
        super(name);
    }

    public HtmlList(String name, int listMode) {
        this(name);
        this.listMode = listMode;
    }

    public Control add(Control control) {
        if (!(control instanceof ListItem)) {
            throw new IllegalArgumentException("Only list items can be added.");
        }
        return super.add(control);
    }

    public String getTag() {
        if (isUnorderedList()) {
            return "ul";
        } else {
            return "ol";
        }
    }

    public int getListMode() {
        return listMode;
    }

    public void setListMode(int listMode) {
        this.listMode = listMode;
    }

    public boolean isUnorderedList() {
        return listMode == UNORDERED_LIST;
    }

    public ListItem getLast() {
        List<Control> items = getControls();
        if (items.size() == 0) {
            throw new IllegalStateException("HtmlList is empty and contains no ListItems.");
        }

        return (ListItem) items.get(items.size() - 1);
    }
}
