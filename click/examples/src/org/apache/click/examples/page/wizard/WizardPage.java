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

import java.util.List;
import org.apache.click.element.CssImport;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.util.HtmlStringBuffer;

/**
 * This Page manages steps in a wizard process.
 */
public class WizardPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    /** Current step in the process. */
    private Step currentStep;

    public static final String STEP1_DESC = "Client";
    public static final String STEP2_DESC = "Address";
    public static final String STEP3_DESC = "Confirmation";

    public static final String STEP1_LABEL = "Step 1";
    public static final String STEP2_LABEL = "Step 2";
    public static final String STEP3_LABEL = "Step 3";

    public String[] stepDescriptions = {STEP1_DESC, STEP2_DESC, STEP3_DESC};
    public String[] stepLabels = {STEP1_LABEL, STEP2_LABEL, STEP3_LABEL};

    private int numberOfSteps = stepDescriptions.length;

    private int currentStepIndex = 0;

    // Constructor ------------------------------------------------------------

    /**
     * Default constructor.
     */
    public WizardPage() {
        // Lookup step ID; defaults to "0"
        int stepId = WizardUils.restoreActiveStepIndex();
        setCurrentStepIndex(stepId);

        //setCurrentStep(steps.get(stepIndex));

        // Initialize all the steps
        /*
        Iterator it = steps.iterator();
        while(it.hasNext()) {
            Step step = (Step) it.next();
            step.init();
        }
         *
         */
    }

    // Public Methods ---------------------------------------------------------

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
     * @param stepIndex the current step index to set
     */
    public void setCurrentStepIndex(int stepIndex) {
        // Store step index for subsequent requests
        WizardUils.saveActiveStepIndex(stepIndex);
        currentStepIndex = stepIndex;

        if (currentStep != null) {
            removeControl(currentStep);
        }
        getModel().remove("heading");

        if (stepIndex == 0) {
            currentStep = new Step1("step", STEP1_LABEL, STEP1_DESC, this);

        } else if (stepIndex == 1) {
            currentStep = new Step2("step", STEP2_LABEL, STEP2_DESC, this);

        } else if (stepIndex == 2) {
            currentStep = new Step3("step", STEP3_LABEL, STEP3_DESC, this);
        }
        currentStep.init();

        // Add the new step to the page list of controls
        addControl(currentStep);
        addModel("heading", getHeading());
    }

    /**
     * Return true if there is another step before the specified step.
     *
     * @return true if there is another step before the specified step
     */
    public boolean hasPreviousStep() {
        return currentStepIndex > 0;
    }

    /**
     * Return true if there is another step after the specified step.
     *
     * @return true if there is another step after the specified step
     */
    public boolean hasNextStep() {
        return !isLastStep();
    }

    /**
     * Return true if the specified step is the last step in the process.
     *
     * @return true if the specified step is the last step in the process
     */
    public boolean isLastStep() {
        // currentStepIndex is a zero based index. Add 1 when comparing to
        // numberOfSteps
        return (numberOfSteps == currentStepIndex + 1);
    }

    /**
     * Goto previous step.
     */
    public void previous() {
        if (currentStepIndex > 0) {
            setCurrentStepIndex(--currentStepIndex);
        }
    }

    /**
     * Goto next step.
     */
    public void next() {
        if (currentStepIndex < numberOfSteps - 1) {
            setCurrentStepIndex(++currentStepIndex);
        }
    }

    /**
     * Return the page stylesheet: wizard.css.
     *
     * @return the page stylesheet
     */
    @Override
    public List getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();
            headElements.add(new CssImport("/wizard/wizard.css"));
        }
        return headElements;
    }

    // Private Methods --------------------------------------------------------

    /**
     * Return an HTML representation of the wizard steps as an Html List <ul>.
     * The current step is assigned a special CSS class so it can be highlighted
     * through CSS.
     */
    private String getHeading() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();
        buffer.append("<ul id=\"steps\">");
        for (int i = 0; i < stepDescriptions.length; i++) {
            String stepDescription = stepDescriptions[i];
            String stepLabel = stepLabels[i];

            buffer.elementStart("li");
            if (stepDescription.equals(currentStep.getDescription())) {
                buffer.appendAttribute("class", "current");
            }
            buffer.closeTag();
            buffer.append(stepDescription);
            buffer.elementStart("span");
            buffer.closeTag();
            buffer.append(stepLabel);
            buffer.elementEnd("span");
            buffer.elementEnd("li");
        }
        buffer.append("</ul>");
        return buffer.toString();
    }
}
