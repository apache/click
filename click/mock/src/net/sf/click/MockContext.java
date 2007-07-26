/*
 * Copyright 2004-2006 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides a mock Context object for unit testing.
 * 
 * @author Malcolm Edgar
 */
public class MockContext extends Context {
    
    // ----------------------------------------------------------- Constructors
    
    private MockContext() {
        super(null, null, new MockRequest(), null, true, null);
    }

    private MockContext(Locale locale) {
        super(null, null, new MockRequest(locale), null, true, null);
    }
    
    private MockContext(HttpServletRequest request) {
        super(null, null, request, null, true, null);
    }
    
    // --------------------------------------------------------- Public Methods
    
    public static void initContext() {
    	Context.setThreadLocalContext(new MockContext());
    }
    
    public static void initContext(Locale locale) {
    	Context.setThreadLocalContext(new MockContext(locale));
    }
    
    public static void initContext(HttpServletRequest request) {
    	Context.setThreadLocalContext(new MockContext(request));
    }
    
    /**
     * @see Context#getLocale()
     */
    public Locale getLocale() {
        return request.getLocale();
    }
    
    /**
     * @see Context#getApplicationMode()
     */
    public String getApplicationMode() {
        return "debug";
    }

}
