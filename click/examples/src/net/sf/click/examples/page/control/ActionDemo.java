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
package net.sf.click.examples.page.control;

import java.util.Date;

import net.sf.click.ActionListener;
import net.sf.click.Control;
import net.sf.click.control.ActionButton;
import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

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

    public ActionLink link = new ActionLink();
    public ActionButton button = new ActionButton();
    public String clicked;

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
