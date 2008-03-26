package net.sf.click.examples.page.jsp;

import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.DateField;
import net.sf.click.extras.control.DoubleField;
import net.sf.click.extras.control.EmailField;

/**
 * Demo a form submit using JSP as template.
 *
 * @author Bob Schellink
 */
public class EditCustomerPage extends BorderPage {

    public Form form = new Form("form");

    public EditCustomerPage() {
        // Setup customers form
        FieldSet fieldSet = new FieldSet("customer");
        fieldSet.add(new TextField("name", true));
        fieldSet.add(new EmailField("email"));
        fieldSet.add(new DoubleField("holdings", true));
        fieldSet.add(new DateField("dateJoined"));
        form.add(fieldSet);
        form.add(new Submit("save", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
    }

    public boolean onSecurityCheck() {
        return form.onSubmitCheck(this, EditCustomerPage.class);
    }

    /**
     * Returns the name of the border template: &nbsp; <tt>"/border-template.jsp"</tt>
     *
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return "/border-template.jsp";
    }

    public boolean onSaveClick() {
        if (form.isValid()) {
            // Perform logic
            // Optionally forward to another Page for display:
            // setForward(ViewCustomersPage.class);
        }
        return true;
    }

    public boolean onCancelClick() {
        form.clearErrors();
        form.clearValues();
        return true;
    }
}
