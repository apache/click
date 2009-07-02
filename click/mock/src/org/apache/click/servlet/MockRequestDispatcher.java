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
package org.apache.click.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.click.MockContainer;

/**
 * Mock implementation of {@link javax.servlet.RequestDispatcher}.
 * <p/>
 * This RequestDispatcher sets the resource path on the request when
 * {@link #forward(ServletRequest, ServletResponse)} or
 * {@link #include(ServletRequest, ServletResponse)} is called.
 * <p/>
 * The resourcePath can later be retrieved by calling
 * {@link MockRequest#getForward()} or {@link MockRequest#getIncludes()}.
 */
public class MockRequestDispatcher implements RequestDispatcher {

    /** The resource path to dispatch to. */
    private String resourcePath;

    /**
     * Constructs a new RequestDispatcher instance for the specified
     * resourcePath.
     *
     * @param resourcePath the resource path to dispatch to
     */
    public MockRequestDispatcher(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * This method stores the dispatcher's resourcePath on the request.
     * <p/>
     * The resourcePath can later be retrieved by calling
     * {@link MockRequest#getForward()}
     *
     * @param request the servlet request
     * @param response the servlet response
     *
     * @throws javax.servlet.ServletException if the response was already
     * committed
     * @throws java.io.IOException if the target resource throws this exception
     */
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        MockRequest mockRequest = MockContainer.findMockRequest(request);
        mockRequest.setForward(resourcePath);
    }

    /**
     * This method stores the dispatcher's resourcePath on the request.
     * <p/>
     * The resourcePath can be retrieved by calling
     * {@link MockRequest#getIncludes()}
     *
     * @param request the servlet request
     * @param response the servlet response
     *
     * @throws javax.servlet.ServletException if the response was already
     * committed
     * @throws java.io.IOException if the target resource throws this exception
     */
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        MockRequest mockRequest = MockContainer.findMockRequest(request);
        mockRequest.addInclude(resourcePath);
    }

}
