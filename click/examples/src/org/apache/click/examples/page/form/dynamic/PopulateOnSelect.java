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
package org.apache.click.examples.page.form.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.click.control.FieldSet;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.element.Element;
import org.apache.click.element.JsScript;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.TabbedForm;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Demonstrate how to dynamically populate Select controls.
 */
public class PopulateOnSelect extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final String EASTERN_CAPE = "EC";
    private static final String FREE_STATE = "FS";
    private static final String GAUTENG_PROVINCE = "GP";
    private static final String WESTERN_CAPE = "WC";

    private TabbedForm form = new TabbedForm("form");

    private Select state = new Select("state", true);
    private Select city = new Select("city", true);
    private Select suburb = new Select("suburb", true);
    private Submit save = new Submit("save");

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        addControl(form);

        FieldSet fieldSet = new FieldSet("select");
        form.addTabSheet(fieldSet);

        // Set onchange attributes
        state.setAttribute("onchange","handleChange('form_city', form);");
        city.setAttribute("onchange","handleChange('form_suburb', form);");
        suburb.setAttribute("onchange", "printValues();");

        // set widths
        state.setWidth("200px");
        city.setWidth("200px");
        suburb.setWidth("200px");

        // add selects
        fieldSet.add(state);
        fieldSet.add(city);
        fieldSet.add(suburb);

        form.add(save);

        // build the Selects in the onInit phase
        buildSelects();
    }

    // Public Methods ---------------------------------------------------------

    public void buildSelects() {
        state.setDefaultOption(Option.EMPTY_OPTION);
        city.setDefaultOption(Option.EMPTY_OPTION);
        suburb.setDefaultOption(Option.EMPTY_OPTION);

        // Populate the States. Do this before binding requests
        populateStateData();

        // Bind the form field request values
        ClickUtils.bind(form);

        if (StringUtils.isEmpty(state.getValue())) {
            // No state selected, exit early
            return;
        }

        // If state is selected, proceed to populate city
        populateCityData(state.getValue());

        if (StringUtils.isEmpty(city.getValue())) {
            // No city selected, exit early
            return;
        }

        // If city is selected, proceed to populate suburbs
        populateSuburbData(city.getValue());

        // If save was not clicked, don't validate
        if(form.isFormSubmission() && !save.isClicked()) {
            form.setValidate(false);
        }
    }

    @Override
    public List<Element> getHeadElements() {
        if (headElements == null) {
            headElements = super.getHeadElements();

            Map<String, Object> templateModel = new HashMap<String, Object>();
            templateModel.put("stateId", state.getId());
            templateModel.put("cityId", city.getId());
            templateModel.put("suburbId", suburb.getId());

            // populate-on-select.js is a Velocity template which is rendered directly
            // from this Page
            JsScript script = new JsScript("/form/dynamic/populate-on-select.js", templateModel);
            headElements.add(script);
        }
        return headElements;
    }

    // Private Methods --------------------------------------------------------

    @SuppressWarnings("serial")
    private void populateStateData() {
        state.setDataProvider(new DataProvider() {

            public List getData() {
                List<Option> optionList = new ArrayList<Option>();
                optionList.add(new Option(EASTERN_CAPE, "Eastern Cape"));
                optionList.add(new Option(FREE_STATE, "Free State"));
                optionList.add(new Option(GAUTENG_PROVINCE, "Gauteng Province"));
                optionList.add(new Option(WESTERN_CAPE, "Western Cape"));
                return optionList;
            }
        });
    }

    private void populateCityData(final String stateCode) {
        city.setDataProvider(new DataProvider() {

            public List getData() {
                List<Option> optionList = new ArrayList<Option>();

                if (EASTERN_CAPE.equals(stateCode)) {
                    optionList.add(new Option("Port Elizabeth"));
                    optionList.add(new Option("East London"));

                } else if (FREE_STATE.equals(stateCode)) {
                    optionList.add(new Option("Bloemfontein"));
                    optionList.add(new Option("Welkom"));

                } else if (GAUTENG_PROVINCE.equals(stateCode)) {
                    optionList.add(new Option("Johannesburg"));
                    optionList.add(new Option("Pretoria"));

                } else if (WESTERN_CAPE.equals(stateCode)) {
                    optionList.add(new Option("Cape Town"));
                    optionList.add(new Option("George"));
                }
                return optionList;
            }
        });
    }

    private void populateSuburbData(final String cityCode) {
        suburb.setDataProvider(new DataProvider() {

            public List getData() {
                List<Option> optionList = new ArrayList<Option>();

                if (cityCode.equals("Port Elizabeth")) {
                    optionList.add(new Option("Humewood"));
                    optionList.add(new Option("Summerstrand"));

                } else if (cityCode.equals("East London")) {
                    optionList.add(new Option("Beacon Bay"));
                    optionList.add(new Option("Cinta East"));

                } else if (cityCode.equals("Bloemfontein")) {
                    optionList.add(new Option("Fichardpark"));
                    optionList.add(new Option("Wilgehof"));

                } else if (cityCode.equals("Welkom")) {
                    optionList.add(new Option("Dagbreek"));
                    optionList.add(new Option("Eerstemyn"));

                } else if (cityCode.equals("Johannesburg")) {
                    optionList.add(new Option("Rivonia"));
                    optionList.add(new Option("Sandton"));

                } else if (cityCode.equals("Pretoria")) {
                    optionList.add(new Option("Garsfontein"));
                    optionList.add(new Option("Sunnyside"));

                } else if (cityCode.equals("Cape Town")) {
                    optionList.add(new Option("Milnerton"));
                    optionList.add(new Option("Blaauwberg"));

                } else if (cityCode.equals("George")) {
                    optionList.add(new Option("Panorama"));
                    optionList.add(new Option("Fernridge"));
                }

                return optionList;
            }
        });
    }
}
