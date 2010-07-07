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
package org.apache.click.pages;

import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.TextField;

/**
 * Form test page.
 */
public class FormPage extends Page {
    private static final long serialVersionUID = 1L;

    /** Form instance. */
    private Form form = new Form("form");

    /**
     * Initialize page.
     */
    @Override
    public void onInit() {
        form.add(new TextField("myfield"));
        addControl(form);
    }

    /**
     * Return form instance.
     *
     * @return form instance
     */
    public Form getForm() {
        return form;
    }
}
