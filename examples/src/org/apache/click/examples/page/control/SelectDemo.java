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
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.CountrySelect;
import org.apache.click.extras.control.PageSubmit;

/**
 * Provides an Select example Page.
 */
public class SelectDemo extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private Select genderSelect;
    private Select investmentSelect;
    private Select locationSelect;
    private Select countrySelect;

    // Constructor ------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public SelectDemo() {
        addControl(form);
        form.setErrorsPosition(Form.POSITION_TOP);

        // Gender Select - populated through a DataProvider
        genderSelect = new Select("gender");
        genderSelect.setRequired(true);

        genderSelect.setDefaultOption(new Option("U", ""));
        genderSelect.setDataProvider(new DataProvider() {

            public List getData() {
                List optionList = new ArrayList(3);
                optionList.add(new Option("M", "Male"));
                optionList.add(new Option("F", "Female"));
                return optionList;
            }
        });

        form.add(genderSelect);

        // Investment Select - populated through Select.add methods
        List investmentOptions = new ArrayList();

        OptionGroup property = new OptionGroup("property");
        property.add(new Option("Commercial Property", "Commercial"));
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
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onPost()
     */
    @Override
    public void onPost() {
        if (form.isValid()) {
            addModel("gender", genderSelect.getValue());
            addModel("investment", investmentSelect.getSelectedValues());
            addModel("location", locationSelect.getValue());
            addModel("country", countrySelect.getValue());
        }
    }
}
