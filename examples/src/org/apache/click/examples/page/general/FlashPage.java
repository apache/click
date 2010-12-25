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

import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;

/**
 * Provides an example of a flash session attribute.
 */
public class FlashPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private TextField valueField = new TextField("value", "Value:", true);

    public FlashPage() {
        addControl(form);

        form.add(valueField);
        form.add(new Submit("flashPage", "  Flash Page ", this, "onFlashClick"));
        form.add(new Submit("homePage", "  Home Page ", this, "onHomeClick"));
    }

    public boolean onFlashClick() {
        if (form.isValid()) {
            getContext().setFlashAttribute("flash", valueField.getValueObject());
            setRedirect(FlashPage.class);
            return false;
        }
        return true;
    }

    public boolean onHomeClick() {
        if (form.isValid()) {
            getContext().setFlashAttribute("flash", valueField.getValueObject());
            setRedirect(HomePage.class);
            return false;
        }
        return true;
    }

}
