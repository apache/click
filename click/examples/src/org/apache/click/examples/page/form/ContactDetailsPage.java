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
package org.apache.click.examples.page.form;

import org.apache.click.examples.control.html.cssform.ContactDetailsForm;
import org.apache.click.examples.page.BorderPage;

/**
 * This page demonstrates how to manually layout a form using Java.
 *
 * The form is laid out as described in the sitepoint
 * article: http://www.sitepoint.com/print/fancy-form-design-css
 *
 * @author Bob Schellink
 */
public class ContactDetailsPage extends BorderPage {

    private ContactDetailsForm form;

    public void onInit() {
        super.onInit();
        form = new ContactDetailsForm("form");
        addControl(form);
    }

    public String getTemplate() {
        return "/form/another-border.htm";
    }
}
