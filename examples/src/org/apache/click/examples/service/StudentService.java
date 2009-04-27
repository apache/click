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

import java.util.Iterator;
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
 *
 * @author Bob Schellink
 */
@Component
public class StudentService extends CayenneTemplate {

    // ---------------------------------------------------------- Student logic

    public List getStudents() {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("studentHouse");
        query.addOrdering("db:id", true);
        return performQuery(query);
    }

    public Student getStudent(Object id) {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("courses");
        query.addPrefetch("studentHouse");
        query.setQualifier(ExpressionFactory.matchDbExp("id", id));

        List students = getDataContext().performQuery(query);

        if (students.size() == 0) {
            return null;
        }

        if (students.size() == 1) {
            return (Student) students.get(0);
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

    public void setStudentCourses(Student student, List courseIds) {
        // First remove current courses
        List removes = student.getCourses();
        for (int i = removes.size() - 1; i >= 0; i--) {
            Course course = (Course) removes.get(i);
            student.removeFromCourses(course);
        }

        if (courseIds == null || courseIds.isEmpty()) {
            return;
        }

        // Next, set the new courses
        SelectQuery query = new SelectQuery(Course.class);
        query.setQualifier(ExpressionFactory.inDbExp("id", courseIds));
        List courses = getDataContext().performQuery(query);

        for (Iterator it = courses.iterator(); it.hasNext(); ) {
            Course course = (Course) it.next() ;
            student.addToCourses(course);
        }
    }

    public List getCourses() {
        SelectQuery query = new SelectQuery(Course.class);
        query.addOrdering("db:id", true);
        return performQuery(query);
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

    public List getStudentsByHouse() {
        SelectQuery query = new SelectQuery(Student.class);
        query.addPrefetch("studentHouse");

        // Add in-memory ordering
        Ordering ordering = new Ordering("studentHouse.name", true);
        List result = performQuery(query);
        ordering.orderList(result);
        return result;
    }

    public List getStudentHouses() {
        SelectQuery query = new SelectQuery(StudentHouse.class);
        query.addOrdering(StudentHouse.NAME_PROPERTY, true);
        return performQuery(query);
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
