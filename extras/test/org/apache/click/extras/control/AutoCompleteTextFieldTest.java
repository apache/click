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

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.Page;
import org.apache.click.element.Element;

public class AutoCompleteTextFieldTest extends TestCase {
    
    public void testParameterRendering() {
        MockContext mockContext = MockContext.initContext();
        
        AutoCompleteTextField field = new AutoCompleteTextField("id") {

            @Override
            public List<?> getAutoCompleteList(String criteria) {
                return new ArrayList();
            }
        };

        // Set parameter
        field.setParameter("a", "b");

        Page p = new Page();
        p.addControl(field);

        String expected = "new Ajax.Autocompleter('id','id-auto-complete-div','/mock/mock.htm',{parameters: 'id=1&a=b',minChars:1});});";
        Element e = field.getHeadElements().get(5);
        assertTrue(e.toString().contains(expected));
    }

}
