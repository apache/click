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

import java.util.ArrayList;
import java.util.List;
import org.apache.click.control.AbstractContainer;
import org.apache.click.control.Option;
import org.apache.click.control.PageLink;
import org.apache.click.control.Select;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.examples.domain.Client;
import org.apache.click.examples.domain.SystemCode;
import org.apache.click.examples.service.ClientService;
import org.apache.click.extras.control.IntegerField;

/**
 * The second step in the 3 step process is to capture the Client address.
 * <p/>
 * Note this Panel has no associated template.
 */
public class Step2 extends Step {

    private static final long serialVersionUID = 1L;

    // Variables --------------------------------------------------------------

    /** Reference to the postCode field. */
    private IntegerField postCodeField;

    /** Reference to the state field. */
    private Select stateSelect;

    /** The client domain object created through the wizard. */
    private Client client;

    /** The client service. */
    private ClientService clientService;

    // Constructors -----------------------------------------------------------

    /**
     * Construct Step2 with the specified name, label, description and page.
     *
     * @param name the step name
     * @param label the step label
     * @param description the step description
     * @param page the wizard page
     */
    public Step2(String name, String label, String description, WizardPage page) {
        super(name, label, description, page);

        // PageLink to page where post codes can be looked up
        final PageLink postCodeLookup = new PageLink("postCodePage", "Lookup Post Code", SelectPostCode.class);

        // We want to right align the postCodeLookup link, however fields must
        // be left aligned. In order to do this we wrap postCodeLookupPage inside
        // a div (block level element) and set its contents to be right aligned.
        Div postCodeLookupWrapper = new Div();
        postCodeLookupWrapper.add(postCodeLookup);
        postCodeLookupWrapper.setStyle("text-align", "right");

        getForm().add(postCodeLookupWrapper);

        getForm().add(new TextField("address.line1", "Line One", true));
        getForm().add(new TextField("address.line2", "Line Two"));
        getForm().add(new TextField("address.suburb", "Suburb", true));

        stateSelect = new Select("address.state", "State", true);
        setupStateSelect(stateSelect);

        getForm().add(stateSelect);
        postCodeField = new IntegerField("address.postCode", "Post Code");
        postCodeField.setRequired(true);
        postCodeField.setMaxLength(5);
        postCodeField.setSize(5);
        getForm().add(postCodeField);

        client = WizardUils.getClientFromSession();
        if (client != null) {
            getForm().copyFrom(client);
        }
    }

    // Public methods ---------------------------------------------------------

    /**
     * Step2 links to a lookup table for populating the post code and state values.
     *
     * The onInit phase is overridden to check if the post code and state values
     * are passed in from the lookup table.
     */
    @Override
    public void onInit() {
        // Invoke default onInit implementation
        super.onInit();

        // Check if postCode is passed to this Page from SelectPostCode page
        String postCodeValue = getContext().getRequestParameter(postCodeField.getName());
        if (postCodeValue != null) {
            postCodeField.setValue(postCodeValue);
        }
        // Check if state is passed to this Page from SelectPostCode page
        String stateValue = getContext().getRequestParameter(stateSelect.getName());
        if (stateValue != null) {
            stateSelect.setValue(stateValue);
        }
    }

    /**
     * The onNext action of Step2 checks if the form is valid, moves to the
     * next step in the process and passes the client to the next step.
     *
     * @return true if page processing should continue or not
     */
    @Override
    public boolean onNext() {
        if (getForm().isValid()) {

            getForm().copyTo(client);
            WizardUils.saveClientInSession(client);

            // Pass client to next Step
            getWizardPage().next();
        }
        return true;
    }

    /**
     * The onPrevious action of Step2 moves to the previous step in the process
     * and clears and form errors.
     *
     * @return true if page processing should continue or not
     */
    @Override
    public boolean onPrevious() {
        getWizardPage().previous();
        getForm().clearErrors();
        getForm().copyTo(client);
        WizardUils.saveClientInSession(client);
        return false;
    }

    public ClientService getClientService() {
        if (clientService == null) {
            clientService = new ClientService();
        }
        return clientService;
    }

    // Private methods --------------------------------------------------------

    private void setupStateSelect(Select select) {
        select.setDefaultOption(Option.EMPTY_OPTION);

        select.setDataProvider(new DataProvider() {

            public List<Option> getData() {
                List<Option> options = new ArrayList<Option>();
                List<SystemCode> states = getClientService().getStates();
                for (SystemCode state : states) {
                    options.add(new Option(state.getValue(), state.getLabel()));
                }
                return options;
            }
        });
    }

    // Inner classes ----------------------------------------------------------

    /**
     * Represents a Div HTML element.
     */
    class Div extends AbstractContainer {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTag() {
            return "div";
        }
    }
}

