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
package org.apache.click.examples.page.pageflow;

import javax.annotation.Resource;

import org.apache.click.examples.domain.CourseBooking;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.BookingService;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Provides the last page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
@Component
public class LastPage extends BorderPage {

    @Resource(name="bookingService")
    private BookingService bookingService;

    @Resource(name="customerService")
    private CustomerService customerService;

    @Override
    public void onInit() {
        super.onInit();

        String bookingId = getContext().getRequest().getParameter("bookingId");

        if (bookingId != null) {
            Integer id = new Integer(bookingId);
            CourseBooking courseBooking =
                bookingService.findCourseBookingByID(id);

            if (courseBooking != null) {
                addModel("courseBooking", courseBooking);

                Customer customer =
                    customerService.findCustomerByID(courseBooking.getCustomerId());
                addModel("customer", customer);
            }
        }
    }

}
