package net.sf.click.examples.page.pageflow;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.examples.control.InvestmentSelect;
import net.sf.click.examples.domain.CourseBooking;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.DateField;

/**
 * Provides the start page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class StartPage extends BorderPage {

    private Form form;
    private Select customerSelect;
    private DateField dateField;
    private Select courseSelect;
    private TextArea notesField;

    private CourseBooking courseBooking;

    public StartPage() {
        addModel("head-include", "ajax/ajax-head.htm");
        addModel("body-onload", "registerAjax();");

        form = new Form("form");
        form.setLabelsPosition("top");
        addControl(form);

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
        customerSelect.setAttribute("onchange", "onCustomerChange(this);");
        form.add(customerSelect);

        dateField = new DateField("Booking Date");
        dateField.setRequired(true);
        form.add(dateField);

        courseSelect = new InvestmentSelect("Course");
        courseSelect.setRequired(true);
        form.add(courseSelect);

        notesField = new TextArea("Booking Notes");
        notesField.setCols(25);
        form.add(notesField);

        form.add(new Submit(" < Back ", this, "onBackClick"));
        form.add(new Submit(" Next > ", this, "onNextClick"));
    }

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        List customerList = getCustomerService().getCustomersSortedByName();
        customerSelect.add(new Option(""));
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }

        if (getContext().isForward() && courseBooking != null) {
            customerSelect.setValueObject(courseBooking.getCustomerId());
            dateField.setDate(courseBooking.getBookingDate());
            courseSelect.setValue(courseBooking.getCourseType());
            notesField.setValue(courseBooking.getBookingNotes());
        }
    }

    /**
     * @see net.sf.click.Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        return form.onSubmitCheck(this, "/pageflow/invalid-submit.html");
    }

    public boolean onBackClick() {
        setRedirect(HomePage.class);
        return false;
    }

    public boolean onNextClick() {
        if (form.isValid()) {
            Integer customerId = new Integer(customerSelect.getValue());

            CourseBooking courseBooking = new CourseBooking();
            courseBooking.setCustomerId(customerId);
            courseBooking.setBookingDate(dateField.getDate());
            courseBooking.setCourseType(courseSelect.getValue());
            courseBooking.setBookingNotes(notesField.getValue());

            NextPage nextPage = (NextPage) getContext().createPage(NextPage.class);
            nextPage.setCourseBooking(courseBooking);

            setForward(nextPage);
            return false;
        }
        return true;
    }

}
