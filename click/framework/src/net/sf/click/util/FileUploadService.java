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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

//TODO available form FileUpload 1.2.1
//import org.apache.commons.fileupload.FileItemHeaders;
//import org.apache.commons.fileupload.FileItemHeadersSupport;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.FileUploadIOException;
import org.apache.commons.fileupload.FileUploadBase.IOFileUploadException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.apache.commons.fileupload.util.Streams;

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
     * Parse the request and extract FileItem instances.
     *
     * @param request the servlet request
     * @return list of FileField instances
     * @throws FileUploadException if request cannot be parsed
     */
    public final List parseRequest(final HttpServletRequest request)
        throws FileUploadException {

        if (request == null) {
            throw new IllegalStateException("Request cannot be null");
        }

        FileItemFactory fileItemFactory = createFileItemFactory(request);
        FileUploadBase fileUploadBase = createFileUpload(request);
        fileUploadBase.setFileItemFactory(fileItemFactory);

        long sizeMax = fileUploadBase.getSizeMax();

        RequestContext requestContext = createRequestContext(request,
            fileUploadBase.getSizeMax());

        // Reset sizeMax to -1 so that exceptions won't be thrown. Instead
        // exceptions will handled manually.
        fileUploadBase.setSizeMax(-1L);

        // Indicates whether a request maximum size has been exceeded.
        boolean isSizeMaxExceeded = false;

        long requestSize = request.getContentLength();

        try {

            if (requestSize >= 0) {
                isSizeMaxExceeded = (sizeMax >= 0 && requestSize > sizeMax);
            }

            FileItemIterator iter = fileUploadBase.getItemIterator(requestContext);
            List items = new ArrayList();
            FileItemFactory fac = fileUploadBase.getFileItemFactory();
            if (fac == null) {
                throw new NullPointerException(
                    "No FileItemFactory has been set.");
            }

            FileItem fileItem = null;

            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                fileItem = fac.createItem(item.getFieldName(),
                    item.getContentType(), item.isFormField(),
                    item.getName());
                try {

                    // Always extract the parameter if it is a form field.
                    if (!isSizeMaxExceeded || fileItem.isFormField()) {
                        Streams.copy(item.openStream(),
                            fileItem.getOutputStream(), true);

                        //TODO available form FileUpload 1.2.1
                        /*
                        if (fileItem instanceof FileItemHeadersSupport) {
                            final FileItemHeaders fih = item.getHeaders();
                            ((FileItemHeadersSupport) fileItem).setHeaders(fih);
                        }*/
                        items.add(fileItem);
                    } else {
                        // If the request size was exceeded and current fileItem
                        // is a file upload field, throw exception.
                        Exception e = new SizeLimitExceededException(
                            "the request was rejected because its size ("
                            + requestSize + ") exceeds the configured maximum ("
                            + sizeMax + ")", requestSize, sizeMax);

                        throw new FileUploadException(e,
                            fileItem.getFieldName(), fileItem.getName(), items);
                    }
                } catch (FileUploadIOException e) {

                    if (e.getCause() instanceof FileSizeLimitExceededException
                        || e.getCause() instanceof SizeLimitExceededException) {
                        FileUploadException exception = new FileUploadException(
                            e.getCause(), fileItem.getFieldName(),
                            fileItem.getName(), items);
                        throw exception;

                    } else {
                        throw e.getCause();
                    }

                } catch (IOException e) {
                    throw new IOFileUploadException(
                        "Processing of " + FileUploadBase.MULTIPART_FORM_DATA
                        + " request failed. " + e.getMessage(), e);
                }
            }

            return items;
        } catch (FileUploadIOException e) {

            // FileLoadIOException must be unwrapped to get the cause.
            throw new RuntimeException((FileUploadException) e.getCause());
        } catch (FileUploadException e) {

            // Let Click custom exception continue up the stack.
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    final RequestContext createRequestContext(final HttpServletRequest request,
        long sizeMax) {

        RequestContext requestContext = new CustomServletRequestContext(request,
            sizeMax);

        return requestContext;
    }

    /**
     * Custom ServletRequestContext that caters for situation where
     * request#getContentLength is not set.
     */
    class CustomServletRequestContext extends ServletRequestContext {

        final long sizeMax;

        final long requestSize;

        public CustomServletRequestContext(HttpServletRequest request,
            long sizeMax) {
            super(request);
            this.sizeMax = sizeMax;
            this.requestSize = request.getContentLength();
        }

        public InputStream getInputStream() throws IOException {
            // If sizeMax is set and contentLength is not set, wrap the
            // InputStream and limit the size of the stream to the specified
            // sizeMax
            if (sizeMax >= 0 && requestSize == -1) {
                return new LimitedInputStream(super.getInputStream(), sizeMax) {
                    protected void raiseError(long pSizeMax, long pCount)
                        throws IOException {
                        String msg = "the request was rejected because"
                            + " its size (" + pCount
                            + ") exceeds the configured maximum"
                            + " (" + pSizeMax + ")";
                        SizeLimitExceededException ex =
                            new SizeLimitExceededException(msg, pCount, pSizeMax);
                        throw new FileUploadIOException(ex);
                    }
                };
            } else {
                return super.getInputStream();
            }
        }
    }
}
