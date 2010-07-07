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

import junit.framework.Assert;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;

/**
 * Page which tests action listener functionality.
 */
public class ListenerPage extends Page {
    private static final long serialVersionUID = 1L;

    /** Form holder. */
    public Form form = new Form("form");
    
    /** Indicates if the submit Assertion succeeded or not. */
    public boolean success = false;

    /**
     * Initialize page.
     */
    @Override
    public void onInit() {

        // Create and add submit button *before* adding the textField
        Submit submit = new Submit("save");
        form.add(submit);

        // Add listener on submit button
        submit.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                // Assert that this listener can access the textfield value
                Assert.assertEquals("one", form.getFieldValue("field"));
                success = true;
                return true;
            }
        });

        // Add textfield after the button.
        form.add(new TextField("field"));
    }
}
