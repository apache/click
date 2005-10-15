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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.click.control.DateField;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Label;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

/**
 * Provides miscellaneous Form, String and Stream utility methods.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class ClickUtils {

    /**
     * Close the given output stream and ignore any exceptions thrown.
     *
     * @param stream the output stream to close.
     */
    public static void close(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                // Ignore.
            }
        }
    }

    /**
     * Close the given input stream and ignore any exceptions thrown.
     *
     * @param stream the input stream to close.
     */
    public static void close(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                // Ignore.
            }
        }
    }

    /**
     * Popuplate the given object's attributes with the Form's field values.
     *
     * @param form the Form to obtain field values from
     * @param object the object to populate with field values
     * @param debug log debug statements when populating the object
     */
    public static void copyFormToObject(Form form, Object object, boolean debug) {
        if (form == null) {
            throw new IllegalArgumentException("Null form parameter");
        }
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        Method[] methods = object.getClass().getMethods();

        final List fieldList = getFormFields(form);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (field.getName().equals(Form.FORM_NAME)) {
                continue;
            }

            String setterName = toSetterName(field.getName());

            Method method = null;
            for (int j = 0; j < methods.length; j++) {
                if (setterName.equals(methods[j].getName())) {
                    method = methods[j];
                    break;
                }
            }

            if (method != null) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    Class paramClass = parameterTypes[0];
                    Object paramObject = null;

                    if (paramClass == String.class) {
                        paramObject = field.getValue();

                    } else if (paramClass == Integer.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Integer.valueOf(field.getValue());
                        }
                    } else if (paramClass == Boolean.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Boolean.valueOf(field.getValue());
                        }
                    } else if (paramClass == Double.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Double.valueOf(field.getValue());
                        }
                    } else if (paramClass == Float.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Float.valueOf(field.getValue());
                        }
                    } else if (paramClass == Long.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Long.valueOf(field.getValue());
                        }
                    } else if (paramClass == Short.class) {
                        if (!StringUtils.isBlank(field.getValue())) {
                            paramObject = Short.valueOf(field.getValue());
                        }
                    } else if (paramClass == java.util.Date.class) {
                        if (field instanceof DateField) {
                            paramObject = ((DateField) field).getDate();
                        }
                    } else if (paramClass == java.sql.Date.class) {
                        if (field instanceof DateField) {
                            Date date = ((DateField) field).getDate();
                            if (date != null) {
                                paramObject =
                                    new java.sql.Date(date.getTime());
                            }
                        }
                    } else if (paramClass == java.sql.Time.class) {
                        if (field instanceof DateField) {
                            Date date = ((DateField) field).getDate();
                            if (date != null) {
                                paramObject =
                                    new java.sql.Time(date.getTime());
                            }
                        }
                    } else if (paramClass == java.sql.Timestamp.class) {
                        if (field instanceof DateField) {
                            Date date = ((DateField) field).getDate();
                            if (date != null) {
                                paramObject =
                                    new java.sql.Timestamp(date.getTime());
                            }
                        }
                    }

                    Object[] params = { paramObject };

                    try {
                        method.invoke(object, params);

                        if (debug) {
                            String msg =
                                "[Click] [debug] Form -> " + objectClassname +
                                "." + method.getName() + " : " +
                                paramObject;
                            System.out.println(msg);
                        }

                    } catch (Exception e) {
                        if (debug) {
                            String msg =
                                "[Click] [debug] Error incurred invoking " +
                                objectClassname + "." + method.getName() +
                                "() with " + paramObject + " error: " +
                                e.toString();
                            System.out.println(msg);
                        }
                    }

                } else {
                    if (debug) {
                        String msg =
                            "[Click] [debug] " +
                            objectClassname + "." + method.getName() +
                            "() method has invalid number of parameters";
                        System.out.println(msg);
                    }
                }
            } else {
                if (debug) {
                    String msg =
                        "[Click] [debug] " + objectClassname + "." +
                        setterName + "() method not found";
                    System.out.println(msg);
                }
            }
        }
    }

    /**
     * Popuplate the given Form field values with the object's attributes.
     *
     * @param object the object to obtain attribute values from
     * @param form the Form to populate
     * @param debug log debug statements when populating the form
     */
    public static void copyObjectToForm(Object object, Form form, boolean debug) {
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }
        if (form == null) {
            throw new IllegalArgumentException("Null form parameter");
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        Method[] methods = object.getClass().getMethods();

        final List fieldList = getFormFields(form);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (field.getName().equals(Form.FORM_NAME)) {
                continue;
            }

            String getterName = toGetterName(field.getName());
            String isGetterName = toIsGetterName(field.getName());

            Method method = null;
            for (int j = 0; j < methods.length; j++) {
                if (getterName.equals(methods[j].getName())) {
                    method = methods[j];
                    break;
                }
                if (isGetterName.equals(methods[j].getName())) {
                    method = methods[j];
                    break;
                }
            }

            if (method != null) {
                // call method
                try {
                    Object result = method.invoke(object, null);

                    if (debug) {
                        String msg =
                            "[Click] [debug] Form <- " + objectClassname +
                            "." + method.getName() + " : " + result;
                        System.out.println(msg);
                    }

                    if (result != null) {
                        if (field instanceof DateField &&
                            result instanceof Date) {

                            DateField dateField = (DateField) field;
                            dateField.setDate((Date) result);

                        } else if (field instanceof HiddenField) {
                            field.setValue(result);

                        } else {
                            field.setValue(result.toString());
                        }
                    }

                } catch (Exception e) {
                    if (debug) {
                        String msg =
                            "[Click] [debug] Error incurred invoking " +
                            objectClassname + "." + method.getName() +
                            "() error: " + e.toString();
                        System.out.println(msg);
                    }
                }

            } else {
                if (debug) {
                    String msg =
                        "[Click] [debug] " + objectClassname + "." +
                        getterName + "() method not found";
                    System.out.println(msg);
                }
            }
        }
    }

    /**
     * Return an encoded version of the <tt>Serializble</tt> object. The object
     * will be serialized, compressed and Base 64 encoded.
     *
     * @param object the object to encode
     * @return a serialized, compressed and Base 64 string encoding of the
     * given object
     * @throws IOException if an I/O error occurs
     * @throws IllegalArgumentException if the object parameter is null, or if
     *      the object is not Serializable
     */
    public static String encode(Object object) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("null object parameter");
        }
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("parameter not Serializable");
        }

        ByteArrayOutputStream bos = null;
        GZIPOutputStream gos = null;
        ObjectOutputStream oos = null;

        try {
            bos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(bos);
            oos = new ObjectOutputStream(gos);

            oos.writeObject(object);
            oos.close();
        } finally {
            close(oos);
            close(gos);
            close(bos);
        }

        Base64 base64 = new Base64();

        try {
            byte[] byteData = base64.encode(bos.toByteArray());

            return new String(byteData);

        } catch (Throwable t) {
            String message =
                "error occured Base64 encoding: " + object + " : " + t;
            throw new IOException(message);
        }
    }

    /**
     * Return an object from the {@link #encode(Object)} string.
     *
     * @param string the encoded string
     * @return an object from the encoded
     * @throws ClassNotFoundException if the class could not be instantiated
     * @throws IOException if an data I/O error occurs
     */
    public static Object decode(String string)
            throws ClassNotFoundException, IOException {

        Base64 base64 = new Base64();
        byte[] byteData = null;

        try {
            byteData = base64.decode(string.getBytes());

        } catch (Throwable t) {
            String message =
                "error occured Base64 decoding: " + string + " : " + t;
            throw new IOException(message);
        }

        ByteArrayInputStream bis = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(byteData);
            gis = new GZIPInputStream(bis);
            ois = new ObjectInputStream(gis);

            return ois.readObject();

        } finally {
            close(ois);
            close(gis);
            close(bis);
        }
    }

    /**
     * Invoke the named method on the given object and return the boolean result.
     *
     * @see net.sf.click.Control#setListener(Object, String)
     *
     * @param listener the object with the method to invoke
     * @param method the name of the method to invoke
     * @return true if the listener method returned true
     */
    public static boolean invokeListener(Object listener, String method) {
        if (listener == null) {
            throw new IllegalArgumentException("Null listener parameter");
        }
        if (method == null) {
            throw new IllegalArgumentException("Null method parameter");
        }

        Method targetMethod = null;
        try {
            targetMethod = listener.getClass().getMethod(method, null);

            Object result = targetMethod.invoke(listener, null);

            if (result instanceof Boolean) {
                return ((Boolean)result).booleanValue();

            } else {
                String msg =
                    "Invalid listener method, missing boolean return type: " +
                    targetMethod;
                throw new RuntimeException(msg);
            }

        } catch (InvocationTargetException ite) {

            Throwable e = ite.getTargetException();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;

            } else if (e instanceof Exception) {
                String msg =
                    "Exception occured invoking public method: " + targetMethod;

                throw new RuntimeException(msg, e);

            } else if (e instanceof Error) {
                String msg =
                    "Error occured invoking public method: " + targetMethod;

                throw new RuntimeException(msg, e);

            } else {
                String msg =
                    "Error occured invoking public method: " + targetMethod;

                throw new RuntimeException(msg, e);
            }

        } catch (Exception e) {
            String msg =
                "Exception occured invoking public method: " + targetMethod;

            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Render the given Map of HTML attributes to the StringBuffer, except for
     * the attribute "id".
     *
     * @param attributes the Map of HTML attributes
     * @param buffer the StringBuffer to render to
     */
    public static void renderAttributes(Map attributes, StringBuffer buffer) {
        if (attributes != null) {
            for (Iterator i = attributes.keySet().iterator(); i.hasNext();) {
                String name = i.next().toString();
                if (!name.equals("id")) {
                    String value = attributes.get(name).toString();
                    buffer.append(" ");
                    buffer.append(name);
                    buffer.append("='");
                    buffer.append(value);
                    buffer.append("'");
                }
            }
        }
    }

    /**
     * Return the list of Field for the given Form, including the any Fields
     * contained in FieldSets. The list of returned fields will exclude any
     * <tt>FieldSet</tt> or <tt>Label</tt> fields.
     *
     * @param form the form to obtain the fields from
     * @return the list of contained form fields
     */
    public static List getFormFields(Form form) {
        if (form == null) {
            throw new IllegalArgumentException("Null form parameter");
        }

        List fieldList = new ArrayList();

        for (int i = 0; i < form.getFieldList().size(); i++) {
            Field field = (Field) form.getFieldList().get(i);

            if (field instanceof FieldSet) {
                FieldSet fieldSet = (FieldSet) field;
                for (int j = 0; j < fieldSet.getFieldList().size(); j++) {
                    Field fieldSetField = (Field) fieldSet.getFieldList().get(j);
                    if (!(fieldSetField instanceof Label)) {
                        fieldList.add(fieldSetField);
                    }
                }

            } else if (!(field instanceof Label)) {
                fieldList.add(field);
            }
        }

        return fieldList;
    }

    /**
     * Return the page resouce path from the request. For example:
     * <pre class="codeHtml">
     * <span class="blue">http://www.mycorp.com/banking/secure/login.htm</span>  ->  <span class="red">/secure/login.htm</span> </pre>
     *
     * @return the page resource path from the request
     */
    public static String getResourcePath(HttpServletRequest request) {
        // Adapted from VelocityViewServlet.handleRequest() method:

        // If we get here from RequestDispatcher.include(), getServletPath()
        // will return the original (wrong) URI requested.  The following special
        // attribute holds the correct path.  See section 8.3 of the Servlet
        // 2.3 specification.

        String path = (String) request.getAttribute("javax.servlet.include.servlet_path");

        // Also take into account the PathInfo stated on SRV.4.4 Request Path Elements.
        String info = (String) request.getAttribute("javax.servlet.include.path_info");

        if (path == null) {
            path = request.getServletPath();
            info = request.getPathInfo();
        }

        if (info != null) {
            path += info;
        }

        return path;
    }


    /**
     * Return the getter method name for the given property name.
     *
     * @param property the property name
     * @return the getter method name for the given property name.
     */
    public static String toGetterName(String property) {
        StringBuffer buffer = new StringBuffer(property.length() + 3);

        buffer.append("get");
        buffer.append(Character.toUpperCase(property.charAt(0)));
        buffer.append(property.substring(1));

        return buffer.toString();
    }

    /**
     * Return the is getter method name for the given property name.
     *
     * @param property the property name
     * @return the is getter method name for the given property name.
     */
    public static String toIsGetterName(String property) {
        StringBuffer buffer = new StringBuffer(property.length() + 3);

        buffer.append("is");
        buffer.append(Character.toUpperCase(property.charAt(0)));
        buffer.append(property.substring(1));

        return buffer.toString();
    }

    /**
     * Return a field name string from the given field label.
     * <p/>
     * A label of " OK do it!" is returned as "okDoIt". Any &amp;nbsp;
     * characters will also be removed.
     * <p/>
     * A label of "customerSelect" is returned as "customerSelect".
     *
     * @param label the field label or caption
     * @return a field name string from the given field label
     */
    public static String toName(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Null label parameter");
        }

        boolean doneFirstLetter = false;
        boolean lastCharBlank = false;
        boolean hasWhiteSpace = (label.indexOf(' ') != -1);

        StringBuffer buffer = new StringBuffer(label.length());
        for (int i = 0, size = label.length(); i < size; i++) {
            char aChar = label.charAt(i);

            if (aChar != ' ') {
                if (Character.isJavaIdentifierPart(aChar)) {
                    if (lastCharBlank) {
                        if (doneFirstLetter) {
                            buffer.append(Character.toUpperCase(aChar));
                            lastCharBlank = false;
                        } else {
                            buffer.append(Character.toLowerCase(aChar));
                            lastCharBlank = false;
                            doneFirstLetter = true;
                        }
                    } else {
                        if (doneFirstLetter) {
                            if (hasWhiteSpace) {
                                buffer.append(Character.toLowerCase(aChar));
                            } else {
                                buffer.append(aChar);
                            }
                        } else {
                            buffer.append(Character.toLowerCase(aChar));
                            doneFirstLetter = true;
                        }
                    }
                }
            } else {
                lastCharBlank = true;
            }
        }

        return buffer.toString();
    }

    /**
     * Return the setter method name for the given property name.
     *
     * @param property the property name
     * @return the setter method name for the given property name.
     */
    public static String toSetterName(String property) {
        StringBuffer buffer = new StringBuffer(property.length() + 3);

        buffer.append("set");
        buffer.append(Character.toUpperCase(property.charAt(0)));
        buffer.append(property.substring(1));

        return buffer.toString();
    }

    /**
     * Return a new XML Document for the given input stream.
     *
     * @param inputStream the file input stream
     * @return new XML Document
     * @throws RuntimeException if a parsing error occurs
     */
    public static Document buildDocument(InputStream inputStream) {
        return buildDocument(inputStream, null);
    }

    /**
     * Return a new XML Document for the given input stream and XML entity
     * resolver.
     *
     * @param inputStream the file input stream
     * @param entityResolver the XML entity resolver
     * @return new XML Document
     * @throws RuntimeException if a parsing error occurs
     */
    public static Document buildDocument(InputStream inputStream,
                                         EntityResolver entityResolver) {
         try {
             DocumentBuilderFactory factory =
                 DocumentBuilderFactory.newInstance();

             DocumentBuilder builder = factory.newDocumentBuilder();

             if (entityResolver != null) {
                 builder.setEntityResolver(entityResolver);
             }

             return builder.parse(inputStream);

         } catch (Exception ex) {
             throw new RuntimeException("Error parsing XML", ex);
         }
    }
}
