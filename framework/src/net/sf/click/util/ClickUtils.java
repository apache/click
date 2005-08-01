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
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

/**
 * Provides miscellaneous String and Stream utility methods.
 *
 * @author Malcolm Edgar
 * @version $Id$
 */
public class ClickUtils {

    /** The number of template lines to display in error report. */
    protected static final int NUMBER_TEMPLATE_LINES = 8;

    // --------------------------------------------------------- Public Methods

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

        } catch (EncoderException ee) {
            String message = "error occured Base64 encoding: " + object;
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

        } catch (DecoderException de) {
            String message = "error occured Base64 decoding: " + string;
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
     * Return the getter method name for the given property name.
     *
     * @param property the property name
     * @return the setter method name for the given property name.
     */
    public static String toGetterName(String property) {
        StringBuffer buffer = new StringBuffer(property.length() + 3);

        buffer.append("get");
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

}
