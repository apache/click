/*
 * Copyright 2005 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package examples.domain;

import java.util.HashMap;
import java.util.Map;


/**
 * Provides a mockup CourseBooking DAO for the examples.
 *
 * @see Customer
 *
 * @author Malcolm Edgar
 */
public class CourseBookingDAO {

    public static CourseBooking findCourseBookingByID(Long id) {
        return (CourseBooking) COURSE_BOOKING_BY_ID.get(id);
    }

    public static Long insertCourseBooking(CourseBooking courseBooking) {
        seqNumber++;

        courseBooking.setId(new Long(seqNumber));
        COURSE_BOOKING_BY_ID.put(courseBooking.getId(), courseBooking);

        return courseBooking.getId();
    }

    private static final Map COURSE_BOOKING_BY_ID = new HashMap();

    private static long seqNumber = 92731;
}
