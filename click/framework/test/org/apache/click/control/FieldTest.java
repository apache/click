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
package org.apache.click.control;

import junit.framework.TestCase;
import org.apache.click.MockContext;

/**
 * Test Field behavior.
 */
public class FieldTest extends TestCase {

    /**
     * Check that Field label style is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testLabelStyle() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setLabelStyle("color: green");
        assertTrue(form.toString().contains("<label for=\"form_field\" style=\"color: green\">"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setLabelStyle("color: green");
        assertTrue(fs.toString().contains("<label for=\"form_field\" style=\"color: green\">"));
    }

    /**
     * Check that Field label style class is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testLabelStyleClass() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setLabelStyleClass("autumn");
        assertTrue(form.toString().contains("<label for=\"form_field\" class=\"autumn\">"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        // FieldStyle value should be overridden by the parentStyleHint below
        form.setFieldStyle("font-weight:bold");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setLabelStyleClass("autumn");
        assertTrue(fs.toString().contains("<label for=\"form_field\" class=\"autumn\">"));
    }

    /**
     * Check that Field parent style hint is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testParentStyleHint() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setParentStyleHint("color: green");
        // Check that style hint is rendered on the label and field cells
        assertTrue(form.toString().contains("<td class=\"fields\" align=\"left\" style=\"color: green\"><label"));
        assertTrue(form.toString().contains("<td align=\"left\" style=\"color: green\"><input"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        // FieldStyle value should be overridden by the parentStyleHint below
        form.setFieldStyle("font-weight:bold");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setParentStyleHint("color: green");
        // Check that style hint is rendered on the label and field cells
        assertTrue(fs.toString().contains("<td class=\"fields\" align=\"left\" style=\"color: green\"><label"));
        assertTrue(fs.toString().contains("<td align=\"left\" style=\"color: green\"><input"));
    }

    /**
     * Check that Field parent style class hint is rendered by Form and FieldSet.
     *
     * CLK-595
     */
    public void testParentStyleClassHint() {
        MockContext.initContext();

        // Check that Form renders the field label style
        Form form = new Form("form");
        Field field = new TextField("field");
        form.add(field);

        field.setParentStyleClassHint("autumn");
        // Check that style class hint is rendered on the label and field cells
        assertTrue(form.toString().contains("<td class=\"fields autumn\" align=\"left\"><label"));
        assertTrue(form.toString().contains("<td class=\"autumn\" align=\"left\"><input"));

        // Check that FieldSet renders the field label style
        form = new Form("form");
        FieldSet fs = new FieldSet("fs");
        form.add(fs);
        field = new TextField("field");
        fs.add(field);

        field.setParentStyleClassHint("autumn");
        // Check that style class hint is rendered on the label and field cells
        assertTrue(fs.toString().contains("<td class=\"fields autumn\" align=\"left\"><label"));
        assertTrue(fs.toString().contains("<td class=\"autumn\" align=\"left\"><input"));
    }
}
