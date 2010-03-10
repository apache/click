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
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.SubmitLink;
import org.apache.click.util.Bindable;

/**
 * This example demonstrates how to use a SubmitLink control together with the
 * Form control.
 */
public class SubmitLinkDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    @Bindable protected String demo1Msg;

    @Bindable protected String demo2Msg;

    @Bindable protected String demo3Msg;

    @Bindable protected String demo4Msg;

    // Constructor ------------------------------------------------------------

    public SubmitLinkDemo() {
        demo1();

        demo2();

        demo3();

        demo4();
    }

    // Public Methods --------------------------------------------------------

    public void demo1() {
        // Create a submit link.
        final SubmitLink submitLink = new SubmitLink("submit");

        Form form = new Form("demo1");
        addControl(form);

        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new TextField("name"));

        // Add the submit link to the fieldSet
        fieldSet.add(submitLink);

        // The SubmitLink action listener
        submitLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo1Msg = submitLink.getName() + ".onAction invoked at "
                    + (new Date());
                return true;
            }
        });
    }

    public void demo2() {
        // Create a submit link which includes parameters.
        final SubmitLink paramLink = new SubmitLink("paramLink");

        Form form = new Form("demo2");
        addControl(form);

        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new TextField("name"));

        // Add some parameters to the parameterized submit link
        paramLink.setValue("myValue");
        paramLink.setParameter("x", "100");

        // Add the parameterized submit link to the FieldSet
        fieldSet.add(paramLink);

        // The Parameterized SubmitLink action listener
        paramLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo2Msg = paramLink.getName() + ".onAction invoked at "
                    + (new Date());
                demo2Msg += "<br>Parameters:" + paramLink.getParameters();
                return true;
            }
        });
    }

    public void demo3() {
        // Create a standalone submit link.
        final SubmitLink standaloneLink = new SubmitLink("standaloneLink");

        // Add the Standalone SubmitLink to the Page
        addControl(standaloneLink);

        // The Standalone SubmitLink action listener
        standaloneLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo3Msg = source.getName() + ".onAction invoked at " +
                    (new Date());
                return true;
            }
        });
    }

    public void demo4() {
        // Create a submit link
        final SubmitLink confirmationLink = new SubmitLink("confirmationLink");

        Form form = new Form("demo4");
        addControl(form);

        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new TextField("name"));

        // Add the submit link to the FieldSet
        fieldSet.add(confirmationLink);

        // Set custom JavaScript for the onclick event. The confirmSubmit function
        // is defined in the page template -> submit-link-demo.htm
        String clickEvent = "return confirmSubmit(this, '" + form.getName() + "', 'Are you sure?');";
        confirmationLink.setOnClick(clickEvent);

        // The Parameterized SubmitLink action listener
        confirmationLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo4Msg = confirmationLink.getName() + ".onAction invoked at "
                    + (new Date());
                return true;
            }
        });
    }
}
