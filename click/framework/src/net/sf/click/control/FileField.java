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

import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
 * library to provide file processing functionality.
 * <p/>
 * Note Browsers enforce the JavaScript <tt>value</tt> property as readonly
 * to prevent script based stealing of users files.
 * <p/>
 * You can make the file field invisible by setting the CSS display attribute, for
 * example:
 *
 * <pre class="codeHtml">
 * &lt;form method="POST" enctype="multipart/form-data"&gt;
 *    &lt;input type="file" name="myfile" <span class='st'>style</span>=<span class='red'>"display:none"</span> onchange="fileName=this.value"&gt;
 *    &lt;input type="button" value="open file" onclick="myfile.click()"&gt;
 *    &lt;input type="button" value="show value" onclick="alert(fileName)"&gt;
 * &lt;/form&gt; </pre>
 *
 * <p/>
 * Please also see the references:
 * <ul>
 * <li><a href="http://jakarta.apache.org/commons/fileupload/using.html">Jakarta Commons - Using FileUpload</a></li>
 * <li><a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867 - Form-based File Upload in HTML</a></li>
 * <li><a href="http://www.cs.tut.fi/~jkorpela/forms/file.html">Jukka Korpela - File input (or "upload") in HTML forms</a></li>
 * <li><a href="https://www.sdn.sap.com/sdn/weblogs.sdn?blog=/pub/wlg/684">SDN - INPUT TYPE="FILE" and your options...</a></li>
 * </ul>
 *
 * @author Malcolm Edgar
 */
public class FileField extends Field {

    private static final long serialVersionUID = 2557325521464484964L;

    // ----------------------------------------------------- Instance Variables

    /** The text field size attribute. The default size is 20. */
    protected int size = 20;

    /** The
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/DefaultFileItem.html">DefaultFileItem</a>
     * after processing a file upload request.
     */
    protected FileItem fileItem;

    /**
     * The
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileUploadBase.html">FileUploadBase</a>
     * utility, used to process the request.
     */
    protected FileUploadBase fileUpload;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct the FileField with the given name.
     *
     * @param name the name of the field
     */
    public FileField(String name) {
        super(name);
    }

    /**
     * Construct the FileField with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */
    public FileField(String name, String label) {
        super(name, label);
    }

    /**
     * Construct the FileField with the given name, label and size.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param size the size of the field
     */
    public FileField(String name, String label, int size) {
        this(name, label);
        setSize(size);
    }

    /**
     * Construct the FileField with the given name and field size.
     *
     * @param name the name of the field
     * @param size the field required status
     */
    public FileField(String name, int size) {
        super(name);
        setSize(size);
    }

    /**
     * Create an FileField with no name defined, <b>please note</b> the
     * control's name must be defined before it is valid.
     * <p/>
     * <div style="border: 1px solid red;padding:0.5em;">
     * No-args constructors are provided for Java Bean tools support and are not
     * intended for general use. If you create a control instance using a
     * no-args constructor you must define its name before adding it to its
     * parent. </div>
     */
    public FileField() {
        super();
    }

    // ------------------------------------------------------ Public Attributes

    /**
     * Return the <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/FileItem.html">FileItem</a>
     * after processing the request, or null otherwise.
     *
     * @return the <tt>FileItem</tt> after processing a request
     */
    public FileItem getFileItem() {
        return fileItem;
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
            FileItemFactory factory = new DiskFileItemFactory();
            fileUpload = new ServletFileUpload(factory);
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
        fileItem = (FileItem) getContext().getMultiPartFormData().get(getName());

        if (!validate()) {
            return true;
        }

        if (fileItem != null) {
            if (isRequired() && fileItem.getSize() == 0) {
                setError(getMessage("file-required-error", getErrorLabel()));

            } else {
                return invokeListener();
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
        HtmlStringBuffer buffer = new HtmlStringBuffer(96);

        buffer.elementStart("input");

        buffer.appendAttribute("type", getType());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("value", getValue());
        buffer.appendAttribute("size", getSize());
        buffer.appendAttribute("title", getTitle());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (isDisabled()) {
            buffer.appendAttributeDisabled();
        }
        if (isReadonly()) {
            buffer.appendAttributeReadonly();
        }
        if (!isValid()) {
            buffer.appendAttribute("class", "error");
        }

        buffer.elementEnd();

        return buffer.toString();
    }
}