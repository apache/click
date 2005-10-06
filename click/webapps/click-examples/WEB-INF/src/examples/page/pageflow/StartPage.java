package examples.page.pageflow;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.DateField;
import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import examples.control.InvestmentSelect;
import examples.domain.CourseBooking;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides the start page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class StartPage extends BorderedPage {

    private Form form;
    private Select customerSelect;
    private DateField dateField;
    private Select courseSelect;
    private TextArea notesField;

    private CourseBooking courseBooking;

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

    public void onInit() {
        addModel("head-include", "ajax/ajax-head.htm");
        addModel("body-onload", "registerAjax();");

        form = new Form("form");
        form.setLabelsPosition("top");
        addControl(form);

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
        customerSelect.setAttribute("onchange", "onCustomerChange(this);");
        List customerList = CustomerDAO.getCustomersSortedByName();
        customerSelect.add(new Option(""));
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }
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

        if (getContext().isForward() && courseBooking != null) {
            customerSelect.setValue(courseBooking.getCustomerId());
            dateField.setDate(courseBooking.getBookingDate());
            courseSelect.setValue(courseBooking.getCourseType());
            notesField.setValue(courseBooking.getBookingNotes());
        }
    }

    public boolean onBackClick() {
        setRedirect("/index.html");
        return false;
    }

    public boolean onNextClick() {
        if (form.isValid()) {
            Long customerId = new Long(customerSelect.getValue());

            CourseBooking courseBooking = new CourseBooking();
            courseBooking.setCustomerId(customerId);
            courseBooking.setBookingDate(dateField.getDate());
            courseBooking.setCourseType(courseSelect.getValue());
            courseBooking.setBookingNotes(notesField.getValue());

            NextPage nextPage =
                (NextPage) getContext().createPage("/pageflow/next-page.htm");
            nextPage.setCourseBooking(courseBooking);

            setForward(nextPage);
            return false;
        }
        return true;
    }

}
