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
package org.apache.click.examples.page.cayenne;

import java.util.List;
import javax.annotation.Resource;
import org.apache.cayenne.DataObject;

import org.apache.cayenne.query.SelectQuery;
import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Student;
import org.apache.click.examples.domain.StudentHouse;
import org.apache.click.examples.service.StudentService;
import org.apache.click.extras.cayenne.PropertySelect;
import org.springframework.stereotype.Component;

/**
 * This example demonstrates how to represent a one-to-many relationship
 * between StudentHouse and Students using Apache Click.
 *
 * A Student House can have many Students, and a Student can only live
 * in one Student House.
 *
 * The relationship is managed by a Select control.
 */
@Component
public class AccommodationDemo extends FormTablePage {

    private static final long serialVersionUID = 1L;

    @Resource(name="studentService")
    private StudentService studentService;

    private PropertySelect select;

    // Event Handlers ---------------------------------------------------------

    /**
     * @see FormTablePage#onInit()
     */
    @Override
    public void onInit() {
        form.add(new TextField("name")).setRequired(true);

        select = new PropertySelect("studentHouse");

        // Populate the Select control with Student Houses where "id" is the
        // option value and "name" is the option label
        select.setSelectQuery(new SelectQuery(StudentHouse.class));
        select.setOptionLabel("name");
        select.setOptional(true);
        form.add(select);

        // Table
        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));
        table.addColumn(new Column("studentHouse")).setDecorator(new Decorator() {

            public String render(Object object, Context context) {
                Student student = (Student) object;
                if (student.getStudentHouse() != null) {
                    return student.getStudentHouse().getName();
                } else {
                    return "";
                }
            }
        });

        super.onInit();
    }

    // Public Methods --------------------------------------------------------

    /**
     * @see FormTablePage#getDataObject(Object)
     */
    @Override
    public DataObject getDataObject(Object id) {
        return studentService.getStudent(id);
    }

    /**
     * @see FormTablePage#getDataObjectClass()
     */
    @Override
    public Class getDataObjectClass() {
        return Student.class;
    }

    /**
     * @see FormTablePage#getRowList()
     */
    @Override
    public List<Student> getRowList() {
        return studentService.getStudentsByHouse();
    }
}
