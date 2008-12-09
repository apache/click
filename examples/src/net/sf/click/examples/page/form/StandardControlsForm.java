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
package net.sf.click.examples.page.form;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Button;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.ImageSubmit;
import net.sf.click.control.Label;
import net.sf.click.control.Option;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Radio;
import net.sf.click.control.RadioGroup;
import net.sf.click.control.Reset;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;
import net.sf.click.control.TextArea;
import net.sf.click.control.TextField;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.util.ClickUtils;

/**
 * Provides a form containing all the Standard Click Controls.
 *
 * @author Malcolm Edgar
 */
public class StandardControlsForm extends BorderPage {

    /** Form options holder. */
    public static class Options implements Serializable {
        static final long serialVersionUID = 1L;
        boolean allFieldsRequired = false;
        boolean javaScriptValidate = false;
    }

    public Form form = new Form();
    public Form optionsForm = new Form();

    private Select select = new Select("select");
    private Checkbox allFieldsRequired = new Checkbox("allFieldsRequired");
    private Checkbox jsValidate = new Checkbox("jsValidate", "JavaScript Validate");

    // ------------------------------------------------------------ Constructor

    public StandardControlsForm() {
        form.setErrorsPosition(Form.POSITION_TOP);

        // Controls FieldSet
        FieldSet fieldSet = new FieldSet("fieldSet");
        form.add(fieldSet);

        fieldSet.add(new Checkbox("checkbox"));
        fieldSet.add(new FileField("fileField"));
        fieldSet.add(new HiddenField("hiddenField", String.class));
        String labelText = "<span style='color:blue;font-style:italic'>Label - note how label text spans both columns</span>";
        fieldSet.add(new Label("label", labelText));
        fieldSet.add(new PasswordField("passwordField"));
        fieldSet.add(new Radio("radio", "Radio", "radio"));
        RadioGroup radioGroup = new RadioGroup("radioGroup");
        radioGroup.add(new Radio("A"));
        radioGroup.add(new Radio("B"));
        radioGroup.add(new Radio("C"));
        fieldSet.add(radioGroup);
        fieldSet.add(select);
        fieldSet.add(new TextArea("textArea"));
        fieldSet.add(new TextField("textField"));

        Button button = new Button("button");
        button.setAttribute("onclick", "alert('Button clicked');");
        form.add(button);
        ImageSubmit imageSubmit = new ImageSubmit("image", "/assets/images/edit-button.gif");
        imageSubmit.setTitle("ImageSubmit");
        form.add(imageSubmit);
        form.add(new Reset("reset"));
        form.add(new Submit("submit"));

        // Settings Form
        fieldSet = new FieldSet("options", "Form Options");
        allFieldsRequired.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(allFieldsRequired);
        jsValidate.setAttribute("onchange", "optionsForm.submit();");
        fieldSet.add(jsValidate);
        optionsForm.add(fieldSet);
        optionsForm.setListener(this, "onOptionsSubmit");
    }

    // --------------------------------------------------------- Event Handlers

    /**
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        List customers = getCustomerService().getCustomers();
        select.add(new Option("[Select]"));
        select.addAll(customers, "id", "name");
        applyOptions();
    }

    public boolean onOptionsSubmit() {
        Options options = new Options();
        options.allFieldsRequired = allFieldsRequired.isChecked();
        options.javaScriptValidate = jsValidate.isChecked();
        setSessionObject(options);
        applyOptions();
        return true;
    }

    // -------------------------------------------------------- Private Methods

    private void applyOptions() {
        Options options = (Options) getSessionObject(Options.class);

        form.setJavaScriptValidation(options.javaScriptValidate);
        List formFiels = ClickUtils.getFormFields(form);
        for (Iterator i = formFiels.iterator(); i.hasNext();) {
            Field field = (Field) i.next();
            field.setRequired(options.allFieldsRequired);
        }

        allFieldsRequired.setChecked(options.allFieldsRequired);
        jsValidate.setChecked(options.javaScriptValidate);
    }

}
