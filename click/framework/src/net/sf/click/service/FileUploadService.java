/*
 * Copyright 2008 Malcolm A. Edgar
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
package net.sf.click.service;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

/**
 * Provides a file upload service interface.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public interface FileUploadService {

    /** The attribute key used for storing an upload exception. */
    public static final String UPLOAD_EXCEPTION = "_upload_exception";

    /**
     * Initialize the FileUploadService with the given application servlet context.
     * <p/>
     * This method is invoked after the FileUploadService has been constructed.
     *
     * @param servletContext the application servlet context
     * @throws Exception if an error occurs initializing the FileUploadService
     */
    public void onInit(ServletContext servletContext) throws Exception;

    /**
     * Destroy the FileUploadService.
     */
    public void onDestroy();

    /**
     * Return a parsed list of FileItem from the request.
     *
     * @param request the servlet request
     * @return the list of FileItem instances parsed from the request
     * @throws FileUploadException if request cannot be parsed
     */
     public List parseRequest(HttpServletRequest request) throws FileUploadException;

}
