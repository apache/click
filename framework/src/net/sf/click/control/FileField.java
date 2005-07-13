/*
 * Copyright 2005 Malcolm A. Edgar
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
package net.sf.click.control;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

/**
 * Provides a File Field control: &nbsp; &lt;input type='file'&gt;.
 *
 * <table class='htmlHeader' cellspacing='6'>
 * <tr>
 * <td>File Field</td>
 * <td><input type='file' value='' title='FileField Control'/></td>
 * </tr>
 * </table>
 *
 * The FileField control uses the Jakarta Commons
 * <a href="http://jakarta.apache.org/commons/fileupload/">FileUpload</a>
 * library to provide file processing functionality.  Please see the
 * <a href="http://jakarta.apache.org/commons/fileupload/using.html">Using FileUpload</a>
 * topic for examples.
 * <p/>
 * Also see Internet <a href="http://www.ietf.org/rfc/rfc1867.txt">rfc1867</a>
 * on Form-based File Upload in HTML.
 *
 * @author Malcolm Edgar
 */
public class FileField extends Field {

    // ----------------------------------------------------- Instance Variables

    /** The text field size attribute. The default size is 20. */
    protected int size = 20;

    /** The list of
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileItem.html">FileItem</a>
     * after processing a file upload request.
     */
    protected List fileItems;

    /**
     * The
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileUploadBase.html">FileUploadBase</a>
     * utility, used to process the request.
     */
    protected FileUploadBase fileUpload;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the File Field with the given label.
     * <p/>
     * The field name will be Java property representation of the given label.
     *
     * @param label the label of the Field
     */
    public FileField(String label) {
        super(label);
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the list of <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileItem.html">FileItem</a>
     * after processing the request, or an empty list otherwise.
     *
     * @return the list of <tt>FileItem</tt> after processing a request
     */
    public List getFileItems() {
        if (fileItems != null) {
            return fileItems;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Return the
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileUploadBase.html">FileUploadBase</a>
     * utility used to process the request. This property is lazy loaded. If
     * its is not already set it will initialize to
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileUpload.html">FileUpload</a>
     *
     * @return the file upload utility used to process the request
     */
    public FileUploadBase getFileUpload() {
        if (fileUpload == null) {
            fileUpload = new FileUpload();
        }
        return fileUpload;
    }

    /**
     * Return the
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileUploadBase.html">FileUploadBase</a>
     * utility used to process the request.
     *
     * @param fileUpload the file upload utility used to process the request
     */
    public void setFileUpload(FileUploadBase fileUpload) {
        this.fileUpload = fileUpload;
    }

    /**
     * Return the field size.
     *
     * @return the field size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the field size.
     *
     * @param  size the field size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the input type: '<tt>file</tt>'.
     *
     * @return the input type: '<tt>file</tt>'
     */
    public String getType() {
        return "file";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Processes the FileField submission.
     *
     * @see net.sf.click.Control#onProcess()
     */
    public boolean onProcess() {
        HttpServletRequest request = getContext().getRequest();

        if (FileUploadBase.isMultipartContent(request)) {
            FileUploadBase fileUpload = getFileUpload();

            try {
                fileItems = fileUpload.parseRequest(request);

                return invokeListener();

            } catch (FileUploadException fue) {
                throw new RuntimeException(fue);
            }
        }

        return true;
    }

    /**
     * Return a HTML rendered FileField string.
     *
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(80);

        buffer.append("<input type='");
        buffer.append(getType());
        buffer.append("' name='");
        buffer.append(getName());
        buffer.append("' id='");
        buffer.append(getId());
        buffer.append("' value='");
        buffer.append(getValue());
        buffer.append("' size='");
        buffer.append(getSize());
        buffer.append("'");
        if (getTitle() != null) {
            buffer.append(" title='");
            buffer.append(getTitle());
            buffer.append("'");
        }

        renderAttributes(buffer);

        if (!isValid()) {
            buffer.append(" class='error'");
        } else if (isDisabled()) {
            buffer.append(" class='disabled'");
        }
        buffer.append(getDisabled());
        buffer.append(getReadonly());
        buffer.append(">");

        return buffer.toString();
    }
}
