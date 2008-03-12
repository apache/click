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
package net.sf.click.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.Validate;

/**
 * Provides an application service to parse a multipart file upload requests.
 * This class uses the Apache <a target="blank" href="http://commons.apache.org/fileupload/">Commons FileUpload</a>
 * library internally.
 *
 * <h3>Configuration</h3>
 *
 * The FileUploadService can be configure in the <tt>click.xml</tt> configuration file.
 * In the application config file you can specify the maximim size of any individual file upload in bytes
 * (<tt>fileSizeMax</tt>) and specify the maximum total size of the request in bytes
 * (<tt>sizeMax</tt>). For example:
 *
 * <pre class="codeConfig">
 * &lt;click-app&gt;
 *
 *     &lt;pages package="com.mycorp.page"/&gt;
 *
 *     &lt;file-upload-service&gt;
 *         &lt;property name="<span class="st">fileSizeMax</span>" value="1048576"/&gt;
 *         &lt;property name="<span class="st">sizeMax</span>" value="10485760"/&gt;
 *     &lt;/file-upload-service&gt;
 *
 * &lt;/click-app&gt; </pre>
 *
 * You can also subclass the FileUploadService to provide your own customization.
 * For example you may wish to override the {@link #createFileItemFactory(HttpServletRequest)} method.
 * To specify an alternative class please see the example configuration below:
 *
 * <pre class="codeConfig">
 * &lt;click-app&gt;
 *     &lt;pages package="com.mycorp.page"/&gt;
 *     &lt;file-upload-service classname="<span class="st">com.mycorp.util.AppFileUploadService</span>"&gt;
 *         &lt;property name="fileSizeMax" value="1048576"/&gt;
 *         &lt;property name="sizeMax" value="10485760"/&gt;
 *     &lt;/file-upload-service&gt;
 * &lt;/click-app&gt; </pre>
 *
 * <b>Please note:</b> this class must have a default no-argument constructor.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
public class FileUploadService {

    // -------------------------------------------------------------- Constants

    /** The attribute key used for storing an upload exception. */
    public static final String UPLOAD_EXCEPTION = "_upload_exception";

    /** The total request maximum size in bytes. */
    protected long sizeMax;

    /** The maximum individual size in bytes. */
    protected long fileSizeMax;

    // ----------------------------------------------------- Public Constructor

    /**
     * Public no-argument constructor.
     */
    public FileUploadService() {
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return maximum individual size in bytes.
     *
     * @return the fileSizeMax
     */
    public long getFileSizeMax() {
        return fileSizeMax;
    }

    /**
     * Set the maximum individual size in bytes.
     *
     * @param value the fileSizeMax to set
     */
    public void setFileSizeMax(long value) {
        this.fileSizeMax = value;
    }

    /**
     * Return the total request maximum size in bytes.
     *
     * @return the setSizeMax
     */
    public long getSizeMax() {
        return sizeMax;
    }

    /**
     * Set the total request maximum size in bytes.
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

    /**
     * Parse the request and extract FileItem instances.
     *
     * @param request the servlet request
     * @param fileItems the list of FileItem instances to return, this list cannot be null
     * @throws FileUploadException if request cannot be parsed
     */
     public void parseRequest(final HttpServletRequest request, final List fileItems)
            throws FileUploadException {

        Validate.notNull(request, "Null request parameter");
        Validate.notNull(fileItems, "Null items parameter");

        FileItemFactory fileItemFactory = createFileItemFactory(request);

        ClickFileUpload fileUpload = new ClickFileUpload();
        fileUpload.setFileItemFactory(fileItemFactory);

        if (fileSizeMax > 0) {
            fileUpload.setFileSizeMax(fileSizeMax);
        }
        if (sizeMax > 0) {
            fileUpload.setSizeMax(sizeMax);
        }

        ServletRequestContext requestContext = new ServletRequestContext(request);

        fileUpload.parseRequest(requestContext, fileItems);
    }

     // --------------------------------------------------------- Inner Classes

     /**
      * Provides an ServletFileUpload class with improve error handling.
      */
     static class ClickFileUpload extends ServletFileUpload {

        private void parseRequest(RequestContext requestContext, List items)
            throws FileUploadException {

            Validate.notNull(requestContext, "Null requestContext parameter");

            try {
                FileItemIterator iter = getItemIterator(requestContext);
                FileItemFactory fac = getFileItemFactory();

                if (fac == null) {
                    throw new IllegalStateException("No FileItemFactory has been set.");
                }

                while (iter.hasNext()) {
                    FileItemStream item = iter.next();

                    FileItem fileItem = fac.createItem(item.getFieldName(),
                            item.getContentType(),
                            item.isFormField(),
                            item.getName());

                    try {
                        Streams.copy(item.openStream(), fileItem.getOutputStream(), true);

                    } catch (FileUploadIOException e) {
                        throw (FileUploadException) e.getCause();

                    } catch (IOException e) {
                        throw new IOFileUploadException("Processing of "
                                + MULTIPART_FORM_DATA + " request failed. "
                                + e.getMessage(), e);
                    }

                    items.add(fileItem);
                }

            } catch (FileUploadIOException e) {
                throw (FileUploadException) e.getCause();

            } catch (IOException e) {
                throw new FileUploadException(e.getMessage(), e);
            }
        }
     }
}
