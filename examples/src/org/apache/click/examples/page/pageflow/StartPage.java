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

import java.util.List;

import java.util.Map;
import javax.annotation.Resource;

import org.apache.click.Context;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.domain.CourseBooking;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.page.ajax.AjaxCustomer;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.util.ClickUtils;
import org.springframework.stereotype.Component;

/**
 * Provides the start page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
@Component
public class StartPage extends BorderPage {

    public Form form = new Form();
    private Select customerSelect;
    private DateField dateField;
    private Select courseSelect;
    private TextArea notesField;

    private CourseBooking courseBooking;

    @Resource(name="customerService")
    private CustomerService customerService;

    // ------------------------------------------------------------ Constructor

    public StartPage() {
        form.setLabelsPosition("top");

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
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
    @Override
    public void onInit() {
        super.onInit();

        List<Customer> customerList = customerService.getCustomers();
        customerSelect.add(Option.EMPTY_OPTION);
        for (Customer customer : customerList) {
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

    /**
     * Return the Page JavaScript resources.
     *
     * @see org.apache.click.Page#getHeadElements()
     */
    public List getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Include the prototype.js library which is made available under
            // the web folder "/click/prototype/"
            headElements.add(new JsImport("/click/prototype/prototype.js"));

            Context context = getContext();

            // Create a model to pass to the Page JavaScript template. The
            // template recognizes the following Velocity variables:
            // $context, $path, $selector and $target
            Map model = ClickUtils.createTemplateModel(this, context);

            // Set path to the AjaxCustomer Page path
            model.put("path", context.getPagePath(AjaxCustomer.class));

            // Add a CSS selector, in this case the customerSelect ID attribute
            model.put("selector", customerSelect.getId());

            // Add the ID of a target element in the Page template to replace
            // with new data, in this example the target is 'customerDetails'
            model.put("target", "customerDetails");

            // Include the ajax-select.js template
            headElements.add(new JsScript("/ajax/ajax-select.js", model));
        }

        return headElements;
    }

    // --------------------------------------------------------- Public Methods

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

}
