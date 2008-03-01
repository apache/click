/*
 * Copyright 2004-2008 Malcolm A. Edgar
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides a mock Context object for unit testing.
 * 
 * @author Malcolm Edgar
 */
public class MockContext extends Context {
    
    // ----------------------------------------------------------- Constructors
    
    private MockContext() {
        super(new MockRequest(), null);
    }

    private MockContext(Locale locale) {
        super(new MockRequest(locale), null);
    }
    
    private MockContext(HttpServletRequest request) {
        super(request, null);
    }
    
    // --------------------------------------------------------- Public Methods
    
    public static void initContext() {
    	Context.pushThreadLocalContext(new MockContext());
    }
    
    public static void initContext(Locale locale) {
    	Context.pushThreadLocalContext(new MockContext(locale));
    }
    
    public static void initContext(HttpServletRequest request) {
    	Context.pushThreadLocalContext(new MockContext(request));
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
    
    /**
     * @see Context#getRequestParameter(String)
     */
    public String getRequestParameter(String name) {
        return request.getParameter(name);
    }    
   
    /**
     * @see Context#getRequestParameterMap()
     */
    public Map getRequestParameterMap() {
        return request.getParameterMap();
    }

    /**
     * @see Context#getRequestParameterValues(String)
     */
    public String[] getRequestParameterValues(String name) {
        return request.getParameterValues(name);
    }    
}
