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

import org.apache.click.control.Field;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Label;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.EmailField;
import org.apache.click.extras.control.PageSubmit;
import org.apache.click.extras.control.TelephoneField;

/**
 * Provides Form layout examples using the Form and FieldSet controls.
 */
public class FormLayout extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form1 = new Form("form1");
    private Form form2 = new Form("form2");

    public FormLayout() {
        addControl(form1);
        addControl(form2);

        // ------
        // Form 1
        form1.setColumns(3);

        // Row 1
        Field titleField = new TextField("title");
        titleField.setStyle("width", "100%");
        form1.add(titleField, 2);
        form1.add(new Label("blank", ""));

        // Row 2
        form1.add(new TextArea("description", 70, 3), 3);

        // Row 3
        form1.add(new TextField("name"));
        form1.add(new TextField("type"));
        form1.add(new TelephoneField("telephone"));

        form1.add(new Submit("ok", " OK "));
        form1.add(new PageSubmit("cancel", HomePage.class));

        //-------
        // Form 2
        form2.setColumns(2);

        FieldSet fieldSet = new FieldSet("fieldSet", "FieldSet");
        form2.add(fieldSet);

        // Row 1
        fieldSet.add(new TextField("name"));
        fieldSet.add(new TextField("type"));

        // Row 2
        fieldSet.add(new TextArea("description", 39, 3), 2);

        // Row 3
        fieldSet.add(new EmailField("email"), 2);

        // Row 4
        fieldSet.add(new TelephoneField("telephone"));

        fieldSet.add(new Submit("ok", " OK "));
        fieldSet.add(new PageSubmit("cancel", HomePage.class));
    }

}
