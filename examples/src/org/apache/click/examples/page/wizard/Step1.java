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
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.examples.domain.Address;
import org.apache.click.examples.domain.Client;
import org.apache.click.examples.domain.SystemCode;
import org.apache.click.examples.service.ClientService;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.EmailField;

/**
 * The first step in the 3 step process is to capture the Client details.
 * <p/>
 * Note this Panel has no associated template.
 */
public class Step1 extends Step {

    private static final long serialVersionUID = 1L;

    // Variables --------------------------------------------------------------

    /** The client domain object created through the wizard. */
    private Client client;

    /** The client service. */
    private ClientService clientService;

    // Constructors -----------------------------------------------------------

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

        Select titleSelect = new Select("title", true);
        setupTitleSelect(titleSelect);
        getForm().add(titleSelect);

        getForm().add(new TextField("firstName", true));
        getForm().add(new TextField("lastName", true));
        getForm().add(new DateField("dateJoined", true));
        getForm().add(new EmailField("email"));

        client = WizardUils.getClientFromSession();
        if (client != null) {
            getForm().copyFrom(client);
        }
    }

    // Public methods ---------------------------------------------------------

    /**
     * The onNext action of Step1 sets the Page to stateful, checks if the form
     * is valid, moves to the next step in the process and passes the client to
     * the next step.
     *
     * @return true if page processing should continue or not
     */
    @Override
    public boolean onNext() {
        if (getForm().isValid()) {

            // Only create client if no client was loaded from the session in this
            // Step's constructor. This allows the user to freely navigate backwards
            // and forwards through the wizard without overwriting a previous Client
            // instance
            if (client == null) {
                ClientService service = getClientService();
                client = service.createNewClient();
                Address address = service.createNewAddress();
                client.setAddress(address);
            }
            getForm().copyTo(client);
            WizardUils.saveClientInSession(client);

            getWizardPage().next();
        }
        return true;
    }

    public ClientService getClientService() {
        if (clientService == null) {
            clientService = new ClientService();
        }
        return clientService;
    }

    // Private methods --------------------------------------------------------

    private void setupTitleSelect(Select select) {
        select.setDefaultOption(Option.EMPTY_OPTION);

        select.setDataProvider(new DataProvider() {

            public List<Option> getData() {
                List<Option> options = new ArrayList<Option>();
                List<SystemCode> titles = getClientService().getTitles();
                for (SystemCode title : titles) {
                    options.add(new Option(title.getValue(), title.getLabel()));
                }
                return options;
            }
        });
    }
}
