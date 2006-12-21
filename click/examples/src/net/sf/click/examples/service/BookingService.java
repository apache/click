package net.sf.click.examples.service;

import java.util.Date;

import net.sf.click.examples.domain.CourseBooking;
import net.sf.click.extras.cayenne.CayenneTemplate;

/**
 * Provides a CourseBooking Service.
 *
 * @see CourseBooking
 *
 * @author Malcolm Edgar
 */
public class BookingService extends CayenneTemplate {

    public CourseBooking findCourseBookingByID(Integer id) {
        return (CourseBooking) getObjectForPK(CourseBooking.class, id);
    }

    public Integer insertCourseBooking(CourseBooking courseBooking) {
        courseBooking.setCreatedAt(new Date());
        getDataContext().registerNewObject(courseBooking);
        commitChanges();
        return courseBooking.getId();
    }

}
