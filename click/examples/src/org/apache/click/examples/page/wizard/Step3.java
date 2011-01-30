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

import org.apache.click.examples.domain.Client;
import org.apache.click.examples.service.ClientService;

/**
 * This step asks for confirmation on the client information added through the
 * wizard. If user confirms, the new client is inserted into the database,
 * if user cancels the wizard is ended and user is redirected back to the first
 * step.
 * <p/>
 * Step3 uses a Velocity template (Step3.htm) to render client
 * information.
 */
public class Step3 extends Step {

    private static final long serialVersionUID = 1L;

    // Variables --------------------------------------------------------------

    /** The client domain object created through the wizard. */
    private Client client;

    // Constructors -----------------------------------------------------------

    /**
     * Construct Step3 with the specified name, label, description and page.
     *
     * @param name the step name
     * @param label the step label
     * @param description the step description
     * @param page the wizard
     */
    public Step3(String name, String label, String description, WizardPage page) {
        super(name, label, description, page);
        client = WizardUils.getClientFromSession();
    }

    // Public methods ---------------------------------------------------------

    /**
     * The onFinish action of Step3 checks if the form is valid, saves the
     * client and address in the database, sets up a success message, and
     * sets the page back to stateless.
     *
     * @return true if page processing should continue or not
     */
    @Override
    public boolean onFinish() {
        if (getForm().isValid()) {

            // Store client and associated address in the database
            ClientService service = new ClientService();
            service.saveClient(client);

            // Set a flash success message
            getContext().setFlashAttribute("message", "The client "
                + client.getName() + " was successfully created.");

            // Remove client from session
            WizardUils.removeClientFromSession();

            // Set Step index back to 0
            getWizardPage().setCurrentStepIndex(0);

            // Redirect to wizard page to start another process
            getWizardPage().setRedirect(WizardPage.class);
        }
        return true;
    }

    /**
     * The onPrevious action of Step3 moves to the previous step in the process
     * and clears and form errors.
     *
     * @return true if page processing should continue or not
     */
    @Override
    public boolean onPrevious() {
        getWizardPage().previous();
        getForm().clearValues();
        return false;
    }

    /**
     * Override onRender phase to add the client instance to the Template
     * model for rendering.
     */
    @Override
    public void onRender() {
        // Invoke default onInit implementation
        super.onRender();

        // Add client to model for displaying confirmation message
        addModel("client", client);
    }
}
