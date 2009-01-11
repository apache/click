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
package click.cayenne.page;

import click.cayenne.entity.Department;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.click.extras.cayenne.CayenneForm;

/**
 * Provides a Department Editor page which can be used to create or edit
 * Department data objects. This page uses the data aware CayenneForm control 
 * for simplified editing data objects.
 *
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class DepartmentEditor extends BorderedPage {

    /** The Department editing CayenneForm. */
    protected CayenneForm form = new CayenneForm("form", Department.class);

    /**
     * Create a new Department Editor.
     */
    public DepartmentEditor() {
        form.setButtonAlign("right");
        addControl(form);

        form.add(new TextField("name", "Department Name", 35));
        form.add(new TextArea("description", 35, 6));

        form.add(new Submit("ok", "   OK   ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
    }
    
    /**
     * Set the department object to edit.
     * 
     * @param department the department to edit
     */
    public void setDepartment(Department department) {
        form.setDataObject(department);
    }

    /**
     * Handle the OK button click, saving the Department if valid and 
     * redirecting to the <tt>DepartmentsViewer</tt> page. If the Department
     * is not valid display form errors.
     */
    public boolean onOkClick() {
        if (form.isValid()) {
            if (form.saveChanges()) {
                setRedirect("departments-viewer.htm");
                return false;
            }
        }
        return true;
    }

    /**
     * Handle the Cancel button click, redirecting to the 
     * <tt>DepartmentsViwer</tt> page.
     * 
     * @return false
     */
    public boolean onCancelClick() {
        setRedirect("departments-viewer.htm");
        return false;
    }
}
