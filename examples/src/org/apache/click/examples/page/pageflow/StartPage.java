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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.click.Context;
import org.apache.click.ActionResult;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.element.Element;
import org.apache.click.element.JsImport;
import org.apache.click.element.JsScript;
import org.apache.click.examples.control.InvestmentSelect;
import org.apache.click.examples.control.ajax.CustomerPanel;
import org.apache.click.examples.domain.CourseBooking;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.extras.control.DateField;
import org.apache.click.util.ClickUtils;
import org.springframework.stereotype.Component;

/**
 * Provides the start page of a multi page work flow.
 */
@Component
public class StartPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private Select customerSelect;
    private DateField dateField;
    private Select courseSelect;
    private TextArea notesField;

    private CourseBooking courseBooking;

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public StartPage() {
        addControl(form);
        form.setLabelsPosition("top");

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
        form.add(customerSelect);

        customerSelect.setDefaultOption(Option.EMPTY_OPTION);
        customerSelect.setDataProvider(new DataProvider() {

            public List getData() {
                List<Option> optionList = new ArrayList<Option>();
                List<Customer> customerList = customerService.getCustomers();
                for (Customer customer : customerList) {
                    optionList.add(new Option(customer.getId(), customer.getName()));
                }
                return optionList;
            }
        });

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

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onSecurityCheck()
     */
    @Override
    public boolean onSecurityCheck() {
        return form.onSubmitCheck(this, "/pageflow/invalid-submit.html");
    }

    // A pageAction that handles Ajax requests for a particular customer
    public ActionResult onChangeCustomer() {
        ActionResult actionResult = new ActionResult();

        // Lookup customer based on request parameter 'customerId'
        String customerId = getContext().getRequest().getParameter("customerId");
        Customer customer = customerService.findCustomerByID(customerId);

        // CustomerPanel will render the customer as an HTML snippet
        CustomerPanel customerPanel = new CustomerPanel(this, customer);
        actionResult.setContent(customerPanel.toString());

        // Set content type and character encoding
        actionResult.setCharacterEncoding("UTF-8");
        actionResult.setContentType(ActionResult.HTML);

        return actionResult;
    }

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

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

            NextPage nextPage = getContext().createPage(NextPage.class);
            nextPage.setCourseBooking(courseBooking);

            setForward(nextPage);
            return false;
        }
        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Return the Page JavaScript resources.
     *
     * @see org.apache.click.Page#getHeadElements()
     */
    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            // Include the prototype.js library which is made available under
            // the web folder "/click/prototype/"
            headElements.add(new JsImport("/click/prototype/prototype.js"));

            Context context = getContext();

            // Create a model to pass to the Page JavaScript template. The
            // template recognizes the following Velocity variables:
            // $context, $path, $selector and $target
            Map<String, Object> jsModel = ClickUtils.createTemplateModel(this, context);

            // Add a CSS selector, in this case the customerSelect ID attribute
            jsModel.put("selector", customerSelect.getId());

            // Add the ID of a target element in the Page template to replace
            // with new data, in this example the target is 'customerDetails'
            jsModel.put("target", "customerDetails");

            // Include the ajax-select.js template
            headElements.add(new JsScript("/ajax/select/ajax-select.js", jsModel));
        }

        return headElements;
    }

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

}
