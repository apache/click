/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
        super.onInit();

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
