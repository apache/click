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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.util.FileUploadService;

/**
 * Mock implementation of {@link net.sf.click.ClickRequestWrapper}.
 *
 * @author Bob Schellink
 */
class MockClickRequestWrapper extends ClickRequestWrapper {

    /**
     * Default constructor.
     *
     * @param request servlet request
     * @param fileUploadService the Commons FileUpload service instance
     */
    public MockClickRequestWrapper(HttpServletRequest request, FileUploadService fileUploadService) {
        super(request, fileUploadService);
    }

    /**
     * Return the original request instead of multipartParameterMap.
     *
     * @return the original request parameter map.
     */
    Map getMultipartParameterMap() {
        // Return the original request map instead of the multipartParamterMap.
        // This enables users to use request.setParameter(String) transparently.
        return getRequest().getParameterMap();
    }
}
