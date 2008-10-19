package net.sf.click.examples.page.wizard;

import net.sf.click.control.TextField;
import net.sf.click.extras.cayenne.QuerySelect;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.EmailField;

/**
 * The first step in the 3 step process is to capture the Client details.
 * <p/>
 * Note this Panel has no associated template.
 *
 * @author Bob Schellink
 */
public class Step1 extends Step {

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
