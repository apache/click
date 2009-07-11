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

import java.util.List;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Submit;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.extras.control.PickList;
import org.apache.click.util.Bindable;

/**
 * Provides an Select example Page.
 *
 * @author Naoki Takezoe
 */
public class PickListDemo extends BorderPage {

    @Bindable public Form form = new Form();
    @Bindable public List selectedValues;

    private PickList pickList = new PickList("languages");

    public PickListDemo() {
        pickList.setHeaderLabel("Languages", "Selected");

        pickList.add(new Option("002", "C/C++"));
        pickList.add(new Option("003", "C#"));
        pickList.add(new Option("004", "Fortran"));
        pickList.add(new Option("005", "Java"));
        pickList.add(new Option("006", "Ruby"));
        pickList.add(new Option("007", "Perl"));
        pickList.add(new Option("008", "Visual Basic"));

        pickList.addSelectedValue("004");

        form.add(pickList);

        form.add(new Submit("ok", " OK ", this, "onOkClick"));
        form.add(new Submit("canel", this, "onCancelClick"));
    }

    public boolean onOkClick() {
        selectedValues = pickList.getSelectedValues();
        return true;
    }

    public boolean onCancelClick() {
        pickList.getSelectedValues().clear();
        return true;
    }

}