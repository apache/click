package net.sf.click.examples.page.pageflow;

import net.sf.click.examples.domain.CourseBooking;
import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.page.BorderPage;

/**
 * Provides the last page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class LastPage extends BorderPage {

    public void onInit() {
        String bookingId = getContext().getRequest().getParameter("bookingId");

        if (bookingId != null) {
            Integer id = new Integer(bookingId);
            CourseBooking courseBooking =
                getBookingService().findCourseBookingByID(id);

            if (courseBooking != null) {
                addModel("courseBooking", courseBooking);

                Customer customer =
                    getCustomerService().findCustomerByID(courseBooking.getCustomerId());
                addModel("customer", customer);
            }
        }
    }

}
