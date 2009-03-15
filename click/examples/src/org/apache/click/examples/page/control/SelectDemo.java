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
package org.apache.click.examples.page.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.OptionGroup;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.CountrySelect;
import org.apache.click.extras.control.PageSubmit;

/**
 * Provides an Select example Page.
 *
 * @author Malcolm Edgar
 */
public class SelectDemo extends BorderPage {

    public Form form = new Form();

    private Select genderSelect;
    private Select investmentSelect;
    private Select locationSelect;
    private Select countrySelect;

    public SelectDemo() {
        form.setErrorsPosition(Form.POSITION_TOP);

        // Gender Select
        genderSelect = new Select("gender");
        genderSelect.setRequired(true);
        genderSelect.add(new Option("U", ""));
        genderSelect.add(new Option("M", "Male"));
        genderSelect.add(new Option("F", "Female"));
        form.add(genderSelect);

        // Investment Select
        List investmentOptions = new ArrayList();

        OptionGroup property = new OptionGroup("property");
        property.add(new Option("Commerical Property", "Commercial"));
        property.add(new Option("Residential Property", "Residential"));
        investmentOptions.add(property);

        OptionGroup securities = new OptionGroup("securities");
        securities.add(new Option("Bonds"));
        securities.add(new Option("Options"));
        securities.add(new Option("Stocks"));
        investmentOptions.add(securities);

        investmentSelect = new Select("investment");
        investmentSelect.setOptionList(investmentOptions);
        investmentSelect.setMultiple(true);
        investmentSelect.setRequired(true);
        investmentSelect.setSize(7);
        form.add(investmentSelect);

        // Location Select
        locationSelect = new Select("location");
        locationSelect.add("QLD");
        locationSelect.add("NSW");
        locationSelect.add("NT");
        locationSelect.add("SA");
        locationSelect.add("TAS");
        locationSelect.add("VIC");
        locationSelect.add("WA");
        form.add(locationSelect);

        countrySelect = new CountrySelect("country", true);
        form.add(countrySelect);

        form.add(new Submit("ok", "  OK  "));
        form.add(new PageSubmit("canel", HomePage.class));
    }

    /**
     * @see org.apache.click.Page#onPost()
     */
    public void onPost() {
        if (form.isValid()) {
            addModel("gender", genderSelect.getValue());
            addModel("investment", investmentSelect.getSelectedValues());
            addModel("location", locationSelect.getValue());
            addModel("country", countrySelect.getValue());
        }
    }
}