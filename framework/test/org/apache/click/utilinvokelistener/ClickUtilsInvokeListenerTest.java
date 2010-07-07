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
package org.apache.click.utilinvokelistener;

import org.apache.click.util.ClickUtils;
import junit.framework.TestCase;

/**
 * Tests ClickUtils.invokeLister(). This is in a separate package
 * because otherwise the protected, package-private restrictions
 * would have no meaning.
 */
public class ClickUtilsInvokeListenerTest extends TestCase {

    /**
     * Test whether invoke listener functions correctly.
     */
    public void testInvokeListener() {
        ListenerMock lm = new ListenerMock();
        
        assertTrue(ClickUtils.invokeListener(lm, "onClickTrue"));
        assertEquals(1,lm.called);
        lm.called = 0;
        
        assertFalse(ClickUtils.invokeListener(lm, "onClickFalse"));
        assertEquals(1,lm.called);
        lm.called = 0;
        
        try{
            ClickUtils.invokeListener(lm,"noReturn");
            fail();
        }catch(Exception e){}
       
        try{
            ClickUtils.invokeListener(lm,"privateMethod");
            fail();
        }catch(Exception e){}

        try{
            ClickUtils.invokeListener(lm,"protectedMethod");
            fail();
        }catch(Exception e){}
        
        try{
            ClickUtils.invokeListener(lm,"packagePrivateMethod");
            fail();
        }catch(Exception e){}
        
        PrivListenerMock pM = new PrivListenerMock();
        try{
            ClickUtils.invokeListener(pM,"onClick");
            fail();
        }catch(Exception e){}
        
        //the anonymous inner class
        Object anon = new Object() {
            @SuppressWarnings("unused")
            public boolean onClick(){
                return true;
            }
            @SuppressWarnings("unused")
            private boolean privateMethod(){
                return true;
            }
            @SuppressWarnings("unused")
            protected boolean protectedMethod() {
                return true;
            }
            @SuppressWarnings("unused")
            boolean packagePrivateMethod(){
                return false;
            }
        };
        
        assertTrue(ClickUtils.invokeListener(anon, "onClick"));
        
        try{
            ClickUtils.invokeListener(anon,"noMethod");
            fail();
        }catch(Exception e){}
       
        try{
            ClickUtils.invokeListener(anon,"privateMethod");
            fail();
        }catch(Exception e){}

        try{
            ClickUtils.invokeListener(anon,"protectedMethod");
            fail();
        }catch(Exception e){}
        
        try{
            ClickUtils.invokeListener(anon,"packagePrivateMethod");
            fail();
        }catch(Exception e){}
        
    }
    
    /**
     * Public mock class which listens to events.
     */
    public static class ListenerMock {
        /** Counts the amount of times listeners are fired. */
        int called = 0;

        /**
         * onClickTrue event handler.
         *
         * @return true if processing should continue
         */
        public boolean onClickTrue(){
           called++;
           return true;
        }
        
        /**
         * onClickFalse event handler.
         *
         * @return true if processing should continue
         */
        public boolean onClickFalse() {
            called++;
            return false;
        }
        
        /**
         * An event handler.
         */
        public void noReturn() {
        }
        
        /**
         * An event handler.
         *
         * @return true if processing should continue
         */
        @SuppressWarnings("unused")
        private boolean privateMethod(){
            return true;
        }
        
        /**
         * An event handler.
         *
         * @return true if processing should continue
         */
        protected boolean protectedMethod() {
            return true;
        }

        /**
         * An event handler.
         *
         * @return true if processing should continue
         */
        boolean packagePrivateMethod(){
            return true;
        }
    }
    
    /**
     * Private mock class which listens to events.
     */
    private static class PrivListenerMock {

        /**
         * onClick event handler.
         *
         * @return true if processing should continue
         */
        @SuppressWarnings("unused")
        public boolean onClick(){
            return true;
        }
    }
}
