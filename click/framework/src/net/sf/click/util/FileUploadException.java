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
import org.apache.commons.io.FilenameUtils;

/**
 * This exception is thrown by {@link FileUploadService} to indicate that a
 * multipart POST failed.
 *
 * @author Bob Schellink
 */
public class FileUploadException extends Exception {

    // -------------------------------------------------------- Variables

    /** List of fileItems that is already parsed. */
    private List fileItems;

    /** Holds the FileField name which caused the exception. */
    private String fieldName;

    /** Holds the name of the file that caused the exception. */
    private String fileName;

    // -------------------------------------------------------- Public Constructor

    /**
     * Constructs a FileUploadException with the specified exception, fileItem
     * and fileItems list.
     *
     * @param t exception that occurred
     * @param fieldName name of the FileField that caused the exception
     * @param fileName name of the file that caused the exception
     * @param fileItems list of fileItems that is already parsed
     */
    public FileUploadException(Throwable t, String fieldName, String fileName,
        List fileItems) {
        super(t);
        this.fieldName = fieldName;
        this.fileName = FilenameUtils.getName(fieldName);        
        this.fileItems = fileItems;
    }

    // -------------------------------------------------------- Public getters and setters

    /**
     * Return the name of the file that caused the exception.
     *
     * @return the name of the file that caused the exception.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Return the name of the {@link net.sf.click.control.FileField} that caused
     * the exception.
     *
     * @return the name of the FileField that caused the exception.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Return the list of fileItems that is already parsed.
     *
     * @return the list of fileItems that is already parsed.
     */
    public List getFileItems() {
        return fileItems;
    }

    /**
     * Set the list of fileItems to the specified list.
     *
     * @param fileItems set the list of fileItems to the specified list
     */
    public void setFileItems(List fileItems) {
        this.fileItems = fileItems;
    }
}

