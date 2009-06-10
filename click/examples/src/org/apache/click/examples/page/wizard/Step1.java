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
package org.apache.click.examples.page.wizard;

import org.apache.click.control.TextField;
import org.apache.click.extras.cayenne.QuerySelect;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;

/**
 * The first step in the 3 step process is to capture the Client details.
 * <p/>
 * Note this Panel has no associated template.
 *
 * @author Bob Schellink
 */
public class Step1 extends Step {

    private static final long serialVersionUID = 1L;

    /**
     * Construct Step1 with the specified name, label, description and page.
     *
     * @param name the step name
     * @param label the step label
     * @param description the step description
     * @param page the wizard page
     */
    public Step1(String name, String label, String description, WizardPage page) {
        super(name, label, description, page);

        QuerySelect querySelect = new QuerySelect("title", true);
        querySelect.setQueryValueLabel("titles", "value", "label");
        getForm().add(querySelect);

        getForm().add(new TextField("firstName"));
        getForm().add(new TextField("lastName"));
        getForm().add(new DateField("dateJoined"));
        getForm().add(new EmailField("email"));
    }

    /**
     * The onNext action of Step1 sets the Page to stateful, checks if the form
     * is valid, moves to the next step in the process and passes the client to
     * the next step.
     *
     * @return true if page processing should continue or not
     */
    public boolean onNext() {
        // Set the page to stateful so the same Page is available throughout the
        // Wizard steps
        getWizardPage().setStateful(true);

        if (getForm().isValid()) {
            // Pass the client to Panel2
            getWizardPage().next();
            getWizardPage().getCurrentStep().setClient(getClient());
        }
        return true;
    }
}
