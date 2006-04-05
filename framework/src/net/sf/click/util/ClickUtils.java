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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.click.Context;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import ognl.Ognl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

/**
 * Provides miscellaneous Form, String and Stream utility methods.
 *
 * @author Malcolm Edgar
 */
public class ClickUtils {

    /** Hexidecimal characters for MD5 encoding. */
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    // --------------------------------------------------------- Public Methods

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
    public static void copyFormToObject(Form form, Object object,
            boolean debug) {

        if (form == null) {
            throw new IllegalArgumentException("Null form parameter");
        }
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        final List fieldList = getFormFields(form);

        if (fieldList.isEmpty()) {
            log("Form has no fields to copy from", debug);
        }

        Set properties = getObjectPropertyNames(object);
        Map ognlContext = new HashMap();

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (!hasMatchingProperty(field, properties)) {
                continue;
            }

            ensureObjectPathNotNull(object, field.getName());

            try {
                Ognl.setValue(field.getName(),
                              ognlContext,
                              object,
                              field.getValueObject());

                String msg = "Form -> " + objectClassname + "."
                             + field.getName() + " : " + field.getValueObject();

                log(msg, debug);

            } catch (Exception e) {
                String msg =
                    "Error incurred invoking " + objectClassname + "."
                    + field.getName() + " with " + field.getValueObject()
                    + " error: " + e.toString();

                log(msg, debug);
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
    public static void copyObjectToForm(Object object, Form form,
            boolean debug) {

        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }
        if (form == null) {
            throw new IllegalArgumentException("Null form parameter");
        }

        final List fieldList = getFormFields(form);

        if (fieldList.isEmpty()) {
            log("Form has no fields to copy to", debug);
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        Set properties = getObjectPropertyNames(object);
        Map ognlContext = new HashMap();

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (!hasMatchingProperty(field, properties)) {
                continue;
            }

            try {
                Object result =
                    Ognl.getValue(field.getName(), ognlContext, object);

                field.setValueObject(result);

                String msg = "Form <- " + objectClassname + "."
                             + field.getName() + " : " + result;
                log(msg, debug);

            } catch (Exception e) {
                String msg = "Error incurred invoking " + objectClassname + "."
                             + field.getName() + " error: " + e.toString();

                log(msg, debug);
            }
        }
    }

    /**
     * Deploy the specified classpath resource to the given target directory
     * under the web application root directory.
     *
     * @param servletContext the web applications servlet context
     * @param resource the classpath resource name
     * @param targetDir the target directory to deploy the resource to
     * @throws IOException if an I/O error occurs
     */
    public static void deployFile(ServletContext servletContext,
        String resource, String targetDir) throws IOException {

        if (servletContext == null) {
            throw new IllegalArgumentException("Null servletContext parameter");
        }

        if (StringUtils.isBlank(resource)) {
            String msg = "Null resource parameter not defined";
            throw new IllegalArgumentException(msg);
        }

        String realTargetDir = servletContext.getRealPath("/") + File.separator;

        if (StringUtils.isNotBlank(targetDir)) {
            realTargetDir = realTargetDir + targetDir;
        }

        // Create files deployment directory
        File directory = new File(realTargetDir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                String msg =
                    "could not create deployment directory: " + directory;
                throw new IOException(msg);
            }
        }

        String destination = resource;
        int index = resource.lastIndexOf('/');
        if (index != -1) {
            destination = resource.substring(index + 1);
        }
        destination = realTargetDir + File.separator + destination;

        File destinationFile = new File(destination);

        if (!destinationFile.exists()) {
            InputStream inputStream =
                ClickUtils.class.getResourceAsStream(resource);

            if (inputStream != null) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(destinationFile);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int length = inputStream.read(buffer);
                        if (length <  0) {
                            break;
                        }
                        fos.write(buffer, 0, length);
                    }

                    ClickLogger logger = ClickLogger.getInstance();
                    if (logger.isTraceEnabled()) {
                        int lastIndex =
                            destination.lastIndexOf(File.separatorChar);
                        if (lastIndex != -1) {
                            destination = destination.substring(lastIndex + 1);
                        }
                        String msg =
                            "deployed " + targetDir + File.separator
                            + destination;
                        logger.trace(msg);
                    }

                } finally {
                    close(fos);
                    close(inputStream);
                }
            } else {
                String msg = "could not locate classpath resource: " + resource;
                throw new IOException(msg);
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
     * Return an encoded URL value for the given object using the context
     * request character encoding. This method uses
     * {@link URLEncoder#encode(java.lang.String, java.lang.String)}
     * internally.
     *
     * @param object the object value to encode as a URL string
     * @param context the context providing the request character encoding
     * @return an encoded URL string
     */
    public static String encodeUrl(Object object, Context context) {
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }
        if (context == null) {
            throw new IllegalArgumentException("Null context parameter");
        }

        String charset = context.getRequest().getCharacterEncoding();

        if (charset == null) {
            charset = "UTF-8";
        }

        try {
            return URLEncoder.encode(object.toString(), charset);

        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
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
        boolean isAccessible = true;
        try {
            Class listenerClass = listener.getClass();
            targetMethod = listenerClass.getMethod(method, null);

            // Change accessible for annonymous inner classes public methods
            // only. Conditional checks:
            // #1 - Target method is not accessible
            // #2 - Annonomous inner classes are not public
            // #3 - Only modify public methods
            // #4 - Annonomous inner classes have no declaring class
            // #5 - Annonomous inner classes have $ in name
            if (!targetMethod.isAccessible()
                && !Modifier.isPublic(listenerClass.getModifiers())
                && Modifier.isPublic(targetMethod.getModifiers())
                && listenerClass.getDeclaringClass() == null
                && listenerClass.getName().indexOf('$') != -1) {

                isAccessible = false;
                targetMethod.setAccessible(true);
            }


            Object result = targetMethod.invoke(listener, null);

            if (result instanceof Boolean) {
                return ((Boolean) result).booleanValue();

            } else {
                String msg =
                    "Invalid listener method, missing boolean return type: "
                    + targetMethod;
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

        } finally {
            if (targetMethod != null && !isAccessible) {
                targetMethod.setAccessible(false);
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
                    Field fieldSetField =
                        (Field) fieldSet.getFieldList().get(j);

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
     * @param request the page servlet request
     * @return the page resource path from the request
     */
    public static String getResourcePath(HttpServletRequest request) {
        // Adapted from VelocityViewServlet.handleRequest() method:

        // If we get here from RequestDispatcher.include(), getServletPath()
        // will return the original (wrong) URI requested.  The following
        // special attribute holds the correct path.  See section 8.3 of the
        // Servlet 2.3 specification.

        String path = (String)
            request.getAttribute("javax.servlet.include.servlet_path");

        // Also take into account the PathInfo stated on
        // SRV.4.4 Request Path Elements.
        String info = (String)
            request.getAttribute("javax.servlet.include.path_info");

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
     * Return a field label string from the given field name. For exampe:
     * <pre class="codeHtml">
     * <span class="blue">faxNumber</span> &nbsp; -&gt; &nbsp; <span class="red">Fax Number</span> </pre>
     *
     * @param name the field name
     * @return a field label string from the given field name
     */
    public static String toLabel(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null name parameter");
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0, size = name.length(); i < size; i++) {
            char aChar = name.charAt(i);

            if (i == 0) {
                buffer.append(Character.toUpperCase(aChar));

            } else {
                buffer.append(aChar);

                if (i < name.length() - 1) {
                    char nextChar = name.charAt(i + 1);
                    if (Character.isLowerCase(aChar)
                        && Character.isUpperCase(nextChar)) {
                       buffer.append(" ");
                    }
                }
            }
        }

        return buffer.toString();
    }

    /**
     * Return an 32 char MD5 encoded string from the given plain text.
     * The returned value is MD5 hash compatible with Tomcat catalina Realm.
     * <p/>
     * Adapted from <tt>org.apache.catalina.util.MD5Encoder</tt>
     *
     * @param plaintext the plain text value to encodet
     * @return encoded MD5 string
     */
    public static String toMD5Hash(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Null plaintext parameter");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(plaintext.getBytes("UTF-8"));

            byte[] binaryData = md.digest();

            char[] buffer = new char[32];

            for (int i = 0; i < 16; i++) {
                int low = (int) (binaryData[i] & 0x0f);
                int high = (int) ((binaryData[i] & 0xf0) >> 4);
                buffer[i * 2] = HEXADECIMAL[high];
                buffer[i * 2 + 1] = HEXADECIMAL[low];
            }

            return new String(buffer);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    // -------------------------------------------------------- Private Methods

    private static Set getObjectPropertyNames(Object object) {
        HashSet hashSet = new HashSet();

        Method[] methods = object.getClass().getMethods();

        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();

            if (methodName.startsWith("get") && methodName.length() > 3) {
                String propertyName =
                    "" + Character.toLowerCase(methodName.charAt(3))
                    + methodName.substring(4);
                hashSet.add(propertyName);
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                String propertyName =
                    "" + Character.toLowerCase(methodName.charAt(2))
                    + methodName.substring(3);
                hashSet.add(propertyName);
            }
            if (methodName.startsWith("set") && methodName.length() > 3) {
                String propertyName =
                    "" + Character.toLowerCase(methodName.charAt(3))
                    + methodName.substring(4);
                hashSet.add(propertyName);
            }
        }

        return hashSet;
    }

    private static boolean hasMatchingProperty(Field field, Set properties) {
        String fieldName = field.getName();
        if (fieldName.indexOf(".") != -1) {
            fieldName = fieldName.substring(0, fieldName.indexOf("."));
        }
        return properties.contains(fieldName);
    }

    private static void ensureObjectPathNotNull(Object object, String path) {

        final int index = path.indexOf('.');

        if (index == -1) {
            return;
        }

        try {
            String value = path.substring(0, index);
            String getterName = toGetterName(value);
            String isGetterName = toIsGetterName(value);

            Method foundMethod = null;
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();
                if (name.equals(getterName)) {
                    foundMethod = methods[i];
                    break;

                } else if (name.equals(isGetterName)) {
                    foundMethod = methods[i];
                    break;
                }
            }

            if (foundMethod == null) {
                String msg =
                    "Getter method not found for path value : " + value;
                throw new RuntimeException(msg);
            }

            Object result = foundMethod.invoke(object, null);

            if (result == null) {
                result = foundMethod.getReturnType().newInstance();

                String setterName = toSetterName(value);
                Class[] classArgs = { foundMethod.getReturnType() };

                Method setterMethod =
                    object.getClass().getMethod(setterName, classArgs);

                Object[] objectArgs = { result };

                setterMethod.invoke(object, objectArgs);
            }

            String remainingPath = path.substring(index + 1);

            ensureObjectPathNotNull(result, remainingPath);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void log(String msg, boolean debug) {
        if (debug) {
            System.out.println("[Click] [debug] " + msg);
        } else {
            ClickLogger.getInstance().debug(msg);
        }
    }

}
