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

import java.util.Iterator;
import java.util.List;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.domain.CourseBooking;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.DateField;

/**
 * Provides the start page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class StartPage extends BorderPage {

    public String jsInclude = "ajax/ajax-select-include.htm";
    public String bodyOnload = "registerAjax();";
    public Form form = new Form();

    private Select customerSelect;
    private DateField dateField;
    private Select courseSelect;
    private TextArea notesField;

    private CourseBooking courseBooking;

    // ------------------------------------------------------------ Constructor

    public StartPage() {
        form.setLabelsPosition("top");

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
        customerSelect.setAttribute("onchange", "onCustomerChange(this);");
        form.add(customerSelect);

        dateField = new DateField("Booking Date");
        dateField.setRequired(true);
        form.add(dateField);

        courseSelect = new InvestmentSelect("Course");
        courseSelect.setRequired(true);
        form.add(courseSelect);

        notesField = new TextArea("Booking Notes");
        notesField.setCols(25);
        form.add(notesField);

        form.add(new Submit(" < Back ", this, "onBackClick"));
        form.add(new Submit(" Next > ", this, "onNextClick"));
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see org.apache.click.Page#onSecurityCheck()
     */
    public boolean onSecurityCheck() {
        return form.onSubmitCheck(this, "/pageflow/invalid-submit.html");
    }

    /**
     * @see org.apache.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        List customerList = getCustomerService().getCustomers();
        customerSelect.add(new Option(""));
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }

        if (getContext().isForward() && courseBooking != null) {
            customerSelect.setValueObject(courseBooking.getCustomerId());
            dateField.setDate(courseBooking.getBookingDate());
            courseSelect.setValue(courseBooking.getCourseType());
            notesField.setValue(courseBooking.getBookingNotes());
        }
    }

    public boolean onBackClick() {
        setRedirect(HomePage.class);
        return false;
    }

    public boolean onNextClick() {
        if (form.isValid()) {
            Integer customerId = new Integer(customerSelect.getValue());

            CourseBooking courseBooking = new CourseBooking();
            courseBooking.setCustomerId(customerId);
            courseBooking.setBookingDate(dateField.getDate());
            courseBooking.setCourseType(courseSelect.getValue());
            courseBooking.setBookingNotes(notesField.getValue());

            NextPage nextPage = (NextPage) getContext().createPage(NextPage.class);
            nextPage.setCourseBooking(courseBooking);

            setForward(nextPage);
            return false;
        }
        return true;
    }

    // --------------------------------------------------------- Public Methods

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

}
