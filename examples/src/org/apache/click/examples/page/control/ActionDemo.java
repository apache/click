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
package org.apache.click.examples.page.control;

import java.util.Date;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.ActionButton;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.Bindable;

/**
 * Provides an ActionLink and ActionButton demonstration Page.
 * <p/>
 * In this example public fields are automatically added to the Page model using
 * their field name. In the case of controls their name will be automatically
 * set to their field name.
 *
 * @author Malcolm Edgar
 */
public class ActionDemo extends BorderPage {

    @Bindable protected ActionLink link = new ActionLink();
    @Bindable protected ActionButton button = new ActionButton();
    @Bindable protected String clicked;

    public ActionDemo() {

        link.setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                clicked = source.getClass().getName() + ".onAction invoked at " + (new Date());
                return true;
            }
        });

        button.setListener(this, "onButtonClick");
    }

    // --------------------------------------------------------- Event Handlers

    public boolean onButtonClick() {
        clicked = getClass().getName() + ".onButtonClick invoked at " + (new Date());
        return true;
    }
}
