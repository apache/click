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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.click.MockContainer;
import org.apache.click.pages.SessionMapPage;

/**
 * Tests for SessionMap.
 */
public class SessionMapTest extends TestCase  {
    /**
     * Test SessionMap with a null session.
     */
    public void testNoSession() {
        SessionMap sm = new SessionMap(null);
       
        Assert.assertEquals(0, sm.size());
        Assert.assertEquals(0, sm.keySet().size());
        Assert.assertEquals(0, sm.entrySet().size());
        Assert.assertEquals(0, sm.values().size());
        
        Assert.assertNull(sm.get("attrib1"));
        Assert.assertNull(sm.get(null));
        Assert.assertTrue(sm.isEmpty());
        
        Assert.assertFalse(sm.containsKey("attrib1"));
        Assert.assertFalse(sm.containsKey(null));
        
        Assert.assertNull(sm.put("attrib1", "value1"));
        Assert.assertNull(sm.remove("attrib1"));
    }
    
    /**
     * Test with a session.
     */
    public void testSession() {
        MockContainer container = new MockContainer("web");
        container.start();
        HttpSession session = container.getRequest().getSession();
        SessionMap sm = new SessionMap(session);
       
        Assert.assertEquals(0, sm.size());
        Assert.assertEquals(0, sm.keySet().size());
        Assert.assertEquals(0, sm.entrySet().size());
        Assert.assertEquals(0, sm.values().size());
        
        Assert.assertNull(sm.get("attrib1"));
        Assert.assertNull(sm.get(null));
        Assert.assertTrue(sm.isEmpty());
        
        session.setAttribute("attrib1", "value1");
        
        Assert.assertEquals("value1", sm.get("attrib1"));
        
        Assert.assertEquals(1, sm.size());
        Assert.assertEquals(1, sm.keySet().size());
        Assert.assertTrue(sm.keySet().contains("attrib1"));
        
        Assert.assertEquals(1, sm.entrySet().size());
        Map.Entry<String, Object> entry = sm.entrySet().iterator().next();
        Assert.assertEquals("attrib1", entry.getKey());
        Assert.assertEquals("value1", entry.getValue());

        Assert.assertEquals(1, sm.values().size());
        Assert.assertTrue(sm.values().contains("value1"));

        container.stop();
    }

    /**
     * Test changes to the SessionMap.
     */
    public void testPuts() {
        MockContainer container = new MockContainer("web");
        container.start();
        HttpSession session = container.getRequest().getSession();
        SessionMap sm = new SessionMap(session);
       
        Assert.assertEquals(0, sm.size());
        
        sm.put("attrib1", "value1");
        
        Assert.assertEquals("value1", session.getAttribute("attrib1"));
        
        sm.put("attrib1", "value2");

        Assert.assertEquals("value2", session.getAttribute("attrib1"));

        Assert.assertEquals("value2", sm.remove("attrib1"));
        
        Assert.assertEquals(0, sm.size());
        
        sm.putAll(null);

        Assert.assertEquals(0, sm.size());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attrib2", "value2");
        
        sm.putAll(map);
        Assert.assertEquals(1, sm.size());

        sm.clear();
        
        Assert.assertEquals(0, sm.size());
        
        container.stop();
    }
    
    /**
     * Test iteration over entrySet from velocity.
     */
    public void testPage() {
        MockContainer container = new MockContainer("web");
        container.start();
        HttpSession session = container.getRequest().getSession(true);
        
        session.setAttribute("attrib1", "value1");
        
        container.getRequest().setMethod("GET");

        container.testPage(SessionMapPage.class);
        Assert.assertTrue(container.getHtml().contains("attrib1=value1"));
        
        container.stop();
    }
}
