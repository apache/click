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
package org.apache.click.util;

import java.util.Arrays;
import junit.framework.TestCase;
import org.apache.click.MockContext;

/**
 * Tests for ContainerUtils.
 */
public class FormatTest extends TestCase {

    public void testMessage() {
        MockContext.initContext();

        // Test message and single argument
        String expected = "hello world";
        Format format = new Format();
        String actual = format.message("hello {0}", "world");
        assertEquals(expected, actual);

        // Test message and array of arguments
        expected = "hello world array";
        actual = format.message("hello {0} {1}", new String[] {"world", "array"});
        assertEquals(expected, actual);

        // Test message and list of arguments
        expected = "hello world list";
        actual = format.message("hello {0} {1}", Arrays.asList(new String[] {"world", "list"}));
        assertEquals(expected, actual);
    }
}
