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
package org.apache.click.extras.gae;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.click.service.CommonsFileUploadService;
import org.apache.commons.fileupload.FileItemFactory;

/**
 * Provides an Apache Commons In-Memory FileUploadService class.
 * <p/>
 * This service creates an {@link MemoryFileItemFactory} for creating
 * {@link MemoryFileItem In-Memory FileItem instances} which content is never
 * written to disk.
 * <p/>
 * This service is recommended to be used with Google App Engine (GAE) which
 * doesn't allow Web Application access to disk.
 * <p/>
 * To use this service in your GAE applications, add the following to your
 * <tt>click.xml</tt> config:
 *
 * <pre class="prettyprint">
 * &lt;file-upload-service classname="org.apache.click.extras.gae.MemoryFileUploadService"&gt;
 *     &lt;!-- Set the total request maximum size to 10mb (10 x 1024 x 1024 = 10485760). --&gt;
 *     &lt;property name="sizeMax" value="10485760"/&gt;
 *
 *     &lt;!-- Set the maximum individual file size to 2mb (2 x 1024 x 1024 = 2097152). --&gt;
 *     &lt;property name="fileSizeMax" value="2097152"/&gt;
 * &lt;/file-upload-service&gt; </pre>
 *
 * To prevent users from uploading exceedingly large files you can configure
 * MemoryFileUploadService through the properties {@link #setSizeMax(long)} and
 * {@link #setFileSizeMax(long)}, as demonstrated above.
 * <p/>
 * <b>Please note:</b> Google App Engine further restricts the size of file
 * uploads as well. Currently the limit is 10MB.
 */
public class MemoryFileUploadService extends CommonsFileUploadService {

    /**
     * @see org.apache.click.service.FileUploadService#onInit(ServletContext)
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the FileUploadService
     */
    @Override
    public void onInit(ServletContext servletContext) throws Exception {
    }

    /**
     * Create and return a new {@link MemoryFileItemFactory} instance.
     *
     * @param request the servlet request
     * @return a new MemoryFileItemFactory instance
     */
    @Override
    public FileItemFactory createFileItemFactory(HttpServletRequest request) {
        return new MemoryFileItemFactory();
    }
}
