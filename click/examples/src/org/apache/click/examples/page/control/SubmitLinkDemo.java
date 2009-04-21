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

/**
 * This example demonstrates how to use a SubmitLink control together with the
 * Form control.
 *
 * @author Bob Schellink
 */
public class SubmitLinkDemo extends BorderPage {

    /** A submit link. */
    private SubmitLink submitLink = new SubmitLink("submit");

    /** A submit link which includes parameters. */
    private SubmitLink paramLink = new SubmitLink("paramLink");

    /** A submit link used outside of a Form control. */
    private SubmitLink standaloneLink = new SubmitLink("standaloneLink");

    public String demo1Msg;

    public String demo2Msg;

    public SubmitLinkDemo() {
        Form form = new Form("form");
        addControl(form);

        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new TextField("name"));

        // Add the submit link to the fieldSet
        fieldSet.add(submitLink);

        // Add some parameters to the parameterized submit link
        paramLink.setValue("myValue");
        paramLink.setParameter("x", "100");

        // Add the parameterized submit link to the FieldSet
        fieldSet.add(paramLink);

        // The SubmitLink action listener
        submitLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo1Msg = source.getName() + ".onAction invoked at " +
                    (new Date());
                return true;
            }
        });

        // The Parameterized SubmitLink action listener
        paramLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo1Msg = source.getName() + ".onAction invoked at " +
                    (new Date());
                demo1Msg += "<br>Parameters:" + paramLink.getParameters();
                return true;
            }
        });

        // Add the Standalone SubmitLink to the Page
        addControl(standaloneLink);

        // The Standalone SubmitLink action listener
        standaloneLink.setActionListener(new ActionListener() {

            public boolean onAction(Control source) {
                demo2Msg = source.getName() + ".onAction invoked at " +
                    (new Date());
                return true;
            }
        });
    }
}
