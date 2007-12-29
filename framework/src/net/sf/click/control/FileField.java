/*
 * Copyright 2006-2007 Malcolm A. Edgar
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

import java.text.MessageFormat;

import net.sf.click.util.HtmlStringBuffer;

import org.apache.commons.fileupload.FileItem;

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

    // -------------------------------------------------------------- Constants

    private static final long serialVersionUID = 1L;

    /**
     * The field validation JavaScript function template.
     * The function template arguments are: <ul>
     * <li>0 - is the field id</li>
     * <li>1 - is the Field required status</li>
     * <li>2 - is the localized error message</li>
     * </ul>
     */
    protected final static String VALIDATE_FILEFIELD_FUNCTION =
        "function validate_{0}() '{'\n"
        + "   var msg = validateFileField(''{0}'',{1}, [''{2}'']);\n"
        + "   if (msg) '{'\n"
        + "      return msg + ''|{0}'';\n"
        + "   '}' else '{'\n"
        + "      return null;\n"
        + "   '}'\n"
        + "'}'\n";

    // ----------------------------------------------------- Instance Variables

    /** The text field size attribute. The default size is 20. */
    protected int size = 20;

    /**
     * The
     * <a href="http://jakarta.apache.org/commons/fileupload/apidocs/org/apache/commons/fileupload/DefaultFileItem.html">DefaultFileItem</a>
     * after processing a file upload request.
     */
    protected FileItem fileItem;

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
     * Construct the FileField with the given name and required status.
     *
     * @param name the name of the field
     * @param required the field required status
     */
    public FileField(String name, boolean required) {
        super(name);
        setRequired(required);
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
     * Construct the FileField with the given name, label and required status.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param required the required status
     */
    public FileField(String name, String label, boolean required) {
        super(name, label);
        setRequired(required);
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
     * Create an FileField with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
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

    /**
     * Return the FileField JavaScript client side validation function.
     *
     * @return the field JavaScript client side validation function
     */
    public String getValidationJavaScript() {
        if (isRequired()) {
            Object[] args = new Object[3];
            args[0] = getId();
            args[1] = String.valueOf(isRequired());
            args[2] = getMessage("file-required-error", getErrorLabel());

            return MessageFormat.format(VALIDATE_FILEFIELD_FUNCTION, args);

        } else {
            return null;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the {@link #fileItem} property from the multi-part form data
     * submission.
     */
    public void bindRequestValue() {
        fileItem = (FileItem) getContext().getFileItem(getName());
    }

    /**
     * Return a HTML rendered FileField string.
     *
     * @return a HTML rendered FileField string
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
        if (getTabIndex() > 0) {
            buffer.appendAttribute("tabindex", getTabIndex());
        }

        appendAttributes(buffer);

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

        if (getHelp() != null) {
            buffer.append(getHelp());
        }

        return buffer.toString();
    }

    /**
     * Validate the FileField request submission.
     * <p/>
     * A field error message is displayed if a validation error occurs.
     * These messages are defined in the resource bundle:
     * <blockquote>
     * <ul>
     *   <li>/click-control.properties
     *     <ul>
     *       <li>file-required-error</li>
     *     </ul>
     *   </li>
     * </ul>
     * </blockquote>
     */
    public void validate() {
        setError(null);

        FileItem fileItem = getFileItem();

        if (fileItem != null) {
            if (isRequired() && fileItem.getSize() == 0) {
                setErrorMessage("file-required-error");
            }
        }
    }

}
