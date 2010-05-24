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
package org.apache.click.service;

import java.io.File;
import java.security.AccessControlException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.click.util.ClickUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.lang.Validate;

/**
 * Provides an Apache Commons FileUploadService class.
 * <p/>
 * To prevent users from uploading exceedingly large files you can configure
 * CommonsFileUploadService through the properties {@link #sizeMax} and
 * {@link #fileSizeMax}.
 * <p/>
 * For example:
 * <pre class="prettyprint">
 * &lt;file-upload-service&gt;
 *     &lt;!-- Set the total request maximum size to 10mb (10 x 1024 x 1024 = 10485760). --&gt;
 *     &lt;property name="sizeMax" value="10485760"/&gt;
 *
 *     &lt;!-- Set the maximum individual file size to 2mb (2 x 1024 x 1024 = 2097152). --&gt;
 *     &lt;property name="fileSizeMax" value="2097152"/&gt;
 * &lt;/file-upload-service&gt; </pre>
 *
 * Note that this is a global configuration and applies to the all file uploads
 * of the application.
 * <p/>
 * If you would like to specify a custom FileUploadService implementation
 * use the <tt>classname</tt> attribute:
 * <pre class="prettyprint">
 * &lt;file-upload-service classname="com.mycorp.util.CustomFileUploadService"&gt;
 *     &lt;property name="customProperty" value="customValue"/&gt;
 * &lt;/file-upload-service&gt; </pre>
 */
public class CommonsFileUploadService implements FileUploadService {

    // -------------------------------------------------------------- Constants

    /** The total request maximum size in bytes. By default there is no limit. */
    protected long sizeMax;

    /** The maximum individual file size in bytes. By default there is no limit. */
    protected long fileSizeMax;

    // --------------------------------------------------------- Public Methods

    /**
     * @see FileUploadService#onInit(ServletContext)
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the FileUploadService
     */
    public void onInit(ServletContext servletContext) throws Exception {
        ConfigService configService = ClickUtils.getConfigService(servletContext);
        LogService logService = configService.getLogService();

        boolean logWarning = false;
        boolean restrictedEnvironment = false;

        // Uploaded files are saved to java.io.tmpdir. Here we check if this
        // directory exists, if it does does not, log a warning
        String tmpdir = System.getProperty("java.io.tmpdir");

        try {
            if (tmpdir == null) {
                logWarning = true;

            } else {
                File tmpfile = new File(tmpdir);
                if (!tmpfile.exists()) {
                    logWarning = true;
                }
            }

            if (!ClickUtils.isResourcesDeployable(servletContext)) {
                restrictedEnvironment = true;
            }

        } catch (AccessControlException exception) {
            if (ClickUtils.isResourcesDeployable(servletContext)) {
                // If resources are deployable, throw exception
                throw exception;
            } else {
                restrictedEnvironment = true;
            }
        }

        if (logWarning) {
            logService.warn("The java.io.tmpdir directory, '" + tmpdir
                + "', does not exist. This can cause file uploading to fail"
                + " as uploaded files are saved to the directory specified by"
                + " the 'java.io.tmpdir' property.");
        }

        if (restrictedEnvironment) {
            // Probably deploying on Google App Engine which throws
            // Security Exception if accessing temp folder
            logService.warn("If you are deploying to Google App Engine,"
                + " please note that Click's default"
                + " 'org.apache.click.service.CommonsFileUploadService'"
                + " does not work with Google App Engine. Instead use"
                + " 'org.apache.click.extras.gae.MemoryFileUploadService'.");
        }
    }

    /**
     * @see FileUploadService#onDestroy()
     */
    public void onDestroy() {
    }

    /**
     * @see FileUploadService#parseRequest(HttpServletRequest)
     *
     * @param request the servlet request
     * @return the list of FileItem instances parsed from the request
     * @throws FileUploadException if request cannot be parsed
     */
    @SuppressWarnings("unchecked")
    public List<FileItem> parseRequest(HttpServletRequest request)
            throws FileUploadException {

        Validate.notNull(request, "Null request parameter");

        FileItemFactory fileItemFactory = createFileItemFactory(request);

        FileUploadBase fileUpload = new ServletFileUpload();
        fileUpload.setFileItemFactory(fileItemFactory);

        if (fileSizeMax > 0) {
            fileUpload.setFileSizeMax(fileSizeMax);
        }
        if (sizeMax > 0) {
            fileUpload.setSizeMax(sizeMax);
        }

        ServletRequestContext requestContext = new ServletRequestContext(request);

        return fileUpload.parseRequest(requestContext);
    }

    /**
     * Return maximum individual size in bytes. By default there is no limit.
     *
     * @return the fileSizeMax
     */
    public long getFileSizeMax() {
        return fileSizeMax;
    }

    /**
     * Set the maximum individual size in bytes. By default there is no limit.
     *
     * @param value the fileSizeMax to set
     */
    public void setFileSizeMax(long value) {
        this.fileSizeMax = value;
    }

    /**
     * Return the total request maximum size in bytes. By default there is no limit.
     *
     * @return the setSizeMax
     */
    public long getSizeMax() {
        return sizeMax;
    }

    /**
     * Set the total request maximum size in bytes. By default there is no limit.
     *
     * @param value the setSizeMax to set
     */
    public void setSizeMax(long value) {
        this.sizeMax = value;
    }

    /**
     * Create and return a new Commons Upload FileItemFactory instance.
     *
     * @param request the servlet request
     * @return a new Commons FileUpload FileItemFactory instance
     */
    public FileItemFactory createFileItemFactory(HttpServletRequest request) {
        return new DiskFileItemFactory();
    }

}
