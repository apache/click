package net.sf.click.examples.page.form;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;

/**
 *
 * @author Bob Schellink
 */
public class MultipleFormsDemo extends BorderPage {

    private Form form1 = new Form("form1");

    private Form form2 = new Form("form2");

    public boolean onSecurityCheck() {
        String pagePath = getContext().getPagePath(getClass());

        // call forms onSubmitCheck
        boolean form1SubmitCheckSucceed = form1.onSubmitCheck(this, pagePath);
        boolean form2SubmitCheckSucceed = form2.onSubmitCheck(this, pagePath);

        if (!form1SubmitCheckSucceed) {
            // if form1 failed the submit check, set error message and stop further processing
            getContext().setFlashAttribute("error", "You have made an invalid form submission for '" + form1.getName() + "'");
            return false;
        } else if (!form2SubmitCheckSucceed) {
            // if form2 failed the submit check, set error message and stop further processing
            getContext().setFlashAttribute("error", "You have made an invalid form submission for '" + form2.getName() + "'");
            return false;
        } else {
            // if both forms succeeded the check, continue processing the request
            return true;
        }
    }

    public void onInit() {
        super.onInit();

        // construct form1
        form1.add(new TextField("name"));
        form1.add(new Submit("submit", this, "onForm1Submit"));

        // construct form2
        form2.add(new TextField("name"));
        form2.add(new Submit("submit", this, "onForm2Submit"));

        // add form1 and form2 to the page controls.
        addControl(form1);
        addControl(form2);
    }

    public boolean onForm1Submit() {
        if (form1.isValid()) {

            // NOTE: to correctly implement the redirect-after-post pattern,
            // uncomment the lines below.
            //redirectAfterPost();
            //return false;
        }

        return true;
    }

    public boolean onForm2Submit() {
        if (form2.isValid()) {

            // NOTE: to correctly implement the redirect-after-post pattern,
            // uncomment the lines below.
            //redirectAfterPost();
            //return false;
        }

        return true;
    }

    public void redirectAfterPost() {
        // redirect back to this page after the post
        setRedirect(this.getClass());
    }
}
