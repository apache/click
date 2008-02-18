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
package net.sf.click;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sf.click.util.ClickUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * Provides a custom HttpServletRequest class for shielding users from
 * multipart request parameters. Thus calling request.getParameter(String)
 * will still work properly.
 *
 * @author Bob Schellink
 * @author Malcolm Edgar
 */
class ClickRequestWrapper extends HttpServletRequestWrapper {

    /**
     * The <tt>FileItem</tt> objects for <tt>"multipart"</tt> POST requests.
     */
    private final Map fileItemMap;

    /** The request is a multi-part file upload POST request. */
    private final boolean isMultipartRequest;

    /** The wrapped servlet request. */
    private final HttpServletRequest request;

    /** The map of request parameter values. */
    private final Map requestParameterMap;

    // ----------------------------------------------------------- Constructors

    /**
     * @see HttpServletRequestWrapper(HttpServletRequest)
     */
    ClickRequestWrapper(HttpServletRequest request, FileItemFactory fileItemFactory) {
        super(request);

        this.isMultipartRequest = ClickUtils.isMultipartRequest(request);
        this.request = request;

        if (isMultipartRequest) {
            // If this request is multipart, populate two maps, one for normal
            // request parameters, the other for all uploaded files
            FileUploadBase fileUpload = new ServletFileUpload(fileItemFactory);

            Map requestParams = new HashMap();
            Map fileItems = new HashMap();

            try {
                ServletRequestContext srvContext =
                    new ServletRequestContext(request);

                List itemsList = fileUpload.parseRequest(srvContext);

                for (int i = 0; i < itemsList.size(); i++) {
                    FileItem fileItem = (FileItem) itemsList.get(i);

                    String name = fileItem.getFieldName();
                    String value = null;

                    //Form fields are placed in the request parameter map,
                    //while file uploads are placed in the file item map.
                    if (fileItem.isFormField()) {

                        if (request.getCharacterEncoding() == null) {
                            value = fileItem.getString();

                        } else {
                            try {
                                value = fileItem.getString(request.getCharacterEncoding());

                            } catch (UnsupportedEncodingException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        //Add the form field value to the parameters
                        addToMapAsString(requestParams, name, value);

                    } else {
                        //Add the file item to the list of file items
                        addToMapAsFileItem(fileItems, name, fileItem);
                    }
                }

                fileItemMap = Collections.unmodifiableMap(fileItems);
                requestParameterMap = Collections.unmodifiableMap(requestParams);

            } catch (FileUploadException fue) {
                throw new RuntimeException(fue);
            }

        } else {
            fileItemMap = Collections.EMPTY_MAP;
            requestParameterMap = Collections.EMPTY_MAP;
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Returns a map of <tt>FileItem arrays</tt> keyed on request parameter
     * name for "multipart" POST requests (file uploads). Thus each map entry
     * will consist of one or more <tt>FileItem</tt> objects.
     *
     * @return map of <tt>FileItem arrays</tt> keyed on request parameter name
     * for "multipart" POST requests
     */
    public Map getFileItemMap() {
        return fileItemMap;
    }

    /**
     * @see javax.servlet.ServletRequest#getParameter(String)
     */
    public String getParameter(String name) {
        if (isMultipartRequest) {
            Object value = requestParameterMap.get(name);

            if (value instanceof String) {
                return (String) value;
            }

            if (value instanceof String[]) {
                String[] array = (String[]) value;
                if (array.length >= 1) {
                    return array[0];
                } else {
                    return null;
                }
            }

            return (value == null ? null : value.toString());

        } else {
            return request.getParameter(name);
        }
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    public Enumeration getParameterNames() {
        if (isMultipartRequest) {
            return Collections.enumeration(requestParameterMap.keySet());

        } else {
            return request.getParameterNames();
        }
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterValues(String)
     */
    public String[] getParameterValues(String name) {
        if (isMultipartRequest) {
            Object values = requestParameterMap.get(name);
            if (values instanceof String) {
                return new String[] { values.toString() };
            }
            if (values instanceof String[]) {
                return (String[]) values;
            } else {
                return null;
            }

        } else {
            return request.getParameterValues(name);
        }
    }

    /**
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    public Map getParameterMap() {
        if (isMultipartRequest) {
            return requestParameterMap;
        } else {
            return request.getParameterMap();
        }
    }

    // -------------------------------------------------------- Private methods

    /**
     * Stores the specified value in a FileItem array in the map, under the
     * specified name. Thus two values stored under the same name will be
     * stored in the same array.
     *
     * @param map the map to add the specified name and value to
     * @param name the name of the map key
     * @param value the value to add to the FileItem array
     */
    private void addToMapAsFileItem(Map map, String name, FileItem value) {
        FileItem[] oldValues = (FileItem[]) map.get(name);
        FileItem[] newValues = null;
        if (oldValues == null) {
            newValues = new FileItem[] {value};
        } else {
            newValues = new FileItem[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    /**
     * Stores the specified value in an String array in the map, under the
     * specified name. Thus two values stored under the same name will be
     * stored in the same array.
     *
     * @param map the map to add the specified name and value to
     * @param name the name of the map key
     * @param value the value to add to the string array
     */
    private void addToMapAsString(Map map, String name, String value) {
        String[] oldValues = (String[]) map.get(name);
        String[] newValues = null;
        if (oldValues == null) {
            newValues = new String[] {value};
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

}
