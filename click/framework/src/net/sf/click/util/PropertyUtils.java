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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * TODO: doco
 *
 * @author Malcolm Edgar
 */
public class PropertyUtils {

    /** Provides a synchronized cache of OGNL get expressions. */
    private static final Map OGNL_GET_EXPRESSION_CACHE = Collections.synchronizedMap(new HashMap());

    /** Provides a synchronized cache of OGNL set expressions. */
    private static final Map OGNL_SET_EXPRESSION_CACHE = Collections.synchronizedMap(new HashMap());

    // -------------------------------------------------------- Public Methods

    /**
     * TODO: doco
     */
    public static Object getValueOgnl(Object source, String name, Map ognlContext)
        throws OgnlException {

        CacheKey cacheKey = new CacheKey(source, name);

        Object expression = OGNL_GET_EXPRESSION_CACHE.get(cacheKey);
        if (expression == null) {
            expression = Ognl.parseExpression(name);
            OGNL_GET_EXPRESSION_CACHE.put(cacheKey, expression);
        }

        return Ognl.getValue(expression, ognlContext, source);
    }

    /**
     * TODO: doco
     */
    public static void setValueOgnl(Object target, String name, Object value, Map ognlContext)
        throws OgnlException {

        CacheKey cacheKey = new CacheKey(target, name);

        Object expression = OGNL_SET_EXPRESSION_CACHE.get(cacheKey);
        if (expression == null) {
            expression = Ognl.parseExpression(name);
            OGNL_SET_EXPRESSION_CACHE.put(cacheKey, expression);
        }

        Ognl.setValue(expression,
                      ognlContext,
                      target,
                      value);
    }

    /**
     * Return the property value for the given object and property name.
     *
     * @param source the source object
     * @param name the name of the property
     * @param cache the reflection method and OGNL cache
     * @return the property value fo the given source object and property name
     */
    public static Object getValue(Object source, String name, Map cache) {
        String basePart = name;
        String remainingPart = null;

        int baseIndex = name.indexOf(".");
        if (baseIndex != -1) {
            basePart = name.substring(0, baseIndex);
            remainingPart = name.substring(baseIndex + 1);
        }

        Object value = getObjectPropertyValue(source, basePart, cache);

        if (remainingPart == null || value == null) {
            return value;

        } else {
            return getValue(value, remainingPart, cache);
        }
    }

    // -------------------------------------------------------- Private Methods

    private static Object getObjectPropertyValue(Object source, String name, Map cache) {
        PropertyUtils.CacheKey methodNameKey = new PropertyUtils.CacheKey(source, name);

        Method method = null;
        try {
            method = (Method) cache.get(methodNameKey);

            if (method == null) {

                method = source.getClass().getMethod(ClickUtils.toGetterName(name), null);
                cache.put(methodNameKey, method);
            }

            return method.invoke(source, null);

        } catch (NoSuchMethodException nsme) {
            try {
                method = source.getClass().getMethod(ClickUtils.toIsGetterName(name), null);
                cache.put(methodNameKey, method);

                return method.invoke(source, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------- Inner Classes

    private static class CacheKey {

        private final Class sourceClass;
        private final String property;

        public CacheKey(Object source, String property) {
            if (source == null) {
                throw new IllegalArgumentException("Null source parameter");
            }
            if (property == null) {
                throw new IllegalArgumentException("Null property parameter");
            }
            this.sourceClass = source.getClass();
            this.property = property;
        }

        /**
         * @see Object#equals(Object)
         */
        public final boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            CacheKey that = (CacheKey) o;

            if (!sourceClass.equals(that.sourceClass)) {
                return false;
            }

            if (!property.equals(that.property)) {
                return false;
            }

            return true;
        }

        /**
         * @see Object#hashCode()
         */
        public final int hashCode() {
            return sourceClass.hashCode() * 31 + property.hashCode();
        }
    }

}
