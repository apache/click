package net.sf.click.examples.page.form;

import net.sf.click.examples.control.html.cssform.ContactDetailsForm;
import net.sf.click.examples.page.BorderPage;

/**
 * This page demonstrates how to manually layout a form using Java.
 * 
 * The form is laid out as described in the sitepoint
 * article: http://www.sitepoint.com/print/fancy-form-design-css
 * 
 * @author Bob Schellink
 */
public class ContactDetailsPage extends BorderPage {

    private ContactDetailsForm form;
    
    public void onInit() {
        super.onInit();
        form = new ContactDetailsForm("form");
        addControl(form);
    }

    public String getTemplate() {
        return "/form/another-border.htm";
    }
}
