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
package org.apache.click.extras.control;

import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.control.TextField;

public class HtmlFieldSetTest extends TestCase {

    /**
     * Check that HtmlFieldSet properly opens and closes the fieldset tag.
     * CLK-427.
     */
    public void testHtmlFieldSetRender() {
        MockContext.initContext();
        HtmlFieldSet fieldset = new HtmlFieldSet("fieldset");
        fieldset.add(new TextField("name"));
        fieldset.add(new TextField("id"));

        // Ensure fieldset tag is opened correctly
        assertTrue(fieldset.toString().indexOf("<fieldset") == 0);

        // Ensure fieldset tag is closed correctly
        assertTrue(fieldset.toString().indexOf("</fieldset>") > 0);
    }
}
