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

import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.extras.cayenne.CayenneForm;
import org.apache.click.extras.cayenne.PropertySelect;
import org.apache.click.extras.control.DateField;
import org.apache.click.extras.control.DoubleField;

import org.apache.cayenne.query.SelectQuery;

import click.cayenne.entity.Department;
import click.cayenne.entity.Person;

/**
 * Provides a Person Editor page which can be used to create or edit
 * Person data objects. This page uses the data aware CayenneForm control
 * for simplified editing data objects.
 * 
 * @author Ahmed Mohombe
 */
public class PersonEditor  extends BorderedPage {

    /** The Person editing CayenneForm. */
    protected CayenneForm form = new CayenneForm("form", Person.class);
    
    /**
     * Create a new Person Editor.
     */
    public PersonEditor() {
        form.add(new TextField("fullName", "Full Name", 35));
        form.add(new DateField("dateHired", "Date Hired"));
        form.add(new DoubleField("baseSalary", "Base Salary"));
        
        PropertySelect department = new PropertySelect("department", true);
        department.setOptionLabel("name");
        department.setSelectQuery(new SelectQuery(Department.class));
        form.add(department);

        form.add(new Submit("ok", "   OK   ", this, "onOkClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));
        
        form.setButtonAlign("right");
        addControl(form);
    }

    /**
     * Set the person object to edit.
     *
     * @param person the department to edit
     */
    public void setPerson(Person person) {
        form.setDataObject(person);
    }
    
    /**
     * Handle the OK button click, saving the Person if valid and
     * redirecting to the <tt>PersonsViewer</tt> page. If the Person
     * is not valid display form errors.
     */
    public boolean onOkClick() {
        if (form.isValid()) {
            if (form.saveChanges()) {
                setRedirect("persons-viewer.htm");
                return false;
            }
        }
        return true;
    }

    /**
     * Handle the Cancel button click, redirecting to the
     * <tt>PersonsViwer</tt> page.
     *
     * @return false
     */
    public boolean onCancelClick() {
        setRedirect("persons-viewer.htm");
        return false;
    }

}
