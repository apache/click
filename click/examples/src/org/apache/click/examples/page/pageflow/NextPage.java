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

import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.Submit;
import org.apache.click.examples.domain.CourseBooking;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.service.BookingService;
import org.apache.click.examples.service.CustomerService;
import org.springframework.stereotype.Component;

/**
 * Provides the next page of a multi page work flow.
 */
@Component
public class NextPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");
    private HiddenField courseField;
    private CourseBooking courseBooking;

    @Resource(name="bookingService")
    private BookingService bookingService;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public NextPage() {
        courseField = new HiddenField("courseField", CourseBooking.class);
        form.add(courseField);

        form.add(new Submit(" < Back ", this, "onBackClick"));
        form.add(new Submit(" Confirm ", this, "onConfirmClick"));

        addControl(form);
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        if (getContext().isForward() && courseBooking != null) {
            courseField.setValueObject(courseBooking);

            Customer customer =
                customerService.findCustomerByID(courseBooking.getCustomerId());

            addModel("customer", customer);
            addModel("courseBooking", courseBooking);
        }
    }

    public boolean onBackClick() {
        StartPage startPage = getContext().createPage(StartPage.class);

        courseBooking = (CourseBooking) courseField.getValueObject();
        startPage.setCourseBooking(courseBooking);

        setForward(startPage);
        return false;
    }

    public boolean onConfirmClick() {
        CourseBooking booking = (CourseBooking) courseField.getValueObject();
        Integer bookingId =
            bookingService.insertCourseBooking(booking);

        String path = getContext().getPagePath(LastPage.class);
        setRedirect(path + "?bookingId=" + bookingId);

        return true;
    }

    // Public Methods ---------------------------------------------------------

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

}
