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

    // -------------------------------------------------------- Constants

    public static final String MOCK_PAGE_REFERENCE = "_page_reference";

    // -------------------------------------------------------- Public methods

    protected void handleRequest(HttpServletRequest request, HttpServletResponse response, boolean isPost) {

        // super#handleRequest() removes the context and logger from the thread.
        // Here we create an instance of Context before calling
        // super#handleRequest().
        Context contextHolder = null;
        contextHolder = createContext(request, response, isPost);

        super.handleRequest(request, response, isPost);

        // super#handleRequest will push a new Context onto the stack but pop
        // it off before returning.
        // Push the previously created Context onto the stack ContextStack and calls to Context#getThreadLocalContext will still work.
        Context.pushThreadLocalContext(contextHolder);

        // Restore ClickLogger references to the Thread
        ClickLogger.setInstance(logger);
    }

    //---------------------------------------------- protected methods

    protected Page newPageInstance(String path, Class pageClass, HttpServletRequest request) throws Exception {
        Page page = super.newPageInstance(path, pageClass, request);
        request.setAttribute(MOCK_PAGE_REFERENCE, page);
        return page;
    }

    //---------------------------------------------- package private methods

    ClickRequestWrapper createClickRequestWrapper(HttpServletRequest request, FileUploadService fileUploadService) {
        return new MockClickRequestWrapper(request, fileUploadService);
    }

    ClickService getClickService() {
        return clickService;
    }
}
