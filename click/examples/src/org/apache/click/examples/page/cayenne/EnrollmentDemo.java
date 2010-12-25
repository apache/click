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
import org.apache.click.control.Column;
import org.apache.click.control.TextField;
import org.apache.click.examples.domain.Course;
import org.apache.click.examples.domain.Student;
import org.apache.click.examples.service.StudentService;
import org.apache.click.extras.control.PickList;
import org.springframework.stereotype.Component;

/**
 * This example demonstrates how to represent a many-to-many relationship
 * between Students and Courses using Apache Click.
 *
 * A Student can take many Courses, and a Course can be taken by many Students.
 *
 * The relationship is managed by a PickList control.
 */
@Component
public class EnrollmentDemo extends FormTablePage {

    private static final long serialVersionUID = 1L;

    @Resource(name="studentService")
    private StudentService studentService;

    private PickList pickList;

    // Event Handlers ---------------------------------------------------------

    /**
     * @see FormTablePage#onInit()
     */
    @Override
    public void onInit() {
        form.add(new TextField("name")).setRequired(true);

        pickList = new PickList("courseList", "Courses");
        pickList.addAll(studentService.getCourses(), "id", "name");
        form.add(pickList);

        // Table
        table.addColumn(new Column("id"));
        table.addColumn(new Column("name"));

        super.onInit();
    }

    /**
     * @see FormTablePage#onSaveClick()
     */
    @Override
    public boolean onSaveClick() {
        if (form.isValid()) {
            Student student = (Student) form.getDataObject();

            List courseIds = pickList.getSelectedValues();
            studentService.setStudentCourses(student, courseIds);

            saveDataObject(student);

            clear();
        }
        return true;
    }

    /**
     * @see FormTablePage#onEditClick()
     */
    @Override
    public boolean onEditClick() {
        Student student = getSelectedStudent();

        // If no student is selected, exist early
        if (student == null) {
            return true;
        }

        // Add each student course to the PickList
        List courses = student.getCourses();
        for (int i = 0; i < courses.size(); i++) {
            Course course = (Course) courses.get(i);
            pickList.addSelectedValue(course.getId().toString());
        }
        form.setDataObject(student);

        return true;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * @see FormTablePage#clear()
     */
    @Override
    public void clear() {
        pickList.setSelectedValues(null);
        super.clear();
    }

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
    public List getRowList() {
        return studentService.getStudents();
    }

    /**
     * Return the selected student or null if no student is selected.
     *
     * @return the selected student or null if no student is selected
     */
    private Student getSelectedStudent() {
        Student student = null;

        Integer id = editLink.getValueInteger();
        if (id != null) {
            student = (Student) getDataObject(id);
        }
        return student;
    }
}
