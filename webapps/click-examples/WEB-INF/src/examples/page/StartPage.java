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
package examples.page;

import java.util.Iterator;
import java.util.List;

import net.sf.click.control.DateField;
import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import examples.control.InvestmentSelect;
import examples.domain.CourseBooking;
import examples.domain.Customer;
import examples.domain.CustomerDAO;

/**
 * Provides TODO: header
 *
 * @author Malcolm Edgar
 */
public class StartPage extends BorderedPage {

    Form form;
    Select customerSelect;
    DateField dateField;
    Select courseSelect;
    TextArea notesField;

    public void onInit() {
        addModel("head-include", "ajax-head.htm");
        addModel("body-onload", "registerAjaxStuff();");

        form = new Form("form", getContext());
        form.setLabelsPosition("top");
        addControl(form);

        customerSelect = new Select("Customer");
        customerSelect.setRequired(true);
        customerSelect.setAttribute("onchange", "onCustomerChange(this);");
        List customerList = CustomerDAO.getCustomersSortedByName();
        customerSelect.add(new Option(""));
        for (Iterator i = customerList.iterator(); i.hasNext();) {
            Customer customer = (Customer) i.next();
            customerSelect.add(new Option(customer.getId(), customer.getName()));
        }
        form.add(customerSelect);

        dateField = new DateField("Booking Date");
        dateField.setRequired(true);
        form.add(dateField);

        courseSelect = new InvestmentSelect("Course");
        courseSelect.setRequired(true);
        form.add(courseSelect);

        notesField = new TextArea("Booking Notes");
        form.add(notesField);

        form.add(new Submit("   Next &gt;   "));

        Submit cancel = new Submit(" Cancel ");
        cancel.setListener(this, "onCancelClick");
        form.add(cancel);
    }

    public boolean onCancelClick() {
        setRedirect("index.html");
        return false;
    }

    public void onPost() {
        if (form.isValid()) {
            Long customerId = new Long(customerSelect.getValue());

            CourseBooking courseBooking = new CourseBooking();
            courseBooking.setCustomerId(customerId);
            courseBooking.setBookingDate(dateField.getDate());
            courseBooking.setCourseType(courseSelect.getValue());
            courseBooking.setBookingNotes(notesField.getValue());

            NextPage nextPage = (NextPage) getContext().createPage("next-page.htm");
            nextPage.setCourseBooking(courseBooking);

            setForward(nextPage);
        }
    }

}
