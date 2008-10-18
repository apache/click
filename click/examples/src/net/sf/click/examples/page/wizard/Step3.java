package net.sf.click.examples.page.wizard;

/**
 * This step asks for confirmation on the client information added through the
 * wizard. If user confirms, the new client is inserted into the database,
 * if user cancels the wizard is ended and user is redirected back to the first
 * step.
 * <p/>
 * Step3 uses a Velocity template (Step3.htm) to render client
 * information.
 *
 * @author Bob Schellink
 */
public class Step3 extends Step {

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
    }

    /**
     * The onFinish action of Step3 checks if the form is valid, saves the
     * client and address in the database, sets up a success message, and
     * sets the page back to stateless.
     * 
     * @return true if page processing should continue or not
     */
    public boolean onFinish() {
        if (getForm().isValid()) {

            // Store client and associated address in the database
            getForm().saveChanges();

            // Set a flash success message
            getContext().setFlashAttribute("message", "The client "
                + getClient().getName() + " was successfully created.");

            // Set page state to stateless which removes the page from
            // the session
            getWizardPage().setStateful(false);
            
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
    public boolean onPrevious() {
        getWizardPage().previous();
        getForm().clearValues();
        return false;
    }
    
    /**
     * Override onRender phase to add the client instance to the Template
     * model for rendering.
     */
    public void onRender() {
        // Invoke default onInit implementation
        super.onRender();

        // Add client to model for displaying confirmation message
        if (!getModel().containsKey("client")) {
            addModel("client", getClient());
        }
    }
}
