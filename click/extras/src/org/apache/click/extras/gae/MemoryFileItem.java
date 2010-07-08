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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemHeadersSupport;
import org.apache.commons.fileupload.ParameterParser;

/**
 * Provides an In-Memory FileItem implementation which represents a file or
 * form item that was received within a <tt>multipart/form-data</tt> POST
 * request.
 * <p/>
 * This FileItem implementation can safely be used with Google App Engine (GAE)
 * since the file content is not written to disk.
 * <p/>
 * The MemoryFileItem is based on the
 * <a class="external" target="_blank" href="http://commons.apache.org/fileupload/">Commons FileUpload</a>
 * project.
 */
public class MemoryFileItem implements FileItem, FileItemHeadersSupport {
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------- Constants

    /**
     * Default content charset to be used when no explicit charset
     * parameter is provided by the sender. Media subtypes of the
     * "text" type are defined to have a default charset value of
     * "ISO-8859-1" when received via HTTP.
     */
    public static final String DEFAULT_CHARSET = "ISO-8859-1";

    // -------------------------------------------------------------- Variables

    /**
     * The name of the form field as provided by the browser.
     */
    private String fieldName;

    /**
     * The content type passed by the browser, or null if not defined.
     */
    private String contentType;

    /**
     * Whether or not this item is a simple form field.
     */
    private boolean isFormField;

    /**
     * The original filename in the user's filesystem.
     */
    private String fileName;

    /**
     * The file items headers.
     */
    private FileItemHeaders headers;

    /**
     * The file item's uploaded content.
     */
    private ByteArrayOutputStream content;

    // ----------------------------------------------------------- Constructors

    /**
     * Constructs a new MemoryFileItem for the given fieldName, contentType
     * isFormField and fileName parameters.
     *
     * @param fieldName the name of the form field as provided by the browser
     * @param contentType the content type passed by the browser
     * @param isFormField specifies whether or not this item is a simple form field
     * @param fileName the original filename in the user's filesystem
     */
    public MemoryFileItem(String fieldName, String contentType, boolean isFormField,
        String fileName) {

        this.fieldName = fieldName;
        this.contentType = contentType;
        this.isFormField = isFormField;
        this.fileName = fileName;
        this.content = new ByteArrayOutputStream();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * This method does nothing since the file is stored in memory only.
     */
    public void delete() {
    }

    /**
     * Returns the contents of the file item as an array of bytes.
     *
     * @return the contents of the file item as an array of bytes
     */
    public byte[] get() {
        return content.toByteArray();
    }

    /**
     * Returns the content type passed by the browser or null if not defined.
     *
     * @return the content type passed by the browser or null if not defined
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the name of the field in the multipart form corresponding to
     * this file item.
     *
     * @return the name of the form field
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field name used to reference this file item.
     *
     * @param fieldName the name of the form field
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Determines whether or not a <tt>FileItem</tt> instance represents
     * a simple form field.
     *
     * @return true if the instance represents a simple form field; false if it
     * represents an uploaded file
     */
    public boolean isFormField() {
        return isFormField;
    }

    /**
     * Specifies whether or not a <tt>FileItem</tt> instance represents
     * a simple form field.
     *
     * @param isFormField true if the instance represents a simple form field;
     * false if it represents an uploaded file
     */
    public void setFormField(boolean isFormField) {
        this.isFormField = isFormField;
    }

    /**
     * Returns an {@link java.io.InputStream InputStream} that can be used to
     * retrieve the contents of the file.
     *
     * @return an {@link java.io.InputStream InputStream} that can be used to
     * retrieve the contents of the file
     * @throws IOException if an error occurs
     */
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(get());
    }

    /**
     * Returns the original filename in the client's filesystem, as provided by
     * the browser (or other client software). In most cases, this will be the
     * base file name, without path information. However, some clients, such as
     * Internet Explorer, do include path information.
     * <p/>
     * You can easily remove the path information with the following snippet:
     *
     * <pre class="prettyprint">
     * String fileName = fileItem.getName();
     * if (fileName != null) {
     *     filename = FilenameUtils.getName(filename);
     * } </pre>
     *
     * @return the original filename in the client's filesystem.
     */
    public String getName() {
        return fileName;
    }

    /**
     * Returns an {@link java.io.OutputStream OutputStream} that can be used for
     * storing the contents of the file.
     *
     * @return an {@link java.io.OutputStream OutputStream} that can be used
     * for storing the contents of the file
     * @throws IOException if an error occurs
     */
    public OutputStream getOutputStream() throws IOException {
        return content;
    }

    /**
     * Returns the size of the file item, in bytes.
     *
     * @return the size of the file item, in bytes
     */
    public long getSize() {
        return content.size();
    }

    /**
     * Returns the contents of the file as a String, using the default
     * character encoding.  This method uses {@link #get()} to retrieve the
     * contents of the file.
     *
     * @return the contents of the file, as a string.
     */
    public String getString() {
        byte[] rawdata = get();
        String charset = getCharSet();
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        try {
            return new String(rawdata, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(rawdata);
        }
    }

    /**
     * Returns the contents of the file as a String, using the specified
     * encoding. This method uses {@link #get()} to retrieve the contents of the
     * file.
     *
     * @param charset the charset to use
     * @return the contents of the file, as a string
     * @throws UnsupportedEncodingException if the requested character
     * encoding is not available.
     */
    public String getString(final String charset)
        throws UnsupportedEncodingException {
        return new String(get(), charset);
    }

    /**
     * Provides a hint as to whether or not the file contents will be read
     * from memory. This method always returns true.
     *
     * @return true to indicate that the file contents will be read from memory
     */
    public boolean isInMemory() {
        return true;
    }

    /**
     * This method does nothing since the file is stored in memory only.
     *
     * @param file the File into which the uploaded item should be stored
     * @throws Exception if an error occurs
     */
    public void write(File file) throws Exception {
    }

    /**
     * Returns the file item headers.
     *
     * @return the file items headers
     */
    public FileItemHeaders getHeaders() {
        return headers;
    }

    /**
     * Sets the file item headers.
     *
     * @param headers the file items headers
     */
    public void setHeaders(FileItemHeaders headers) {
        this.headers = headers;
    }

    /**
     * Returns the content charset passed by the agent or null if not defined.
     *
     * @return The content charset passed by the agent or null if not defined
     */
    public String getCharSet() {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        // Parameter parser can handle null input
        Map<?, ?> params = parser.parse(getContentType(), ';');
        return (String) params.get("charset");
    }
}
