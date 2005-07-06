package examples.page;

import examples.domain.CourseBooking;
import examples.domain.CourseBookingDAO;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides TODO: header
 *
 * @author Malcolm Edgar
 */
public class ConfirmPage extends BorderedPage {

    public void onInit() {
        String bookingId = getContext().getRequest().getParameter("bookingId");

        if (bookingId != null) {
            CourseBooking courseBooking =
                CourseBookingDAO.findCourseBookingByID(new Long(bookingId));
            addModel("courseBooking", courseBooking);

            Customer customer =
                CustomerDAO.findCustomerByID(courseBooking.getCustomerId());
            addModel("customer", customer);
        }
    }

}
