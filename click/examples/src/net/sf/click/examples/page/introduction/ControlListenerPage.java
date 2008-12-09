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
package net.sf.click.examples.page.introduction;

import net.sf.click.control.ActionLink;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides a control listener example Page.
 *
 * @author Malcolm Edgar
 */
public class ControlListenerPage extends BorderPage {

    /* Public scope controls are automatically added to the page. */
    public ActionLink myLink = new ActionLink();

    /* Public scope variable are automatically added to the model. */
    public String msg;

    // ----------------------------------------------------------- Constructors

    /**
     * Create a new Page instance.
     */
    public ControlListenerPage() {
        myLink.setListener(this, "onMyLinkClick");
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * Handle the myLink control click event.
     */
    public boolean onMyLinkClick() {
        msg = "ControlListenerPage#" + hashCode()
            + " object method <tt>onMyLinkClick()</tt> invoked.";

        return true;
    }

}
