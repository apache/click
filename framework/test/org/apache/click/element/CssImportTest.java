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
package org.apache.click.element;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

/**
 * Provide tests for CssImport.
 */
public class CssImportTest extends TestCase {

    public void testMarkup() {
        CssImport cssImport = new CssImport("test.css");
        cssImport.setId("css-id");
        String cssImportStr = cssImport.toString();
        assertTrue(StringUtils.countMatches(cssImportStr, "href=") == 1);
        assertTrue(StringUtils.countMatches(cssImportStr, "id=") == 1);
    }
}
