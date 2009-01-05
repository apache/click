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

import org.apache.click.MockContext;
import junit.framework.TestCase;
import org.apache.click.servlet.MockRequest;

public class TelephoneFieldTest extends TestCase {
    
    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        
        TelephoneField telephoneField = new TelephoneField("telephone");
        assertEquals("telephone", telephoneField.getName());
        
        request.getParameterMap().put("telephone", "02 8734 7653");
        
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("02 8734 7653", telephoneField.getValue());
        assertEquals("02 8734 7653", telephoneField.getValueObject());
        
        request.getParameterMap().put("telephone", "1800-DOCTOR");
        
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("1800-DOCTOR", telephoneField.getValue());
  
        request.getParameterMap().put("telephone", "01-923 02 2345 3654");

        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("01-923 02 2345 3654", telephoneField.getValue());
        
        request.getParameterMap().put("telephone", "");
        
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("", telephoneField.getValue());
        assertNull(telephoneField.getValueObject());
        
        telephoneField.setRequired(true);
        
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("", telephoneField.getValue());
        assertEquals(null, telephoneField.getValueObject());
        
        request.getParameterMap().put("telephone", "(01) 2345 3654");
        
        telephoneField.setMinLength(10);
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());
                
        telephoneField.setMinLength(20);
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());   
        
        telephoneField.setMinLength(0);
        
        telephoneField.setMaxLength(20);
        assertTrue(telephoneField.onProcess());
        assertTrue(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());
                
        telephoneField.setMaxLength(10);
        assertTrue(telephoneField.onProcess());
        assertFalse(telephoneField.isValid());
        assertEquals("(01) 2345 3654", telephoneField.getValue());
        assertEquals("(01) 2345 3654", telephoneField.getValueObject());   
    }

}
