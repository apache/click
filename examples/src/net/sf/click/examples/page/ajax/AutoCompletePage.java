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
package net.sf.click.examples.page.ajax;

import java.util.List;

import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.extras.control.AutoCompleteTextField;

/**
 * Provides AJAX AutoCompleteTextField example page.
 *
 * @author Malcolm Edgar
 */
public class AutoCompletePage extends BorderPage {

    public Form form = new Form();

    // ------------------------------------------------------------ Constructor

    public AutoCompletePage() {
        FieldSet fieldSet = new FieldSet("Enter a Suburb Location");
        fieldSet.setStyle("background-color", "");
        form.add(fieldSet);

        AutoCompleteTextField locationField = new AutoCompleteTextField("location") {
            public List getAutoCompleteList(String criteria) {
                return getPostCodeService().getPostCodeLocations(criteria);
            }
        };
        locationField.setSize(40);

        fieldSet.add(locationField);
    }

}
