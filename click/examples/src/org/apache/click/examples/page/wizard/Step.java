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

import org.apache.click.examples.control.SimplePanel;
import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.Button;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;

/**
 * Provides common functionality for each step in the wizard.
 */
public abstract class Step extends SimplePanel {

    private static final long serialVersionUID = 1L;

    // Variables --------------------------------------------------------------

    /** Reference to the form. */
    private Form form = new Form("form");

    /** Reference to the wizard page. */
    private WizardPage wizardPage;

    /** Reference to the step description. */
    private String description;

    /** Reference to the next button. */
    private Button next;

    /** Reference to the previous button. */
    private Button previous;

    /** Reference to the finish button. */
    private Button finish;

    /** Reference to the cancel button. */
    private Button cancel;

    // Constructor ------------------------------------------------------------

    /**
     * Constructs a Step for the specified name and page.
     *
     * @param name the step name
     * @param label the step label
     * @param description the step description
     * @param page the wizard page
     */
    public Step(String name, String label, String description, WizardPage page) {
        super(name);
        setWizardPage(page);
        setLabel(label);
        setDescription(description);
    }

    // Public Properties ------------------------------------------------------

    /**
     * Return the Step form instance.
     *
     * @return the Step form instance
     */
    public Form getForm() {
        return form;
    }


    /**
     * Return the Step WizardPage instance.
     *
     * @return the WizardPage instance
     */
    public WizardPage getWizardPage() {
        return wizardPage;
    }

    /**
     * Set the WizardPage instance.
     *
     * @param wizardPage the WizardPage instance for this Step
     */
    public void setWizardPage(WizardPage wizardPage) {
        this.wizardPage = wizardPage;
    }

    /**
     * Return the Step description.
     *
     * @return the Step description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the Step visual description.
     *
     * @param description the visual description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the button representing the next action.
     *
     * @return the button representing the next action
     */
    public Button getNextButton() {
        return next;
    }

    /**
     * Return the button representing the previous action.
     *
     * @return the button representing the previous action
     */
    public Button getPreviousButton() {
        return previous;
    }

    /**
     * Return the button representing the next action.
     *
     * @return the button representing the next action
     */
    public Button getFinishButton() {
        return finish;
    }

    /**
     * Return the button representing the cancel action.
     *
     * @return the button representing the cancel action
     */
    public Button getCancelButton() {
        return cancel;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * The onNext action. Subclasses can override this method to implement
     * custom logic.
     *
     * @return true if Page processing should continue, false otherwise
     */
    public boolean onNext() {
        return true;
    }

    /**
     * The onPrevious action. Subclasses can override this method to implement
     * custom logic.
     *
     * @return true if Page processing should continue, false otherwise
     */
    public boolean onPrevious() {
        return true;
    }

    /**
     * The onFinish action. Subclasses can override this method to implement
     * custom logic.
     *
     * @return true if Page processing should continue, false otherwise
     */
    public boolean onFinish() {
        return true;
    }

    /**
     * The onCancel action. Subclasses can override this method to implement
     * custom logic.
     *
     * @return true if Page processing should continue, false otherwise
     */
    public boolean onCancel() {
        // Set a flash cancel message
        getContext().setFlashAttribute("message", "You have cancelled the "
            + "client creation process.");

        // Set page state to stateless which removes the page from
        // the session
        getWizardPage().setRedirect(WizardPage.class);
        return false;
    }

    /**
     * Setup and initializes the Step. This method must be called before
     * the Step can be used.
     */
    @SuppressWarnings("serial")
    public final void init() {
        Form localForm = getForm();
        localForm.setButtonAlign(Form.ALIGN_RIGHT);
        localForm.setErrorsPosition(Form.POSITION_MIDDLE);

        previous = new Submit("previous");
        previous.setLabel("< Previous");
        getPreviousButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onPrevious();
            }
        });
        localForm.add(previous);
        if (!getWizardPage().hasPreviousStep()) {
            previous.setDisabled(true);
        }

        next = new Submit("next");
        next.setLabel("Next >");
        getNextButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onNext();
            }
        });
        localForm.add(next);
        if (!getWizardPage().hasNextStep()) {
            next.setDisabled(true);
        }

        finish = new Submit("Finish");
        getFinishButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onFinish();
            }
        });
        localForm.add(finish);
        if (!getWizardPage().isLastStep()) {
            finish.setDisabled(true);
        }

        cancel = new Submit("Cancel");
        getCancelButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onCancel();
            }
        });
        localForm.add(cancel);

        add(localForm);
    }
}
