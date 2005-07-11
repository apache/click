package examples.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Provides a mockup persistent Course Booking business object for the examples.
 *
 * @author Malcolm Edgar
 */
public class CourseBooking implements Serializable  {

    Long id;
    Date bookingDate;
    String bookingNotes;
    Long customerId;
    String courseType;
    Date createdAt = new Date();

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the bookingDate.
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * @param bookingDate The bookingDate to set.
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * @return Returns the bookingNotes.
     */
    public String getBookingNotes() {
        return bookingNotes;
    }

    /**
     * @param bookingNotes The bookingNotes to set.
     */
    public void setBookingNotes(String bookingNotes) {
        this.bookingNotes = bookingNotes;
    }

    /**
     * @return Returns the courseType.
     */
    public String getCourseType() {
        return courseType;
    }

    /**
     * @param courseType The courseType to set.
     */
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    /**
     * @return Returns the customerId.
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The customerId to set.
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    /**
     * @return Returns the createdAt.
     */
    public Date getCreatedAt() {
        return createdAt;
    }
}
