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
import java.util.Iterator;
import java.util.List;
import org.apache.click.examples.page.BorderPage;

/**
 * This Page manages steps in a wizard process.
 *
 * @author Bob Schellink
 */
public class WizardPage extends BorderPage {

    /** Current step in the process. */
    private Step currentStep;

    /** List of all steps. */
    private List steps = new ArrayList();

    /**
     * Default constructor.
     */
    public WizardPage() {
        steps.add(new Step1("step", "Client", "Step 1 of 3", this));
        steps.add(new Step2("step", "Address", "Step 2 of 3", this));
        steps.add(new Step3("step", "Confirmation", "Step 3 of 3", this));

        // Set first step as current
        setCurrentStep((Step) steps.get(0));

        // Initialize all the steps
        Iterator it = steps.iterator();
        while(it.hasNext()) {
            Step step = (Step) it.next();
            step.init();
        }
    }

    /**
     * Return the current step.
     *
     * @return the current step
     */
    public Step getCurrentStep() {
        return currentStep;
    }

    /**
     * Sets the current Step to the specified Step.
     *
     * @param step the new step to set
     */
    public void setCurrentStep(Step step) {
        if (this.currentStep != null) {
            // Remove the current step from the page list of controls
            removeControl(this.currentStep);
        }
        this.currentStep = step;
        // Add the new step to the page list of controls
        addControl(step);
    }

    /**
     * Return true if there is another step before the specified step.
     *
     * @param step the step to check against
     * @return true if there is another step before the specified step
     */
    public boolean hasPreviousStep(Step step) {
        return steps.indexOf(step) > 0;
    }

    /**
     * Return true if there is another step after the specified step.
     *
     * @param step the step to check against
     * @return true if there is another step after the specified step
     */
    public boolean hasNextStep(Step step) {
        return !isLastStep(step);
    }

    /**
     * Return true if the specified step is the last step in the process.
     *
     * @param step the step to check against
     * @return true if the specified step is the last step in the process
     */
    public boolean isLastStep(Step step) {
        int numberOfSteps = steps.size();
        int currentStepIndex = steps.indexOf(step);

        // currentStepIndex is a zero based index. Add 1 when comparing to
        // numberOfSteps
        return (numberOfSteps == currentStepIndex + 1);
    }

    /**
     * Goto previous step.
     */
    public void previous() {
        int currentIndex = steps.indexOf(getCurrentStep());
        if (currentIndex > 0) {
            setCurrentStep((Step) steps.get(currentIndex - 1));
        }
    }

    /**
     * Goto next step.
     */
    public void next() {
        int currentIndex = steps.indexOf(getCurrentStep());
        if (currentIndex < steps.size() - 1) {
            setCurrentStep((Step) steps.get(currentIndex + 1));
        }
    }

    /**
     * Return the page stylesheet: wizard.css.
     *
     * @return the page stylesheet
     */
    @Override
    public String getHtmlImports() {
        String contextPath = getContext().getRequest().getContextPath();
        return "<link type=\"text/css\" rel=\"stylesheet\" href=\"" + contextPath + "/wizard/wizard.css\"/>";
    }

}
