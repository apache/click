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
package benchmark.click.pages;

import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import benchmark.dao.Customer;
import benchmark.dao.CustomerDao;

public class AddCustomer extends Page {

    private Form form;

    public void onInit() {
        form = new Form("form");
        form.add(new TextField("id"));
        form.add(new TextField("firstName"));
        form.add(new TextField("lastName"));
        Select stateSelect = new Select("state");
        populateStates(stateSelect);
        form.add(stateSelect);
        form.add(new TextField("birthDate"));
        form.add(new Submit("submit", this, "onSubmit"));
        addControl(form);
    }

    public boolean onSubmit() {
        if (form.isFormSubmission()) {
            Customer customer = new Customer();
            form.copyTo(customer);
            CustomerDao.getInstance().saveOrUpdate(customer);
        }
        return true;
    }
    
    /**
     * Populate the Select control from backend STATE info.
     * 
     * @param select
     */
    private void populateStates(Select select) {
        for (int i = 0; i < CustomerDao.STATES.length; i++) {
            String state = CustomerDao.STATES[i];
            Option option = new Option(Integer.toString(i), state);
            select.getOptionList().add(option);
        }
    }
}

