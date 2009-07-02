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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

/**
 * Provides a FileItemFactory implementation that creates {@link MemoryFileItem}
 * instances which always keep their content in memory.
 */
public class MemoryFileItemFactory implements FileItemFactory {

    /**
     * Create a new {@link MemoryFileItem} instance from the supplied parameters.
     *
     * @param fieldName the name of the form field
     * @param contentType the content type of the form field
     * @param isFormField true if this is a plain form field, false otherwise
     * @param fileName the name of the uploaded file, if any, as supplied by the
     * browser or other client
     * @return the newly created file item
     */
    public FileItem createItem(String fieldName, String contentType,
        boolean isFormField, String fileName) {

        FileItem result =
            new MemoryFileItem(fieldName, contentType, isFormField, fileName);
        return result;
    }
}
