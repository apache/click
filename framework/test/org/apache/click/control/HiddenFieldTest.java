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

import java.sql.Timestamp;
import java.util.Date;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.servlet.MockRequest;
import org.apache.click.util.User;

/**
 * Test HiddenField behavior.
 */
public class HiddenFieldTest extends TestCase {

    /**
     * Check that FileField value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        HiddenField field = new HiddenField("name", String.class);
        String value = "<script>";
        String expected = "&lt;script&gt;";

        field.setValue(value);

        assertTrue(field.toString().indexOf(expected) > 1);

        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }

    /**
     * Check that timestamp copies to and from a HiddenField.
     *
     * CLK-484.
     */
    public void testTimestampCopy() {
        MockContext context = MockContext.initContext();
        MockRequest request = context.getMockRequest();
        request.setParameter("timestamp", String.valueOf(new Date().getTime()));

        // set up the form for copyTo
        Form form = new Form("sample");

        HiddenField hiddenField = new HiddenField("timestamp", Timestamp.class);
        form.add(hiddenField);

        hiddenField.bindRequestValue();

        User user = new User();

        form.copyTo(user, true);
        assertEquals(hiddenField.getValueObject(), user.getTimestamp());

        // set up the form for copyFrom
        form = new Form("sample");
        hiddenField = new HiddenField("timestamp", Timestamp.class);
        form.add(hiddenField);

        user = new User();
        user.setTimestamp(new Timestamp(new Date().getTime()));
        form.copyFrom(user);

        assertEquals(user.getTimestamp(), hiddenField.getValueObject());
    }
}
