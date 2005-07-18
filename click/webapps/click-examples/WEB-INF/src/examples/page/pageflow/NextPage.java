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
package examples.page.pageflow;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Submit;
import examples.domain.CourseBooking;
import examples.domain.CourseBookingDAO;
import examples.domain.Customer;
import examples.domain.CustomerDAO;
import examples.page.BorderedPage;

/**
 * Provides the next page of a multi page work flow.
 *
 * @author Malcolm Edgar
 */
public class NextPage extends BorderedPage {

    private HiddenField courseField;
    private CourseBooking courseBooking;

    public void setCourseBooking(CourseBooking courseBooking) {
        this.courseBooking = courseBooking;
    }

    public void onInit() {
        Form form = new Form("form", getContext());
        addControl(form);

        courseField = new HiddenField("courseField", CourseBooking.class);
        form.add(courseField);

        Submit backButton = new Submit(" < Back ");
        backButton.setListener(this, "onBackClick");
        form.add(backButton);

        Submit nextButton = new Submit("  Next > ");
        nextButton.setListener(this, "onNextClick");
        form.add(nextButton);

        if (getContext().isForward() && courseBooking != null) {
            courseField.setValue(courseBooking);

            Customer customer =
                CustomerDAO.findCustomerByID(courseBooking.getCustomerId());

            addModel("customer", customer);
            addModel("courseBooking", courseBooking);
        }
    }

    public boolean onBackClick() {
        StartPage startPage = 
            (StartPage) getContext().createPage("pageflow/start-page.htm");
        
        courseBooking = (CourseBooking) courseField.getValueObject();
        startPage.setCourseBooking(courseBooking);

        setForward(startPage);
        return false;
    }

    public boolean onNextClick() {
        CourseBooking booking = (CourseBooking) courseField.getValueObject();
        Long bookingId = CourseBookingDAO.insertCourseBooking(booking);

        setRedirect("last-page.htm?bookingId=" + bookingId);
        return true;
    }

}
