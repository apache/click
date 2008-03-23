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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.FileUploadService;

/**
 * Mock implementation of {@link net.sf.click.ClickServlet}.
 * <p/>
 * <b>Note:</b> {@link net.sf.click.MockContext} contains convenience methods
 * to quickly instantiate all mock objects needed for unit testing.
 *
 * @author Bob Schellink
 */
public class MockClickServlet extends ClickServlet {

    // -------------------------------------------------------- Private variables

    /** Stores all pages created during the request. */
    private Map pageInstanceMap = new HashMap();

    // -------------------------------------------------------- Public methods

    /**
     * This version of {@link javax.servlet.Servlet#init(javax.servlet.ServletConfig)}
     * does not throw a ServletException making it easier to use in a mock
     * scenario.
     *
     * @param servletConfig the ServletConfig object that contains configuration
     * information for this servlet
     */
    public void init(ServletConfig servletConfig) {
        // Purpose of overriding this method is to coerce ServletException to
        // a RuntimeException
        try {
            super.init(servletConfig);
        } catch (ServletException ex) {
            throw new MockContainer.CleanRuntimeException(ex);
        }
    }

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, boolean isPost) {
        pageInstanceMap.clear();

        // super#handleRequest() removes the context and logger from the thread.
        // Here we create an instance of Context before calling
        // super#handleRequest().
        Context contextHolder = null;
        contextHolder = createContext(request, response, isPost);

        // Push the new Context onto the stack. super#handleRequest will push the
        // same instance onto the stack and pop it before returning. After
        // super#handleRequest returns this contextHolder will be on top of the
        // ContextStack and calls to Context#getThreadLocalContext will still work.
        Context.pushThreadLocalContext(contextHolder);

        super.handleRequest(request, response, isPost);

        // Restore ClickLogger references to the Thread
        ClickLogger.setInstance(logger);
    }

    public String getServletInfo() {
        return "mock servlet info";
    }

    public String getServletName() {
        return getServletConfig().getServletName();
    }

    /**
     * Return the {@link net.sf.click.Page} for the specified class.
     *
     * @param pageClass specifies the class of the Page to return
     * @return the specified pageClass Page instance
     */
    public Page getPage(Class pageClass) {
        return (Page) pageInstanceMap.get(pageClass);
    }

    //---------------------------------------------- protected methods

    protected Page newPageInstance(String path, Class pageClass, HttpServletRequest request) throws Exception {
        Page page = super.newPageInstance(path, pageClass, request);
        pageInstanceMap.put(pageClass, page);
        return page;
    }

    protected Context createContext(HttpServletRequest request,
        HttpServletResponse response, boolean isPost) {
        /*
        Overridden to ensure only a single context is created for this request.
        In a mock environment a mock Context can be created before the servlet
        is requested. The user would expect the same context to be used in the
        servlet as well. By overriding this method we ensure the servlet does
        not create a new Context if one already exists.
        */
        try {
            return Context.getThreadLocalContext();
        } catch (Exception expected) {
            return super.createContext(request, response, isPost);
        }
    }

    //---------------------------------------------- package private methods

    ClickApp createClickApp() {
        return new MockClickApp();
    }

    ClickRequestWrapper createClickRequestWrapper(HttpServletRequest request, FileUploadService fileUploadService) {
        return new MockClickRequestWrapper(request, fileUploadService);
    }

    ClickService getClickService() {
        return clickService;
    }
}
