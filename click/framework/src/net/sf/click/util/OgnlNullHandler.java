/*
 * Copyright 2006 Malcolm A. Edgar
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

import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.Map;

import ognl.NullHandler;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

/**
 * Provides a OGNL NullHandler to support null property path
 * evaluation. This class is used by the {@see net.sf.click.control.Table}
 * control to support rendering table data outer joins where property paths may
 * be null.
 * <p/>
 * If a null property path value is determined, then the OgnlRuntime will
 * invoke the <tt>nullPropertyValue()</tt> method which will attempt to
 * create a new instance of the property value using the property classes
 * no-args constructor.
 * <p/>
 * The Table control registers a <tt>OgnlNullHandler</tt> object with the
 * <tt>OgnlRuntime</tt> when the Table rowList is set.
 *
 * @see net.sf.click.control.Column
 * @see net.sf.click.control.Table
 *
 * @author Malcolm Edgar
 */
public class OgnlNullHandler implements NullHandler {

    /**
     * This method returns null.
     *
     * @see NullHandler#nullMethodResult(Map, Object, String, Object[])
     *
     * @param context the OGNL context map
     * @param target the target object
     * @param methodName the name of the target method
     * @param args the method arguments array
     * @return the method result object
     */
    public Object nullMethodResult(Map context, Object target,
            String methodName, Object[] args) {

        return null;
    }

    /**
     * Return a new property instance for the given target object and property
     * name.
     * <p/>
     * If a null property path value is determined, then the OgnlRuntime will
     * invoke the <tt>nullPropertyValue()</tt> method which will attempt to
     * create a new instance of the property value using the property classes
     * no-args constructor.
     *
     * @see NullHandler#nullPropertyValue(Map, Object, Object)
     *
     * @param context the OGNL context map
     * @param target the target object
     * @param property the name of the property
     * @return a new property instance
     * @throws RuntimeException if a new property instance could not be created
     */
    public Object nullPropertyValue(Map context, Object target,
            Object property) {

        try {
            Method method = OgnlRuntime.getGetMethod((OgnlContext) context,
                    target.getClass(),
                    property.toString());

            return method.getReturnType().newInstance();

        } catch (OgnlException oe) {
            throw new RuntimeException(oe);

        } catch (IntrospectionException ie) {
            throw new RuntimeException(ie);

        } catch (InstantiationException ise) {
            throw new RuntimeException(ise);

        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }
    }

}
