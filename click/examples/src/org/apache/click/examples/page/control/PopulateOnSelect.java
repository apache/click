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

import java.util.HashMap;
import java.util.Map;

import org.apache.click.control.FieldSet;
import org.apache.click.control.Select;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.TabbedForm;
import org.apache.commons.lang.StringUtils;

public class PopulateOnSelect extends BorderPage {

    private static final String EASTERN_CAPE = "EC";
    private static final String FREE_STATE = "FS";
    private static final String GAUTENG_PROVINCE = "GP";
    private static final String WESTERN_CAPE = "WC";

    public TabbedForm form = new TabbedForm("form");

    private Select state = new Select("state");
    private Select city = new Select("city");
    private Select suburb = new Select("suburb");

    public void onInit() {
        super.onInit();

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

        // build the Selects in the onInit phase
        buildSelects();
    }

    public void buildSelects() {
        state.add("---");
        city.add("---");
        suburb.add("---");

        // Populate the States. Do this before binding requests
        populateStateData();

        // Bind the state to its request value
        state.bindRequestValue();

        if (StringUtils.isEmpty(state.getValue())) {
            // No state selected, exit early
            return;
        }

        // If state is selected, proceed to populate city
        populateCityData(state.getValue());

        // Bind the city to its request value
        city.bindRequestValue();

        if (StringUtils.isEmpty(city.getValue())) {
            // No city selected, exit early
            return;
        }

        // If city is selected, proceed to populate suburbs
        populateSuburbData(city.getValue());
    }

    public String getHtmlImports() {
        Map model = new HashMap();
        model.put("stateId", state.getId());
        model.put("cityId", city.getId());
        model.put("suburbId", suburb.getId());

        // populate-on-select.js is a Velocity template which is rendered directly
        // from this Page
        String templatePath = "/control/populate-on-select.js";
        String template = getContext().renderTemplate(templatePath, model);

        // Click's JavaScript and CSS import parser does not yet handle multiline
        // imports so coerce it into a single line
        return "<script type=\"text/javascript\">" + template.replace('\n', ' ') + "</script>\n";
    }

    // -------------------------------------------------------- Private Methods

    private void populateStateData() {
        Map map = new HashMap() {{
                put(EASTERN_CAPE, "Eastern Cape");
                put(FREE_STATE, "Free State");
                put(GAUTENG_PROVINCE, "Gauteng Province");
                put(WESTERN_CAPE, "Western Cape");
            }};
        state.addAll(map);
    }

    private void populateCityData(String stateCode) {
        String[] cities = null;
        if (EASTERN_CAPE.equals(stateCode)) {
            cities = new String[] {"Port Elizabeth", "East London"};
        } else if (FREE_STATE.equals(stateCode)) {
            cities = new String[] {"Bloemfontein", "Welkom"};
        } else if (GAUTENG_PROVINCE.equals(stateCode)) {
            cities = new String[] {"Johannesburg", "Pretoria"};
        } else if (WESTERN_CAPE.equals(stateCode)) {
            cities = new String[] {"Cape Town", "George"};
        }
        if (cities != null) {
            city.addAll(cities);
        }
    }

    private void populateSuburbData(String cityCode) {
        String[] suburbs = null;
        if (cityCode.equals("Port Elizabeth")) {
            suburbs = new String[] {"Humewood", "Summerstrand"};
        } else if (cityCode.equals("East London")) {
            suburbs = new String[] {"Beacon Bay", "Cinta East"};
        } else if (cityCode.equals("Bloemfontein")) {
            suburbs = new String[] {"Fichardpark", "Wilgehof"};
        } else if (cityCode.equals("Welkom")) {
            suburbs = new String[] {"Dagbreek", "Eerstemyn"};
        } else if (cityCode.equals("Johannesburg")) {
            suburbs = new String[] {"Rivonia", "Sandton"};
        } else if (cityCode.equals("Pretoria")) {
            suburbs = new String[] {"Garsfontein", "Sunnyside"};
        } else if (cityCode.equals("Cape Town")) {
            suburbs = new String[] {"Milnerton", "Blaauwberg"};
        } else if (cityCode.equals("George")) {
            suburbs = new String[] {"Panorama", "Fernridge"};
        }
        if (suburbs != null) {
            suburb.addAll(suburbs);
        }
    }
}
