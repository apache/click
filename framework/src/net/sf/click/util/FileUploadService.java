/*
 * Copyright 2004-2005 Malcolm A. Edgar
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
package net.sf.click.util;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemFactory;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * Provides the services and configuration needed to parse a multipart request.
 * <p/>
 * This class is built on the Commons FileUpload library.
 * <p/>
 * You can configure Commons FileUpload by overriding one of the createXxx
 * methods.
 * <p/>
 * Below is an example of how to set the maxSize and fileMaxSize configuration:
 *
 * <pre class="codeConfig">
 * <span class="kw">public class</span> AppFileUploadService <span class="kw">extends</span> FileUploadService {
 *
 *     <span class="kw">public </span>FileUploadBase createFileUpload(HttpServletRequest request) {
 *
 *         FileUploadBase fileUploadBase = <span class="kw">super</span>.createFileUpload(request);
 *
 *         <span class="cm">//Set request maximum size to 5mb -> 5,000,000 bytes.</span>
 *         fileUploadBase.setSizeMax(50000000);
 *
 *         <span class="cm">//Set file maximum size to 2mb -> 2,000,000 bytes.</span>
 *         fileUploadBase.setFileSizeMax(2000000);
 *         <span class="kw">return </span>fileUploadBase;
 *     }
 * }
 * </pre>
 *
 * Specify your custom implementation in click.xml:
 *
 * <pre class="codeConfig">
 * &lt;click-app charset="UTF-8" locale="de"&gt;
 *     &lt;pages package="<span class="st">com.mycorp.page</span>"/&gt;
 *     &lt;file-upload-service classname="<span class="st">com.mycorp.util.AppFileUploadService</span>"/&gt;
 * &lt;/click-app&gt;
 * </pre>
 *
 * <b>Please note:</b> this class must have a default no-argument constructor.
 *
 * @author Bob Schellink
 */
public class FileUploadService {

    // -------------------------------------------------------- Constants

    /** The attribute key used for storing an upload exception. */
    public static final String UPLOAD_EXCEPTION = "_upload_exception";

    // -------------------------------------------------------- Public Constructors

    /**
     * Public no-argument constructor.
     */
    public FileUploadService() {
    }

    // -------------------------------------------------------- Public Methods

    /**
     * Create and return a new Commons Upload FileItemFactory instance.
     *
     * @param request the servlet request
     * @return a new Commons FileUpload FileItemFactory instance
     */
    public FileItemFactory createFileItemFactory(HttpServletRequest request) {
        return new DiskFileItemFactory();
    }

    /**
     * Create and return a new Commons Upload FileUploadBase instance.
     *
     * @param request the servlet request
     * @return a new Commons FileUpload FileUploadBase instance
     */
    public FileUploadBase createFileUpload(HttpServletRequest request) {
        FileUploadBase fileUploadBase = new ServletFileUpload();
        return fileUploadBase;
    }

    /**
     * Create and return a new Commons Upload RequestContext instance.
     *
     * @param request the servlet request
     * @return a new Commons FileUpload RequestContext instance
     */
    public RequestContext createRequestContext(HttpServletRequest request) {
        return new ServletRequestContext(request);
    }

    /**
     * Parse the request and extract FileItem instances.
     *
     * @param request the servlet request
     * @return list of FileField instances
     * @throws org.apache.commons.fileupload.FileUploadException if request
     * cannot be parsed
     */
     public List parseRequest(final HttpServletRequest request)
        throws FileUploadException {

        if (request == null) {
            throw new IllegalStateException("Request cannot be null");
        }

        FileItemFactory fileItemFactory = createFileItemFactory(request);
        FileUploadBase fileUploadBase = createFileUpload(request);
        fileUploadBase.setFileItemFactory(fileItemFactory);

        RequestContext requestContext = createRequestContext(request);

        return fileUploadBase.parseRequest(requestContext);
    }
}
