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
package org.apache.click.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

/**
 * Mock implementation of <tt>org.apache.commons.fileupload.FileItem</tt>.
 */
public class MockFileItem implements FileItem {

    private static final long serialVersionUID = 1L;

    public void delete() {
    }

    public byte[] get() {
        return null;
    }

    public String getContentType() {
        return null;
    }

    public String getFieldName() {
        return null;
    }

    public InputStream getInputStream() throws IOException {
        return null;
    }

    public String getName() {
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    public long getSize() {
        return 0;
    }

    public String getString() {
        return null;
    }

    public String getString(String arg0) throws UnsupportedEncodingException {
        return null;
    }

    public boolean isFormField() {
        return false;
    }

    public boolean isInMemory() {
        return false;
    }

    public void setFieldName(String arg0) {
    }

    public void setFormField(boolean arg0) {
    }

    public void write(File arg0) throws Exception {
    }

}
