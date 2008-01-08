package net.sf.click.examples.page.pageflow;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import net.sf.click.examples.domain.CourseBooking;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides the next page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class NextPage extends BorderPage {

    private Form form = new Form("form");
    private HiddenField courseField;
    private CourseBooking courseBooking;

    // ------------------------------------------------------------ Constructor

    public NextPage() {
        courseField = new HiddenField("courseField", CourseBooking.class);
        form.add(courseField);

        form.add(new Submit(" < Back ", this, "onBackClick"));
        form.add(new Submit(" Confirm ", this, "onConfirmClick"));

        addControl(form);
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        if (getContext().isForward() && courseBooking != null) {
            courseField.setValueObject(courseBooking);

            Customer customer =
                getCustomerService().findCustomerByID(courseBooking.getCustomerId());

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

    public boolean onConfirmClick() {
        CourseBooking booking = (CourseBooking) courseField.getValueObject();
        Integer bookingId =
            getBookingService().insertCourseBooking(booking);

        String path = getContext().getPagePath(LastPage.class);
        setRedirect(path + "?bookingId=" + bookingId);

        return true;
    }

    // --------------------------------------------------------- Public Methods

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

}
