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
package org.apache.click.examples.page.form;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.apache.click.control.Checkbox;
import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.examples.service.CustomerService;
import org.apache.click.examples.util.ExampleUtils;
import org.apache.click.extras.control.CheckList;
import org.apache.click.extras.control.ColorPicker;
import org.apache.click.extras.control.CountrySelect;
import org.apache.click.extras.control.CreditCardField;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.IntegerField;
import org.apache.click.extras.control.LongField;
import org.apache.click.extras.control.NumberField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.extras.control.RegexField;
import org.apache.click.extras.control.TelephoneField;
import org.apache.click.extras.control.VirtualKeyboard;
import org.apache.click.util.ContainerUtils;
import org.springframework.stereotype.Component;

/**
 * Provides a form containing all the Click Extras Controls.
 */
@Component
public class ExtraControlsForm extends BorderPage {

    private static final long serialVersionUID = 1L;

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        boolean allFieldsRequired = false;
        boolean javaScriptValidate = false;
    }

    private Form form = new Form("form");
    private Form optionsForm = new Form("optionsForm");

    private CheckList checkList = new CheckList("checkList");
    private Checkbox allFieldsRequired = new Checkbox("allFieldsRequired");
    private Checkbox jsValidate = new Checkbox("jsValidate", "JavaScript Validate");

    @Resource(name="customerService")
    private CustomerService customerService;

    // Constructor ------------------------------------------------------------

    public ExtraControlsForm() {
        addControl(form);
        addControl(optionsForm);

        form.setErrorsPosition(Form.POSITION_TOP);
        form.setColumns(2);

        checkList.setHeight("5em");
        form.add(checkList);
        form.add(new CreditCardField("creditCardField"));
        form.add(new ColorPicker("colorPicker"));
        form.add(new DateField("dateField"));
        form.add(new DoubleField("doubleField"));
        form.add(new EmailField("emailField"));
        form.add(new IntegerField("integerField"));
        form.add(new LongField("longField"));
        form.add(new NumberField("numberField"));
        form.add(new RegexField("regexField"));
        form.add(new TelephoneField("telephoneField"));
        form.add(new VirtualKeyboard("keyboardField"));
        form.add(new CountrySelect("countrySelect"));

        form.add(new Submit("save"));
        form.add(new PageSubmit("cancel", HomePage.class));

        // Settings Form
        FieldSet fieldSet = new FieldSet("options", "Form Options");
        allFieldsRequired.setAttribute("onclick", "form.submit();");
        fieldSet.add(allFieldsRequired);
        jsValidate.setAttribute("onclick", "form.submit();");
        fieldSet.add(jsValidate);
        optionsForm.add(fieldSet);
        optionsForm.setListener(this, "onOptionsSubmit");
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * @see org.apache.click.Page#onInit()
     */
    @Override
    public void onInit() {
        super.onInit();

        List<Customer> customers = customerService.getCustomers();
        checkList.addAll(customers, "id", "name");
        applyOptions();
    }

    public boolean onOptionsSubmit() {
        Options options = new Options();
        options.allFieldsRequired = allFieldsRequired.isChecked();
        options.javaScriptValidate = jsValidate.isChecked();
        ExampleUtils.setSessionObject(options);
        applyOptions();
        return true;
    }

    // Private Methods --------------------------------------------------------

    private void applyOptions() {
        Options options = (Options) ExampleUtils.getSessionObject(Options.class);

        form.setJavaScriptValidation(options.javaScriptValidate);
        for (Field field :  ContainerUtils.getInputFields(form)) {
            field.setRequired(options.allFieldsRequired);
        }

        allFieldsRequired.setChecked(options.allFieldsRequired);
        jsValidate.setChecked(options.javaScriptValidate);
    }

}
