package examples.page.pageflow;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import examples.domain.CourseBooking;
import examples.domain.CourseBookingDAO;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides the next page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class NextPage extends BorderedPage {

    private HiddenField courseField;

    private CourseBooking courseBooking;

    public NextPage() {
        Form form = new Form("form");
        addControl(form);

        courseField = new HiddenField("courseField", CourseBooking.class);
        form.add(courseField);

        form.add(new Submit(" < Back ", this, "onBackClick"));
        form.add(new Submit("  Next > ", this, "onNextClick"));
    }

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        if (getContext().isForward() && courseBooking != null) {
            courseField.setValueObject(courseBooking);

            Customer customer =
                CustomerDAO.findCustomerByID(courseBooking.getCustomerId());

            addModel("customer", customer);
            addModel("courseBooking", courseBooking);
        }
    }

    public boolean onBackClick() {
        StartPage startPage =
            (StartPage) getContext().createPage(StartPage.class);

        courseBooking = (CourseBooking) courseField.getValueObject();
        startPage.setCourseBooking(courseBooking);

        setForward(startPage);
        return false;
    }

    public boolean onNextClick() {
        CourseBooking booking = (CourseBooking) courseField.getValueObject();
        Long bookingId = CourseBookingDAO.insertCourseBooking(booking);

        setRedirect("/pageflow/last-page.htm?bookingId=" + bookingId);
        return true;
    }

}
