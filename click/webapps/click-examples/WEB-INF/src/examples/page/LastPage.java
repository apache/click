package examples.page;

import examples.domain.CourseBooking;
import examples.domain.CourseBookingDAO;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides the last page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class LastPage extends BorderedPage {

    public void onInit() {
        String bookingId = getContext().getRequest().getParameter("bookingId");

        if (bookingId != null) {
            CourseBooking courseBooking =
                CourseBookingDAO.findCourseBookingByID(new Long(bookingId));

            if (courseBooking != null) {
                addModel("courseBooking", courseBooking);

                Customer customer =
                    CustomerDAO.findCustomerByID(courseBooking.getCustomerId());
                addModel("customer", customer);
            }
        }
    }

}
