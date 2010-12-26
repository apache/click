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
package org.apache.click.examples.page.introduction;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.ActionLink;
import org.apache.click.examples.page.BorderPage;

/**
 * Provides a control listener example Page using the compile time binding of
 * the control ActionListener.
 * <p/>
 * The advantage of this control listener binding style is you get compile
 * time safety and compiler refactoring support, the disadvantage is that you
 * have to write more lines of code.
 */
public class ControlListenerType2Page extends BorderPage {

    private static final long serialVersionUID = 1L;

     private ActionLink myLink = new ActionLink("myLink");

    // Constructor ------------------------------------------------------------

    /**
     * Create a new Page instance.
     */
    public ControlListenerType2Page() {
        addControl(myLink);

        myLink.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control control) {
                 String msg = "ControlListenerPage#" + hashCode()
                 + " object method <tt>onAction()</tt> invoked.";
                 addModel("msg", msg);

             return true;
            }
        });
    }

}
