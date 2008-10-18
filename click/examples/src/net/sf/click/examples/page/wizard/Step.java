package net.sf.click.examples.page.wizard;

import net.sf.click.examples.control.SimplePanel;
import net.sf.click.ActionListener;
import net.sf.click.Control;
import net.sf.click.control.Button;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.examples.control.cayenne.NestedCayenneForm;
import net.sf.click.examples.domain.Client;
import net.sf.click.extras.cayenne.CayenneForm;
import org.apache.commons.lang.ClassUtils;

/**
 * Provides common functionality for each step in the wizard.
 * 
 * @author Bob Schellink
 */
public abstract class Step extends SimplePanel {

    // -------------------------------------------------------------- Variables

    /** Reference to the form. */
    private CayenneForm form = new NestedCayenneForm("form"
        + ClassUtils.getShortClassName(getClass()), Client.class);

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

    // ------------------------------------------------------------ Constructor

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

    // ------------------------------------------------------ Public Properties

    /**
     * Return the Step form instance.
     * 
     * @return the Step form instance
     */
    public CayenneForm getForm() {
        return form;
    }

    /**
     * Set the form instance.
     * 
     * @param form the form for this Step
     */
    public void setForm(CayenneForm form) {
        this.form = form;
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
     * Return the Client instance.
     * 
     * @return the Client instance
     */
    public Client getClient() {
        // form.getDataObject ensures that latest field values are copied to
        // domain object
        return (Client) getForm().getDataObject();
    }

    /**
     * Set the Client instance.
     * 
     * @param client the client for this Step
     */
    public void setClient(Client client) {
        getForm().setDataObject(client);
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

    // --------------------------------------------------------- Public Methods

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
        getWizardPage().setStateful(false);
        getWizardPage().setRedirect(WizardPage.class);
        return false;
    }

    /**
     * Step up and initializes the Step. This method must be called before
     * the Step can be used.
     */
    public final void init() {
        getForm().setButtonAlign(Form.ALIGN_RIGHT);
        getForm().setErrorsPosition(Form.POSITION_MIDDLE);

        previous = new Submit("previous");
        previous.setLabel("< Previous");
        getPreviousButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onPrevious();
            }
        });
        form.add(previous);
        if (!getWizardPage().hasPreviousStep(this)) {
            previous.setDisabled(true);
        }

        next = new Submit("next");
        next.setLabel("Next >");
        getNextButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onNext();
            }
        });
        form.add(next);
        if (!getWizardPage().hasNextStep(this)) {
            next.setDisabled(true);
        }

        finish = new Submit("Finish");
        getFinishButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onFinish();
            }
        });
        form.add(finish);
        if (!getWizardPage().isLastStep(this)) {
            finish.setDisabled(true);
        }

        cancel = new Submit("Cancel");
        getCancelButton().setActionListener(new ActionListener() {
            public boolean onAction(Control source) {
                return onCancel();
            }
        });
        form.add(cancel);

        add(form);
    }
}
