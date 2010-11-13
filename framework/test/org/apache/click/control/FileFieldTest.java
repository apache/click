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

import java.io.File;
import java.net.URI;
import java.net.URL;
import junit.framework.TestCase;
import org.apache.click.MockContainer;
import org.apache.click.MockContext;
import org.apache.click.pages.FileFieldPage;

/**
 * Test FileField behavior.
 */
public class FileFieldTest extends TestCase {

    /**
     * Check that FileField value is escaped. This protects against
     * cross-site scripting attacks (XSS).
     */
    public void testEscapeValue() {
        MockContext.initContext();

        FileField field = new FileField("name");
        String value = "<script>";
        String expected = "&lt;script&gt;";

        field.setValue(value);

        assertTrue(field.toString().indexOf(expected) > 1);
        
        // Check that the value <script> is not rendered
        assertTrue(field.toString().indexOf(value) < 0);
    }

    /**
     * Check that FileField onProcess works properly for multipart requests.
     */
    public void testOnProcess() {
        try {
            MockContainer container = new MockContainer("web");

        container.start();

          // Prepare a file for upload
        String fileName = "file-field.htm";
        String filePath = "/web/" + fileName;
            URL resource = container.getClass().getResource(filePath);
            URI uri = new URI(resource.toString());
            File file = new File(uri);

            // Prepare container parameters
            String fieldName = "fileField";
            container.setParameter(fieldName, file, "text/html");
            container.setParameter("form_name", "form");

            FileFieldPage page = container.testPage(FileFieldPage.class);

        FileField field = page.getFileField();

        // Perform tests
        assertNotNull(field.getFileItem());
        field.getFileItem().getName();
        assertEquals(fieldName, field.getFileItem().getFieldName());
        assertEquals(fileName, field.getFileItem().getName());
        assertEquals(file.length(), field.getFileItem().getSize());

        container.stop();

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
