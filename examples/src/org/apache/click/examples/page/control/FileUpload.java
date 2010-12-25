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

import org.apache.click.control.FieldSet;
import org.apache.click.control.FileField;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.PageSubmit;

/**
 * Provides File Upload example using the FileField control.
 */
public class FileUpload extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");

    private FileField fileField1;
    private TextField descField1;

    private FileField fileField2;
    private TextField descField2;

    // Constructor ------------------------------------------------------------

    public FileUpload() {
        addControl(form);

        form.setLabelsPosition("top");

        FieldSet fieldSet1 = new FieldSet("upload1", "<b>Upload File 1</b>");
        form.add(fieldSet1);

        fileField1 = new FileField("selectFile1", "Select File 1", 40);
        fieldSet1.add(fileField1);

        descField1 = new TextField("description1", "File Description 1", 30);
        fieldSet1.add(descField1);

        FieldSet fieldSet2 = new FieldSet("upload2", "<b>Upload File 2</b>");
        form.add(fieldSet2);

        fileField2 = new FileField("selectFile2", "Select File 2", 40);
        fieldSet2.add(fileField2);

        descField2 = new TextField("description2", "File Description 2", 30);
        fieldSet2.add(descField2);

        form.add(new Submit("ok", "  OK  ", this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    // Event Handlers ---------------------------------------------------------

    public boolean onOkClick() {

        if (form.isValid()) {
            if (fileField1.getFileItem() != null) {
                addModel("fileItem1", fileField1.getFileItem());
            }
            addModel("fileDesc1", descField1.getValue());

            if (fileField2.getFileItem() != null) {
                addModel("fileItem2", fileField2.getFileItem());
            }
            addModel("fileDesc2", descField2.getValue());
        }
        return true;
    }

}

