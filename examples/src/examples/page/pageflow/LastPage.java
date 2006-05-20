package examples.page.pageflow;

import examples.domain.CourseBooking;
import examples.domain.Customer;
import examples.page.BorderPage;

/**
 * Provides the last page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class LastPage extends BorderPage {

    public void onInit() {
        String bookingId = getContext().getRequest().getParameter("bookingId");

        if (bookingId != null) {
            CourseBooking courseBooking =
                getCourseBookingService().findCourseBookingByID(new Long(bookingId));

            if (courseBooking != null) {
                addModel("courseBooking", courseBooking);

                Customer customer =
                    getCustomerService().findCustomerByID(courseBooking.getCustomerId());
                addModel("customer", customer);
            }
        }
    }

}
