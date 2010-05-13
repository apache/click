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
package org.apache.click.examples.service;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.click.examples.domain.Course;
import org.apache.click.examples.domain.Student;
import org.apache.click.examples.domain.StudentHouse;
import org.apache.click.extras.cayenne.CayenneTemplate;
import org.springframework.stereotype.Component;

/**
 * Provides a Student Service.
 */
@Component
public class StudentService extends CayenneTemplate {

    // ---------------------------------------------------------- Student logic

    @SuppressWarnings("unchecked")
    public List<Student> getStudents() {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("studentHouse");
        query.addOrdering("db:id", true);
        return performQuery(query);
    }

    @SuppressWarnings("unchecked")
    public Student getStudent(Object id) {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("courses");
        query.addPrefetch("studentHouse");
        query.setQualifier(ExpressionFactory.matchDbExp("id", id));

        List<Student> students = getDataContext().performQuery(query);

        if (students.size() == 0) {
            return null;
        }

        if (students.size() == 1) {
            return students.get(0);
        } else {
            String msg = "SelectQuery for " + Student.class.getName()
                    + " where id equals " + id + " returned "
                    + students.size() + " rows";
            throw new RuntimeException(msg);
        }
    }

    public void saveStudent(Student student) {
        if (student.getObjectContext() == null) {
            registerNewObject(student);
        }
        commitChanges();
    }

    // ----------------------------------------------------------- Course logic

    @SuppressWarnings("unchecked")
    public void setStudentCourses(Student student, List courseIds) {
        // First remove current courses
        List<Course> removes = student.getCourses();
        for (Course course : removes) {
            student.removeFromCourses(course);
        }

        if (courseIds == null || courseIds.isEmpty()) {
            return;
        }

        // Next, set the new courses
        SelectQuery query = new SelectQuery(Course.class);
        query.setQualifier(ExpressionFactory.inDbExp("id", courseIds));
        List<Course> courses = getDataContext().performQuery(query);

        for (Course course : courses) {
            student.addToCourses(course);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Course> getCourses() {
        SelectQuery query = new SelectQuery(Course.class);
        query.addOrdering("db:id", true);
        return (List<Course>) performQuery(query);
    }

    public Course getCourse(Object id) {
        return (Course) getObjectForPK(Course.class, id);
    }

    public void saveCourse(Course course) {
        if (course.getObjectContext() == null) {
            registerNewObject(course);
        }
        commitChanges();
    }

    // ---------------------------------------------------- Student House logic

    @SuppressWarnings("unchecked")
    public List<Student> getStudentsByHouse() {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("studentHouse");

        // Add in-memory ordering
        Ordering ordering = new Ordering("studentHouse.name", true);
        List<Student> result = performQuery(query);
        ordering.orderList(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<StudentHouse> getStudentHouses() {
        SelectQuery query = new SelectQuery(StudentHouse.class);
        query.addOrdering(StudentHouse.NAME_PROPERTY, true);
        return (List<StudentHouse>) performQuery(query);
    }

    public StudentHouse getStudentHouse(Object id) {
        return (StudentHouse) getObjectForPK(StudentHouse.class, id);
    }

    public void saveStudentHouse(StudentHouse studentHouse) {
        if (studentHouse.getObjectContext() == null) {
            registerNewObject(studentHouse);
        }
        commitChanges();
    }
}
