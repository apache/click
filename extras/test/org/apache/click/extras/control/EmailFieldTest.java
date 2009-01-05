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
import org.apache.click.servlet.MockRequest;

public class EmailFieldTest extends TestCase {

    public void testOnProcess() {
        MockContext mockContext = MockContext.initContext();
        MockRequest request = mockContext.getMockRequest();
        
        EmailField emailField = new EmailField("email");
        assertEquals("email", emailField.getName());
        
        request.getParameterMap().put("email", "username@server.com");
        
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server.com", emailField.getValue());
        assertEquals("username@server.com", emailField.getValueObject());
        
        request.getParameterMap().put("email", "username@");
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@", emailField.getValue());
  
        request.getParameterMap().put("email", "@servr");
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("@servr", emailField.getValue());
        
        request.getParameterMap().put("email", "");
        
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("", emailField.getValue());
        assertNull(emailField.getValueObject());
        
        emailField.setRequired(true);
        
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("", emailField.getValue());
        assertEquals(null, emailField.getValueObject());
        
        request.getParameterMap().put("email", "username@server.com");
        
        emailField.setMinLength(10);
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server.com", emailField.getValue());
        assertEquals("username@server.com", emailField.getValueObject());
                
        emailField.setMinLength(20);
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@server.com", emailField.getValue());
        assertEquals("username@server.com", emailField.getValueObject());   
        
        emailField.setMinLength(0);
        
        emailField.setMaxLength(20);
        assertTrue(emailField.onProcess());
        assertTrue(emailField.isValid());
        assertEquals("username@server.com", emailField.getValue());
        assertEquals("username@server.com", emailField.getValueObject());
                
        emailField.setMaxLength(10);
        assertTrue(emailField.onProcess());
        assertFalse(emailField.isValid());
        assertEquals("username@server.com", emailField.getValue());
        assertEquals("username@server.com", emailField.getValueObject());   
    }

}
